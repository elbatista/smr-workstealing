
package bftsmart.util;

import java.util.Random;

public class RealDistExponential  {

    protected double mean;
    protected Random javaAPIRandomGenerator;

    public RealDistExponential(double mean){
        this.mean = mean;
        this.javaAPIRandomGenerator = new Random();
    }

    public double getMean() {
        return mean;
    }

    public double sample() {
        return -java.lang.Math.log(javaAPIRandomGenerator.nextDouble()) * mean;
        // onde javaAPIRandomGenerator.nextDouble() é o proximo valor da distrib uniforme entre 0 e 1
    }

}