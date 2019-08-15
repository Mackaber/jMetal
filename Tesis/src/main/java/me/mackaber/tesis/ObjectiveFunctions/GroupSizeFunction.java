package me.mackaber.tesis.ObjectiveFunctions;

import me.mackaber.tesis.Util.Function;
import me.mackaber.tesis.Util.User;
import org.apache.commons.math3.stat.descriptive.summary.Sum;

import java.util.List;

public class GroupSizeFunction extends Function {

    @Override
    public double eval(List<User> group) {
        return Math.abs(group.size() - 4.5);
    }

    @Override
    public String getName() {
        return "GroupSize";
    }

    @Override
    public String getDescription() {
        return null;
    }
}
