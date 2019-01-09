package me.mackaber.tesis.MultiObjective;

import me.mackaber.tesis.SingleObjective.DefaultGroupingSolution;
import me.mackaber.tesis.SingleObjective.GroupingSolution;
import me.mackaber.tesis.SingleObjective.SingleObjectiveGrouping;
import me.mackaber.tesis.Util.CombinationProblem;
import me.mackaber.tesis.Util.Function;
import me.mackaber.tesis.Util.InterestsFunction;
import me.mackaber.tesis.Util.User;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiObjectiveGrouping extends CombinationProblem {
    private final String userFile;
    private List<Function> functions;
    private InterestsFunction interestsFunction = null;
    private AbstractStorelessUnivariateStatistic ct_measure = new Mean();
    private int min_size = 3;
    private int max_size = 6;
    private int usersSize;
    private List<User> users;

    public MultiObjectiveGrouping(String userFile) {
        this.userFile = userFile;
        this.functions = new ArrayList<>();
    }

    public MultiObjectiveGrouping setGroupSizeRange(int min_size, int max_size) {
        this.min_size = min_size;
        this.max_size = max_size;
        setNumberOfVariables((int) Math.ceil(usersSize / min_size));
        return this;
    }

    public MultiObjectiveGrouping addObjectiveFunction(Function function) {
        this.functions.add(function);
        return this;
    }

    public MultiObjectiveGrouping setInterestsFunction(InterestsFunction function) {
        this.functions.add(function);
        this.interestsFunction = function;
        return this;
    }

    public MultiObjectiveGrouping setCentralTendencyMeasure(AbstractStorelessUnivariateStatistic ct_measure) {
        this.ct_measure = ct_measure;
        return this;
    }

    public void build() throws IOException {
        users = readProblem(userFile);
        setNumberOfVariables((int) Math.ceil(usersSize / min_size));
        setNumberOfObjectives(functions.size());
        setName("MultiObjectiveGrouping");
    }

    @Override
    public void evaluate(GroupingSolution<List<User>> solution) {
        double fitness;
        int j = 0;

        for(Function function: functions) {
            double[] results = new double[getNumberOfVariables()];
            for (int i = 0; i < solution.getNumberOfVariables(); i++) {
                if (solution.getVariableValue(i).size() > 0) // The solution may contain empty groups as variables
                    results[i] = (function.eval(solution.getVariableValue(i)));
                else {
                    results = Arrays.copyOfRange(results, 0, i);
                    break;
                }
            }

            fitness = ct_measure.evaluate(results);
            solution.setObjective(j,fitness);
            j++;
        }
    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public int getMinSize() {
        return min_size;
    }

    @Override
    public int getMaxSize() {
        return max_size;
    }

    @Override
    public GroupingSolution<List<User>> createSolution() {
        return new DefaultGroupingSolution(this);
    }

    private List<User> readProblem(String file) throws IOException {
        List<User> problem_users = new ArrayList<>();

        Reader in = new FileReader(file);
        CSVParser records = CSVFormat.DEFAULT.parse(in);
        for (CSVRecord record : records) {
            List<String> interests = Arrays.asList(record.get(2), record.get(3), record.get(4));
            User user = new User(Integer.parseInt(record.get(0)));
            user.setLevel(Integer.parseInt(record.get(1)))
                    .setInterests(interests)
                    .setPart_prc(Double.parseDouble(record.get(5)))
                    .setPart_time(Double.parseDouble(record.get(6)));
            if(interestsFunction != null) {
                user.setInterestVector(interestsFunction.getInterestVector(interests));
            }
            problem_users.add(user);
        }
        usersSize = problem_users.size();
        return problem_users;
    }
}
