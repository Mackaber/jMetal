package me.mackaber.tesis.MultiObjective;

import org.apache.commons.lang3.ArrayUtils;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CustomNSGAIIBuilder<S extends Solution<?>> extends NSGAIIBuilder {
    /**
     * NSGAIIBuilder constructor
     *
     * @param problem
     * @param crossoverOperator
     * @param mutationOperator
     */

    private ArrayList<Double> improvements = new ArrayList<>();

    public CustomNSGAIIBuilder(Problem problem, CrossoverOperator crossoverOperator, MutationOperator mutationOperator) {
        super(problem, crossoverOperator, mutationOperator);
    }

    @Override
    public NSGAII<S> build() {
        CustomNSGAII algorithm = null;
        algorithm = new CustomNSGAII(super.getProblem(), super.getMaxIterations(), super.getPopulationSize(),
                super.getCrossoverOperator(), super.getMutationOperator(), super.getSelectionOperator(), super.getSolutionListEvaluator());

        return algorithm;
    }

    class CustomNSGAII extends NSGAII {
        public CustomNSGAII(Problem problem, int maxEvaluations, int populationSize, CrossoverOperator crossoverOperator, MutationOperator mutationOperator,
                            SelectionOperator selectionOperator, SolutionListEvaluator evaluator) {
            super(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator);
        }

        @Override
        protected void initProgress() {
            super.initProgress();
        }

        @Override
        public void run() {
            super.run();
        }

        @Override
        protected void updateProgress() {

            super.updateProgress();

            List elitePopulation = getNonDominatedSolutions(getPopulation());

            List<Double> results = new ArrayList<>();

            for(Object rawSolution:elitePopulation) {
                Solution solution = (Solution) rawSolution;
                double sum = 0;
                for(int i = 0; i<solution.getNumberOfObjectives(); i++)
                    sum += solution.getObjective(i);
                results.add(sum);
            }

            improvements.add(Collections.min(results));

        }

        public ArrayList<Double> getImprovements() {
            return improvements;
        }

    }
}
