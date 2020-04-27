package me.mackaber.tesis.Util;

import me.mackaber.tesis.MultiObjective.MultiObjectiveGrouping;
import me.mackaber.tesis.ObjectiveFunctions.GroupSizeFunction;
import me.mackaber.tesis.ObjectiveFunctions.InterestsCosineSimilarityFunction;
import me.mackaber.tesis.ObjectiveFunctions.LevelFunction;
import me.mackaber.tesis.ObjectiveFunctions.ParticipationStyleFunction;
import me.mackaber.tesis.SingleObjective.DefaultGroupSolution;
import me.mackaber.tesis.SingleObjective.GroupSolution;
import me.mackaber.tesis.SingleObjective.GroupingProblem;
import me.mackaber.tesis.SingleObjective.GroupingSolution;
import org.uma.jmetal.problem.Problem;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class DecomposedSolution {
    private  GroupSolution solution;

    public DecomposedSolution(GroupingProblem problem, String solutionString) {
        String[] variables = solutionString.split("\\s+");
        solution = new DefaultGroupSolution(problem, variables);
    }

    public <Result> DecomposedSolution(Result solution) {
        this.solution = (GroupSolution) solution;
    }

    public List<GroupSolution> getPopulation() {
        GroupingProblem problem = (GroupingProblem) solution.getProblem();

        GroupingProblem problemHolder = new GroupingProblem(problem.getUserFile());
        problemHolder.setVector(problem.getVector());
        problemHolder.setUsers(problem.getUsers());
        problemHolder.addObjectiveFunction(new GroupSizeFunction());
        problemHolder.addObjectiveFunction(new ParticipationStyleFunction());
        problemHolder.addObjectiveFunction(new LevelFunction());
        problemHolder.addObjectiveFunction(new InterestsCosineSimilarityFunction());

        List<GroupSolution> population = new ArrayList<>(1);
        problemHolder.buildHolder(solution);
        GroupSolution solutionHolder = (GroupSolution) solution.copy(problemHolder);
        problemHolder.evaluate(solutionHolder);

        population.add(solutionHolder);

        return population;
    }
}
