package me.mackaber.tesis.Util;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.fileoutput.FileOutputContext;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SolutionImprovementOutput {
    private FileOutputContext impFileContext;
    private String impFileName = "IMP";
    private String separator = ",";
    private List<Double> improvementList;

    public SolutionImprovementOutput(List<Double> improvementList) {
        impFileContext = new DefaultFileOutputContext(impFileName);
        impFileContext.setSeparator(separator);
        this.improvementList = improvementList;
    }

    public SolutionImprovementOutput setImpFileOutputContext(FileOutputContext fileContext) {
        impFileContext = fileContext;

        return this;
    }

    public SolutionImprovementOutput setSeparator(String separator) {
        this.separator = separator;
        impFileContext.setSeparator(this.separator);
        return this;
    }

    public void print() {
        printImprovementsToFile(impFileContext, improvementList);
    }

    public void printImprovementsToFile(FileOutputContext context, List<Double> improvementList) {
        BufferedWriter bufferedWriter = context.getFileWriter();

        try {
            if (improvementList.size() > 0) {
                for (Double improvement : improvementList) {
                    bufferedWriter.write(improvement + context.getSeparator());
                    bufferedWriter.newLine();
                }
            }

            bufferedWriter.close();
        } catch (IOException e) {
            throw new JMetalException("Error writing data ", e);
        }

    }

    /*
     * Wrapper for printing with default configuration
     */

    public void printImprovementsToFile(String fileName) throws IOException {
        printImprovementsToFile(new DefaultFileOutputContext(fileName), improvementList);
    }

}
