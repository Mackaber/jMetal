package me.mackaber.tesis.Experiments;

import me.mackaber.tesis.ObjectiveFunctions.*;
import me.mackaber.tesis.SingleObjective.Algorithms.*;
import me.mackaber.tesis.SingleObjective.EpsilonGroupingProblem;
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
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
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

public class ConstraintsTest {

    private static final int INDEPENDENT_RUNS = 30;

    public static void main(String[] args) throws IOException {
        JMetalRandom.getInstance().setSeed(120L);

        List<EpsilonGroupingProblem> problems = new ArrayList<>();

        // Problem Definition

        problems.add(new EpsilonGroupingProblem("Tesis/src/main/resources/synthetic_20.csv"));
        List<ExperimentProblem<GroupSolution>> problemList = new ArrayList<>();


        for (EpsilonGroupingProblem problem : problems) {
            problem .addConstraint(new ParticipationStyleFunction(), 1.0)
                    .addConstraint(new LevelFunction(), 1.0)
                    .addConstraint(new InterestsCosineSimilarityFunction(), 1.0)
                    .addObjectiveFunction(new InterestsCosineSimilarityFunction())
                    .setVector(new InterestVector("Tesis/src/main/resources/custom_interests.json"))
                    .setCentralTendencyMeasure(new Mean())
                    .setGroupSizeRange(3, 6)
                    .build();

            problemList.add(new ExperimentProblem<>(problem)); // dataset of 20
        }

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
                                new InvertedGenerationalDistancePlus<>()
                        ))
                        .build();

        new ExecuteAlgorithms<>(experiment).run();
        new ComputeQualityIndicatorsWoNorm<>(experiment).run();
        new GenerateLatexTablesWithStatistics(experiment).run();
        //new GenerateWilcoxonTestTablesWithR<>(experiment).run();
        new GenerateFriedmanTestTables<>(experiment).run();
        //new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(3).run();
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

                GroupingProblem problem = (GroupingProblem) problemList.get(i).getProblem();

                // Parameters and stuff
                CombinationNPointCrossover crossover = new CombinationNPointCrossover(0.9, problemList.get(i).getProblem().getNumberOfVariables());

                double mutationProbability = 1.0 / problemList.get(i).getProblem().getNumberOfVariables();
                MutationOperator<GroupSolution> mutation = new GroupSwapMutation(mutationProbability, (CombinationProblem) problemList.get(i).getProblem());
                //MutationOperator<GroupSolution> mutation = new GroupRandomMutation<>(mutationProbability);

                // Genetic Algorithm

                Algorithm<GroupSolution> genetic_generational = new GeneticAlgorithmBuilder<>(
                        problem,
                        crossover,
                        mutation)
                        .setSelectionOperator(selection)
                        .setPopulationSize(2000)
                        .setMaxEvaluations(2000)
                        .setSolutionListEvaluator(new MultithreadedSolutionListEvaluator(3, problem))
                        .build();

                algorithms.add(new SingleObjectiveExperimentAlgorithm<>(genetic_generational, genetic_generational.getName(), problemList.get(i), run));

            }
        }
        return algorithms;
    }
}

