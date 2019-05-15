package me.mackaber.tesis.SingleObjective;

import me.mackaber.tesis.ObjectiveFunctions.*;
import me.mackaber.tesis.SingleObjective.Algorithms.JamesTabuSearch;
import me.mackaber.tesis.SingleObjective.JamesTools.JamesAlgorithm;
import me.mackaber.tesis.Util.*;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.jamesframework.core.search.stopcriteria.MaxSteps;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SingleObjectiveGroupingJames {
    public static void main(String[] args) throws Exception {
        GroupingProblem problem = new GroupingProblem("Tesis/src/main/resources/synthetic_20.csv");

        WeightedFunction function = new NormalizedWeightedFunction()
                .addObjectiveFunction(1.0, new GroupSizeFunction(), 0.5, 1.5)
                .addObjectiveFunction(1.0, new ParticipationStyleFunction(), 0.001666, 1.0)
                .addObjectiveFunction(1.0, new LevelFunction(), 0.0, 2.82843)
                .addObjectiveFunction(1.0, new InterestsCosineSimilarityFunction(), 0.0, 1.0);

        problem.setGroupSizeRange(3, 6)
                .addObjectiveFunction(function)
                .setVector(new InterestVector("Tesis/src/main/resources/custom_interests.json"))
                .setCentralTendencyMeasure(new Mean())
                .build();

        CombinationNPointCrossoverWithoutRepair crossover = new CombinationNPointCrossoverWithoutRepair(0.9, problem.getNumberOfVariables());
        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        MutationOperator<GroupSolution> mutation = new GroupSwapMutation(mutationProbability, (CombinationProblem) problem);

        Comparator<GroupingSolution<List<User>>> comparator = new DominanceComparator<>(0);

        JamesAlgorithm<GroupSolution> tabu_search = new JamesTabuSearch<>(problem,
                mutation,1);

        tabu_search.getJamesAlgorithm().addStopCriterion(new MaxSteps(20));

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(tabu_search)
                .execute();

        GroupSolution solution = tabu_search.getResult();
        List<GroupSolution> population = new ArrayList<>(1);
        population.add(solution);

        long computingTime = algorithmRunner.getComputingTime() ;

        new SolutionListOutput(population)
                .setSeparator("\t")
                .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
                .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
                .print();

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
        JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
        JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
        JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
    }
}

