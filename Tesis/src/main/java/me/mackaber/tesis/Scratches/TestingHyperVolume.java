package me.mackaber.tesis.Scratches;

import org.apache.commons.lang3.ArrayUtils;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;

import java.util.*;

import static java.util.stream.Collectors.toMap;
import static org.uma.jmetal.util.front.util.FrontUtils.getInvertedFront;

public class TestingHyperVolume {

    public static void main(String[] args) throws Exception {
        PISAHypervolume hypervolume = new PISAHypervolume();

        HashMap<Integer, Double> volumes = new HashMap<>();

        for (int i = 0; i < 5; i++) {

            ArrayFront front = new ArrayFront("SOS/data/NSGAII/Grouping_Problem_20/FUN" + i + ".tsv");
            Front invertedFront = getInvertedFront(front);
            double[][] doubleArrayFront = FrontUtils.convertFrontToArray(front);
            double value = hypervolume.calculateHypervolume(doubleArrayFront,front.getNumberOfPoints(),front.getPointDimensions());
            System.out.println("" + value);
            volumes.put(i, value);

        }

        LinkedHashMap<Integer, Double> sorted = volumes.entrySet()
                .stream()
                //.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .sorted(Map.Entry.comparingByValue())
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));

        System.out.println(sorted);
    }
}
