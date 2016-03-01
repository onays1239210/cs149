
import java.util.Comparator;
import java.util.LinkedList;

/**
 * A queue of ready-to-run processes
 *
 */
public class ReadyToRunQueue
{
    private Process currProcess;
    private LinkedList<Process> ready_queue;
    
    /**
     * Initialize a new ReadyToRunQueue
     */
    public ReadyToRunQueue()
    {
        ready_queue = new LinkedList<Process>();
        currProcess = null;
    }
    
    /**
     * New process added to queue
     * @param proc add process
     */
    public void add(Process proc)
    {
       ready_queue.addLast(proc);
        if(currProcess == null)
            updateCurrentProc();
    }
    
    /**
     * Current process is removed and updated to finish time
     * @param currentTime the time process is removed
     * @return the process that's removed
     */
    public Process removeCurrentProc(float currentTime)
    {
        if(currProcess == null)
            updateCurrentProc();
        Process removedProc = currProcess;
        removedProc.setFinishTime(currentTime);
        currProcess = null;
        updateCurrentProc();
        return removedProc;
    }

    /**
     * Currently running process moved to end of queue, if there's one
     * New Currently running process is updated to be the real one.
     *
     */
    public void updateCurrentProc()
    {
        if(currProcess != null)
        {
            add(currProcess);
        }
        if(!ready_queue.isEmpty())
            currProcess = ready_queue.removeFirst();
        else
            currProcess = null;
    }

    
    /**
     * process in queue is sorted based on comparator
     * @param comp the comparator
     */
    public void sort(Comparator<? super Process> comp)
    {
        ready_queue.sort(comp);
    }
    
    
    /**
     * New process added
     * @param currProcess the new process
     */
    public void addAsCurrentProc(Process currProcess)
    {
        if(currProcess != null)
            add(this.currProcess);
        this.currProcess = currProcess;
    }

    
    /**
     * currently running process returned
     * @return the current process
     */
    public Process getCurrProcess()
    {
        if(currProcess == null)
            updateCurrentProc();
        return currProcess;
    }
    
    
    
    /**
     * Checks if the queue is empty
     * @return true if there are no processes running or in the queue
     */
    public boolean isEmpty()
    {
        return ready_queue.isEmpty() && currProcess == null;
    }
    
    
    /**
     * Current process for time runs
     * If the process never ran before, its start time will be updated
     * @param timeSlice the time to run the process
     * @param currentTime the current time
     */
    public void runCurrProcess(float timeSlice, float currentTime)
    {
        if(currProcess == null)
            updateCurrentProc();
        currProcess.run(timeSlice);
        if(currProcess.getStartTime() < 0)
            currProcess.setStartTime(currentTime);
    }
    
    /**
     * processes that were never started are removed
     */
    public void removeStarved()
    {
        ready_queue.removeIf((proc) -> (proc.getStartTime() < 0));
        if(currProcess != null && currProcess.getStartTime() < 0)
        {
            currProcess = null;
            updateCurrentProc();
        }
    }
}
