package me.mackaber.tesis.Scratches;

import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.WFGHypervolume;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;

import java.util.List;

public class HVTests {
    public static void main(String[] args) {

        // Reference Front

        ArrayFront ref_front = create_two_point_front(2,4,7,2);

        // A point inside the reference front

        ArrayFront inside = create_single_point_front(1,1);

        // A point outside the reference front

        ArrayFront outside = create_single_point_front(2,2);

        // A point part the reference front

        ArrayFront part = create_single_point_front(1,2);

        // A front outside the reference front

        ArrayFront front_alt = create_two_point_front(5,6,3,9);

        // Hypervolumes with a reference front (2,2)

        PISAHypervolume hypervolume_pisa_r = new PISAHypervolume<>(ref_front);
        WFGHypervolume hypervolume_wfgh_r = new WFGHypervolume(ref_front);
        InvertedGenerationalDistancePlus IGDP = new InvertedGenerationalDistancePlus(ref_front);

        System.out.println("HyperVolume with a reference Front");
        System.out.println("Inside");
        System.out.println("HV PISA:");
        System.out.println(hypervolume_pisa_r.evaluate(inside));
        System.out.println(IGDP.invertedGenerationalDistancePlus(inside,ref_front));
        System.out.println();
        System.out.println("HV WFGH:");
        //hypervolume_wfgh_r.evaluate(inside))
        System.out.println();
        System.out.println("Outside");
        System.out.println("HV PISA:");
        System.out.println(hypervolume_pisa_r.evaluate(outside));
        System.out.println(IGDP.invertedGenerationalDistancePlus(outside,ref_front));
        System.out.println();
        System.out.println("HV WFGH:");
        //hypervolume_wfgh_r.evaluate(inside))
        System.out.println();
        System.out.println("Part");
        System.out.println("HV PISA:");
        System.out.println(hypervolume_pisa_r.evaluate(part));
        System.out.println();
        System.out.println("HV WFGH:");
        //hypervolume_wfgh_r.evaluate(inside))
        System.out.println("Front Alt");
        System.out.println("HV PISA:");
        System.out.println(hypervolume_pisa_r.evaluate(front_alt));
        System.out.println();
        System.out.println("HV WFGH:");
        //hypervolume_wfgh_r.evaluate(inside))

        // Hypervolumes without a reference front
        PISAHypervolume hypervolume_pisa = new PISAHypervolume<>();
        WFGHypervolume hypervolume_wfgh = new WFGHypervolume();
    }

    static ArrayFront create_single_point_front(double x, double y) {
        ArrayFront front = new ArrayFront(1,2);
        Point point_p = new ArrayPoint(2);
        point_p.setDimensionValue(0,x);
        point_p.setDimensionValue(1,y);
        front.setPoint(0,point_p);
        return front;
    }

    static ArrayFront create_two_point_front(double x1, double y1, double x2, double y2) {
        ArrayFront front = new ArrayFront(2,2);

        Point point_1 = new ArrayPoint(2);
        point_1.setDimensionValue(0,x1);
        point_1.setDimensionValue(1,y1);
        front.setPoint(0,point_1);

        Point point_2 = new ArrayPoint(2);
        point_2.setDimensionValue(0,x2);
        point_2.setDimensionValue(1,y2);
        front.setPoint(1,point_2);

        return front;
    }
}
