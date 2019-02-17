package me.mackaber.tesis.Util;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SingleObjectiveExperimentAlgorithm<S extends Solution<?>, Result> extends ExperimentAlgorithm<S,Result> {
    public SingleObjectiveExperimentAlgorithm(Algorithm algorithm, String algorithmTag, ExperimentProblem problem, int runId) {
        super(algorithm, algorithmTag, problem, runId);
    }


    @Override
    public void runAlgorithm(Experiment<?, ?> experimentData) {
        String outputDirectoryName = experimentData.getExperimentBaseDirectory()
                + "/data/"
                + getAlgorithmTag()
                + "/"
                + getProblemTag();

        File outputDirectory = new File(outputDirectoryName);
        if (!outputDirectory.exists()) {
            boolean result = new File(outputDirectoryName).mkdirs();
            if (result) {
                JMetalLogger.logger.info("Creating " + outputDirectoryName);
            } else {
                JMetalLogger.logger.severe("Creating " + outputDirectoryName + " failed");
            }
        }

        String funFile = outputDirectoryName + "/FUN" + getRunId() + ".tsv";
        String decFile = outputDirectoryName + "/DEC" + getRunId() + ".tsv";
        String varFile = outputDirectoryName + "/VAR" + getRunId() + ".tsv";

        JMetalLogger.logger.info(
                " Running algorithm: " + getAlgorithmTag() +
                        ", problem: " + getProblemTag() +
                        ", run: " + getRunId() +
                        ", funFile: " + funFile);


        getAlgorithm().run();
        Result solution = getAlgorithm().getResult();

        List<Result> population = new ArrayList<>(1);
        population.add(solution);


        DecomposedSolution decomposedSolution = new DecomposedSolution(solution);

        // Print the Decomposed result values...

        new SolutionListOutput(decomposedSolution.getPopulation())
                .setSeparator("\t")
                .setFunFileOutputContext(new DefaultFileOutputContext(decFile))
                .print();

        // Print everything else

        new SolutionListOutput((List<S>) population)
                .setSeparator("\t")
                .setVarFileOutputContext(new DefaultFileOutputContext(varFile))
                .setFunFileOutputContext(new DefaultFileOutputContext(funFile))
                .print();
    }
}
