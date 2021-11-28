package test;

import algorithm.PrintOut;
import datagenerate.DataGen;
import datagenerate.HSMDataGenRead;
import iDModel.GenTopology;
import indoor_entitity.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class HSM_PositionData {
    public static String raw_data = System.getProperty("user.dir") + "/HSM/location20180101.csv";
    public static String pro_data = System.getProperty("user.dir") + "/HSM/location_pro.txt";
    public static HashMap<Integer, Double> scale_map = new HashMap<>();
    public static HashMap<Integer, Double> rotAng_map = new HashMap<>();
    public static HashMap<Integer, Double> diffX_map = new HashMap<>();
    public static HashMap<Integer, Double> diffY_map = new HashMap<>();
    public static HashMap<String, Integer> id_map = new HashMap<>();

    public static Set<String> mac_set = new HashSet<>();

    public static String trajectoryPoints = System.getProperty("user.dir") + "/HSM/trajectoryPoint_";
    public static String trajectorys = System.getProperty("user.dir") + "/HSM/trajectory_";

    public static void trajectoryGen_save(String file_point, String file_tra, String dataset) throws IOException {
        String results = "";
        try {
            FileWriter fw = new FileWriter(file_point + dataset + ".txt");
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
                    FileWriter fw = new FileWriter(file_point + dataset + ".txt", true);
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
            FileWriter fw = new FileWriter(file_point + dataset + ".txt", true);
            fw.write(results);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        results = "";
        try {
            FileWriter fw = new FileWriter(file_tra + dataset + ".txt");
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
                    FileWriter fw = new FileWriter(file_tra + dataset + ".txt", true);
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
            FileWriter fw = new FileWriter(file_tra + dataset + ".txt", true);
            fw.write(results);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

    }

    // read raw data
    public static void read_raw_data() throws IOException {
        String result = "";
        int macId = 0;
        Path path = Paths.get(raw_data);
        Scanner scanner = new Scanner(path);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] tempArr = line.split(",");
            String mac = tempArr[1];
            if (!id_map.keySet().contains(mac)) {
                id_map.put(mac, macId++);
            }
            int id = id_map.get(mac);
            double x_raw = Double.parseDouble(tempArr[3]);
            double y_raw = Double.parseDouble(tempArr[4]);

            String time_raw1 = tempArr[5].split(" ")[1];
            int time1 = Integer.parseInt(time_raw1.split(":")[0]) * 3600 + Integer.parseInt(time_raw1.split(":")[1]) * 60
                    + Integer.parseInt(time_raw1.split(":")[2]);

            String time_raw2 = tempArr[6].split(" ")[1];
            int time2 = Integer.parseInt(time_raw2.split(":")[0]) * 3600 + Integer.parseInt(time_raw2.split(":")[1]) * 60
                    + Integer.parseInt(time_raw2.split(":")[2]);

            int floor = Integer.parseInt(tempArr[8].split(" ")[1]) - 1;

            ArrayList<Double> point_xy = transform_point(x_raw, y_raw, floor);
            double x = point_xy.get(0);
            double y = point_xy.get(1);

            int parId = locPartition(new Point(x, y, floor));
            if (parId == -1) {
                System.out.println("something wrong with locPartition");
                System.out.println("x: " + x + ", y: " + y + ", floor: " + floor);
                continue;
            }
            result += id + "\t" + x + "\t" + y + "\t" + floor + "\t" + parId + "\t" + time1 + "\t" + time2 + "\n";
        }

        try {
            FileWriter fw = new FileWriter(pro_data);
            fw.write(result);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public static void rawData_analysis() throws IOException {
        Path path = Paths.get(raw_data);
        Scanner scanner = new Scanner(path);
        int num = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] tempArr = line.split(",");
            String mac = tempArr[1];
            mac_set.add(mac);
            num++;
        }
        System.out.println("mac size: " + mac_set.size() + ", positioning record number: " + num);
    }


    public static void pro_data() throws IOException{
        Path path = Paths.get(pro_data);
        Scanner scanner = new Scanner(path);

        HashMap<Integer, ArrayList<ArrayList<Double>>> tra_info = new HashMap<>();
        ArrayList<ArrayList<Double>> info = new ArrayList<>();
        int currentId = 0;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] tempArr = line.split("\t");
            int id = Integer.parseInt(tempArr[0]);
//            System.out.println("id: " + id);
            if (id != currentId) {
                Trajectory trajectory = new Trajectory();
                IndoorSpace.iTrajectory.add(trajectory);
                System.out.println("currentId: " + currentId + ", traId: " + trajectory.getTrajectoryId() + ", traSize: " + IndoorSpace.iTrajectory.size());
                ArrayList<ArrayList<Double>> cal_info = tra_info.get(currentId);
                for (int i = 0; i < cal_info.size(); i++) {
                    ArrayList<Double> cal_info_temp = cal_info.get(i);
                    double x_temp = cal_info_temp.get(1);
                    double y_temp = cal_info_temp.get(2);
                    int floor_temp = (int)(double)cal_info_temp.get(3);
                    int parId_temp = (int)(double)cal_info_temp.get(4);
                    int time1_temp = (int)(double)cal_info_temp.get(5);
                    int time2_temp = (int)(double)cal_info_temp.get(6);
                    if (i + 1 < cal_info.size()) {
                        int next_time1 = (int)(double)(cal_info.get(i + 1).get(5));
                        if (next_time1 < time2_temp) {
                            time2_temp = next_time1;
                        }
                    }

                    int start_t;
                    if (time1_temp % 10 == 0) {
                        start_t = time1_temp;
                    }
                    else {
                        start_t = time1_temp / 10 * 10 + 10;
                    }
                    for (int t = start_t; t < time2_temp; t += 10) {
//                        System.out.println("t: " + t);
                        if (trajectory.getMaxTime() != -1 && t - trajectory.getMaxTime() >= 60) {
                            trajectory = new Trajectory();
                            IndoorSpace.iTrajectory.add(trajectory);
                            System.out.println("the interval is longer than 60... t: " + 60);
                        }
                        TrajectoryPoint traPoint = new TrajectoryPoint(x_temp, y_temp, floor_temp, t);
                        IndoorSpace.iTraPoint.add(traPoint);
                        traPoint.setTrajectoryID(trajectory.getTrajectoryId());
                        traPoint.setProbability(1);
                        traPoint.setParID(parId_temp);
                        Partition par = IndoorSpace.iPartitions.get(traPoint.getParID());
                        par.addTrajectory(t, trajectory.getTrajectoryId(), traPoint.getTraPointId());
                        trajectory.addTime(t);
                        trajectory.addTrajectory(t, traPoint.getTraPointId());
                    }
                }
                currentId = id;
                info = new ArrayList<>();
            }
            double x = Double.parseDouble(tempArr[1]);
            double y = Double.parseDouble(tempArr[2]);
            int floor = Integer.parseInt(tempArr[3]);

            int parId = Integer.parseInt(tempArr[4]);

            int time1 = Integer.parseInt(tempArr[5]);
            int time2 = Integer.parseInt(tempArr[6]);

            ArrayList<Double> temp_info = new ArrayList<>();
            temp_info.add((double)id);
            temp_info.add(x);
            temp_info.add(y);
            temp_info.add((double)floor);
            temp_info.add((double)parId);
            temp_info.add((double)time1);
            temp_info.add((double)time2);
            info.add(temp_info);
            tra_info.put(id, info);
        }

        Trajectory trajectory = new Trajectory();
        IndoorSpace.iTrajectory.add(trajectory);
        ArrayList<ArrayList<Double>> cal_info = tra_info.get(currentId);
        for (int i = 0; i < cal_info.size(); i++) {
            ArrayList<Double> cal_info_temp = cal_info.get(i);
            double x_temp = cal_info_temp.get(1);
            double y_temp = cal_info_temp.get(2);
            int floor_temp = (int)(double)cal_info_temp.get(3);
            int parId_temp = (int)(double)cal_info_temp.get(4);
            int time1_temp = (int)(double)cal_info_temp.get(5);
            int time2_temp = (int)(double)cal_info_temp.get(6);
            if (i + 1 < cal_info.size()) {
                int next_time1 = (int)(double)(cal_info.get(i + 1).get(5));
                if (next_time1 < time2_temp) {
                    time2_temp = next_time1;
                }
            }

            for (int t = time1_temp / 10 * 10 + 10; t < time2_temp; t += 10) {
                TrajectoryPoint traPoint = new TrajectoryPoint(x_temp, y_temp, floor_temp, t);
                IndoorSpace.iTraPoint.add(traPoint);
                traPoint.setTrajectoryID(trajectory.getTrajectoryId());
                traPoint.setProbability(1);
                traPoint.setParID(parId_temp);
                Partition par = IndoorSpace.iPartitions.get(traPoint.getParID());
                par.addTrajectory(t, trajectory.getTrajectoryId(), traPoint.getTraPointId());
                trajectory.addTime(t);
                trajectory.addTrajectory(t, traPoint.getTraPointId());
            }
        }


    }



    public static ArrayList<Double> transform_point(double x, double y, int floor) {
        ArrayList<Double> result = new ArrayList<>();

        double x_scaled = - x * scale_map.get(floor);
        double y_scaled = y * scale_map.get(floor);

        double radians = rotAng_map.get(floor) * Math.PI / 180;
        double x_rotated = x_scaled * Math.cos(radians) - y_scaled * Math.sin(radians);
        double y_rotated = x_scaled * Math.sin(radians) + y_scaled * Math.cos(radians);

        double x_moved = x_rotated + diffX_map.get(floor);
        double y_moved = y_rotated + diffY_map.get(floor);

        result.add(x_moved);
        result.add(y_moved);

        System.out.println("x_moved: " + x_moved + ", y_moved: " + y_moved);

        return result;
    }

    public static void map_prepare() {
        scale_map.put(0, 25.5);
        scale_map.put(1, 25.5);
        scale_map.put(2, 25.5);
        scale_map.put(3, 28.0);
        scale_map.put(4, 57.5);
        scale_map.put(5, 25.7);
        scale_map.put(6, 25.0);

        rotAng_map.put(0, 3.3);
        rotAng_map.put(1, 3.3);
        rotAng_map.put(2, 3.3);
        rotAng_map.put(3, 3.3);
        rotAng_map.put(4, 3.3);
        rotAng_map.put(5, 3.3);
        rotAng_map.put(6, 3.3);

        diffX_map.put(0, 2290.0);
        diffX_map.put(1, 2290.0);
        diffX_map.put(2, 2230.0);
        diffX_map.put(3, 2230.0);
        diffX_map.put(4, 2230.0);
        diffX_map.put(5, 2230.0);
        diffX_map.put(6, 2250.0);

        diffY_map.put(0, 550.0);
        diffY_map.put(1, 700.0);
        diffY_map.put(2, 800.0);
        diffY_map.put(3, 700.0);
        diffY_map.put(4, 730.0);
        diffY_map.put(5, 560.0);
        diffY_map.put(6, 570.0);

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

    public static void main(String[] arg) throws IOException {
//        PrintOut printOut = new PrintOut();
//        HSMDataGenRead dateGenRead = new HSMDataGenRead();
//        dateGenRead.dataGen("newHsm");
//
//        GenTopology genTopology = new GenTopology();
//        genTopology.genTopology();
////        map_prepare();
//////        transform_point(37.167843, 10.873213, 1);
//////        transform_point(35.062122, 10.718712, 1);
////        read_raw_data();
//        pro_data();
//        trajectoryGen_save(trajectoryPoints, trajectorys, "HSM");

        rawData_analysis();
    }

}
