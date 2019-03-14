package me.mackaber.tesis.Scratches;

import me.mackaber.tesis.ObjectiveFunctions.*;
import me.mackaber.tesis.SingleObjective.Algorithms.JamesRandomDescent;
import me.mackaber.tesis.SingleObjective.Algorithms.JamesSteepestDescent;
import me.mackaber.tesis.SingleObjective.GroupingSolution;
import me.mackaber.tesis.SingleObjective.JamesTools.JamesAlgorithm;
import me.mackaber.tesis.SingleObjective.JamesTools.ProgressSearchListener;
import me.mackaber.tesis.SingleObjective.SingleObjectiveGrouping;
import me.mackaber.tesis.Util.GroupSwapMutation;
import me.mackaber.tesis.Util.User;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.jamesframework.core.search.stopcriteria.MaxStepsWithoutImprovement;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.List;

public class RunWithJames {

    public static void main(String[] args) throws Exception {
        JMetalRandom.getInstance().setSeed(120L);


        SingleObjectiveGrouping problem = new SingleObjectiveGrouping("Tesis/src/main/resources/synthetic_20.csv");

        WeightedFunction function = new WeightedFunction();
        function.addObjectiveFunction(1.0, new GroupSizeFunction())
                .setInterestsFunction(1.0, new InterestsCosineSimilarityFunction("Tesis/src/main/resources/custom_interests.json"))
                .addObjectiveFunction(1.0, new LevelFunction())
                .addObjectiveFunction(1.0, new ParticipationStyleFunction());

        problem.setGroupSizeRange(3, 6)
                .setObjectiveFunction(function)
                .setCentralTendencyMeasure(new Mean())
                .build();

        MutationOperator<GroupingSolution<List<User>>> mutationOperator =
                new GroupSwapMutation<>(1.0, problem);


        JamesAlgorithm<GroupingSolution<List<User>>> random_descend = new JamesRandomDescent<>(
                problem,
                mutationOperator);
        random_descend.getJamesAlgorithm().addStopCriterion(new MaxStepsWithoutImprovement(10000));
        random_descend.getJamesAlgorithm().addSearchListener(new ProgressSearchListener());


//        JamesAlgorithm<GroupingSolution<List<User>>> parallelTempering = new JamesParallelTempering<>(
//                problem,
//                mutationOperator);
//        parallelTempering.getJamesAlgorithm().addStopCriterion(new MaxStepsWithoutImprovement(20));
//        parallelTempering.getJamesAlgorithm().addSearchListener(new ProgressSearchListener());


        JamesAlgorithm<GroupingSolution<List<User>>> steepest_descend = new JamesSteepestDescent<>(
                problem,
                mutationOperator);
        steepest_descend.getJamesAlgorithm().addStopCriterion(new MaxStepsWithoutImprovement(2000));
        steepest_descend.getJamesAlgorithm().addSearchListener(new ProgressSearchListener());


        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(steepest_descend).execute();
        long computingTime = algorithmRunner.getComputingTime();
        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
        GroupingSolution<List<User>> solution = steepest_descend.getResult();
        System.out.print(solution);
        System.out.print(solution.getObjective(0));

    }

}
