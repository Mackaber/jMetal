package me.mackaber.tesis.MultiObjective;

import me.mackaber.tesis.ObjectiveFunctions.GroupSizeFunction;
import me.mackaber.tesis.ObjectiveFunctions.InterestsCosineSimilarityFunction;
import me.mackaber.tesis.ObjectiveFunctions.LevelFunction;
import me.mackaber.tesis.ObjectiveFunctions.ParticipationStyleFunction;
import me.mackaber.tesis.SingleObjective.GroupingSolution;
import me.mackaber.tesis.Util.GroupSwapMutation;
import me.mackaber.tesis.Util.User;
import org.apache.commons.math3.stat.descriptive.moment.GeometricMean;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.NPointCrossover;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.List;

public class MultiObjectiveGroupingRunner {
    public static void main(String[] args) throws Exception {
        JMetalRandom.getInstance().setSeed(120L);

        MultiObjectiveGrouping problem;
        Algorithm<List<GroupingSolution<List<User>>>> algorithm;
        //CrossoverOperator<GroupingSolution<List<User>>> crossover;
        MutationOperator<GroupingSolution<List<User>>> mutation;
        SelectionOperator<List<GroupingSolution<List<User>>>,GroupingSolution<List<User>>> selection;

        problem = new MultiObjectiveGrouping("Tesis/src/main/resources/synthetic_2000.csv");

        problem.setGroupSizeRange(3, 6)
                //.addObjectiveFunction(new GroupSizeFunction())
                //.addObjectiveFunction(new ParticipationStyleFunction())
                .addObjectiveFunction(new LevelFunction())
                .setInterestsFunction(new InterestsCosineSimilarityFunction("Tesis/src/main/resources/custom_interests.json"))
                .setCentralTendencyMeasure(new Mean())
                .build();


        int popSize = 200;
        int genNum = 210;

        NPointCrossover crossover = new NPointCrossover(0.9,problem.getNumberOfVariables());
        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        mutation = new GroupSwapMutation<>(mutationProbability, problem);
        selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());
        algorithm = new CustomNSGAIIBuilder(problem, crossover, mutation)
                        .setSelectionOperator(selection)
                        .setPopulationSize(popSize)
                        .setMaxEvaluations(genNum*popSize)
                        .setSolutionListEvaluator(new MultithreadedSolutionListEvaluator<>(10, problem))
                        .build();

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

        List<GroupingSolution<List<User>>> population = algorithm.getResult();
        long computingTime = algorithmRunner.getComputingTime();

        new SolutionListOutput(population)
                .setSeparator("\t")
                .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
                .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
                .print();

        CustomNSGAIIBuilder.CustomNSGAII alg = (CustomNSGAIIBuilder.CustomNSGAII) algorithm;

        JMetalLogger.logger.info("Improvement: " + alg.getImprovements());
        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
        JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
        JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
        JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
    }
}
