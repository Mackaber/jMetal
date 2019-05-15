package me.mackaber.tesis.SingleObjective.Algorithms;

import me.mackaber.tesis.SingleObjective.GroupSolution;
import me.mackaber.tesis.SingleObjective.JamesTools.GroupProblemJames;
import me.mackaber.tesis.SingleObjective.JamesTools.GroupSolutionJames;
import me.mackaber.tesis.SingleObjective.JamesTools.JamesAlgorithm;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.algo.tabu.FullTabuMemory;
import org.jamesframework.core.search.algo.tabu.TabuMemory;
import org.jamesframework.core.search.algo.tabu.TabuSearch;
import org.jamesframework.core.search.neigh.Move;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.HashMap;

public class JamesTabuSearch<S extends Solution<?>> extends JamesAlgorithm<S> implements Algorithm<S> {
    private final MutationOperator mutation;
    private Problem<S> problem;
    private TabuSearch<GroupSolutionJames<S>> tabuSearch;

    public JamesTabuSearch(Problem<S> groupingProblem, MutationOperator mutation,int memorySize) {
        this.mutation = mutation;
        this.problem = groupingProblem;
        GroupProblemJames<S> groupProblemJames = new GroupProblemJames<>(problem, mutation);
        this.tabuSearch = new TabuSearch<>(groupProblemJames,
                groupProblemJames.getNeighbourhood(), new FullTabuMemory<>(memorySize));
    }

    @Override
    public Search<GroupSolutionJames<S>> getJamesAlgorithm() {
        return tabuSearch;
    }

    @Override
    public void run() {
        tabuSearch.dispose();
        tabuSearch.start();
    }

    @Override
    public S getResult() {
        return tabuSearch.getBestSolution().getjMetalSolution();
    }

    @Override
    public String getName() {
        return tabuSearch.getName();
    }

    @Override
    public String getDescription() {
        return null;
    }
}
