package test;

import algorithm.PrintOut;
import datagenerate.HSMDataGenRead;
import datagenerate.HSMTrajectoryGen;
import iDModel.GenTopology;
import indoor_entitity.IndoorSpace;
import indoor_entitity.Trajectory;
import utilities.DataGenConstant;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class Test {
    public static String output = System.getProperty("user.dir") + "/data_trajectory/random_uncertain_point.txt";

    public static void main(String arg[]) throws IOException {
//        for (int i = 0; i < 20; i++) {
//            int a = (int)(Math.random() * 3);
//            System.out.println(a);
//        }

//        for (int i = 0; i < 100; i++) {
//            int x = 0;
//            x = Possion.getPossionVariable(3);
//            System.out.println(x);
//        }

//        int a = 5;
//        int b = a;
//        a = 3;
//
//        System.out.println("a " + a + " b " + b);
//        Stamp [] stamps = new Stamp[5];
//        Stamp s1 = new Stamp(0, 12, 32);
//        Stamp s2 = new Stamp(1, 14, 22);
//        Stamp s3 = new Stamp(2, 10, 22);
//        Stamp s4 = new Stamp(3, 18, 13);
//        Stamp s5 = new Stamp(4, 11, 22);
//
//        stamps[0] = s1;
//        stamps[1] = s2;
//        stamps[2] = s3;
//        stamps[3] = s4;
//        stamps[4] = s5;
//
//        Stamp ss = new Stamp(4, 299, 455);
//        Stamp old = stamps[4];
//        old.pop = ss.pop;
//        old.D = ss.D;
//
//        System.out.println(stamps[4].doorId  + " " + stamps[4].pop + " " + stamps[4].D);
//        Test o= new Test();// 构造对象
//        System.out.print("Reallly?");
//        System.out.println("Yes");
//        System.out.println("So easy");

//        ArrayList<Integer> test1 = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
//        ArrayList<Integer> test2 = new ArrayList<>(Arrays.asList(8, 9, 10));
//        test2 = test1;
//
//        for (int i = 0; i < test2.size(); i++) {
//            System.out.println(test2);
//        }

//        HashMap<Integer, String> test = new HashMap<>();
//        test.put(1, "a");
//        test.put(2, "b");
//        test.put(3, "c");
//        System.out.println(test.get(1));
//        System.out.println(test.get(4));

//        ArrayList<Integer> list1 = new ArrayList<>();
//        list1.add(1);
//        list1.add(2);
//        list1.add(3);
//        list1.add(5);
//        list1.add(6);
//
//        ArrayList<Integer> list2 = new ArrayList<>();
//        list2.add(2);
//        list2.add(3);
//        list2.add(7);
//        list2.add(8);
//
//        // 交集
//        List<Integer> intersection = list1.stream().filter(item -> list2.contains(item)).collect(toList());
//        System.out.println("---交集 intersection---");
//        intersection.parallelStream().forEach(System.out :: println);


//        ArrayList<Integer> list1 = new ArrayList<>();
//        System.out.println(list1.get(list1.size() - 1));

//        int [] isVisited = new int [10];
//        System.out.println(isVisited[0]);
//        isVisited[3] = 1;
//        System.out.println(isVisited[3]);

//        Set<String> result = new HashSet<String>();
//        Set<String> set1 = new HashSet<String>() {
//            {
//                add("王者荣耀");
//                add("英雄联盟");
//                add("穿越火线");
//                add("地下城与勇士");
//            }
//        };
//
//        Set<String> set2 = new HashSet<String>() {
//            {
//                add("王者荣耀");
//                add("地下城与勇士");
//                add("魔兽世界");
//            }
//        };
//
//        result.clear();
//        result.addAll(set1);
//        result.retainAll(set2);
//        System.out.println("交集：" + result);

//        ArrayList<Double> list = new ArrayList<>();
//        list.add(1.0);
//        list.add(3.0);
//        list.add(5.0);
//        list.add(7.0);
//        list.add(9.0);
//        System.out.println(list);
//        ArrayList<Double> result = addDist(list, 6.0);
//        System.out.println(result);
//        result = addDist(list, 11.0);
//        System.out.println(result);
//        result = addDist(list, 0.0);
//        System.out.println(result);
//        result = addDist(list, 4.0);
//        System.out.println(result);
//        result = addDist(list, 12.0);
//        System.out.println(result);

//        HashMap<Integer, Integer> map = new HashMap<>();
//        map.put(1, 22);
//        System.out.println(map.get(1));
//        map.put(2, 33);
//        map.put(1, 55);
//        System.out.println(map.get(1));

//        double a = 1.707533638412677E-5;
//        double b = 1.707533638412677E-5;
//        double c = a + b;
//        System.out.println(a + ", " + b + ", " + c);

//        Set<Integer> test_set = new HashSet<>();
//        ArrayList<Integer> test_list = new ArrayList<>();
//        test_list.add(1);
//        test_list.add(2);
//        test_set.addAll(test_list);
//        System.out.println(test_set);
//        ArrayList<Integer> test = new ArrayList<>();
//        test.add(1);
//        test.add(0);
//        test.remove(1);
//        System.out.println(test);

        PrintOut printOut = new PrintOut();
//        DataGen dataGen = new DataGen();
//        dataGen.genAllData(DataGenConstant.dataType, DataGenConstant.divisionType);
//        HSMDataGenRead hsmDataGenRead = new HSMDataGenRead();
//        hsmDataGenRead.dataGen("newHsm");
//
//        GenTopology genTopology = new GenTopology();
//        genTopology.genTopology();
//        HSMTrajectoryGen.trajectoryGen_read(HSMTrajectoryGen.trajectoryPoints, HSMTrajectoryGen.trajectorys, "HSM");
//
//        ArrayList<Integer> tra_6 = new ArrayList<>();
//        ArrayList<Integer> tra_12 = new ArrayList<>();
//
//        for (Trajectory tra: IndoorSpace.iTrajectory) {
//            if (tra.getMinTime() < 3600 * 6) {
//                tra_6.add(tra.getTrajectoryId());
//            }
//            if (tra.getMinTime() < 3600 * 12) {
//                tra_12.add(tra.getTrajectoryId());
//            }
//        }
//        System.out.println("tra_6: " + tra_6);
//        System.out.println("tra_12: " + tra_12);
//        ArrayList<Integer> idList = new ArrayList<>();
//
//        while (idList.size() < 700000) {
//
//            int id = (int)(Math.random() * 7000000);
//            if (!idList.contains(id)) {
//                idList.add(id);
//            }
//        }
//        String results = "";
//        for (int i = 0; i < idList.size(); i++) {
//            results += idList.get(i) + "\t";
//        }
//
//        try {
//            FileWriter fw = new FileWriter(output);
//            fw.write(results);
//            fw.flush();
//            fw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return;
//        }
        int n = 0;
        while (n < 10000) {
            System.out.println(Math.random());
            n++;
        }




    }
    public static ArrayList<Double> addDist(ArrayList<Double> list, double dist) {
        int initSize = list.size();
        for (int i = list.size() - 1; i >= 0; i--) {
            if (i == list.size() -1 && dist > list.get(i)) {
                list.add(dist);
                break;
            }
            else if (dist > list.get(i)) {
                list.add(list.get(list.size() - 1));
                for (int j = list.size() - 1; j > i + 1; j--){
                    list.set(j, list.get(j - 1));
                }
                list.set(i + 1, dist);
                break;
            }
        }
        if (list.size() == initSize) {
            list.add(list.get(list.size() - 1));
            for (int j = list.size() - 1; j > 0; j--){
                list.set(j, list.get(j - 1));
            }
            list.set(0, dist);
        }
        return list;
    }
//    public Test(){
//        try {
//
//            PrintStream print=new PrintStream(System.getProperty("user.dir") + "/printInfo/text.txt");  //写好输出位置文件；
//            System.setOut(print);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

}

