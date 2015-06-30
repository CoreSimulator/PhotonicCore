package edu.salisbury.cyclical_core_simulator;

import java.util.Iterator;
import java.util.LinkedList;

import edu.salisbury.core_simulator.CoreNode;
import edu.salisbury.core_simulator.CoreNodeIOPort;
import edu.salisbury.core_simulator.CoreTask;

/**
 * A cyclical node is a non-head node which passes messages to other nodes in Cyclical architecture.
 * 
 * @author timfoil
 * 
 */
public class CyclicalNode extends CoreNode
{
	/**
	 * A queue of tasks for each of which this node is the source of the message to be sent. The 
	 * first task is the currently executing task while the others are waiting to be executed.
	 */
	public LinkedList<CoreTask> tasks = new LinkedList<CoreTask>();
	
	private int nodeNumber;
	
	/**
	 * Constructor for cyclical node
	 * @param overseerNode the headNode
	 * @param nodeNumber the designated nodeNumber for this node
	 */
	public CyclicalNode(CyclicalHeadNode overseerNode, int nodeNumber)
	{
		edges = new CoreNodeIOPort[3];
		setOverseerEdge(overseerNode); 
		this.nodeNumber = nodeNumber;
	}
	
	/**
	 * Adds a task to this specific node. This simulates a message with this node as the source 
	 * and some other node as the destination.
	 * 
	 * @param task The task being added to this node.
	 */
	public void addTask(CoreTask task)
	{
		tasks.addLast(task);
		if(tasks.size() == 1)
		{
			CyclicalTask basicTask = (CyclicalTask)task;
			basicTask.status = CyclicalTask.CyclicalTaskStatus.REQUESTING;
			requestTaskClearanceFromHead(basicTask);
		}
	}
	
	/**
	 * Sets the link containing the clockwise neighbor of this node.
	 * @param neighbor The clockwise neighbor of this node
	 */
	public void setClockwiseEdge(CyclicalNode neighbor)
	{
		edges[2] = new CoreNodeIOPort(neighbor);
	}
	
	/**
	 * Sets the link containing the counterClockwise neighbor of this node.
	 * @param neighbor The counterClockwise neighbor of this node
	 */
	public void setCounterClockwiseEdge(CyclicalNode neighbor)
	{
		edges[1] = new CoreNodeIOPort(neighbor);
	}
	
	/**
	 * Gets the node connected by the clockwise edge, if the edge has been set already.
	 * @return The node connected by the clockwise edge
	 */
	public CoreNodeIOPort getClockwiseEdge()
	{
		if(edges == null) return null;
		return edges[2];
	}
	
	/**
	 * Gets the node connected by the counter-clockwiseEdge, if the edge has been set already.
	 * @return The node connected by the counter-clockwise edge
	 */
	public CoreNodeIOPort getCounterClockwiseEdge()
	{
		if(edges == null) return null;
		return edges[1];
	}
	
	/**
	 * Gets the headnode which is connected through an edge.
	 * @return
	 */
	public CoreNodeIOPort getOverseerEdge()
	{
		if(edges == null) return null;
		return edges[0];
	}
	
	/**
	 * Sets the headNode's edge given the headNode 
	 * @param overseerNode the HeadNode
	 */
	public void setOverseerEdge(CyclicalHeadNode overseerNode)
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
			CyclicalTask nextTask = (CyclicalTask) tasks.peek(); 
			
			//check the first to see if it needs to start requesting
			if(nextTask.status == CyclicalTask.CyclicalTaskStatus.NEW)
			{
				nextTask.status = CyclicalTask.CyclicalTaskStatus.REQUESTING;
				//request clearance from head
				requestTaskClearanceFromHead(nextTask);
			}
			
			Iterator<CoreTask> taskIterator = tasks.iterator();
			
