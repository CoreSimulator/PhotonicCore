package edu.salisbury.basic_core_simulator;

import edu.salisbury.core_simulator.CoreNode;
import edu.salisbury.core_simulator.CoreTask;


public class BasicTask extends CoreTask
{
	/**
	 * Describes the different states a BasicTask could currently be in.
	 * @author timfoil
	 *
	 */
	public enum BasicTaskStatus
	{
		NEW, REQUESTING, APPROVED, DENIED, SENDING, TEARDOWN, COMPLETE
	}
		
	private CoreNode sourceNodeRef;
	private int sourceNodeNum;
	private int destinationNodeNum;
	private BasicDirection direction = BasicDirection.UNDETERMINED;
	private int flitSize;
	private int bitsSent;
	private int bitsPerFlit;
	
	//the following time variables should add up to the taskTime variable
	
	protected int newTaskTime;
	protected int requestingTaskTime;
	protected int approvedTaskTime;
	protected int deniedTaskTime;
	protected int sendingTaskTime;
	protected int completeTaskTime;
	protected int teardownTaskTime;
	
	/**Cycle number this task was created on*/
	protected int taskCreationTime;
	
	public BasicTaskStatus status;
	
	//TODO javadoc
	/**
	 * 
	 * @param sourceNode
	 * @param destinationNode
	 * @param flitSize 
	 * @param taskCreationTime Cycle number this task was created on
	 */
	public BasicTask(CoreNode sourceNodeRef, int sourceNodeNum, int destinationNodeNum, 
			int flitSize, int bitsPerFlit, int taskCreationTime, BasicArchitecture architecture) 
	{
		this.sourceNodeRef = sourceNodeRef;
		this.sourceNodeNum = sourceNodeNum;
		this.destinationNodeNum = destinationNodeNum;
		this.status = BasicTaskStatus.NEW;
		this.flitSize = flitSize;
		this.bitsPerFlit = bitsPerFlit;
		this.taskCreationTime = taskCreationTime;
	}//end BasicTask constructor

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
				break;
			case COMPLETE:
				completeTaskTime++;
				break;
			default:
				throw new RuntimeException("Basic Task's state must be defined " +
						"with a BasicTaskStatus enum.");
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
				throw new RuntimeException("Basic Task's state must be defined " +
						"with a BasicTaskStatus enum.");
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
				status = BasicTaskStatus.SENDING;//Change to sending
				((BasicNode) sourceNodeRef).setupConnectionToDestNode(this);//Create port tunnel
				
				incrementTotalTaskTime();//increment
				//Start sending 
				if(sendData()) //Send data
				{
					//finished, change to teardown
					status = BasicTaskStatus.TEARDOWN;
				}
				break;
			case DENIED:
				throw new RuntimeException("Denials are not possible in this architecture");
			case SENDING:
				incrementTotalTaskTime();//increment
				if(sendData()) //Send data
				{
					//finished, change to teardown
					status = BasicTaskStatus.TEARDOWN;
				}
				break;
			case TEARDOWN:
				incrementTotalTaskTime();//increment teardown
				((BasicNode) sourceNodeRef).teardownConnectionToDestNode(this);//teardown
				status = BasicTaskStatus.COMPLETE;//change to complete
				break;
			case COMPLETE:
				throw new RuntimeException("This task is already complete, " +
						"this should not be run");
			default:
				throw new RuntimeException("Basic Task's state must be defined " +
						"with a BasicTaskStatus enum.");
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
	public BasicTaskStatus getStatus()
	{
		return status;
	}

	
	/**
	 * @return the sourceNodeNum
	 */
	public int getSourceNodeNum()
	{
		return sourceNodeNum;
	}
	

	/**
	 * @return the destinationNodeNum
	 */
	public int getDestinationNodeNum()
	{
		return destinationNodeNum;
	}

	/**
	 * @return the flitSize
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
		return (flitSize * bitsPerFlit) - bitsSent;
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
	public BasicDirection getDirection()
	{
		return direction;
	}

	/**
	 * @param direction the direction to set
	 */
	public void setDirection(BasicDirection direction)
	{
		this.direction = direction;
	}
	
	@Override
	public String toString()
	{
		StringBuilder taskAnalysis = new StringBuilder();
		taskAnalysis.append("Task created at: ").append(taskCreationTime);
		taskAnalysis.append(" Task finished at: ").append(taskCreationTime + taskTime);
		taskAnalysis.append(" Task duration Time: ").append(taskTime);
		taskAnalysis.append(" Task direction: ").append(direction);
		taskAnalysis.append(" Task source: ").append(sourceNodeNum);
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
