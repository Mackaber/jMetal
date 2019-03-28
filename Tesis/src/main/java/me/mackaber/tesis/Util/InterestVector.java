package me.mackaber.tesis.Util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterestVector {
    private final HashMap<String, HashMap<String, Double>> interestsTree = new HashMap<>();

    public InterestVector(String interestTreeFile) throws FileNotFoundException {
        JsonParser parser = new JsonParser();
        FileReader reader = new FileReader(interestTreeFile);
        JsonElement interestsJson = parser.parse(reader);

        JsonArray interests = interestsJson.getAsJsonArray();
        for (JsonElement entry : interests) {
            JsonObject interest = (JsonObject) entry;
            JsonArray path = interest.get("path").getAsJsonArray();

            HashMap<String, Double> path_values = new HashMap<>();
            Double i = (double) path.size();
            for (JsonElement element : path.getAsJsonArray()) {
                path_values.put(element.getAsString(), i);
                i--;
            }
            path_values.put(interest.get("raw_name").getAsString(), 0.0);
            interestsTree.put(interest.get("raw_name").getAsString(), path_values);
        }
    }

    public HashMap<String, Double> getUserInterestVector(List<String> interests) {
        HashMap<String, Double> userVector = new HashMap<>();
        for (String interest : interests) {
            HashMap<String, Double> path = getUserInterestPath(interest);
            for (Map.Entry<String, Double> branch : path.entrySet()) {
                updateVector(userVector, branch.getKey(), branch.getValue());
            }
        }
        return userVector;
    }

    private HashMap<String, Double> getUserInterestPath(String interest) {
        return interestsTree.get(interest);
    }

    private void updateVector(HashMap<String, Double> user_vector, String name, Double value) {
        if (user_vector.containsKey(name))
            user_vector.put(name, Math.max(user_vector.get(name), (1.0 / (value + 1.0))));
        else
            user_vector.put(name, (1.0 / (value + 1.0)));
    }

}
