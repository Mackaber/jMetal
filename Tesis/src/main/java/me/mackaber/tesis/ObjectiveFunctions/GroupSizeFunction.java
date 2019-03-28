package me.mackaber.tesis.ObjectiveFunctions;

import me.mackaber.tesis.Util.Function;
import me.mackaber.tesis.Util.User;
import org.apache.commons.math3.stat.descriptive.summary.Sum;

import java.util.List;

public class GroupSizeFunction extends Function {

    @Override
    public double eval(List<Integer> group) {
        double eval = Math.abs(group.size() - 4.5);
        return eval;
    }
}
