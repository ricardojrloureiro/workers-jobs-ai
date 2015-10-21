package Models.Jobs;

public class Job {

    private int mTimeToComplete; // time to finish the job
    private int mDeadLine; // time left to select the worker
    public static float PENALTY = 71;

    public Job(int mTimeToComplete) {
        this.mTimeToComplete = mTimeToComplete;
    }
}
