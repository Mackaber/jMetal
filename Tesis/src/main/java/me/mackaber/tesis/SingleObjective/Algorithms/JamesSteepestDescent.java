package me.mackaber.tesis.SingleObjective.Algorithms;

import me.mackaber.tesis.SingleObjective.JamesTools.GroupProblemJames;
import me.mackaber.tesis.SingleObjective.JamesTools.GroupSolutionJames;
import me.mackaber.tesis.SingleObjective.JamesTools.JamesAlgorithm;
import org.jamesframework.core.search.LocalSearch;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.algo.SteepestDescent;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

public class JamesSteepestDescent<S extends Solution<?>> extends JamesAlgorithm<S> implements Algorithm<S> {
    private final MutationOperator mutation;
    private Problem<S> problem;
    private LocalSearch<GroupSolutionJames<S>> steepestDescent;

    public JamesSteepestDescent(Problem<S> groupingProblem, MutationOperator mutation) {
        this.problem = groupingProblem;
        this.mutation = mutation;
        GroupProblemJames<S> groupProblemJames = new GroupProblemJames<>(problem, mutation);
        this.steepestDescent = new SteepestDescent<>(groupProblemJames, groupProblemJames.getNeighbourhood());
    }

    @Override
    public void run() {
        steepestDescent.start();
    }

    @Override
    public S getResult() {
        return steepestDescent.getBestSolution().getjMetalSolution();
    }

    @Override
    public String getName() {
        return steepestDescent.getName();
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public Search getJamesAlgorithm() {
        return steepestDescent;
    }
}
