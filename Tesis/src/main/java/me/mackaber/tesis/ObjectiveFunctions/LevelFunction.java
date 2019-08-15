package me.mackaber.tesis.ObjectiveFunctions;

import me.mackaber.tesis.Util.Function;
import me.mackaber.tesis.Util.User;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.util.List;

public class LevelFunction extends Function {
    private static final java.util.function.Function<List<User>,Double> CACHED = Memoizer.memoize(LevelFunction::uncached);

    public double eval(List<User> group) {
        return CACHED.apply(group);
    }

    private static double uncached(List<User> group) {
        StandardDeviation sd = new StandardDeviation();
        return sd.evaluate(group.stream().mapToDouble(User::getLevel).toArray());
    }

    @Override
    public String getName() {
        return "LevelFunction";
    }

    @Override
    public String getDescription() {
        return null;
    }
}
