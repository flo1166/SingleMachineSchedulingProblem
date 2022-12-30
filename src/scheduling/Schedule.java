package scheduling;

/**
 * This class represents a schedule of jobs in the single machine scheduling problem
 * @author Florian Korn, Andre Konersmann
 *
 */
public class Schedule {
	
	// the sequence of jobs in the schedule
	public Preemption[] sequence;
	
	// full parameter constructor
	public Schedule(Preemption[] sequence) {
		this.sequence = sequence;
	}
	
	/**
	 * Creates a new sequence of jobs by swapping job k and k+1
	 * @param delayedJob is the index of the job to be delayed
	 * @param swapedJob is the index of the job to be swapped
	 * @return a sequence of jobs with swapped jobs
	 */
	public Preemption[] swapJobs(Preemption[] sequence, Preemption delayedJob, Preemption swappedJob) {
		
		Preemption[] newSequence = new Preemption[sequence.length];
		
		for (int i = 0; i < sequence.length; i++) {
			if (sequence[i].getName() == delayedJob.getName()) {
				newSequence[i] = swappedJob;
			} else if (sequence[i].getName() == swappedJob.getName()) {
				newSequence[i] = delayedJob;
			} else {
				newSequence[i] = sequence[i];
			}
		}
		return newSequence;
	}
	
	/**
	 * This method builds a schedule to get the job end to calculate the maximum lateness
	 * @param sequence of jobs
	 * @param root if true then this is the root node (so released jobs can fill empty slots, otherwise start of schedule is release date of first job
	 * @return sequence with the end of the jobs
	 */
	public Preemption[] buildSchedule(Preemption[] sequence, boolean root) {
		
		int currentPeriod = sequence[0].getR();
		Preemption[] localSequence = sequence;
		
		// if root true, then fill empty slots before the release of the first job with other jobs
		if (root) {
			localSequence = rootSolver(localSequence, currentPeriod);
		}
		
		// variation of jobs
		for (int i = 0; i < localSequence.length; i++) {
			
			// go through periods until job is exhausted
			while (localSequence[i].remainingP != 0) {
					
					// check if neighbor is released, if true then swapJobs, otherwise none
					if (i != localSequence.length - 1 && localSequence[i+1].getR() == currentPeriod) {
						localSequence = swapJobs(localSequence, localSequence[i], localSequence[i+1]);
					}
					// adjust remainingP
					if (localSequence[i].getR() <= currentPeriod) {
						localSequence[i].remainingP -= 1;
					} else {
						localSequence = neighborReleased(localSequence, i, currentPeriod);
					}
				
					// count next period
					currentPeriod += 1;
				}	
			
			// set job end to current period (if dummy "-1" is set, otherwise job end is allready set)
			if (localSequence[i].getJobEnd() == -1) {
				localSequence[i].setJobEnd(currentPeriod);
			}
		}
		
		return localSequence;
	}
	
	/**
	 * This method looks up if neighbors have remaining p and adjusts the remaining p accordingly
	 * @param sequence of jobs
	 * @param index the current job we are looking at
	 * @param currentPeriod the period which we want to check
	 * @return sequence of jobs with adjusted remaining p
	 */
	public Preemption[] neighborReleased(Preemption[] sequence, int index, int currentPeriod) {
		
		for (int i = index + 1; i < sequence.length; i++) {
			if (sequence[i].getR() <= currentPeriod && sequence[i].remainingP != 0) {
				sequence[i].remainingP -= 1;
				i = sequence.length - 1;
				
				if (sequence[i].getJobEnd() == -1 && sequence[i].remainingP == 0) {
					sequence[i].setJobEnd(currentPeriod);
				}
			}
		}
		
		return sequence;
	}
	
	/**
	 * This method solves a root problem in the branch and bound problem.
	 * It is needed to fill the empty slots with jobs before the first problem 
	 * has been released.
	 * @param sequence with jobs
	 * @param currentPeriod is the current period
	 * @return sequence with changed jobs
	 */
	public Preemption[] rootSolver(Preemption[] sequence, int currentPeriod) {
		if (currentPeriod > 0) {
			int j = 1;
			for (int i = 0; i < currentPeriod; i++) {
				// if remaining p is exhausted, change job and set job end
				if (sequence[j].remainingP == 0) {
					sequence[j].setJobEnd(i);
					j += 1;
				}
				// if job is released, adjust remaining p, otherwise change job and adjust current period
				if (sequence[j].getR() <= currentPeriod) {
					sequence[j].remainingP -= 1;
				} else {
					if (j == sequence.length - 1) {
						j = 1;
					} else {
						j += 1;
						i -= 1;
					}
				}
			}
		}
		return sequence;
	}
	
	/**
	 * This method calculates the maximum lateness of a given sequence of jobs
	 * @param sequence of jobs
	 * @param root true if it is a root problem, false if not
	 * @return maximum lateness in a sequence of jobs
	 */
	public int maxLateness(Preemption[] sequence, boolean root) {
		
		buildSchedule(sequence, root);		
		int jobLateness = 0;
		int maxLateness = 0;
		
		for (int i = 0; i < sequence.length; i++) {
			jobLateness = sequence[i].getJobEnd() - sequence[i].getD();
			if (jobLateness > maxLateness) {
				maxLateness = jobLateness;
			}
		}
		printSchedule(sequence, root);
		return maxLateness;
	}
	
	/**
	 * This method prints a schedule (with job name and job end)
	 * @param sequence of jobs
	 */
	public void printSchedule(Preemption[] sequence, boolean root) {
		
		if (root) {
			System.out.println("The schedule for the root is:");
		} else {
			System.out.println("The schedule is:");
		}
		
		for (int i = 0; i < sequence.length; i++) {
			System.out.println(sequence[i].getName() + " ends in: " + sequence[i].getJobEnd());
		}
	}
// DEPRECATED formally used in buildSchedule()
//	/**
//	 * This method calculates the remaining processing time of a sequence of jobs.
//	 * @param sequence of jobs
//	 * @return remaining processing time of all jobs
//	 */
//	public int calcRemainingP(Preemption[] sequence) {
//		
//		int remainingP = 0;
//		
//		for (Preemption job : sequence) {
//			remainingP += job.getRemainingP();
//		}
//		return remainingP;
//	}
}
