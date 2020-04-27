package me.mackaber.tesis.Experiments;

import me.mackaber.tesis.ObjectiveFunctions.*;
import me.mackaber.tesis.SingleObjective.GroupSolution;
import me.mackaber.tesis.SingleObjective.GroupingProblem;
import me.mackaber.tesis.SingleObjective.SingleObjectiveGrouping;
import me.mackaber.tesis.Util.CombinationNPointCrossoverWithoutRepair;
import me.mackaber.tesis.Util.CombinationProblem;
import me.mackaber.tesis.Util.GroupSwapMutation;
import me.mackaber.tesis.Util.InterestVector;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.util.ArrayList;
import java.util.List;

public class Single_Run {
    public static void main(String[] args) throws Exception {
        GroupingProblem problem = new GroupingProblem("Tesis/src/main/resources/synthetic_10.csv");

        problem .setType(GroupingProblem.Type.SINGLE_OBJECTIVE)
                .addObjectiveFunction(new GroupSizeFunction())
                .addObjectiveFunction(new InterestsCosineSimilarityFunction())
                .addObjectiveFunction(new LevelFunction())
                .setVector(new InterestVector("Tesis/src/main/resources/custom_interests.json"))
                .setCentralTendencyMeasure(new Mean())
                .build();

        //problem.setWeights(0.4,0.2,0.3,0.1);

        SelectionOperator<List<GroupSolution>, GroupSolution> selection;
        selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());

        CombinationNPointCrossoverWithoutRepair crossover = new CombinationNPointCrossoverWithoutRepair(0.9, problem.getNumberOfVariables());

        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        MutationOperator<GroupSolution> mutation = new GroupSwapMutation(mutationProbability, problem);

        Algorithm<GroupSolution> genetic_generational = new GeneticAlgorithmBuilder<>(
                problem,
                crossover,
                mutation)
                .setSelectionOperator(selection)
                .setPopulationSize(2000)
                .setMaxEvaluations(2000)
                .setSolutionListEvaluator(new MultithreadedSolutionListEvaluator(3, problem))
                .build();

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(genetic_generational)
                .execute() ;

        GroupSolution solution = genetic_generational.getResult();;
        List<GroupSolution> population = new ArrayList<>(1) ;
        population.add(solution) ;

        long computingTime = algorithmRunner.getComputingTime() ;

        new SolutionListOutput(population)
                .setSeparator("\t")
                .setVarFileOutputContext(new DefaultFileOutputContext("TEST_VAR.tsv"))
                .setFunFileOutputContext(new DefaultFileOutputContext("TEST_FUN.tsv"))
                .print();

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
        JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
        JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

    }

}
