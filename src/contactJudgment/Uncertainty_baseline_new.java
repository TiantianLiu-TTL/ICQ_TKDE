package contactJudgment;

import algorithm.BinaryHeap;
import indoor_entitity.*;
import utilities.Constant;
import utilities.DataGenConstant;
import utilities.RoomType;

import java.util.*;

public class Uncertainty_baseline_new {
    public static int preTraPointNum = -1;
    public static HashMap<Integer, HashMap<Integer, ArrayList<TrajectoryPoint>>> preTrajectory = new HashMap<Integer, HashMap<Integer, ArrayList<TrajectoryPoint>>>();
    public static HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> par_tra = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();

    public static void findUncertainTra_forBaselineNew(Trajectory trajectory, int t){
        int t1 = -1, t2 = -1;
        int time = t;
        if (preTrajectory.get(trajectory.getTrajectoryId()) != null && preTrajectory.get(trajectory.getTrajectoryId()).get(t) != null)
            return;
        boolean isFound = false;
        while (!isFound) {
            time -= DataGenConstant.sampleInterval;
            if (trajectory.getTime().contains(time)) {
                isFound = true;
                t1 = time;
            }
        }
        time = t;
        isFound = false;
        while (!isFound) {
            time += DataGenConstant.sampleInterval;
            if (trajectory.getTime().contains(time)) {
                isFound = true;
                t2 = time;
            }
        }
//        System.out.println("t1: " + t1 + ", t2: " + t2);
        if (t1 == -1 || t2 == -1) {
            System.out.println("something wrong with the time: Uncertainty_baseline_new. findUncertainTra()");
        }

//        boolean isStatic = true;
////        int temp_time1 = t1;
////        int temp_time2 = t2;
//
//        if (!trajectory.getTime().contains(t1) || !trajectory.getTime().contains(t2)) {
//            isStatic = false;
//        }
//        if (trajectory.getTrajectory(t1).size() != 1 || trajectory.getTrajectory(t2).size() != 1) {
//            isStatic = false;
//        }
//        TrajectoryPoint temp_traPoint1 = IndoorSpace.iTraPoint.get(trajectory.getTrajectory(t1).get(0));
//        TrajectoryPoint temp_traPoint2 = IndoorSpace.iTraPoint.get(trajectory.getTrajectory(t2).get(0));
//
//        if (temp_traPoint1.getX() != temp_traPoint2.getX() || temp_traPoint1.getY() != temp_traPoint2.getY() || temp_traPoint1.getmFloor() != temp_traPoint2.getmFloor()) {
//            isStatic = false;
//        }
//
//        if (isStatic) {
////            System.out.println("is static....");
//            TrajectoryPoint traPoint_pre = IndoorSpace.iTraPoint.get(trajectory.getTrajectory(t1).get(0));
//            TrajectoryPoint traPointPre = new TrajectoryPoint(traPoint_pre, t, preTraPointNum);
//
//            traPointPre.setParID(traPoint_pre.getParID());
//            traPointPre.setTrajectoryID(trajectory.getTrajectoryId());
//            traPointPre.setProbability(1);
//
//            HashMap<Integer, ArrayList<TrajectoryPoint>> temp_map = new HashMap<Integer, ArrayList<TrajectoryPoint>>();
//            if (preTrajectory.get(trajectory.getTrajectoryId())!= null) {
//                temp_map = preTrajectory.get(trajectory.getTrajectoryId());
//            }
//            temp_map.put(t, new ArrayList<TrajectoryPoint>(Arrays.asList(traPointPre)));
//
//            preTrajectory.put(trajectory.getTrajectoryId(), temp_map);
//
//            HashMap<Integer, ArrayList<Integer>> temp_tra = new HashMap<Integer, ArrayList<Integer>>();
//            if (par_tra.get(traPoint_pre.getParID()) != null) {
//                temp_tra = par_tra.get(traPoint_pre.getParID());
//            }
//            temp_tra.put(t, new ArrayList<Integer>(Arrays.asList(trajectory.getTrajectoryId())));
//            par_tra.put(traPoint_pre.getParID(), temp_tra);
//
//            return;
//        }

        double distance1 = (t - t1) * DataGenConstant.traveling_speed_max;
        double distance2 = (t2 - t) * DataGenConstant.traveling_speed_max;
//        System.out.println("distance1: " + distance1 + ", distance2: " + distance2);

        int traPointId1 = -1;
        if (trajectory.getTrajectory(t1) != null && trajectory.getTrajectory(t1).size() == 1) {
            traPointId1 = trajectory.getTrajectory(t1).get(0);
        }
        else {
            System.out.println(trajectory.getTrajectory(t1));
            System.out.println("something wrong with trajectory point: Uncertainty_baseline_new.findUncertainTra_0");
        }
        TrajectoryPoint traPoint1 = IndoorSpace.iTraPoint.get(traPointId1);

        int traPointId2 = -1;
        if (trajectory.getTrajectory(t2) != null && trajectory.getTrajectory(t2).size() == 1) {
            traPointId2 = trajectory.getTrajectory(t2).get(0);
        }
        else {
            System.out.println(trajectory.getTrajectory(t2));
            System.out.println("something wrong with trajectory point: Uncertainty_baseline_new.findUncertainTra_1");
        }
        TrajectoryPoint traPoint2 = IndoorSpace.iTraPoint.get(traPointId2);

        double traPoint1_uncertain_x1 = Math.max(0, traPoint1.getX() - distance1);
        double traPoint1_uncertain_x2 = Math.min(traPoint1.getX() + distance1, DataGenConstant.floorRangeX);
        double traPoint1_uncertain_y1 = Math.max(0, traPoint1.getY() - distance1);
        double traPoint1_uncertain_y2 = Math.min(traPoint1.getY() + distance1, DataGenConstant.floorRangeY);

        double traPoint2_uncertain_x1 = Math.max(0, traPoint2.getX() - distance2);
        double traPoint2_uncertain_x2 = Math.min(traPoint2.getX() + distance2, DataGenConstant.floorRangeX);
        double traPoint2_uncertain_y1 = Math.max(0, traPoint2.getY() - distance2);
        double traPoint2_uncertain_y2 = Math.min(traPoint2.getY() + distance2, DataGenConstant.floorRangeY);

        double common_uncertain_x1 = Math.max(traPoint1_uncertain_x1, traPoint2_uncertain_x1);
        double common_uncertain_x2 = Math.min(traPoint1_uncertain_x2, traPoint2_uncertain_x2);
        double common_uncertain_y1 = Math.max(traPoint1_uncertain_y1, traPoint2_uncertain_y1);
        double common_uncertain_y2 = Math.min(traPoint1_uncertain_y2, traPoint2_uncertain_y2);

        int traPoint1_uncertain_floorNum = (int)(distance1 / DataGenConstant.lenStairway);
        int traPoint2_uncertain_floorNum = (int)(distance2 / DataGenConstant.lenStairway);

        int traPoint1_uncertain_floor1 = Math.max(0, traPoint1.getmFloor() - traPoint1_uncertain_floorNum);
        int traPoint1_uncertain_floor2 = Math.min(traPoint1.getmFloor() + traPoint1_uncertain_floorNum, DataGenConstant.nFloor - 1);
        int traPoint2_uncertain_floor1 = Math.max(0, traPoint2.getmFloor() - traPoint2_uncertain_floorNum);
        int traPoint2_uncertain_floor2 = Math.min(traPoint2.getmFloor() + traPoint2_uncertain_floorNum, DataGenConstant.nFloor - 1);

//        System.out.println("traPoint1 floor: " + traPoint1.getmFloor() + ", traPoint2 floor: " + traPoint2.getmFloor());
//        System.out.println("traPoint1_uncertain_floor1: " + traPoint1_uncertain_floor1 + ", traPoint1_uncertain_floor2: " + traPoint1_uncertain_floor2);
//        System.out.println("traPoint2_uncertain_floor1: " + traPoint2_uncertain_floor1 + ", traPoint2_uncertain_floor2: " + traPoint2_uncertain_floor2);


        int common_uncertain_floor1 = Math.max(traPoint1_uncertain_floor1, traPoint2_uncertain_floor1);
        int common_uncertain_floor2 = Math.min(traPoint1_uncertain_floor2, traPoint2_uncertain_floor2);


        if (common_uncertain_x1 > common_uncertain_x2 || common_uncertain_y1 > common_uncertain_y2 || common_uncertain_floor1 > common_uncertain_floor2) {
            System.out.println("something wrong with trajectory point: Uncertainty_baseline_new.findUncertainTra_2");
        }

//        System.out.println("common_uncertain_x1: " + common_uncertain_x1 + "common_uncertain_x2: " + common_uncertain_x2);
//        System.out.println("common_uncertain_y1: " + common_uncertain_y1 + "common_uncertain_y2: " + common_uncertain_y2);
//        System.out.println("common_uncertain_floor1: " + common_uncertain_floor1 + "common_uncertain_floor2: " + common_uncertain_floor2);
//
//        System.out.println("traPoint1_x: " + traPoint1.getX() + ", traPoint1_y: " + traPoint1.getY());
//        System.out.println("traPoint2_x: " + traPoint2.getX() + ", traPoint2_y: " + traPoint2.getY());

        double temp_x = common_uncertain_x1;
        double temp_y = common_uncertain_y1;
        int temp_floor = common_uncertain_floor1;
        ArrayList<TrajectoryPoint> temp_list = new ArrayList<TrajectoryPoint>();
        HashMap<Integer, ArrayList<TrajectoryPoint>> temp_map = new HashMap<Integer, ArrayList<TrajectoryPoint>>();
        if (preTrajectory.get(trajectory.getTrajectoryId())!= null) {
            temp_map = preTrajectory.get(trajectory.getTrajectoryId());
        }

        double number = 0;
        ArrayList<TrajectoryPoint> tras = new ArrayList<TrajectoryPoint>();
        while (temp_floor <= common_uncertain_floor2) {
            temp_x = common_uncertain_x1;
            while (temp_x <= common_uncertain_x2) {
                temp_y = common_uncertain_y1;
                while (temp_y <= common_uncertain_y2) {
                    TrajectoryPoint traPointPre = new TrajectoryPoint(temp_x, temp_y, temp_floor, t, preTraPointNum);
//                    System.out.println("traPointPreId: " + traPointPre.getTraPointId());
                    int parId = getHostPartition(new Point(temp_x, temp_y, temp_floor));
                    if (parId == -1) {
                        temp_y += DataGenConstant.sample_dist;
                        continue;
//                        System.out.println("parId: " + parId);
//                        System.out.println("x: " + temp_x + ", y: " + temp_y + ", floor: " + temp_floor);
                    }
                    double temp_distance1 = traPoint1.eDist(new Point(temp_x, temp_y, temp_floor));
                    double temp_distance2 = traPoint2.eDist(new Point(temp_x, temp_y, temp_floor));
//                    System.out.println("temp_distance1: " + temp_distance1 + ", temp_distance2: " + temp_distance2);

                    if (temp_distance1 > distance1 || temp_distance2 > distance2) {
                        temp_y += DataGenConstant.sample_dist;
                        continue;
                    }
//                    System.out.println("x: " + temp_x + ", y: " + temp_y + ", floor: " + temp_floor);

                    traPointPre.setParID(parId);
                    //                        traPointPre.setProbability(1 / number);
                    number++;
                    traPointPre.setTrajectoryID(trajectory.getTrajectoryId());

                    HashMap<Integer, ArrayList<Integer>> temp_tra = new HashMap<Integer, ArrayList<Integer>>();
                    if (par_tra.get(parId) != null) {
                        temp_tra = par_tra.get(parId);
                        if (temp_tra.get(t) != null) {
                            ArrayList<Integer> temp_tra_list = temp_tra.get(t);
                            if (!temp_tra_list.contains(trajectory.getTrajectoryId())) {
                                temp_tra_list.add(trajectory.getTrajectoryId());
                                temp_tra.put(t, temp_tra_list);
                            }
                        } else {
                            temp_tra.put(t, new ArrayList<Integer>(Arrays.asList(trajectory.getTrajectoryId())));
                        }
                        par_tra.put(parId, temp_tra);
                    } else {
                        temp_tra.put(t, new ArrayList<Integer>(Arrays.asList(trajectory.getTrajectoryId())));
                        par_tra.put(parId, temp_tra);
                    }


                    temp_list.add(traPointPre);
                    tras.add(traPointPre);
                    temp_y += DataGenConstant.sample_dist;
                }
                temp_x += DataGenConstant.sample_dist;
            }
            temp_floor += 1;
        }

        for (TrajectoryPoint tra: tras) {
            tra.setProbability(1 / number);
        }

        temp_map.put(t, temp_list);
        preTrajectory.put(trajectory.getTrajectoryId(), temp_map);

        if (preTrajectory.get(trajectory.getTrajectoryId()) == null || preTrajectory.get(trajectory.getTrajectoryId()).get(t) == null) {
//            System.out.println("is empty....");
            HashMap<Integer, ArrayList<TrajectoryPoint>> temp_map1 = new HashMap<Integer, ArrayList<TrajectoryPoint>>();
            if (preTrajectory.get(trajectory.getTrajectoryId())!= null) {
                temp_map1 = preTrajectory.get(trajectory.getTrajectoryId());
            }
            ArrayList<TrajectoryPoint> list = new ArrayList<TrajectoryPoint>();
            TrajectoryPoint traPointPre = new TrajectoryPoint(traPoint1, t, preTraPointNum);

            traPointPre.setParID(traPoint1.getParID());
            traPointPre.setTrajectoryID(trajectory.getTrajectoryId());
            traPointPre.setProbability(0.5);

            list.add(traPointPre);

            HashMap<Integer, ArrayList<Integer>> temp_tra = new HashMap<Integer, ArrayList<Integer>>();
            if (par_tra.get(traPoint1.getParID()) != null) {
                temp_tra = par_tra.get(traPoint1.getParID());
                if (temp_tra.get(t) != null) {
                    ArrayList<Integer> temp_tra_list = temp_tra.get(t);
                    if (!temp_tra_list.contains(trajectory.getTrajectoryId())) {
                        temp_tra_list.add(trajectory.getTrajectoryId());
                        temp_tra.put(t, temp_tra_list);
                    }
                }
                else {
                    temp_tra.put(t, new ArrayList<Integer>(Arrays.asList(trajectory.getTrajectoryId())));
                }
                par_tra.put(traPoint1.getParID(), temp_tra);
            }
            else {
                temp_tra.put(t, new ArrayList<Integer>(Arrays.asList(trajectory.getTrajectoryId())));
                par_tra.put(traPoint1.getParID(), temp_tra);
            }
//


            TrajectoryPoint traPointPre1 = new TrajectoryPoint(traPoint2, t, preTraPointNum);

            traPointPre1.setParID(traPoint2.getParID());
            traPointPre1.setTrajectoryID(trajectory.getTrajectoryId());
            traPointPre1.setProbability(0.5);
            list.add(traPointPre1);

            HashMap<Integer, ArrayList<Integer>> temp_tra2 = new HashMap<Integer, ArrayList<Integer>>();
            if (par_tra.get(traPoint2.getParID()) != null) {
                temp_tra2 = par_tra.get(traPoint2.getParID());
                if (temp_tra2.get(t) != null) {
                    ArrayList<Integer> temp_tra_list = temp_tra2.get(t);
                    if (!temp_tra_list.contains(trajectory.getTrajectoryId())) {
                        temp_tra_list.add(trajectory.getTrajectoryId());
                        temp_tra2.put(t, temp_tra_list);
                    }
                }
                else {
                    temp_tra2.put(t, new ArrayList<Integer>(Arrays.asList(trajectory.getTrajectoryId())));
                }
                par_tra.put(traPoint2.getParID(), temp_tra2);
            }
            else {
                temp_tra2.put(t, new ArrayList<Integer>(Arrays.asList(trajectory.getTrajectoryId())));
                par_tra.put(traPoint2.getParID(), temp_tra2);
            }

            temp_map1.put(t, list);

            preTrajectory.put(trajectory.getTrajectoryId(), temp_map1);
            return;
        }

//        HashMap<Integer, ArrayList<Double>> uncertainPars1 = findPartition(traPoint1, distance1);
//        HashMap<Integer, ArrayList<Double>> uncertainPars2 = findPartition(traPoint2, distance2);
//
//        Set<Integer> set1 = uncertainPars1.keySet();
//        Set<Integer> set2 = uncertainPars2.keySet();
////        System.out.println("set1: " + set1 + ", set2: " +set2);
//
//        Set<Integer> insertSet = new HashSet<>();
//        insertSet.clear();
//        insertSet.addAll(set1);
//        insertSet.retainAll(set2);
//
//
//        HashMap<Integer, ArrayList<TrajectoryPoint>> temp_map = new HashMap<>();
//        if (preTrajectory.get(trajectory.getTrajectoryId())!= null) {
//            temp_map = preTrajectory.get(trajectory.getTrajectoryId());
//        }
//        ArrayList<TrajectoryPoint> temp_list = new ArrayList<>();
//
//        double number = 0;
//        ArrayList<TrajectoryPoint> tras = new ArrayList<>();
//
//        for (int parId: insertSet) {
////            System.out.println("parId: " + parId);
//            Partition par = IndoorSpace.iPartitions.get(parId);
//            int pId1 = (int)(double)uncertainPars1.get(parId).get(0);
//            int pId2 = (int)(double)uncertainPars2.get(parId).get(0);
////            System.out.println("pId1: " + pId1 + "pId2: " + pId2);
//            Point p1 = traPoint1;
//            Point p2 = traPoint2;
//            if (pId1 != -1) {
//                p1 = IndoorSpace.iDoors.get(pId1);
//            }
//            if (pId2 != -1) {
//                p2 = IndoorSpace.iDoors.get(pId2);
//            }
//
////            System.out.println("p1: " + p1.getX() + ", " + p1.getY() + ", " + p1.getmFloor() + "; dist: " + (distance1 - uncertainPars1.get(parId).get(1)));
////            System.out.println("p2: " + p2.getX() + ", " + p2.getY() + ", " + p2.getmFloor() + "; dist: " + (distance2 - uncertainPars2.get(parId).get(1)));
//
//
//            double temp_dist = p1.eDist(p2);
//            if (p1.getmFloor() != p2.getmFloor()) {
//                temp_dist = DataGenConstant.lenStairway;
//            }
//            if (uncertainPars1.get(parId).get(1) + uncertainPars2.get(parId).get(1) + temp_dist > (distance1 + distance2)) {
////                System.out.println("continue");
//                continue;
//            }
//
//            trajectory.addPartition(t, par.getmID());
//            if (p1.getmFloor() != p2.getmFloor()) {
////                System.out.println("different floor....");
//
//                TrajectoryPoint traPointPre = new TrajectoryPoint(p1, t, preTraPointNum);
//
//                traPointPre.setParID(parId);
//                traPointPre.setTrajectoryID(trajectory.getTrajectoryId());
//                traPointPre.setProbability(1);
//
//                HashMap<Integer, ArrayList<TrajectoryPoint>> temp_map1 = new HashMap<>();
//                if (preTrajectory.get(trajectory.getTrajectoryId())!= null) {
//                    temp_map1 = preTrajectory.get(trajectory.getTrajectoryId());
//                }
//                temp_map1.put(t, new ArrayList<TrajectoryPoint>(Arrays.asList(traPointPre)));
//
//                preTrajectory.put(trajectory.getTrajectoryId(), temp_map1);
//
//                HashMap<Integer, ArrayList<Integer>> temp_tra = new HashMap<>();
//                if (par_tra.get(parId) != null) {
//                    temp_tra = par_tra.get(parId);
//                    if (temp_tra.get(t) != null) {
//                        ArrayList<Integer> temp_tra_list = temp_tra.get(t);
//                        if (!temp_tra_list.contains(trajectory.getTrajectoryId())) {
//                            temp_tra_list.add(trajectory.getTrajectoryId());
//                            temp_tra.put(t, temp_tra_list);
//                        }
//                    }
//                    else {
//                        temp_tra.put(t, new ArrayList<Integer>(Arrays.asList(trajectory.getTrajectoryId())));
//                    }
//                    par_tra.put(parId, temp_tra);
//                }
//                else {
//                    temp_tra.put(t, new ArrayList<Integer>(Arrays.asList(trajectory.getTrajectoryId())));
//                    par_tra.put(parId, temp_tra);
//                }
//
//                return;
//            }
//
//            trajectory.addPartition(t, par.getmID());
//
//            double x1 = par.getX1();
//            double x2 = par.getX2();
//            double y1 = par.getY1();
//            double y2 = par.getY2();
//            int floor = par.getmFloor();
//            Point point1 = new Point(x1, y1, floor);
//            Point point2 = new Point(x2, y2, floor);
//            Point point3 = new Point(x1, y2, floor);
//            Point point4 = new Point(x2, y1, floor);
//            if (x1 > x2 || y1 > y2) {
//                System.out.println("something wrong with partition");
//            }
//            if (p1.getX() > x2 || p1.getX() < x1 || p1.getY() > y2 || p1.getY() < y1) {
////                System.out.println("something wrong with p1..." + "parId: " + par.getmID() + ", p.x: " + p1.getX() + ", p.y: " + p1.getY());
//            }
//            if (p2.getX() > x2 || p2.getX() < x1 || p2.getY() > y2 || p2.getY() < y1) {
////                System.out.println("something wrong with p2..." + "parId: " + par.getmID() + ", p.x: " + p2.getX() + ", p.y: " + p2.getY());
//            }
//
//            double dist1_a = uncertainPars1.get(parId).get(1) + p1.eDist(point1);
//            double dist1_b = uncertainPars2.get(parId).get(1) + p2.eDist(point1);
//
//            double dist2_a = uncertainPars1.get(parId).get(1) + p1.eDist(point2);
//            double dist2_b = uncertainPars2.get(parId).get(1) + p2.eDist(point2);
//
//            double dist3_a = uncertainPars1.get(parId).get(1) + p1.eDist(point3);
//            double dist3_b = uncertainPars2.get(parId).get(1) + p2.eDist(point3);
//
//            double dist4_a = uncertainPars1.get(parId).get(1) + p1.eDist(point4);
//            double dist4_b = uncertainPars2.get(parId).get(1) + p2.eDist(point4);
//
//            if (dist1_a <= distance1 && dist1_b <= distance2 && dist2_a <= distance1 && dist2_b <= distance2 &&
//                    dist3_a <= distance1 && dist3_b <= distance2 && dist4_a <= distance1 && dist4_b <= distance2) {
//
////                System.out.println("sample1...");
//                number += ((x2 - x1)/DataGenConstant.sample_dist) * ((y2 - y1)/DataGenConstant.sample_dist);
//                double x = x1, y = y1;
//
//                while (x < x2) {
////                    System.out.println("while x < x2");
//                    x += DataGenConstant.sample_dist;
//                    y = y1;
//                    while (y < y2) {
////                        System.out.println("while x < x2");
//                        y += DataGenConstant.sample_dist;
//                        TrajectoryPoint traPointPre = new TrajectoryPoint(x, y, floor, t, preTraPointNum);
//
//                        traPointPre.setParID(parId);
////                        traPointPre.setProbability(1 / number);
//                        number++;
//                        traPointPre.setTrajectoryID(trajectory.getTrajectoryId());
//
//                        HashMap<Integer, ArrayList<Integer>> temp_tra = new HashMap<>();
//                        if (par_tra.get(parId) != null) {
//                            temp_tra = par_tra.get(parId);
//                            if (temp_tra.get(t) != null) {
//                                ArrayList<Integer> temp_tra_list = temp_tra.get(t);
//                                if (!temp_tra_list.contains(trajectory.getTrajectoryId())) {
//                                    temp_tra_list.add(trajectory.getTrajectoryId());
//                                    temp_tra.put(t, temp_tra_list);
//                                }
//                            }
//                            else {
//                                temp_tra.put(t, new ArrayList<Integer>(Arrays.asList(trajectory.getTrajectoryId())));
//                            }
//                            par_tra.put(parId, temp_tra);
//                        }
//                        else {
//                            temp_tra.put(t, new ArrayList<Integer>(Arrays.asList(trajectory.getTrajectoryId())));
//                            par_tra.put(parId, temp_tra);
//                        }
//
//
//                        temp_list.add(traPointPre);
//                        tras.add(traPointPre);
//
//                    }
//                }
//
//            }
//            else {
////                System.out.println("sample2...");
////                double number = 0;
////                ArrayList<TrajectoryPoint> tras = new ArrayList<>();
//
//                double p1RangeX1 = Math.max(p1.getX() - (distance1 - uncertainPars1.get(parId).get(1)), x1);
//                double p1RangeX2 = Math.min(p1.getX() + (distance1 - uncertainPars1.get(parId).get(1)), x2);
//                double p1RangeY1 = Math.max(p1.getY() - (distance1 - uncertainPars1.get(parId).get(1)), y1);
//                double p1RangeY2 = Math.min(p1.getY() + (distance1 - uncertainPars1.get(parId).get(1)), y2);
//
//                double p2RangeX1 = Math.max(p2.getX() - (distance2 - uncertainPars2.get(parId).get(1)), x1);
//                double p2RangeX2 = Math.min(p2.getX() + (distance2 - uncertainPars2.get(parId).get(1)), x2);
//                double p2RangeY1 = Math.max(p2.getY() - (distance2 - uncertainPars2.get(parId).get(1)), y1);
//                double p2RangeY2 = Math.min(p2.getY() + (distance2 - uncertainPars2.get(parId).get(1)), y2);
//
////                System.out.println("x1: " + x1 + ", x2: " + x2 + ", y1: " + y1 + ", y2: " + y2);
////                System.out.println("p1RangeX1: " + p1RangeX1 + ", p1RangeX2: " + p1RangeX2 + ", p1RangeY1" + p1RangeY1 + ", p1RangeY2" + p1RangeY2);
////                System.out.println("p2RangeX1: " + p2RangeX1 + ", p2RangeX2: " + p2RangeX2 + ", p2RangeY1" + p2RangeY1 + ", p2RangeY2" + p2RangeY2);
//
//
//
//                double x = Math.max(p1RangeX1, p2RangeX1), y;
//                while (x < Math.min(p1RangeX2, p2RangeX2)) {
////                    System.out.println("while x < x2");
//                    x += DataGenConstant.sample_dist;
//                    y = Math.max(p1RangeY1, p2RangeY1);
//                    while (y < Math.min(p1RangeY2, p2RangeY2)) {
////                        System.out.println("while y < y2");
//                        y += DataGenConstant.sample_dist;
//                        Point point = new Point(x, y, floor);
////                        System.out.println("p1-p2: " + p1.eDist(p2));
////                        System.out.println("p1: " + uncertainPars1.get(parId).get(1) + ", " + p1.eDist(point) + ", " + distance1);
////                        System.out.println("p2: " + uncertainPars2.get(parId).get(1) + ", " + p2.eDist(point) + ", " + distance2);
//                        if (uncertainPars1.get(parId).get(1) + p1.eDist(point) <= distance1 && uncertainPars2.get(parId).get(1) + p2.eDist(point) <= distance2) {
////                            System.out.println("meet distance");
//                            TrajectoryPoint traPointPre = new TrajectoryPoint(x, y, floor, t, preTraPointNum);
//                            traPointPre.setParID(parId);
//                            traPointPre.setTrajectoryID(trajectory.getTrajectoryId());
//
//                            temp_list.add(traPointPre);
////                            System.out.println("temp_list size: " + temp_list.size());
//
//                            HashMap<Integer, ArrayList<Integer>> temp_tra = new HashMap<>();
//                            if (par_tra.get(parId) != null) {
//                                temp_tra = par_tra.get(parId);
//                                if (temp_tra.get(t) != null) {
//                                    ArrayList<Integer> temp_tra_list = temp_tra.get(t);
//                                    if (!temp_tra_list.contains(trajectory.getTrajectoryId())) {
//                                        temp_tra_list.add(trajectory.getTrajectoryId());
//                                        temp_tra.put(t, temp_tra_list);
//                                    }
//                                }
//                                else {
//                                    temp_tra.put(t, new ArrayList<Integer>(Arrays.asList(trajectory.getTrajectoryId())));
//                                }
//                                par_tra.put(parId, temp_tra);
//                            }
//                            else {
//                                temp_tra.put(t, new ArrayList<Integer>(Arrays.asList(trajectory.getTrajectoryId())));
//                                par_tra.put(parId, temp_tra);
//                            }
//
//
//                            number++;
//                            tras.add(traPointPre);
//                        }
//
//                    }
//                }
//
//
//            }
//
//        }
//        for (TrajectoryPoint tra: tras) {
//            tra.setProbability(1 / number);
//        }
////        System.out.println("temp_list size: " + temp_list.size() + "..........");
////        System.out.println("trajectoryId: " + trajectory.getTrajectoryId() + "..........");
////        System.out.println("t: " + t + "..........");
//        temp_map.put(t, temp_list);
//        preTrajectory.put(trajectory.getTrajectoryId(), temp_map);
//
//        if (preTrajectory.get(trajectory.getTrajectoryId()) == null || preTrajectory.get(trajectory.getTrajectoryId()).get(t) == null) {
////            System.out.println("is empty....");
//            HashMap<Integer, ArrayList<TrajectoryPoint>> temp_map1 = new HashMap<>();
//            if (preTrajectory.get(trajectory.getTrajectoryId())!= null) {
//                temp_map1 = preTrajectory.get(trajectory.getTrajectoryId());
//            }
//            ArrayList<TrajectoryPoint> list = new ArrayList<>();
//            TrajectoryPoint traPointPre = new TrajectoryPoint(traPoint1, t, preTraPointNum);
//
//            traPointPre.setParID(traPoint1.getParID());
//            traPointPre.setTrajectoryID(trajectory.getTrajectoryId());
//            traPointPre.setProbability(0.5);
//
//            list.add(traPointPre);
//
//            HashMap<Integer, ArrayList<Integer>> temp_tra = new HashMap<>();
//            if (par_tra.get(traPoint1.getParID()) != null) {
//                temp_tra = par_tra.get(traPoint1.getParID());
//                if (temp_tra.get(t) != null) {
//                    ArrayList<Integer> temp_tra_list = temp_tra.get(t);
//                    if (!temp_tra_list.contains(trajectory.getTrajectoryId())) {
//                        temp_tra_list.add(trajectory.getTrajectoryId());
//                        temp_tra.put(t, temp_tra_list);
//                    }
//                }
//                else {
//                    temp_tra.put(t, new ArrayList<Integer>(Arrays.asList(trajectory.getTrajectoryId())));
//                }
//                par_tra.put(traPoint1.getParID(), temp_tra);
//            }
//            else {
//                temp_tra.put(t, new ArrayList<Integer>(Arrays.asList(trajectory.getTrajectoryId())));
//                par_tra.put(traPoint1.getParID(), temp_tra);
//            }
////
//
//
//            TrajectoryPoint traPointPre1 = new TrajectoryPoint(traPoint2, t, preTraPointNum);
//
//            traPointPre1.setParID(traPoint2.getParID());
//            traPointPre1.setTrajectoryID(trajectory.getTrajectoryId());
//            traPointPre1.setProbability(0.5);
//            list.add(traPointPre1);
//
//            HashMap<Integer, ArrayList<Integer>> temp_tra2 = new HashMap<>();
//            if (par_tra.get(traPoint2.getParID()) != null) {
//                temp_tra2 = par_tra.get(traPoint2.getParID());
//                if (temp_tra2.get(t) != null) {
//                    ArrayList<Integer> temp_tra_list = temp_tra2.get(t);
//                    if (!temp_tra_list.contains(trajectory.getTrajectoryId())) {
//                        temp_tra_list.add(trajectory.getTrajectoryId());
//                        temp_tra2.put(t, temp_tra_list);
//                    }
//                }
//                else {
//                    temp_tra2.put(t, new ArrayList<Integer>(Arrays.asList(trajectory.getTrajectoryId())));
//                }
//                par_tra.put(traPoint2.getParID(), temp_tra2);
//            }
//            else {
//                temp_tra2.put(t, new ArrayList<Integer>(Arrays.asList(trajectory.getTrajectoryId())));
//                par_tra.put(traPoint2.getParID(), temp_tra2);
//            }
//
//            temp_map1.put(t, list);
//
//            preTrajectory.put(trajectory.getTrajectoryId(), temp_map1);
//            return;
//        }


    }

