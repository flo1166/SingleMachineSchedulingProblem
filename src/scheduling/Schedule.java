package scheduling;

// this class represents a schedule in the single machine scheduling problem
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
	 * @return sequence with the end of the jobs
	 */
	public Preemption[] buildSchedule(Preemption[] sequence) {
		
		Preemption[] localSequence = sequence;
		int currentPeriod = sequence[0].getR();
		
		// variation of jobs
		for (int i = 0; i < sequence.length; i++) {
			
			// go through periods until job is exhausted
			while (localSequence[i].remainingP != 0) {
				
					// check if neighbor is released, if true then swapJobs, otherwise none
					if (i != localSequence.length - 1 && localSequence[i+1].getR() == currentPeriod) {
						localSequence = swapJobs(localSequence, localSequence[i], localSequence[i+1]);
					}
					// check if current job is released, if true then adjust remainingP, otherwise none
					if (localSequence[i].getR() <= currentPeriod) {
						localSequence[i].remainingP -= 1;
					}
					// count next period
					currentPeriod += 1;
				}	
			
			// set job end to current period
			localSequence[i].setJobEnd(currentPeriod);
		}
		return localSequence;
	}
	
	/**
	 * This method calculates the maximum lateness of a given sequence of jobs
	 * @param sequence of jobs
	 * @return maximum lateness in a sequence of jobs
	 */
	public int maxLateness(Preemption[] sequence) {
		
		buildSchedule(sequence);		
		int jobLateness = 0;
		int maxLateness = 0;
		
		for (int i = 0; i < sequence.length; i++) {
			jobLateness = sequence[i].getJobEnd() - sequence[i].getD();
			if (jobLateness > maxLateness) {
				maxLateness = jobLateness;
			}
		}
		printSchedule(sequence);
		return maxLateness;
	}
	
	/**
	 * This method prints a schedule (with job name and job end)
	 * @param sequence of jobs
	 */
	public void printSchedule(Preemption[] sequence) {
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
