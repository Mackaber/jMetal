package me.mackaber.tesis.Util;

import me.mackaber.tesis.SingleObjective.GroupSolution;
import org.uma.jmetal.operator.impl.crossover.NPointCrossover;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.List;

public class CombinationNPointCrossover extends NPointCrossover<GroupSolution> {
    public CombinationNPointCrossover(double probability, int crossovers) {
        super(probability, crossovers);
    }

    public CombinationNPointCrossover(int crossovers) {
        super(crossovers);
    }

    @Override
    public List execute(List s) {
        List<GroupSolution> result = super.execute(s);
        List<GroupSolution> solutions = new ArrayList<>();
        for (GroupSolution groupSolution : result) {
            solutions.add(groupSolution.repair());
        }

        return solutions;
    }
}
