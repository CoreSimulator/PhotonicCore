package edu.salisbury.basic_core_simulator;

import java.util.Iterator;
import java.util.LinkedList;

import edu.salisbury.core_simulator.CoreNode;
import edu.salisbury.core_simulator.CoreNodeIOPort;
import edu.salisbury.core_simulator.CoreTask;


public class BasicNode extends CoreNode
{
	public LinkedList<CoreTask> tasks = new LinkedList<CoreTask>();
	
	private int nodeNumber;
	
	public BasicNode(BasicHeadNode overseerNode, int nodeNumber)
	{
		edges = new CoreNodeIOPort[3];
		setOverseerEdge(overseerNode); 
		this.nodeNumber = nodeNumber;
	}
	
	public void addTask(CoreTask task)
	{
		tasks.addLast(task);
		if(tasks.size() == 1)
		{
			BasicTask basicTask = (BasicTask)task;
			basicTask.status = BasicTask.BasicTaskStatus.REQUESTING;
			requestTaskClearanceFromHead(basicTask);
		}
	}
	
	public void setClockwiseEdge(BasicNode neighbor)
	{
		edges[2] = new CoreNodeIOPort(neighbor);
	}
	
	public void setCounterClockwiseEdge(BasicNode neighbor)
	{
		edges[1] = new CoreNodeIOPort(neighbor);
	}
	
	public CoreNodeIOPort getClockwiseEdge()
	{
		if(edges == null) return null;
		return edges[2];
	}
	
	public CoreNodeIOPort getCounterClockwiseEdge()
	{
		if(edges == null) return null;
		return edges[1];
	}
	
	public CoreNodeIOPort getOverseerEdge()
	{
		if(edges == null) return null;
		return edges[0];
	}
	
	public void setOverseerEdge(BasicHeadNode overseerNode)
	{
		edges[0] = new CoreNodeIOPort(overseerNode);
	}

	@Override
	public void simulateCycle()
	{
		//simulate cycle for all edges
		for(CoreNodeIOPort edge: edges)
		{
			edge.simulateCycle();
		}
		//simulate cycle for first task
		if(!tasks.isEmpty())
		{
			BasicTask nextTask = (BasicTask) tasks.peek(); //TODO move this section block to
			
			//check the first to see if it needs to start requesting
			if(nextTask.status == BasicTask.BasicTaskStatus.NEW)
			{
				nextTask.status = BasicTask.BasicTaskStatus.REQUESTING;
				//request clearance from head
				requestTaskClearanceFromHead(nextTask);
			}
			
			Iterator<CoreTask> taskIterator = tasks.iterator();
			
			while(taskIterator.hasNext())
			{
				taskIterator.next().simulateCycle();
			}
			if(((BasicTask) tasks.peek()).getStatus() == BasicTask.BasicTaskStatus.COMPLETE)
			{
				System.out.println(tasks.pop());
			}
		}
	}
	
	public void approveWaitingTask(BasicDirection direction)
	{
		//determine if a task is waiting
		if(tasks != null && tasks.size() > 0 && 
				((BasicTask) tasks.peek()).getStatus() == BasicTask.BasicTaskStatus.REQUESTING)
		{
			 BasicTask approvedTask = (BasicTask) tasks.peek();
			 approvedTask.status = BasicTask.BasicTaskStatus.APPROVED;
			 approvedTask.setDirection(direction);
			 teardownHeadConnection();
		} else 
		{ 
			//throw an error if a task is not waiting
			throw new RuntimeException("No task is waiting to be approved");
		}
	}
	
	public BasicHeadNode getOverseerNode()
	{
		if(edges == null) return null;
		return (BasicHeadNode) edges[0].getLink();
	}

	public void requestTaskClearanceFromHead(BasicTask taskRequest)
	{
		if(tasks == null || tasks.isEmpty() || tasks.peek() == null)
			throw new RuntimeException("No task exists. Clearance cannot be obtained");
		if(getOverseerEdge().isAvailable()) 
		{
			getOverseerNode().initiateConnectionAtEdge(nodeNumber);
			initiateConnectionAtEdge(0);
			getOverseerNode().addRequestForTask(taskRequest);
		} else throw new RuntimeException("Port is still opened. Clearance could not be obtained.");
	}
	
