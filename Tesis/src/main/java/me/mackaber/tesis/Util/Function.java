package me.mackaber.tesis.Util;

import me.mackaber.tesis.SingleObjective.GroupingProblem;
import me.mackaber.tesis.SingleObjective.GroupingSolution;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.naming.DescribedEntity;

import java.io.FileNotFoundException;
import java.util.List;

public abstract class Function implements DescribedEntity {
    private GroupingProblem problem;

    public void setProblem(GroupingProblem problem) {
        this.problem = problem;
    }

    public GroupingProblem getProblem() {
        return problem;
    }

    public abstract double eval(List<Integer> variableValue);
}
