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
	 * The parent of the parent job;
	 */
	private Preemption parentParentJob;
	
	/**
	 * The maximum lateness of the sequence, specified sequence until this tree node (job) other jobs as EDD.
	 */
	private int maxLateness;
	
	/**
	 * This is a full parameter constructor.
	 * @param job is the current job
	 * @param parentNode is the parent of the current job in a higher hierarchy
	 * @param parentParentJob is the parent of the parent of the current job in a higher higher hierarchy
	 */
	public Node(Preemption job, Preemption parentJob, Preemption parentParentJob) {
		
		this.job = job;
		this.parentJob = parentJob;
		this.maxLateness = -1;
		this.parentParentJob = parentParentJob;
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
	 * The getter for the parent of the parent job.
	 * @return the parent of the parent (to identify right node)
	 */
	public Preemption getParentParentJob() {
		return parentParentJob;
	}
	
	/**
	 * The setter for maximum lateness.
	 * @param MaxLateness 
	 */
	public void setMaxLateness(int MaxLateness) {
		this.maxLateness = MaxLateness;
	}	
}
