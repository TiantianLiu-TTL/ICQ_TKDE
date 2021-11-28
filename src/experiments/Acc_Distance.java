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

public class Acc_Distance {
    static String dataset = "syn";

    static double probabilityThreshold = 0.5;
    public static String result_true_distance = System.getProperty("user.dir") + "/result/result_" + dataset + "_true_distance.txt";
    public static String result_pre_distance_icq = System.getProperty("user.dir") + "/result/result_" + dataset + "_pre_distance_icq.txt";
    public static String result_pre_distance_icq_base = System.getProperty("user.dir") + "/result/result_" + dataset + "_pre_distance_icqBase.txt";
    public static String result_pre_distance_icq_base_new = System.getProperty("user.dir") + "/result/result_" + dataset + "_pre_distance_icqBaseNew.txt";
    public static String result_pre_distance_icq_base_unseen = System.getProperty("user.dir") + "/result/result_" + dataset + "_pre_distance_icqBaseUnseen.txt";


    public static String outFilePrecision = System.getProperty("user.dir") + "/result/" + dataset + "_distance_precision.csv";
    public static String outFileRecall = System.getProperty("user.dir") + "/result/" + dataset + "_distance_recall.csv";
    public static String outFileF = System.getProperty("user.dir") + "/result/" + dataset + "_distance_F.csv";

