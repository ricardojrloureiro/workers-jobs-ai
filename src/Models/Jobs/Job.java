package Models.Jobs;

import java.io.Serializable;

public class Job implements Serializable {

    private int mTimeToComplete; // time to finish the job
    //private int mDeadLine; // time left to select the worker
    public int[] requiredTools;
    public static float PENALTY = 71;

    public Job(int mTimeToComplete, int[] requiredTools) {
        this.mTimeToComplete = mTimeToComplete;
        this.requiredTools = requiredTools;
    }

    public int getTimeToComplete() {
        return mTimeToComplete;
    }

    public int[] getRequiredTools() {
        return requiredTools;
    }
}
