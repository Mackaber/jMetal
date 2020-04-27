package me.mackaber.tesis.Experiments;

import me.mackaber.tesis.ObjectiveFunctions.*;
import me.mackaber.tesis.SingleObjective.Algorithms.*;
import me.mackaber.tesis.SingleObjective.GroupSolution;
import me.mackaber.tesis.SingleObjective.GroupingProblem;
import me.mackaber.tesis.SingleObjective.JamesTools.JamesAlgorithm;
import me.mackaber.tesis.SingleObjective.JamesTools.ProgressSearchListener;
import me.mackaber.tesis.Util.*;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.jamesframework.core.search.stopcriteria.MaxRuntime;
import org.jamesframework.core.search.stopcriteria.MaxSteps;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.EvolutionStrategyBuilder;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.WFGHypervolume;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.ComputeQualityIndicators;
import org.uma.jmetal.util.experiment.component.ExecuteAlgorithms;
import org.uma.jmetal.util.experiment.component.GenerateLatexTablesWithStatistics;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SingleObjectiveStudy {

    private static final int INDEPENDENT_RUNS = 2;

    public static void main(String[] args) throws IOException {
        JMetalRandom.getInstance().setSeed(120L);

        List<GroupingProblem> problems = new ArrayList<>();

        // Problem Definition

        problems.add(new GroupingProblem("Tesis/src/main/resources/synthetic_20.csv"));
        //problems.add(new GroupingProblem("Tesis/src/main/resources/synthetic_200.csv"));
        //problems.add(new GroupingProblem("Tesis/src/main/resources/synthetic_2000.csv"));
        //problems.add(new GroupingProblem("Tesis/src/main/resources/synthetic_10001.csv"));

        List<ExperimentProblem<GroupSolution>> problemList = new ArrayList<>();

        // Weighted Function

        /*WeightedFunction function = new NormalizedWeightedFunction()
                .addObjectiveFunction(1.0, new GroupSizeFunction(), 0.5, 1.5)
                .addObjectiveFunction(1.0, new ParticipationStyleFunction(), 0.001666, 1.0)
                .addObjectiveFunction(1.0, new LevelFunction(), 0.0, 2.82843)
                .addObjectiveFunction(1.0, new InterestsCosineSimilarityFunction(), 0.0, 1.0);*/


//        WeightedFunction function = new WeightedFunction()
//                .addObjectiveFunction(0.25,new GroupSizeFunction())
//                .addObjectiveFunction(0.250417,new ParticipationStyleFunction())
//                .addObjectiveFunction(0.0883883, new LevelFunction())
//                .addObjectiveFunction(0.25 , new InterestsCosineSimilarityFunction());


//        ArrayList<Function> functions = new ArrayList<>();
//        functions.add(new GroupSizeFunction());
//        functions.add(new ParticipationStyleFunction());
//        functions.add(new LevelFunction());
//        functions.add(new InterestsCosineSimilarityFunction());

        for (GroupingProblem problem : problems) {
            problem.setGroupSizeRange(3, 6)
                    .setType(GroupingProblem.Type.SINGLE_OBJECTIVE)
                    .addObjectiveFunction(new GroupSizeFunction())
                    .addObjectiveFunction(new ParticipationStyleFunction())
                    .addObjectiveFunction(new LevelFunction())
                    .addObjectiveFunction(new InterestsCosineSimilarityFunction())
                    .setVector(new InterestVector("Tesis/src/main/resources/custom_interests.json"))
                    .setCentralTendencyMeasure(new Mean())
                    .build();
            problemList.add(new ExperimentProblem<>(problem)); // dataset of 20
        }

        List<ExperimentAlgorithm<GroupSolution, List<GroupSolution>>> algorithmList =
                configureAlgorithmList(problemList);

        Experiment<GroupSolution, List<GroupSolution>> experiment =
                new ExperimentBuilder<GroupSolution, List<GroupSolution>>("TEST")
                        .setAlgorithmList(algorithmList)
                        .setProblemList(problemList)
                        .setExperimentBaseDirectory(".")
                        .setOutputParetoFrontFileName("FUN")
                        .setOutputParetoSetFileName("VAR")
                        .setReferenceFrontDirectory("Tesis/src/main/resources/paretoFronts")
                        .setIndependentRuns(INDEPENDENT_RUNS)
                        .setNumberOfCores(5)
                        .setIndicatorList(Arrays.asList(
                                new Epsilon<>(),
                                new Spread<>(),
                                new GenerationalDistance<>(),
                                new InvertedGenerationalDistance<>(),
                                new InvertedGenerationalDistancePlus<>()
                                //new WFGHypervolume<>()
                                )
                        ).build();

        new ExecuteAlgorithms<>(experiment).run();
        new  ComputeQualityIndicators<>(experiment).run();
        //new GenerateLatexTablesWithStatistics(experiment).run();
    }


    static List<ExperimentAlgorithm<GroupSolution, List<GroupSolution>>> configureAlgorithmList(
            List<ExperimentProblem<GroupSolution>> problemList) throws IOException {

        List<ExperimentAlgorithm<GroupSolution, List<GroupSolution>>> algorithms = new ArrayList<>();

        int popSize = 10;
        int genNum = 10;
        String tag = String.format("%s_%s_%s_%s", INDEPENDENT_RUNS, popSize, genNum, new Date().getTime());


        SelectionOperator<List<GroupSolution>, GroupSolution> selection;
        selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());


        double[][] weights = new double[24][4];

        for (int run = 0; run < INDEPENDENT_RUNS; run++) {
            double[] weight = weights[run];
            for (int i = 0; i < problemList.size(); i++) {

                GroupingProblem problem = (GroupingProblem) problemList.get(i).getProblem();

                // Parameters and stuff
                //CombinationNPointCrossover crossover = new CombinationNPointCrossover(0.9, problemList.get(i).getProblem().getNumberOfVariables());
                CombinationNPointCrossoverWithoutRepair crossover = new CombinationNPointCrossoverWithoutRepair(0.9, problemList.get(i).getProblem().getNumberOfVariables());


                double mutationProbability = 1.0 / problemList.get(i).getProblem().getNumberOfVariables();
                MutationOperator<GroupSolution> mutation = new GroupSwapMutation(mutationProbability, (CombinationProblem) problemList.get(i).getProblem());

                // Genetic Algorithm

                Algorithm<GroupSolution> genetic_generational = new GeneticAlgorithmBuilder<>(
                        problem,
                        crossover,
                        mutation)
                        .setSelectionOperator(selection)
                        .setPopulationSize(popSize)
                        .setMaxEvaluations(genNum * popSize)
                        .setSolutionListEvaluator(new MultithreadedSolutionListEvaluator(10, problemList.get(i).getProblem()))
                        .build();


                Algorithm<GroupSolution> genetic_steady = new GeneticAlgorithmBuilder<>(
                        problem,
                        crossover,
                        mutation)
                        .setSelectionOperator(selection)
                        .setPopulationSize(popSize)
                        .setMaxEvaluations(genNum * popSize)
                        .setSolutionListEvaluator(new MultithreadedSolutionListEvaluator(10, problemList.get(i).getProblem()))
                        .setVariant(GeneticAlgorithmBuilder.GeneticAlgorithmVariant.STEADY_STATE)
                        .build();

                Algorithm<GroupSolution> elitist = new EvolutionStrategyBuilder<>(
                        problemList.get(i).getProblem(),
                        mutation,
                        EvolutionStrategyBuilder.EvolutionStrategyVariant.ELITIST)
                        .setMaxEvaluations(genNum * popSize)
                        .build();

                Algorithm<GroupSolution> non_elitist = new EvolutionStrategyBuilder<>(
                        problemList.get(i).getProblem(),
                        mutation,
                        EvolutionStrategyBuilder.EvolutionStrategyVariant.NON_ELITIST)
                        .setMaxEvaluations(genNum * popSize)
                        .build();

                // Local Search
                Comparator<GroupSolution> comparator = new DominanceComparator<>(0);

                Algorithm<GroupSolution> localSearch = new LocalSearch<>(100,
                        mutation,
                        comparator,
                        problemList.get(i).getProblem());

                JamesAlgorithm<GroupSolution> random_descent = new JamesRandomDescent<>(
                        problemList.get(i).getProblem(),
                        mutation);
                random_descent.getJamesAlgorithm().addStopCriterion(new MaxSteps(20));

                double minTemp = 1 * 1e-8;
                double maxTemp = 1 * 0.6;
                int numReplicas = 2;

                JamesAlgorithm<GroupSolution> parallel_tempering = new JamesParallelTempering<>(
                        problemList.get(i).getProblem(),
                        mutation, minTemp, maxTemp, numReplicas);
                parallel_tempering.getJamesAlgorithm().addStopCriterion(new MaxSteps(200));

                int memorySize = 2000;

                JamesAlgorithm<GroupSolution> tabu_search = new JamesTabuSearch<>(
                        problemList.get(i).getProblem(),
                        mutation, memorySize);
                tabu_search.getJamesAlgorithm().addStopCriterion(new MaxRuntime(60, TimeUnit.SECONDS));
                tabu_search.getJamesAlgorithm().addSearchListener(new ProgressSearchListener());

                JamesAlgorithm<GroupSolution> random_search = new JamesRandomSearch<>(
                        problemList.get(i).getProblem(),
                        mutation);
                random_search.getJamesAlgorithm().addStopCriterion(new MaxSteps(200));


                // ----------- MULTIOBJECTIVE --------------


                Algorithm<List<GroupSolution>> nsgaii = new NSGAIIBuilder<>(
                        problemList.get(i).getProblem(),
                        crossover,
                        mutation)
                        .setSelectionOperator(selection)
                        .setPopulationSize(popSize)
                        .setMaxEvaluations(genNum * popSize)
                        .setSolutionListEvaluator(new MultithreadedSolutionListEvaluator<>(4, problemList.get(i).getProblem()))
                        .build();


                algorithms.add(new SingleObjectiveExperimentAlgorithm<>(genetic_steady, "Genetic_Steady_" + tag, problemList.get(i), run));
                algorithms.add(new SingleObjectiveExperimentAlgorithm<>(genetic_generational, "Genetic_Generational_" + tag + "_" + problemList.get(i).getProblem(), problemList.get(i), run));
                //algorithms.add(new SingleObjectiveExperimentAlgorithm<>(elitist, "Elitist_" + tag, problemList.get(i), run));
                //algorithms.add(new SingleObjectiveExperimentAlgorithm<>(non_elitist, "NON_ELITIST_" + tag, problemList.get(i), run));
                //algorithms.add(new SingleObjectiveExperimentAlgorithm<>(localSearch, "Local_Search_" + tag, problemList.get(i), run));
                //algorithms.add(new SingleObjectiveExperimentAlgorithm<>(random_descent, "Random_Descend_" + tag, problemList.get(i), run));
                //algorithms.add(new SingleObjectiveExperimentAlgorithm<>(parallel_tempering, "Parallel_Tempering_" + tag, problemList.get(i), run));
                //algorithms.add(new SingleObjectiveExperimentAlgorithm<>(tabu_search, "Tabu_Search_" + tag, problemList.get(i), run));
                //algorithms.add(new SingleObjectiveExperimentAlgorithm<>(random_search, "Random_Search_" + tag, problemList.get(i), run));
                //algorithms.add(new ExperimentAlgorithm<>(nsgaii, "Random_Descend_" + tag, problemList.get(i), run));
            }
        }
        return algorithms;
    }
}

