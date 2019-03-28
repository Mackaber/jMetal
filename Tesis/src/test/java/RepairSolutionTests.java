import me.mackaber.tesis.ObjectiveFunctions.*;
import me.mackaber.tesis.SingleObjective.GroupSolution;
import me.mackaber.tesis.SingleObjective.GroupingProblem;
import me.mackaber.tesis.Util.Groups;
import me.mackaber.tesis.Util.InterestVector;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.jamesframework.core.problems.sol.Solution;
import org.junit.Test;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotSame;

public class RepairSolutionTests {

    private GroupingProblem buildProblem() throws IOException {
        GroupingProblem problem = new GroupingProblem("src/main/resources/synthetic_20.csv");

        WeightedFunction function = new WeightedFunction();
        function.addObjectiveFunction(1.0, new GroupSizeFunction())
                .addObjectiveFunction(1.0, new InterestsCosineSimilarityFunction())
                .addObjectiveFunction(1.0, new LevelFunction())
                .addObjectiveFunction(1.0, new ParticipationStyleFunction());

        problem.setGroupSizeRange(3, 6)
                .addObjectiveFunction(function)
                .setVector(new InterestVector("src/main/resources/custom_interests.json"))
                .setCentralTendencyMeasure(new Mean())
                .build();
        return problem;
    }


    @Test
    public void dontChangeAvalidSolution() throws IOException {
        GroupSolution groupSolution = new GroupSolution(buildProblem());

        Integer[] sequence = {0,0,3,1,2,2,1,3,1,1,2,3,0,1,3,0,3,2,0,2};

        groupSolution.setGroups(new Groups(20));

        int i = 0;
        for(Integer groupId: sequence){
            groupSolution.setVariableValue(i,groupId);
            i++;
        }

        GroupSolution newSolution = groupSolution.repair();

        assertEquals(groupSolution,newSolution);
    }

    @Test
    public void ChangeAnInvalidSolution() throws IOException {
        GroupSolution groupSolution = new GroupSolution(buildProblem());

        Integer[] sequence = {0,0,0,0,0,2,1,3,1,1,2,3,0,1,3,0,3,2,0,2};

        groupSolution.setGroups(new Groups(20));

        int i = 0;
        for(Integer groupId: sequence){
            groupSolution.setVariableValue(i,groupId);
            i++;
        }

        GroupSolution newSolution = groupSolution.repair();

        assertNotSame(groupSolution,newSolution);
    }
}
