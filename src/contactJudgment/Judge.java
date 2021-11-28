package contactJudgment;

import algorithm.Algo_ICQ;
import indoor_entitity.Trajectory;
import indoor_entitity.IndoorSpace;
import indoor_entitity.TrajectoryPoint;
import utilities.DataGenConstant;

import java.util.ArrayList;
import java.util.HashMap;


public class Judge {
    public static HashMap<Integer, HashMap<Integer, Double>> pro_map = new HashMap<Integer, HashMap<Integer, Double>>();
    public static HashMap<Integer, ArrayList<Integer>> con_timeList = new HashMap<Integer, ArrayList<Integer>>();
    public static HashMap<Integer, Integer> lastUnConTime = new HashMap<Integer, Integer>();

    public static int contactJudge(Trajectory trajectory1, Trajectory trajectory2, int sTime, int eTime, double probabilityThreshold) {
        HashMap<Integer, ArrayList<Integer>> trajectoryPoints_tra1 = trajectory1.getTrajectory();
        HashMap<Integer, ArrayList<Integer>> trajectoryPoints_tra2 = trajectory2.getTrajectory();
        if (trajectory1.getMaxTime() < sTime || trajectory1.getMinTime() > eTime) {
            System.out.println("object1 did not appear in [sTime, eTime]");
            return 0;
        }
        if (trajectory2.getMaxTime() < sTime || trajectory2.getMinTime() > eTime) {
            System.out.println("object1 did not appear in [sTime, eTime]");
            return 0;
        }
        int t1 = Math.max(trajectory1.getMinTime(), trajectory2.getMinTime());
        int t2 = Math.max(sTime, t1);
        int t3 = 0;
        if (con_timeList.get(trajectory2.getTrajectoryId()) != null) {
            t3 = con_timeList.get(trajectory2.getTrajectoryId()).get(con_timeList.get(trajectory2.getTrajectoryId()).size() - 1) + DataGenConstant.sampleInterval;
        }
        if (lastUnConTime.get(trajectory2.getTrajectoryId()) > t3) {
            t3 = lastUnConTime.get(trajectory2.getTrajectoryId()) + DataGenConstant.sampleInterval;
        }
        int startTIme = Math.max(t2, t3);
        int t = startTIme;
//        System.out.println("start time: " + t);


        while (t <= trajectory1.getMaxTime() && t <= trajectory2.getMaxTime() && t <= eTime) {
//            System.out.println("time: " + t);

            ArrayList<TrajectoryPoint> traPointList1 = new ArrayList<TrajectoryPoint>();
            if (trajectoryPoints_tra1.get(t) != null) {
                for (int traPointId1: trajectoryPoints_tra1.get(t)) {
                    TrajectoryPoint traPoint1 = IndoorSpace.iTraPoint.get(traPointId1);
                    traPointList1.add(traPoint1);
                }
            }
            else {
                traPointList1 = Uncertainty.preTrajectory.get(trajectory1.getTrajectoryId()).get(t);
            }

            ArrayList<Integer> traPointList2 = new ArrayList<Integer>();
            if (trajectoryPoints_tra2.get(t) != null) {
                traPointList2 = trajectoryPoints_tra2.get(t);
            }
            else {
                t += DataGenConstant.sampleInterval;
                continue;
            }

            if (traPointList2.size() > 1) {
                t += DataGenConstant.sampleInterval;
                continue;
            }

            double probability = 0;
            for ( TrajectoryPoint traPoint1: traPointList1) {

                for (int traPointId2: traPointList2) {
                    TrajectoryPoint traPoint2 = IndoorSpace.iTraPoint.get(traPointId2);
                    if (traPoint1.getParID() != traPoint2.getParID()) {
                        continue;
                    }
                    if (traPoint1.eDist(traPoint2) > DataGenConstant.distanceThreshold) {
                        continue;
                    }
                    probability += traPoint1.getProbability() * traPoint2.getProbability();
                    if (probability >= probabilityThreshold) break;
                }
                if (probability >= probabilityThreshold) break;
            }
//            System.out.println("probability: " + probability);
            HashMap<Integer, Double> temp_pro = new HashMap<Integer, Double>();
            if (pro_map.get(trajectory2.getTrajectoryId()) != null) {
                temp_pro = pro_map.get(trajectory2.getTrajectoryId());
            }
            temp_pro.put(t, probability);
            pro_map.put(trajectory2.getTrajectoryId(), temp_pro);
//            trajectory2.setContactProbability(t, probability);
            if (probability < probabilityThreshold) {
                int lastTime = lastUnConTime.get(trajectory2.getTrajectoryId());
                lastUnConTime.put(trajectory2.getTrajectoryId(), t);
                int conDuration = 0;
                if (lastTime == -1000) {
                    conDuration = t - startTIme - DataGenConstant.sampleInterval;
                }
                else {
                    conDuration = t - lastTime - 2 * DataGenConstant.sampleInterval;
                }
                if (conDuration < DataGenConstant.duration) {
                    if (con_timeList.get(trajectory2.getTrajectoryId()) != null) {
                        con_timeList.put(trajectory2.getTrajectoryId(), null);
                    }
//                    return false;
                }
                else {
//                    System.out.println("start verifying...");
                    int count = verify(Math.max(lastTime + DataGenConstant.sampleInterval, startTIme), t - DataGenConstant.sampleInterval, trajectory1, trajectory2, probabilityThreshold);
//                    System.out.println("pro: " + pro);
                    if (count >= DataGenConstant.duration / DataGenConstant.sampleInterval + 1) {
                        return count;
                    }
                }

                if (eTime - t <= DataGenConstant.duration) {
                    return 0;
                }
            }
            else {
                ArrayList<Integer> temp_list = new ArrayList<Integer>();
                if (con_timeList.get(trajectory2.getTrajectoryId()) != null) {
                    temp_list = con_timeList.get(trajectory2.getTrajectoryId());
                }
                temp_list = addContactTime(temp_list, t);
                con_timeList.put(trajectory2.getTrajectoryId(), temp_list);
                if (t - Math.max(lastUnConTime.get(trajectory2.getTrajectoryId()) + DataGenConstant.sampleInterval, temp_list.get(0)) >= DataGenConstant.duration) {
//                    System.out.println("start verifying 1...");
                    int count = verify(Math.max(lastUnConTime.get(trajectory2.getTrajectoryId()) + DataGenConstant.sampleInterval, temp_list.get(0)), t, trajectory1, trajectory2, probabilityThreshold);
//                    System.out.println("pro: " + pro);
                    if (count >= DataGenConstant.duration / DataGenConstant.sampleInterval + 1) {
                        return count;
                    }
                }
            }
            t += DataGenConstant.sampleInterval;
        }

        return 0;
    }

