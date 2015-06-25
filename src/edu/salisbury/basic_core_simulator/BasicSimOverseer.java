package edu.salisbury.basic_core_simulator;

import edu.salisbury.core_simulator.Coordinate;
import edu.salisbury.core_simulator.CoreSimOverseer;
import edu.salisbury.core_simulator.LogEntry;

public class BasicSimOverseer extends CoreSimOverseer
{	
	private BasicArchitecture simulation;
	public static int bitsPerFlit = 64;
	public static int sendFromHeadToSourceCycles = 1;
	public static int teardownTime = 1;
	
	public BasicSimOverseer(int numberOfCoreNodes)
	{
		simulation = new BasicArchitecture(numberOfCoreNodes);
	}
	
	@Override
	protected void delegateTaskToNode(LogEntry entry)
	{
		simulation.simulateTask(entry);
	}

	@Override
	protected void simulateCycle()
	{
		simulation.simCycle();
	}

	@Override
	public boolean allTasksFinished()
	{
		return simulation.headNode.allTasksFinished();
	}

	public class BasicArchitecture
	{
		public BasicHeadNode headNode;
		public BasicNode[][] architecture;
		
		public BasicArchitecture(int numberOfCoreNodes)
		{
			headNode = new BasicHeadNode(this, numberOfCoreNodes, bitsPerFlit, teardownTime);
			initArchitecture();
		}
		
		/* Create the node architecture in a clockwise fashion.
		 * Setup the edges as BasicNodeIOPorts */
		private void initArchitecture()
		{
			architecture = new BasicNode[2][8];
			BasicNode[] basicNodeList = new BasicNode[16];
			
			Coordinate nodeCoord = numberToCoordinate(0);
			basicNodeList[0] = new BasicNode(headNode, nodeCoord, 0);
			
			
			architecture[nodeCoord.getX()-1][nodeCoord.getY()] = basicNodeList[0];
			
			for(int i = 1; i < basicNodeList.length; i++)
			{
				nodeCoord = numberToCoordinate(i);
				basicNodeList[i] = new BasicNode(headNode, nodeCoord, i);
				
				basicNodeList[i].setCounterClockwiseEdge(basicNodeList[i-1]);
				basicNodeList[i-1].setClockwiseEdge(basicNodeList[i]);
				
				
				architecture[nodeCoord.getX()-1][nodeCoord.getY()] = basicNodeList[i]; 
			}
			basicNodeList[basicNodeList.length - 1].setClockwiseEdge(basicNodeList[0]);
			basicNodeList[0].setCounterClockwiseEdge(basicNodeList[basicNodeList.length - 1]);
			headNode.setEdges(basicNodeList);
		}

		public void simulateTask(LogEntry entry)
		{
			BasicTask taskToAssign = new BasicTask(this.coordinatesToNode(entry.sourceNode()), 
					entry.sourceNode(), entry.destNode(), entry.packetSize(), bitsPerFlit, cycles);
			coordinatesToNode(taskToAssign.getSourceNode()).addTask(taskToAssign);
		}
		
		public void simCycle()
		{
			
			for(int i = 0; i < architecture.length; i++)
			{
				for(int j = 0; j < architecture[i].length; j++)
				{
					architecture[i][j].simulateCycle();
				}
			}
			headNode.simulateCycle();
		}
		
		public Coordinate numberToCoordinate(int nodeNumber)
		{
			checkForValidNodeNumber(nodeNumber);
			return new Coordinate((nodeNumber/8)+1 , nodeNumber % 8); 
		}
		
		public BasicNode numberToNode(int nodeNumber)
		{
			return coordinatesToNode(numberToCoordinate(nodeNumber));
		}
		
		public int coordinatesToNumber(Coordinate coord)
		{
			checkForValidCoordinates(coord);
			return (coord.getY() + (coord.getX()-1) * 8);
		}
		
		public BasicNode coordinatesToNode(Coordinate coord)
		{
			checkForValidCoordinates(coord);
			return architecture[coord.getX()-1][coord.getY()];
		}
		
		private void checkForValidCoordinates(Coordinate coord)
		{
			if(coord.getX() > 2 || coord.getX() < 1)
			{
				throw new IllegalArgumentException("X coordinate must be either 1 or 2");
			}
			if(coord.getY() > 7 || coord.getY() < 0)
			{
				throw new IllegalArgumentException("Y coordinate must be between 0 and 7 " +
						"inclusive.");
			}
		}
		
		private void checkForValidNodeNumber(int nodeNumber)
		{
			if(nodeNumber < 0 || nodeNumber > 15) 
			{
				throw new IllegalArgumentException("The nodenumber for the basicArchitecture " +
						"must be between 0 and 15 inclusive.");
			}
		}
	}
}
