package algorithm;

import contactJudgment.Judge;
import contactJudgment.Uncertainty;
import datagenerate.DataGen;
import datagenerate.PreCompute;
import datagenerate.TrajectoryGen;
import iDModel.GenTopology;
import indoor_entitity.IndoorSpace;
import indoor_entitity.Partition;
import indoor_entitity.Trajectory;
import indoor_entitity.TrajectoryPoint;
import utilities.Constant;
import utilities.DataGenConstant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Algo_ICQ_pre {
    public static HashMap<Integer, HashMap<Integer, Double>> pro_map = new HashMap<Integer, HashMap<Integer, Double>>();
    public static HashMap<Integer, ArrayList<Integer>> con_timeList = new HashMap<Integer, ArrayList<Integer>>();
    public static HashMap<Integer, Integer> lastUnConTime = new HashMap<Integer, Integer>();

    public static HashMap<Integer, Double> icq_pre(int trajectoryId, double probability, int sTime, int eTime) {
//        con_timeList = new HashMap<>();
//        pro_map = new HashMap<>();
//        lastUnConTime = new HashMap<>();
//        for (Trajectory tra: IndoorSpace.iTrajectory) {
//            lastUnConTime.put(tra.getTrajectoryId(), -1000);
//        }
        HashMap<Integer, Double> results = new HashMap<Integer, Double>();
        HashMap<Integer, ArrayList<Integer>> traMap = new HashMap<Integer, ArrayList<Integer>>(); // key: time; value: candidate trajectory ID
        HashMap<Integer, Integer> traDurationMap = new HashMap<Integer, Integer>(); // key: trajectory ID; value: duration
        HashMap<Integer, HashMap<Integer, ArrayList<Double>>> canTraProMap = new HashMap<Integer, HashMap<Integer, ArrayList<Double>>>(); // key: time; value: (key: candidate trajectory ID; value: probabilitys in different partitions)

        // get the query time interval
        Trajectory trajectory = IndoorSpace.iTrajectory.get(trajectoryId);
        int ts = Math.max(trajectory.getMinTime(), sTime);
        int te = Math.min(trajectory.getMaxTime(), eTime);

//        System.out.println("ts: " + ts + ", te: " + te);
//        int tempTime = ts;
//        while (tempTime < te) {
//            tempTime += DataGenConstant.sampleInterval;
//            if (!trajectory.getTime().contains(tempTime)) {
//                Uncertainty.findUncertainTra(trajectory, tempTime);
//            }
//            System.out.println(trajectory.getTrajectory(tempTime));
//
//        }
        System.out.println("start query...");
        int t = ts;
        while (t <= te) {
//            System.out.println("t: " + t);
            ArrayList<Integer> canPars = trajectory.getParList(t);
            ArrayList<Integer> visitedCanPars = new ArrayList<Integer>();
            ArrayList<Integer> traPointIds = trajectory.getTrajectory(t);
            ArrayList<Integer> trajectorys = new ArrayList<Integer>();
            HashMap<Integer, Double> traPro = new HashMap<Integer, Double>();
//            ArrayList<Integer> pars = trajectory.getParList(t);
            for (int canParId: canPars) {
                if (visitedCanPars.contains(canParId)) continue;
                visitedCanPars.add(canParId);
//                System.out.println("canParId: " + canParId);
                Partition par = IndoorSpace.iPartitions.get(canParId);

//                HashMap<Integer, ArrayList<Integer>> canTrasMap = par.getTrajectorys(t);
                ArrayList<Integer> canTrajectorys = par.getTraKeys(t);
//                System.out.println("canTrajectorys: " + canTrajectorys);
                for (int canTrajectoryId: canTrajectorys) {
                    if (results.keySet().contains(canTrajectoryId)) continue;
                    if (canTrajectoryId == trajectoryId) continue;
//                    if (visitedCanTra.contains(canTrajectoryId)) continue;
                    if (par.getTraDistance(t, canTrajectoryId + "-" + trajectoryId) == null && par.getTraDistance(t, trajectoryId + "-" + canTrajectoryId) == null) {
                        continue;
                    }

//                    visitedCanTra.add(canTrajectoryId);
                    Trajectory canTrajectory = IndoorSpace.iTrajectory.get(canTrajectoryId);
                    if (canTrajectory.getMaxTime() - canTrajectory.getMinTime() < DataGenConstant.duration) continue;
                    double pro = 0;
                    ArrayList<ArrayList<Double>> dist_pros = new ArrayList<ArrayList<Double>>();
                    if (par.getTraDistance(t, canTrajectoryId + "-" + trajectoryId) == null) {
                        dist_pros = par.getTraDistance(t, trajectoryId + "-" + canTrajectoryId);
                    }
                    else {
                        dist_pros = par.getTraDistance(t, canTrajectoryId + "-" + trajectoryId);
                    }

//                    System.out.println("dist_pros: " + dist_pros);
                    for (int i = 0; i < dist_pros.size(); i++) {
//                        System.out.println("dist: " + dist_pros.get(i).get(0));
//                        System.out.println("pro: " + dist_pros.get(i).get(1));
                        if (dist_pros.get(i).get(0) <= DataGenConstant.distanceThreshold) {
                            pro += dist_pros.get(i).get(1);
                        }
                        if (dist_pros.get(i).get(0) > DataGenConstant.distanceThreshold) break;
                    }

//                    ArrayList<Integer> temp_canPars = canTrajectory.getParList(t);
//                    for (int temp_canParId: temp_canPars) {
//                        Partition temp_canPar = IndoorSpace.iPartitions.get(temp_canParId);
//                        if (temp_canParId == canParId) continue;
//                        if (!temp_canPar.getTrajectorys(t).keySet().contains(trajectoryId)) continue;
//                        if (temp_canPar.getTraDistance(t, canTrajectoryId + "-" + trajectoryId) == null && temp_canPar.getTraDistance(t, trajectoryId + "-" + canTrajectoryId) == null) {
//                            continue;
//                        }
//                        ArrayList<ArrayList<Double>> temp_dist_pros = new ArrayList<>();
//                        if (temp_canPar.getTraDistance(t, canTrajectoryId + "-" + trajectoryId) == null) {
//                            temp_dist_pros = temp_canPar.getTraDistance(t, trajectoryId + "-" + canTrajectoryId);
//                        }
//                        else {
//                            temp_dist_pros = temp_canPar.getTraDistance(t, canTrajectoryId + "-" + trajectoryId);
//                        }
//
//                        for (int i = 0; i < temp_dist_pros.size(); i++) {
//                            if (temp_dist_pros.get(i).get(0) <= DataGenConstant.distanceThreshold) {
//                                pro += temp_dist_pros.get(i).get(1);
//                            }
//                            if (dist_pros.get(i).get(0) > DataGenConstant.distanceThreshold) break;
//                        }
//                    }
//                    System.out.println("pro: " + pro);

                    if (traPro.get(canTrajectoryId) != null) {
                        double temp_pro = traPro.get(canTrajectoryId);
                        traPro.put(canTrajectoryId, temp_pro + pro);
                    }
                    else {
                        traPro.put(canTrajectoryId, pro);
                    }



//                    if (!trajectorys.contains(canTrajectoryId)) {
//                        trajectorys.add(canTrajectoryId);
//                    }
//                    if (traPro.get(canTrajectoryId) != null) {
//                        ArrayList<Double> temp_list = traPro.get(canTrajectoryId);
//                        temp_list.add(pro);
//                        traPro.replace(canTrajectoryId, temp_list);
//                    }
//                    else {
//                        ArrayList<Double> temp_list = new ArrayList<>();
//                        temp_list.add(pro);
//                        traPro.put(canTrajectoryId, temp_list);
//                    }


                }
            }
            Set<Integer> canTrajectoryIds = traPro.keySet();
            for (int canTrajectoryId: canTrajectoryIds) {
                double pro = traPro.get(canTrajectoryId);

//                System.out.println("canTraId: " + canTrajectoryId + ".......");
//                System.out.println("pro: " + pro + ".......");

                if (pro == 0) {
                    lastUnConTime.put(canTrajectoryId, t);
                    con_timeList.remove(canTrajectoryId);
                }
                else {


                    ArrayList<Integer> temp_list = new ArrayList<Integer>();
                    if (con_timeList.get(canTrajectoryId) != null) {
                        temp_list = con_timeList.get(canTrajectoryId);
                    }
                    temp_list = addContactTime(temp_list, t);
                    con_timeList.put(canTrajectoryId, temp_list);


                    HashMap<Integer, Double> temp_pro = new HashMap<Integer, Double>();
                    if (pro_map.get(canTrajectoryId) != null) {
                        temp_pro = pro_map.get(canTrajectoryId);
                    }
                    temp_pro.put(t, pro);
                    pro_map.put(canTrajectoryId, temp_pro);

                }

                if (con_timeList.get(canTrajectoryId) != null && con_timeList.get(canTrajectoryId).size() >= DataGenConstant.duration / DataGenConstant.sampleInterval) {

                    while (con_timeList.get(canTrajectoryId).size() > DataGenConstant.duration / DataGenConstant.sampleInterval) {
                        con_timeList.get(canTrajectoryId).remove(0);
                    }

                    double temp = 1;
                    ArrayList<Integer> temp_list = con_timeList.get(canTrajectoryId);
                    for (int temp_time: temp_list) {
                        double temp_pro = pro_map.get(canTrajectoryId).get(temp_time);

                        temp *= temp_pro;
                    }

    //                        System.out.println("ave pro: " + temp);
                    if (temp >= probability) {
                        results.put(canTrajectoryId, temp);
                    }
                }
            }
//            traMap.put(t, trajectorys);
//            canTraProMap.put(t, traPro);
//
//            if (traMap.get(t - DataGenConstant.sampleInterval) == null) {
//                for (int traId: trajectorys) {
//                    traDurationMap.put(traId, 1);
//                }
//
//                t += DataGenConstant.sampleInterval;
//                continue;
//            }
//            ArrayList<Integer> trajectorys_prev = traMap.get(t - DataGenConstant.sampleInterval);
//            for (int traId_prev: trajectorys_prev) {
//                if (trajectorys.contains(traId_prev)) {
//                    int duration = traDurationMap.get(traId_prev);
//                    System.out.println("duration: " + duration);
//                    traDurationMap.replace(traId_prev, duration + 1);
//
//                    if ((duration + 1) * DataGenConstant.sampleInterval >= DataGenConstant.duration){
//                        System.out.println("calculate probability...");
//                        // calculate the probability
//                        double ave = 0;
//                        double sum = 0;
//                        for (int i = 0; i < DataGenConstant.duration / DataGenConstant.sampleInterval; i++) {
//                            int temp_time = t - i * DataGenConstant.sampleInterval;
//                            ArrayList<Double> pros = canTraProMap.get(temp_time).get(traId_prev);
//                            double ave1 = 0;
//                            double sum1 = 0;
//                            for (double pro: pros) {
//                                sum1 += pro;
//                            }
//                            ave1 = sum1 / pros.size();
//                            System.out.println("ave1 pro: " + ave1);
//                            sum += ave1;
//                        }
//                        ave = sum / ((double)DataGenConstant.duration / (double)DataGenConstant.sampleInterval);
//                        System.out.println("ave pro: " + ave);
//                        if (ave >= probability) {
//                            results.add(traId_prev);
//                        }
//
//                    }
//                }
//                else {
//                    traDurationMap.remove(traId_prev);
//                }
//            }
//            for (int traId: trajectorys) {
//                if (!trajectorys_prev.contains(traId)) {
//                    traDurationMap.put(traId, 1);
//                }
//            }

            t += DataGenConstant.sampleInterval;
        }
        return results;
    }

    public static void icq_pre_clear() {
        con_timeList = new HashMap<Integer, ArrayList<Integer>>();
        pro_map = new HashMap<Integer, HashMap<Integer, Double>>();
        lastUnConTime = new HashMap<Integer, Integer>();
        for (Trajectory tra: IndoorSpace.iTrajectory) {
            lastUnConTime.put(tra.getTrajectoryId(), -1000);
        }
    }

    public static ArrayList<Integer> addContactTime(ArrayList<Integer> list, int t) {
        if (list.size() == 0) {
            list.add(t);
        }
        else if (t >= list.get(list.size() - 1)) {
            list.add(t);
        }
        else {
            ArrayList<Integer> temp = new ArrayList<Integer>();
            Boolean isAdded = false;
            for (int i = 0; i < list.size(); i++) {
                int time = list.get(i);
                if (time < t) {
                    temp.add(time);
                }
                else if (!isAdded){
                    temp.add(t);
                    temp.add(time);
                    isAdded = true;
                }
                else {
                    temp.add(time);
                }
            }
            list = temp;
        }
        return list;
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
        PreCompute.preInfo_read("syn", 4000, DataGenConstant.sample_dist);



        System.out.println(IndoorSpace.iTrajectory.size());

        Algo_ICQ_pre.icq_pre_clear();
        HashMap<Integer, Double> result = icq_pre(241, 0.5, DataGenConstant.startTime, DataGenConstant.endTime);
        System.out.println("result: " + result);
    }
}
