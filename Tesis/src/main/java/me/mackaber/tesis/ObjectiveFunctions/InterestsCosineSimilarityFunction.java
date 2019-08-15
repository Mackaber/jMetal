package me.mackaber.tesis.ObjectiveFunctions;

import me.mackaber.tesis.Util.Function;
import me.mackaber.tesis.Util.User;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                HashMap<String, Double> vect2 = (HashMap<String, Double>) group.get(i).getInterestVector().clone();

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

    @Override
    public String getName() {
        return "InterestsCosineSimilarity";
    }

    @Override
    public String getDescription() {
        return null;
    }
}
