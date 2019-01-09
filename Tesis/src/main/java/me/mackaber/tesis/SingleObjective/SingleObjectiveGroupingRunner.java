package me.mackaber.tesis.SingleObjective;

import me.mackaber.tesis.ObjectiveFunctions.*;
import me.mackaber.tesis.Util.GroupSwapMutation;
import me.mackaber.tesis.Util.User;
import org.apache.commons.math3.stat.descriptive.moment.GeometricMean;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.localsearch.BasicLocalSearch;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SingleObjectiveGroupingRunner {
    public static void main(String[] args) throws Exception {
        JMetalRandom.getInstance().setSeed(120L);

        SingleObjectiveGrouping problem = new SingleObjectiveGrouping("Tesis/src/main/resources/synthetic_200.csv");

        WeightedFunction function = new WeightedFunction("Tesis/src/main/resources/custom_interests.json");
        function.setW1(1.0); // Group Size
        function.setW2(1.0); // Interests
        function.setW3(1.0); // Level
        function.setW4(1.0); // Participation Style

        problem.setGroupSizeRange(3, 6)
                .setObjectiveFunction(function)
                .setCentralTendencyMeasure(new Mean())
                .build();

        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        MutationOperator<GroupingSolution<List<User>>> mutationOperator =
                new GroupSwapMutation<>(mutationProbability, problem);

        int improvementRounds = 25000;

        Comparator<GroupingSolution<List<User>>> comparator = new DominanceComparator<>(0);

        List<Double> improvement = new ArrayList<>();

        LocalSearchOperator<GroupingSolution<List<User>>> localSearch = new CustomLocalSearch<>(
                improvementRounds,
                mutationOperator,
                comparator,
                problem,
                improvement::add);

        GroupingSolution solution = problem.createSolution();
        problem.evaluate(solution);
        GroupingSolution newSolution = localSearch.execute(solution);


        JMetalLogger.logger.info("Improvement: " + improvement);
        JMetalLogger.logger.info("Fitness: " + newSolution.getObjective(0)) ;
        JMetalLogger.logger.info("Group Size: " + newSolution.evaluate(new GroupSizeFunction()));
        JMetalLogger.logger.info("Interests: " + newSolution.evaluate(new InterestsCosineSimilarityFunction("Tesis/src/main/resources/custom_interests.json")));
        JMetalLogger.logger.info("Level: " + newSolution.evaluate(new LevelFunction()));
        JMetalLogger.logger.info("Participation Style: " + newSolution.evaluate(new ParticipationStyleFunction()));

        JMetalLogger.logger.info("Solution: " + newSolution.getSampleSolution(3)) ;
        System.out.print(solution.getObjective(0));
    }
}
