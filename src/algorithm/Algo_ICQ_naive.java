package algorithm;

import contactJudgment.Judge_naive;
import contactJudgment.Uncertainty;
import datagenerate.DataGen;
import datagenerate.TrajectoryGen;
import iDModel.GenTopology;
import indoor_entitity.Trajectory;
import indoor_entitity.IndoorSpace;
import indoor_entitity.Partition;
import indoor_entitity.TrajectoryPoint;
import utilities.DataGenConstant;

import java.io.IOException;
import java.util.*;

public class Algo_ICQ_naive {

    public static HashMap<Integer, Double> icq_naive(int trajectoryId, double probability, int sTime, int eTime) {
//        Uncertainty.par_tra = new HashMap<>();
//        Uncertainty.preTrajectory = new HashMap<>();
//
//        Judge_naive.pro_map = new HashMap<>();

        HashMap<Integer, Double> results_pro = new HashMap<Integer, Double>(); // probability

        // get the query time interval
        Trajectory trajectory = IndoorSpace.iTrajectory.get(trajectoryId);
        int ts = Math.max(trajectory.getMinTime(), sTime);
        int te = Math.min(trajectory.getMaxTime(), eTime);

//        System.out.println("ts: " + ts + ", te: " + te);
        int tempTime = ts;
        while (tempTime < te) {
            tempTime += DataGenConstant.sampleInterval;
            if (!trajectory.getTime().contains(tempTime)) {
                Uncertainty.findUncertainTra_forQuery(trajectory, tempTime);
            }
//            System.out.println(trajectory.getTrajectory(tempTime));

        }
//        System.out.println("start query...");
        int t = ts;
        while (t <= te) {
//            System.out.println("query t: " + t + "------------------");
            ArrayList<Integer> visitedPars = new ArrayList<Integer>();
            ArrayList<Integer> visitedTrajectorys = new ArrayList<Integer>();

            ArrayList<TrajectoryPoint> traPointList = new ArrayList<TrajectoryPoint>();
            if (trajectory.getTrajectory(t) != null) {
                for (int traPointId: trajectory.getTrajectory(t)) {
                    TrajectoryPoint traPoint1 = IndoorSpace.iTraPoint.get(traPointId);
                    traPointList.add(traPoint1);
                }
            }
            else {
                traPointList = Uncertainty.preTrajectory.get(trajectory.getTrajectoryId()).get(t);
            }

            for (TrajectoryPoint traPoint: traPointList) {

                int parId = traPoint.getParID();
                if (visitedPars.contains(parId)) continue;
//                System.out.println("parId: " + parId);
                Partition par = IndoorSpace.iPartitions.get(parId);
                visitedPars.add(parId);
                HashMap<Integer, ArrayList<Integer>> canObjectsMap = par.getTrajectorys(t);
                Set<Integer> canTrajectorys = new HashSet<Integer>();
                if (canObjectsMap != null) {
                    canTrajectorys.addAll(canObjectsMap.keySet());
                }
                if (Uncertainty.par_tra.get(parId) != null && Uncertainty.par_tra.get(parId).get(t) != null) {
                    ArrayList<Integer> temp_tra_list = Uncertainty.par_tra.get(parId).get(t);
//                    System.out.println("temp_tra_list: " + temp_tra_list);
//                    for (int temp_tra: temp_tra_list) {
//                        canTrajectorys.add(temp_tra);
//                    }
                    canTrajectorys.addAll(temp_tra_list);

                }
                if (canTrajectorys.isEmpty()) {
                    System.out.println("something wrong with conTrajectorys: Algo_ICQ");
                }
//                System.out.println("canTrajectorys: " + canTrajectorys);
                for (int canTrajectoryId: canTrajectorys) {
                    if (results_pro.keySet().contains(canTrajectoryId)) continue;
                    if (visitedTrajectorys.contains(canTrajectoryId)) continue;
                    if (canTrajectoryId == trajectoryId) continue;
                    visitedTrajectorys.add(canTrajectoryId);
                    Trajectory canTrajectory = IndoorSpace.iTrajectory.get(canTrajectoryId);
                    double count = 0;
                    if (t - ts < DataGenConstant.duration) {
                        count = Judge_naive.contactJudge(trajectory, canTrajectory, ts, ts + DataGenConstant.duration, probability);
                        if (count >= DataGenConstant.duration / DataGenConstant.sampleInterval + 1) {
                            results_pro.put(canTrajectoryId, count);
                        }
                    }
                    count = Judge_naive.contactJudge(trajectory, canTrajectory, t, Math.min(te, t + DataGenConstant.duration), probability);
                    if (count >= DataGenConstant.duration / DataGenConstant.sampleInterval + 1) {
                        results_pro.put(canTrajectoryId, count);
                    }

                }
            }
            t += DataGenConstant.sampleInterval;
        }
        return results_pro;
    }

    public static void icq_naive_clear() {
        Uncertainty.par_tra = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
        Uncertainty.preTrajectory = new HashMap<Integer, HashMap<Integer, ArrayList<TrajectoryPoint>>>();

        Judge_naive.pro_map = new HashMap<Integer, HashMap<Integer, Double>>();
    }

    public static void main(String[] arg) throws IOException {
        PrintOut printOut = new PrintOut();
        DataGen dataGen = new DataGen();
        dataGen.genAllData(DataGenConstant.dataType, DataGenConstant.divisionType);

        GenTopology genTopology = new GenTopology();
        genTopology.genTopology();
        TrajectoryGen.trajectoryGen_read(TrajectoryGen.trajectoryPoints, TrajectoryGen.trajectorys,"syn", 4000, false);

//        Trajectory trajectory = IndoorSpace.iTrajectory.get(1);
//        Trajectory trajectory2 = new Trajectory();
//        IndoorSpace.iTrajectory.add(trajectory2);
//        for (int time: trajectory.getTime()) {
//            ArrayList<Integer> traPointIds = trajectory.getTrajectory(time);
//
//            for (int traPointId: traPointIds) {
//                TrajectoryPoint traPoint = IndoorSpace.iTraPoint.get(traPointId);
//                TrajectoryPoint traPoint2 = new TrajectoryPoint(traPoint.getX(), traPoint.getY(), traPoint.getmFloor(), time);
//                IndoorSpace.iTraPoint.add(traPoint2);
//                traPoint2.setTrajectoryID(trajectory2.getTrajectoryId());
//                traPoint2.setProbability(1);
//                traPoint2.setParID(traPoint.getParID());
//                Partition par = IndoorSpace.iPartitions.get(traPoint.getParID());
//                par.addTrajectory(time, trajectory2.getTrajectoryId(), traPoint2.getTraPointId());
//                trajectory2.addTime(time);
//                trajectory2.addTrajectory(time, traPoint2.getTraPointId());
//            }
//
//        }

//        System.out.println(IndoorSpace.iTrajectory.size());


        Algo_ICQ_naive.icq_naive_clear();
        HashMap<Integer, Double> result = icq_naive(1, 0.5, DataGenConstant.startTime, DataGenConstant.endTime);
        System.out.println("result: " + result);
    }
}



