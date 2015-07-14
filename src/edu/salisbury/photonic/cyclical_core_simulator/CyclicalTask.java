package edu.salisbury.photonic.cyclical_core_simulator;

import edu.salisbury.photonic.MainClasses.Main;
import edu.salisbury.photonic.core_simulator.CoreTask;
import edu.salisbury.photonic.core_simulator.MainGUI;

/**
 * A task that describes a message that needs to be passed from a source to destination node in a 
 * cyclical architecture. Contains information about the source; destination; and direction of the 
 * dataflow, the status of the task, and the amount of cycles the task has spent in each status. 
 * 
 * @author timfoil
 */
public class CyclicalTask extends CoreTask
{
	/**
	 * Describes the different states a CyclicalTask could currently be in.
	 * @author timfoil
	 *
	 */
	public enum CyclicalTaskStatus
	{
		NEW, REQUESTING, APPROVED, DENIED, SENDING, TEARDOWN, COMPLETE
	}
	
	private CyclicalDirection direction = CyclicalDirection.UNDETERMINED;

	
	private CyclicalNode sourceNodeRef;
	private CyclicalArchitecture architecture;
	
	private int destinationNodeNum;
	private int flitSize;
	private int bitsSent;
	
	//the following time variables should add up to the taskTime variable
	private int newTaskTime;
	private int requestingTaskTime;
	private int approvedTaskTime;
	private int deniedTaskTime;
	private int sendingTaskTime;
	private int completeTaskTime;
	private int teardownTaskTime;
	
	private int taskId; //Typically the log index this task was created at
	
	/**Cycle number this task was created on*/
	private int taskCreationTime;
	
	/**The current status of this task.*/
	public CyclicalTaskStatus status;
	
	/**
	 * Constructor for a CyclicalTask.
	 * 
	 * @param 	sourceNodeRef reference to the sourceNode of the task
	 * @param 	destinationNodeNum The designated nodeNumber of the destinationNode
	 * @param 	flitSize The number of flitsToSend typically either 1 or 5
	 * @param 	taskCreationTime cycle number the task was created on
	 * @param 	architecture the overlying architecture of the connected nodes, which
	 * 			should include the source and destination nodes.
	 */
	public CyclicalTask(int taskId, CyclicalNode sourceNodeRef, int destinationNodeNum, 
			int flitSize, int taskCreationTime, CyclicalArchitecture architecture) 
	{
		this.taskId = taskId;
		this.architecture = architecture;
		this.sourceNodeRef = sourceNodeRef;
		this.destinationNodeNum = destinationNodeNum;
		this.status = CyclicalTaskStatus.NEW;
		this.flitSize = flitSize;
		this.taskCreationTime = taskCreationTime;
		
	}

	@Override
	public void incrementTotalTaskTime()
	{
		super.incrementTotalTaskTime();
		switch(status)
		{
			case NEW:
				newTaskTime++;
				break;
			case REQUESTING:
				requestingTaskTime++;
				break;
			case APPROVED:
				approvedTaskTime++;
				break;
			case DENIED:
				deniedTaskTime++;
				break;
			case SENDING:
				sendingTaskTime++;
				break;
			case TEARDOWN:
				teardownTaskTime++;
				Main.tester[taskId] = true; //TODO
				break;
			case COMPLETE:
				completeTaskTime++;
				break;
			default:
				throw new RuntimeException("Cyclical Task's state must be defined " +
						"with a CyclicalTaskStatus enum.");
		}
	}

	@Override
	public void incrementTotalTaskTimeBy(int increment)
	{
		super.incrementTotalTaskTimeBy(increment);
		switch(status)
		{
			case NEW:
				newTaskTime += increment;
				break;
			case REQUESTING:
				requestingTaskTime += increment;
				break;
			case APPROVED:
				approvedTaskTime += increment;
				break;
			case DENIED:
				deniedTaskTime += increment;
				break;
			case SENDING:
				sendingTaskTime += increment;
				break;
			case TEARDOWN:
				teardownTaskTime += increment;
				break;
			case COMPLETE:
				completeTaskTime += increment;
				break;
			default:
				throw new RuntimeException("Cyclical Task's state must be defined " +
						"with a CyclicalTaskStatus enum.");
		}
	}
	
