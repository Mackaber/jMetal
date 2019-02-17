package me.mackaber.tesis.Scratches;

import me.mackaber.tesis.MultiObjective.CustomNSGAIIBuilder;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.PMXCrossover;
import org.uma.jmetal.operator.impl.mutation.PermutationSwapMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.PermutationProblem;
import org.uma.jmetal.problem.multiobjective.MultiobjectiveTSP;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.List;

public class TspwithcustomNSA {
    public static void main(String[] args) throws Exception {

        JMetalRandom.getInstance().setSeed(100L);

        PermutationProblem<PermutationSolution<Integer>> problem;
        Algorithm<List<PermutationSolution<Integer>>> algorithm;
        CrossoverOperator<PermutationSolution<Integer>> crossover;
        MutationOperator<PermutationSolution<Integer>> mutation;
        SelectionOperator<List<PermutationSolution<Integer>>, PermutationSolution<Integer>> selection;

        problem = new MultiobjectiveTSP("/tspInstances/kroA100.tsp", "/tspInstances/kroB100.tsp");

        crossover = new PMXCrossover(0.9) ;

        double mutationProbability = 0.2 ;
        mutation = new PermutationSwapMutation<Integer>(mutationProbability) ;

        selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());
/**
 * List<Double> inters = new ArrayList<>();
 inters.add(0.0);
 inters.add(0.0);
 double epsilon =0.0001;
 algorithm = new RNSGAIIBuilder<>(problem, crossover, mutation,inters,epsilon)

 */
        algorithm = new CustomNSGAIIBuilder<PermutationSolution<Integer>>(problem, crossover, mutation)
                .setSelectionOperator(selection)
                .setMaxEvaluations(10000)
                .setPopulationSize(10)
                .build() ;

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute() ;

        List<PermutationSolution<Integer>> population = algorithm.getResult() ;
        long computingTime = algorithmRunner.getComputingTime() ;

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