	private void teardownHeadConnection()
	{
		getOverseerEdge().teardownConnection();
	}
	
	public void teardownConnectionToDestNode(BasicTask toCeaseComm)
	{
		if(toCeaseComm.getSourceNodeNum() == nodeNumber)
		{
			switch(toCeaseComm.getDirection())
			{
				case CLOCKWISE:
					teardownPortConnectionWithDirection(BasicDirection.CLOCKWISE);
					break;
				case COUNTERCLOCKWISE:
					teardownPortConnectionWithDirection(BasicDirection.COUNTERCLOCKWISE);
					break;
				default:
					throw new RuntimeException("Task direction should either be clockwise or" +
							"counter-clockwise.");
			}
			
		} else if (toCeaseComm.getDestinationNodeNum() == nodeNumber)
		{
			switch(toCeaseComm.getDirection())
			{
				case CLOCKWISE:
					teardownPortConnectionWithDirection(BasicDirection.COUNTERCLOCKWISE);
					break;
				case COUNTERCLOCKWISE:					
					teardownPortConnectionWithDirection(BasicDirection.CLOCKWISE);
					break;
				default:
					throw new RuntimeException("Task direction should either be clockwise or" +
							"counter-clockwise.");
			}
		} else 
		{
			teardownPortConnectionWithDirection(BasicDirection.COUNTERCLOCKWISE);
			teardownPortConnectionWithDirection(BasicDirection.CLOCKWISE);
		}
	}
	
	public void setupConnectionToDestNode(BasicTask toStartComm)
	{
		if(toStartComm.getSourceNodeNum() == nodeNumber)
		{
			switch(toStartComm.getDirection())
			{
				case CLOCKWISE:
					setupConnectionWithDirection(BasicDirection.CLOCKWISE);
					break;
				case COUNTERCLOCKWISE:
					setupConnectionWithDirection(BasicDirection.COUNTERCLOCKWISE);
					break;
				default:
					throw new RuntimeException("Task direction should either be clockwise or" +
							"counter-clockwise.");
			}
			
		} else if (toStartComm.getDestinationNodeNum() == nodeNumber)
		{
			switch(toStartComm.getDirection())
			{
				case CLOCKWISE:
					setupConnectionWithDirection(BasicDirection.COUNTERCLOCKWISE);
					break;
				case COUNTERCLOCKWISE:					
					setupConnectionWithDirection(BasicDirection.CLOCKWISE);
					break;
				default:
					throw new RuntimeException("Task direction should either be clockwise or" +
							"counter-clockwise.");
			}
		} else 
		{
			setupConnectionWithDirection(BasicDirection.COUNTERCLOCKWISE);
			setupConnectionWithDirection(BasicDirection.CLOCKWISE);
		}
	}
	
	private void teardownPortConnectionWithDirection(BasicDirection direction)
	{
		switch(direction)
		{
			case CLOCKWISE:
				getClockwiseEdge().teardownConnection();
				break;
			case COUNTERCLOCKWISE:
				getCounterClockwiseEdge().teardownConnection();
				break;
			default:
				throw new RuntimeException("Direction to teardown should either be clockwise or" +
						"counter-clockwise.");
		}
	}
	
	private void setupConnectionWithDirection(BasicDirection direction)
	{
		switch(direction)
		{
			case CLOCKWISE:
				getClockwiseEdge().initiateConnection();
				break;
			case COUNTERCLOCKWISE:
				getCounterClockwiseEdge().initiateConnection();
				break;
			default:
				throw new RuntimeException("Direction for setup should either be clockwise or" +
						"counter-clockwise.");
		}
	}

	/**
	 * @return the nodeNumber
	 */
	public int getNodeNumber()
	{
		return nodeNumber;
	}
}