	@Override
	public void simulateCycle()
	{
		switch(status)
		{
			case NEW:
				incrementTotalTaskTime();
				break;
			case REQUESTING:
				incrementTotalTaskTime();//increment
				break;
			case APPROVED:
				status = CyclicalTaskStatus.SENDING;//Change to sending
				((CyclicalNode) sourceNodeRef).setupConnectionToDestNode(this);//Create port tunnel
				
				incrementTotalTaskTime();//increment
				
				//Start sending 
				if(sendData()) //Send data
				{
					//finished, change to teardown
					status = CyclicalTaskStatus.TEARDOWN;
				}
				break;
			case DENIED:
				throw new RuntimeException("Denials are not possible in this architecture");
			case SENDING:
				incrementTotalTaskTime();//increment
				if(sendData()) //Send data
				{
					//finished, change to teardown
					status = CyclicalTaskStatus.TEARDOWN;
				}
				break;
			case TEARDOWN:
				incrementTotalTaskTime();//increment teardown
				((CyclicalNode) sourceNodeRef).teardownConnectionToDestNode(this);//teardown
				status = CyclicalTaskStatus.COMPLETE;//change to complete
				MainGUI.totalRequestingTime += requestingTaskTime;
				MainGUI.totalTasks ++;
				break;
			case COMPLETE:
				throw new RuntimeException("This task is already complete, " +
						"this should not be run");
			default:
				throw new RuntimeException("Cyclical Task's state must be defined " +
						"with a CyclicalTaskStatus enum.");
		}
		
	}

	//method that simulates sending data to cores
	//returns true when all data has been sent
	private boolean sendData()
	{
		bitsSent++;
		return getBitsToSend() <= 0;
	}

	/**
	 * @return the newTaskTime
	 */
	public int getNewTaskTime()
	{
		return newTaskTime;
	}

	/**
	 * @return the requestingTaskTime
	 */
	public int getRequestingTaskTime()
	{
		return requestingTaskTime;
	}

	/**
	 * @return the approvedTaskTime
	 */
	public int getApprovedTaskTime()
	{
		return approvedTaskTime;
	}

	/**
	 * @return the deniedTaskTime
	 */
	public int getDeniedTaskTime()
	{
		return deniedTaskTime;
	}

	/**
	 * @return the sendingTaskTime
	 */
	public int getSendingTaskTime()
	{
		return sendingTaskTime;
	}

	/**
	 * @return the completeTaskTime
	 */
	public int getCompleteTaskTime()
	{
		return completeTaskTime;
	}

	/**
	 * @return the status
	 */
	public CyclicalTaskStatus getStatus()
	{
		return status;
	}

	/**
	 * @return the sourceNodeNum
	 */
	public int getSourceNodeNum()
	{
		return sourceNodeRef.getNodeNumber();
	}
	

	/**
	 * @return the destinationNodeNum
	 */
	public int getDestinationNodeNum()
	{
		return destinationNodeNum;
	}

	/**
	 * @return the total flitSize of the package to send
	 */
	public int getFlitSize()
	{
		return flitSize;
	}

	/**
	 * @return the bitsSent
	 */
	public int getBitsSent()
	{
		return bitsSent;
	}

	/**
	 * @return the bitsToSend
	 */
	public int getBitsToSend()
	{
		return (flitSize * architecture.getBitsPerFlit()) - bitsSent;
	}

	/**
	 * @return the teardownTaskTime
	 */
	public int getTeardownTaskTime()
	{
		return teardownTaskTime;
	}

	/**
	 * @return the direction
	 */
	public CyclicalDirection getDirection()
	{
		return direction;
	}

	/**
	 * @param direction the direction to set
	 */
	public void setDirection(CyclicalDirection direction)
	{
		this.direction = direction;
	}
	
	@Override
	public String toString()
	{
		StringBuilder taskAnalysis = new StringBuilder();
		taskAnalysis.append("Task created at: ").append(taskCreationTime);
		taskAnalysis.append(" Task finished at: ").append(taskCreationTime + getTotalTaskTime());
		taskAnalysis.append(" Task duration Time: ").append(getTotalTaskTime());
		taskAnalysis.append(" Task direction: ").append(direction);
		taskAnalysis.append(" Task source: ").append(sourceNodeRef.getNodeNumber());
		taskAnalysis.append(" Task destination: ").append(destinationNodeNum);
		taskAnalysis.append(" Flit size: ").append(this.flitSize);
		//the following time variables should add up to the taskTime variable
		
		//TODO add a variable to lock/unlock this analysis
		if(true)
		{
			taskAnalysis.append("\nTime analysis, new: ").append(newTaskTime);
			taskAnalysis.append(" requesting: ").append(requestingTaskTime);
			taskAnalysis.append(" sending: ").append(sendingTaskTime);
			//taskAnalysis.append(" approved: ").append(approvedTaskTime);
			//taskAnalysis.append(" denied: ").append(deniedTaskTime);
			//taskAnalysis.append(" complete: ").append(completeTaskTime);
			taskAnalysis.append(" teardown: ").append(teardownTaskTime);
		}
		return taskAnalysis.toString();
	}

	/**
	 * @return the taskCreationTime
	 */
	public int getTaskCreationTime()
	{
		return taskCreationTime;
	}
}// end CurrentTask class
