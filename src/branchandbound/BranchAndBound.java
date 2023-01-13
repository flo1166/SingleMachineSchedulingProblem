package branchandbound;

import scheduling.Preemption;
import scheduling.Schedule;

/**
 * This class solves a branch and bound problem and represents a branch and bound with nodes.
 * @author Florian Korn, Andre Konersmann
 *
 */
public class BranchAndBound {
	
	/**
	 * This is a sequence of nodes that inhabits the tree structure of a branch and bound problem
	 */
	public Node[] nodes;
	
	/**
	 * This variable tells us until which node we solved the problem
	 */
	public int depthLevel = 1;
	
	/**
	 * This is a super constructor.
	 * @param sequence of jobs
	 */
	public BranchAndBound() {
		this.nodes = null;
	}
	
	/**
	 * This method manages the nodes for a branch and bound tree. First it copies
	 * the old nodes and then adds the new node. The parent of the parent job
	 * is needed to identify the previous node in a backward search when starting
	 * at a random node in the tree.
	 * @param job which represents a node in a tree structure of a branch and bound problem
	 * @param parentJob is the job with a higher hierarchy
	 * @param parentParentJob is the parent of the parent job
	 */
	public void setNodes(Preemption job, Preemption parentJob, Preemption parentParentJob) {
		
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
				currentNodes[i] = new Node(job, parentJob, parentParentJob);
			}
		}
		
		// save new nodes
		this.nodes = currentNodes;
	}
	
	/**
	 * This method initiates the root problem. It sets a empty node and caluclates the maximum lateness of it.
	 * Afterwards the first depth of the tree is set and calculated
	 * @param sequence of all jobs
	 */
	public void rootProblem(Preemption[] sequence) {
		
		Preemption[] EDDSequence = Schedule.selectionSortEDD(sequence, true);

		// set node root problem ****
		setNodes(null, null, null);
		nodes[nodes.length - 1].setMaxLateness(Schedule.maxLateness(EDDSequence, null));
		
		// first depth
		buildDepth(sequence, nodes[nodes.length - 1]);
	}
	
	/**
	 * This method searches all jobs in a given branch and bound tree structure until
	 * the given node.
	 * @param node is the pointer where we are in the tree structure
	 * @return sequence with jobs (including the current node and their parents)
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
					if (nodes[j].getJob() == currentNode.getParentJob() && nodes[j].getParentJob() == currentNode.getParentParentJob()) {
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
	 * This method sets a node and calculates the node max lateness
	 * @param job is the node
	 * @param parentJob is the job in a higher hierarchy than the job
	 * @param parentParentJob is the parent of the parent job (in a higher higher hierarchy)
	 * @param sequence of all available jobs
	 */
	public void calcNodeMaxLateness(Preemption job, Preemption parentJob, Preemption parentParentJob, Preemption[] sequence) {
		
		// declare EDD
		Preemption[] EDDSequence = sequence;
		EDDSequence = Schedule.selectionSortEDD(sequence, true); 
		
		// declare max lateness
		int maxLateness = 0;
		
		// set node
		setNodes(job, parentJob, parentParentJob);
		
		// build sequence for calculation of maximum lateness of all jobs
		Preemption[] currentSequence = getNodeJobs(nodes[nodes.length - 1]);
		
		// calculate maxLateness
		maxLateness = Schedule.maxLateness(EDDSequence, currentSequence);
		
		// set maximum lateness in node
		nodes[nodes.length - 1].setMaxLateness(maxLateness);
	}
	
	/**
	 * This method compares the upper bound and establishes a new depth for the node with most potential
	 * @param sequence of all jobs
	 * @return true if it has preemption, otherwise false
	 */
	public boolean branching(Preemption[] sequence) {
		
		int upperBound = depthLevel;
		
		// search for lowest lateness
		for (int i = depthLevel; i < nodes.length; i++) {
			if (nodes[upperBound].getMaxLateness() > nodes[i].getMaxLateness()) {
				upperBound = i;
			}
		}
		depthLevel += 1; // manipulation for one upper bound chosen
		System.out.println("The upper bound is " + nodes[upperBound].getJob().getName() + " with max lateness: " + nodes[upperBound].getMaxLateness());
		System.out.println();
		return buildDepth(sequence, nodes[upperBound]);
	}
	
	/**
	 * This method establishes another depth level into the branch and bound tree
	 * @param sequence of all jobs
	 * @param job that is set in the new node
	 * @return true if preemption is used, false if not
	 */
	public boolean buildDepth(Preemption[] sequence, Node job) {
		
		Preemption[] usedSequence = getNodeJobs(job);
		boolean preemption = false;
			
		for (int i = 0; i < sequence.length; i++) {
			if (!Schedule.checkArrayElement(usedSequence, sequence[i])) {
				calcNodeMaxLateness(sequence[i], job.getJob(), job.getParentJob(), sequence);
				if (job.getJob() != null) {
					depthLevel += 1;
				}
				if (preemption == false && sequence[i].preemption == true) {
					preemption = true;
				}
			}
		}
		return preemption;
	}
	
	/**
	 * This method checks if perhaps there is a better solution than our optimal solution
	 * @param sequence of all jobs
	 * @return true if there is a better solution, false if not
	 */
	public boolean checkOptimalSolution(Preemption[] sequence) {
		
		// search where node with minimal maximum lateness is (in nodes)
		int optimalIndex = 0;
		for (int k = 0; k < sequence.length - 1; k++) {
			if (nodes[nodes.length - k - 1].getMaxLateness() < nodes[sequence.length - optimalIndex - 1].getMaxLateness()) {
				optimalIndex = k;
			}
		}
		
		// initialize variables
		Node[] currentNodes = nodes;
		Preemption[] usedSequence = getNodeJobs(nodes[nodes.length - optimalIndex]);
		Node optimalSequence = nodes[nodes.length - 1];
		nodes = new Node[sequence.length];
		
		// copy nodes, except the already branched one
		int j = 0;
		for (int i = 0; i < sequence.length + 1; i++) {
			if (currentNodes[i] == null || currentNodes[i].getJob() != usedSequence[0]) {
				nodes[j] = currentNodes[i];
				j += 1;
			}
		}
		System.out.print("Checked optimal solution: ");
		for (Preemption seq : usedSequence) {
			System.out.print(seq.getName() + ", ");
		}
		System.out.println();
		
		// check for optimal solution
		for (int k = 1; k < nodes.length; k++) {
			if (optimalSequence.getMaxLateness() > nodes[k].getMaxLateness()) {
				System.out.println("More promising solution found.");
				System.out.println("Continue with branching: " + nodes[k].getJob().getName());
				return true;
			}
		}
		System.out.print("Best solution is: ");
		for (Preemption seq : usedSequence) {
			System.out.print(seq.getName() + ", ");
		}
		System.out.println();
		return false;
	}
}
