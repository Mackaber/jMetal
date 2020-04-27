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
import org.uma.jmetal.algorithm.multiobjective.espea.ESPEABuilder;
import org.uma.jmetal.algorithm.multiobjective.espea.util.EnergyArchive;
import org.uma.jmetal.algorithm.multiobjective.mombi.MOMBI2;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.paes.PAESBuilder;
import org.uma.jmetal.algorithm.multiobjective.randomsearch.RandomSearchBuilder;
import org.uma.jmetal.algorithm.multiobjective.rnsgaii.RNSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.smsemoa.SMSEMOABuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.EvolutionStrategyBuilder;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.WFGHypervolume;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.*;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BOTHStudy {

    private static final int INDEPENDENT_RUNS = 30;

    public static void main(String[] args) throws IOException {
        JMetalRandom.getInstance().setSeed(120L);

        List<GroupingProblem> problems = new ArrayList<>();

        // Problem Definition

        problems.add(new GroupingProblem("Tesis/src/main/resources/synthetic_20.csv"));
        problems.add(new GroupingProblem("Tesis/src/main/resources/synthetic_200.csv"));
        problems.add(new GroupingProblem("Tesis/src/main/resources/synthetic_2000.csv"));
        problems.add(new GroupingProblem("Tesis/src/main/resources/synthetic_10001.csv"));

        List<ExperimentProblem<GroupSolution>> problemList = new ArrayList<>();


        for (GroupingProblem problem : problems) {
            problem.setGroupSizeRange(3, 6)
                    .addObjectiveFunction(new GroupSizeFunction())
                    .addObjectiveFunction(new ParticipationStyleFunction())
                    .addObjectiveFunction(new LevelFunction())
                    .addObjectiveFunction(new InterestsCosineSimilarityFunction())
                    .setVector(new InterestVector("Tesis/src/main/resources/custom_interests.json"))
                    .setCentralTendencyMeasure(new Mean())
                    .build();

            problemList.add(new ExperimentProblem<>(problem)); // dataset of 20
        }

        NormalizedWeightedFunction function = new NormalizedWeightedFunction()
                .addObjectiveFunction(1.0, new GroupSizeFunction(), 0.5, 1.5)
                .addObjectiveFunction(1.0, new ParticipationStyleFunction(), 0.001666, 1.0)
                .addObjectiveFunction(1.0, new LevelFunction(), 0.0, 2.82843)
                .addObjectiveFunction(1.0, new InterestsCosineSimilarityFunction(), 0.0, 1.0);


        List<ExperimentAlgorithm<GroupSolution, List<GroupSolution>>> algorithmList =
                configureAlgorithmList(problemList);

        Experiment<GroupSolution, List<GroupSolution>> experiment =
                new ExperimentBuilder<GroupSolution, List<GroupSolution>>("BOTH")
                        .setAlgorithmList(algorithmList)
                        .setProblemList(problemList)
                        .setExperimentBaseDirectory(".")
                        .setOutputParetoFrontFileName("DEC")
                        .setOutputParetoSetFileName("VAR")
                        .setReferenceFrontDirectory("Tesis/src/main/resources/paretoFronts")
                        .setIndependentRuns(INDEPENDENT_RUNS)
                        .setNumberOfCores(5)
                        .setIndicatorList(Arrays.asList(
                                new Epsilon<>(),
                                new Spread<>(),
                                new GenerationalDistance<>(),
                                new InvertedGenerationalDistance<>(),
                                new InvertedGenerationalDistancePlus<>(),
                                new SingleObjectiveFunction<>(function)
                        ))
                        .build();

        //new ExecuteAlgorithms<>(experiment).run();
        new ComputeQualityIndicatorsWoNorm<>(experiment).run();
        new GenerateLatexTablesWithStatistics(experiment).run();
        new GenerateWilcoxonTestTablesWithR<>(experiment).run();
        new GenerateFriedmanTestTables<>(experiment).run();
        new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(3).run();
    }


    static List<ExperimentAlgorithm<GroupSolution, List<GroupSolution>>> configureAlgorithmList(
            List<ExperimentProblem<GroupSolution>> problemList) {

        List<ExperimentAlgorithm<GroupSolution, List<GroupSolution>>> algorithms = new ArrayList<>();

        int popSize = 20;
        int genNum = 200;
        String tag = String.format("%s_%s_%s_%s", INDEPENDENT_RUNS, popSize, genNum, new Date().getTime());


        SelectionOperator<List<GroupSolution>, GroupSolution> selection;
        selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());


        for (int run = 0; run < INDEPENDENT_RUNS; run++) {
            for (int i = 0; i < problemList.size(); i++) {

                // Parameters and stuff
                CombinationNPointCrossover crossover = new CombinationNPointCrossover(0.9, problemList.get(i).getProblem().getNumberOfVariables());

                double mutationProbability = 1.0 / problemList.get(i).getProblem().getNumberOfVariables();
                MutationOperator<GroupSolution> mutation = new GroupSwapMutation(mutationProbability, (CombinationProblem) problemList.get(i).getProblem());
                //MutationOperator<GroupSolution> mutation = new GroupRandomMutation<>(mutationProbability);

                // Genetic Algorithm

                Algorithm<GroupSolution> gga = new GeneticAlgorithmBuilder<>(
                        problemList.get(i).getProblem(),
                        crossover,
                        mutation)
                        .setSelectionOperator(selection)
                        .setPopulationSize(popSize)
                        .setMaxEvaluations(genNum * popSize)
                        .build();
                /*
                Algorithm<GroupSolution> genetic_generational = new GeneticAlgorithmBuilder<>(
                        problemList.get(i).getProblem(),
                        crossover,
                        mutation)
                        .setSelectionOperator(selection)
                        .setPopulationSize(popSize)
                        .setMaxEvaluations(genNum * popSize)
                        .setSolutionListEvaluator(new MultithreadedSolutionListEvaluator(10, problemList.get(i).getProblem()))
                        .build();
                   */

                Algorithm<GroupSolution> genetic_steady = new GeneticAlgorithmBuilder<>(
                        problemList.get(i).getProblem(),
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
                        mutation,minTemp,maxTemp,numReplicas);
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

                ESPEABuilder builder = new ESPEABuilder<>(problemList.get(i).getProblem(), crossover, mutation);
                builder.setMaxEvaluations(25000);
                builder.setPopulationSize(100);
                builder.setReplacementStrategy(EnergyArchive.ReplacementStrategy.WORST_IN_ARCHIVE);
                Algorithm<List<GroupSolution>> espea = builder.build();

                Algorithm<List<GroupSolution>> mombi = new MOMBI2<>(problemList.get(i).getProblem(), 750, crossover, mutation, selection, new SequentialSolutionListEvaluator<>(),
                        "mombi2-weights/weight/gecco/weight_04D_2.sld");

                Algorithm<List<GroupSolution>> nsgaii = new NSGAIIBuilder<>(
                        problemList.get(i).getProblem(),
                        crossover,
                        mutation)
                        .setSelectionOperator(selection)
                        .setPopulationSize(popSize)
                        .setMaxEvaluations(genNum * popSize)
                        .setSolutionListEvaluator(new MultithreadedSolutionListEvaluator<>(10, problemList.get(i).getProblem()))
                        .build();

                Algorithm<List<GroupSolution>> nsgaiii = new NSGAIIIBuilder<>(problemList.get(i).getProblem())
                        .setCrossoverOperator(crossover)
                        .setMutationOperator(mutation)
                        .setSelectionOperator(selection)
                        .setMaxIterations(300)
                        .build();

                Algorithm<List<GroupSolution>> paes = new PAESBuilder<>(problemList.get(i).getProblem())
                        .setMutationOperator(mutation)
                        .setMaxEvaluations(25000)
                        .setArchiveSize(100)
                        .setBiSections(5)
                        .build();

                Algorithm<List<GroupSolution>> randomSearch = new RandomSearchBuilder<>(problemList.get(i).getProblem())
                        .setMaxEvaluations(25000)
                        .build();

                List<Double> referencePoint = new ArrayList<>();
                referencePoint.add(0.1);
                referencePoint.add(0.6);
                referencePoint.add(0.3);
                referencePoint.add(0.6);
                referencePoint.add(0.5);
                referencePoint.add(0.2);
                referencePoint.add(0.7);
                referencePoint.add(0.2);
                referencePoint.add(0.9);
                referencePoint.add(0.0);

                double epsilon = 0.0045;
                Algorithm<List<GroupSolution>> rnsgaii = new RNSGAIIBuilder<>(problemList.get(i).getProblem(), crossover, mutation, referencePoint, epsilon)
                        .setSelectionOperator(selection)
                        .setMaxEvaluations(2500)
                        .setPopulationSize(20)
                        .build();

                Hypervolume<GroupSolution> hypervolume;
                hypervolume = new PISAHypervolume<>();
                hypervolume.setOffset(100.0);
                Algorithm<List<GroupSolution>> smsemoa = new SMSEMOABuilder<>(problemList.get(i).getProblem(), crossover, mutation)
                        .setSelectionOperator(selection)
                        .setMaxEvaluations(2500)
                        .setPopulationSize(20)
                        .setHypervolumeImplementation(hypervolume)
                        .build();

                Algorithm<List<GroupSolution>> spea2 = new SPEA2Builder<>(problemList.get(i).getProblem(), crossover, mutation)
                        .setSelectionOperator(selection)
                        .setMaxIterations(250)
                        .setPopulationSize(100)
                        .build();

                // SINGLE
               //algorithms.add(new ExperimentAlgorithm<>(gga, gga.getName(), problemList.get(i), run));
                algorithms.add(new ExperimentAlgorithm<>(espea, espea.getName(), problemList.get(i), run)); // OK
                algorithms.add(new ExperimentAlgorithm<>(mombi, mombi.getName(), problemList.get(i), run)); // OK
                algorithms.add(new ExperimentAlgorithm<>(nsgaii, nsgaii.getName(), problemList.get(i), run)); // OK
                //algorithms.add(new ExperimentAlgorithm<>(nsgaiii, nsgaiii.getName(), problemList.get(i), run)); // OK
                //algorithms.add(new ExperimentAlgorithm<>(paes, paes.getName(), problemList.get(i), run)); // OK
                algorithms.add(new ExperimentAlgorithm<>(randomSearch, randomSearch.getName(), problemList.get(i), run)); // OK
                //algorithms.add(new ExperimentAlgorithm<>(rnsgaii,rnsgaii.getName(), problemList.get(i), run)); // OK
                //algorithms.add(new ExperimentAlgorithm<>(smsemoa, smsemoa.getName(), problemList.get(i), run)); // OK
                algorithms.add(new ExperimentAlgorithm<>(spea2, spea2.getName(), problemList.get(i), run)); // OK

                // MULTI

                // algorithms.add(new SingleObjectiveExperimentAlgorithm<>(random_descent, "Random_Descent", problemList.get(i), run));
                // algorithms.add(new SingleObjectiveExperimentAlgorithm<>(parallel_tempering, "Parallel_Tempering", problemList.get(i), run));
                // //algorithms.add(new SingleObjectiveExperimentAlgorithm<>(tabu_search, "Tabu_Search", problemList.get(i), run));
                // algorithms.add(new SingleObjectiveExperimentAlgorithm<>(random_search, "Random_Search", problemList.get(i), run));
                // algorithms.add(new SingleObjectiveExperimentAlgorithm<>(localSearch, "Local_Search", problemList.get(i), run));
                // algorithms.add(new SingleObjectiveExperimentAlgorithm<>(genetic_steady, "Genetic_Steady", problemList.get(i), run));
                // algorithms.add(new SingleObjectiveExperimentAlgorithm<>(genetic_generational, "Genetic_Generational", problemList.get(i), run));
                // algorithms.add(new SingleObjectiveExperimentAlgorithm<>(elitist, "Elitist", problemList.get(i), run));
                // algorithms.add(new SingleObjectiveExperimentAlgorithm<>(non_elitist, "NON_ELITIST", problemList.get(i), run));
//
            }
        }
        return algorithms;
    }
}

