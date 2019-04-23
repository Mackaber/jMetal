package me.mackaber.tesis.SingleObjective;

import me.mackaber.tesis.Util.CombinationProblem;
import me.mackaber.tesis.Util.Function;
import me.mackaber.tesis.Util.Groups;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.AbstractGenericSolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class GroupSolution extends AbstractGenericSolution<Integer, CombinationProblem> implements GroupingSolution<Integer> {
    private Groups groups;

    public GroupSolution(GroupingProblem problem) {
        super(problem);
    }

    /**
     * Copy Constructor
     */
    public GroupSolution(GroupSolution solution) {
        super(solution.problem);
        setGroups(new Groups(getNumberOfVariables()));

        for (int i = 0; i < solution.groups.getInternalGroups().size(); i++) {
            List<Integer> group = new ArrayList<>();
            for (int j = 0; j < solution.groups.getInternalGroups().get(i).size(); j++) {
                group.add(solution.groups.getInternalGroups().get(i).get(j));
            }
        }
        for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
            setObjective(i, solution.getObjective(i));
        }

        for (int i = 0; i < problem.getNumberOfVariables(); i++) {
            setAttributeWithoutChange(i, solution.getVariableValue(i));
        }

        attributes = new HashMap<>(solution.attributes);
    }

    public static boolean checkSolution(List<GroupSolution> s) {
        for (GroupSolution solution : s) {
            for (List<Integer> group : solution.getGroups().getInternalGroups()) {
                if (group.size() < 3 || group.size() > 6)
                    return false;
            }
        }
        return true;
    }

    public Groups getGroups() {
        return groups;
    }

    public GroupSolution setGroups(Groups groups) {
        this.groups = groups;
        return this;
    }

    public GroupSolution repair() {
        GroupSolution newSolution = new GroupSolution((GroupingProblem) problem);
        newSolution.setGroups(new Groups());

        // group to be repaired
        List<Integer> pendingGroup = null;
        LinkedList<List<Integer>> orgnGroups = groups.getInternalGroups();

        for (List<Integer> group : orgnGroups) {
            int size = group.size();
            if (size > 0) {
                if (pendingGroup != null && size + pendingGroup.size() >= 3 && size + pendingGroup.size() <= 6) {
                    newSolution.groups.addMergedGroups(pendingGroup, group);
                    pendingGroup = null;
                } else if (size > 6)
                    newSolution.groups.addSplittedGroup(group);
                else if (size < 3)
                    pendingGroup = group;
                else
                    newSolution.groups.getInternalGroups().add(group);
            }
        }

        // Part 2: Add the users to the corresponding group
        int groupId = 0;
        int j = 0;
        for (List<Integer> group : newSolution.groups.getInternalGroups()) {
            if (group.size() < 6)
                newSolution.groups.addAvailableGroup(groupId);
            for (Integer user : group) {
                newSolution.setAttributeWithoutChange(user, groupId);
                j++;
            }
            groupId++;
        }

        if (j == problem.getNumberOfVariables())
            return newSolution;
        else {
            for (int i = 0; i < newSolution.getNumberOfVariables(); i++) {
                if (newSolution.getVariableValue(i) == null)
                    newSolution.setVariableValue(i, newSolution.groups.getRandomGroup());
            }
            return newSolution;
        }
    }


    @Override
    public String getVariableValueString(int index) {
        return getVariableValue(index).toString();
    }

    @Override
    public Solution<Integer> copy() {
        return new GroupSolution(this);
    }

    @Override
    public List<HashMap<String, String>> getSampleSolution(int n) {
        return null;
    }

    @Override
    public Double evaluate(Function function) {
        // HACK!!!, not for standard use
        SingleObjectiveGrouping grouping = (SingleObjectiveGrouping) problem;
        grouping.setObjectiveFunction(function);
        grouping.evaluate(this);
        return getObjective(0);
    }

    @Override
    public void setVariableValue(int index, Integer value) {
        Integer origin = super.getVariableValue(index);
        groups.changeUserGroup(index, origin, value);
        super.setVariableValue(index, value);
    }

    public void setAttributeWithoutChange(Integer user, int i) {
        super.setVariableValue(user, i);
    }
}
