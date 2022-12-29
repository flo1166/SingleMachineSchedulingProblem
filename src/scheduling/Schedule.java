package scheduling;

// this class represents a schedule in the single machine scheduling problem
public class Schedule {
	
	// the sequence of jobs in the schedule
	private Job[] sequence;
	
	// full parameter constructor
	public Schedule(Job[] sequence) {
		this.sequence = sequence;
	}
	
	public int getMaxLateness() {
		// sum variable
		int[] allLateness = new int[sequence.length];
		
		// the current period
		int currentPeriod = 0;
		
		// the previous period
		int previousPeriod = 0;
		
		// the current maximum
		int maxLateness = 0;
		
		// the job preemption correction
		Preemption[] preemptions = null;
		
		for (int i = 0; i < sequence.length; i++) {
			// add the job's tardiness
			allLateness[i] = sequence[i].getLateness(currentPeriod);
			// change the maximum lateness
			if (maxLateness < allLateness[i]) {
				maxLateness = allLateness[i];
			}
			
			// set previous period
			previousPeriod = currentPeriod;
			
			// set current period
			currentPeriod = sequence[i].getCompletionDate(currentPeriod);
			
			System.out.println(sequence[i].getName());
			// job preemption from previous period
			System.out.print(" if preemption is filled ");
			System.out.println(preemptions != null);
			if (preemptions != null) {
				int j = 0;
				for (Preemption prem : preemptions) {	
					System.out.print("compare start with release date: ");
					System.out.println(prem.getStartPeriod() >= sequence[i].getR());
					if (prem.getStartPeriod() >= sequence[i].getR()) {
						currentPeriod -= prem.getEmptyCapacity();
						preemptions = deletePreemption(preemptions, j);
						j += 1;
						System.out.println(prem.getEmptyCapacity());
					}
				}
			}
			System.out.println("current period " + currentPeriod);
			
			// setting new preemption
			System.out.print("previous period " + previousPeriod + " new preemption ");
			System.out.println(previousPeriod < sequence[i].getR());
			if (previousPeriod < sequence[i].getR()) {
				preemptions = addPreemption(preemptions, previousPeriod, sequence[i].getPreemption(previousPeriod));
			}
		}
		return maxLateness;
	}
	
	// get all the neighbors
	public Schedule[] getNeighbors() {
		Schedule[] neighbors = new Schedule[sequence.length - 1];
		
		for (int i = 0; i < neighbors.length; i++) {
			neighbors[i] = new Schedule(swapJobs(i));
			System.out.println(neighbors[i]);
		}
		
		return neighbors;
	}
	
	// creates a new sequence of jobs by swapping job k and k+1
	private Job[] swapJobs(int k) {
		Job[] newSequence = new Job[sequence.length];
		
		for (int i = 0; i < sequence.length; i++) {
			if (i == k) {
				newSequence[i] = sequence[k+1];
			} else if (i == k+1) {
				newSequence[i] = sequence[k];
			} else {
				newSequence[i] = sequence[i];
			}
		}
		
		return newSequence;
	}	
	
	
	public Preemption[] deletePreemption(Preemption[] preemptions, int index) {
		
		// initial variables
		Preemption[] newPreemptions = new Preemption[preemptions.length - 1];
		int j = 0;
		
		if (preemptions != null && preemptions.length != 1) {
			// copy array
			for (int i = 0; i < preemptions.length; i++) {
				if (i == index) {
				} else {
					newPreemptions[j] = preemptions[i];
					j += 1;
				}
			}
		} else {
			newPreemptions = null;
		}
		return newPreemptions;
	}
	
	/**
	 * This method adds a preemption to a given array
	 * @param preemptions the allready saved preemptions
	 * @param startPeriod of the new preemption
	 * @param emptyCapacity of the new preemption
	 * @return array with old and new preemtpion
	 */
	public Preemption[] addPreemption(Preemption[] preemptions, int startPeriod, int emptyCapacity) {
		
		// initial variables
		int size = 0;
		
		if (preemptions != null) {
			size = preemptions.length;
		}
		
		Preemption[] newPreemptions = new Preemption[size + 1];
		
		// copy array
		if (preemptions != null) {
			for (int i = 0; i < preemptions.length; i++) {
				newPreemptions[i] = preemptions[i];
			}
		} 
		newPreemptions[size] = new Preemption(emptyCapacity, startPeriod);

		return newPreemptions;
	}
}
