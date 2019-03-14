package me.mackaber.tesis.Util;

import me.mackaber.tesis.SingleObjective.GroupingSolution;
import me.mackaber.tesis.SingleObjective.SingleObjectiveGrouping;
import org.apache.commons.math3.analysis.function.Sin;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import java.util.List;

/**
 * This class implements a swap mutation. The solution type of the solution
 * must be Permutation.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class GroupRandomMutation<T> implements MutationOperator<GroupingSolution<T>> {
    private double mutationProbability;
    private RandomGenerator<Double> mutationRandomGenerator;
    private BoundedRandomGenerator<Integer> positionRandomGenerator;
    private CombinationProblem combinationProblem;

    /**
     * Constructor
     */
    public GroupRandomMutation(double mutationProbability) {
        this(mutationProbability, () -> JMetalRandom.getInstance().nextDouble(), (a, b) -> JMetalRandom.getInstance().nextInt(a, b));
    }

    /**
     * Constructor
     */
    public GroupRandomMutation(double mutationProbability, CombinationProblem problem) {
        this(mutationProbability, () -> JMetalRandom.getInstance().nextDouble(), (a, b) -> JMetalRandom.getInstance().nextInt(a, b));
        this.combinationProblem = problem;
    }


    /**
     * Constructor
     */
    public GroupRandomMutation(double mutationProbability, RandomGenerator<Double> randomGenerator) {
        this(mutationProbability, randomGenerator, BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
    }

    /**
     * Constructor
     */
    public GroupRandomMutation(double mutationProbability, RandomGenerator<Double> mutationRandomGenerator, BoundedRandomGenerator<Integer> positionRandomGenerator) {
        if ((mutationProbability < 0) || (mutationProbability > 1)) {
            throw new JMetalException("Mutation probability value invalid: " + mutationProbability);
        }
        this.mutationProbability = mutationProbability;
        this.mutationRandomGenerator = mutationRandomGenerator;
        this.positionRandomGenerator = positionRandomGenerator;
    }

    /* Getters */
    public double getMutationProbability() {
        return mutationProbability;
    }

    /* Setters */
    public void setMutationProbability(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    /* Execute() method */
    @Override
    public GroupingSolution<T> execute(GroupingSolution<T> solution) {
        if (null == solution) {
            throw new JMetalException("Null parameter");
        }

        doMutation((GroupingSolution<List<User>>) solution);
        return solution;
    }

    /**
     * Performs the operation
     *
     * @param solution
     */
    public void doMutation(GroupingSolution<List<User>> solution) {
        GroupingSolution new_sol = combinationProblem.createSolution();
        solution.setVariableValue(0, (List<User>) new_sol.getVariableValue(0));
    }
}

