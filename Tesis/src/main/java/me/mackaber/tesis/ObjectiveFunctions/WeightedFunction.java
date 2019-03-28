package me.mackaber.tesis.ObjectiveFunctions;

import me.mackaber.tesis.SingleObjective.GroupingProblem;
import me.mackaber.tesis.Util.Function;
import me.mackaber.tesis.Util.InterestVector;
import me.mackaber.tesis.Util.User;
import org.apache.commons.math3.exception.NotANumberException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WeightedFunction extends Function {
    private ArrayList<Function> functions;
    private ArrayList<Double> weights;

    public WeightedFunction() {
        this.functions = new ArrayList<>();
        this.weights = new ArrayList<>();
    }

    public WeightedFunction WeightedFunction() {
        this.functions = new ArrayList<>();
        return this;
    }

    public WeightedFunction addObjectiveFunction(double weight, Function function) {
        function.setProblem(getProblem());
        this.functions.add(function);
        this.weights.add(weight);
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
    public void setProblem(GroupingProblem problem) {
        super.setProblem(problem);
        for(Function function: functions){
            function.setProblem(problem);
        }
    }

    @Override
    public double eval(List<Integer> group) {
        double result = 0.0;
        int i = 0;
        for (Function function : functions) {
            result += weights.get(i) * function.eval(group);
            i++;
        }

        return result;
    }
}
