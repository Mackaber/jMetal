package me.mackaber.tesis.MultiObjective;

import me.mackaber.tesis.SingleObjective.GroupingSolution;
import me.mackaber.tesis.Util.User;
import org.uma.jmetal.operator.CrossoverOperator;

import java.util.List;

public class GroupCrossOver implements CrossoverOperator<GroupingSolution<List<User>>> {
    public GroupCrossOver(double v) {
    }

    @Override
    public int getNumberOfRequiredParents() {
        return 0;
    }

    @Override
    public int getNumberOfGeneratedChildren() {
        return 0;
    }

    @Override
    public List<GroupingSolution<List<User>>> execute(List<GroupingSolution<List<User>>> groupingSolutions) {
        return null;
    }
}
