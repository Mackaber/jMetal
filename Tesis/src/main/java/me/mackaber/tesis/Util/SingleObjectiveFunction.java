package me.mackaber.tesis.Util;

import me.mackaber.tesis.ObjectiveFunctions.NormalizedWeightedFunction;
import me.mackaber.tesis.ObjectiveFunctions.WeightedFunction;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;

import java.io.*;
import java.util.List;

/**
 * This class implements the unary epsilon additive indicator as proposed in E.
 * Zitzler, E. Thiele, L. Laummanns, M., Fonseca, C., and Grunert da Fonseca. V
 * (2003): Performance Assessment of Multiobjective Optimizers: An Analysis and
 * Review. The code is the a Java version of the original metric implementation
 * by Eckart Zitzler. It can be used also as a command line program just by
 * typing $java org.uma.jmetal.qualityindicator.impl.Epsilon <solutionFrontFile>
 * <trueFrontFile> <getNumberOfObjectives>
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class SingleObjectiveFunction<S extends Solution<?>> extends GenericIndicator<S> {

    private Function function;
    private Double value;

    /**
     * Default constructor
     */
    public SingleObjectiveFunction() {
    }

    public SingleObjectiveFunction(WeightedFunction function) {
        this.function = function;
    }


    public SingleObjectiveFunction(NormalizedWeightedFunction function) {
        this.function = function;
    }

    /**
     * Constructor
     *
     * @param referenceParetoFrontFile
     * @throws FileNotFoundException
     */
    public SingleObjectiveFunction(String referenceParetoFrontFile) throws FileNotFoundException {
        super(referenceParetoFrontFile);
    }

    /**
     * new SingleObjectiveFunction<>(function)
     * Constructor
     *
     * @param referenceParetoFront
     */
    public SingleObjectiveFunction(Front referenceParetoFront) {
        super(referenceParetoFront);
    }

    @Override
    public boolean isTheLowerTheIndicatorValueTheBetter() {
        return true;
    }

    /**
     * Evaluate() method
     *
     * @param solutionList
     * @return
     */
    @Override
    public Double evaluate(List<S> solutionList) {
        if (solutionList == null) {
            throw new JMetalException("The pareto front approximation list is null");
        }

        return eval(new ArrayFront(solutionList), referenceParetoFront);
    }

    /**
     * Returns the value of the epsilon indicator.
     *
     * @param front          Solution front
     * @param referenceFront Optimal Pareto front
     * @return the value of the epsilon indicator
     * @throws JMetalException
     */
    // No idea what I was trying to do here... :P
    private double eval(Front front, Front referenceFront) throws JMetalException {
        double res = 0.0;
        double best = Double.POSITIVE_INFINITY;
        if (function instanceof NormalizedWeightedFunction) {
            NormalizedWeightedFunction fun = (NormalizedWeightedFunction) function;
            for (int i = 0; i < front.getNumberOfPoints(); i++) {
                res = fun.normalizedEval(front.getPoint(i));
                if (res < best)
                    best = res;
            }
        } else {
            WeightedFunction fun = (WeightedFunction) function;
            for (int i = 0; i < front.getNumberOfPoints(); i++) {
                res = fun.evalObjectives(front.getPoint(i));
                if (res < best)
                    best = res;
            }
        }

        return best;
    }

    @Override
    public String getName() {
        return "FUN";
    }

    @Override
    public String getDescription() {
        return "Single Objective Result";
    }
}
