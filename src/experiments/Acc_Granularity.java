package experiments;

import algorithm.Algo_ICQ;
import algorithm.PrintOut;
import datagenerate.DataGen;
import datagenerate.PreCompute;
import datagenerate.TrajectoryGen;
import iDModel.GenTopology;
import utilities.DataGenConstant;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Acc_Granularity {
    static String dataset = "syn";

    static double probabilityThreshold = 0.5;
    public static String result_true_granularity = System.getProperty("user.dir") + "/result/result_" + dataset + "_true_granularity.txt";
    public static String result_pre_granularity_icq = System.getProperty("user.dir") + "/result/result_" + dataset + "_pre_granularity_icq.txt";
    public static String result_pre_granularity_icq_base = System.getProperty("user.dir") + "/result/result_" + dataset + "_pre_granularity_icqBase.txt";
    public static String result_pre_granularity_icq_base_new = System.getProperty("user.dir") + "/result/result_" + dataset + "_pre_granularity_icqBaseNew.txt";
    public static String result_pre_granularity_icq_base_unseen = System.getProperty("user.dir") + "/result/result_" + dataset + "_pre_granularity_icqBaseUnseen.txt";


    public static String outFilePrecision = System.getProperty("user.dir") + "/result/" + dataset + "_granularity_precision.csv";
    public static String outFileRecall = System.getProperty("user.dir") + "/result/" + dataset + "_granularity_recall.csv";
    public static String outFileF = System.getProperty("user.dir") + "/result/" + dataset + "_granularity_F.csv";

    public static void getGroundTruth() throws IOException {
        PrintOut printOut = new PrintOut();
        DataGen dataGen = new DataGen();
        dataGen.genAllData(DataGenConstant.dataType, DataGenConstant.divisionType);

        GenTopology genTopology = new GenTopology();
        genTopology.genTopology();
        TrajectoryGen.trajectoryGen_read(TrajectoryGen.trajectoryPoints, TrajectoryGen.trajectorys, dataset, 4000, true);
//        PreCompute.preInfo_read(dataset, 4000, DataGenConstant.sample_dist);

        System.out.println("start querying...");
        ArrayList<Double> granularities = new ArrayList<>(Arrays.asList(0.2, 0.4, 0.6, 0.8, 1.0));
        String s = "";

        for (int i = 0; i < granularities.size(); i++) {
            DataGenConstant.sample_dist = granularities.get(i);
            System.out.println("granularity: " + DataGenConstant.sample_dist);

            s += DataGenConstant.sample_dist;

            ArrayList<Integer> trajectorys = new ArrayList<>(Arrays.asList(4, 7, 90, 23, 34, 87, 99, 1, 67, 29, 5, 31, 47, 39, 32, 52, 96, 22, 91, 63));

            for (int j = 0; j < trajectorys.size(); j++) {
                s += "\t" + j;
                System.out.println("traId: " + j);
                int traId = trajectorys.get(j);

                HashMap<Integer, Double> result0 = new HashMap<>();

                Algo_ICQ.icq_clear();
                result0 = Algo_ICQ.icq(traId, probabilityThreshold, DataGenConstant.startTime, DataGenConstant.endTime);
                System.out.println("icq result: " + result0);

                Set<Integer> parSet0 = result0.keySet();
                for (int parId: parSet0) {
                    s += "," + parId + "&" + result0.get(parId);
                }

            }
            s += "\n";
        }

        try {
            FileWriter output = new FileWriter(result_true_granularity);
            output.write(s);
            output.flush();
            output.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void calAccuracy() throws IOException {
        String resultPrecision = "granularity" + "\t" +"icq" + "\t" + "icq_baseNew" + "\t" + "icq_baseUnseen" + "\n";
        String resultRecall = "granularity" + "\t" + "icq" + "\t" + "icq_baseNew" + "\t" + "icq_baseUnseen" + "\n";
        String resultF = "granularity" + "\t" +"icq" + "\t" + "icq_baseNew" + "\t" + "icq_baseUnseen" + "\n";

        Path path = Paths.get(result_true_granularity);
        Scanner scanner = new Scanner(path);

        HashMap<Double, HashMap<Integer, HashMap<Integer, Double>>> granularity_true_map = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String [] tempArr1 = line.split("\t");
            double granularity = Double.parseDouble(tempArr1[0]);

            HashMap<Integer,HashMap<Integer, Double>> tra_map = new HashMap<>();

            for (int m = 1; m < tempArr1.length; m++) {
                String [] tempArr2 = tempArr1[m].split(",");
                int traId = Integer.parseInt(tempArr2[0]);


                HashMap<Integer, Double> true_map = new HashMap<>();
                for (int i = 1; i < tempArr2.length; i++) {
                    int parId = Integer.parseInt(tempArr2[i].split("&")[0]);
                    double pro = Double.parseDouble(tempArr2[i].split("&")[1]);
                    true_map.put(parId, pro);
                    System.out.println("granularity: " + granularity + ", traId: " + traId + ", parId: " + parId + ", pro: " + pro);
                }


                tra_map.put(traId, true_map);
            }

            granularity_true_map.put(granularity, tra_map);
        }

        Path path1 = Paths.get(result_pre_granularity_icq);
        Scanner scanner1 = new Scanner(path1);

        HashMap<Double, HashMap<Integer, HashMap<Integer, Double>>> granularity_icq_map = new HashMap<>();
        while (scanner1.hasNextLine()) {
            String line = scanner1.nextLine();
            String [] tempArr1 = line.split("\t");
            double granularity = Double.parseDouble(tempArr1[0]);

            HashMap<Integer,HashMap<Integer, Double>> tra_map = new HashMap<>();

            for (int m = 1; m < tempArr1.length; m++) {
                String [] tempArr2 = tempArr1[m].split(",");
                int traId = Integer.parseInt(tempArr2[0]);


                HashMap<Integer, Double> icq_map = new HashMap<>();
                for (int i = 1; i < tempArr2.length; i++) {
                    int parId = Integer.parseInt(tempArr2[i].split("&")[0]);
                    double pro = Double.parseDouble(tempArr2[i].split("&")[1]);
                    icq_map.put(parId, pro);
                }


                tra_map.put(traId, icq_map);
            }

            granularity_icq_map.put(granularity, tra_map);
        }

        Path path2 = Paths.get(result_pre_granularity_icq_base_new);
        Scanner scanner2 = new Scanner(path2);

        HashMap<Double, HashMap<Integer, HashMap<Integer, Double>>> granularity_icqBaseNew_map = new HashMap<>();
        while (scanner2.hasNextLine()) {
            String line = scanner2.nextLine();
            String [] tempArr1 = line.split("\t");
            double granularity = Double.parseDouble(tempArr1[0]);

            HashMap<Integer,HashMap<Integer, Double>> tra_map = new HashMap<>();

            for (int m = 1; m < tempArr1.length; m++) {
                String [] tempArr2 = tempArr1[m].split(",");
                int traId = Integer.parseInt(tempArr2[0]);


                HashMap<Integer, Double> icq_map = new HashMap<>();
                for (int i = 1; i < tempArr2.length; i++) {
                    int parId = Integer.parseInt(tempArr2[i].split("&")[0]);
                    double pro = Double.parseDouble(tempArr2[i].split("&")[1]);
                    icq_map.put(parId, pro);
                }


                tra_map.put(traId, icq_map);
            }

            granularity_icqBaseNew_map.put(granularity, tra_map);
        }

        Path path3 = Paths.get(result_pre_granularity_icq_base_unseen);
        Scanner scanner3 = new Scanner(path3);

        HashMap<Double, HashMap<Integer, HashMap<Integer, Double>>> granularity_icqBaseUnseen_map = new HashMap<>();
        while (scanner3.hasNextLine()) {
            String line = scanner3.nextLine();
            String [] tempArr1 = line.split("\t");
            double granularity = Double.parseDouble(tempArr1[0]);

            HashMap<Integer,HashMap<Integer, Double>> tra_map = new HashMap<>();

            for (int m = 1; m < tempArr1.length; m++) {
                String [] tempArr2 = tempArr1[m].split(",");
                int traId = Integer.parseInt(tempArr2[0]);


                HashMap<Integer, Double> icq_map = new HashMap<>();
                for (int i = 1; i < tempArr2.length; i++) {
                    int parId = Integer.parseInt(tempArr2[i].split("&")[0]);
                    double pro = Double.parseDouble(tempArr2[i].split("&")[1]);
                    icq_map.put(parId, pro);
                }


                tra_map.put(traId, icq_map);
            }

            granularity_icqBaseUnseen_map.put(granularity, tra_map);
        }



        ArrayList<Double> granularities = new ArrayList<>(Arrays.asList(0.2, 0.4, 0.6, 0.8, 1.0));

        for (double granularity: granularities) {
            resultF += granularity;
            resultRecall += granularity;
            resultPrecision += granularity;

            System.out.println("granularity: " + granularity);
            ArrayList<Double> precisionList1 = new ArrayList<>();
            ArrayList<Double> recallList1 = new ArrayList<>();
            ArrayList<Double> FList1 = new ArrayList<>();

            ArrayList<Double> precisionList2 = new ArrayList<>();
            ArrayList<Double> recallList2 = new ArrayList<>();
            ArrayList<Double> FList2 = new ArrayList<>();

            ArrayList<Double> precisionList3 = new ArrayList<>();
            ArrayList<Double> recallList3 = new ArrayList<>();
            ArrayList<Double> FList3 = new ArrayList<>();



            HashMap<Integer, HashMap<Integer, Double>> tra_icq_map = granularity_icq_map.get(granularity);
            HashMap<Integer, HashMap<Integer, Double>> tra_icqBaseNew_map = granularity_icqBaseNew_map.get(granularity);
            HashMap<Integer, HashMap<Integer, Double>> tra_true_map = granularity_true_map.get(granularity);
            HashMap<Integer, HashMap<Integer, Double>> tra_icqBaseUnseen_map = granularity_icqBaseUnseen_map.get(granularity);

            System.out.println("tra_icq_map size: " + tra_icq_map.size());
            System.out.println("tra_icq_baseNew_map size: " + tra_icqBaseNew_map.size());
            System.out.println("tra_true_map size: " + tra_true_map.size());
            System.out.println("tra_icq_baseUnseen_map size: " + tra_icqBaseUnseen_map.size());

            ArrayList<Integer> trajectorys = new ArrayList<>(Arrays.asList(4, 7, 90, 23, 34, 87, 99, 1, 67, 29, 5, 31, 47, 39, 32, 52, 96, 22, 91, 63));

            for (int i = 0; i < trajectorys.size(); i++) {
                System.out.println("traId: " + i);

                HashMap<Integer, Double> icq_map = tra_icq_map.get(i);
                HashMap<Integer, Double> icqBaseNew_map = tra_icqBaseNew_map.get(i);
                HashMap<Integer, Double> true_map = tra_true_map.get(i);
                HashMap<Integer, Double> icqBaseUnseen_map = tra_icqBaseUnseen_map.get(i);

                System.out.println("icq_map size: " + icq_map.size());
                System.out.println("icq_baseNew_map size: " + icqBaseNew_map.size());
                System.out.println("true_map size: " + true_map.size());
                System.out.println("icq_baseUnseen_map size: " + icqBaseUnseen_map.size());


                Set<Integer> icq_list = icq_map.keySet();
                Set<Integer> icqBaseNew_list = icqBaseNew_map.keySet();
                Set<Integer> true_list = true_map.keySet();
                Set<Integer> icqBaseUnseen_list = icqBaseUnseen_map.keySet();


                int num1 = 0;
//                double error1 = 0;
                double F1 = 0;
                int num2 = 0;
//                double error2 = 0;
                double F2 = 0;

                int num3 = 0;
                double F3 = 0;

                for (int true_parId: true_list) {
                    for (int icq_parId: icq_list) {
                        if (icq_parId == true_parId) {
                            num1++;
//                            System.out.println("icq error: " + Math.abs(true_map.get(true_parId) - icq_map.get(icq_parId)));
//                            error1 += Math.abs(true_map.get(true_parId) - icq_map.get(icq_parId));

                        }
                    }
                    for (int icq_parId: icqBaseNew_list) {
                        if (icq_parId == true_parId) {
                            num2++;
//                            System.out.println("icq_base error: " + Math.abs(true_map.get(true_parId) - icqBase_map.get(icq_parId)));
//                            error2 += Math.abs(true_map.get(true_parId) - icqBase_map.get(icq_parId));

                        }
                    }

                    for (int icq_parId: icqBaseUnseen_list) {
                        if (icq_parId == true_parId) {
                            num3++;
                        }
                    }
                }

                double precision1;
                double recall1;
                double precision2;
                double recall2;
                double precision3;
                double recall3;


                if (icq_list.size() == 0) {
                    precision1 = 1;
                }
                else {
                    precision1 = (double)num1 / (double)icq_list.size();
                }

                if (true_list.size() == 0) {
                    recall1 = 1;

                }
                else {
                    recall1 = (double)num1 / (double)true_list.size();

                }

//                if (num1 == 0) {
//                    error1 = 0;
//                    acc1 = 1 - error1;
//                }
//                else {
//                    System.out.println("error1 sum: " + error1);
//                    error1 = error1 / num1;
//                    System.out.println("error1 ave: " + error1);
//                    acc1 = 1 - error1;
//                    System.out.println("acc1: " + acc1);
//                }
                F1 = 2 * (precision1 * recall1) / (precision1 + recall1);

                System.out.println("precision1: " + precision1 + " recall1: " + recall1);

                precisionList1.add(precision1);
                recallList1.add(recall1);
                FList1.add(F1);

                if (icqBaseNew_list.size() == 0) {
                    precision2 = 1;
                }
                else {
                    precision2 = (double)num2 / (double)icqBaseNew_list.size();
                }

                if (true_list.size() == 0) {
                    recall2 = 1;

                }
                else {
                    recall2 = (double)num2 / (double)true_list.size();

                }

//                if (num2 == 0) {
//                    error2 = 0;
//                    acc2 = 1 - error2;
//                }
//                else {
//                    System.out.println("error2 sum: " + error2);
//                    error2 = error2 / num2;
//                    System.out.println("error2 ave: " + error2);
//                    acc2 = 1 - error2;
//                    System.out.println("acc2: " + acc2);
//                }

                F2 = 2 * (precision2 * recall2) / (precision2 + recall2);


                System.out.println("precision2: " + precision2 + " recall2: " + recall2);

                precisionList2.add(precision2);
                recallList2.add(recall2);
                FList2.add(F2);


                if (icqBaseUnseen_list.size() == 0) {
                    precision3 = 1;
                }
                else {
                    precision3 = (double)num3 / (double)icqBaseUnseen_list.size();
                }

                if (true_list.size() == 0) {
                    recall3 = 1;

                }
                else {
                    recall3 = (double)num3 / (double)true_list.size();

                }

                F3 = 2 * (precision3 * recall3) / (precision3 + recall3);


                System.out.println("precision2: " + precision3 + " recall2: " + recall3);

                precisionList3.add(precision3);
                recallList3.add(recall3);
                FList3.add(F3);


            }

//            System.out.println("precisionList1: " + precisionList1);
//            System.out.println("recallList1: " + recallList1);
            ArrayList<ArrayList<Double>> precisionLists = new ArrayList<>();
            ArrayList<ArrayList<Double>> recallLists = new ArrayList<>();
            ArrayList<ArrayList<Double>> FLists = new ArrayList<>();

            precisionLists.add(precisionList1);
            precisionLists.add(precisionList2);
            precisionLists.add(precisionList3);

            recallLists.add(recallList1);
            recallLists.add(recallList2);
            recallLists.add(recallList3);

            FLists.add(FList1);
            FLists.add(FList2);
            FLists.add(FList3);


            for (int i = 0; i < precisionLists.size(); i++) {
                double sum1 = 0;
                double ave1 = 0;
                ArrayList<Double> precisionList = precisionLists.get(i);

                for (double precision: precisionList) {
                    sum1 += precision;
                    //                System.out.println("sum1 + precision: " + sum1);
                }
                ave1 = sum1 / precisionList.size();
                resultPrecision += "\t" + ave1;
            }
            resultPrecision += "\n";


            for (int i = 0; i < recallLists.size(); i++) {
                double sum2 = 0;
                double ave2 = 0;
                ArrayList<Double> recallList = recallLists.get(i);

                for (double recall: recallList) {
                    sum2 += recall;
                }
                ave2 = sum2 / recallList.size();
                resultRecall += "\t" + ave2;
            }
            resultRecall += "\n";


            for (int i = 0; i < FLists.size(); i++) {
                double sum3 = 0;
                double ave3 = 0;
                ArrayList<Double> FList = FLists.get(i);

                for (double F: FList) {
                    sum3 += F;
                    System.out.println("F: " + F + "........");
                }
                ave3 = sum3 / FList.size();
                System.out.println("ave3: " + ave3 + "........");
                resultF += "\t" + ave3;
            }
            resultF += "\n";
        }

        FileOutputStream outputPrecision = new FileOutputStream(outFilePrecision);
        outputPrecision.write(resultPrecision.getBytes());
        outputPrecision.flush();
        outputPrecision.close();

        FileOutputStream outputRecall = new FileOutputStream(outFileRecall);
        outputRecall.write(resultRecall.getBytes());
        outputRecall.flush();
        outputRecall.close();

        FileOutputStream outputF = new FileOutputStream(outFileF);
        outputF.write(resultF.getBytes());
        outputF.flush();
        outputF.close();


    }

    public static void main(String[] arg) throws IOException{
        getGroundTruth();
        calAccuracy();
    }
}
