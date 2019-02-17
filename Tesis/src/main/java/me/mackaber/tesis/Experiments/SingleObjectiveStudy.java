package me.mackaber.tesis.Experiments;

import me.mackaber.tesis.MultiObjective.MultiObjectiveGrouping;
import me.mackaber.tesis.ObjectiveFunctions.*;
import me.mackaber.tesis.SingleObjective.GroupingSolution;
import me.mackaber.tesis.SingleObjective.SingleObjectiveGrouping;
import me.mackaber.tesis.Util.CombinationProblem;
import me.mackaber.tesis.Util.GroupSwapMutation;
import me.mackaber.tesis.Util.SingleObjectiveExperimentAlgorithm;
import me.mackaber.tesis.Util.User;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.NPointCrossover;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.ExecuteAlgorithms;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
                .addObjectiveFunction(1.0, new GroupSizeFunction(),0.5,1.5)
                .addObjectiveFunction(1.0, new ParticipationStyleFunction(), 0.001666, 1.0)
                .addObjectiveFunction(1.0, new LevelFunction(), 0.0,2.82843)
                .setInterestsFunction(1.0, new InterestsCosineSimilarityFunction("Tesis/src/main/resources/custom_interests.json"),0.0,1.0);


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
                        .setReferenceFrontDirectory("/paretoFronts")
                        .setIndependentRuns(INDEPENDENT_RUNS)
                        .setNumberOfCores(5)
                        .build();

         new ExecuteAlgorithms<>(experiment).run();
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

                Algorithm<GroupingSolution<List<User>>> genetic_algorithm = new GeneticAlgorithmBuilder<>(
                        problemList.get(i).getProblem(),
                        crossover,
                        mutation)
                        .setSelectionOperator(selection)
                        .setPopulationSize(popSize)
                        .setMaxEvaluations(genNum * popSize)
                        .setSolutionListEvaluator(new MultithreadedSolutionListEvaluator(10, problemList.get(i).getProblem()))
                        .build();

                algorithms.add(new SingleObjectiveExperimentAlgorithm<>(genetic_algorithm, "Genetic_Algorithm_" + tag, problemList.get(i), run));
            }
        }
        return algorithms;
    }
}

