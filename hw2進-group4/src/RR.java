import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Round Robin Algorithm
 *
 */
public class RR
{
    private static final float maxTimeGiven = 1;
    private static final float timeSpace = 1;
    
    /**
     * Waits for arriving processes for that timeline
     * @return the name of the process that ran, if any
     */
    private static String runTSlice(ReadyToRunQueue readyQueue, LinkedList<Process> waitQueue, float currentTime)
    {
        String ranProcess = "wait";
        if(!readyQueue.isEmpty())
        {
            readyQueue.runCurrProcess(timeSpace, currentTime);
            ranProcess = readyQueue.getCurrProcess().getProcName();
        }
        
        for(Process proc: waitQueue)
        {
            proc.waitForArrival(timeSpace);
            if(proc.getArrivalTime() <= 0)
            {
                readyQueue.add(proc);
                proc.setAllotedTime(maxTimeGiven);
            }
        }
        waitQueue.removeIf((proc) -> proc.getArrivalTime() <= 0);
        
        return ranProcess;
    }
    

    /**
     * Remove finished processes
     * @param readyQueue ready processes queue
     * @param currentTime the current time
     */
    private static void switchProcess(ReadyToRunQueue readyQueue, float currentTime)
    {
        if(!readyQueue.isEmpty() && readyQueue.getCurrProcess().getRunTime() <= 0)
        {
            readyQueue.removeCurrentProc(currentTime);
            if(!readyQueue.isEmpty())
                readyQueue.getCurrProcess().setAllotedTime(maxTimeGiven);
        }
        else if(!readyQueue.isEmpty() && readyQueue.getCurrProcess().getAllotedTime() <= 0)
        {
            readyQueue.updateCurrentProc();
            readyQueue.getCurrProcess().setAllotedTime(maxTimeGiven);
        }
        
    }
    
    /**
     * the queues of starved processes are purged
     */
    private static boolean checkStarvedProcesses(Collection<Process> procs, ReadyToRunQueue readyQueue, LinkedList<Process> waitQueue, float currentTime, float targetTime, boolean hasStarved)
    {
        if(currentTime >= targetTime && !hasStarved)
        {
            readyQueue.removeStarved();
            waitQueue.clear();
            procs.removeIf((proc) -> (proc.getStartTime() < 0));
            hasStarved = true;
        }
        return hasStarved;
    }

    
    /**
     * Runs the algorithm on the new collection of processes
     * @return a string showing statistics
     */
    public static String execute(Collection<Process> procs, float targetTime)
    {
        ReadyToRunQueue readyQueue = new ReadyToRunQueue();
        LinkedList<Process> waitQueue = new LinkedList<Process>();
        waitQueue.addAll(procs);
        waitQueue.sort(Scheduler.ARRIVAL_TIME_COMPARATOR);
        
        float currentTime = 0;
        ArrayList<String> timeline = new ArrayList<String>();
        boolean hasStarved = false;
        while(!readyQueue.isEmpty() || !waitQueue.isEmpty())
        {
            switchProcess(readyQueue, currentTime);
            hasStarved = checkStarvedProcesses(procs, readyQueue, waitQueue, currentTime, targetTime, hasStarved);
            if(!readyQueue.isEmpty() || !waitQueue.isEmpty())
            {
                timeline.add(runTSlice(readyQueue, waitQueue, currentTime));
                currentTime += timeSpace;
            }
        }
        return Scheduler.formatOutput(procs, currentTime,timeline);
    }
    

}
