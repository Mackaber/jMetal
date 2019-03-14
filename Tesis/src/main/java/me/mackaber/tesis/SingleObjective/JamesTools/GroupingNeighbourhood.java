package me.mackaber.tesis.SingleObjective.JamesTools;

import me.mackaber.tesis.SingleObjective.DefaultGroupingSolution;
import me.mackaber.tesis.SingleObjective.GroupingSolution;
import me.mackaber.tesis.Util.User;
import org.jamesframework.core.search.neigh.Neighbourhood;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GroupingNeighbourhood<S extends Solution> implements Neighbourhood<GroupSolutionJames<S>> {
    private final MutationOperator mutation;

    GroupingNeighbourhood(MutationOperator mutation) {
        this.mutation = mutation;
    }

    @Override
    public GroupingMove<S> getRandomMove(GroupSolutionJames<S> solution, Random random) {
        Solution movedSolution = (Solution) mutation.execute(solution.getjMetalSolution());
        GroupingMove<S> groupingMove = new GroupingMove(movedSolution);
        return groupingMove;
    }

    @Override
    public List<GroupingMove<S>> getAllMoves(GroupSolutionJames<S> solution) {
        List<GroupingMove<S>> moves = new ArrayList<>();
        GroupingSolution<List<User>> jMetalSolution = (GroupingSolution<List<User>>) solution.getjMetalSolution();

        for (int i = 0; i < jMetalSolution.getNumberOfVariables() - 1; i++) {
            for (int j = 0; j < jMetalSolution.getVariableValue(i).size() - 2; j++) {
                GroupingSolution<List<User>> newSol = (GroupingSolution<List<User>>) jMetalSolution.copy();

                User user = newSol.getVariableValue(i).get(j);
                newSol.getVariableValue(i).remove(user);
                newSol.getVariableValue(i + 1).add(user);

                GroupingMove groupingMove = new GroupingMove<>(newSol);
                moves.add(groupingMove);


            }
        }
        return moves;
    }
}
