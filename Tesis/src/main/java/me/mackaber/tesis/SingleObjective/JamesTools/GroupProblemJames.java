package me.mackaber.tesis.SingleObjective.JamesTools;

import me.mackaber.tesis.SingleObjective.GroupSolution;
import me.mackaber.tesis.SingleObjective.GroupingSolution;
import me.mackaber.tesis.SingleObjective.SingleObjectiveGrouping;
import me.mackaber.tesis.Util.User;
import org.jamesframework.core.problems.GenericProblem;
import org.jamesframework.core.problems.Problem;
import org.jamesframework.core.problems.constraints.validations.SimpleValidation;
import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.neighborhood.Neighborhood;

import java.util.List;
import java.util.Random;

public class GroupProblemJames<S extends Solution> implements Problem<GroupSolutionJames<S>> {

    private final org.uma.jmetal.problem.Problem problem;
    private final MutationOperator mutation;

    public GroupProblemJames(org.uma.jmetal.problem.Problem problem, MutationOperator mutation) {
        this.problem = problem;
        this.mutation = mutation;
    }

    @Override
    public Evaluation evaluate(GroupSolutionJames<S> sGroupSolutionJames) {
        GroupingSolution<List<User>> sol = (GroupingSolution<List<User>>) sGroupSolutionJames.getjMetalSolution();
        problem.evaluate(sol);
        double eval = sol.getObjective(0);
        return SimpleEvaluation.WITH_VALUE(eval);
    }

    @Override
    public Validation validate(GroupSolutionJames<S> sGroupSolutionJames) {
        return SimpleValidation.PASSED;
    }

    @Override
    public boolean isMinimizing() {
        return true;
    }

    @Override
    public GroupSolutionJames<S> createRandomSolution(Random random) {
        return new GroupSolutionJames<>((S) problem.createSolution());
    }

    public Neighbourhood<GroupSolutionJames<S>> getNeighbourhood() {
        Neighbourhood<GroupSolutionJames<S>> neighborhood = new GroupingNeighbourhood<>(mutation);
        return neighborhood;
    }
}
