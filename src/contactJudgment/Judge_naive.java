package contactJudgment;

import indoor_entitity.Trajectory;
import indoor_entitity.IndoorSpace;
import indoor_entitity.TrajectoryPoint;
import utilities.DataGenConstant;

import java.util.ArrayList;
import java.util.HashMap;


public class Judge_naive {
    public static HashMap<Integer, HashMap<Integer, Double>> pro_map = new HashMap<Integer, HashMap<Integer, Double>>();
//    public static HashMap<Integer, ArrayList<Integer>> con_timeList = new HashMap<>();
//    public static HashMap<Integer, Integer> lastUnConTime = new HashMap<>();

    public static int contactJudge(Trajectory trajectory1, Trajectory trajectory2, int sTime, int eTime, double probabilityThreshold) {
        pro_map = new HashMap<Integer, HashMap<Integer, Double>>();
//        Uncertainty.preTrajectory.put(trajectory2.getTrajectoryId(), null);
        int result = 0;
        HashMap<Integer, ArrayList<Integer>> trajectoryPoints_tra1 = trajectory1.getTrajectory();
        HashMap<Integer, ArrayList<Integer>> trajectoryPoints_tra2 = trajectory2.getTrajectory();
        if (trajectory1.getMaxTime() < sTime || trajectory1.getMinTime() > eTime) {
            System.out.println("object1 did not appear in [sTime, eTime]");
            return 0;
        }
        if (trajectory2.getMaxTime() < sTime || trajectory2.getMinTime() > eTime) {
            System.out.println("object1 did not appear in [sTime, eTime]");
            return 0;
        }
        int t1 = Math.max(trajectory1.getMinTime(), trajectory2.getMinTime());
        int startTIme = Math.max(sTime, t1);
        int t = startTIme;
//        System.out.println("start time: " + t);


        while (t <= trajectory1.getMaxTime() && t <= trajectory2.getMaxTime() && t <= eTime) {

            ArrayList<TrajectoryPoint> traPointList1 = new ArrayList<TrajectoryPoint>();
            if (trajectoryPoints_tra1.get(t) != null) {
                for (int traPointId1: trajectoryPoints_tra1.get(t)) {
                    TrajectoryPoint traPoint1 = IndoorSpace.iTraPoint.get(traPointId1);
                    traPointList1.add(traPoint1);
                }
            }
            else {
                traPointList1 = Uncertainty.preTrajectory.get(trajectory1.getTrajectoryId()).get(t);
            }

            ArrayList<TrajectoryPoint> traPointList2 = new ArrayList<TrajectoryPoint>();
            if (trajectoryPoints_tra2.get(t) != null) {
                for (int traPointId2: trajectoryPoints_tra2.get(t)) {
                    TrajectoryPoint traPoint2 = IndoorSpace.iTraPoint.get(traPointId2);
                    traPointList2.add(traPoint2);
                }
            }
            else {
                Uncertainty.findUncertainTra_forQuery(trajectory2, t);
                traPointList2 = Uncertainty.preTrajectory.get(trajectory2.getTrajectoryId()).get(t);
            }


            double probability = 0;
            for ( TrajectoryPoint traPoint1: traPointList1) {

                for (TrajectoryPoint traPoint2: traPointList2) {
                    if (traPoint1.getParID() != traPoint2.getParID()) {
                        continue;
                    }
                    if (traPoint1.eDist(traPoint2) > DataGenConstant.distanceThreshold) {
                        continue;
                    }
                    probability += traPoint1.getProbability() * traPoint2.getProbability();
                }
            }
//            System.out.println("probability: " + probability);
            HashMap<Integer, Double> temp_pro = new HashMap<Integer, Double>();
            if (pro_map.get(trajectory2.getTrajectoryId()) != null) {
                temp_pro = pro_map.get(trajectory2.getTrajectoryId());
            }
            temp_pro.put(t, probability);
            pro_map.put(trajectory2.getTrajectoryId(), temp_pro);
//            trajectory2.setContactProbability(t, probability);
            if (t - startTIme >= DataGenConstant.duration) {
                int ts = t - DataGenConstant.duration;
                int count = 0;
                for (int t_temp = ts; t_temp <= t; t_temp += DataGenConstant.sampleInterval) {
                    if (pro_map.get(trajectory2.getTrajectoryId()).get(t_temp) >= probabilityThreshold) {
                        count++;
                    }
                    else break;
                }
                if (count >= DataGenConstant.duration / DataGenConstant.sampleInterval + 1) {
                    return  count;
                }
            }


            t += DataGenConstant.sampleInterval;
        }

        return result;
    }


}

