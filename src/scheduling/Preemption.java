package scheduling;

/**
 * This class extends the job class to implement preemption of jobs.
 * @author Florian Korn, Andre Konersmann
 *
 */
public class Preemption extends Job {
	
	/**
	 * The end of the job.
	 */
	private int jobEnd;
	
	/**
	 * The remaining processing time of a job.
	 */
	public int remainingP;

	/**
	 * This is a super class constructor.
	 * @param string the name of a job
	 * @param p the production length
	 * @param r the release date
	 * @param d the due date
	 */
	public Preemption(String name, int p, int r, int d) {
		super(name, p, r, d);
		this.remainingP = p;
		this.jobEnd = -1;
	}
	
	/**
	 * The setter for the end of the job
	 * @param jobEnd, when the job is finished
	 */
	public void setJobEnd(int jobEnd) {
		this.jobEnd = jobEnd;
	}
	
	/**
	 * The getter for the job.
	 * @return when the job is finished
	 */
	public int getJobEnd() {
		return jobEnd;
	}


	
	

}
