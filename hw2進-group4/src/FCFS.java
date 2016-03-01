import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collection;


/**
 * First Come First Serve
 * @author CS 149 Group 4
 */
public class FCFS
{
    private static final float TIME_SLICE = 1;
    
    /**
     * Runs the First Come First Serve algorithm 
     * @param processes  collection of processes
     * @param t_Time  time at which starved processes are terminated
     * @return String output showing results of the FCFS algorithm run
     */
    public static String execute(Collection<Process> processes, float t_Time)
    {
        LinkedList<Process> watingQueue = new LinkedList<Process>();
        ReadyToRunQueue rQueue = new ReadyToRunQueue();
        watingQueue.addAll(processes);
        watingQueue.sort(Scheduler.ARRIVAL_TIME_COMPARATOR); //sort by arrival time
        float curr_Time = 0;
        ArrayList<String> history = new ArrayList<String>(); 
        boolean hasStarved = false; 
        while(!watingQueue.isEmpty() || !rQueue.isEmpty())
        {
            switchProcess(rQueue, curr_Time);
            hasStarved = checkStarvedProcesses(processes, rQueue, watingQueue, curr_Time, t_Time, hasStarved);
            if(!watingQueue.isEmpty() || !rQueue.isEmpty())
            {
                history.add(runTSlice(rQueue, watingQueue, curr_Time));
                curr_Time += TIME_SLICE; 
            }
        }
        return Scheduler.formatOutput(processes,curr_Time,history);
    }
        /**
     * removes the queues and the original collection of the starved processes if needed
     * @param processes the original collection of processes
     * @param rQueue the queue of ready processes
     * @param watingQueue the queue of arriving processes
     * @param curr_Time the current time thus far
     * @param t_Time the time at which to purge starved processes
     * @param hasStarved true if a purge already occurred
     * @return true if a purge occurs, false otherwise
     */
    private static boolean checkStarvedProcesses(Collection<Process> processes, ReadyToRunQueue rQueue, 
            LinkedList<Process> watingQueue, float curr_Time, 
            float t_Time, boolean hasStarved)
    {
        if(curr_Time >= t_Time && !hasStarved)
        {
            rQueue.removeStarved();
            watingQueue.clear(); 
            processes.removeIf((proc) -> (proc.getStartTime() < 0));
            hasStarved = true;
        }
        return hasStarved;
    }
    
    /**
     * This method runs a process and waits for arriving processes for a timeslice
     * @param rQueue the queue of ready processes
     * @param watingQueue the queue of arriving processes
     * @param curr_Time the current time thus far
     * @return the name of the process that ran, if any
     */
    private static String runTSlice(ReadyToRunQueue rQueue, LinkedList<Process> watingQueue, float curr_Time)
    {
        String prevProcess = "waiting";
        if(!rQueue.isEmpty())
        {
            rQueue.runCurrProcess(TIME_SLICE, curr_Time);
            prevProcess = rQueue.getCurrProcess().getProcName();
        }
        for(Process proc: watingQueue)
        {
            proc.waitForArrival(TIME_SLICE);
            if(proc.getArrivalTime() <= 0)
                rQueue.add(proc);
        }
        watingQueue.removeIf((proc) -> proc.getArrivalTime() <= 0);
        return prevProcess;
    }
    
    
      /**
     * This method removes the finished processes and performs a process switch when needed
     * @param rQueue  queue for the ready processes
     * @param curr_Time  current time 
     */
    private static void switchProcess(ReadyToRunQueue rQueue, float curr_Time)
    {
        if(!rQueue.isEmpty()  && rQueue.getCurrProcess().getRunTime() <= 0)
        {
            rQueue.removeCurrentProc(curr_Time);
        }
    }
}
