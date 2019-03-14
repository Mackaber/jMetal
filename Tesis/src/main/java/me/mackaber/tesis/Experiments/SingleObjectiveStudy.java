package me.mackaber.tesis.Experiments;

import me.mackaber.tesis.ObjectiveFunctions.*;
import me.mackaber.tesis.SingleObjective.Algorithms.JamesParallelTempering;
import me.mackaber.tesis.SingleObjective.Algorithms.LocalSearch;
import me.mackaber.tesis.SingleObjective.Algorithms.JamesRandomDescent;
import me.mackaber.tesis.SingleObjective.GroupingSolution;
import me.mackaber.tesis.SingleObjective.JamesTools.JamesAlgorithm;
import me.mackaber.tesis.SingleObjective.SingleObjectiveGrouping;
import me.mackaber.tesis.Util.CombinationProblem;
import me.mackaber.tesis.Util.GroupSwapMutation;
import me.mackaber.tesis.Util.SingleObjectiveExperimentAlgorithm;
import me.mackaber.tesis.Util.User;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.jamesframework.core.search.stopcriteria.MaxSteps;
import org.jamesframework.core.search.stopcriteria.MaxStepsWithoutImprovement;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.coralreefsoptimization.CoralReefsOptimizationBuilder;
import org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.EvolutionStrategyBuilder;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.NPointCrossover;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.ExecuteAlgorithms;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.IOException;
import java.util.*;

public class SingleObjectiveStudy {

    private static final int INDEPENDENT_RUNS = 20;

    public static void main(String[] args) throws IOException {
        JMetalRandom.getInstance().setSeed(120L);

        List<SingleObjectiveGrouping> problems = new ArrayList<>();

        // Problem Definition

        problems.add(new SingleObjectiveGrouping("Tesis/src/main/resources/synthetic_20.csv"));
        problems.add(new SingleObjectiveGrouping("Tesis/src/main/resources/synthetic_200.csv"));
//        problems.add(new SingleObjectiveGrouping("Tesis/src/main/resources/synthetic_2000.csv"));
//        problems.add(new SingleObjectiveGrouping("Tesis/src/main/resources/synthetic_10001.csv"));

        List<ExperimentProblem<GroupingSolution<List<User>>>> problemList = new ArrayList<>();

        // Weighted Function

        WeightedFunction function = new NormalizedWeightedFunction()
                .addObjectiveFunction(1.0, new GroupSizeFunction(), 0.5, 1.5)
                .addObjectiveFunction(1.0, new ParticipationStyleFunction(), 0.001666, 1.0)
                .addObjectiveFunction(1.0, new LevelFunction(), 0.0, 2.82843)
                .setInterestsFunction(1.0, new InterestsCosineSimilarityFunction("Tesis/src/main/resources/custom_interests.json"), 0.0, 1.0);


        for (SingleObjectiveGrouping problem : problems) {
            problem.setGroupSizeRange(3, 6)
                    .setCentralTendencyMeasure(new Mean())
                    .setObjectiveFunction(function)
                    .build();

            problemList.add(new ExperimentProblem<>(problem)); // dataset of 20
        }

        List<ExperimentAlgorithm<GroupingSolution<List<User>>, GroupingSolution<List<User>>>> algorithmList =
                configureAlgorithmList(problemList);

        Experiment<GroupingSolution<List<User>>, GroupingSolution<List<User>>> experiment =
                new ExperimentBuilder<GroupingSolution<List<User>>, GroupingSolution<List<User>>>("Thing Study")
                        .setAlgorithmList(algorithmList)
                        .setProblemList(problemList)
                        .setExperimentBaseDirectory(".")
                        .setOutputParetoFrontFileName("FUN")
                        .setOutputParetoSetFileName("VAR")
                        .setReferenceFrontDirectory("Tesis/src/main/resources/paretoFronts")
                        .setIndependentRuns(INDEPENDENT_RUNS)
                        .setNumberOfCores(5)
                        .setIndicatorList(Arrays.asList(
                                new PISAHypervolume<>(),
                                new GenerationalDistance<>()))
                        .build();

        new ExecuteAlgorithms<>(experiment).run();
        // new ComputeQualityIndicators<>(experiment).run();
    }


    static List<ExperimentAlgorithm<GroupingSolution<List<User>>, GroupingSolution<List<User>>>> configureAlgorithmList(
            List<ExperimentProblem<GroupingSolution<List<User>>>> problemList) {

        List<ExperimentAlgorithm<GroupingSolution<List<User>>, GroupingSolution<List<User>>>> algorithms = new ArrayList<>();

        int popSize = 20;
        int genNum = 200;
        String tag = String.format("%s_%s_%s_%s", INDEPENDENT_RUNS, popSize, genNum, new Date().getTime());


        SelectionOperator<List<GroupingSolution<List<User>>>, GroupingSolution<List<User>>> selection;
        selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());


