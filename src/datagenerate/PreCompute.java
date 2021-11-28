package datagenerate;

import algorithm.PrintOut;
import contactJudgment.Uncertainty;
import iDModel.GenTopology;
import indoor_entitity.IndoorSpace;
import indoor_entitity.Partition;
import indoor_entitity.Trajectory;
import indoor_entitity.TrajectoryPoint;
import utilities.DataGenConstant;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class PreCompute {
    private static String pre_info = System.getProperty("user.dir") + "/data_trajectory/pre_info_";
    private static String tra_partition = System.getProperty("user.dir") + "/data_trajectory/tra_partition_";
    private static String par_tra = System.getProperty("user.dir") + "/data_trajectory/par_tra_";
    public static String trajectoryPoints_uncertain = System.getProperty("user.dir") + "/data_trajectory/trajectoryPoint_uncertain_";
    public static String trajectorys_uncertain = System.getProperty("user.dir") + "/data_trajectory/trajectory_uncertain_";

    public static void preInfo_save(String dataset, int objectNum, double sampleDist) throws IOException{
        uncertainty(dataset, objectNum, sampleDist);
//        trajectoryGen_save(trajectoryPoints_uncertain, trajectorys_uncertain, dataset);
        preComputing(dataset, objectNum, sampleDist);


    }

    public static void preInfo_read(String dataset, int objectNum, double sampleDist) throws IOException {
        Path path = Paths.get(pre_info + dataset + "_objectNum_" + objectNum + "_sampleDist_" + sampleDist + ".txt");
        Scanner scanner = new Scanner(path);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String [] tempArr = line.split("\t");
            int parId = Integer.parseInt(tempArr[0]);
            Partition par = IndoorSpace.iPartitions.get(parId);
            for (int i = 1; i < tempArr.length; i++) {
                String[] tempArr1 = tempArr[i].split(";");
                int t = Integer.parseInt(tempArr1[0]);
                for (int j = 1; j < tempArr1.length; j++) {
                    String[] tempArr2 = tempArr1[j].split(",");
                    String s = tempArr2[0];
//                    int num = Integer.parseInt(tempArr2[1]);
//                    par.addTraRelationNum(t, s, num);
                    ArrayList<ArrayList<Double>> list = new ArrayList<>();
                    for (int m = 1; m < tempArr2.length; m++) {
                        String [] tempArr3 = tempArr2[m].split("&");
                        double dist = Double.parseDouble(tempArr3[0]);
                        double pro = Double.parseDouble(tempArr3[1]);
//                        if (dist > DataGenConstant.distanceThreshold) break;
                        list.add(new ArrayList<>(Arrays.asList(dist, pro)));
                    }
                    par.addTraDistance(t, s, list);
                }
            }
        }

        Path path1 = Paths.get(tra_partition + dataset + "_objectNum_" + objectNum + "_sampleDist_" + sampleDist + ".txt");
        Scanner scanner1 = new Scanner(path1);
        while (scanner1.hasNextLine()) {
            String line = scanner1.nextLine();
            String [] tempArr = line.split("\t");
            int traId = Integer.parseInt(tempArr[0]);
            Trajectory tra = IndoorSpace.iTrajectory.get(traId);

            for (int i = 1; i < tempArr.length; i++) {
                String[] tempArr1 = tempArr[i].split(",");
                int t = Integer.parseInt(tempArr1[0]);
                for (int j = 1; j < tempArr1.length; j++) {
                    int parId = Integer.parseInt(tempArr1[j]);
                    tra.addPartition(t, parId);
                }
            }
        }

        Path path2 = Paths.get(par_tra + dataset + "_objectNum_" + objectNum + "_sampleDist_" + sampleDist + ".txt");
        Scanner scanner2 = new Scanner(path2);
        while (scanner2.hasNextLine()) {
            String line = scanner2.nextLine();
            String [] tempArr = line.split("\t");
            int parId = Integer.parseInt(tempArr[0]);
            Partition par = IndoorSpace.iPartitions.get(parId);

            for (int i = 1; i < tempArr.length; i++) {
                String[] tempArr1 = tempArr[i].split(",");
                int t = Integer.parseInt(tempArr1[0]);
                ArrayList<Integer> traList = new ArrayList<>();
                for (int j = 1; j < tempArr1.length; j++) {
                    int traId = Integer.parseInt(tempArr1[j]);
                    traList.add(traId);
                }
                par.addTraKeys(t, traList);
            }
        }

    }

    public static void uncertainty(String dataset, int objectNum, double sampleDist) {
        System.out.println("start processing the uncertainty");
        String result = "";
        try {
            FileWriter fw = new FileWriter(tra_partition + dataset + "_objectNum_" + objectNum + "_sampleDist_" + sampleDist + ".txt");
            fw.write(result);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        for (Trajectory trajectory: IndoorSpace.iTrajectory) {
            if (trajectory.getTime().size() == 0) continue;
            System.out.println("trajectoryId: " + trajectory.getTrajectoryId());
            int ts = trajectory.getMinTime();
            int te = trajectory.getMaxTime();

//            System.out.println("ts: " + ts + ", te: " + te);
            int tempTime = ts;
            trajectory.addPartition(ts, IndoorSpace.iTraPoint.get(trajectory.getTrajectory(ts).get(0)).getParID());
            while (tempTime < te) {
                tempTime += DataGenConstant.sampleInterval;
                if (!trajectory.getTime().contains(tempTime)) {
                    System.out.println("time: " + tempTime);
                    Uncertainty.findUncertainTra(trajectory, tempTime);
                }
                else {
                    trajectory.addPartition(tempTime, IndoorSpace.iTraPoint.get(trajectory.getTrajectory(tempTime).get(0)).getParID());
                }
//                System.out.println(trajectory.getTrajectory(tempTime));

            }
            result = "";
            result += trajectory.getTrajectoryId();
            for (int t = trajectory.getMinTime(); t <= trajectory.getMaxTime(); t+= DataGenConstant.sampleInterval) {
                result += "\t" + t;
//                System.out.println("traId: " + tra.getTrajectoryId() + ", t: " + t);
                ArrayList<Integer> pars = trajectory.getParList(t);
                for (int parId: pars) {
                    result += "," + parId;
                }
            }
            result += "\n";
            try {
                FileWriter fw = new FileWriter(tra_partition + dataset + "_objectNum_" + objectNum + "_sampleDist_" + sampleDist + ".txt", true);
                fw.write(result);
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public static void preComputing(String dataset, int objectNum, double sampleDist) {
        System.out.println("start precomputing....");
        String result = "";
        String result1 = "";
        try {
            FileWriter fw = new FileWriter(pre_info + dataset + "_objectNum_" + objectNum + "_sampleDist_" + sampleDist + ".txt");
            fw.write(result);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            FileWriter fw = new FileWriter(par_tra + dataset + "_objectNum_" + objectNum + "_sampleDist_" + sampleDist + ".txt");
            fw.write(result1);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        for (int i = 0; i < IndoorSpace.iPartitions.size(); i++) {
            Partition par = IndoorSpace.iPartitions.get(i);
            System.out.println("partitionId: " + par.getmID() + "-----------------");
            result = "";
            result += par.getmID();
            result1 = "";
            result1 += par.getmID();
            Set<Integer> timeSet = par.getTrajectorys().keySet();

            for (int t: timeSet) {
                Set<Integer> traSet = par.getTrajectorys(t).keySet();
                result1 += "\t" + t;
                for (int traId: traSet) {

                    result1 += "," + traId;
                }
//                System.out.println("t: " + t);
                Set<Integer> traSet1 = par.getTrajectorys(t).keySet();
//                System.out.println("traSet: " + traSet1);
//                if (traSet1.size() < 2) continue;
                for (int tra1: traSet1) {
                    Set<Integer> traSet2 = par.getTrajectorys(t).keySet();
                    for (int tra2: traSet2) {
                        if (tra1 == tra2) continue;
//                        System.out.println("tra1: " + tra1 + ", tra2: " + tra2);
                        if (par.getTraDistance(t, tra1 + "-" + tra2) != null || par.getTraDistance(t, tra2 + "-" + tra1) != null) {
//                            System.out.println("continue...");
                            continue;
                        }
                        ArrayList<Integer> traPointList1 = par.getTrajectorys(t).get(tra1);
                        ArrayList<Integer> traPointList2 = par.getTrajectorys(t).get(tra2);
                        ArrayList<ArrayList<Double>> dists = calDistance(traPointList1, traPointList2);
                        String s = tra1 + "-" + tra2;
//                        System.out.println("traPointList1: " + traPointList1);
//                        System.out.println("traPointList2: " + traPointList2);
//                        System.out.println("dists: " + dists);
                        par.addTraDistance(t, s, dists);
//                        par.addTraRelationNum(t, s, dists.size());
                    }
                }

                if (par.getTraDistance(t) == null) continue;
                Set<String> infoSet = par.getTraDistance(t).keySet();
                boolean isVisit_s = false;
                String temp_s = "";
                for (String s: infoSet) {
                    ArrayList<ArrayList<Double>> dists = par.getTraDistance(t, s);
                    if (dists == null) continue;
                    String temp = "";
                    boolean isVisit = false;
                    for (ArrayList<Double> dist: dists) {
                        if (dist.get(0) > 5) break;
                        temp += "," + dist.get(0) + "&" + dist.get(1);
                        isVisit = true;
                        isVisit_s = true;
                    }
                    if (isVisit) {
                        temp_s += ";" + s + temp;
                    }
                }
                if (isVisit_s) {
                    result += "\t" + t + temp_s;
                }


            }
            result += "\n";
            result1 += "\n";

            try {
                FileWriter fw = new FileWriter(pre_info + dataset + "_objectNum_" + objectNum + "_sampleDist_" + sampleDist + ".txt", true);
                fw.write(result);
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            try {
                FileWriter fw = new FileWriter(par_tra + dataset + "_objectNum_" + objectNum + "_sampleDist_" + sampleDist + ".txt", true);
                fw.write(result1);
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public static ArrayList<ArrayList<Double>> calDistance(ArrayList<Integer> list1, ArrayList<Integer> list2) {
        ArrayList<ArrayList<Double>> result = new ArrayList<>();
        for (int traPointId1: list1) {
            TrajectoryPoint traPoint1 = IndoorSpace.iTraPoint.get(traPointId1);
            for (int traPointId2: list2) {
                TrajectoryPoint traPoint2 = IndoorSpace.iTraPoint.get(traPointId2);
                double dist = traPoint1.eDist(traPoint2);
                double pro = traPoint1.getProbability() * traPoint2.getProbability();
                if (dist <= 5) {
                    result = addDist(result, dist, pro);
                }
            }
        }
        return result;
    }

    public static ArrayList<ArrayList<Double>> addDist(ArrayList<ArrayList<Double>> list, double dist, double pro) {
        ArrayList<Double> dist_pro = new ArrayList<>(Arrays.asList(dist, pro));
        if (list.size() == 0) {
            list.add(dist_pro);
            return list;
        }
        int initSize = list.size();
        for (int i = list.size() - 1; i >= 0; i--) {
            if (i == list.size() -1 && dist > list.get(i).get(0)) {
                list.add(dist_pro);
                break;
            }
            else if (dist > list.get(i).get(0)) {
                list.add(list.get(list.size() - 1));
                for (int j = list.size() - 1; j > i + 1; j--){
                    list.set(j, list.get(j - 1));
                }
                list.set(i + 1, dist_pro);
                break;
            }
        }
        if (list.size() == initSize) {
            list.add(list.get(list.size() - 1));
            for (int j = list.size() - 1; j > 0; j--){
                list.set(j, list.get(j - 1));
            }
            list.set(0, dist_pro);
        }
        return list;

    }

    public static void main(String[] arg) throws IOException {

        PrintOut printOut = new PrintOut();
        Runtime runtime0 = Runtime.getRuntime();
        runtime0.gc();
        long startMem0 = runtime0.totalMemory() - runtime0.freeMemory();


        DataGen dataGen = new DataGen();
        dataGen.genAllData(DataGenConstant.dataType, DataGenConstant.divisionType);
//        HSMDataGenRead hsmDataGenRead = new HSMDataGenRead();
//        hsmDataGenRead.dataGen("newHsm");

        GenTopology genTopology = new GenTopology();
        genTopology.genTopology();
//        HSMTrajectoryGen.trajectoryGen_read(HSMTrajectoryGen.trajectoryPoints, HSMTrajectoryGen.trajectorys, "HSM");
        TrajectoryGen.trajectoryGen_read(TrajectoryGen.trajectoryPoints, TrajectoryGen.trajectorys,"syn", 8000, false);

        long endMem0 = runtime0.totalMemory() - runtime0.freeMemory();
        long mem0 = (endMem0 - startMem0) / 1024; // kb


        preInfo_read("syn", 8000, DataGenConstant.sample_dist);

        long endMem1 = runtime0.totalMemory() - runtime0.freeMemory();
        long mem1 = (endMem1 - endMem0) / 1024; // kb


        System.out.println("mem0: " + mem0 + ", mem1: " + mem1);



    }
}
