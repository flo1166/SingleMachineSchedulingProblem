package branchandbound;

import scheduling.Preemption;

/** This class represents a node in a branch and bound problem
 * 
 * @author Florian Korn, Andre Konersmann
 *
 */
public class Node {

	/**
	 * The job as child from the parentJob.
	 */
	private Preemption job;
	
	/**
	 * The parent of the current job.
	 */
	private Preemption parentJob;
	
	/**
	 * The maximum lateness of the sequence, specified sequence until this tree node (job) other jobs as EDD.
	 */
	private int maxLateness;
	
	/**
	 * This is a parameter constructor.
	 * @param parentNode is the job in a higher hierarchy of the branch and bound problem
	 */
	public Node(Preemption job, Preemption parentJob) {
		
		this.job = job;
		this.parentJob = parentJob;
		this.maxLateness = -1;
	}
	
	/**
	 * The getter of the job.
	 * @return job
	 */
	public Preemption getJob() {
		return job;
	}
	
	/**
	 * The getter for the parent node (job in higher hierarchy).
	 * @return job in higher hierarchy
	 */
	public Preemption getParentJob() {
		return parentJob;
	}
	
	/**
	 * The getter for the maximum lateness.
	 * @return the maximum lateness until this job specified sequence, after that EDD.
	 */
	public int getMaxLateness() {
		return maxLateness;
	}
	
	/**
	 * The setter for maximum lateness.
	 */
	public void setMaxLateness(int MaxLateness) {
		this.maxLateness = MaxLateness;
	}
}
