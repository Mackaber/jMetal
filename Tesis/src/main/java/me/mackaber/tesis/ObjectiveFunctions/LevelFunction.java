package me.mackaber.tesis.ObjectiveFunctions;

import me.mackaber.tesis.Util.Function;
import me.mackaber.tesis.Util.User;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.util.List;

public class LevelFunction extends Function {
    @Override
    public double eval(List<Integer> group) {
        int numberOfUsers = group.size();
        double[] levels = new double[numberOfUsers];

        for (int i = 0; i < numberOfUsers - 1; i++) {
            levels[i] = Double.valueOf(getProblem().getUsers().get(i).getLevel());
        }

        StandardDeviation sd = new StandardDeviation();
        return sd.evaluate(levels);
    }
}
