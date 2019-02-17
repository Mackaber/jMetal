package me.mackaber.tesis.Util;

import me.mackaber.tesis.SingleObjective.GroupSolution;
import me.mackaber.tesis.SingleObjective.GroupingSolution;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.PermutationSolution;
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
public class GroupSwapMutation<T> implements MutationOperator<GroupingSolution<T>> {
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
        int combinationLength;
        combinationLength = solution.getNumberOfVariables();

        if ((combinationLength != 0) && (combinationLength != 1)) { // No se si esto vaya o no XP
            if (mutationRandomGenerator.getRandomValue() < mutationProbability) {
                GroupingSolution<List<User>> tmp_sol = (GroupingSolution<List<User>>) solution.copy();

                int g1 = positionRandomGenerator.getRandomValue(0, combinationLength - 1);
                int g2 = positionRandomGenerator.getRandomValue(0, combinationLength - 1);
                int min_size = combinationProblem.getMinSize();
                int max_size = combinationProblem.getMaxSize();

                if (g1 != g2) {
                    List<User> group1 = tmp_sol.getVariableValue(g1);
                    List<User> group2 = tmp_sol.getVariableValue(g2);
                    if((group1.size() >  min_size + 1) && (group2.size() > min_size) && (group2.size() < max_size + 1)) {
                        User user = group1.get(0); // Maybe Random
                        group1.remove(user);
                        group2.add(user);

                        solution.setVariableValue(g1, group1);
                        solution.setVariableValue(g2, group2);
                    }
                }
            }
        }
    }
}