			while(taskIterator.hasNext())
			{
				taskIterator.next().simulateCycle();
			}
			if(((CyclicalTask) tasks.peek()).getStatus() == CyclicalTask.CyclicalTaskStatus.COMPLETE)
			{
				System.out.println(tasks.pop());
			}
		}
	}
	
	/**
	 * If a task is waiting for clearence from the headnode, allow the headNode to approve it and 
	 * begin.
	 * @param direction
	 */
	public void approveWaitingTask(CyclicalDirection direction)
	{
		//determine if a task is waiting
		if(tasks != null && tasks.size() > 0 && 
				((CyclicalTask) tasks.peek()).getStatus() == 
				CyclicalTask.CyclicalTaskStatus.REQUESTING)
		{
			 CyclicalTask approvedTask = (CyclicalTask) tasks.peek();
			 approvedTask.status = CyclicalTask.CyclicalTaskStatus.APPROVED;
			 approvedTask.setDirection(direction);
			 teardownHeadConnection();
		} else 
		{ 
			//throw an error if a task is not waiting
			throw new RuntimeException("No task is waiting to be approved");
		}
	}
	/**
	 * Get the headnode if the OverseerLink has been set
	 * @return the headNode
	 */
	public CyclicalHeadNode getOverseerNode()
	{
		if(edges == null) return null;
		return (CyclicalHeadNode) edges[0].getLink();
	}

	/**
	 * Request clearance for the topmost task from the headNode
	 * @param taskRequest
	 */
	public void requestTaskClearanceFromHead(CyclicalTask taskRequest)
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
	
	/**
	 * Destroy the Connection from this node (the sourceNode) to the destination node.
	 * @param toCeaseComm the finished task whose connection is being destroyed
	 */
	public void teardownConnectionToDestNode(CyclicalTask toCeaseComm)
	{
		if(toCeaseComm.getSourceNodeNum() == nodeNumber)
		{
			switch(toCeaseComm.getDirection())
			{
				case CLOCKWISE:
					teardownPortConnectionWithDirection(CyclicalDirection.CLOCKWISE);
					break;
				case COUNTERCLOCKWISE:
					teardownPortConnectionWithDirection(CyclicalDirection.COUNTERCLOCKWISE);
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
					teardownPortConnectionWithDirection(CyclicalDirection.COUNTERCLOCKWISE);
					break;
				case COUNTERCLOCKWISE:					
					teardownPortConnectionWithDirection(CyclicalDirection.CLOCKWISE);
					break;
				default:
					throw new RuntimeException("Task direction should either be clockwise or" +
							"counter-clockwise.");
			}
		} else 
		{
			teardownPortConnectionWithDirection(CyclicalDirection.COUNTERCLOCKWISE);
			teardownPortConnectionWithDirection(CyclicalDirection.CLOCKWISE);
		}
	}
	
	/**
	 * Opens a connection from this node to the destination node as described in the given task.
	 * @param toStartComm The task which describes the connection to build
	 */
	public void setupConnectionToDestNode(CyclicalTask toStartComm)
	{
		if(toStartComm.getSourceNodeNum() == nodeNumber)
		{
			switch(toStartComm.getDirection())
			{
				case CLOCKWISE:
					setupConnectionWithDirection(CyclicalDirection.CLOCKWISE);
					break;
				case COUNTERCLOCKWISE:
					setupConnectionWithDirection(CyclicalDirection.COUNTERCLOCKWISE);
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
					setupConnectionWithDirection(CyclicalDirection.COUNTERCLOCKWISE);
					break;
				case COUNTERCLOCKWISE:					
					setupConnectionWithDirection(CyclicalDirection.CLOCKWISE);
					break;
				default:
					throw new RuntimeException("Task direction should either be clockwise or" +
							"counter-clockwise.");
			}
		} else 
		{
			setupConnectionWithDirection(CyclicalDirection.COUNTERCLOCKWISE);
			setupConnectionWithDirection(CyclicalDirection.CLOCKWISE);
		}
	}
	
	private void teardownPortConnectionWithDirection(CyclicalDirection direction)
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
	
	private void setupConnectionWithDirection(CyclicalDirection direction)
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
	 * @return the nodeNumber that this node is designated with
	 */
	public int getNodeNumber()
	{
		return nodeNumber;
	}
}

