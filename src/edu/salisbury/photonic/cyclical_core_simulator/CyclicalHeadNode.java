package edu.salisbury.photonic.cyclical_core_simulator;

import java.util.LinkedList;

import edu.salisbury.photonic.core_simulator.CoreNode;
import edu.salisbury.photonic.core_simulator.CoreNodeIOPort;

/**
 * Headnode that can be used for an architecture that extends the abstract 
 * {@link CyclicalArchitecture} class
 * @author timfoil
 *
 */
public class CyclicalHeadNode extends CoreNode
{
	private CyclicalArchitecture underlyingArchitecture;
	
	/**Task requests to the headNode that have been received this cycle*/
	public LinkedList<CyclicalRoutingTask> newlyReceivedTasks;
	
	/**Task requests to the headNode that have been received in a previous cycle*/
	public LinkedList<CyclicalRoutingTask> previouslySentTasks;
	
	/**Tasks that are being currently executing in the overlying architecture*/
	public LinkedList<CyclicalRoutingTask> currentlyExecutingTasks;
	
	/**
	 * Constructor for the CyclicalHeadNode.
	 * @param architecture The overlying architecture of which the headNode is presiding over
	 */
	public CyclicalHeadNode(CyclicalArchitecture architecture)
	{
		this.underlyingArchitecture = architecture;
		this.edges = new CoreNodeIOPort[architecture.numberOfCoreNodes()];
		this.newlyReceivedTasks = new LinkedList<CyclicalRoutingTask>();
		this.previouslySentTasks = new LinkedList<CyclicalRoutingTask>();
		this.currentlyExecutingTasks = new LinkedList<CyclicalRoutingTask>();
	}
	
	/**
	 * Adds the CyclicalTask request to the queue to be approved.
	 * @param request the request which will be approved later
	 */
	public void addRequestForTask(CyclicalTask request)
	{
		newlyReceivedTasks.add(new CyclicalRoutingTask(request));
	}
	
	/**
	 * Sets all of the connecting edges to the HeadNode
	 * @param connectedNodes the coreNodes which this CyclicalHeadNodeConnectsTo
	 */
	public void setEdges(CoreNode[] connectedNodes)
	{
		edges = new CoreNodeIOPort[connectedNodes.length];
		
		for(int i = 0; i < connectedNodes.length; i++)
		{
			edges[i] = new CoreNodeIOPort(connectedNodes[i]);
		}
	}
	

	@Override
	public void simulateCycle()
	{
		//teardown previous connections
		for(CoreNodeIOPort edge:edges)
		{
			edge.simulateCycle();
		}
		
		// simulate cycle for all edges
		for(int i = 0; i < currentlyExecutingTasks.size(); i++)
		{
			CyclicalRoutingTask task = currentlyExecutingTasks.get(i);
			task.simulateCycle();
			if(task.finished)
			{
				currentlyExecutingTasks.remove(i);
				//need to decrement i because elements above the removed element are shifted left
				i--;
			}
		}
		for(int i = 0; i < previouslySentTasks.size(); i++)
		{
			if(attemptToAdd(previouslySentTasks.get(i)))
			{
				CyclicalRoutingTask newTask = previouslySentTasks.remove(i);
				newTask.setTimeLeftForTask(determineTimeLeft(newTask));
					CoreNodeIOPort taskEdge = 
							edges[newTask.getSourceNodeNumber()];
				approveTask(taskEdge, newTask.getDirection());	
				i--;
			}
		}
		previouslySentTasks.addAll(newlyReceivedTasks);
		newlyReceivedTasks = new LinkedList<CyclicalRoutingTask>();
	}

	private void approveTask(CoreNodeIOPort taskEdge, CyclicalDirection direction)
	{
		CyclicalNode sourceNode = (CyclicalNode) taskEdge.getLink();
		sourceNode.approveWaitingTask(direction);
		taskEdge.teardownConnection();
	}

