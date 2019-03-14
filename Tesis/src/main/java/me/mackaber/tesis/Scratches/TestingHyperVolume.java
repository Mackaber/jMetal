package me.mackaber.tesis.Scratches;

import org.apache.commons.lang3.ArrayUtils;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.util.front.imp.ArrayFront;

import java.util.*;

import static java.util.stream.Collectors.toMap;

public class TestingHyperVolume {

    public static void main(String[] args) throws Exception {
        PISAHypervolume hypervolume = new PISAHypervolume<>("Thing Study/LITTLEMONSTER/NSGAII_200_20_200_1550372695641/MultiObjectiveGrouping_10001/FUN67.tsv");

        HashMap<Integer, Double> volumes = new HashMap<>();

        for(int i = 0;i<20;i++) {
//            Double value = hypervolume.evaluate(new ArrayFront("Thing Study/LITTLEMONSTER/NSGAII_200_20_200_1550372695641/MultiObjectiveGrouping_20/FUN" + i + ".tsv"));
//            Double value = hypervolume.evaluate(new ArrayFront("Thing Study/LITTLEMONSTER/Genetic_Algorithm_20_20_200_1550369899318/SingleObjectiveGrouping_10001/DEC" + i + ".tsv"));


//            System.out.println("" + value);
//            volumes.put(i, value);
        }

        LinkedHashMap<Integer, Double> sorted = volumes.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));

        System.out.println(sorted);
    }
}
