package me.mackaber.tesis.Util;

import me.mackaber.tesis.SingleObjective.GroupSolution;
import me.mackaber.tesis.SingleObjective.GroupingProblem;
import me.mackaber.tesis.SingleObjective.GroupingSolution;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * This class implements a swap mutation. The solution type of the solution
 * must be Permutation.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class GroupSwapMutation implements MutationOperator<GroupSolution> {
    private double mutationProbability;
    private RandomGenerator<Double> mutationRandomGenerator;
    private BoundedRandomGenerator<Integer> positionRandomGenerator;
    private CombinationProblem combinationProblem;

    /**
     * Constructor
     */
    public GroupSwapMutation(double mutationProbability) {
        this(mutationProbability, () -> JMetalRandom.getInstance().nextDouble(), (a, b) -> JMetalRandom.getInstance().nextInt(a, b));
    }

    /**
     * Constructor
     */
    public GroupSwapMutation(double mutationProbability, CombinationProblem problem) {
        this(mutationProbability, () -> JMetalRandom.getInstance().nextDouble(), (a, b) -> JMetalRandom.getInstance().nextInt(a, b));
        this.combinationProblem = problem;
    }


    /**
     * Constructor
     */
    public GroupSwapMutation(double mutationProbability, RandomGenerator<Double> randomGenerator) {
        this(mutationProbability, randomGenerator, BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
    }

    /**
     * Constructor
     */
    public GroupSwapMutation(double mutationProbability, RandomGenerator<Double> mutationRandomGenerator, BoundedRandomGenerator<Integer> positionRandomGenerator) {
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
    public GroupSolution execute(GroupSolution solution) {
        if (null == solution) {
            throw new JMetalException("Null parameter");
        }

        doMutation(solution);
        return solution;
    }

    /**
     * Performs the operation
     *
     * @param solution
     */
    public void doMutation(GroupSolution solution) {
        int combinationLength;
        combinationLength = solution.getNumberOfVariables();

        if ((combinationLength != 0) && (combinationLength != 1)) {
            if (mutationRandomGenerator.getRandomValue() < mutationProbability) {
                int pos = positionRandomGenerator.getRandomValue(0, combinationLength - 1);
                solution.setVariableValue(pos,solution.getGroups().getRandomGroup());
            }
        }
    }
}

