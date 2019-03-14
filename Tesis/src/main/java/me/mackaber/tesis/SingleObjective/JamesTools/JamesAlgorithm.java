package me.mackaber.tesis.SingleObjective.JamesTools;

import me.mackaber.tesis.SingleObjective.GroupSolution;
import org.jamesframework.core.search.Search;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;

public abstract class JamesAlgorithm<Result extends Solution> implements Algorithm<Result> {
    public abstract Search<GroupSolutionJames<Result>> getJamesAlgorithm();
}
