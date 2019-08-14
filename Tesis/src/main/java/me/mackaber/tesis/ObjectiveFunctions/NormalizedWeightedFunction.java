package me.mackaber.tesis.ObjectiveFunctions;

import me.mackaber.tesis.Util.Function;
import me.mackaber.tesis.Util.InterestVector;
import me.mackaber.tesis.Util.User;
import org.uma.jmetal.util.point.Point;

import java.util.ArrayList;
import java.util.List;

public class NormalizedWeightedFunction extends WeightedFunction {
    private List<Double> min_values = new ArrayList<>();
    private List<Double> max_values = new ArrayList<>();

    public NormalizedWeightedFunction addObjectiveFunction(double weight, Function function, double min_value, double max_value) {
        super.addObjectiveFunction(weight, function);
        min_values.add(min_value);
        max_values.add(max_value);
        return this;
    }

    @Override
    public double eval(List<Integer> groups) {
        double result = 0.0;
        int i = 0;
        for (Function function : getFunctions()) {
            result += getWeights().get(i) * ((max_values.get(i) - function.eval(groups)) / (max_values.get(i) - min_values.get(i)));
            i++;
        }

        return result;
    }

    // This function is to evaluate the objectives if they use a normalized function
    public double normalizedEval(Point point) {
        double result = 0.0;
        for (int i = 0; i < getFunctions().size(); i++) {
            // This step is for normalizing the result (in the case of the Normalized Weighted Function)
            result += getWeights().get(i) * ((max_values.get(i) - point.getDimensionValue(i) / (max_values.get(i) - min_values.get(i))));
            i++;
        }

        return result;
    }
}
