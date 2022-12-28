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
		
		// the current maximum
		int maxLateness = 0;
		
		for (int i = 0; i < sequence.length; i++) {
			// add the job's tardiness
			allLateness[i] = sequence[i].getLateness(currentPeriod);
			// change the maximum lateness
			if (maxLateness < allLateness[i]) {
				maxLateness = allLateness[i];
			}
			// setting the current period
			currentPeriod = sequence[i].getCompletionDate(currentPeriod);
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
}
