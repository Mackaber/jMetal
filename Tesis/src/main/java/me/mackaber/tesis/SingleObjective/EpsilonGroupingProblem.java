package me.mackaber.tesis.SingleObjective;

import me.mackaber.tesis.Util.Function;
import me.mackaber.tesis.Util.InterestVector;
import me.mackaber.tesis.Util.User;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EpsilonGroupingProblem extends GroupingProblem implements ConstrainedProblem<GroupSolution> {
    private List<Function> constraints = new ArrayList<>();
    private List<Double> lowerBounds = new ArrayList<>();
    private AbstractStorelessUnivariateStatistic ct_measure = new Mean();

    public OverallConstraintViolation<GroupSolution> overallConstraintViolationDegree ;
    public NumberOfViolatedConstraints<GroupSolution> numberOfViolatedConstraints ;

    public EpsilonGroupingProblem(String usersFile) {
        super(usersFile);
    }

    public EpsilonGroupingProblem addConstraint(Function function, Double lowerBound) {
        this.constraints.add(function);
        this.lowerBounds.add(lowerBound);
        return this;
    }

    public void build() throws IOException {
        super.build();
        setNumberOfConstraints(constraints.size());
        overallConstraintViolationDegree = new OverallConstraintViolation<>() ;
        numberOfViolatedConstraints = new NumberOfViolatedConstraints<>() ;
    }

    @Override
    public void evaluate(GroupSolution solution) {
        super.evaluate(solution);
    }

    @Override
    public void evaluateConstraints(GroupSolution solution) {
        double[] constraint = new double[this.getNumberOfConstraints()];
        double overallConstraintViolation = 0.0;
        int violatedConstraints = 0;

        for (int j = 0; j < constraints.size(); j++) {
            int n_groups = solution.getGroups().getInternalGroups().size();
            double[] results = new double[n_groups];
            // This could have been parallelized
            for (int i = 0; i < n_groups - 1; i++) {
                List<User> group = solution.getUserGroup(i);
                if (group.size() > 0)
                    results[i] = (constraints.get(j).eval(group));
            }

            double fitness = ct_measure.evaluate(results);

            if (fitness >= lowerBounds.get(j)) {
                overallConstraintViolation += constraint[j];
                violatedConstraints++;
            }
            j++;
        }

        overallConstraintViolationDegree.setAttribute(solution, overallConstraintViolation);
        numberOfViolatedConstraints.setAttribute(solution, violatedConstraints);
    }

}
