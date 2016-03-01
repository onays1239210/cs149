import java.util.ArrayList;
import java.util.Random;

/**
 * PROCESS Design.
 * @author CS 149 GROUP 4
 */
public class Process
{
    private final float initialArrivalTime;
    private final float initialRunTime;
    
    private final int initialPriority;
    
    private float runTime;
    private float arrivalTime;
    private float startTime;
    private float finishTime;
    private float allotedTime;
    private float ellapsedTime;
    
    private int priority;
    
    private String processName;
    
    public final static float MAX_ARRIVAL_TIME = 99;
    public final static float MIN_ARRIVAL_TIME = 0;
    public final static float MAX_RUN_TIME = 10;
    public final static float MIN_RUN_TIME = 0.1f;
    
    public final static int MAX_PRIORITY = 4;
    
    /**
     * Constructs Process object.
     * @param arrivalTime Time of new process.
     * @param runTime Time when the process is run.
     * @param priority Priority of the process.
     * @param processName Process name.
     */
    public Process(float arrivalTime, float runTime, int priority, String processName)
    {
        this.arrivalTime = arrivalTime;
        this.runTime = runTime;
        this.priority = priority;
        this.processName = processName;
        
        initialArrivalTime = arrivalTime;
        initialRunTime = runTime;
        initialPriority = priority;
        startTime = -1;
        finishTime = -1;
        allotedTime = 0;
        ellapsedTime = 0;
    }
    
    /**
     * Get time of new process.
     * @return Arrival time.
     */
    public float getArrivalTime()
    {
        return arrivalTime;
    }
    
    /**
     * Get remaining time of running process
     * @return Remaining time.
     */
    public float getRunTime()
    {
        return runTime;
    }
    
    /**
     * Get priority of process.
     * @return Priority.
     */
    public int getPriority()
    {
        return priority;
    }
    
    /**
     * Get the process name.
     * @return Process name.
     */
    public String getProcName()
    {
        return processName;
    }
    
    /**
     * Get processe's start time.
     * @return Start time.
     */
    public float getStartTime()
    {
        return startTime;
    }
    
    /**
     * Get initial arrival time of the process
     * @return Arrival time.
     */
    public float getInitArrivalTime()
    {
        return initialArrivalTime;
    }
    
    /**
     * Get expected run time.
     * @return Expected time.
     */
    public float getInitRunTime()
    {
        return initialRunTime;
    }
    
    /**
     * Get the initial priority
     * @return Initial Priority.
     */
    public int getInitPriority()
    {
        return initialPriority;
    }
    
    /**
     * Get elapsed time.
     * @return elapsed time.
     */
    public float getAge()
    {
        return ellapsedTime;
    }
    
    /**
     * Get allotted time before switching process
     * @return Alloted time
     */
    public float getAllotedTime()
    {
        return allotedTime;
    }
    
    /**
     * Get finishing time of the process
     * @return Finishing time
     */
    public float getFinishTime()
    {
        return finishTime;
    }
    
    /**
     * Set priority of the thread
     * @param Priority of thread.
     */
    public void setPriority(int priority)
    {
        this.priority = priority;
    }
    
    /**
     * Sets start time of the process
     * @param startTime Start time.
     */
    public void setStartTime(float startTime)
    {
        this.startTime = startTime;
    }
    
    /**
     * Set finishing time of the process
     * @param finishTime Finishing time
     */
    public void setFinishTime(float finishTime)
    {
        this.finishTime = finishTime;
    }
    
    /**
     * Set elapsed time of process
     * @param ellapsedTime Ellapsed time.
     */
    public void setAge(float ellapsedTime)
    {
        this.ellapsedTime = ellapsedTime;
    }
    
    /**
     * Set alloted time before switching process.
     * @param allotedTime Alloted time.
     */
    public void setAllotedTime(float allotedTime)
    {
        this.allotedTime = allotedTime;
    }
    
    /**
     * Returns the initial arrival time, expected run time, and priority of the process
     * @return a String with the initial information of the process.
     */
    public String initInfo()
    {
        String initialInformation = "Process Name: " + processName;
        initialInformation += "\nArrival time: " + initialArrivalTime;
        initialInformation += "\nEstimated runtime: " + initialRunTime;
        initialInformation += "\nPriority: " + initialPriority;
        
        return initialInformation;
    }
    
    /**
     * Generates a process with random arrival time, run time, and priority.
     * @param processName the name of the process
     * @return the new process
     */
    public static Process GenRandProcess(String processName)
    {
        Random random = new Random();
        float arrivalTime = MIN_ARRIVAL_TIME + (MAX_ARRIVAL_TIME - MIN_ARRIVAL_TIME) * random.nextFloat();
        float runTime = MIN_RUN_TIME + (MAX_RUN_TIME - MIN_RUN_TIME) * random.nextFloat();
        int priority = 1 + random.nextInt(MAX_PRIORITY);
        
        return new Process(arrivalTime, runTime, priority, processName);
    }
    
    /**
     * Returns several randomly generated processes
     * @param number the number of desired processes
     * @return ArrayList containing the processes
     */
    public static ArrayList<Process> GenMultiple(int number)
    {
        ArrayList<Process> processes = new ArrayList<Process>();
        for(int i = 1; i <= number; i++)
        {
            processes.add(GenRandProcess("P" + i));
        }
        return processes;
    }
    
    
    /**
     * Decrements the alloted time based on the time that passed
     * @param timeSlice the elapsed time
     */
    public void decAllotedTime(float timeSlice)
    {
        if(timeSlice > allotedTime)
            allotedTime = 0;
        else
            allotedTime -= timeSlice;
    }
    
    /**
     * Increases the age of the process by the given timeslice
     * @param timeSlice the time elapsed
     */
    public void incAge(float timeSlice)
    {
        ellapsedTime += timeSlice;
    }
    
    /**
     * Decreases priority (increases value.).
     */
    public void decPriority()
    {
        if(priority > 1)
        {
            priority--;
        }
    }
    
    /**
     * Updates time of new process.
     * @param waitTime Wait time measured in Quanta.
     */
    public void waitForArrival(float waitTime)
    {
        if(waitTime > arrivalTime)
        {
            arrivalTime = 0;
        }
        else
        {
            arrivalTime -= waitTime;
        }
    }
    
    /**
     * Updates run time.
     * @param elapsedTime Remaining time.
     */
    public void run(float elapsedTime)
    {
        if(elapsedTime > runTime)
        {
            runTime = 0;
        }
        else
        {
            runTime -= elapsedTime;
        }
        decAllotedTime(elapsedTime);
    }
}