package test;

import algorithm.PrintOut;
import datagenerate.HSMDataGenRead;
import iDModel.GenTopology;
import indoor_entitity.Point;
import utilities.DataGenConstant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class HSM_dataset {
    public static void main(String arg[]) throws IOException {
        PrintOut printOut = new PrintOut();
        DataGenConstant.init("hsm");
        HSMDataGenRead dateGenRead = new HSMDataGenRead();
        dateGenRead.dataGen("hsm");

        GenTopology genTopology = new GenTopology();
        genTopology.genTopology();

//        dateGenRead.saveDP();



//        String result = "";
//        System.out.println("door-------------------");
//        for (int i = 0; i < IndoorSpace.iDoors.size(); i++) {
////            if (IndoorSpace.iDoors.get(i).getmPartitions().size() != 2) {
////                System.out.println(IndoorSpace.iDoors.get(i).toString());
////                result += IndoorSpace.iDoors.get(i).getmID() + ", ";
////            }
//            System.out.println(IndoorSpace.iDoors.get(i));
//
//        }
//        System.out.println(result);
//
//
//        System.out.println("partition-------------------");
//        for (int i = 0; i < IndoorSpace.iPartitions.size(); i++) {
//            System.out.println(IndoorSpace.iPartitions.get(i).toString());
//        }
        getPointPairs(1100);

    }

    public static ArrayList<ArrayList<Point>> getPointPairs(double distance) {
        ArrayList<ArrayList<Point>> pointPairs = new ArrayList<>();
        ArrayList<String> point1s = new ArrayList<>();
        ArrayList<String> point2s = new ArrayList<>();
        if ((int) distance == (int) 1100) {
            point1s.addAll(new ArrayList<>(Arrays.asList("840.0,1369.0,1", "1146.0,1474.0,6", "1367.0,787.0,3", "951.0,764.0,2", "595.0,1847.0,5", "888.0,778.0,2", "1252.0,680.0,6", "514.0,1842.0,1", "1683.0,1289.0,4", "712.0,1526.0,5")));
            point2s.addAll(new ArrayList<>(Arrays.asList("816.0,1727.0,6", "1168.0,914.0,4", "1114.0,1652.0,3", "1407.0,1249.0,2", "715.0,1672.0,6", "1447.0,883.0,2", "616.0,1246.0,5", "1179.0,1545.0,2", "1087.0,1853.0,1", "1375.0,1870.0,5")));

        }
        if ((int) distance == (int) 1300) {
            point1s.addAll(new ArrayList<>(Arrays.asList("840.0,1369.0,1", "1146.0,1474.0,6", "1367.0,787.0,3", "951.0,764.0,2", "595.0,1847.0,5", "888.0,778.0,2", "1252.0,680.0,6", "514.0,1842.0,1", "1683.0,1289.0,4", "712.0,1526.0,5")));
            point2s.addAll(new ArrayList<>(Arrays.asList("953.0,865.0,2", "1426.0,888.0,0", "1030.0,1567.0,5", "1575.0,1195.0,2", "1636.0,1515.0,1", "1302.0,1029.0,0", "1514.0,1655.0,2", "814.0,1589.0,6", "1640.0,1413.0,2", "1339.0,1430.0,2")));

        }
        if ((int) distance == (int) 1500) {
            point1s.addAll(new ArrayList<>(Arrays.asList("840.0,1369.0,1", "1146.0,1474.0,6", "1367.0,787.0,3", "951.0,764.0,2", "595.0,1847.0,5", "888.0,778.0,2", "1252.0,680.0,6", "514.0,1842.0,1", "1683.0,1289.0,4", "712.0,1526.0,5")));
            point2s.addAll(new ArrayList<>(Arrays.asList("645.0,1386.0,4", "879.0,810.0,5", "896.0,1743.0,1", "1121.0,1045.0,1", "1305.0,1044.0,0", "1179.0,1844.0,2", "1460.0,1732.0,0", "1309.0,1103.0,6", "568.0,1715.0,3", "621.0,1545.0,3")));

        }
        if ((int) distance == (int) 1700) {
            point1s.addAll(new ArrayList<>(Arrays.asList("840.0,1369.0,1", "1146.0,1474.0,6", "1367.0,787.0,3", "951.0,764.0,2", "595.0,1847.0,5", "888.0,778.0,2", "1252.0,680.0,6", "514.0,1842.0,1", "1683.0,1289.0,4", "712.0,1526.0,5")));
            point2s.addAll(new ArrayList<>(Arrays.asList("612.0,750.0,2", "635.0,766.0,2", "1634.0,1818.0,2", "912.0,1241.0,1", "814.0,927.0,3", "1180.0,1639.0,1", "1646.0,1794.0,0", "1047.0,953.0,3", "580.0,864.0,3", "621.0,1141.0,3")));

        }
        if ((int) distance == (int) 1900) {
            point1s.addAll(new ArrayList<>(Arrays.asList("840.0,1369.0,1", "1146.0,1474.0,6", "1367.0,787.0,3", "951.0,764.0,2", "595.0,1847.0,5", "888.0,778.0,2", "1252.0,680.0,6", "514.0,1842.0,1", "1683.0,1289.0,4", "712.0,1526.0,5")));
            point2s.addAll(new ArrayList<>(Arrays.asList("904.0,712.0,4", "902.0,652.0,3", "1765.0,1867.0,3", "1177.0,2046.0,4", "686.0,746.0,3", "690.0,1421.0,1", "1761.0,1750.0,0", "614.0,879.0,3", "565.0,1152.0,1", "674.0,1107.0,4")));

        }

        for (int i = 0; i < point1s.size(); i++) {
            String point1Str = point1s.get(i);
            String point2Str = point2s.get(i);
            String[] point1Arr = point1Str.split(",");
            String[] point2Arr = point2Str.split(",");
            Point point1 = new Point(Double.parseDouble(point1Arr[0]), Double.parseDouble(point1Arr[1]), Integer.parseInt(point1Arr[2]));
            Point point2 = new Point(Double.parseDouble(point2Arr[0]), Double.parseDouble(point2Arr[1]), Integer.parseInt(point2Arr[2]));
            pointPairs.add(new ArrayList<>(Arrays.asList(point1, point2)));
        }
        return pointPairs;
    }
}
