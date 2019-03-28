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
    public double eval(List<Integer> group) {
        List<Double> evals = new ArrayList<>();
        for (int i = 0; i < group.size() - 1; i++) {
            for (int j = i + 1; j < group.size(); j++) {
                HashMap<String, Double> vect1 = (HashMap<String, Double>) getProblem().getUsers().get(i).getInterestVector().clone();
                HashMap<String, Double> vect2 = (HashMap<String, Double>) getProblem().getUsers().get(j).getInterestVector().clone();

                evals.add(cosineSimilarity(vect1, vect2));
            }
        }
        Mean mean = new Mean();
        double result = 1 - mean.evaluate(ArrayUtils.toPrimitive(evals.toArray(new Double[evals.size()])));
        return result < 0 | result == Float.NaN ? 0.0 : result;
    }
}
