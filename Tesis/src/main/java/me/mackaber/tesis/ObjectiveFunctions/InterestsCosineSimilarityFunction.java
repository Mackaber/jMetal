package me.mackaber.tesis.ObjectiveFunctions;

import me.mackaber.tesis.Util.Function;
import me.mackaber.tesis.Util.User;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import weka.classifiers.trees.j48.EntropyBasedSplitCrit;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class InterestsCosineSimilarityFunction extends Function {
    private static final java.util.function.Function<List<User>,Double> CACHED = Memoizer.memoize(InterestsCosineSimilarityFunction::uncached);

    private static Double cosineSimilarity(HashMap<String, Double> vector1, HashMap<String, Double> vector2) {
        // Merge Vectors before comparing...
        double sumSqrt1 = 0.0;
        double sumSqrt2 = 0.0;

        for (Map.Entry<String, Double> interest : vector1.entrySet()) {
            if (!vector2.containsKey(interest.getKey()))
                vector2.put(interest.getKey(), 0.0);
            sumSqrt1 += interest.getValue() * interest.getValue();
        }

        for (Map.Entry<String, Double> interest : vector2.entrySet()) {
            if (!vector1.containsKey(interest.getKey()))
                vector1.put(interest.getKey(), 0.0);
            sumSqrt2 += interest.getValue() * interest.getValue();
        }

        double sumProduct = 0.0;
        for (Map.Entry<String, Double> interest : vector1.entrySet()) {
            sumProduct += interest.getValue() * vector2.get(interest.getKey());
        }

        return (sumProduct / (Math.sqrt(sumSqrt1) * Math.sqrt(sumSqrt2)));
    }


    @Override
    public double eval(List<User> group) {
        return CACHED.apply(group);
    }

    public static double uncached(List<User> group) {
        List<Double> evals = new ArrayList<>();
        for (int i = 0; i < group.size() - 1; i++) {
            for (int j = i + 1; j < group.size(); j++) {
                HashMap<String, Double> vect1 = (HashMap<String, Double>) group.get(i).getInterestVector().clone();
                HashMap<String, Double> vect2 = (HashMap<String, Double>) group.get(j).getInterestVector().clone();

                evals.add(cosineSimilarity(vect1, vect2));
            }
        }
        Mean mean = new Mean();
        Double result = 1 - mean.evaluate(ArrayUtils.toPrimitive(evals.toArray(new Double[evals.size()])));

        // TODO: Check why there still groups with 1 person!

        if (result.isNaN())
            return 1.0;
        else
            return result < 0 ? 0.0 : result;
    }

    public HashMap<Integer,Map<Integer,Double>> getRankings(List<User> group) {
        HashMap<Integer,Map<Integer,Double>> rankings = new HashMap<>();
        for (int i = 0; i < group.size(); i++) {
            User user1 = group.get(i);
            HashMap<Integer, Double> values = new HashMap<>();

            //for (int j = i + 1; j < group.size(); j++) {
            for (int j = 0; j < group.size(); j++) {
                User user2 = group.get(j);

                HashMap<String, Double> vect1 = (HashMap<String, Double>) user1.getInterestVector().clone();
                HashMap<String, Double> vect2 = (HashMap<String, Double>) user2.getInterestVector().clone();

                Integer levelDifference = (5 - Math.abs(user1.getLevel() - user2.getLevel()))/5;
                Math.abs((user1.getLevel() - user2.getLevel()));
                Double cosineDistance = cosineSimilarity(vect1, vect2);
                values.put(user2.getId(), 0.5 * cosineDistance + 0.5 * levelDifference);
            }

            // Sort the values
            Comparator<Map.Entry<Integer, Double>> comparator = Map.Entry.comparingByValue();

            Map<Integer,Double> sortedValues = values.entrySet().stream()
                    .sorted(comparator.reversed())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2) -> e2, LinkedHashMap::new));

            rankings.put(user1.getId(),sortedValues);

        }
        return rankings;
    }

    @Override
    public String getName() {
        return "InterestsCosineSimilarity";
    }

    @Override
    public String getDescription() {
        return null;
    }
}
