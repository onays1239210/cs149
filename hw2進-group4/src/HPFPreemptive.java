import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Highest-Priority First Preemptive
 * CS149 Group 4
 */
public class HPFPreemptive
{
	private static final float MAX_ALLOTED_TIME = 1;
	private static final float TIME_SLICE = 1;
        private static final float MAX_AGE = 5;
    
    /**
     * Runs the High Priority First Preemptive Algorithm
     * @param processes a collection of new processes
     * @param removeTime the time of removal of starved processes
     * @param aging true if the priorities change based on process age
     * @return String of the result of the execution of the algorithm
     */
    public static String execute(Collection<Process> processes, float removeTime, boolean aging)
    {
        LinkedList<Process> waitQueue = new LinkedList<Process>();
        waitQueue.addAll(processes);
        waitQueue.sort(Scheduler.ARRIVAL_TIME_COMPARATOR);
        PriorityQueues ready = new PriorityQueues(Process.MAX_PRIORITY);
        
        ArrayList<String> timeLine = new ArrayList<String>();
        boolean starved = false;
        float time = 0;
        
        while(!ready.isEmpty() || !waitQueue.isEmpty())
        {
            switchProcess(ready, time);
            starved = checkStarvedProcesses(processes, ready, waitQueue, time, removeTime, starved);
            if(!ready.isEmpty() || !waitQueue.isEmpty())
            {
                timeLine.add(runTSlice(ready, waitQueue, time, aging));
                time = time + TIME_SLICE;
            }
        }
        return Scheduler.formatPriorityOutput(processes, time,timeLine);
    }

    /**
     * Runs a process while waiting for incoming processes
     * @param ready the queue of process that are ready
     * @param queue the queue of arriving processes
     * @param time the current time
     * @param aging true if the priorities change based on process' age
     * @return the name of the process the was ran
     */
    private static String runTSlice(PriorityQueues ready, LinkedList<Process> queue, float time, boolean aging)
    {
        String process = "waiting";
        if(!ready.isEmpty())
        {
            ready.runCurrProcess(TIME_SLICE, time);
            process = ready.getCurrProcess().getProcName();
        }
        if(aging)
        {
            ready.ageProcesses(TIME_SLICE);
            ready.changePriority(MAX_AGE);
        }

        for(Process proc: queue)
        {
            proc.waitForArrival(TIME_SLICE);
            if(proc.getArrivalTime() <= 0)
            {
                ready.add(proc);
                proc.setAllotedTime(MAX_ALLOTED_TIME);
            }
        }
        queue.removeIf((proc) -> proc.getArrivalTime() <= 0);
        return process;
    }

    /**
     * This method determines if a eviction will happen
     * @param processes collection of processes
     * @param ready  queue of ready processes
     * @param wait queue of waiting processes
     * @param time  current time
     * @param removeTime  time of removal of starved processes
     * @param starved returns true if there has been a eviction of process
     * @return true if there is a eviction
     */
    private static boolean checkStarvedProcesses(Collection<Process> processes,
            PriorityQueues ready, LinkedList<Process> wait,
            float time, float removeTime, boolean starved)
    {
        if(!starved && time >= removeTime)
        {  
            ready.removeStarved();
            wait.clear();
            processes.removeIf((proc) -> (proc.getStartTime() < 0));
        }
        return starved;
    }

    /**
     * This method removes finished processes and switches the processes if needed
     * @param ready the queue of process that are ready
     * @param time  current time
     */
    private static void switchProcess(PriorityQueues ready, float time)
    {
        
      if(!ready.isEmpty()  && ready.getCurrProcess().getRunTime() <= 0)
        {
            ready.removeCurrentProc(time);
        }
        
    }
}
