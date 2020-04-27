package me.mackaber.tesis.SingleObjective;

import me.mackaber.tesis.Util.Function;
import me.mackaber.tesis.Util.User;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChevyshevGroupingProblem extends GroupingProblem {
    private List<Function> constraints = new ArrayList<>();
    private List<Double> lowerBounds = new ArrayList<>();
    private AbstractStorelessUnivariateStatistic ct_measure = new Mean();
    private List<Function> functions = new ArrayList<>();
    private List<Double> weights = new ArrayList<>();

    public ChevyshevGroupingProblem(String usersFile) {
        super(usersFile);
    }

    public GroupingProblem addConstraint(Function function, Double lowerBound) {
        this.constraints.add(function);
        this.lowerBounds.add(lowerBound);
        return this;
    }

    public void build() throws IOException {
        super.build();
        setNumberOfConstraints(constraints.size());
    }

    public GroupingProblem addObjectiveFunction(Function function) {
        this.functions.add(function);
        this.weights.add(1.0);
        return this;
    }

    public GroupingProblem setWeights(Double w1, Double w2, Double w3, Double w4) {
        List<Double> n_weights = new ArrayList<>();
        n_weights.add(w1);
        n_weights.add(w2);
        n_weights.add(w3);
        n_weights.add(w4);
        this.weights = n_weights;
        return this;
    }

    @Override
    public void evaluate(GroupSolution solution) {
        int j = 0;

        // Chebyshev Scalarization Function
        double[] fitness_v = new double[4];
        for (Function function : functions) {
            int n_groups = solution.getGroups().getInternalGroups().size();
            double[] results = new double[n_groups];
            for (int i = 0; i < n_groups - 1; i++) {
                List<User> group = solution.getUserGroup(i);
                if (group.size() > 0)
                    results[i] = (function.eval(group));
            }
            fitness_v[j] = weights.get(j) * ct_measure.evaluate(results);
            j++;
        }
        Max max = new Max();
        solution.setObjective(0, max.evaluate(fitness_v));
    }



}
