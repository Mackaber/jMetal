package me.mackaber.tesis.Util;

import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;
import org.uma.jmetal.util.pseudorandom.impl.JavaRandomGenerator;

import java.util.ArrayList;
import java.util.List;

public class RandomFunctions {

    public static List<User> generateRandomGroup(List<User> pool, CombinationProblem problem) {
        List<User> generatedUserGroup = new ArrayList<>();
        JavaRandomGenerator random = new JavaRandomGenerator();
        int size = random.nextInt(problem.getMinSize(), problem.getMaxSize());
        if (pool.size() <= problem.getMaxSize()) {
            generatedUserGroup.addAll(pool);
            pool.clear();
        } else
            for (int i = 0; i < size; i++) {
                int id = random.nextInt(0, pool.size() - 1);
                User user = pool.get(id);
                generatedUserGroup.add(user);
                pool.remove(user);
            }
        return generatedUserGroup;
    }

    public static List<User> generateRandomGroup_(List<User> pool, CombinationProblem problem) {
        JavaRandomGenerator random = new JavaRandomGenerator();
        int size = random.nextInt(problem.getMinSize(), problem.getMaxSize());
        BoundedRandomGenerator<Integer> indexSelector = random::nextInt;
        RandomGenerator<User> generator = RandomGenerator.forCollection(indexSelector, pool);

        List<User> generatedUserGroup = new ArrayList<>();

        if (pool.size() <= problem.getMaxSize()) {
            generatedUserGroup = pool;
            pool.removeAll(pool); // So, it doesn't fill the remaining variables (altough, maybe it could be an interesting hack
        } else { // Only if the pool still has users, otherwise it will return an empty list
            for (int i = 0; i < size; i++) {
                User userToAdd = generator.getRandomValue();
                generatedUserGroup.add(userToAdd);
                pool.remove(userToAdd);
            }
        }
        return generatedUserGroup;
    }
}
