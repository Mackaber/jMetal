package me.mackaber.tesis.Util;

import me.mackaber.tesis.SingleObjective.DefaultGroupSolution;
import me.mackaber.tesis.SingleObjective.GroupSolution;
import me.mackaber.tesis.SingleObjective.GroupingProblem;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;
import org.uma.jmetal.util.pseudorandom.impl.JavaRandomGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Groups {
    private List<Integer> availableGroups;
    private LinkedList<List<Integer>> groups;
    private JavaRandomGenerator random = new JavaRandomGenerator();
    private BoundedRandomGenerator<Integer> indexSelector = random::nextInt;

    public Groups(int size) {
        availableGroups = new ArrayList<>();
        groups = new LinkedList<>();
        for (int i = 0; i < Math.ceil(size / 3); i++) {
            groups.add(i, new ArrayList<>());
            availableGroups.add(i);
        }
    }

    public Groups() {
        availableGroups = new ArrayList<>();
        groups = new LinkedList<>();
    }

    public Integer getRandomGroup() {
        RandomGenerator<Integer> generator = RandomGenerator.forCollection(indexSelector, availableGroups);
        if (availableGroups.size() > 0)
            return generator.getRandomValue();
        else {
            groups.add(new LinkedList<>());
            // if There are no available groups, Generate a new group
            return groups.size() - 1;
        }
    }

    public void changeUserGroup(Integer userId, Integer originId, Integer destinyId) {

        List<Integer> destiny = groups.get(destinyId);
        int currentCount = destiny.size();

        destiny.add(userId);

        if (originId != null) {
            List<Integer> origin = groups.get(originId);
            origin.remove(userId);
            if (!availableGroups.contains(originId))
                availableGroups.add(originId);
        }

        if (currentCount > 5)
            availableGroups.remove(destinyId);
    }

    public LinkedList<List<Integer>> getInternalGroups() {
        return groups;
    }

    public Groups setInternalGroupsLinkedList(LinkedList<List<Integer>> groups) {
        this.groups = groups;
        return this;
    }

    public void addAvailableGroup(Integer groupId) {
        availableGroups.add(groupId);
    }

    public void addMergedGroups(List<Integer> pendingGroup, List<Integer> group) {
        List<Integer> merged = new ArrayList<>(pendingGroup);
        merged.addAll(group);
        groups.add(merged);
    }

    public void addSplittedGroup(List<Integer> group) {
        int midIndex = (group.size() - 1) / 2;

        List<List<Integer>> lists = new ArrayList<>(
                group.stream().collect(Collectors.groupingBy(s -> group.indexOf(s) < midIndex)).values());
        groups.addAll(lists);
    }

    public List<Integer> getAvailableGroups() {
        return availableGroups;
    }
}