package me.mackaber.tesis.ObjectiveFunctions;

import me.mackaber.tesis.SingleObjective.GroupingProblem;
import me.mackaber.tesis.Util.Function;
import me.mackaber.tesis.Util.User;

import java.util.List;

public class ParticipationStyleFunction extends Function {

    @Override
    public double eval(List<Integer> group) {
        Double sum = 0.0;

        for (int i = 0; i < group.size(); i++) {
            sum += getProblem().getUsers().get(i).getPart_prc();
        }

        return (1 / sum);
    }
}
