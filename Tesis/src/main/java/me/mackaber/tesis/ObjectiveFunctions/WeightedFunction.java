package me.mackaber.tesis.ObjectiveFunctions;

import me.mackaber.tesis.MultiObjective.MultiObjectiveGrouping;
import me.mackaber.tesis.SingleObjective.GroupingSolution;
import me.mackaber.tesis.Util.Function;
import me.mackaber.tesis.Util.InterestsFunction;
import me.mackaber.tesis.Util.User;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WeightedFunction extends InterestsFunction {
    private ArrayList<Function> functions;
    private ArrayList<Double> weights;
    private InterestsFunction interestsFunction = null;

    public WeightedFunction()  {
        this.functions = new ArrayList<>();
        this.weights = new ArrayList<>();
    }

    public WeightedFunction WeightedFunction()  {
        this.functions = new ArrayList<>();
        return this;
    }

    public WeightedFunction addObjectiveFunction(double weight, Function function) {
        this.functions.add(function);
        this.weights.add(weight);
        return this;
    }

    public WeightedFunction setInterestsFunction(double weight, InterestsFunction function) {
        this.functions.add(function);
        this.weights.add(weight);
        this.interestsFunction = function;
        return this;
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    public WeightedFunction setFunctions(ArrayList<Function> functions) {
        this.functions = functions;
        return this;
    }

    public ArrayList<Double> getWeights() {
        return weights;
    }

    public WeightedFunction setWeights(ArrayList<Double> weights) {
        this.weights = weights;
        return this;
    }

    @Override
    public double eval(List<User> variableValue) {
        double result = 0.0;
        int i = 0;
        for(Function function: functions){
            result += weights.get(i) * function.eval(variableValue);
            i++;
        }

        return result;
    }

    @Override
    public HashMap<String, Double> getInterestPath(String interests) {
        return interestsFunction.getInterestPath(interests);
    }
}
