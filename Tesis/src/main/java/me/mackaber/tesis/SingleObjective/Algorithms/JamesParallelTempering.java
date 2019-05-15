package me.mackaber.tesis.SingleObjective.Algorithms;

import me.mackaber.tesis.SingleObjective.JamesTools.GroupProblemJames;
import me.mackaber.tesis.SingleObjective.JamesTools.GroupSolutionJames;
import me.mackaber.tesis.SingleObjective.JamesTools.JamesAlgorithm;
import org.jamesframework.core.search.LocalSearch;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.algo.ParallelTempering;
import org.jamesframework.core.search.algo.RandomDescent;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

public class JamesParallelTempering<S extends Solution<?>> extends JamesAlgorithm<S> implements Algorithm<S> {
    private final MutationOperator mutation;
    private Problem<S> problem;
    private LocalSearch<GroupSolutionJames<S>> parallelTempering;

    public JamesParallelTempering(Problem<S> groupingProblem, MutationOperator mutation,double minTemp, double maxTemp, int numReplicas) {
        this.problem = groupingProblem;
        this.mutation = mutation;
        GroupProblemJames<S> groupProblemJames = new GroupProblemJames<>(problem, mutation);
        this.parallelTempering = new ParallelTempering<>(groupProblemJames, groupProblemJames.getNeighbourhood(), numReplicas, minTemp, maxTemp);
    }

    @Override
    public void run() {
        parallelTempering.start();
    }

    @Override
    public S getResult() {
        parallelTempering.dispose();
        return parallelTempering.getBestSolution().getjMetalSolution();
    }

    @Override
    public String getName() {
        return parallelTempering.getName();
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public Search getJamesAlgorithm() {
        return parallelTempering;
    }
}
