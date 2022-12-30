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
		
		Preemption[] jobs = new Preemption[4];
		jobs[0] = new Preemption("A", 6, 0, 14);
		jobs[1] = new Preemption("B", 3, 1, 17);
		jobs[2] = new Preemption("C", 8, 5, 15);
		jobs[3] = new Preemption("D", 5, 8, 13);
		
		BranchAndBound schedule = new BranchAndBound(jobs);
		
		Preemption[] jobs2 = new Preemption[4];
		jobs2[2] = new Preemption("A", 6, 0, 14);
		jobs2[0] = new Preemption("B", 3, 1, 17);
		jobs2[3] = new Preemption("C", 8, 5, 15);
		jobs2[1] = new Preemption("D", 5, 8, 13);
		BranchAndBound schedule2 = new BranchAndBound(jobs2);
		
		int lowerBound = schedule.rootProblem();
		System.out.println("The lower bound of the root is: " + lowerBound);
		
		int newBound = schedule2.maxLateness(jobs2, false);
		System.out.println("The lower bound of the problem is: " + newBound);
	}

}