	private int determineTimeLeft(CyclicalRoutingTask task)
	{
		int timeLeft = task.getFlitsToSend() * underlyingArchitecture.getBitsPerFlit();
		timeLeft += underlyingArchitecture.getTeardownTime();
		return timeLeft;
	}
	private boolean attemptToAdd(CyclicalRoutingTask possibleTask) 
	{
		ModulusRange clockwiseRangeToAdd = new ModulusRange(possibleTask.getSourceNodeNumber(),
				possibleTask.getDestinationNodeNumber(), true);
		
		ModulusRange counterClockwiseRangeToAdd = new ModulusRange(clockwiseRangeToAdd, false);
		
		boolean cWRangeConflicts = false;
		boolean cCWRangeConflicts = false;
		
		//check if a clockwise or counterclockwise path is available against currently
		//executingTask paths
		for(CyclicalRoutingTask runningTask:currentlyExecutingTasks)
		{
			ModulusRange comparisonRange = new ModulusRange(runningTask);
					
			if(!cWRangeConflicts && 
					clockwiseRangeToAdd.modulusRangesDoConflict(comparisonRange, edges.length)) 
			{
					cWRangeConflicts = true;
			}
			if(!cCWRangeConflicts && 
					counterClockwiseRangeToAdd.modulusRangesDoConflict(
							comparisonRange, edges.length))
			{
				cCWRangeConflicts = true;
			}
			if(cWRangeConflicts && cCWRangeConflicts) 
				return false;
		}
		
		//determine which way to take
		currentlyExecutingTasks.add(possibleTask);
		
		//If one path is explicitly not available take the opposing path
		if(cCWRangeConflicts)
		{
			//set clockwise
			possibleTask.setDirection(CyclicalDirection.CLOCKWISE);
		} 
		else if(cWRangeConflicts)
		{
			//set counterclockwise
			possibleTask.setDirection(CyclicalDirection.COUNTERCLOCKWISE);
		}
		
		//if both paths are available take the one with the smaller distance between the source and
		//the destination nodes (if equi-distant take clockwise path).
		else if(clockwiseRangeToAdd.getLast() > clockwiseRangeToAdd.getFirst())
		{
			int cWDistance = clockwiseRangeToAdd.getLast() - clockwiseRangeToAdd.getFirst();
			if(edges.length - cWDistance >= cWDistance)
			{
				 possibleTask.setDirection(CyclicalDirection.CLOCKWISE); //set clockwise
			} 
			else 
			{
				possibleTask.setDirection(CyclicalDirection.COUNTERCLOCKWISE); //set counterclockwise
			}
		}
		else
		{
			int cCWDistance = clockwiseRangeToAdd.getFirst() - clockwiseRangeToAdd.getLast();
			if(edges.length - cCWDistance > cCWDistance)
			{
				 possibleTask.setDirection(CyclicalDirection.COUNTERCLOCKWISE); //setClockwise
			} 
			else 
			{
				possibleTask.setDirection(CyclicalDirection.CLOCKWISE); //set counterclockwise
			}
		}
		return true;
	}
	
	/**
	 * A way of modeling the ongoing connections such that the {@link CyclicalHeadNode} knows which 
	 * nodes are being utilized at the same time
	 * @author timfoil
	 */
	public static class ModulusRange
	{
		private int first;
		private int last;
		private boolean clockwise;

