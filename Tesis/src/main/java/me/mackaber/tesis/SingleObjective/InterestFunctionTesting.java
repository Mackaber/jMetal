package me.mackaber.tesis.SingleObjective;

import me.mackaber.tesis.MultiObjective.CustomGeneticBuilder;
import me.mackaber.tesis.ObjectiveFunctions.*;
import me.mackaber.tesis.Util.GroupSwapMutation;
import me.mackaber.tesis.Util.SolutionImprovementOutput;
import me.mackaber.tesis.Util.User;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.NPointCrossover;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InterestFunctionTesting {
    public static void main(String[] args) throws Exception {
        JMetalRandom.getInstance().setSeed(120L);

        Algorithm<GroupingSolution<List<User>>> algorithm;
//        CrossoverOperator<GroupingSolution<List<User>>> crossover;
        MutationOperator<GroupingSolution<List<User>>> mutation;
        SelectionOperator<List<GroupingSolution<List<User>>>,GroupingSolution<List<User>>> selection;

//        SingleObjectiveGrouping problem = new SingleObjectiveGrouping("Tesis/src/main/resources/synthetic_2000.csv");
        SingleObjectiveGrouping problem = new SingleObjectiveGrouping("Tesis/src/main/resources/synthetic_10001.csv");


        problem.setGroupSizeRange(3, 6)
                .setObjectiveFunction(new InterestsCosineSimilarityFunction("Tesis/src/main/resources/custom_interests.json"))
//                .setObjectiveFunction(new LevelFunction())
                .setCentralTendencyMeasure(new Mean())
                .build();

        NPointCrossover crossover = new NPointCrossover(0.9,problem.getNumberOfVariables());
        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        mutation = new GroupSwapMutation<>(mutationProbability, problem);
        selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());

        int popSize = 200;
        int genNum = 210;

        algorithm = new CustomGeneticBuilder<>(problem, crossover, mutation)
                .setPopulationSize(popSize)
                .setMaxEvaluations(popSize*genNum)
                .setSelectionOperator(selection)
                .setSolutionListEvaluator(new MultithreadedSolutionListEvaluator<>(10, problem))
                .build();

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

        GroupingSolution<List<User>> solution = algorithm.getResult() ;
        List<GroupingSolution<List<User>>> population = new ArrayList<>(1) ;
        population.add(solution) ;

        long computingTime = algorithmRunner.getComputingTime() ;

        CustomGeneticBuilder.CustomGenerationanGeneticAlgorithm geneticAlgorithm = (CustomGeneticBuilder.CustomGenerationanGeneticAlgorithm) algorithm;

        new SolutionImprovementOutput(geneticAlgorithm.getImprovements())
                .setImpFileOutputContext(new DefaultFileOutputContext("IMP_Single_Local_interests_2.csv"))
                .print();

        new SolutionListOutput(population)
                .setSeparator("\t")
                .setVarFileOutputContext(new DefaultFileOutputContext("VAR_Single_genetic.tsv"))
                .setFunFileOutputContext(new DefaultFileOutputContext("FUN_Single_genetic.tsv"))
                .print();

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    }
}
