package me.mackaber.tesis.SingleObjective.JamesTools;

import org.jamesframework.core.search.neigh.Move;
import org.uma.jmetal.solution.Solution;

public class GroupingMove<S extends Solution> implements Move<GroupSolutionJames<S>> {
    private S solution;

    public GroupingMove(S solution) {
        this.solution = solution;
    }

    @Override
    public void apply(GroupSolutionJames<S> solutionJames) {
        solutionJames.setSolution(solution);
    }

    @Override
    public void undo(GroupSolutionJames<S> solutionJames) {
    }

}
