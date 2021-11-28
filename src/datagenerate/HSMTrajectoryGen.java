package datagenerate;

import indoor_entitity.IndoorSpace;
import indoor_entitity.Partition;
import indoor_entitity.Trajectory;
import indoor_entitity.TrajectoryPoint;
import utilities.DataGenConstant;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class HSMTrajectoryGen {
    public static String trajectoryPoints = System.getProperty("user.dir") + "/HSM/trajectoryPoint_";
    public static String trajectorys = System.getProperty("user.dir") + "/HSM/trajectory_";

    public static void trajectoryGen_read(String file_point, String file_tra, String dataset) throws IOException {

        Path path1 = Paths.get(file_tra + dataset + ".txt");
        Scanner scanner1 = new Scanner(path1);
        while (scanner1.hasNextLine()) {
            String line = scanner1.nextLine();
            String [] tempArr = line.split("\t");
            int trajectoryId = Integer.parseInt(tempArr[0]);
//            System.out.println("traId: " + trajectoryId);

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
                for (int j = 1; j < traPointArr.length; j++) {
//                    System.out.println("j: " + j);
                    int traPointId = Integer.parseInt(traPointArr[j]);

                    trajectory.addTrajectory(t, traPointId);
//                    System.out.println("add trajectory: t " + t + ", traPointId: " + traPointId);
                    trajectory.addTime(t);


                }
            }
        }

        Path path = Paths.get(file_point + dataset + ".txt");
        Scanner scanner = new Scanner(path);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String [] tempArr = line.split("\t");

            int traPointId = Integer.parseInt(tempArr[0]);

            int t = Integer.parseInt(tempArr[1]);
            double x = Double.parseDouble(tempArr[2]) * DataGenConstant.zoom_HSM;
            double y = Double.parseDouble(tempArr[3]) * DataGenConstant.zoom_HSM;
            int floor = Integer.parseInt(tempArr[4]);
            TrajectoryPoint traPoint = new TrajectoryPoint(x, y, floor, t);
            IndoorSpace.iTraPoint.add(traPoint);

            if (t % DataGenConstant.sampleInterval != 0) continue;
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
}
