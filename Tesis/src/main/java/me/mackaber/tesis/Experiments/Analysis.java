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
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.GenerateBoxplotsWithR;
import org.uma.jmetal.util.experiment.component.GenerateFriedmanTestTables;
import org.uma.jmetal.util.experiment.component.GenerateLatexTablesWithStatistics;
import org.uma.jmetal.util.experiment.component.GenerateWilcoxonTestTablesWithR;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Analysis {

    private static final int INDEPENDENT_RUNS = 30;

    public static void main(String[] args) throws IOException {
        JMetalRandom.getInstance().setSeed(120L);

        List<GroupingProblem> problems = new ArrayList<>();

        // Problem Definition

        problems.add(new GroupingProblem("Tesis/src/main/resources/synthetic_20.csv"));
        problems.add(new GroupingProblem("Tesis/src/main/resources/synthetic_200.csv"));
        problems.add(new GroupingProblem("Tesis/src/main/resources/synthetic_2000.csv"));

        List<ExperimentProblem<GroupSolution>> problemList = new ArrayList<>();


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
                                //new SingleObjectiveFunction<>(function)
                        ))
                        .build();

        //new ExecuteAlgorithms<>(experiment).run();
        new ComputeQualityIndicatorsWoNorm<>(experiment).run();
        new GenerateLatexTablesWithStatistics(experiment).run();
        // new GenerateWilcoxonTestTablesWithR<>(experiment).run();
        new GenerateFriedmanTestTables<>(experiment).run();
        //new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(3).run();
    }


    static List<ExperimentAlgorithm<GroupSolution, List<GroupSolution>>> configureAlgorithmList(
            List<ExperimentProblem<GroupSolution>> problemList) {

        List<ExperimentAlgorithm<GroupSolution, List<GroupSolution>>> algorithms = new ArrayList<>();

        for (int run = 0; run < INDEPENDENT_RUNS; run++) {
            for (int i = 0; i < problemList.size(); i++) {

                algorithms.add(new SingleObjectiveExperimentAlgorithm<>(new DummyAlgorithm(), "ESPEA", problemList.get(i), run));

            }
        }
        return algorithms;
    }
}

