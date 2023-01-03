package branchandbound;

import scheduling.Preemption;
import scheduling.Schedule;

/**
 * This class solves a branch and bound problem.
 * @author Florian Korn, Andre Konersmann
 *
 */
public class BranchAndBound {
	
	/**
	 * This is a sequence of nodes that inhabits the tree structure of a branch and bound problem
	 */
	public Node[] nodes;
	
	/**
	 * This is a super constructor.
	 * @param sequence of jobs
	 */
	public BranchAndBound() {
		this.nodes = null;
	}
	
	/**
	 * This method manages the nodes for a branch and bound tree. First it copies
	 * the old nodes and then adds the new node.
	 * @param job which represents a node in a tree structure of a branch and bound problem
	 * @param parentJob is the job with a higher hierarchy
	 */
	public void setNodes(Preemption job, Preemption parentJob) {
		
		Node[] oldNodes = nodes;
		Node[] currentNodes;
		
		// if nodes is null then Node array with size 1, otherwise size old array + 1
		if (nodes == null) {
			currentNodes = new Node[1];
		} else {
			currentNodes = new Node[oldNodes.length + 1];
		}
		
		// copy old array or if exhausted, then set new node object
		for (int i = 0; i < currentNodes.length; i++) {
			if (currentNodes.length != 1 && i < oldNodes.length) {
				currentNodes[i] = oldNodes[i];
			} else {
				currentNodes[i] = new Node(job, parentJob);
			}
		}
		
		// save new nodes
		this.nodes = currentNodes;
	}
	
	/**
	 * This method calculates the lower bound of a branch and bound problem (of a sequence of jobs)
	 * @return maximum lateness of a selection sorted sequence of jobs
	 */
	public void rootProblem(Preemption[] sequence) {
		
		Preemption[] EDDSequence = Schedule.selectionSortEDD(sequence, true);

		// set node root problem ****
		setNodes(null, null);
		nodes[nodes.length - 1].setMaxLateness(Schedule.maxLateness(EDDSequence, null, true));
		
		// first variation
		buildDepth(sequence, nodes[nodes.length - 1]);
	}
	
	/**
	 * This method searches all jobs in a given branch and bound tree structure.
	 * @param node is the pointer where we are in the tree structure
	 * @return sequence with all jobs in branch and bound tree structure
	 */
	public Preemption[] getNodeJobs(Node node) {
		
		// set first job in sequence
		Preemption[] currentSequence = new Preemption[1];
		currentSequence[0] = node.getJob();
		
		if (node.getParentJob() != null) {
			// set current node to compare in while loop
			Node currentNode = node;
			
			// if node has a parent job copy all parents into sequence	
			Preemption[] currentSequence2 = null;
			// copy jobs into sequence, as long as current node has parent jobs
			while (currentNode.getParentJob() != null) {
				// copy old sequence
				currentSequence2 = new Preemption[currentSequence.length + 1];
				for (int i = 0; i < currentSequence.length; i++) {
					currentSequence2[i] = currentSequence[i];
				}
				
				// search for parent node as new current node
				for (int j = 0; j < nodes.length; j++) {
					if (nodes[j].getJob() == currentNode.getParentJob()) {
						currentNode = nodes[j];
						j = nodes.length;
					}
				}
				// copy new job
				currentSequence2[currentSequence2.length - 1] = currentNode.getJob();
				currentSequence = currentSequence2;
			}
			
			// change order
			currentSequence2 = sortJobs(currentSequence2);
			currentSequence = currentSequence2;
		}
		
		return currentSequence;
	}
	
	/**
	 * This method sorts an array that the order is switched backwards (last entry is first entry ...)
	 * @param sequence to sort in switched order
	 * @return switched order sequence
	 */
	public Preemption[] sortJobs(Preemption[] sequence) {
			
		Preemption[] currentSequence = new Preemption[sequence.length];
		int indexSeq = sequence.length - 1;
		
		for (int i = 0; i < sequence.length; i++) {
			currentSequence[i] = sequence[indexSeq];
			indexSeq -= 1;	
		}
		
		return currentSequence;
	}
	
	/**
	 * This method generates our sequence to calculate the maximum lateness until a given node
	 * @param node is the node we want to look at
	 * @param sequence is a job sequence with all jobs
	 * @return a sequence of jobs with forced order until depth and after that the EDD logic
	 */
	public int nodeLateness(Node node, Preemption[] sequence) {
		
		// declare EDD
		Preemption[] EDDSequence = sequence;
		EDDSequence = Schedule.selectionSortEDD(sequence, true); 
		
		// declare max lateness
		int maxLateness = 0;
		
		// build sequence for calculation of maximum lateness of all jobs
		Preemption[] currentSequence = getNodeJobs(node);
		
		// print
		System.out.println("The current sequence from the tree for " + currentSequence[currentSequence.length - 1].getName());
		for (Preemption job : currentSequence) {
			System.out.print(job.getName() + ", ");
		}
		System.out.println();
		
//		// build full sequence with EDD (if current sequence is smaller than the length of all jobs
//		for (int i = 0; i < currentSequence.length; i++) {
//			while (currentSequence[i] != EDDSequence[i]) {
//				for (int j = 0; j < EDDSequence.length; j++) {
//					if (EDDSequence[j] == currentSequence[i]) {
//						EDDSequence = Schedule.swapJobs(EDDSequence, EDDSequence[j - 1], EDDSequence[j]);
//						j = EDDSequence.length;
//					}
//				}
//			}
//		}
//		currentSequence = EDDSequence;
//		for (Preemption curr : currentSequence) {
//			System.out.println("new current: " + curr.getName());
//		}
		
		// calculate maxLateness
		maxLateness = Schedule.maxLateness(EDDSequence, currentSequence, false);
		
		return maxLateness;
	}
	
	/**
	 * This method sets a node and calculates the node max lateness
	 * @param job is the node
	 * @param parentJob is the job in a higher hierarchy than the job
	 * @param sequence of all available jobs
	 */
	public int calcNodeMaxLateness(Preemption job, Preemption parentJob, Preemption[] sequence) {
		
		// set node
		setNodes(job, parentJob);
		// set maximum lateness in node
		nodes[nodes.length - 1].setMaxLateness(nodeLateness(nodes[nodes.length - 1], sequence));
		
		return nodes[nodes.length - 1].getMaxLateness();
	}
	
	/**
	 * This method compares the upper bound and establishes a new depth for the node with most potential
	 * @param sequence of all jobs
	 */
	public void branching(Preemption[] sequence) {
		
		int upperBound = 1;
		
		for (int i = 1; i < nodes.length; i++) {
			if (nodes[upperBound].getMaxLateness() > nodes[i].getMaxLateness()) {
				upperBound = i;
			}
		}
		System.out.println("Upper bound: " + nodes[upperBound].getJob().getName());
		System.out.println();
		buildDepth(sequence, nodes[upperBound]);
	}
	
	/**
	 * This method establishes another depth level into the branch and bound tree
	 * @param sequence of all jobs
	 * @param parentJob that is set in the new node
	 */
	public void buildDepth(Preemption[] sequence, Node job) {
		
		Preemption[] usedSequence = getNodeJobs(job);
		
		for (int i = 0; i < sequence.length; i++) {
			for (int j = 0; j < usedSequence.length; j++) {
				if(usedSequence[j] == sequence[i]) {
					j = usedSequence.length;
				} else {
					calcNodeMaxLateness(sequence[i], job.getJob(), sequence);
				}
			}
		}
	}
}
