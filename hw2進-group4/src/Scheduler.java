import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

/**
 *  @author CS 149 GROUP 4
 *
 */
public class Scheduler
{
    public static final Comparator<Process> ARRIVAL_TIME_COMPARATOR = (p1, p2) -> Float.compare(p1.getArrivalTime(), p2.getArrivalTime());
    public static final Comparator<Process> RUN_TIME_COMPARATOR = (p1, p2) -> Float.compare(p1.getRunTime(), p2.getRunTime());
    public static final String NEWLINE = System.getProperty("line.separator");
    
    /**
     * Formats output of priority level present in processes.
     * @param process Processes.
     * @param time The time of algorithm completion.
     * @param timeLine ArrayList of processes running.
     * @return Information of priority levels and all processes.
     */
    public static String formatPriorityOutput(Collection<Process> process, float time, ArrayList<String> timeLine)
    {
        ArrayList<LinkedList<Process>> processQueues = new ArrayList<LinkedList<Process>>();
        
        for(int i = 0; i < Process.MAX_PRIORITY; i++)
        {
            processQueues.add(new LinkedList<Process>());
        }
        
        for(Process processs: process)
        {
            processQueues.get(processs.getInitPriority() - 1).addLast(processs);
        }
        
        StringBuilder outputString = new StringBuilder();
        
        for(int i = 0; i < Process.MAX_PRIORITY; i++)
        {
            LinkedList<Process> processQueue = processQueues.get(i);
            outputString.append("Statistics for priority " + (i + 1) + NEWLINE);
            outputString.append(formatOutput(processQueue, time, null) + NEWLINE + NEWLINE);
        }
        
        outputString.append(NEWLINE + NEWLINE + "Overall statistics");
        outputString.append(NEWLINE + formatOutput(process, time, timeLine));
        
        return outputString.toString();
    }
    
    /**
     * Helper method for formatPriorityOutput method.
     * Formats output in form of
     * Timeline of processes, average turnaround time, average waiting time, average response time, average throughput
     * @param process Processes.
     * @param time Time of algorithm completion.
     * @param timeLine Which process ran.
     * @return Formated output.
     */
    public static String formatOutput(Collection<Process> process, float time, ArrayList<String> timeLine)
    {
        StringBuilder outputString = new StringBuilder();
        
        float totalFinishingTime = 0;
        float totalResponseTime = 0;
        float totalWaitingTime = 0;
        
        float averageTurnaround = 0;
        float averageWait = 0;
        float averageResponse = 0;
        
        float throughput = 0;
        
        process.forEach((proc) -> {outputString.append(proc.initInfo() + NEWLINE + NEWLINE);});
        
        if(timeLine != null)
        {
            outputString.append(NEWLINE);
            outputString.append(formatTimeline(timeLine));
        }
        
        for(Process processs: process)
        {
            totalFinishingTime += processs.getFinishTime();
            totalWaitingTime += processs.getFinishTime() - processs.getInitRunTime() - processs.getInitArrivalTime();
            totalResponseTime += processs.getStartTime();
        }
        
        if(process.size() > 0)
        {
            averageTurnaround = totalFinishingTime / process.size();
            averageWait = totalWaitingTime / process.size();
            averageResponse = totalResponseTime / process.size();
        }
        outputString.append(NEWLINE + "Average turnaround time: " + averageTurnaround);
        outputString.append(NEWLINE + "Average wait time: " + averageWait);
        outputString.append(NEWLINE + "Average response time: " + averageResponse);
        
        if(time > 0)
        {
            throughput = process.size() / time;
        }
        outputString.append(NEWLINE + "Throughput: " + throughput + " processes per quantum");
        
        return outputString.toString();
    }
    
    /**
     * Helper method for formatOutput method.
     * @param timeLine Which process ran.
     * @return Formatted output.
     */
    private static String formatTimeline(ArrayList<String> timeLine)
    {
        StringBuilder timeChartOutputString = new StringBuilder();
        
        for(int i = 0; i < timeLine.size(); i++)
        {
            String currentQuant = String.format("Q%03d: ", i);
            String currentProc = String.format("%4s", timeLine.get(i));
            timeChartOutputString.append("|" + currentQuant + currentProc + "|");
            
            if((i + 1) % 10 == 0)
            {
                timeChartOutputString.append(NEWLINE);
            }
        }
        
        return timeChartOutputString.toString();
    }
    
}
