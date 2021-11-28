package indoor_entitity;

import utilities.DataGenConstant;

import java.util.ArrayList;

/**
 * Indoor Object
 * @author Tiantian Liu
 */

public class IndoorObject {
    private int objectId;
    private ArrayList<Integer> trajectorys = new ArrayList<>(); // trajectoryIds

    public IndoorObject() {
        this.objectId = DataGenConstant.mID_Object++;
    }

    public int getObjectId() { return this.objectId; }

    public void addTrajectory(int trajectoryId) {
        this.trajectorys.add(trajectoryId);
    }
    public ArrayList<Integer> getTrajectorys() { return this.trajectorys; }
}
