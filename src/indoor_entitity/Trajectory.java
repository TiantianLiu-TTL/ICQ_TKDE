package indoor_entitity;

import datagenerate.DataGen;
import utilities.Constant;
import utilities.DataGenConstant;

import java.util.*;

/**
 * Trajectory in indoor spaces
 * @author Tiantian Liu
 */
public class Trajectory {
    private int trajectoryId;
    private int objectId;
    private HashMap<Integer, ArrayList<Integer>> trajectory = new HashMap<>(); // the trajectory of an object
    private HashMap<Integer, ArrayList<Integer>> preTrajectory = new HashMap<>(); // the predicted trajectory of an object
    private ArrayList<Integer> timeList = new ArrayList<>();
//    private ArrayList<Integer> contactTimeList = new ArrayList<>();
//    private int latestUncontactTime = -1000;
//    private HashMap<Integer, Double> contactProbability = new HashMap<>(); // key: time; value: probability;
    private HashMap<Integer, ArrayList<Integer>> parList = new HashMap<>(); // partition list


    /**
     * construct trajectory
     * @param objectId
     */
    public Trajectory(int objectId) {
        this.trajectoryId = DataGenConstant.mID_trajectory++;
        this.objectId = objectId;

    }

    /**
     * construct trajectory
     */
    public Trajectory() {
        this.trajectoryId = DataGenConstant.mID_trajectory++;
    }


    public int getTrajectoryId() { return  this.trajectoryId;}

    public int getObjectId() {
        return this.objectId;
    }

    public void setObjectId(int objectId) { this.objectId = objectId;}

    /**
     * add trajectory point for the trajectory
     * @param traPointId
     */
    public void addTrajectory(int t, int traPointId) {

        if (this.trajectory.get(t) == null) {
            ArrayList<Integer> tra = new ArrayList<>();
            tra.add(traPointId);
            this.trajectory.put(t, tra);
        }
        else {
            ArrayList<Integer> tra = this.trajectory.get(t);
            tra.add(traPointId);
            this.trajectory.replace(t, tra);
        }

    }

    /**
     * get trajectory
     */
    public HashMap<Integer, ArrayList<Integer>> getTrajectory() {
        return this.trajectory;
    }

    public ArrayList<Integer> getTrajectory(int t) {
        return this.trajectory.get(t);
    }

    /**
     * add trajectory point for the trajectory
     * @param parId
     */
    public void addPartition(int t, int parId) {

        if (this.parList.get(t) == null) {
            ArrayList<Integer> pars = new ArrayList<>();
            pars.add(parId);
            this.parList.put(t, pars);
        }
        else {
            ArrayList<Integer> pars = this.parList.get(t);
            pars.add(parId);
            this.parList.replace(t, pars);
        }

    }

    /**
     * get trajectory
     */
    public HashMap<Integer, ArrayList<Integer>> getParList() {
        return this.parList;
    }

    public ArrayList<Integer> getParList(int t) {
        return this.parList.get(t);
    }


    /**
     * add pre trajectory for the object
     * @param preTraPointId
     */
    public void addPreTrajectory(int t, int preTraPointId) {

        if (this.preTrajectory.get(t) == null) {
            ArrayList<Integer> tra = new ArrayList<>();
            tra.add(preTraPointId);
            this.preTrajectory.put(t, tra);
        }
        else {
            ArrayList<Integer> tra = this.preTrajectory.get(t);
            tra.add(preTraPointId);
            this.preTrajectory.replace(t, tra);
        }

    }

    /**
     * get trajectory
     */
    public HashMap<Integer, ArrayList<Integer>> getPreTrajectory() {
        return this.preTrajectory;
    }

//    public String trajectoryToString() {
//        String result = "";
//
//        for (int i = 0; i < this.trajectory.size(); i++) {
//            TrajectoryPoint info = this.trajectory.get(i);
//            result += info.getObjectID() + "," + info.getX() + "," + info.getY() + "," + info.getmFloor() + "," + info.getT()  + "," + info.getProbability() + "\t";
//        }
//        return result;
//    }

    public void addTime(int t) { this.timeList.add(t); }
    public ArrayList<Integer> getTime() { return this.timeList; }
    public int getMaxTime() {
        if (this.timeList.size() > 0) {
            return this.timeList.get(timeList.size() - 1);
        }
        else {
            return -1;
        }
    }
    public int getMinTime() {
        if (this.timeList.size() > 0) {
            return this.timeList.get(0);
        }
        else {
            return -1;
        }
    }

//    public void addContactTime(int t) {
//        if (this.contactTimeList.size() == 0) {
//            this.contactTimeList.add(t);
//        }
//        else if (t >= this.contactTimeList.get(contactTimeList.size() - 1)) {
//            this.contactTimeList.add(t);
//        }
//        else {
//            ArrayList<Integer> temp = new ArrayList<>();
//            Boolean isAdded = false;
//            for (int i = 0; i < this.contactTimeList.size(); i++) {
//                int time = this.contactTimeList.get(i);
//                if (time < t) {
//                    temp.add(time);
//                }
//                else if (!isAdded){
//                    temp.add(t);
//                    temp.add(time);
//                    isAdded = true;
//                }
//                else {
//                    temp.add(time);
//                }
//            }
//            this.contactTimeList = temp;
//        }
//
//    }

//    public ArrayList<Integer> getContactTimeList() {
//        return this.contactTimeList;
//    }
//
//    public void clearContactTimeList() {
//        this.contactTimeList.clear();
//    }
//
//    public void setLatestUncontactTime(int t) {
//        this.latestUncontactTime = t;
//    }
//
//    public int getLatestUncontactTime() {
//        return this.latestUncontactTime;
//    }
//
//    public void setContactProbability(int t, double probability) {
//        this.contactProbability.put(t, probability);
//    }
//
//    public double getContactProbability(int t) {
//
//        if (this.contactProbability.get(t) == null) return -1;
//        else return this.contactProbability.get(t);
//    }



}
