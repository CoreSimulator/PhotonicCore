package edu.salisbury.basic_core_simulator;

import java.util.LinkedList;

import edu.salisbury.basic_core_simulator.BasicSimOverseer.BasicArchitecture;
import edu.salisbury.core_simulator.CoreNode;
import edu.salisbury.core_simulator.CoreNodeIOPort;

public class BasicHeadNode extends CoreNode
{
	
	private int bitsPerFlit;
	private int teardownTime;
	
	public LinkedList<BasicRoutingTask> newlyReceivedTasks;
	public LinkedList<BasicRoutingTask> previouslySentTasks;
	public LinkedList<BasicRoutingTask> currentlyExecutingTasks;
	public int headNodeCycle; //TODO
	
	public BasicHeadNode(BasicArchitecture architecture, int numberOfCoreNodes, int bitsPerFlit, 
			int teardownTime)
	{
		this.underlyingArchitecture = architecture;
		this.teardownTime = teardownTime;
		this.edges = new CoreNodeIOPort[numberOfCoreNodes];
		this.bitsPerFlit = bitsPerFlit;
		this.newlyReceivedTasks = new LinkedList<BasicRoutingTask>();
		this.previouslySentTasks = new LinkedList<BasicRoutingTask>();
		this.currentlyExecutingTasks = new LinkedList<BasicRoutingTask>();
	}

	public void addRequestForTask(BasicTask request)
	{
		newlyReceivedTasks.add(new BasicRoutingTask(request));
	}
	
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
		headNodeCycle++; //TODO
		for(int i = 0; i < currentlyExecutingTasks.size(); i++)
		{
			BasicRoutingTask task = currentlyExecutingTasks.get(i);
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
					BasicRoutingTask newTask = previouslySentTasks.remove(i);
					newTask.setTimeLeftForTask(determineTimeLeft(newTask));
						CoreNodeIOPort taskEdge = 
								edges[underlyingArchitecture.coordinatesToNumber(
										newTask.getSourceNode())];
					approveTask(taskEdge, newTask.getDirection());	
					i--;
				}
			}
		previouslySentTasks.addAll(newlyReceivedTasks);
		newlyReceivedTasks = new LinkedList<BasicRoutingTask>();
	}

	private void approveTask(CoreNodeIOPort taskEdge, BasicDirection direction)
	{
		BasicNode sourceNode = (BasicNode) taskEdge.getLink();
		sourceNode.approveWaitingTask(direction);
		taskEdge.teardownConnection();
	}

	private int determineTimeLeft(BasicRoutingTask task)
	{
		int timeLeft = task.getFlitsToSend() * bitsPerFlit;
		timeLeft += teardownTime;
		return timeLeft;
	}

	//TODO clean up this terribly messy method
	private boolean attemptToAdd(BasicRoutingTask possibleTask) 
	{
		
		ModulusRange clockwiseRangeToAdd = new ModulusRange(
				underlyingArchitecture.coordinatesToNumber(possibleTask.getSourceNode()),
				underlyingArchitecture.coordinatesToNumber(
						possibleTask.getDestinationNode()));
		
		ModulusRange counterClockwiseRangeToAdd = new ModulusRange(clockwiseRangeToAdd, false);
		
		boolean cWRangeConflicts = false;
		boolean cCWRangeConflicts = false;
		
		for(BasicRoutingTask runningTask:currentlyExecutingTasks)
		{
			ModulusRange comparisonRange = new ModulusRange(runningTask, underlyingArchitecture);
					
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
		
		if(cCWRangeConflicts)
		{
			//set clockwise
			possibleTask.setDirection(BasicDirection.CLOCKWISE);
		} 
		else if(cWRangeConflicts)
		{
			//set counterclockwise
			possibleTask.setDirection(BasicDirection.COUNTERCLOCKWISE);
		}
		else if(clockwiseRangeToAdd.getLast() > clockwiseRangeToAdd.getFirst())
		{
			int cWDistance = clockwiseRangeToAdd.getLast() - clockwiseRangeToAdd.getFirst();
			if(edges.length - cWDistance >= cWDistance)
			{
				 possibleTask.setDirection(BasicDirection.CLOCKWISE); //set clockwise
			} 
			else 
			{
				possibleTask.setDirection(BasicDirection.COUNTERCLOCKWISE); //set counterclockwise
			}
		}
		else
		{
			int cCWDistance = clockwiseRangeToAdd.getFirst() - clockwiseRangeToAdd.getLast();
			if(edges.length - cCWDistance > cCWDistance)
			{
				 possibleTask.setDirection(BasicDirection.COUNTERCLOCKWISE); //setClockwise
			} 
			else 
			{
				possibleTask.setDirection(BasicDirection.CLOCKWISE); //set counterclockwise
			}
		}
		
		return true;
	}
	
	public static class ModulusRange
	{
		private int first;
		private int last;
		private boolean clockwise;

		
		public ModulusRange(ModulusRange range, boolean clockwise)
		{
			this.first = range.first;
			this.last = range.last;
			this.clockwise = clockwise;
		}
		
		public ModulusRange(int first, int last, boolean clockwise)
		{
			this.first = first;
			this.last = last;
			this.clockwise = clockwise;
		}
		
		public ModulusRange(BasicRoutingTask range, BasicArchitecture ringArchitecture)
		{
			//creates a clockwise Range
			int source = ringArchitecture.coordinatesToNumber(
					range.getSourceNode());
			int dest = ringArchitecture.coordinatesToNumber(
					range.getDestinationNode());
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
		
		private ModulusRange(int first, int last)
		{
			this.first = first;
			this.last = last;
			this.clockwise = true;
		}
		
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
					return new ModulusRange[] {
							new ModulusRange(toConvert.first, modulus), 
							new ModulusRange(0, toConvert.last)};
				}
			}
			else 
			{
				if(toConvert.first < toConvert.last)
				{
					return new ModulusRange[] {
							new ModulusRange(toConvert.last, modulus), 
							new ModulusRange(0, toConvert.first)};
				} 
				else 
				{ 
					return new ModulusRange[] {new ModulusRange(toConvert.last,toConvert.first)};
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

	public boolean allTasksFinished()
	{
		return currentlyExecutingTasks.isEmpty() && 
				previouslySentTasks.isEmpty() && 
				newlyReceivedTasks.isEmpty();
	}
}