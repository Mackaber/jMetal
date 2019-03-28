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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class DecomposedSolution<T> extends GroupSolution {


    public <Result> DecomposedSolution(Result solution) {
        super((GroupSolution) solution);
    }

    public List<GroupSolution> getPopulation() {
        GroupingProblem groupingProblem = (GroupingProblem) problem;
        GroupingProblem problemHolder = new GroupingProblem(groupingProblem.getUserFile());
        problemHolder.setUsers(groupingProblem.getUsers());
        problemHolder.addObjectiveFunction(new GroupSizeFunction());
        problemHolder.addObjectiveFunction(new ParticipationStyleFunction());
        problemHolder.addObjectiveFunction(new LevelFunction());
        problemHolder.addObjectiveFunction(new InterestsCosineSimilarityFunction());

        List<GroupSolution> population = new ArrayList<>(1);
        population.add(problemHolder.createHolder(this));

        return population;
    }
}