    public static void getGroundTruth() throws IOException {
        PrintOut printOut = new PrintOut();
        DataGen dataGen = new DataGen();
        dataGen.genAllData(DataGenConstant.dataType, DataGenConstant.divisionType);

        GenTopology genTopology = new GenTopology();
        genTopology.genTopology();
        TrajectoryGen.trajectoryGen_read(TrajectoryGen.trajectoryPoints, TrajectoryGen.trajectorys, dataset, 4000, true);
//        PreCompute.preInfo_read(dataset, 4000, DataGenConstant.sample_dist);

        System.out.println("start querying...");
        ArrayList<Integer> distances = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        String s = "";

        for (int i = 0; i < distances.size(); i++) {
            DataGenConstant.distanceThreshold = distances.get(i);
            System.out.println("distance: " + DataGenConstant.distanceThreshold);

            s += DataGenConstant.distanceThreshold;

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
            FileWriter output = new FileWriter(result_true_distance);
            output.write(s);
            output.flush();
            output.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void calAccuracy() throws IOException {
        String resultPrecision = "distance" + "\t" +"icq" + "\t" + "icq_base" + "\t" + "icq_baseNew" + "\t" + "icq_baseUnseen" + "\n";
        String resultRecall = "distance" + "\t" + "icq" + "\t" + "icq_base" + "\t" + "icq_baseNew" + "\t" + "icq_baseUnseen" + "\n";
        String resultF = "distance" + "\t" +"icq" + "\t" + "icq_base" + "\t" + "icq_baseNew" + "\t" + "icq_baseUnseen" + "\n";

        Path path = Paths.get(result_true_distance);
        Scanner scanner = new Scanner(path);

        HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> distance_true_map = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String [] tempArr1 = line.split("\t");
            int distance = Integer.parseInt(tempArr1[0]);

            HashMap<Integer,HashMap<Integer, Double>> tra_map = new HashMap<>();

            for (int m = 1; m < tempArr1.length; m++) {
                String [] tempArr2 = tempArr1[m].split(",");
                int traId = Integer.parseInt(tempArr2[0]);


                HashMap<Integer, Double> true_map = new HashMap<>();
                for (int i = 1; i < tempArr2.length; i++) {
                    int parId = Integer.parseInt(tempArr2[i].split("&")[0]);
                    double pro = Double.parseDouble(tempArr2[i].split("&")[1]);
                    true_map.put(parId, pro);
                    System.out.println("distance: " + distance + ", traId: " + traId + ", parId: " + parId + ", pro: " + pro);
                }


                tra_map.put(traId, true_map);
            }

            distance_true_map.put(distance, tra_map);
        }

        Path path1 = Paths.get(result_pre_distance_icq);
        Scanner scanner1 = new Scanner(path1);

        HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> distance_icq_map = new HashMap<>();
        while (scanner1.hasNextLine()) {
            String line = scanner1.nextLine();
            String [] tempArr1 = line.split("\t");
            int distance = Integer.parseInt(tempArr1[0]);

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

            distance_icq_map.put(distance, tra_map);
        }

        Path path2 = Paths.get(result_pre_distance_icq_base);
        Scanner scanner2 = new Scanner(path2);

        HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> distance_icqBase_map = new HashMap<>();
        while (scanner2.hasNextLine()) {
            String line = scanner2.nextLine();
            String [] tempArr1 = line.split("\t");
            int distance = Integer.parseInt(tempArr1[0]);

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

            distance_icqBase_map.put(distance, tra_map);
        }

        Path path3 = Paths.get(result_pre_distance_icq_base_new);
        Scanner scanner3 = new Scanner(path3);

        HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> distance_icqBaseNew_map = new HashMap<>();
        while (scanner3.hasNextLine()) {
            String line = scanner3.nextLine();
            String [] tempArr1 = line.split("\t");
            int distance = Integer.parseInt(tempArr1[0]);

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

            distance_icqBaseNew_map.put(distance, tra_map);
        }

        Path path4 = Paths.get(result_pre_distance_icq_base_unseen);
        Scanner scanner4 = new Scanner(path4);

        HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> distance_icqBaseUnseen_map = new HashMap<>();
        while (scanner4.hasNextLine()) {
            String line = scanner4.nextLine();
            String [] tempArr1 = line.split("\t");
            int distance = Integer.parseInt(tempArr1[0]);

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

            distance_icqBaseUnseen_map.put(distance, tra_map);
        }



        ArrayList<Integer> distances = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

        for (int distanceThreshold: distances) {
            resultF += distanceThreshold;
            resultRecall += distanceThreshold;
            resultPrecision += distanceThreshold;

            System.out.println("distanceThreshold: " + distanceThreshold);
            ArrayList<Double> precisionList1 = new ArrayList<>();
            ArrayList<Double> recallList1 = new ArrayList<>();
            ArrayList<Double> FList1 = new ArrayList<>();

            ArrayList<Double> precisionList2 = new ArrayList<>();
            ArrayList<Double> recallList2 = new ArrayList<>();
            ArrayList<Double> FList2 = new ArrayList<>();

            ArrayList<Double> precisionList3 = new ArrayList<>();
            ArrayList<Double> recallList3 = new ArrayList<>();
            ArrayList<Double> FList3 = new ArrayList<>();

            ArrayList<Double> precisionList4 = new ArrayList<>();
            ArrayList<Double> recallList4 = new ArrayList<>();
            ArrayList<Double> FList4 = new ArrayList<>();



            HashMap<Integer, HashMap<Integer, Double>> tra_icq_map = distance_icq_map.get(distanceThreshold);
            HashMap<Integer, HashMap<Integer, Double>> tra_icqBase_map = distance_icqBase_map.get(distanceThreshold);
            HashMap<Integer, HashMap<Integer, Double>> tra_true_map = distance_true_map.get(distanceThreshold);
            HashMap<Integer, HashMap<Integer, Double>> tra_icqBaseNew_map = distance_icqBaseNew_map.get(distanceThreshold);
            HashMap<Integer, HashMap<Integer, Double>> tra_icqBaseUnseen_map = distance_icqBaseUnseen_map.get(distanceThreshold);

            System.out.println("tra_icq_map size: " + tra_icq_map.size());
            System.out.println("tra_icq_base_map size: " + tra_icqBase_map.size());
            System.out.println("tra_true_map size: " + tra_true_map.size());
            System.out.println("tra_icq_base_new_map size: " + tra_icqBaseNew_map.size());
            System.out.println("tra_icq_base_unseen_map size: " + tra_icqBaseUnseen_map.size());

            ArrayList<Integer> trajectorys = new ArrayList<>(Arrays.asList(4, 7, 90, 23, 34, 87, 99, 1, 67, 29, 5, 31, 47, 39, 32, 52, 96, 22, 91, 63));

            for (int i = 0; i < trajectorys.size(); i++) {
                System.out.println("traId: " + i);

                HashMap<Integer, Double> icq_map = tra_icq_map.get(i);
                HashMap<Integer, Double> icqBase_map = tra_icqBase_map.get(i);
                HashMap<Integer, Double> true_map = tra_true_map.get(i);
                HashMap<Integer, Double> icqBaseNew_map = tra_icqBaseNew_map.get(i);
                HashMap<Integer, Double> icqBaseUnseen_map = tra_icqBaseUnseen_map.get(i);

                System.out.println("icq_map size: " + icq_map.size());
                System.out.println("icq_base_map size: " + icqBase_map.size());
                System.out.println("true_map size: " + true_map.size());
                System.out.println("icq_base_new_map size: " + icqBaseNew_map.size());
                System.out.println("icq_base_unseen_map size: " + icqBaseUnseen_map.size());


                Set<Integer> icq_list = icq_map.keySet();
                Set<Integer> icqBase_list = icqBase_map.keySet();
                Set<Integer> true_list = true_map.keySet();
                Set<Integer> icqBaseNew_list = icqBaseNew_map.keySet();
                Set<Integer> icqBaseUnseen_list = icqBaseUnseen_map.keySet();


                int num1 = 0;
//                double error1 = 0;
                double F1 = 0;
                int num2 = 0;
//                double error2 = 0;
                double F2 = 0;
                int num3 = 0;
                double F3 = 0;
                int num4 = 0;
                double F4 = 0;

                for (int true_parId: true_list) {
                    for (int icq_parId: icq_list) {
                        if (icq_parId == true_parId) {
                            num1++;
//                            System.out.println("icq error: " + Math.abs(true_map.get(true_parId) - icq_map.get(icq_parId)));
//                            error1 += Math.abs(true_map.get(true_parId) - icq_map.get(icq_parId));

                        }
                    }
                    for (int icq_parId: icqBase_list) {
                        if (icq_parId == true_parId) {
                            num2++;
//                            System.out.println("icq_base error: " + Math.abs(true_map.get(true_parId) - icqBase_map.get(icq_parId)));
//                            error2 += Math.abs(true_map.get(true_parId) - icqBase_map.get(icq_parId));

                        }
                    }
                    for (int icq_parId: icqBaseNew_list) {
                        if (icq_parId == true_parId) {
                            num3++;
//                            System.out.println("icq_base error: " + Math.abs(true_map.get(true_parId) - icqBase_map.get(icq_parId)));
//                            error2 += Math.abs(true_map.get(true_parId) - icqBase_map.get(icq_parId));

                        }
                    }
                    for (int icq_parId: icqBaseUnseen_list) {
                        if (icq_parId == true_parId) {
                            num4++;
//                            System.out.println("icq_base error: " + Math.abs(true_map.get(true_parId) - icqBase_map.get(icq_parId)));
//                            error2 += Math.abs(true_map.get(true_parId) - icqBase_map.get(icq_parId));

                        }
                    }
                }

                double precision1;
                double recall1;
                double precision2;
                double recall2;
                double precision3;
                double recall3;
                double precision4;
                double recall4;


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

                if (icqBase_list.size() == 0) {
                    precision2 = 1;
                }
                else {
                    precision2 = (double)num2 / (double)icqBase_list.size();
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

                if (icqBaseNew_list.size() == 0) {
                    precision3 = 1;
                }
                else {
                    precision3 = (double)num3 / (double)icqBaseNew_list.size();
                }

                if (true_list.size() == 0) {
                    recall3 = 1;

                }
                else {
                    recall3 = (double)num3 / (double)true_list.size();

                }

                F3 = 2 * (precision3 * recall3) / (precision3 + recall3);


                System.out.println("precision3: " + precision3 + " recall3: " + recall3);

                precisionList3.add(precision3);
                recallList3.add(recall3);
                FList3.add(F3);



                if (icqBaseUnseen_list.size() == 0) {
                    precision4 = 1;
                }
                else {
                    precision4 = (double)num4 / (double)icqBaseUnseen_list.size();
                }

                if (true_list.size() == 0) {
                    recall4 = 1;

                }
                else {
                    recall4 = (double)num4 / (double)true_list.size();

                }


                F4 = 2 * (precision4 * recall4) / (precision4 + recall4);


                System.out.println("precision4: " + precision4 + " recall4: " + recall4);

                precisionList4.add(precision4);
                recallList4.add(recall4);
                FList4.add(F4);


            }

//            System.out.println("precisionList1: " + precisionList1);
//            System.out.println("recallList1: " + recallList1);
            ArrayList<ArrayList<Double>> precisionLists = new ArrayList<>();
            ArrayList<ArrayList<Double>> recallLists = new ArrayList<>();
            ArrayList<ArrayList<Double>> FLists = new ArrayList<>();

            precisionLists.add(precisionList1);
            precisionLists.add(precisionList2);
            precisionLists.add(precisionList3);
            precisionLists.add(precisionList4);

            recallLists.add(recallList1);
            recallLists.add(recallList2);
            recallLists.add(recallList3);
            recallLists.add(recallList4);

            FLists.add(FList1);
            FLists.add(FList2);
            FLists.add(FList3);
            FLists.add(FList4);


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