    public static HashMap<Integer, ArrayList<Double>> findPartition(TrajectoryPoint traPoint, double distance) {
//        System.out.println("traPoint: " + traPoint.getTraPointId() + ", distance: " + distance);
        HashMap<Integer, ArrayList<Double>> result = new HashMap<Integer, ArrayList<Double>>();// key: partitionId; value: <doorId, distance>
        int [] isParVisited = new int [IndoorSpace.iPartitions.size()];
        int [] isDoorVisited = new int [IndoorSpace.iDoors.size()];
        double [] dist = new double[IndoorSpace.iDoors.size()];
        ArrayList<ArrayList<Integer>> prev = new ArrayList<ArrayList<Integer>>();

        int qPartitionId = traPoint.getParID();
        Partition qPar = IndoorSpace.iPartitions.get(qPartitionId);
        result.put(qPartitionId, new ArrayList<Double>(Arrays.asList(-1.0, 0.0)));

        isParVisited[qPartitionId] = 1;

        ArrayList<Integer> sDoors = new ArrayList<Integer>();

        sDoors = qPar.getmDoors();

        for(int i = 0; i < sDoors.size(); i++) {
            int sDoorId = sDoors.get(i);
            Door sDoor = IndoorSpace.iDoors.get(sDoorId);
            double dist1 = traPoint.eDist(sDoor);
//            System.out.println("sdoor: " + sDoorId + ", distance: " + dist1);
            dist[sDoorId] = dist1;
        }

        // minHeap
        BinaryHeap<Double> H = new BinaryHeap<Double>(IndoorSpace.iDoors.size());
        for (int j = 0; j < IndoorSpace.iDoors.size(); j++) {
            if (!sDoors.contains(j)) {
                dist[j] = Constant.large;
            }

            H.insert(dist[j], j);
            prev.add(null);
        }

        while (H.heapSize > 0) {
            String minElement = H.delete_min();
            String [] minElementArr = minElement.split(",");
            int curDoorId = Integer.parseInt(minElementArr[1]);
            if (Double.parseDouble(minElementArr[0]) != dist[curDoorId]) {
                System.out.println("Something wrong with min heap: algorithm.IDModel_SPQ.pt2ptDistance3");
            }
            Door curDoor = IndoorSpace.iDoors.get(curDoorId);

            if (dist[curDoorId] >= distance) break;
            if (sDoors.contains(curDoorId)) {
                prev.set(curDoorId, new ArrayList<Integer>(Arrays.asList(qPartitionId, -1)));
            }

            int prevParId = prev.get(curDoorId).get(0);

            isDoorVisited[curDoorId] = 1;
            ArrayList<Integer> nextPars = curDoor.getmPartitions();
            for (int i = 0; i < nextPars.size(); i++) {
                int nextParId = nextPars.get(i);
                if (nextParId == prevParId) continue;
//                System.out.println("nextParId: " + nextParId);
                if (result.get(nextParId) != null) {
                    if (dist[curDoorId] < result.get(nextParId).get(1)) {
                        ArrayList<Double> temp = new ArrayList<Double>();
                        temp.add((double)curDoorId);
                        temp.add(dist[curDoorId]);
                        result.put(nextParId, temp);
//                        result.replace(nextParId, temp);
                    }
                }
                else {
                    ArrayList<Double> temp = new ArrayList<Double>();
                    temp.add((double)curDoorId);
                    temp.add(dist[curDoorId]);
                    result.put(nextParId, temp);
                }
                Partition nextPar = IndoorSpace.iPartitions.get(nextParId);

                ArrayList<Integer> leaveDoors = nextPar.getmDoors();
                for (int k = 0; k < leaveDoors.size(); k++) {
                    int leaveDoorId = leaveDoors.get(k);
                    if (isDoorVisited[leaveDoorId] != 1) {

                        if (dist[curDoorId] + nextPar.getdistMatrix().getDistance(curDoorId, leaveDoorId) < dist[leaveDoorId]) {
                            double oldDist = dist[leaveDoorId];
                            dist[leaveDoorId] = dist[curDoorId] + nextPar.getdistMatrix().getDistance(curDoorId, leaveDoorId);
                            prev.set(leaveDoorId, new ArrayList<Integer>(Arrays.asList(nextParId, leaveDoorId)));
                            H.updateNode(oldDist, leaveDoorId, dist[leaveDoorId], leaveDoorId);
                        }

                    }
                }
            }

        }

        return result;
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

}
