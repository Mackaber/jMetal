package me.mackaber.tesis.Scratches;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;

import java.io.FileNotFoundException;
import java.util.List;

public class CompareHyperVolume {
    public static void main(String[] args) throws Exception {

        int problem_size = 20;
        String experiment = "Random_Descend_20_20_200_1551902029162";


//        double inside_prc = getInsidePRC("Thing Study/data/" + experiment + "/MultiObjectiveGrouping_" + problem_size + "/FUN",
//                "Tesis/src/main/resources/paretoFronts/SingleObjectiveGrouping_" + problem_size + ".pf",
//                20);

        double inside_prc = getInsidePRC("Thing Study/data/" + experiment + "/SingleObjectiveGrouping_" + problem_size + "/DEC",
                "Tesis/src/main/resources/paretoFronts/SingleObjectiveGrouping_" + problem_size + ".pf",
                20);

        System.out.print(inside_prc);
    }


    public static double getInsidePRC(String file_name, String pareto_front, int runs) throws FileNotFoundException {
        PISAHypervolume hypervolume = new PISAHypervolume<>(pareto_front);
        Double hpareto = hypervolume.evaluate(new ArrayFront(pareto_front));

        int inside = 0;

        for (int num = 0; num < runs - 1; num++) {
            String solution = file_name + num + ".tsv";

            Double hsolution = hypervolume.evaluate(new ArrayFront(solution));

            System.out.println(hpareto);
            System.out.println(hsolution);
            System.out.println();

            if (hsolution > hpareto)
                inside++;
        }

        return inside / runs;
    }
}



