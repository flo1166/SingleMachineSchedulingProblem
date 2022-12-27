package solver;

import job.Job;

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
		
		Job[] jobs = new Job[4];
		jobs[0] = new Job("A", 1, 8, 10);
		jobs[1] = new Job("B", 3, 0, 7);
		jobs[2] = new Job("C", 5, 6, 11);
		jobs[3] = new Job("D", 3, 1, 4);
	}

}
