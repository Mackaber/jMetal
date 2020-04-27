package me.mackaber.tesis.SingleObjective.Algorithms;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.impl.OverallConstraintViolationComparator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;
import weka.core.stopwords.Null;

import java.util.Comparator;

/**
 * This class implements a basic local search operator based in the use of a
 * mutation operator.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DummyAlgorithm<S extends Solution<?>> implements LocalSearchOperator<S>, Algorithm<S> {

    public DummyAlgorithm(){}

    /**
     * Executes the local search.
     * @param  solution The solution to improve
     * @return An improved solution
     */
    @SuppressWarnings("unchecked")
    public S execute(S solution) {
        return (S) solution.copy();
    }

    /**
     * Returns the number of evaluations
     */
    public int getEvaluations() {
        return 0;
    }

    @Override public int getNumberOfImprovements() {
        return 0 ;
    }

    @Override public int getNumberOfNonComparableSolutions() {
        return 0 ;
    }

    @Override
    public void run() {

    }

    @Override
    public S getResult() {
        return null;
    }

    @Override
    public String getName() {
        return "Dummy Algorithm";
    }

    @Override
    public String getDescription() {
        return "Some Description";
    }
}
