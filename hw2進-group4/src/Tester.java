import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Tester
{
    public static final float TARGET_TIME = 100;
    public static final int REPEAT_TEST = 5;
    public static final int FCFS_PROCS = 40;
    public static final int SJF_PROCS = 30;
    public static final int SRT_PROCS = 30;
    public static final int RR_PROCS = 40;
    public static final int HPFNP = 40;
    public static final int HPFP = 50;
    public static final String NL = System.getProperty("line.separator");
    
    public static void main(String[] args) throws FileNotFoundException
    {
        test();
        System.out.println("Printed output to project directory.");
    }
    
    private static void test() throws FileNotFoundException
    {
        try(PrintWriter out = new PrintWriter("output.txt"))
        {
            out.println("----------------------------------------------");
            out.println("Running FCFS " + REPEAT_TEST + " times.");
            out.println("----------------------------------------------" + NL + NL);
            for(int i = 1; i <= REPEAT_TEST; i++)
            {
                out.println("FCFS run #" + i);
                out.println(FCFS.execute(Process.GenMultiple(FCFS_PROCS), TARGET_TIME) + NL + NL);
            }
            
            out.println("----------------------------------------------");
            out.println("Running SJF " + REPEAT_TEST + " times.");
            out.println("----------------------------------------------" + NL + NL);
            for(int i = 1; i <= REPEAT_TEST; i++)
            {
                out.println("SJF run #" + i);
                out.println(SJF.execute(Process.GenMultiple(SJF_PROCS), TARGET_TIME) + NL + NL);
            }
            
            out.println("----------------------------------------------");
            out.println("Running SRT " + REPEAT_TEST + " times.");
            out.println("----------------------------------------------" + NL + NL);
            for(int i = 1; i <= REPEAT_TEST; i++)
            {
                out.println("SRT run #" + i);
                out.println(SRT.execute(Process.GenMultiple(SRT_PROCS), TARGET_TIME) + NL + NL);
            }
            
            out.println("----------------------------------------------");
            out.println("Running HPF Non-preemptive Non-aging " + REPEAT_TEST + " times.");
            out.println("----------------------------------------------" + NL + NL);
            for(int i = 1; i <= REPEAT_TEST; i++)
            {
                out.println("HPF Non-preemptive Non-aging run #" + i);
                out.println(HPFNonPreemptive.execute(Process.GenMultiple(HPFNP), TARGET_TIME, false) + NL + NL);
            }
            
            out.println("----------------------------------------------");
            out.println("Running HPF Non-preemptive w/Aging " + REPEAT_TEST + " times.");
            out.println("----------------------------------------------" + NL + NL);
            for(int i = 1; i <= REPEAT_TEST; i++)
            {
                out.println("HPF Non-preemptive w/Aging run #" + i);
                out.println(HPFNonPreemptive.execute(Process.GenMultiple(HPFNP), TARGET_TIME, true) + NL + NL);
            }
              out.println("----------------------------------------------");
            out.println("Running HPF Non-preemptive w/Aging " + REPEAT_TEST + " times.");
            out.println("----------------------------------------------" + NL + NL);
            for(int i = 1; i <= REPEAT_TEST; i++)
            {
                out.println("HPF Preemptive  run #" + i);
                out.println(HPFPreemptive.execute(Process.GenMultiple(HPFP), TARGET_TIME, true) + NL + NL);
            }
           out.println("----------------------------------------------");

            out.println("Running RR " + REPEAT_TEST + " times.");
            out.println("**********************************************" + NL + NL);
            for(int i = 1; i <= REPEAT_TEST; i++)
            {
                out.println("RR run #" + i);
                out.println(RR.execute(Process.GenMultiple(RR_PROCS), TARGET_TIME) + NL + NL);
            }
        }        
    }
}
