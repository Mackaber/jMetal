package me.mackaber.tesis.Experiments;

import me.mackaber.tesis.ObjectiveFunctions.GroupSizeFunction;
import me.mackaber.tesis.ObjectiveFunctions.InterestsCosineSimilarityFunction;
import me.mackaber.tesis.ObjectiveFunctions.LevelFunction;
import me.mackaber.tesis.ObjectiveFunctions.ParticipationStyleFunction;
import me.mackaber.tesis.SingleObjective.GroupSolution;
import me.mackaber.tesis.SingleObjective.GroupingProblem;
import me.mackaber.tesis.SingleObjective.GroupingSolution;
import me.mackaber.tesis.Util.Function;
import me.mackaber.tesis.Util.InterestVector;
import me.mackaber.tesis.Util.User;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class MeasureStability {
        private static List<User> readProblem(String userFile,String interestsFile) throws IOException {
            InterestVector vector = new InterestVector(interestsFile);
            List<User> problem_users = new ArrayList<>();

            Reader in = new FileReader(userFile);
            CSVParser records = CSVFormat.DEFAULT.parse(in);
            for (CSVRecord record : records) {
                List<String> interests = Arrays.asList(record.get(2), record.get(3), record.get(4));
                User user = new User(Integer.parseInt(record.get(0)));
                user.setLevel(Integer.parseInt(record.get(1)))
                        .setInterests(interests)
                        .setPart_prc(Double.parseDouble(record.get(5)))
                        .setPart_time(Double.parseDouble(record.get(6)));

                user.setInterestVector(vector.getUserInterestVector(interests));

                problem_users.add(user);
            }
            return problem_users;
        }

        private static int[] readFileSolution(String file) throws IOException {

            Reader in = new FileReader(file);
            CSVParser records = CSVFormat.TDF.parse(in);
            int size = records.getRecords().get(0).size();

            int[] sol = new int[size];
            int i = 0;
            for(String user: records.getRecords().get(0)) {
                sol[i] = Integer.parseInt(user);
                i++;
            }
            return  sol;
        }

        private static List<List<User>> readSolution(int[] solution,List<User> users) {
            List<List<User>> groups = new LinkedList<>();
            groups.add(0,new ArrayList<>());
            groups.add(1,new ArrayList<>());
            groups.add(2,new ArrayList<>());
            groups.add(3,new ArrayList<>());
            groups.add(4,new ArrayList<>());

            int usrId = 0;
            for(int group: solution){
                List<User> g = groups.get(group);
                g.add(users.get(usrId));
                usrId++;
            }

            return groups;
        }

        private static void printResult(String name, int gss, int pss, int ints, int lvls, int all) {
            System.out.println(name);
            System.out.println("----------------");
            System.out.println("Gs-Stable: " + gss);
            System.out.println("Ps-Stable: " + pss);
            System.out.println("Int-Stable: " + ints);
            System.out.println("Lvl-Stable: " + lvls);
            System.out.println("Over-all: " + all);
            System.out.println();
        }
         
        private static int checkStability(Function fn,List<List<User>> groups, List<User> users){
            int unstable_users = 0;
            for(User user: users){
                double stay = 0.0;
                double leave = 0.0;

                List<List<User>> groups_m = new ArrayList<List<User>>(groups);
                
                for(List<User> group: groups_m){
                    if(group.contains(user)) {
                        stay = fn.eval(group);
                        groups_m.remove(group);
                        break;
                    }
                }

                for(List<User> group: groups_m){
                    group.add(user);
                    leave = fn.eval(group);
                    if(stay > leave){
                        unstable_users++;
                        break;
                    }
                }
            }
            return users.size() - unstable_users;
        }

    private static int checkStabilityAll(List<Function> fns, List<List<User>> groups, List<User> users){
        int unstable_users = 0;
        for(User user: users){
            double[] stay = new double[4];
            double[] leave = new double[4];


            List<List<User>> groups_m = new ArrayList<List<User>>(groups);

            for(List<User> group: groups_m){
                if(group.contains(user)) {
                    int i = 0;
                    for(Function fn: fns){
                        stay[i] = fn.eval(group);
                        i++;
                    }
                    groups_m.remove(group);
                    break;
                }
            }

            for(List<User> group: groups_m){
                group.add(user);
                int i = 0;
                for(Function fn: fns){
                    stay[i] = fn.eval(group);
                    i++;
                }
                if(stay[0] > leave[0] && stay[1] > leave[1] && stay[2] > leave[2] && stay[3] > leave[3]){
                    unstable_users++;
                    break;
                }
            }
        }
        return users.size() - unstable_users;
    }

        public static void main(String[] args) throws IOException {
            List<User> users;

            users = readProblem("Tesis/src/main/resources/synthetic_10001.csv","Tesis/src/main/resources/custom_interests.json");
            List<List<User>> groups = new LinkedList<>();
            List<Function> functions = new ArrayList<>();
            functions.add(new GroupSizeFunction());
            functions.add(new ParticipationStyleFunction());
            functions.add(new InterestsCosineSimilarityFunction());
            functions.add(new LevelFunction());


            GroupingProblem problem = new GroupingProblem("Tesis/src/main/resources/synthetic_20.csv");
            problem.build();
            GroupSolution solution = problem.createSolution();
            System.out.println(solution);

            int[] random_ar = {1, 1, 2, 2, 2, 1, 0, 0, 3, 1, 3, 3, 0, 3, 1, 0, 0, 3, 1, 3};
            List<List<User>> random = readSolution(random_ar, users);
            printResult("Random",
                    checkStability(new GroupSizeFunction(), random, users),
                    checkStability(new ParticipationStyleFunction(), random, users),
                    checkStability(new InterestsCosineSimilarityFunction(), random, users),
                    checkStability(new LevelFunction(), random, users),
                    checkStabilityAll(functions,random,users)
            );

            // Local search

            //int[] local_search_arr = {3, 0, 3, 4, 4, 3, 2, 2, 3, 3, 1, 1, 1, 0, 3, 3, 4, 0, 1, 1};
            String[] algs = {"Elitist","NON_Elitist","Genetic_Generational","Genetic_Steady","Local_search","Random_Search",
                            "Random_Descent","Parallel_Tempering","ESPEA","NSGAII","MOMBI","RS","SPEA2"};

            for(String alg: algs){
                int[] local_search_arr = readFileSolution("Tesis/src/main/resources/best_20"+ alg + ".tsv");
                List<List<User>> local_search = readSolution(local_search_arr, users);
                printResult("LocalSearch",
                        checkStability(new GroupSizeFunction(), local_search, users),
                        checkStability(new ParticipationStyleFunction(), local_search, users),
                        checkStability(new InterestsCosineSimilarityFunction(), local_search, users),
                        checkStability(new LevelFunction(), local_search, users),
                        checkStabilityAll(functions,local_search,users)
                );
            }
        }
}
