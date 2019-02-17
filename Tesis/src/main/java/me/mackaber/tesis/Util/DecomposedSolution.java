package me.mackaber.tesis.Util;

import me.mackaber.tesis.MultiObjective.MultiObjectiveGrouping;
import me.mackaber.tesis.ObjectiveFunctions.GroupSizeFunction;
import me.mackaber.tesis.ObjectiveFunctions.InterestsCosineSimilarityFunction;
import me.mackaber.tesis.ObjectiveFunctions.LevelFunction;
import me.mackaber.tesis.ObjectiveFunctions.ParticipationStyleFunction;
import me.mackaber.tesis.SingleObjective.DefaultGroupingSolution;
import me.mackaber.tesis.SingleObjective.GroupingSolution;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class DecomposedSolution<T> extends DefaultGroupingSolution {


    public <Result> DecomposedSolution(Result solution) {
        super((DefaultGroupingSolution) solution);
    }

    public List<GroupingSolution> getPopulation() {
        MultiObjectiveGrouping problemHolder = new MultiObjectiveGrouping();
        problemHolder.addObjectiveFunction(new GroupSizeFunction());
        problemHolder.addObjectiveFunction(new ParticipationStyleFunction());
        problemHolder.addObjectiveFunction(new LevelFunction());
        try {
            problemHolder.setInterestsFunction(new InterestsCosineSimilarityFunction("Tesis/src/main/resources/custom_interests.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        List<GroupingSolution> population = new ArrayList<>(1);
        population.add(problemHolder.createHolder(this));

        return population;
    }
}
