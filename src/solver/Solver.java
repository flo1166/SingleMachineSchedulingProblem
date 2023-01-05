package solver;

import branchandbound.BranchAndBound;
import scheduling.Preemption;

/**
 * This class solves a single machine scheduling problem.
 * @author Florian Korn, Andre Konersmann
 *
 */
public class Solver {
	
	/**
	 * The main class calls all methods to solve a single machine scheduling problem.
	 * @param args
	 */
	public static void main(String[] args) {
		
		// the jobs with the initial data
		Preemption[] jobs = new Preemption[4];
		jobs[0] = new Preemption("A", 6, 0, 14);
		jobs[1] = new Preemption("B", 3, 1, 17);
		jobs[2] = new Preemption("C", 8, 5, 15);
		jobs[3] = new Preemption("D", 5, 8, 13);
		
		// initiate a branch and bound tree structure
		BranchAndBound schedule = new BranchAndBound();
		
		// initiate root problem and first depth
		schedule.rootProblem(jobs);	
		
		// work further depths until end of tree
		for (int i = 1; i < jobs.length; i++) {
			schedule.branching(jobs);
		}
		
		// check optimal solution, otherwise start again
		while (schedule.checkOptimalSolution(jobs)) {
			for (int i = 1; i < schedule.nodes.length - 1; i++) {
				schedule.branching(jobs);
			}
		}
	}
}
