package me.mackaber.tesis.Util;

import me.mackaber.tesis.SingleObjective.GroupSolution;
import org.apache.commons.lang3.ArrayUtils;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

public class CombinationNPointCrossoverRelaxed implements CrossoverOperator<GroupSolution> {
    private final JMetalRandom randomNumberGenerator = JMetalRandom.getInstance();
    private final double probability;
    private final int crossovers;

    public CombinationNPointCrossoverRelaxed(double probability, int crossovers) {
        if (probability < 0.0) throw new JMetalException("Probability can't be negative");
        if (crossovers < 1) throw new JMetalException("Number of crossovers is less than one");
        this.probability = probability;
        this.crossovers = crossovers;
    }

    public CombinationNPointCrossoverRelaxed(int crossovers) {
        this.crossovers = crossovers;
        this.probability = 1.0;
    }

    @Override
    public List<GroupSolution> execute(List<GroupSolution> s) {
        if (getNumberOfRequiredParents() != s.size()) {
            throw new JMetalException("Point Crossover requires + " + getNumberOfRequiredParents() + " parents, but got " + s.size());
        }


        if (randomNumberGenerator.nextDouble() < probability && GroupSolution.checkSolution(s)) {
            List<GroupSolution> result = doCrossover(s);
            if (GroupSolution.checkSolution(result))
                return result;
            else
                return s;
        } else {
            return s;
        }
    }

    private List<GroupSolution> doCrossover(List<GroupSolution> s) {
        GroupSolution mom = s.get(0);
        GroupSolution dad = s.get(1);

        if (mom.getNumberOfVariables() != dad.getNumberOfVariables()) {
            throw new JMetalException("The 2 parents doesn't have the same number of variables");
        }
        if (mom.getNumberOfVariables() > crossovers) {
            throw new JMetalException("The number of crossovers is higher than the number of variables");
        }

        int[] crossoverPoints = new int[crossovers];
        for (int i = 0; i < crossoverPoints.length; i++) {
            crossoverPoints[i] = randomNumberGenerator.nextInt(0, mom.getNumberOfVariables() - 1);
        }
        GroupSolution girl = (GroupSolution) mom.copy();
        GroupSolution boy = (GroupSolution) dad.copy();
        boolean swap = false;

        for (int i = 0; i < mom.getNumberOfVariables(); i++) {
            if (swap) {
                boy.setAttributeWithoutChange(i, mom.getVariableValue(i));
                girl.setAttributeWithoutChange(i, dad.getVariableValue(i));
            }

            if (ArrayUtils.contains(crossoverPoints, i)) {
                swap = !swap;
            }
        }
        List<GroupSolution> result = new ArrayList<>();
        result.add(girl);
        result.add(boy);
        return result;
    }

    @Override
    public int getNumberOfRequiredParents() {
        return 2;
    }

    @Override
    public int getNumberOfGeneratedChildren() {
        return 2;
    }
}
