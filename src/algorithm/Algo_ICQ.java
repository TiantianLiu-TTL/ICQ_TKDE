package algorithm;

import contactJudgment.Judge;
import contactJudgment.Uncertainty;
import datagenerate.DataGen;
import datagenerate.HSMDataGenRead;
import datagenerate.HSMTrajectoryGen;
import datagenerate.TrajectoryGen;
import iDModel.GenTopology;
import indoor_entitity.Trajectory;
import indoor_entitity.IndoorSpace;
import indoor_entitity.Partition;
import indoor_entitity.TrajectoryPoint;
import utilities.DataGenConstant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Algo_ICQ {

//    public static HashMap<Integer, HashMap<Integer, Double>> pro_map = new HashMap<>();
//    public static HashMap<Integer, ArrayList<Integer>> con_timeList = new HashMap<>();
//    public static HashMap<Integer, Integer> lastUnConTime = new HashMap<>();


    public static HashMap<Integer, Double> icq(int trajectoryId, double probability, int sTime, int eTime) {
//        Uncertainty.par_tra = new HashMap<>();
//        Uncertainty.preTrajectory = new HashMap<>();
//        Judge.con_timeList = new HashMap<>();
//        Judge.pro_map = new HashMap<>();
//        Judge.lastUnConTime = new HashMap<>();
//        for (Trajectory tra: IndoorSpace.iTrajectory) {
//            Judge.lastUnConTime.put(tra.getTrajectoryId(), -1000);
//        }
        HashMap<Integer, Double> results = new HashMap<Integer, Double>();

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
        System.out.println("start query...");
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
                    if (results.keySet().contains(canTrajectoryId)) continue;
                    if (visitedTrajectorys.contains(canTrajectoryId)) continue;
                    if (canTrajectoryId == trajectoryId) continue;
//                    System.out.println("canTrajectory: " + canTrajectoryId);
                    visitedTrajectorys.add(canTrajectoryId);
                    Trajectory canTrajectory = IndoorSpace.iTrajectory.get(canTrajectoryId);
                    if (canTrajectory.getMaxTime() - canTrajectory.getMinTime() < DataGenConstant.duration) continue;
                    int ts1, te1;
                    double count = 0;
                    if (t - ts < DataGenConstant.duration) {
                        count = Judge.contactJudge(trajectory, canTrajectory, ts, Math.min(te, Math.min(te, ts + DataGenConstant.duration)), probability);
                        if (count >= DataGenConstant.duration / DataGenConstant.sampleInterval + 1) {
                            results.put(canTrajectoryId, count);
                        }
                    }
//                    else {
//                        ts1 = t - DataGenConstant.duration;
//                    }
//
//                    if (t + DataGenConstant.duration > te) {
//                        te1 = te;
//                    }
//                    else {
//                        te1 = t + DataGenConstant.duration;
//                    }
                    count = Judge.contactJudge(trajectory, canTrajectory, t, Math.min(te, t + DataGenConstant.duration), probability);
                    if (count >= DataGenConstant.duration / DataGenConstant.sampleInterval + 1) {
                        results.put(canTrajectoryId, count);
                    }

                }
            }
            t += DataGenConstant.sampleInterval;
        }
        return results;
    }
    public static void icq_clear() {
        Uncertainty.par_tra = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
        Uncertainty.preTrajectory = new HashMap<Integer, HashMap<Integer, ArrayList<TrajectoryPoint>>>();
        Judge.con_timeList = new HashMap<Integer, ArrayList<Integer>>();
        Judge.pro_map = new HashMap<Integer, HashMap<Integer, Double>>();
        Judge.lastUnConTime = new HashMap<Integer, Integer>();
        for (Trajectory tra: IndoorSpace.iTrajectory) {
            Judge.lastUnConTime.put(tra.getTrajectoryId(), -1000);
        }
    }

    public static void main(String[] arg) throws IOException {
        PrintOut printOut = new PrintOut();
        DataGen dataGen = new DataGen();
        dataGen.genAllData(DataGenConstant.dataType, DataGenConstant.divisionType);
//        HSMDataGenRead hsmDataGenRead= new HSMDataGenRead();
//        hsmDataGenRead.dataGen("newHsm");

        GenTopology genTopology = new GenTopology();
        genTopology.genTopology();
//        TrajectoryGen.trajectoryGen_save(TrajectoryGen.trajectoryPoints, TrajectoryGen.trajectorys, "syn", 4000);
        TrajectoryGen.trajectoryGen_read(TrajectoryGen.trajectoryPoints, TrajectoryGen.trajectorys,"syn", 4000, false);
//        HSMTrajectoryGen.trajectoryGen_read(HSMTrajectoryGen.trajectoryPoints, HSMTrajectoryGen.trajectorys,"HSM");

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

        System.out.println(IndoorSpace.iTrajectory.size());
//        DataGenConstant.distanceThreshold = 1;
//        Trajectory trajectory = IndoorSpace.iTrajectory.get(5);
//        for (int t = trajectory.getMinTime(); t <= trajectory.getMaxTime(); t += 10) {
//            System.out.println("t: " + t + ", " + trajectory.getTrajectory(t));
//        }

        Algo_ICQ.icq_clear();
        HashMap<Integer, Double> result = icq(1, 0.5, DataGenConstant.startTime, DataGenConstant.endTime);
        System.out.println("result: " + result);
//        Algo_ICQ.icq_clear();
//        result = icq(6, 0.5, DataGenConstant.startTime, DataGenConstant.endTime);
//        System.out.println("result: " + result);
//        Algo_ICQ.icq_clear();
//        result = icq(7, 0.5, DataGenConstant.startTime, DataGenConstant.endTime);
//        System.out.println("result: " + result);
//        Algo_ICQ.icq_clear();
//        result = icq(8, 0.5, DataGenConstant.startTime, DataGenConstant.endTime);
//        System.out.println("result: " + result);
//        Algo_ICQ.icq_clear();
//        result = icq(9, 0.5, DataGenConstant.startTime, DataGenConstant.endTime);
//        System.out.println("result: " + result);
//        Algo_ICQ.icq_clear();
//        result = icq(10, 0.5, DataGenConstant.startTime, DataGenConstant.endTime);
//        System.out.println("result: " + result);
//        Algo_ICQ.icq_clear();
//        result = icq(11, 0.5, DataGenConstant.startTime, DataGenConstant.endTime);
//        System.out.println("result: " + result);
//        Algo_ICQ.icq_clear();
//        result = icq(12, 0.5, DataGenConstant.startTime, DataGenConstant.endTime);
//        System.out.println("result: " + result);
//        Algo_ICQ.icq_clear();
//        result = icq(13, 0.5, DataGenConstant.startTime, DataGenConstant.endTime);
//        System.out.println("result: " + result);
//        Algo_ICQ.icq_clear();
//        result = icq(14, 0.5, DataGenConstant.startTime, DataGenConstant.endTime);
//        System.out.println("result: " + result);

    }
}
