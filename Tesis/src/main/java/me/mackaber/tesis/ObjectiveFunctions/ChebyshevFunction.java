package me.mackaber.tesis.ObjectiveFunctions;

import me.mackaber.tesis.Util.Function;
import me.mackaber.tesis.Util.User;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.uma.jmetal.util.point.Point;

import java.util.ArrayList;
import java.util.List;

public class ChebyshevFunction extends Function {
    private ArrayList<Function> functions;
    private ArrayList<Double> weights;
    private double bias = 0.0;
    private double utopicalPoint;

    public ChebyshevFunction() {
        this.functions = new ArrayList<>();
        this.weights = new ArrayList<>();
    }

    public ChebyshevFunction WeightedFunction() {
        this.functions = new ArrayList<>();
        return this;
    }

    public ChebyshevFunction addObjectiveFunction(double weight, Function function, double utopicalPoint) {
        this.functions.add(function);
        this.weights.add(weight);
        this.utopicalPoint = utopicalPoint;
        return this;
    }

    public ChebyshevFunction addBias(double bias) {
        this.bias = bias;
        return this;
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    public ChebyshevFunction setFunctions(ArrayList<Function> functions) {
        this.functions = functions;
        return this;
    }

    public ArrayList<Double> getWeights() {
        return weights;
    }

    public ChebyshevFunction setWeights(ArrayList<Double> weights) {
        this.weights = weights;
        return this;
    }


    @Override
    public double eval(List<User> group) {
        //evalObjectives(group)

        Max max = new Max();
        //return max.evaluate(result);
        return 0.0;
    }

    // This function is to evaluate the objectives if the objectives aren't normalized
    public double evalObjectives(Point point) {
        double result = 0.0;
        for (int i = 0; i < getFunctions().size(); i++) {
            result += getWeights().get(i) * point.getDimensionValue(i);
            i++;
        }

        return result;
    }

    @Override
    public String getName() {
        return "WeightedFunction";
    }

    @Override
    public String getDescription() {
        return null;
    }
}
