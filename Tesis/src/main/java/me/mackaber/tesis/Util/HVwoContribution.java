package me.mackaber.tesis.Util;

import me.mackaber.tesis.ObjectiveFunctions.NormalizedWeightedFunction;
import me.mackaber.tesis.ObjectiveFunctions.WeightedFunction;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;

import java.io.FileNotFoundException;
import java.util.List;

import static org.uma.jmetal.util.front.util.FrontUtils.getInvertedFront;

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
public class HVwoContribution<S extends Solution<?>> extends GenericIndicator<S> {

    private Double value;

    /**
     * Default constructor
     */
    public HVwoContribution() {
    }

    /**
     * Constructor
     *
     * @param referenceParetoFrontFile
     * @throws FileNotFoundException
     */
    public HVwoContribution(String referenceParetoFrontFile) throws FileNotFoundException {
        super(referenceParetoFrontFile);
    }

    /**
     * new SingleObjectiveFunction<>(function)
     * Constructor
     *
     * @param referenceParetoFront
     */
    public HVwoContribution(Front referenceParetoFront) {
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
    private double eval(Front front, Front referenceFront) throws JMetalException {
        PISAHypervolume hypervolume = new PISAHypervolume();

        // Default Front 0,0,0,0
        Front origin = new ArrayFront();
        Point orgn_point = new ArrayPoint(4);
        orgn_point.setDimensionValue(0, 0);
        orgn_point.setDimensionValue(1, 0);
        orgn_point.setDimensionValue(2, 0);
        orgn_point.setDimensionValue(3, 0);

        //hypervolume.
        return value;
    }

    @Override
    public String getName() {
        return "HV";
    }

    @Override
    public String getDescription() {
        return "HyperVolume Without Contribution";
    }
}
