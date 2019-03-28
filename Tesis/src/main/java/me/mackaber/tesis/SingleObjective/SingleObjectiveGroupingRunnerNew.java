package me.mackaber.tesis.SingleObjective;

import me.mackaber.tesis.ObjectiveFunctions.*;
import me.mackaber.tesis.SingleObjective.Algorithms.LocalSearch;
import me.mackaber.tesis.Util.*;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import java.util.Comparator;

public class SingleObjectiveGroupingRunnerNew {
    public static void main(String[] args) throws Exception {
        JMetalRandom.getInstance().setSeed(120L);

        GroupingProblem problem = new GroupingProblem("Tesis/src/main/resources/synthetic_20.csv");

        WeightedFunction function = new WeightedFunction();
        function.addObjectiveFunction(1.0, new GroupSizeFunction())
                .addObjectiveFunction(1.0, new InterestsCosineSimilarityFunction())
                .addObjectiveFunction(1.0, new LevelFunction())
                .addObjectiveFunction(1.0, new ParticipationStyleFunction());

        problem.setGroupSizeRange(3, 6)
                .addObjectiveFunction(function)
                .setVector(new InterestVector("Tesis/src/main/resources/custom_interests.json"))
                .setCentralTendencyMeasure(new Mean())
                .build();

        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        MutationOperator<GroupSolution> mutationOperator = new GroupSwapMutation(mutationProbability, problem);

        int improvementRounds = 100000;

        Comparator<GroupSolution> comparator = new DominanceComparator<>(0);

        LocalSearchOperator<GroupSolution> localSearch = new LocalSearch<>(
                improvementRounds,
                mutationOperator,
                comparator,
                problem);

        GroupSolution solution = problem.createSolution();
        problem.evaluate(solution);
        GroupSolution newSolution = localSearch.execute(solution);

        DecomposedSolution decomposedSolution = new DecomposedSolution(newSolution);
        new SolutionListOutput(decomposedSolution.getPopulation())
                .setSeparator("\t")
                .setFunFileOutputContext(new DefaultFileOutputContext("DEC0.tsv"))
                .print();
        System.out.println(solution.getObjective(0));
        System.out.println(newSolution.getObjective(0));
    }
}
