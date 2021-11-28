package experiments;

import algorithm.*;
import datagenerate.*;
import iDModel.GenTopology;
import utilities.DataGenConstant;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;


public class HSM_Test_Granularity {
    public static double probabilityThreshold = 0.5;
    public static String dataset = "HSM";

//    public static String result_pre_distance_icqBase = System.getProperty("user.dir") + "/result/result_" + dataset + "_pre_distance_icqBase.txt";
//    public static String result_pre_distance_icq = System.getProperty("user.dir") + "/result/result_" + dataset + "_pre_distance_icq.txt";
//    public static String result_pre_distance_icqPre = System.getProperty("user.dir") + "/result/result_" + dataset + "_pre_distance_icqPre.txt";


    public static void run(String outFileTime, String outFileMemory) throws IOException {
        String resultTime = "granularity" + "\t" + "icq_naive" + "\t" + "icq" + "\t" + "icq_baseNew" + "\t" + "icq_baseUnseen" + "\n";
        String resultMemo = "granularity" + "\t" + "icq_naive" + "\t" + "icq" + "\t" + "icq_baseNew" + "\t" + "icq_baseUnseen" + "\n";

        String s0 = "";
        String s1 = "";
        String s2 = "";
        String s3 = "";

        ArrayList<Double> granularities = new ArrayList<>(Arrays.asList(0.2, 0.2, 0.4, 0.6, 0.8, 1.0));

        for (int i = 0; i < granularities.size(); i++) {
            DataGenConstant.sample_dist = granularities.get(i);
            System.out.println("granularity: " + DataGenConstant.sample_dist);

            resultTime += DataGenConstant.sample_dist + "\t";
            resultMemo += DataGenConstant.sample_dist + "\t";

            s0 += DataGenConstant.sample_dist;
            s1 += DataGenConstant.sample_dist;
            s2 += DataGenConstant.sample_dist;
            s3 += DataGenConstant.sample_dist;



            ArrayList<Long> arrTime0 = new ArrayList<>();
            ArrayList<Long> arrTime1 = new ArrayList<>();
            ArrayList<Long> arrTime2 = new ArrayList<>();
            ArrayList<Long> arrTime3 = new ArrayList<>();

            ArrayList<Long> arrMem0 = new ArrayList<>();
            ArrayList<Long> arrMem1 = new ArrayList<>();
            ArrayList<Long> arrMem2 = new ArrayList<>();
            ArrayList<Long> arrMem3 = new ArrayList<>();


            ArrayList<Integer> trajectorys = new ArrayList<>(Arrays.asList(4, 797, 241, 945, 34, 1438, 99, 382, 843, 1003, 5, 795, 242, 947, 32, 1435, 96, 381, 847, 1007));
            for (int j = 0; j < trajectorys.size(); j++) {
                int traId = trajectorys.get(j);
                s0 += "\t" + j;
                s1 += "\t" + j;
                s2 += "\t" + j;
                s3 += "\t" + j;

                System.out.println("traId: " + j);
                for (int k = 0; k < 1; k++) {
                    // icq_naive
                    Algo_ICQ_naive.icq_naive_clear();
                    Runtime runtime0 = Runtime.getRuntime();
                    runtime0.gc();
                    HashMap<Integer, Double> result0 = new HashMap<>();
                    long startMem0 = runtime0.totalMemory() - runtime0.freeMemory();
                    long startTime0 = System.currentTimeMillis();
                    result0 = Algo_ICQ_naive.icq_naive(traId, probabilityThreshold, DataGenConstant.startTime, DataGenConstant.endTime);
                    System.out.println("icq_naive result: " + result0);
                    long endTime0 = System.currentTimeMillis();
                    long endMem0 = runtime0.totalMemory() - runtime0.freeMemory();
                    long mem0 = (endMem0 - startMem0) / 1024; // kb
                    long time0 = endTime0 - startTime0;
                    arrTime0.add(time0);
                    arrMem0.add(mem0);
                    Set<Integer> parSet0 = result0.keySet();
                    for (int parId: parSet0) {
                        s0 += "," + parId + "&" + result0.get(parId);
                    }

                    // icq
                    Algo_ICQ.icq_clear();
                    Runtime runtime1 = Runtime.getRuntime();
                    runtime1.gc();
                    HashMap<Integer, Double> result1 = new HashMap<>();
                    long startMem1 = runtime1.totalMemory() - runtime1.freeMemory();
                    long startTime1 = System.currentTimeMillis();
                    result1 = Algo_ICQ.icq(traId, probabilityThreshold, DataGenConstant.startTime, DataGenConstant.endTime);
                    System.out.println("icq result: " + result1);
                    long endTime1 = System.currentTimeMillis();
                    long endMem1 = runtime1.totalMemory() - runtime1.freeMemory();
                    long mem1 = (endMem1 - startMem1) / 1024; // kb
                    long time1 = endTime1 - startTime1;
                    arrTime1.add(time1);
                    arrMem1.add(mem1);
                    Set<Integer> parSet1 = result1.keySet();
                    for (int parId: parSet1) {
                        s1 += "," + parId + "&" + result1.get(parId);
                    }


                    // icq_baseNew
                    Algo_ICQ_baselineNew.icq_baselineNew_clear();
                    Runtime runtime2 = Runtime.getRuntime();
                    runtime2.gc();
                    HashMap<Integer, Double> result2 = new HashMap<>();
                    long startMem2 = runtime2.totalMemory() - runtime2.freeMemory();
                    long startTime2 = System.currentTimeMillis();
                    result2 = Algo_ICQ_baselineNew.icq_baselineNew(traId, probabilityThreshold, DataGenConstant.startTime, DataGenConstant.endTime);
                    System.out.println("icq_baseNew result: " + result2);
                    long endTime2 = System.currentTimeMillis();
                    long endMem2 = runtime2.totalMemory() - runtime2.freeMemory();
                    long mem2 = (endMem2 - startMem2) / 1024; // kb
                    long time2 = endTime2 - startTime2;
                    arrTime2.add(time2);
                    arrMem2.add(mem2);
                    Set<Integer> parSet2 = result2.keySet();
                    for (int parId: parSet2) {
                        s2 += "," + parId + "&" + result2.get(parId);
                    }

                    // icq_base_unseen
//                    Algo_ICQ_baseline_unseen.icq_baseline_unseen_clear();
                    Runtime runtime3 = Runtime.getRuntime();
                    runtime3.gc();
                    HashMap<Integer, Double> result3 = new HashMap<>();
                    long startMem3 = runtime3.totalMemory() - runtime3.freeMemory();
                    long startTime3 = System.currentTimeMillis();
                    result3 = Algo_ICQ_baseline_unseen.icq_baseline_unseen(traId, probabilityThreshold, DataGenConstant.startTime, DataGenConstant.endTime);
                    System.out.println("icq_baseline_unseen result: " + result3);
                    long endTime3 = System.currentTimeMillis();
                    long endMem3 = runtime3.totalMemory() - runtime3.freeMemory();
                    long mem3 = (endMem3 - startMem3) / 1024; // kb
                    long time3 = endTime3 - startTime3;
                    arrTime3.add(time3);
                    arrMem3.add(mem3);

                    Set<Integer> parSet3 = result3.keySet();
                    for (int parId: parSet3) {
                        s3 += "," + parId + "&" + result3.get(parId);
                    }


                }
            }

            s0 += "\n";
            s1 += "\n";
            s2 += "\n";
            s3 += "\n";


            ArrayList<ArrayList<Long>> arrTimeAll = new ArrayList<>();
            arrTimeAll.add(arrTime0);
            arrTimeAll.add(arrTime1);
            arrTimeAll.add(arrTime2);
            arrTimeAll.add(arrTime3);

            for (int j = 0; j < arrTimeAll.size(); j++) {
                long sum = 0;
                long ave = 0;
                for (int h = 0; h < arrTimeAll.get(j).size(); h++) {
                    sum += arrTimeAll.get(j).get(h);
                }
                ave = sum / arrTimeAll.get(j).size();
                resultTime += ave + "\t";
            }
            resultTime += "\n";

            ArrayList<ArrayList<Long>> arrMemAll = new ArrayList<>();
            arrMemAll.add(arrMem0);
            arrMemAll.add(arrMem1);
            arrMemAll.add(arrMem2);
            arrMemAll.add(arrMem3);

            for (int j = 0; j < arrMemAll.size(); j++) {
                long sum = 0;
                long ave = 0;
                for (int h = 0; h < arrMemAll.get(j).size(); h++) {
                    sum += arrMemAll.get(j).get(h);
                }
                ave = sum / arrMemAll.get(j).size();
                resultMemo += ave + "\t";
            }
            resultMemo += "\n";


        }

        FileOutputStream outputTime = new FileOutputStream(outFileTime, true);
        outputTime.write(resultTime.getBytes());
        outputTime.flush();
        outputTime.close();

        FileOutputStream outputMem = new FileOutputStream(outFileMemory, true);
        outputMem.write(resultMemo.getBytes());
        outputMem.flush();
        outputMem.close();


//        try {
//            FileWriter output = new FileWriter(result_pre_distance_icqBase);
//            output.write(s0);
//            output.flush();
//            output.close();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        try {
//            FileWriter output = new FileWriter(result_pre_distance_icq);
//            output.write(s1);
//            output.flush();
//            output.close();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        try {
//            FileWriter output = new FileWriter(result_pre_distance_icqPre);
//            output.write(s2);
//            output.flush();
//            output.close();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }


    }


    public static void main(String arg[]) throws IOException {
        PrintOut printOut = new PrintOut();
        HSMDataGenRead hsmDataGenRead= new HSMDataGenRead();
        hsmDataGenRead.dataGen("newHsm");

        GenTopology genTopology = new GenTopology();
        genTopology.genTopology();
        HSMTrajectoryGen.trajectoryGen_read(HSMTrajectoryGen.trajectoryPoints, HSMTrajectoryGen.trajectorys,"HSM");
//        PreCompute.preInfo_read(dataset, 10000, DataGenConstant.sample_dist);


        // granularity
        String outFileTime = System.getProperty("user.dir") + "/result/" + dataset + "_granularity_time.csv";
        String outFileMemory = System.getProperty("user.dir") + "/result/" + dataset + "_granularity_memory.csv";

        run(outFileTime, outFileMemory);

    }

}
