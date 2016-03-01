import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Highest Priority First Non-Preemptive
 * @author CS 149 Group 4
 *
 */
public class HPFNonPreemptive
{
    private static final float MAX_AGE = 5;
    private static final float TIME_SLICE = 1;
    /**
     * Runs the algorithm on the collection of hopefully new processes
     * @param procs a collection of new processes
     * @param targetTime the time at which starved processes are purged
     * @param isAging true if the priorities should be 
     * changed based on a process's age since last changing priority
     * @return a string showing statistics about the run
     */
    public static String execute(Collection<Process> procs, float targetTime, boolean isAging)
    {
        LinkedList<Process> waitingProcessesQueue = new LinkedList<Process>();
        waitingProcessesQueue.addAll(procs);
        waitingProcessesQueue.sort(Scheduler.ARRIVAL_TIME_COMPARATOR);
        PriorityQueues readyProcessesQueue = new PriorityQueues(Process.MAX_PRIORITY);
        
        float currentTime = 0;
        ArrayList<String> timeline = new ArrayList<String>();
        boolean hasStarved = false;
        while(!readyProcessesQueue.isEmpty() || !waitingProcessesQueue.isEmpty())
        {
            
            switchProcess(readyProcessesQueue, currentTime);
            hasStarved = checkStarvedProcesses(procs, readyProcessesQueue, waitingProcessesQueue, currentTime, targetTime, hasStarved);
            if(!readyProcessesQueue.isEmpty() || !waitingProcessesQueue.isEmpty())
            {
                timeline.add(runTSlice(readyProcessesQueue, waitingProcessesQueue, currentTime, isAging));
                currentTime += TIME_SLICE;
            }
            //System.out.println(readyProcessesQueue.toString());
        }
        return Scheduler.formatPriorityOutput(procs, currentTime,timeline);
    }

    /**
     * Runs a process and waits for arriving processes for a defined timeslice
     * @param readyProcessesQueue the multi level queue of ready processes
     * @param waitingProcessesQueue the queue of arriving processes
     * @param currentTime the current time thus far
     * @param isAging true if the priorities should be 
     * changed based on a process's age since last changing priority
     * @return the name of the process that ran, if any
     */
    private static String runTSlice(PriorityQueues readyProcessesQueue,
            LinkedList<Process> waitingProcessesQueue, float currentTime, boolean isAging)
    {
        String ranProcess = "wait";
        if(!readyProcessesQueue.isEmpty())
        {
            readyProcessesQueue.runCurrProcess(TIME_SLICE, currentTime);
            ranProcess = readyProcessesQueue.getCurrProcess().getProcName();
        }
        if(isAging)
        {
            readyProcessesQueue.ageProcesses(TIME_SLICE);
            readyProcessesQueue.changePriority(MAX_AGE);
        }

        for(Process process: waitingProcessesQueue)
        {
            process.waitForArrival(TIME_SLICE);
            if(process.getArrivalTime() <= 0)
                readyProcessesQueue.add(process);
        }
        waitingProcessesQueue.removeIf((proc) -> proc.getArrivalTime() <= 0);
        
        return ranProcess;
    }

    /**
     * Purges the queues and original collection of starved processes if necessary
     * @param procs the original collection of processes
     * @param readyProcessesQueue the multi-level queue of ready processes
     * @param waitingProcessesQueue the queue of arriving processes
     * @param currentTime the current time thus far
     * @param targetTime the time at which to purge starved processes
     * @param hasStarved true if a purge already occurred
     * @return true if a purge occurs, false otherwise
     */
    private static boolean checkStarvedProcesses(Collection<Process> procs,
            PriorityQueues readyProcessesQueue, LinkedList<Process> waitingProcessesQueue,
            float currentTime, float targetTime, boolean hasStarved)
    {
        if(currentTime >= targetTime && !hasStarved)
        {  
            readyProcessesQueue.removeStarved();
            waitingProcessesQueue.clear();
            procs.removeIf((proc) -> (proc.getStartTime() < 0));
        }
        return hasStarved;
    }

    /**
     * A method that removes finished processes and/or performs a process switch,
     * or neither if these actions are unneeded
     * @param readyProcessesQueue the multi-level queue of ready processes
     * @param currentTime the current time thus far
     */
    private static void switchProcess(PriorityQueues readyProcessesQueue, float currentTime)
    {
        
        if(!readyProcessesQueue.isEmpty()  && readyProcessesQueue.getCurrProcess().getRunTime() <= 0)
        {
            readyProcessesQueue.removeCurrentProc(currentTime);
        }
    }
}
