package me.mackaber.tesis.SingleObjective;

import me.mackaber.tesis.Util.*;
import org.uma.jmetal.solution.impl.AbstractGenericSolution;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.impl.JavaRandomGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DefaultGroupSolution extends GroupSolution implements GroupingSolution<Integer> {

    public DefaultGroupSolution(GroupingProblem problem) {
        super(problem);
        setGroups(new Groups(problem.getNumberOfVariables()));
        for (int i = 0; i < problem.getNumberOfVariables(); i++) {
            setVariableValue(i, getGroups().getRandomGroup());
        }
    }

    public DefaultGroupSolution(DefaultGroupSolution solution) {
        super(solution);
    }


    @Override
    public String getVariableValueString(int index) {
        return getVariableValue(index).toString();
    }

    @Override
    public GroupSolution copy() {
        return new DefaultGroupSolution(this);
    }


    @Override
    public List<HashMap<String, String>> getSampleSolution(int n) {
        List<HashMap<String, String>> sampleGroups = new ArrayList<>();

        JavaRandomGenerator random = new JavaRandomGenerator();
        BoundedRandomGenerator<Integer> indexSelector = random::nextInt;
        for (int i = 0; i < n; i++) {
            //List<User> group = getVariableValue(indexSelector.getRandomValue(0, getNumberOfVariables() - 1));
//            for (User user : group) {
//                HashMap<String, String> sampleGroup = new HashMap<>();
//                sampleGroup.put("group", i + "");
//                sampleGroup.put("id", user.getId() + "");
//                sampleGroup.put("level", user.getLevel() + "");
//                sampleGroup.put("interests", user.getInterests() + "");
//                sampleGroup.put("part_prc", user.getPart_prc() + "");
//                sampleGroups.add(sampleGroup);
//            }
        }
        return sampleGroups;
    }

    @Override
    public Double evaluate(Function function) {
        // HACK!!!, not for standard use
        SingleObjectiveGrouping grouping = (SingleObjectiveGrouping) problem;
        grouping.setObjectiveFunction(function);
        grouping.evaluate(this);
        return getObjective(0);
    }
}
