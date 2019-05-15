package me.mackaber.tesis.SingleObjective.Algorithms;

import me.mackaber.tesis.SingleObjective.JamesTools.GroupProblemJames;
import me.mackaber.tesis.SingleObjective.JamesTools.GroupSolutionJames;
import me.mackaber.tesis.SingleObjective.JamesTools.JamesAlgorithm;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.algo.RandomSearch;
import org.jamesframework.core.search.algo.tabu.FullTabuMemory;
import org.jamesframework.core.search.algo.tabu.TabuSearch;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

public class JamesRandomSearch<S extends Solution<?>> extends JamesAlgorithm<S> implements Algorithm<S> {
    private final MutationOperator mutation;
    private Problem<S> problem;
    private RandomSearch<GroupSolutionJames<S>> randomSearch;

    public JamesRandomSearch(Problem<S> groupingProblem, MutationOperator mutation) {
        this.mutation = mutation;
        this.problem = groupingProblem;
        GroupProblemJames<S> groupProblemJames = new GroupProblemJames<>(problem, mutation);
        this.randomSearch = new RandomSearch<>(groupProblemJames);
    }

    @Override
    public Search<GroupSolutionJames<S>> getJamesAlgorithm() {
        return randomSearch;
    }

    @Override
    public void run() {
        randomSearch.start();
    }

    @Override
    public S getResult() {
        randomSearch.dispose();
        return randomSearch.getBestSolution().getjMetalSolution();
    }

    @Override
    public String getName() {
        return randomSearch.getName();
    }

    @Override
    public String getDescription() {
        return null;
    }
}
