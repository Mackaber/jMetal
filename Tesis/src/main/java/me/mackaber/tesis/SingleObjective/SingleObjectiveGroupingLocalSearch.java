package me.mackaber.tesis.SingleObjective;

import me.mackaber.tesis.ObjectiveFunctions.*;
import me.mackaber.tesis.Util.GroupSwapMutation;
import me.mackaber.tesis.Util.User;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.localsearch.BasicLocalSearch;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.DominanceComparator;

import java.util.Comparator;
import java.util.List;

public class SingleObjectiveGroupingLocalSearch {
    public static void main(String[] args) throws Exception {
        SingleObjectiveGrouping problem = new SingleObjectiveGrouping("res/synthetic_10001.csv");

        WeightedFunction function = new WeightedFunction();
        function.addObjectiveFunction(1.0, new GroupSizeFunction())
//                .addObjectiveFunction(1.0, new InterestsCosineSimilarityFunction("Tesis/src/main/resources/custom_interests.json"))
                .addObjectiveFunction(1.0, new LevelFunction())
                .addObjectiveFunction(1.0, new ParticipationStyleFunction());

        problem.setGroupSizeRange(3, 6)
                .setObjectiveFunction(function)
                .setCentralTendencyMeasure(new Mean())
                .build();

//        MutationOperator<GroupingSolution<List<User>>> mutationOperator =
//                new GroupSwapMutation<>(0.02, problem);

        int improvementRounds = 100;

        Comparator<GroupingSolution<List<User>>> comparator = new DominanceComparator<>(0);

//        LocalSearchOperator<GroupingSolution<List<User>>> localSearch = new BasicLocalSearch<>(
//                improvementRounds,
//                mutationOperator,
//                comparator,
//                problem);

        GroupingSolution solution = problem.createSolution();
        problem.evaluate(solution);
//        GroupingSolution newSolution = localSearch.execute(solution);

//        JMetalLogger.logger.info("Fitness: " + newSolution.getObjective(0));
//        JMetalLogger.logger.info("Solution: " + newSolution.getSampleSolution(3));
    }
}