		/**
		 * 
		 * @param 	range A modulusRange object which the constructed Range will inherit its first 
		 * 			and last integers.
		 * @param 	clockwise <code>true</code> if the range travels clockwise from the source 
		 * 			to destination; false otherwise. 
		 */
		public ModulusRange(ModulusRange range, boolean clockwise)
		{
			this.first = range.first;
			this.last = range.last;
			this.clockwise = clockwise;
		}
		/**
		 * 
		 * @param 	first the source of the range
		 * @param 	last the destination of the range
		 * @param 	clockwise <code>true</code> if the range travels clockwise from the source 
		 * 			to destination; false otherwise.
		 */
		public ModulusRange(int first, int last, boolean clockwise)
		{
			this.first = first;
			this.last = last;
			this.clockwise = clockwise;
		}
		/**
		 * Constructor for a modulus range
		 * @param range The task to create a modulusRangeFrom 
		 */
		public ModulusRange(CyclicalRoutingTask range)
		{
			//creates a clockwise Range
			int source = range.getSourceNodeNumber();
			int dest = range.getDestinationNodeNumber();
			this.clockwise = true;
			
			switch(range.getDirection())
			{
				case CLOCKWISE:
					first = source;
					last = dest;
					break;
				case COUNTERCLOCKWISE:
					first = dest;
					last = source;
					break;
				default:
					throw new IllegalArgumentException("BasicRoutingTask's range must have " +
							"either a clockwise or counter clockwise direction");
			}
		}
		
		/**
		 * Compares two modulus ranges to each other using a shared modulus.
		 * 
		 * @param 	secondRange the secondRange to compare this one to, to see if they conflict in 
		 * 			range
		 * @param 	modulus The number that the range loops around at.
		 * @return 	<code>true</code>
		 * 			<code>false</code>
		 */
		public boolean modulusRangesDoConflict(ModulusRange secondRange, int modulus)
		{
			return clockwiseRangesConflict(
					convertRangeToClockwiseRanges(this, modulus), 
					convertRangeToClockwiseRanges(secondRange, modulus));	
		}
		
		private static ModulusRange[] convertRangeToClockwiseRanges(
				ModulusRange toConvert, int modulus)
		{
			if(toConvert.clockwise)
			{
				if(toConvert.first <= toConvert.last)
				{
					return new ModulusRange[] {toConvert};
				} 
				else 
				{
					return new ModulusRange[] { new ModulusRange(toConvert.first, modulus, true), 
							new ModulusRange(0, toConvert.last, true)};
				}
			}
			else 
			{
				if(toConvert.first < toConvert.last)
				{
					return new ModulusRange[] { new ModulusRange(toConvert.last, modulus, true), 
							new ModulusRange(0, toConvert.first, true)};
				} 
				else 
				{ 
					return new ModulusRange[] {
							new ModulusRange(toConvert.last,toConvert.first, true)};
				}
			}
		}
		
		private boolean clockwiseRangesConflict(ModulusRange[] range1, ModulusRange[] range2)
		{
			for(int i = 0; i < range1.length; i++)
			{
				for (int j = 0; j < range2.length; j++)
				{
					if (clockwiseRangesConflict(range1[i], range2[j])) 
					{
						return true;
					}
				}
			}
			return false;
		}
		
		private boolean clockwiseRangesConflict(ModulusRange range1, ModulusRange range2)
		{
			if(!range1.clockwise || !range2.clockwise) 
				throw new IllegalArgumentException("Both ranges must be clockwise.");
			
			//ranges conflict
			if( (range1.last > range2.first && range1.last < range2.last) ||
					(range1.first > range2.first && range1.first > range2.last) || 
					(range2.last > range1.first && range2.last < range1.last))
			{
				return true;
			}
			return false; //ranges do not conflict
		}

		/**
		 * @return the first in range
		 */
		public int getFirst()
		{
			return first;
		}

		/**
		 * @return the last in range
		 */
		public int getLast()
		{
			return last;
		}
	}
	/**
	 *  Returns true if all tasks in the architecture have been finished
	 * @return 	<code>true</code> if all tasks in the overlying architecture have been completed;
	 * 			<code>false</code> otherwise.
	 */
	public boolean allTasksFinished()
	{
		return currentlyExecutingTasks.isEmpty() && 
				previouslySentTasks.isEmpty() && 
				newlyReceivedTasks.isEmpty();
	}
}
