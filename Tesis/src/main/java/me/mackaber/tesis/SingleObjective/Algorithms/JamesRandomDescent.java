package me.mackaber.tesis.SingleObjective.Algorithms;

import me.mackaber.tesis.SingleObjective.JamesTools.GroupProblemJames;
import me.mackaber.tesis.SingleObjective.JamesTools.GroupSolutionJames;
import me.mackaber.tesis.SingleObjective.JamesTools.JamesAlgorithm;
import me.mackaber.tesis.SingleObjective.JamesTools.ProgressSearchListener;
import org.jamesframework.core.search.LocalSearch;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.algo.RandomDescent;
import org.jamesframework.core.search.stopcriteria.MaxRuntime;
import org.jamesframework.core.search.stopcriteria.MaxStepsWithoutImprovement;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

import java.util.concurrent.TimeUnit;

public class JamesRandomDescent<S extends Solution<?>> extends JamesAlgorithm<S> implements Algorithm<S> {
    private final MutationOperator mutation;
    private Problem<S> problem;
    private LocalSearch<GroupSolutionJames<S>> randomDescent;

    public JamesRandomDescent(Problem<S> groupingProblem, MutationOperator mutation) {
        this.problem = groupingProblem;
        this.mutation = mutation;
        GroupProblemJames<S> groupProblemJames = new GroupProblemJames<>(problem, mutation);
        this.randomDescent = new RandomDescent<>(groupProblemJames, groupProblemJames.getNeighbourhood());

    }

    @Override
    public void run() {
        randomDescent.start();
    }

    @Override
    public S getResult() {
        return randomDescent.getBestSolution().getjMetalSolution();
    }

    @Override
    public String getName() {
        return randomDescent.getName();
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public Search getJamesAlgorithm() {
        return randomDescent;
    }
}
