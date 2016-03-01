import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collection;


/**
 * This is the algorithm that handles the Shortest Job First (non-preemptive). 
 *
 */
public class SJF
{
    private static final float DIVIDE_TIME = 1;
    
    /**
     * This method executes the Shortest Job First algorithm on new processes entered. 
     * @param processes these are the new processes
     * @param targetT the time that the starved processes are removed from queue
     * @return displays the stats on the SJF run 
     */

    public static String execute(Collection<Process> processes, float targetT)
    {
        ReadyToRunQueue readyQueue = new ReadyToRunQueue();
        ArrayList<String> timeline = new ArrayList<String>();
        LinkedList<Process> waitQueue = new LinkedList<Process>();
        float currentTime = 0;
        boolean hasStarved = false;
        
        waitQueue.addAll(processes);
        waitQueue.sort(Scheduler.ARRIVAL_TIME_COMPARATOR);
        
        
        while(!readyQueue.isEmpty() || !waitQueue.isEmpty())
        {
            switchProcess(readyQueue, currentTime);
            hasStarved = checkStarvedProcesses(processes, readyQueue, waitQueue, currentTime, targetT, hasStarved);
            if(!readyQueue.isEmpty() || !waitQueue.isEmpty())
            {
                timeline.add(runTSlice(readyQueue, waitQueue, currentTime));
                currentTime += DIVIDE_TIME;
            }
        }
        return Scheduler.formatOutput(processes,currentTime,timeline);
    }



    /**
     * This method either removes the finished processes, switches the processes,
     * or skips any action if it is not required. 
     * @param readyQueue the queue of ready processes
     * @param currentTime the time up to this point of running the processes
     */
    private static void switchProcess(ReadyToRunQueue readyQueue, float currentTime)
    {
        if(!readyQueue.isEmpty() && readyQueue.getCurrProcess().getRunTime() <= 0)
        {
            //This sort ensures that the next running process has the shortest runtime
            readyQueue.sort(Scheduler.RUN_TIME_COMPARATOR);
            readyQueue.removeCurrentProc(currentTime);     
        }
        
    }



    /**
     * This method initially checks if any of the processes are starved and then 
     * removes the queues and checks initial collection if they are.
     * @param processes the initial collection of processes
     * @param readyQueue the queue of ready processes
     * @param waitQueue the queue of incoming processes
     * @param currentTime the time up to this point of running the processes
     * @param targetT the time that the starved processes are removed from queue
     * @param hasStarved if process removed, returns true
     * @return true if a process removed, false otherwise
     */
    private static boolean checkStarvedProcesses(Collection<Process> processes, ReadyToRunQueue readyQueue, LinkedList<Process> waitQueue, float currentTime, float targetT, boolean hasStarved)
    {
        if(currentTime >= targetT && !hasStarved)
        {
            readyQueue.removeStarved();
            waitQueue.clear();
            processes.removeIf((proc) -> (proc.getStartTime() < 0));
            hasStarved = true;
        }
        return hasStarved;
    }

    
    /**
     * This method runs a process through the SJF algorithm timeslice 
     *along with waiting for incoming processes. 
     * @param readyQueue the queue of ready processes
     * @param waitQueue the queue of incoming processes
     * @param currentTime the time up to this point of running the processes
     * @return all the processes that ran 
     */
    private static String runTSlice(ReadyToRunQueue readyQueue, LinkedList<Process> waitQueue, float currentTime)
    {
        String ranProcess = "wait";
        
        if(!readyQueue.isEmpty())
        {
            readyQueue.runCurrProcess(DIVIDE_TIME, currentTime);
            ranProcess = readyQueue.getCurrProcess().getProcName();
        }
        
        for(Process proc: waitQueue)
        {
            proc.waitForArrival(DIVIDE_TIME);
            if(proc.getArrivalTime() <= 0)
                readyQueue.add(proc);
        }
        waitQueue.removeIf((proc) -> proc.getArrivalTime() <= 0);
        
        return ranProcess;
    }
    

    
}
