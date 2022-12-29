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
		
		for(int i = 0; i < sequence.length; i++) {
			
			// find min / max
			int minMax = i;
			
			for(int j = i + 1; j < sequence.length; j++) {
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
		
		Preemption[] currentSequence = selectionSortEDD(sequence, true);
		return maxLateness(currentSequence);
	}
}
