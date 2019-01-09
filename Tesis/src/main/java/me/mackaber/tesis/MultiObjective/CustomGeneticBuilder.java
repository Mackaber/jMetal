package me.mackaber.tesis.MultiObjective;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GenerationalGeneticAlgorithm;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomGeneticBuilder<S extends Solution<?>> extends GeneticAlgorithmBuilder {

    private ArrayList<Double> improvements = new ArrayList<>();

    public CustomGeneticBuilder(Problem problem, CrossoverOperator crossoverOperator, MutationOperator mutationOperator) {
        super(problem, crossoverOperator, mutationOperator);
    }

    @Override
    public GenerationalGeneticAlgorithm<S> build() {
        CustomGenerationanGeneticAlgorithm algorithm = null;
        algorithm = new CustomGenerationanGeneticAlgorithm(super.getProblem(), super.getMaxEvaluations(), super.getPopulationSize(),
                super.getCrossoverOperator(), super.getMutationOperator(), super.getSelectionOperator(), super.getEvaluator());

        return algorithm;
    }

    public class CustomGenerationanGeneticAlgorithm extends GenerationalGeneticAlgorithm {

        /**
         * Constructor
         *
         * @param problem
         * @param maxEvaluations
         * @param populationSize
         * @param crossoverOperator
         * @param mutationOperator
         * @param selectionOperator
         * @param evaluator
         */
        public CustomGenerationanGeneticAlgorithm(Problem problem, int maxEvaluations, int populationSize, CrossoverOperator crossoverOperator, MutationOperator mutationOperator, SelectionOperator selectionOperator, SolutionListEvaluator evaluator) {
            super(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator);
        }

        @Override
        public void initProgress() {
            super.initProgress();
        }

        @Override
        public void run() {
            super.run();
        }

        @Override
        public void updateProgress() {

            super.updateProgress();

            List<Double> results = new ArrayList<>();

            for(Object rawSolution:getPopulation()) {
                Solution solution = (Solution) rawSolution;
                results.add( solution.getObjective(0));
            }

            improvements.add(Collections.min(results));
        }

        public ArrayList<Double> getImprovements() {
            return improvements;
        }

    }
}
