package me.mackaber.tesis.Util;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.component.ComputeQualityIndicators;
import org.uma.jmetal.util.experiment.component.ExecuteAlgorithms;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.util.PointSolution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * This class computes the {@link QualityIndicator}s of an experiment. Once the algorithms of an
 * experiment have been executed through running an instance of class {@link ExecuteAlgorithms},
 * the list of indicators in obtained from the {@link ExperimentComponent #getIndicatorsList()} method.
 * Then, for every combination algorithm + problem, the indicators are applied to all the FUN files and
 * the resulting values are store in a file called as {@link QualityIndicator #getName()}, which is located
 * in the same directory of the FUN files.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ComputeQualityIndicatorsWoNorm<S extends Solution<?>, Result> extends ComputeQualityIndicators {

    private final Experiment<S, Result> experiment;

    public ComputeQualityIndicatorsWoNorm(Experiment<S, Result> experiment) {
        super(experiment);
        this.experiment = experiment;
    }

    @Override
    public void run() throws IOException {

        for (GenericIndicator<S> indicator : experiment.getIndicatorList()) {
            JMetalLogger.logger.info("Computing indicator: " + indicator.getName());
            ;

            for (ExperimentAlgorithm<?, Result> algorithm : experiment.getAlgorithmList()) {
                String algorithmDirectory;
                algorithmDirectory = experiment.getExperimentBaseDirectory() + "/data/" + algorithm.getAlgorithmTag();
                String problemDirectory = algorithmDirectory + "/" + algorithm.getProblemTag();

                String referenceFrontDirectory = experiment.getReferenceFrontDirectory();

                String referenceFrontName = referenceFrontDirectory + "/" + algorithm.getReferenceParetoFront();

                JMetalLogger.logger.info("RF: " + referenceFrontName);
                ;
                Front referenceFront = new ArrayFront(referenceFrontName);

                String qualityIndicatorFile = problemDirectory + "/" + indicator.getName();

                indicator.setReferenceParetoFront(referenceFront);
                String frontFileName = problemDirectory + "/" +
                        experiment.getOutputParetoFrontFileName() + algorithm.getRunId() + ".tsv";

                Front front = new ArrayFront(frontFileName);
                List<PointSolution> population = FrontUtils.convertFrontToSolutionList(front);
                Double indicatorValue = indicator.evaluate((List<S>) population);
                JMetalLogger.logger.info(indicator.getName() + ": " + indicatorValue);

                writeQualityIndicatorValueToFile(indicatorValue, qualityIndicatorFile);
            }
        }
        findBestIndicatorFronts(experiment);
    }

    private void writeQualityIndicatorValueToFile(Double indicatorValue, String qualityIndicatorFile) {
        FileWriter os;
        try {
            os = new FileWriter(qualityIndicatorFile, false);
            os.write("" + indicatorValue + "\n");
            os.close();
        } catch (IOException ex) {
            throw new JMetalException("Error writing indicator file" + ex);
        }
    }
}

