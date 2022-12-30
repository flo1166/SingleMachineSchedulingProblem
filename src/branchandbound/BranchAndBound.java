package branchandbound;

import scheduling.Preemption;
import scheduling.Schedule;

/**
 * This class solves a branch and bound problem.
 * @author Florian Korn, Andre Konersmann
 *
 */
public class BranchAndBound extends Schedule {
	
	/**
	 * This is a super constructor.
	 * @param sequence of jobs
	 */
	public BranchAndBound(Preemption[] sequence) {
		super(sequence);
	}

	/**
	 * This sorts a sequence of jobs via selection sort.
	 * @param sequence of jobs
	 * @param ascending if true, then ascending otherwise decending
	 */
	public Preemption[] selectionSortEDD(Preemption[] sequence, boolean ascending) {
		
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
	 * This method calculates the lower bound of a branch and bound problem (of a sequence of jobs)
	 * @return maximum lateness of a selection sorted sequence of jobs
	 */
	public int rootProblem() {
		
		Preemption[] EDDSequence = selectionSortEDD(sequence, true);
		return maxLateness(EDDSequence, true);
	}
	
	public BranchAndBound[] BBLogic() {
		
		Preemption[] currentSequence = null;

		
		for (int i = 0; i < sequence.length; i++) {
			currentSequence = branching(i);

		}
	}
	
	/**
	 * This method generates our sequence to calculate the maximum lateness until a given depth
	 * @param depth of the branch and bound problem
	 * @return a sequence of jobs with forced order until depth and after that the EDD logic
	 */
	public Preemption[] branching(int depth) {
		
		Preemption[] EDDSequence = selectionSortEDD(sequence, true);
		Preemption[] currentSequence = sequence;
		int currentMaxLateness = -1;
		Preemption[] currentSolution = null;
		
		for (int i = 0; i < sequence.length; i++) {
			currentSequence[i + 1] = EDDSequence[i + 1];
			}
		
		currentMaxLateness = maxLateness(currentSequence, false);
		currentSolution = new Preemption[currentSequence.length + 1];
		
		// copy sequence until specified sequence
		for (int j = 0; j == depth; j++) {
				currentSolution[j] = currentSequence[j];
				}	
		// save max lateness
		currentSolution[currentSolution.length - 1] = currentMaxLateness;
		
		return currentSolution;
	}
}
