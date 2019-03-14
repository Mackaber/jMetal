package me.mackaber.tesis.SingleObjective.JamesTools;

import me.mackaber.tesis.SingleObjective.GroupingSolution;
import org.jamesframework.core.problems.sol.Solution;

public class GroupSolutionJames<S extends org.uma.jmetal.solution.Solution> extends Solution {
    private S solution;

    public GroupSolutionJames(S solution) {
        this.solution = solution;
    }

    public GroupSolutionJames(GroupingSolution<?> solution){
        this.solution = (S) solution.copy();
    }

    public S getjMetalSolution(){
        return solution;
    }

    public void setSolution(S solution) {
        this.solution = solution;
    }

    @Override
    public Solution copy() {
        return new GroupSolutionJames<>(solution.copy());
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
