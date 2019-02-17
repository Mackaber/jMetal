package me.mackaber.tesis.ObjectiveFunctions;

import me.mackaber.tesis.Util.Function;
import me.mackaber.tesis.Util.InterestsFunction;
import me.mackaber.tesis.Util.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NormalizedWeightedFunction extends WeightedFunction {
    private List<Double> min_values = new ArrayList<>();
    private List<Double> max_values = new ArrayList<>();

    public NormalizedWeightedFunction addObjectiveFunction(double weight, Function function, double min_value, double max_value) {
        super.addObjectiveFunction(weight,function);
        min_values.add(min_value);
        max_values.add(max_value);
        return this;
    }

    public NormalizedWeightedFunction setInterestsFunction(double weight, InterestsFunction function, double min_value, double max_value) {
        super.setInterestsFunction(weight,function);
        min_values.add(min_value);
        max_values.add(max_value);
        return this;
    }

    @Override
    public double eval(List<User> variableValue) {
        double result = 0.0;
        int i = 0;
        for(Function function: getFunctions()){
            result += getWeights().get(i) * ((max_values.get(i) - function.eval(variableValue))/(max_values.get(i) - min_values.get(i)));
            i++;
        }

        return result;
    }
}
