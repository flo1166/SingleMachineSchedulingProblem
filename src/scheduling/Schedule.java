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
	 * @param sequence of all jobs
	 * @param delayedJob is the job to be delayed
	 * @param swappedJob is the job to be swapped
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
	 */
	public static void buildSchedule(Preemption[] EDDsequence, Preemption[] sequence) {
		
		// reset jobs with new remaining p and empty job end
		resetPJobs(EDDsequence);
		
		// initial variables
		int	currentPeriod = 0;
		
		// if forced sequence, change current period and do jobWithoutPreemption
		if (sequence != null) {
			currentPeriod = sequence[0].getR();
			currentPeriod = jobWithoutPreemption(sequence, currentPeriod);
		} 
			
		// EDDsequence of jobs with preemption
		jobPreemption(EDDsequence, currentPeriod);
	}
	
	/**
	 * This method resets all jobs (no job end set, remaining p equals the p of the job 
	 * and preemption is set to false).
	 * @param sequence of jobs to be reseted
	 */
	public static void resetPJobs(Preemption[] sequence) {
		
		for (Preemption seq : sequence) {
			seq.setJobEnd(-1);
			seq.remainingP = seq.getP();
			seq.preemption = false;
		}
	}
	
	/**
	 * This method changes the p and job end in jobs without preemption
	 * @param sequence of jobs to be changed (only forced sequence is allowed without preemption)
	 * @param currentPeriod of the calculation
	 * @return the current period for further calculations
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
	 */
	public static void jobPreemption(Preemption[] sequence, int currentPeriod) {
			
		for (int i = 0; i < sequence.length; i++) {
			// go through periods until job is exhausted
			while (sequence[i].remainingP != 0) {
				// check if a new job is released
				for (int k = i; k < sequence.length; k++) {					
					if (sequence[k].getR() == currentPeriod) {
						// check if first job in sequence is not released
						if (sequence[i].getR() > currentPeriod){
							sequence = swapJobs(sequence, sequence[i], sequence[k]);
						// if first job in sequence is released and the due date of the job at k is lower than of the job in i, swap jobs
						} else if (sequence[i].getR() <= currentPeriod && sequence[k].getD() < sequence[i].getD()) {
							sequence = swapJobs(sequence, sequence[i], sequence[k]);							
						} 						
					}
				}
				// changes to the object if it is released
				if (sequence[i].getR() <= currentPeriod) {
					sequence[i].remainingP -= 1;
					currentPeriod += 1;
					// set job end to current period (if dummy "-1" is set, otherwise job end is already set)
					if (sequence[i].remainingP == 0 && sequence[i].getJobEnd() == -1) {
							sequence[i].setJobEnd(currentPeriod);
					}
					// otherwise swap job with neighbor
				} else if (i != sequence.length - 1){
					sequence = swapJobs(sequence, sequence[i], sequence[i+1]);	
				} else {
					currentPeriod += 1;				
				}
			}
		}
	}

	/**
	 * This method calculates the maximum lateness of a given sequence of jobs
	 * @param EDDsequence of all jobs sorted with EDD logic
	 * @param sequence of jobs (until current node)
	 * @return maximum lateness in a sequence of jobs
	 */
	public static int maxLateness(Preemption[] EDDsequence, Preemption[] sequence) {
		
		buildSchedule(EDDsequence, sequence);		
		int jobLateness = 0;
		int maxLateness = 0;
		
		for (int i = 0; i < EDDsequence.length; i++) {
			jobLateness = EDDsequence[i].getJobEnd() - EDDsequence[i].getD();
			if (jobLateness > maxLateness) {
				maxLateness = jobLateness;
			}
		}
		printSchedule(EDDsequence, sequence, maxLateness);

		return maxLateness;
	}
	
	/**
	 * This sorts a sequence of jobs via selection sort.
	 * @param sequence of jobs
	 * @param ascending if true, then ascending otherwise descending
	 * @return a sorted EDD sequence
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
	 * @param EDDsequence is the sorted sequence with EDD logic
	 * @param sequence is the forced sequence
	 * @param maxLateness is the max lateness of the current node schedule
	 */
	 
	public static void printSchedule(Preemption[] EDDsequence, Preemption[] sequence, int maxLateness) {
		
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
