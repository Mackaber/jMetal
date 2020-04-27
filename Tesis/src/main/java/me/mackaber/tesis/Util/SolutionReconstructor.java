package me.mackaber.tesis.Util;

import me.mackaber.tesis.ObjectiveFunctions.GroupSizeFunction;
import me.mackaber.tesis.ObjectiveFunctions.InterestsCosineSimilarityFunction;
import me.mackaber.tesis.ObjectiveFunctions.LevelFunction;
import me.mackaber.tesis.ObjectiveFunctions.ParticipationStyleFunction;
import me.mackaber.tesis.SingleObjective.EpsilonGroupingProblem;
import me.mackaber.tesis.SingleObjective.GroupingProblem;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SolutionReconstructor {

    public static DecomposedSolution generateSolutionFromFile(GroupingProblem problem, String variablesFile) throws FileNotFoundException {
        File file = new File(variablesFile);
        Scanner reader = new Scanner(file);
        String solutionString = reader.nextLine();
        return new DecomposedSolution(problem, solutionString);
    }

    private static void writeDecomposedSolutionResult(DecomposedSolution decomposedSolution, String outputDirectoryName, Integer runId) {
        String decFile = outputDirectoryName + "/DEC" + runId + ".tsv";
        new SolutionListOutput(decomposedSolution.getPopulation())
                .setSeparator("\t")
                .setFunFileOutputContext(new DefaultFileOutputContext(decFile))
                .print();
    }

    public static void main(String[] args) throws IOException {

        GroupingProblem groupingProblem = new GroupingProblem("Tesis/src/main/resources/synthetic_20.csv");
        groupingProblem.setGroupSizeRange(3, 6)
                .addObjectiveFunction(new GroupSizeFunction())
                .addObjectiveFunction(new ParticipationStyleFunction())
                .addObjectiveFunction(new LevelFunction())
                .addObjectiveFunction(new InterestsCosineSimilarityFunction())
                .setVector(new InterestVector("Tesis/src/main/resources/custom_interests.json"))
                .setCentralTendencyMeasure(new Mean())
                .build();

        int i = 0;
        // for Experiment -> Algorithm -> Sizes
        String outputDirectoryName = "Tesis/src/main/resources";
        DecomposedSolution decomposedSolution = generateSolutionFromFile(groupingProblem, "Tesis/src/main/resources/VAR1.tsv");
        writeDecomposedSolutionResult(decomposedSolution, outputDirectoryName, i);
        i++;

    }

    /*
    public static void main(String[] args) throws IOException {

        List<GroupingProblem> problems = new ArrayList<>();

        GroupingProblem groupingProblem = new GroupingProblem("Tesis/src/main/resources/synthetic_20.csv");
        groupingProblem.setGroupSizeRange(3, 6)
                .addObjectiveFunction(new GroupSizeFunction())
                .addObjectiveFunction(new ParticipationStyleFunction())
                .addObjectiveFunction(new LevelFunction())
                .addObjectiveFunction(new InterestsCosineSimilarityFunction())
                .setVector(new InterestVector("Tesis/src/main/resources/custom_interests.json"))
                .setCentralTendencyMeasure(new Mean())
                .build();

        int i = 0;
        // for Experiment -> Algorithm -> Sizes
        String outputDirectoryName = "";
        for (GroupingProblem problem: problems) {
            DecomposedSolution decomposedSolution = generateSolutionFromFile(problem, file);
            writeDecomposedSolutionResult(decomposedSolution, outputDirectoryName, i);
            i++;
        }
    }
    */

}
