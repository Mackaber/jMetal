package me.mackaber.tesis.Scratches;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class StandardDeviationTest {
    public static void main(String[] args) {
        StandardDeviation sd = new StandardDeviation();
        Double levelDifference = 1 - ((2.82843 - sd.evaluate(new double[]{Double.valueOf(1), Double.valueOf(1)})) / 2.82843);

        System.out.print(levelDifference);
    }
}
