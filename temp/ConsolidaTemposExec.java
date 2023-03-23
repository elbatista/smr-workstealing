import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ConsolidaTemposExec {
    public static void gera(String algo){
        // LIGHT COSTS (1000)
        long count = 0;
        long sumSched = 0, sumExec = 0;
        double tempos [][] = new double[3][2];
        for (File file : new File("./").listFiles((a,name)->name.matches(algo+"_1000_(.*).txt"))){
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader(file.getPath()));
                String line = br.readLine();
                while (line != null) {
                    count++;
                    long sched = Long.valueOf(line.split("\t")[0]);
                    long exec = Long.valueOf(line.split("\t")[1]);
                    sumSched += sched;
                    sumExec += exec;
                    line = br.readLine();
                }
            } catch (IOException e) {e.printStackTrace();}
        }


        try {
        tempos[0][0] = sumSched/count;
        tempos[0][1] = sumExec/count;
        }catch(ArithmeticException e){}


        // MODERATE COSTS (10000)
        count = 0;
        sumSched = 0;
        sumExec = 0;
        for (File file : new File("./").listFiles((a,name)->name.matches(algo+"_10000_(.*).txt"))){
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader(file.getPath()));
                String line = br.readLine();
                while (line != null) {
                    count++;
                    long sched = Long.valueOf(line.split("\t")[0]);
                    long exec = Long.valueOf(line.split("\t")[1]);
                    sumSched += sched;
                    sumExec += exec;
                    line = br.readLine();
                }
            } catch (IOException e) {e.printStackTrace();}
        }


        try {
        tempos[1][0] = sumSched/count;
        tempos[1][1] = sumExec/count;
        }catch(ArithmeticException e){}


        //HEAVY COSTS (100000)
        count = 0;
        sumSched = 0;
        sumExec = 0;
        for (File file : new File("./").listFiles((a,name)->name.matches(algo+"_100000_(.*).txt"))){
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader(file.getPath()));
                String line = br.readLine();
                while (line != null) {
                    count++;
                    long sched = Long.valueOf(line.split("\t")[0]);
                    long exec = Long.valueOf(line.split("\t")[1]);
                    sumSched += sched;
                    sumExec += exec;
                    line = br.readLine();
                }
            } catch (IOException e) {e.printStackTrace();}
        }


        try{
        tempos[2][0] = (sumSched/count);
        tempos[2][1] = (sumExec/count);
        }catch(ArithmeticException e){}


        System.out.println(
            algoName(algo) + 
            "\t" + TimeUnit.NANOSECONDS.toMicros((long)tempos[0][0]) + "\t" + TimeUnit.NANOSECONDS.toMicros((long)tempos[0][1]) + 
            "\t" + TimeUnit.NANOSECONDS.toMicros((long)tempos[1][0]) + "\t" + TimeUnit.NANOSECONDS.toMicros((long)tempos[1][1]) + 
            "\t" + TimeUnit.NANOSECONDS.toMicros((long)tempos[2][0]) + "\t" + TimeUnit.NANOSECONDS.toMicros((long)tempos[2][1])
        );
        
    }

    private static String algoName(String algo) {
        switch(algo){
            case "seq": return "Sequential";
            case "early": return "Early-Scheduling";
            case "busywait": return "Busy-Wait";
            case "ws": return "Work-Stealing";
        }
        return null;
    }

    public static void main(String[] args) {
        gera("seq");
        gera("early");
        gera("busywait");
        gera("ws");
    }
}
