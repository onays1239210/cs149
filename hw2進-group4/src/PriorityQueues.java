import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Class for the structure and function of PriorityQueues for Project 2
 * CS149 Group 4
 *
 */
public class PriorityQueues
{
	private Process process;
    private ArrayList<LinkedList<Process>> processQueue;
    
    /**
     * Initializes LinkedList of priority queues
     * @param max
     */
    public PriorityQueues(int max)
    {
        processQueue = new ArrayList<LinkedList<Process>>();
        for(int i = 0; i < max; i++)
        {
            processQueue.add(new LinkedList<Process>());
        }
        process = null;
    }
    
    /**
     * Adds a new process to the queue
     * @param proc new process
     */
    public void add(Process proc)
    {
        proc.setAge(0);
        processQueue.get(proc.getPriority() - 1).addLast(proc);
        if(process == null)
            updateCurrentProc();
    }
    
    /**
     * Changes priority of the processes
     * @param max the age to change priority
     */
    public void changePriority(float max)
    {
        if(!isEmpty())
        {
            for(int i = 1; i < processQueue.size(); i++)
            {
                LinkedList<Process> currentQueue = processQueue.get(i);
                final int priority = i;
                for(Process proc: currentQueue)
                {
                    if(proc.getAge() >= max)
                    {
                        proc.setPriority(priority);
                        add(proc);
                    }
                }
                currentQueue.removeIf((proc) -> proc.getPriority() == priority);
            }
            if(process != null)
            {
                if(process.getAge() >= max)
                {
                    process.decPriority();
                }
            }
        }
    }
    
    /**
     * Returns current process
     * @return current process
     */
    public Process getCurrProcess()
    {  
        if(process == null)
            updateCurrentProc();
        return process;
    }
    
    /**
     * Increases age of every process within timeslice
     * @param time the timeslice
     */
    public void ageProcesses(float time)
    {
        for(LinkedList<Process> queue: processQueue)
        {
            for(Process proc: queue)
            {
                proc.incAge(time);
            }
        }
        if(process != null)
        {
            process.incAge(time);
        }
    }
    
    /**
     * Removes the current process
     * @param time time of completion
     * @return removed process
     */
    public Process removeCurrentProc(float time)
    {
        if(process == null)
            updateCurrentProc();
        Process removedProcess = process;
        removedProcess.setFinishTime(time);
        process = null;
        updateCurrentProc();
        return removedProcess;
    }
    
    /**
     * Removes current process if starved and evicts starved processes from queues
     */
    public void removeStarved()
    {
        for(LinkedList<Process> queue: processQueue)
        {
            queue.removeIf((proc) -> (proc.getStartTime() < 0));
        }
        if(process != null && process.getStartTime() < 0)
        {
            process = null;
            updateCurrentProc();
        }   
    }
    
    /**
     * Checks if all queues are empty and if current process is null
     * @return true if all queues are empty and current process is null
     */
    public boolean isEmpty()
    {
        for(LinkedList<Process> queue: processQueue)
        {
            if(!queue.isEmpty())
                return false;
        }
        return process == null;
    }

 
    
    /**
     * Finds the new process and moves the current process to end of queue
     */
    public void updateCurrentProc()
    {
        if(process != null)
            add(process);
        for(int i = 0; i < processQueue.size(); i++)
        {
            if(!processQueue.get(i).isEmpty())
            {
                process = processQueue.get(i).removeFirst();
                return;
            }
        }
        process = null;
    }
    
    /**
     * Runs current process and updates the starting time
     * @param time the timeslice to run the process
     * @param currTime the time of method call
     */
    public void runCurrProcess(float time, float currTime)
    {
        if(process == null)
            updateCurrentProc();
        process.run(time);
        if(process.getStartTime() < 0) 
            process.setStartTime(currTime);
    }
    
    /**
     * Returns a string representation of queues and processes
     */
    public String toString()
    {
        String s = "Current=";
        if(process != null)
            s += process.getProcName();
        for(int i= 0; i < processQueue.size(); i++)
        {
            s += "\nPriority " + (i + 1);
            for(Process proc: processQueue.get(i))
            {
                s += " " + proc.getProcName();
            }
        }
        return s;
    }
}