        for (int run = 0; run < INDEPENDENT_RUNS; run++) {
            for (int i = 0; i < problemList.size(); i++) {

                // Parameters and stuff
                NPointCrossover crossover = new NPointCrossover(0.9, problemList.get(i).getProblem().getNumberOfVariables());

                double mutationProbability = 1.0 / problemList.get(i).getProblem().getNumberOfVariables();
                MutationOperator<GroupingSolution<List<User>>> mutation;
                mutation = new GroupSwapMutation<>(mutationProbability, (CombinationProblem) problemList.get(i).getProblem());

                // Genetic Algorithm

                Algorithm<GroupingSolution<List<User>>> genetic_generational = new GeneticAlgorithmBuilder<>(
                        problemList.get(i).getProblem(),
                        crossover,
                        mutation)
                        .setSelectionOperator(selection)
                        .setPopulationSize(popSize)
                        .setMaxEvaluations(genNum * popSize)
                        .setSolutionListEvaluator(new MultithreadedSolutionListEvaluator(10, problemList.get(i).getProblem()))
                        .build();


                Algorithm<GroupingSolution<List<User>>> genetic_steady = new GeneticAlgorithmBuilder<>(
                        problemList.get(i).getProblem(),
                        crossover,
                        mutation)
                        .setSelectionOperator(selection)
                        .setPopulationSize(popSize)
                        .setMaxEvaluations(genNum * popSize)
                        .setSolutionListEvaluator(new MultithreadedSolutionListEvaluator(10, problemList.get(i).getProblem()))
                        .setVariant(GeneticAlgorithmBuilder.GeneticAlgorithmVariant.STEADY_STATE)
                        .build();

                Algorithm<GroupingSolution<List<User>>> elitist = new EvolutionStrategyBuilder<>(
                        problemList.get(i).getProblem(),
                        mutation,
                        EvolutionStrategyBuilder.EvolutionStrategyVariant.ELITIST)
                        .setMaxEvaluations(genNum * popSize)
                        .build();

                Algorithm<GroupingSolution<List<User>>> non_elitist = new EvolutionStrategyBuilder<>(
                        problemList.get(i).getProblem(),
                        mutation,
                        EvolutionStrategyBuilder.EvolutionStrategyVariant.NON_ELITIST)
                        .setMaxEvaluations(genNum * popSize)
                        .build();

                Algorithm<GroupingSolution<List<User>>> coral = new CoralReefsOptimizationBuilder<>(
                        problemList.get(i).getProblem(),
                        selection,
                        crossover,
                        mutation)
                        .setM(10)
                        .setN(10)
                        .setRho(0.6)
                        .setFbs(0.9)
                        .setFbr(0.1)
                        .setFa(0.1)
                        .setPd(0.1)
                        .setAttemptsToSettle(3)
                        .setComparator(new ObjectiveComparator<>(0))
                        .setMaxEvaluations(genNum * popSize)
                        .build();

                // Local Search
                Comparator<GroupingSolution<List<User>>> comparator = new DominanceComparator<>(0);

                Algorithm<GroupingSolution<List<User>>> localSearch = new LocalSearch<>(100,
                        mutation,
                        comparator,
                        problemList.get(i).getProblem());


                JamesAlgorithm<GroupingSolution<List<User>>> random_descent = new JamesRandomDescent<>(
                        problemList.get(i).getProblem(),
                        mutation);
                random_descent.getJamesAlgorithm().addStopCriterion(new MaxStepsWithoutImprovement(100));

                JamesAlgorithm<GroupingSolution<List<User>>> parallel_tempering = new JamesParallelTempering<>(
                        problemList.get(i).getProblem(),
                        mutation);
                parallel_tempering.getJamesAlgorithm().addStopCriterion(new MaxSteps(5));


//                algorithms.add(new SingleObjectiveExperimentAlgorithm<>(genetic_steady, "Genetic_Steady_" + tag, problemList.get(i), run));
//                algorithms.add(new SingleObjectiveExperimentAlgorithm<>(genetic_generational, "Genetic_Generational_" + tag, problemList.get(i), run));
//                algorithms.add(new SingleObjectiveExperimentAlgorithm<>(elitist, "Elitist_" + tag, problemList.get(i), run));
//                algorithms.add(new SingleObjectiveExperimentAlgorithm<>(non_elitist, "NON_ELITIST_" + tag, problemList.get(i), run));
//                algorithms.add(new SingleObjectiveExperimentAlgorithm<>(localSearch, "Local_Search_" + tag, problemList.get(i), run));
//                algorithms.add(new SingleObjectiveExperimentAlgorithm<>(random_descent, "Random_Descend_" + tag, problemList.get(i), run));
                algorithms.add(new SingleObjectiveExperimentAlgorithm<>(parallel_tempering, "Parallel_Tempering_" + tag, problemList.get(i), run));
                //algorithms.add(new SingleObjectiveExperimentAlgorithm<>(coral, "Coral_" + tag, problemList.get(i), run));

            }
        }
        return algorithms;
    }
}

