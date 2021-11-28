package datagenerate;

import algorithm.PrintOut;
import iDModel.GenTopology;
import indoor_entitity.*;
import algorithm.IDModel_SPQ;
import utilities.DataGenConstant;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class TrajectoryGen {
    public static String trajectoryPoints = System.getProperty("user.dir") + "/data_trajectory/trajectoryPoint_";
    public static String trajectorys = System.getProperty("user.dir") + "/data_trajectory/trajectory_";
    public static String uncertainPoints = System.getProperty("user.dir") + "/data_trajectory/random_uncertain_point.txt";

    public static void trajectoryGen_save(String file_point, String file_tra, String dataset, int num) throws IOException {
//        genAllTrajectories(num);
        genTrajectories_test(num);
        String results = "";
        try {
            FileWriter fw = new FileWriter(file_point + dataset + "_" + num + "_" + DataGenConstant.sampleInterval + ".txt");
            fw.write(results);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (int i = 0; i < IndoorSpace.iTraPoint.size(); i++) {
            TrajectoryPoint traPoint = IndoorSpace.iTraPoint.get(i);
            results += traPoint.getTraPointId() + "\t" + traPoint.getT() + "\t" + traPoint.getX() + "\t" + traPoint.getY() + "\t" + traPoint.getmFloor() + "\t" + traPoint.getParID()
                            + "\t" + traPoint.getProbability() + "\t" + traPoint.getTrajectoryID() + "\n";
            if (i % 1000 == 0) {
                System.out.println("traPointId: " + traPoint.getTraPointId());
                try {
                    FileWriter fw = new FileWriter(file_point + dataset + "_" + num + "_" + DataGenConstant.sampleInterval + ".txt", true);
                    fw.write(results);
                    fw.flush();
                    fw.close();
                    results = "";
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        try {
            FileWriter fw = new FileWriter(file_point + dataset + "_" + num + "_" + DataGenConstant.sampleInterval + ".txt", true);
            fw.write(results);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        results = "";
        try {
            FileWriter fw = new FileWriter(file_tra + dataset + "_" + num+ "_" + DataGenConstant.sampleInterval + ".txt");
            fw.write(results);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        for (int i = 0; i < IndoorSpace.iTrajectory.size(); i++) {
            Trajectory trajectory = IndoorSpace.iTrajectory.get(i);
            HashMap<Integer, ArrayList<Integer>> traPoints = trajectory.getTrajectory();
            results += trajectory.getTrajectoryId();
            ArrayList<Integer> times = trajectory.getTime();
            for (int time: times) {
                results += "\t" + time;
                ArrayList<Integer> traPointList = traPoints.get(time);
                for (int traPointId: traPointList) {
                    results += "," + traPointId;
                }
            }
            results += "\n";
            if (i % 500 == 0) {
                System.out.println("trajectoryId: " + trajectory.getTrajectoryId());
                try {
                    FileWriter fw = new FileWriter(file_tra + dataset + "_" + num + "_" + DataGenConstant.sampleInterval + ".txt", true);
                    fw.write(results);
                    fw.flush();
                    fw.close();
                    results = "";
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        try {
            FileWriter fw = new FileWriter(file_tra + dataset + "_" + num+ "_" + DataGenConstant.sampleInterval + ".txt", true);
            fw.write(results);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

    }

    public static void trajectoryGen_read(String file_point, String file_tra, String dataset, int num, boolean isTrue) throws IOException {
        ArrayList<Integer> uncertainTraId = new ArrayList<>();

        if (!isTrue) {

            Path path0 = Paths.get(uncertainPoints);
            Scanner scanner0 = new Scanner(path0);
            while (scanner0.hasNextLine()) {
                String line = scanner0.nextLine();
                String [] tempArr = line.split("\t");
                for (int i = 0; i < tempArr.length; i++) {
                    int pointId = Integer.parseInt(tempArr[i]);
                    uncertainTraId.add(pointId);
                }

            }
        }

        Path path1 = Paths.get(file_tra + dataset + "_" + 10000 + "_" + DataGenConstant.sampleInterval + ".txt");
        Scanner scanner1 = new Scanner(path1);
        while (scanner1.hasNextLine()) {
            String line = scanner1.nextLine();
            String [] tempArr = line.split("\t");
            int trajectoryId = Integer.parseInt(tempArr[0]);
            if (trajectoryId >= num) break;
            Trajectory trajectory = new Trajectory();
            IndoorSpace.iTrajectory.add(trajectory);
            if (trajectory.getTrajectoryId() != trajectoryId) {
                System.out.println("something wrong with the object ID: TrajectoryGen.trajectoryGen_read");
            }
            for (int i = 1; i < tempArr.length; i++) {
//                System.out.println("i: " + i);
                String [] traPointArr = tempArr[i].split(",");
                int t = Integer.parseInt(traPointArr[0]);
                if (t % DataGenConstant.sampleInterval != 0) continue;
//                if (!isTrue && t % (DataGenConstant.sampleInterval * 10) == 0) continue;
                for (int j = 1; j < traPointArr.length; j++) {
//                    System.out.println("j: " + j);
                    int traPointId = Integer.parseInt(traPointArr[j]);

//                    if (!isTrue && traPointId % 10 == 1) {
////                        System.out.println("trajectoryId" + traPointId);
//                        uncertainTraId.add(traPointId);
//                    }
                    if (!isTrue && uncertainTraId.contains(traPointId)) {
                        continue;
                    }
                    else {
                        trajectory.addTrajectory(t, traPointId);
                        trajectory.addTime(t);
                    }

                }
            }
        }

//        int id = -1;
        Path path = Paths.get(file_point + dataset + "_" + 10000 + "_" + DataGenConstant.sampleInterval + ".txt");
        Scanner scanner = new Scanner(path);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String [] tempArr = line.split("\t");

            if (Integer.parseInt(tempArr[7]) >= num) break;
            int traPointId = Integer.parseInt(tempArr[0]);

            int t = Integer.parseInt(tempArr[1]);
            double x = Double.parseDouble(tempArr[2]);
            double y = Double.parseDouble(tempArr[3]);
            int floor = Integer.parseInt(tempArr[4]);
            TrajectoryPoint traPoint = new TrajectoryPoint(x, y, floor, t);
            IndoorSpace.iTraPoint.add(traPoint);
            if (uncertainTraId.contains(traPointId)) continue;
            if (t % DataGenConstant.sampleInterval != 0) continue;
//            if (!isTrue && t % (DataGenConstant.sampleInterval * 10) == 0) continue;
            if (traPoint.getTraPointId() != traPointId) {
                System.out.println("something wrong with the traPoint ID: TrajectoryGen.trajectoryGen_read");
            }
            int parId = Integer.parseInt(tempArr[5]);
            traPoint.setParID(parId);
            traPoint.setProbability(Double.parseDouble(tempArr[6]));
            int trajectoryId = Integer.parseInt(tempArr[7]);

            traPoint.setTrajectoryID(trajectoryId);
            Partition par = IndoorSpace.iPartitions.get(parId);
            par.addTrajectory(t, trajectoryId, traPointId);
        }



    }

    public static void genTrajectories_test(int num) throws IOException{
        String result = "";
        int n = 0;
        while (n < 1000 - 200) {
            Trajectory trajectory = new Trajectory();
            IndoorSpace.iTrajectory.add(trajectory);
            result += trajectory.getTrajectoryId();
            HashMap<Integer, ArrayList<Point>> SEPoints = new HashMap<>();
            while (trajectory.getTime().size() == 0 || trajectory.getTime().size() * DataGenConstant.sampleInterval < 2500) {
                Point point1 = pickPoint();
                Point point2 = pickPoint();
                int t = (int)(Math.random() * 7000) * 10;
                if (trajectory.getTime().size() != 0) {
                    t = trajectory.getMaxTime() + DataGenConstant.sampleInterval;
                    point1 = IndoorSpace.iTraPoint.get(IndoorSpace.iTraPoint.size() - 1);
                }
                if (trajectory.getMaxTime() >= DataGenConstant.endTime) break;

                IDModel_SPQ idModel_spq = new IDModel_SPQ();
                String path = idModel_spq.pt2ptDistance3(point1, point2);
                result += "\t" + point1.getX() + "," + point1.getY() + "," + point1.getmFloor() + "," + point2.getX() + "," + point2.getY() + "," + point2.getmFloor();
                genTrajectory(t, trajectory, point1, path);
                genTrajectory_static();
            }
            result += "\n";
            n++;
        }
        while (n < 1000) {
            ArrayList<Integer> trajectorys = new ArrayList<>(Arrays.asList(4, 7, 90, 23, 34, 87, 99, 1, 67, 29, 5, 31, 47, 39, 32, 52, 96, 22, 91, 63));
            for (int k = 0; k < trajectorys.size(); k++) {
                int traId = trajectorys.get(k);
                for (int m = 0; m < 10; m++) {
                    Trajectory trajectory = IndoorSpace.iTrajectory.get(traId);
                    Trajectory trajectory2 = new Trajectory();
                    IndoorSpace.iTrajectory.add(trajectory2);
                    int startIndex = (int)(Math.random() * trajectory.getTime().size());
                    int duration = (int)((500 / DataGenConstant.sampleInterval) + Math.random() * (1800 / DataGenConstant.sampleInterval));
                    for (int i = startIndex; i < Math.min(trajectory.getTime().size(), startIndex + duration); i++) {
                        int time = trajectory.getTime().get(i);
                        ArrayList<Integer> traPointIds = trajectory.getTrajectory(time);

                        for (int traPointId: traPointIds) {
                            TrajectoryPoint traPoint = IndoorSpace.iTraPoint.get(traPointId);
                            TrajectoryPoint traPoint2 = new TrajectoryPoint(traPoint.getX(), traPoint.getY(), traPoint.getmFloor(), time);
                            IndoorSpace.iTraPoint.add(traPoint2);
                            traPoint2.setTrajectoryID(trajectory2.getTrajectoryId());
                            traPoint2.setProbability(1);
                            traPoint2.setParID(traPoint.getParID());
                            Partition par = IndoorSpace.iPartitions.get(traPoint.getParID());
                            par.addTrajectory(time, trajectory2.getTrajectoryId(), traPoint2.getTraPointId());
                            trajectory2.addTime(time);
                            trajectory2.addTrajectory(time, traPoint2.getTraPointId());
                        }

                    }
                    n++;
                }
            }
        }

        while (n < num) {
            Trajectory trajectory = new Trajectory();
            IndoorSpace.iTrajectory.add(trajectory);
            result += trajectory.getTrajectoryId();
            HashMap<Integer, ArrayList<Point>> SEPoints = new HashMap<>();
            while (trajectory.getTime().size() == 0 || trajectory.getTime().size() * DataGenConstant.sampleInterval < 3600) {
                Point point1 = pickPoint();
                Point point2 = pickPoint();
                int t = (int)(Math.random() * 7000) * 10;
                if (trajectory.getTime().size() != 0) {
                    t = trajectory.getMaxTime() + DataGenConstant.sampleInterval;
                    point1 = IndoorSpace.iTraPoint.get(IndoorSpace.iTraPoint.size() - 1);
                }
                if (trajectory.getMaxTime() >= DataGenConstant.endTime) break;

                IDModel_SPQ idModel_spq = new IDModel_SPQ();
                String path = idModel_spq.pt2ptDistance3(point1, point2);
                result += "\t" + point1.getX() + "," + point1.getY() + "," + point1.getmFloor() + "," + point2.getX() + "," + point2.getY() + "," + point2.getmFloor();
                genTrajectory(t, trajectory, point1, path);
                genTrajectory_static();
            }
            result += "\n";
//            traSEPoints.put(trajectory.getTrajectoryId(), SEPoints);
            n++;
        }
    }


    public static void genTrajectory_static() {
        int num = (int)(Math.random() * (480 / DataGenConstant.sampleInterval));
        for (int i = 0; i < num; i++) {
            TrajectoryPoint traPoint_prev = IndoorSpace.iTraPoint.get(IndoorSpace.iTraPoint.size() - 1);
            Trajectory trajectory = IndoorSpace.iTrajectory.get(traPoint_prev.getTrajectoryID());
            if (trajectory.getMaxTime() >= DataGenConstant.endTime) break;
            TrajectoryPoint traPoint = new TrajectoryPoint(traPoint_prev.getX(), traPoint_prev.getY(), traPoint_prev.getmFloor(), traPoint_prev.getT() + DataGenConstant.sampleInterval);
            IndoorSpace.iTraPoint.add(traPoint);
            traPoint.setParID(traPoint_prev.getParID());
            traPoint.setProbability(1);
            traPoint.setTrajectoryID(traPoint_prev.getTrajectoryID());
            trajectory.addTrajectory(traPoint_prev.getT() + DataGenConstant.sampleInterval, traPoint.getTraPointId());
            trajectory.addTime(traPoint_prev.getT() + DataGenConstant.sampleInterval);
            Partition par = IndoorSpace.iPartitions.get(traPoint_prev.getParID());
            par.addTrajectory(traPoint_prev.getT() + DataGenConstant.sampleInterval, trajectory.getTrajectoryId(), traPoint.getTraPointId());


        }

    }
    public static void genTrajectory(int t, Trajectory trajectory, Point point1, String path) {

        String[] pathArr = path.split("\t");
//        System.out.println("pathArr.length: " + pathArr.length);

        // sampling
        int sampleInterval = DataGenConstant.sampleInterval;
        Point p1 = point1;
        int parId = locPartition(p1);
        TrajectoryPoint traPoint = new TrajectoryPoint(p1, t);
        IndoorSpace.iTraPoint.add(traPoint);
        traPoint.setParID(parId);
        traPoint.setProbability(1);
        traPoint.setTrajectoryID(trajectory.getTrajectoryId());
        trajectory.addTrajectory(t, traPoint.getTraPointId());
        trajectory.addTime(t);
        IndoorSpace.iPartitions.get(parId).addTrajectory(t, trajectory.getTrajectoryId(), traPoint.getTraPointId());
        t = t + sampleInterval;
        double sampleDist = sampleInterval * DataGenConstant.traveling_speed;
//        System.out.println("path 0: " + pathArr[0]);
//        System.out.println("path 1: " + pathArr[1]);
//        System.out.println("path 2: " + pathArr[2]);

        for (int i = 1; i < pathArr.length - 1; i++) {
//            System.out.println("start sampling...");
            Door p2 = IndoorSpace.iDoors.get(Integer.parseInt(pathArr[i]));
            double p2pDist = p1.eDist(p2);
//            System.out.println("p2: " + p2.getX() + ", " + p2.getY());
            if (p1.getmFloor() != p2.getmFloor() && p1.getX() == p2.getX() && p1.getY() == p2.getY()) {
                p2pDist = DataGenConstant.lenStairway;
            }

            if (i > 1) {
                ArrayList<Integer> list1 = IndoorSpace.iDoors.get(Integer.parseInt(pathArr[i - 1])).getmPartitions();
                ArrayList<Integer> list2 = p2.getmPartitions();
                List<Integer> intersection = list1.stream().filter(item -> list2.contains(item)).collect(toList());
                if (intersection.size() == 1) {
                    parId = intersection.get(0);
//                    System.out.println("parId: " + parId);
                }
                else {
                    System.out.println("something wrong with TrajectoryGen.genTrajectory(");
                }
//
            }
            Partition par = IndoorSpace.iPartitions.get(parId);
            while (p2pDist > sampleDist) {
                if (p2pDist == DataGenConstant.lenStairway || p2pDist <= 3 * sampleDist) {
                    p1 = sample(p1, p2, p2pDist, sampleDist, par);
                }
                else {
                    p1 = sample_random(p1, p2, p2pDist, sampleDist, par);
                }
                traPoint = new TrajectoryPoint(p1, t);
                IndoorSpace.iTraPoint.add(traPoint);
                traPoint.setParID(parId);
                traPoint.setProbability(1);
                traPoint.setTrajectoryID(trajectory.getTrajectoryId());
                trajectory.addTrajectory(t, traPoint.getTraPointId());
                trajectory.addTime(t);
                par.addTrajectory(t, trajectory.getTrajectoryId(), traPoint.getTraPointId());
                t += sampleInterval;
//                if (t >= 1000) break;
                p2pDist = p1.eDist(p2);
                sampleDist = sampleInterval * DataGenConstant.traveling_speed;
            }
            if (p2pDist == sampleDist) {
                p1 = p2;
                traPoint = new TrajectoryPoint(p1, t);
                IndoorSpace.iTraPoint.add(traPoint);
                traPoint.setParID(parId);
                traPoint.setProbability(1);
                traPoint.setTrajectoryID(trajectory.getTrajectoryId());
                trajectory.addTrajectory(t, traPoint.getTraPointId());
                trajectory.addTime(t);
                par.addTrajectory(t, trajectory.getTrajectoryId(), traPoint.getTraPointId());
                t += sampleInterval;
//                if (t >= 1000) break;
                sampleDist = sampleInterval * DataGenConstant.traveling_speed;
            }
            else {
                p1 = p2;
                sampleDist = sampleInterval * DataGenConstant.traveling_speed - p2pDist;
            }
            if (trajectory.getMaxTime() >= DataGenConstant.endTime) break;
        }



    }

    public static Point sample(Point p1, Point p2, double p2pDist, double sampleDist, Partition par) {
        double xDist = (sampleDist) * Math.abs(p2.getX() - p1.getX()) / p2pDist;
        double yDist = (sampleDist) * Math.abs(p2.getY() - p1.getY()) / p2pDist;
        double x = 0;
        double y = 0;
        int floor = par.getmFloor();
        if (p1.getX() >= p2.getX()) {
            x = p1.getX() - xDist;
        }
        else {
            x = p1.getX() + xDist;
        }
        if (p1.getY() >= p2.getY()) {
            y = p1.getY() - yDist;
        }
        else {
            y = p1.getY() + yDist;
        }
        return new Point(x, y, floor);
    }

    public static Point sample_random(Point p1, Point p2, double p2pDist, double sampleDist, Partition par) {
//        System.out.println("p1 x: " + p1.getX() + ", p1 y: " + p1.getY() + ", p2 x: " + p2.getX() + ", p2 y: " + p2.getY());
//        System.out.println("sampleDist: " + sampleDist + "p2pDist: " + p2pDist);
//        System.out.println("par x1: " + par.getX1() + ", par x2: " + par.getX2() + ", par y1: " + par.getY1() + ", par y2: " + par.getY2());
        double xDist = 0;
        double yDist = 0;
        double x = 0;
        double y = 0;
        int floor = par.getmFloor();
        boolean flag = true;
        while(flag) {
            if (Math.abs(p2.getX() - p1.getX()) < sampleDist) {
                xDist = Math.random() * (Math.min(Math.abs(p2.getX() - p1.getX()), sampleDist));
                yDist = Math.sqrt(Math.pow(sampleDist, 2) - Math.pow(xDist, 2));
            }
            else if (Math.abs(p2.getY() - p1.getY()) < sampleDist) {
                yDist = Math.random() * (Math.min(Math.abs(p2.getY() - p1.getY()), sampleDist));
                xDist = Math.sqrt(Math.pow(sampleDist, 2) - Math.pow(yDist, 2));
            }
            else {
                if (Math.random() > 0.5) {
                    xDist = Math.random() * (sampleDist);
                    yDist = Math.sqrt(Math.pow(sampleDist, 2) - Math.pow(xDist, 2));
                }
                else {
                    yDist = Math.random() * (sampleDist);
                    xDist = Math.sqrt(Math.pow(sampleDist, 2) - Math.pow(yDist, 2));
                }
            }
//            xDist = Math.random() * (sampleDist);
//            if (Math.pow(sampleDist, 2) - Math.pow(xDist, 2) <= 0) {
//                yDist = 0;
//            }
//            else {
//                yDist = Math.sqrt(Math.pow(sampleDist, 2) - Math.pow(xDist, 2));
//            }
//            System.out.println("xDist: " + xDist + ", yDist: " + yDist);
            if (p1.getX() >= p2.getX()) {
                x = p1.getX() - xDist;
            }
            else {
                x = p1.getX() + xDist;
            }
            if (p1.getY() >= p2.getY()) {
                y = p1.getY() - yDist;
            }
            else {
                y = p1.getY() + yDist;
            }
            if (x <= par.getX2() && x >= par.getX1() && y <= par.getY2() && y >= par.getY1()) break;
        }


        return new Point(x, y, floor);
    }

    public static String pickAnotherPoint(Point point1, double distance) {
        String result = "";

        IDModel_SPQ idModel_spq = new IDModel_SPQ();


        while (true) {
            Point point2 = pickPoint();
            result = idModel_spq.pt2ptDistance3(point1, point2);
            //            System.out.println("point2 is finished");
            double dist = Double.parseDouble(result.split("\t")[0]);
            //            System.out.println(3);
            //            double dist = Double.parseDouble(distStr.split("\t")[0]);
            if (dist > distance) {
                return point2.getX() + "," + point2.getY() + "," + point2.getmFloor() + "\t" + result;
            }
        }

    }

    public static Point pickPoint() {
        Point point = null;
        while (true) {
            int x = (int) (Math.random() * (1368 + 1));
            int y = (int) (Math.random() * (1368 + 1));
            int floor = (int) (Math.random() * (DataGenConstant.nFloor));
            if (isLegal(x, y, floor)) {
                point = new Point(x, y, floor);
//                System.out.println("point: " + point.getX() + "," + point.getY() + "," + point.getmFloor());
                return point;
            }
        }
    }

    /**
     * check whether the object is legal
     *
     * @param x
     * @param y
     * @param floor
     * @return
     */
    public static boolean isLegal(double x, double y, int floor) {
        if (floor > DataGenConstant.nFloor) {
            System.out.println("something wrong with the random floor");
            return false;
        }

        if (x >= 1368 || y >= 1368) {
            return false;
        }

        if (x <= 1080 && x >= 288) {
            if ((y <= 144 && y >= 0) || (y <= 1368 && y >= 1224)) {
                return false;
            }
        }

        if (y <= 1080 && y >= 288) {
            if ((x <= 144 && x >= 0) || (x <= 1368 && x >= 1224)) {
                return false;
            }
        }

        return true;
    }

    /**
     * locate a partition according to location string
     *
     * @param point
     * @return partition
     */
    public static int locPartition(Point point) {
        int partitionId = -1;

        int floor = point.getmFloor();
        ArrayList<Integer> pars = IndoorSpace.iFloors.get(floor).getmPartitions();
        for (int i = 0; i < pars.size(); i++) {
            Partition par = IndoorSpace.iPartitions.get(pars.get(i));
            if (point.getX() >= par.getX1() && point.getX() <= par.getX2() && point.getY() >= par.getY1() && point.getY() <= par.getY2()) {
                partitionId = par.getmID();
                return partitionId;
            }
        }

        return partitionId;
    }

    public static void main(String [] arg) throws IOException {
        PrintOut printOut = new PrintOut();
        DataGen dataGen = new DataGen();
        dataGen.genAllData(DataGenConstant.dataType, DataGenConstant.divisionType);

        GenTopology genTopology = new GenTopology();
        genTopology.genTopology();

        DataGenConstant.sampleInterval = 10;
//        trajectoryGen_read(trajectoryPoints, trajectorys, "syn", 10000, true);
//        System.out.println("read finished");
        trajectoryGen_save(trajectoryPoints, trajectorys, "syn", 4000);
//        trajectoryGen_read(trajectoryPoints, trajectorys, "syn");
//
//        dataGen.drawFloor();
    }
}
