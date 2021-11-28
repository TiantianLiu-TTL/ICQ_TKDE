package indoor_entitity;

import utilities.DataGenConstant;

import java.util.ArrayList;

/**
 * @author Tiantian Liu
 */

public class TrajectoryPoint extends Point {
    private int traPointId;
    private double x;	// x coordinate
    private double y;	// y coordinate
    private int mFloor;    // belonging floor
    private int mType;
    private int trjectoryID;
    private double probability;
    private int t;
    private int parID;

    /**
     * Constructor
     *
     * @param x
     * @param y
     * @param mFloor
     * @param t
     */
    public TrajectoryPoint(double x, double y, int mFloor, int t) {
        super(x, y, mFloor);
        this.traPointId = DataGenConstant.mID_TrajectoryPoint++;
        this.t = t;
    }

    public TrajectoryPoint(double x, double y, int mFloor, int t, int Id) {
        super(x, y, mFloor);
        this.traPointId = Id++;
        this.t = t;
    }

    /**
     * Constructor
     *
     * @param point
     * @param t
     */
    public TrajectoryPoint(Point point, int t) {
        super(point.getX(), point.getY(), point.getmFloor());
        this.traPointId = DataGenConstant.mID_TrajectoryPoint++;
        this.t = t;
    }
    public TrajectoryPoint(Point point, int t, int Id) {
        super(point.getX(), point.getY(), point.getmFloor());
        this.traPointId = Id++;
        this.t = t;
    }

    /**
     * Constructor
     *
     * @param x
     * @param y
     * @param mFloor
     * @param pro
     * @param t
     */
    public TrajectoryPoint(double x, double y, int mFloor, double pro, int t) {
        super(x, y, mFloor);
        this.traPointId = DataGenConstant.mID_TrajectoryPoint++;
        this.probability = pro;
        this.t = t;
    }

    public int getTraPointId() {
        return this.traPointId;
    }
    public void setProbability(double pro) {
        this.probability = pro;
    }

    public double getProbability() {
        return this.probability;
    }

    public void setTime(int t) {
        this.t = t;
    }

    public int getT() {
        return this.t;
    }

    public void setTrajectoryID(int trajectoryID) {
        this.trjectoryID = trajectoryID;
    }

    public int getTrajectoryID() {
        return  this.trjectoryID;
    }

    public void setParID(int parID) {
        this.parID = parID;
    }

    public int getParID() {
        return this.parID;
    }

}
