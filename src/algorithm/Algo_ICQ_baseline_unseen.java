package algorithm;

import contactJudgment.Judge_baseline_new;
import contactJudgment.Uncertainty;
import contactJudgment.Uncertainty_baseline_new;
import datagenerate.DataGen;
import datagenerate.TrajectoryGen;
import iDModel.GenTopology;
import indoor_entitity.*;
import utilities.DataGenConstant;
import utilities.RoomType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Algo_ICQ_baseline_unseen {
    public static HashMap<Integer, Double> icq_baseline_unseen(int trajectoryId, double probability, int sTime, int eTime) {
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
//        int tempTime = ts;
//        while (tempTime < te) {
//            tempTime += DataGenConstant.sampleInterval;
//            if (!trajectory.getTime().contains(tempTime)) {
//                Uncertainty_baseline_new.findUncertainTra_forBaselineNew(trajectory, tempTime);
//            }
////            System.out.println(trajectory.getTrajectory(tempTime));
//
//        }
        System.out.println("start query...");
        ArrayList<Integer> canTraList = new ArrayList<Integer>();
        HashMap<Integer, ArrayList<Integer>> canTraMap = new HashMap<Integer, ArrayList<Integer>>();
        int t = ts;
        while (t <= te) {

//            System.out.println("query t: " + t + "------------------");

            if (!trajectory.getTime().contains(t)) {
                t += DataGenConstant.sampleInterval;
                continue;
            }
            int traPointId = trajectory.getTrajectory(t).get(0);
            TrajectoryPoint traPoint = IndoorSpace.iTraPoint.get(traPointId);
            int parId = getHostPartition(traPoint);
            ArrayList<Integer> canTraList_t = new ArrayList<Integer>();

            for (Trajectory temp_tra : IndoorSpace.iTrajectory) {
                if (temp_tra.getTrajectoryId() == trajectoryId) continue;
                if (temp_tra.getTime().contains(t)) {
                    int temp_traPointId = temp_tra.getTrajectory(t).get(0);
                    TrajectoryPoint temp_traPoint = IndoorSpace.iTraPoint.get(temp_traPointId);
                    int temp_parId = getHostPartition(temp_traPoint);
                    if (temp_parId != parId) continue;
                    double distance = traPoint.eDist(temp_traPoint);
                    if (distance <= DataGenConstant.distanceThreshold) {
                        canTraList_t.add(temp_tra.getTrajectoryId());
                        if (!canTraList.contains(temp_tra.getTrajectoryId())) {
                            canTraList.add(temp_tra.getTrajectoryId());
                        }
                    }

                }
            }
//            ArrayList<Integer> canTraList_temp = new ArrayList<Integer>();
//            canTraList_temp.addAll(IndoorSpace.iPartitions.get(parId).getTrajectorys(t).keySet());
////            System.out.println("list size: " + canTraList_temp.size());
//            for (int temp_traId: canTraList_temp) {
////                System.out.println("temp_traId: " + temp_traId);
//                if (temp_traId == trajectoryId) continue;
////                System.out.println("temp_traId_1: " + temp_traId);
//                Trajectory temp_tra = IndoorSpace.iTrajectory.get(temp_traId);
//                int temp_traPointId = temp_tra.getTrajectory(t).get(0);
////                System.out.println("temp_traPointId: " + temp_traPointId);
//                TrajectoryPoint temp_traPoint = IndoorSpace.iTraPoint.get(temp_traPointId);
//                double distance = traPoint.eDist(temp_traPoint);
//                    if (distance <= DataGenConstant.distanceThreshold) {
//                        canTraList_t.add(temp_tra.getTrajectoryId());
//                        if (!canTraList.contains(temp_tra.getTrajectoryId())) {
//                            canTraList.add(temp_tra.getTrajectoryId());
//                        }
//                    }
//            }
            canTraMap.put(t, canTraList_t);
            t += DataGenConstant.sampleInterval;
        }

        System.out.println("querying the final results ...");
        for (int temp_traId: canTraList) {
            double count = 0;
            if (results_pro.keySet().contains(temp_traId)) continue;
            t = ts;
            while (t <= te) {
                if (!canTraMap.keySet().contains(t)) {
                    t += DataGenConstant.sampleInterval;
                    count = 0;
                    continue;
                }
                ArrayList<Integer> tempList = canTraMap.get(t);
                if (tempList.contains(temp_traId)) {
                    count++;
                    if (count >= DataGenConstant.duration / DataGenConstant.sampleInterval + 1) {
                        results_pro.put(temp_traId, count);
                        break;
                    }
                }
                else {
                    count = 0;
                }
                t += DataGenConstant.sampleInterval;
            }

        }


//            ArrayList<Integer> visitedPars = new ArrayList<>();
//            ArrayList<Integer> visitedTrajectorys = new ArrayList<>();
//
//            ArrayList<TrajectoryPoint> traPointList = new ArrayList<>();
//            if (trajectory.getTrajectory(t) != null) {
//                for (int traPointId: trajectory.getTrajectory(t)) {
//                    TrajectoryPoint traPoint1 = IndoorSpace.iTraPoint.get(traPointId);
//                    traPointList.add(traPoint1);
//                }
//            }
//            else {
//                traPointList = Uncertainty.preTrajectory.get(trajectory.getTrajectoryId()).get(t);
//            }
//
//            for (TrajectoryPoint traPoint: traPointList) {
//
//                int parId = traPoint.getParID();
//                if (visitedPars.contains(parId)) continue;
////                System.out.println("parId: " + parId);
//                Partition par = IndoorSpace.iPartitions.get(parId);
//                visitedPars.add(parId);
//                HashMap<Integer, ArrayList<Integer>> canObjectsMap = par.getTrajectorys(t);
//                Set<Integer> canTrajectorys = new HashSet<>();
//                if (canObjectsMap != null) {
//                    canTrajectorys.addAll(canObjectsMap.keySet());
//                }
//                if (Uncertainty.par_tra.get(parId) != null && Uncertainty.par_tra.get(parId).get(t) != null) {
//                    ArrayList<Integer> temp_tra_list = Uncertainty.par_tra.get(parId).get(t);
////                    System.out.println("temp_tra_list: " + temp_tra_list);
////                    for (int temp_tra: temp_tra_list) {
////                        canTrajectorys.add(temp_tra);
////                    }
//                    canTrajectorys.addAll(temp_tra_list);
//
//                }
//                if (canTrajectorys.isEmpty()) {
//                    System.out.println("something wrong with conTrajectorys: Algo_ICQ");
//                }
////                System.out.println("canTrajectorys: " + canTrajectorys);
//                for (int canTrajectoryId: canTrajectorys) {
//                    if (results_pro.keySet().contains(canTrajectoryId)) continue;
//                    if (visitedTrajectorys.contains(canTrajectoryId)) continue;
//                    if (canTrajectoryId == trajectoryId) continue;
//                    visitedTrajectorys.add(canTrajectoryId);
//                    Trajectory canTrajectory = IndoorSpace.iTrajectory.get(canTrajectoryId);
//                    double count = 0;
//                    if (t - ts < DataGenConstant.duration) {
//                        count = Judge_baseline_new.contactJudge(trajectory, canTrajectory, ts, ts + DataGenConstant.duration, probability);
//                        if (count >= DataGenConstant.duration / DataGenConstant.sampleInterval + 1) {
//                            results_pro.put(canTrajectoryId, count);
//                        }
//                    }
//                    count = Judge_baseline_new.contactJudge(trajectory, canTrajectory, t, Math.min(te, t + DataGenConstant.duration), probability);
//                    if (count >= DataGenConstant.duration / DataGenConstant.sampleInterval + 1) {
//                        results_pro.put(canTrajectoryId, count);
//                    }
//
//                }
//            }
//            t += DataGenConstant.sampleInterval;
//        }
        return results_pro;
    }


    /**
     * get host partition of a point
     */
    public static int getHostPartition(Point point) {
        int partitionId = -1;
        int floor = point.getmFloor();
        ArrayList<Integer> pars = IndoorSpace.iFloors.get(floor).getmPartitions();
        for (int i = 0; i < pars.size(); i++) {
            Partition par = IndoorSpace.iPartitions.get(pars.get(i));
            if (point.getX() >= par.getX1() && point.getX() <= par.getX2() && point.getY() >= par.getY1() && point.getY() <= par.getY2()) {
                partitionId = par.getmID();
                if (DataGenConstant.dataset.equals("MZB") && IndoorSpace.iPartitions.get(partitionId).getmType() == RoomType.HALLWAY) continue;
                return partitionId;
            }
        }
        return partitionId;
    }

    public static void main(String[] arg) throws IOException {
        PrintOut printOut = new PrintOut();
        DataGen dataGen = new DataGen();
        dataGen.genAllData(DataGenConstant.dataType, DataGenConstant.divisionType);

        GenTopology genTopology = new GenTopology();
        genTopology.genTopology();
        TrajectoryGen.trajectoryGen_read(TrajectoryGen.trajectoryPoints, TrajectoryGen.trajectorys,"syn", 4000, false);

//

        System.out.println("start querying 1...");
        HashMap<Integer, Double> result = icq_baseline_unseen(7, 0.5, DataGenConstant.startTime, DataGenConstant.endTime);
        System.out.println("result: " + result);
    }

}
