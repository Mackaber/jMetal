package me.mackaber.tesis.SingleObjective;

import me.mackaber.tesis.Util.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.Max;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupingProblem extends CombinationProblem {
    private final String userFile;
    private List<Function> functions = new ArrayList<>();
    private AbstractStorelessUnivariateStatistic ct_measure = new Mean();
    private int min_size = 3;
    private int max_size = 6;
    private int usersSize;
    private List<User> users;
    private Type type;
    private InterestVector vector;
    private List<Double> weights = new ArrayList<>();

    public enum Type {SINGLE_OBJECTIVE, MULTI_OBJECTIVE}

    public GroupingProblem(String usersFile) {
        this.userFile = usersFile;
    }

    public GroupingProblem setGroupSizeRange(int min_size, int max_size) {
        this.min_size = min_size;
        this.max_size = max_size;
        return this;
    }

    public GroupingProblem addObjectiveFunction(Function function) {
        this.functions.add(function);
        return this;
    }

    public GroupingProblem setCentralTendencyMeasure(AbstractStorelessUnivariateStatistic ct_measure) {
        this.ct_measure = ct_measure;
        return this;
    }

    public GroupingProblem setType(Type type) {
        this.type = type;
        return this;
    }

    public GroupingProblem setVector(InterestVector vector) {
        this.vector = vector;
        return this;
    }

    public InterestVector getVector() {
        return vector;
    }

    public String getUserFile() {
        return userFile;
    }

    public void build() throws IOException {
        users = readProblem(userFile);
        if (type == Type.SINGLE_OBJECTIVE)
            setNumberOfObjectives(1);
        else
            setNumberOfObjectives(functions.size());
        setName("Grouping_Problem_" + usersSize);
    }

    public void buildHolder(GroupSolution solution) {
        setNumberOfVariables(solution.getNumberOfVariables());
        setNumberOfObjectives(functions.size());
    }

    @Override
    public void evaluate(GroupSolution solution) {
        int j = 0;

        // Default Weighted Sum Method
        double fitness = 0;
        for (Function function : functions) {
            int n_groups = solution.getGroups().getInternalGroups().size();
            double[] results = new double[n_groups];
            for (int i = 0; i < n_groups - 1; i++) {
                List<User> group = solution.getUserGroup(i);
                if (group.size() > 0)
                    results[i] = (function.eval(group));
            }

            fitness = ct_measure.evaluate(results);
            solution.setObjective(j, fitness);
            j++;
        }
    }

    @Override
    public GroupSolution createSolution() {
        DefaultGroupSolution solution = new DefaultGroupSolution(this);
        return solution.repair();
    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public int getMinSize() {
        return min_size;
    }

    @Override
    public int getMaxSize() {
        return max_size;
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
            if (this.vector != null) {
                user.setInterestVector(vector.getUserInterestVector(interests));
            }
            problem_users.add(user);
        }
        usersSize = problem_users.size();
        setNumberOfVariables(usersSize);
        return problem_users;
    }
}
