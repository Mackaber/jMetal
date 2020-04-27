package me.mackaber.tesis.Experiments;

import me.mackaber.tesis.ObjectiveFunctions.InterestsCosineSimilarityFunction;
import me.mackaber.tesis.Util.InterestVector;
import me.mackaber.tesis.Util.User;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class InterestSimilarityRanking {

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

    public static void main(String[] args) throws IOException {
        List<User> users;
        users = readProblem("Tesis/src/main/resources/synthetic_10.csv","Tesis/src/main/resources/custom_interests.json");

        InterestsCosineSimilarityFunction interestsFunction = new InterestsCosineSimilarityFunction();
        HashMap<Integer, Map<Integer, Double>> rankings = interestsFunction.getRankings(users);

        for(Integer user : rankings.keySet()){
            System.out.print(user + ": ");
            for (Integer pref_user : rankings.get(user).keySet()){
                System.out.print(pref_user + "("+ rankings.get(user).get(pref_user) + ")" + ",");
            }
            System.out.println();
        }
    }



}
