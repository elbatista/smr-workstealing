import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


/**
 * Consolida os arquivos dos experimentos de medicao de tempos de execucao.
 * Essa classe considera arquivos com somente tres medicoes de tempo:
 *      Coluna 1 - Tempo desde decisionTime ateh chegar no receive messages
 *      Coluna 2 - Tempo desde chegar no receive messages ateh escalonar
 *      Coluna 3 - Tempo desde escalonar ateh chegar pra executar
 *      Coluna 4 - Tempo desde executar ateh fim execucao
 *      Coluna 5 - Tempo total
 */
public class ConsolidaTemposExec {

    private String path;

    public ConsolidaTemposExec(String path){
        this.path = path;
    }

    public ConsolidaTemposExec gera(String algo){
        // LIGHT COSTS (1000)
        long count = 0;
        long sumSched = 0;
        double stdDevSched = 0;
        long sumExec = 0;
        double stdDevExec = 0;

        // Matriz de dados
        double tempos [][] = new double[3][4];

        ArrayList<Long> arraySched = new ArrayList<>();
        ArrayList<Long> arrayExec = new ArrayList<>();

        for (File file : new File(path).listFiles((a,name)->name.matches(algo+"_1000_(.*).txt"))){
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader(file.getPath()));
                String line = br.readLine();
                while (line != null) {
                    count++;
                    long sched = (Long.valueOf(line.split("\t")[0]) + Long.valueOf(line.split("\t")[1]) + Long.valueOf(line.split("\t")[2]));
                    long exec = Long.valueOf(line.split("\t")[3]);
                    sumSched += sched;
                    sumExec += exec;
                    arraySched.add(sched);
                    arrayExec.add(exec);
                    line = br.readLine();
                }
            } catch (IOException e) {e.printStackTrace();}
        }

        for(Long i : arraySched){
            stdDevSched += Math.pow(i - (sumSched/count), 2);
        }
        for(Long i : arrayExec){
            stdDevExec += Math.pow(i - (sumExec/count), 2);
        }

        try {
            tempos[0][0] = sumExec/count;
            tempos[0][1] = Math.sqrt(stdDevExec/count);
            tempos[0][2] = sumSched/count;
            tempos[0][3] = Math.sqrt(stdDevSched/count);
        }
        catch(ArithmeticException e){}


        // MODERATE COSTS (10000)
        count = 0;
        sumSched = 0;
        sumExec = 0;
        stdDevSched = 0;
        stdDevExec = 0;
        arraySched = new ArrayList<>();
        arrayExec = new ArrayList<>();

        for (File file : new File(path).listFiles((a,name)->name.matches(algo+"_10000_(.*).txt"))){
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader(file.getPath()));
                String line = br.readLine();
                while (line != null) {
                    count++;
                    long sched = (Long.valueOf(line.split("\t")[0]) + Long.valueOf(line.split("\t")[1]) + Long.valueOf(line.split("\t")[2]));
                    long exec = Long.valueOf(line.split("\t")[3]);
                    sumSched += sched;
                    sumExec += exec;
                    arraySched.add(sched);
                    arrayExec.add(exec);
                    line = br.readLine();
                }
            } catch (IOException e) {e.printStackTrace();}
        }

        for(Long i : arraySched){
            stdDevSched += Math.pow(i - (sumSched/count), 2);
        }
        for(Long i : arrayExec){
            stdDevExec += Math.pow(i - (sumExec/count), 2);
        }

        try {
            tempos[1][0] = sumExec/count;
            tempos[1][1] = Math.sqrt(stdDevExec/count);
            tempos[1][2] = sumSched/count;
            tempos[1][3] = Math.sqrt(stdDevSched/count);
        }
        catch(ArithmeticException e){}


        //HEAVY COSTS (100000)
        count = 0;
        sumSched = 0;
        sumExec = 0;
        stdDevSched = 0;
        stdDevExec = 0;
        arraySched = new ArrayList<>();
        arrayExec = new ArrayList<>();

        for (File file : new File(path).listFiles((a,name)->name.matches(algo+"_100000_(.*).txt"))){
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader(file.getPath()));
                String line = br.readLine();
                while (line != null) {
                    count++;
                    long sched = (Long.valueOf(line.split("\t")[0]) + Long.valueOf(line.split("\t")[1]) + Long.valueOf(line.split("\t")[2]));
                    long exec = Long.valueOf(line.split("\t")[3]);
                    sumSched += sched;
                    sumExec += exec;
                    arraySched.add(sched);
                    arrayExec.add(exec);
                    line = br.readLine();
                }
            } catch (IOException e) {e.printStackTrace();}
        }

        for(Long i : arraySched){
            stdDevSched += Math.pow(i - (sumSched/count), 2);
        }
        for(Long i : arrayExec){
            stdDevExec += Math.pow(i - (sumExec/count), 2);
        }

        try{
            tempos[2][0] = sumExec/count;
            tempos[2][1] = Math.sqrt(stdDevExec/count);
            tempos[2][2] = sumSched/count;
            tempos[2][3] = Math.sqrt(stdDevSched/count);
        }
        catch(ArithmeticException e){}

        System.out.println(
            algoName(algo) + 
            "\t" + TimeUnit.NANOSECONDS.toMicros((long)tempos[0][0]) + "\t" + TimeUnit.NANOSECONDS.toMicros((long)tempos[0][1]) + "\t" + TimeUnit.NANOSECONDS.toMicros((long)tempos[0][2]) + "\t" + TimeUnit.NANOSECONDS.toMicros((long)tempos[0][3]) + 
            "\t" + TimeUnit.NANOSECONDS.toMicros((long)tempos[1][0]) + "\t" + TimeUnit.NANOSECONDS.toMicros((long)tempos[1][1]) + "\t" + TimeUnit.NANOSECONDS.toMicros((long)tempos[1][2]) + "\t" + TimeUnit.NANOSECONDS.toMicros((long)tempos[1][3]) + 
            "\t" + TimeUnit.NANOSECONDS.toMicros((long)tempos[2][0]) + "\t" + TimeUnit.NANOSECONDS.toMicros((long)tempos[2][1]) + "\t" + TimeUnit.NANOSECONDS.toMicros((long)tempos[2][2]) + "\t" + TimeUnit.NANOSECONDS.toMicros((long)tempos[2][3])
        );

        return this;
        
    }

    protected String algoName(String algo) {
        switch(algo){
            case "seq":         return "Seq";
            case "early":       return "Early";
            case "busywait":    return "B-Wait";
            case "ws":          return "WS";
        }
        return null;
    }

    public static void main(String[] args) {
        new ConsolidaTemposExec(args[0])
        .gera("seq")
        .gera("early")
        .gera("busywait")
        .gera("ws");
    }
}
