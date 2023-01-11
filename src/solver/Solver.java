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
		
		boolean preemption = true;
		
		// the jobs with the initial data
		Preemption[] jobs = new Preemption[4];
		jobs[0] = new Preemption("A", 6, 0, 14);
		jobs[1] = new Preemption("B", 3, 1, 17);
		jobs[2] = new Preemption("C", 8, 5, 15);
		jobs[3] = new Preemption("D", 5, 8, 13);
		
		// initiate a branch and bound tree structure
		BranchAndBound tree = new BranchAndBound();
		
		// initiate root problem and first depth
		tree.rootProblem(jobs);	
		
		// work further depths until end of tree
		for (int i = 1; i < jobs.length; i++) {
			preemption = tree.branching(jobs);
			// don't go deeper in the tree if schedule doesn't use preemption anymore
			if (!preemption) {
				i = jobs.length;
			}
		}
		
		// check optimal solution, otherwise start again
		while (tree.checkOptimalSolution(jobs)) {
			for (int i = 1; i < tree.nodes.length - 1; i++) {
				tree.branching(jobs);
			}
		}
	}
}
