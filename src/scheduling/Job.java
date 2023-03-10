package scheduling;

/**
 * This class represents a job (on a machine) in the single machine scheduling problem.
 * @author Florian Korn, Andre Konersmann
 *
 */
public class Job {
	
	/**
	 * The name of the job.
	 */
	private String name;
	
	/**
	 * The production length.
	 */
	private int p;
	
	/**
	 * The release date - the job is available afterwards.
	 */
	private int r;
	
	/**
	 * The due date - the job becomes tardy afterwards.
	 */
	private int d;
	
	/**
	 * This saves if preemption is used in this job (true) or not (false)
	 */
	public boolean preemption;

	/**
	 * The full parameter constructor for a job.
	 * @param name of a job
	 * @param p the production length
	 * @param r the release date
	 * @param d the due date
	 */
	public Job(String name, int p, int r, int d) {
		this.name = name;
		this.p = p;
		this.r = r;
		this.d = d;
		this.preemption = false;
	}
	
	/**
	 * The getter for the name.
	 * @return the name of the job
	 */
	public String getName() {
		return name;
	}

	/**
	 * The getter for the production length.
	 * @return the production length
	 */
	public int getP() {
		return p;
	}

	/**
	 * The getter for the release date.
	 * @return the release date
	 */
	public int getR() {
		return r;
	}

	/**
	 * The getter for the due date.
	 * @return the due date
	 */
	public int getD() {
		return d;
	}
}