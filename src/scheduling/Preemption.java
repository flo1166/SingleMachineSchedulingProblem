package scheduling;

/**
 * This class saves empty slots in the schedule.
 * @author Florian Korn, André Konersmann
 *
 */
public class Preemption {
	
	/**
	 * This represents the empty slot in a schedule
	 */
	private int emptyCapacity;
	
	/**
	 * This is the start period where the empty slot starts
	 */
	private int startPeriod;
	
	/**
	 * This is a constructor, which automatically indicates the end period of the empty slot
	 * @param emptyCapacity is the slot which can be filled partially by a job
	 * @param startPeriod is the period when the slot is available
	 */
	public Preemption(int emptyCapacity, int startPeriod) {
		this.emptyCapacity = emptyCapacity;
		this.startPeriod = startPeriod;
	}

	/**
	 * The getter for the empty capacity
	 * @return the slot which can be filled partially by a job
	 */
	public int getEmptyCapacity() {
		return emptyCapacity;
	}
	
	/**
	 * The getter for the start period
	 * @return start period of the empty capacity
	 */
	public int getStartPeriod() {
		return startPeriod;
	}
}
