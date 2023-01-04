package scheduling;

import branchandbound.BranchAndBound;

/**
 * This class extends the branch and bound with methods.
 * @author Florian Korn, Andre Konersmann
 *
 */
public class Schedule extends BranchAndBound {
	
	/**
	 * Creates a new sequence of jobs by swapping job k and k+1
	 * @param sequence is all jobs
	 * @param delayedJob is the index of the job to be delayed
	 * @param swappedJob is the index of the job to be swapped
	 * @return a sequence of jobs with swapped jobs
	 */
	public static Preemption[] swapJobs(Preemption[] sequence, Preemption delayedJob, Preemption swappedJob) {
		
		Preemption[] newSequence = new Preemption[sequence.length];
		swappedJob.preemption = true;
		
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
	 * @param EDDsequence of jobs sorted with EDD logic
	 * @param sequence of jobs (until current node)
	 * @param root if true then this is the root node (so released jobs can fill empty slots, otherwise start of schedule is release date of first job
	 * @return sequence with the end of the jobs
	 */
	public static Preemption[] buildSchedule(Preemption[] EDDsequence, Preemption[] sequence, boolean root) {
		
		// reset jobs with new remaining p and empty job end
		resetPJobs(EDDsequence);
		
		// initial variables
		int currentPeriod = EDDsequence[0].getR();
		if (sequence != null) {
			currentPeriod = sequence[0].getR();
		} 
		
		// if root true, then fill empty slots before the release of the first job with other jobs
		if (root) {
			EDDsequence = rootSolver(EDDsequence, currentPeriod);
		}
		
		// sequence of fixed jobs without preemption
		if (sequence != null) {
			currentPeriod = jobWithoutPreemption(sequence, currentPeriod);
		}
		
		// EDDsequence of jobs with preemption
		EDDsequence = jobPreemption(EDDsequence, currentPeriod);
		
		return EDDsequence;
	}
	
	/**
	 * This method resets all jobs (no job end set and remaining p equals the p of the job).
	 * @param sequence of jobs to be reseted
	 */
	public static void resetPJobs(Preemption[] sequence) {
		
		for (Preemption seq : sequence) {
			if (seq.getJobEnd() != -1) {
				seq.setJobEnd(-1);
				seq.remainingP = seq.getP();
				seq.preemption = false;
			}
		}
	}
	
	/**
	 * This method changes the p and job end in jobs without preemption
	 * @param sequence of jobs to be changed (only forced sequence is allowed without preemption)
	 * @param currentPeriod of the calculation
	 * @return a sequence of jobs with job end and p changed
	 */
	public static int jobWithoutPreemption(Preemption[] sequence, int currentPeriod) {
		
		for (int i = 0; i < sequence.length; i++) {
			while (sequence[i].remainingP != 0) {
				// distract 1 from p if job is released
				if (sequence[i].getR() <= currentPeriod) {
					sequence[i].remainingP -= 1;
				}
				currentPeriod += 1;
				// set job end if no remaining p and job end isn't already set
				if (sequence[i].remainingP == 0 && sequence[i].getJobEnd() == -1) {
					sequence[i].setJobEnd(currentPeriod);
				}
			}
		}
		return currentPeriod;
	}
	
	/**
	 * This method changes the p and job end in jobs with preemption
	 * @param sequence of jobs to be changed (not forced sequence)
	 * @param currentPeriod of the calculation
	 * @return a sequence of jobs with job end and p changed
	 */
	public static Preemption[] jobPreemption(Preemption[] sequence, int currentPeriod) {
		
		for (int i = 0; i < sequence.length; i++) {
			// go through periods until job is exhausted
			while (sequence[i].remainingP != 0) {
				// if end of sequence or neighbor is not released
				if (i == sequence.length - 1 || sequence[i+1].getR() != currentPeriod) {
					// if sequence is released
					if (sequence[i].getR() <= currentPeriod) {
						sequence[i].remainingP -= 1;
						currentPeriod += 1;
						// set job end to current period (if dummy "-1" is set, otherwise job end is already set)
						if (sequence[i].remainingP == 0 && sequence[i].getJobEnd() == -1) {
							sequence[i].setJobEnd(currentPeriod);
						}
					// if sequence is not released
					} else {
						// if not on end of sequence swap jobs
						if (sequence.length - 1 != i) {
							sequence = swapJobs(sequence, sequence[i], sequence[i+1]);
							
						// if on end no job is suitable, therefore count period +1
						} else {
							currentPeriod += 1;
						}
					}
				// if not on the end of sequence and neighbor is released, swap jobs
				} else {
					sequence = swapJobs(sequence, sequence[i], sequence[i+1]);
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
	public static Preemption[] rootSolver(Preemption[] sequence, int currentPeriod) {
		if (currentPeriod > 0) {
			int j = 1;
			for (int i = 0; i < currentPeriod; i++) {
				// if remaining p is exhausted, change job and set job end
				if (sequence[j].remainingP == 0 && sequence[j].getJobEnd() == -1) {
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
	 * @param EDDsequence of all jobs sorted with EDD logic
	 * @param sequence of jobs (until current node)
	 * @param root true if it is a root problem, false if not
	 * @return maximum lateness in a sequence of jobs
	 */
	public static int maxLateness(Preemption[] EDDsequence, Preemption[] sequence, boolean root) {
		
		buildSchedule(EDDsequence, sequence, root);		
		int jobLateness = 0;
		int maxLateness = 0;
		
		for (int i = 0; i < EDDsequence.length; i++) {
			jobLateness = EDDsequence[i].getJobEnd() - EDDsequence[i].getD();
			if (jobLateness > maxLateness) {
				maxLateness = jobLateness;
			}
		}
		printSchedule(EDDsequence, sequence, root, maxLateness);

		return maxLateness;
	}
	
	/**
	 * This sorts a sequence of jobs via selection sort.
	 * @param sequence of jobs
	 * @param ascending if true, then ascending otherwise decending
	 */
	public static Preemption[] selectionSortEDD(Preemption[] sequence, boolean ascending) {
		
		for (int i = 0; i < sequence.length; i++) {
			
			// find min / max
			int minMax = i;
			
			for (int j = i + 1; j < sequence.length; j++) {
				if(ascending && sequence[j].getD() < sequence[minMax].getD()) {
					minMax = j;
				} else if (!ascending && sequence[j].getD() > sequence[minMax].getD()) {
					minMax = j;
				}
			}
			
			// swap min/max element into place
			sequence = swapJobs(sequence, sequence[i], sequence[minMax]);
		}
		return sequence;
	}
	
	/**
	 * This method prints a schedule (with job name and job end)
	 * @param sequence of jobs
	 */
	public static void printSchedule(Preemption[] EDDsequence, Preemption[] sequence, boolean root, int maxLateness) {
		
		boolean preemption = false;
		// if it isn't the root node
		if (sequence != null) {
			System.out.println("The current node: ");
			System.out.println(sequence[sequence.length - 1].getName());
			System.out.println("The forced sequence is:");
			for (Preemption job : sequence) {
				System.out.print(job.getName() + ", ");
			}
			System.out.println();
			System.out.println("The schedule is:");
			
			for (int i = 0; i < sequence.length; i++) {
				System.out.println(sequence[i].getName() + ": " + sequence[i].getJobEnd());
				if (sequence[i].preemption == true) {
					preemption = true;
				}
			}
		// if it is the root node
		} else {
			System.out.println("The schedule for the root is:");
		}
		for (int j = 0; j < EDDsequence.length; j++) {
			if (sequence == null || !checkArrayElement(sequence, EDDsequence[j])) {
				System.out.println(EDDsequence[j].getName() + ": " + EDDsequence[j].getJobEnd());
				if (EDDsequence[j].preemption == true) {
					preemption = true;
				}
			}
		}
		if (preemption) {
			System.out.println("With preemption");
			System.out.println("Max lateness >= " + maxLateness);
		} else {
			System.out.println("Without preemption");
			System.out.println("Max lateness = " + maxLateness);
		}
		System.out.println();
	}
	
	/**
	 * This method checks if a job exists in given sequence
	 * @param sequence of jobs
	 * @param Job to look for
	 * @return true if it exists, false if not
	 */
	public static boolean checkArrayElement(Preemption[] sequence, Preemption Job) {
		
		for (Preemption seq : sequence) {
		    if (seq == Job) {
		        return true;
		    }
		}
		return false;
	}
}
