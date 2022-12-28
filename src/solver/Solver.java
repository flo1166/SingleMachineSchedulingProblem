package solver;

import scheduling.Job;
import scheduling.Schedule;

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
		jobs[0] = new Job("A", 6, 0, 14);
		jobs[1] = new Job("B", 3, 1, 17);
		jobs[2] = new Job("C", 8, 5, 15);
		jobs[3] = new Job("D", 5, 8, 13);
		
		Schedule[] schedule = new Schedule[1];
		schedule[0] = new Schedule(jobs);
		System.out.println(schedule[0].getMaxLateness());
	}

}