    public static int verify(int sTime, int eTime, Trajectory trajectory1, Trajectory trajectory2, double probabilityThreshold) {
        int result = 0;
        int t = sTime;
        int uncontactTime = 0;
        int contactTime = -1;
        while (t <= eTime) {
//            System.out.println("t: " + t);
            if (pro_map.get(trajectory2.getTrajectoryId()) == null || pro_map.get(trajectory2.getTrajectoryId()).get(t) == null) {
//                System.out.println("uncertainty t: " + t);
                Uncertainty.findUncertainTra_forQuery(trajectory2, t);

//                System.out.println("found uncertain points........");
                ArrayList<TrajectoryPoint> traPointList1 = new ArrayList<TrajectoryPoint>();
                if (trajectory1.getTrajectory(t) != null) {
                    for (int traPointId1: trajectory1.getTrajectory(t)) {
                        TrajectoryPoint traPoint1 = IndoorSpace.iTraPoint.get(traPointId1);
                        traPointList1.add(traPoint1);
                    }
                }
                else {
                    traPointList1 = Uncertainty.preTrajectory.get(trajectory1.getTrajectoryId()).get(t);
                }



                ArrayList<TrajectoryPoint> traPointList2 = Uncertainty.preTrajectory.get(trajectory2.getTrajectoryId()).get(t);
//                System.out.println("traPointList1.size(): " + traPointList1.size());
//                System.out.println("traPointList2.size(): " + traPointList2.size());

                double probability = 0;
                for (TrajectoryPoint traPoint1: traPointList1) {
                    for (TrajectoryPoint traPoint2: traPointList2) {
//                        System.out.println("traPoint1: " + traPoint1.getTraPointId() + ", traPoint2: " + traPoint2.getTraPointId());
//                        System.out.println("traPoint1 parId: " + traPoint1.getParID() + ", traPoint2 parId: " + traPoint2.getParID());
                        if (traPoint1.getParID() != traPoint2.getParID()) {
//                            System.out.println("parId different");
                            continue;
                        }
//                        System.out.println("x,y: " + traPoint1.getX() + ", " + traPoint1.getY() + ", " + traPoint2.getX() + ", " + traPoint2.getY());
//                        System.out.println("distance: " + traPoint1.eDist(traPoint2));
                        if (traPoint1.eDist(traPoint2) > DataGenConstant.distanceThreshold) {
                            continue;
                        }
                        probability += traPoint1.getProbability() * traPoint2.getProbability();
                        if (probability >= probabilityThreshold) break;
//                        System.out.println("probability: " + probability);
                    }
                    if (probability >= probabilityThreshold) break;
                }

//

                HashMap<Integer, Double> temp_pro = new HashMap<Integer, Double>();
                if (pro_map.get(trajectory2.getTrajectoryId()) != null) {
                    temp_pro = pro_map.get(trajectory2.getTrajectoryId());
                }
                temp_pro.put(t, probability);
                pro_map.put(trajectory2.getTrajectoryId(), temp_pro);
//                trajectory2.setContactProbability(t, probability);

                if (probability < probabilityThreshold) {

                    lastUnConTime.put(trajectory2.getTrajectoryId(), t);

                    if (con_timeList.get(trajectory2.getTrajectoryId()) != null) {
                        con_timeList.put(trajectory2.getTrajectoryId(), null);
                    }

                    uncontactTime = t;
                    contactTime = -1;
                    if (eTime - uncontactTime < DataGenConstant.duration) {
                        result = 0;
                        return result;
                    }
                }
                else {
//                    System.out.println("contact time: " + contactTime);
                    if (contactTime == -1) {
                        contactTime = t;
                    }
                    else {
                        if (t - contactTime >= DataGenConstant.duration) {
                            if (t - contactTime > DataGenConstant.duration) {
                                contactTime = t - DataGenConstant.duration;
                            }
                            int count = 0;
                            for(int i = contactTime; i <= t; i += DataGenConstant.sampleInterval) {
//                                System.out.println("time: " + i + ", pro: " + pro_map.get(trajectory2.getTrajectoryId()).get(i));
                                if (pro_map.get(trajectory2.getTrajectoryId()).get(i) >= probabilityThreshold) {
                                    count++;
                                }
                                else {
                                    break;
                                }
                            }
                            result = count;
//                            System.out.println("result: " + result);
                            if (result >= DataGenConstant.duration / DataGenConstant.sampleInterval + 1) {
                                return result;
                            }
                        }
                    }
                }
            }
            else {
                if (pro_map.get(trajectory2.getTrajectoryId()).get(t) < probabilityThreshold) {
                    System.out.println("something wrong with the prpbability: Judge.verify()");
                }
//                System.out.println("t: " + t + "pro: " + Algo_ICQ.pro_map.get(trajectory2.getTrajectoryId()).get(t));
                if (contactTime == -1) {
                    contactTime = t;
                }
                else {
//                    System.out.println("contact time: " + contactTime);
                    if (t - contactTime >= DataGenConstant.duration) {
//                        System.out.println("meet duration....");
                        if (t - contactTime > DataGenConstant.duration) {
                            contactTime = t - DataGenConstant.duration;
                        }
                        int count = 0;
                        for(int i = contactTime; i <= t; i += DataGenConstant.sampleInterval) {
                            if (pro_map.get(trajectory2.getTrajectoryId()).get(i) >= probabilityThreshold) {
                                count++;
                            }
//                            System.out.println("temp pro: " + temp + ", t: " + i);
                        }

                        if (count >= DataGenConstant.duration / DataGenConstant.sampleInterval + 1) {
//                            System.out.println("temp: " + temp);
                            result = count;
                            return result;
                        }
                    }
                }
            }
            t += DataGenConstant.sampleInterval;
        }
        return result;

    }

    public static ArrayList<Integer> addContactTime(ArrayList<Integer> list, int t) {
        if (list.size() == 0) {
            list.add(t);
        }
        else if (t >= list.get(list.size() - 1)) {
            list.add(t);
        }
        else {
            ArrayList<Integer> temp = new ArrayList<Integer>();
            Boolean isAdded = false;
            for (int i = 0; i < list.size(); i++) {
                int time = list.get(i);
                if (time < t) {
                    temp.add(time);
                }
                else if (!isAdded){
                    temp.add(t);
                    temp.add(time);
                    isAdded = true;
                }
                else {
                    temp.add(time);
                }
            }
            list = temp;
        }
        return list;
    }
}
