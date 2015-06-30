package edu.salisbury.photonic.cyclical_core_simulator;

import edu.salisbury.photonic.core_simulator.CoreTask;

/**
 * Tasks for {@link CyclicalHeadNode BasicHeadNodes}.
 * 
 * After the HeadNode decides how to route a task, BasicRoutingTask also 
 * serves as a sort of memory for the HeadNode to let it know which
 * connections between cores are currently being occupied.
 * @author timfoil
 *
 */
public class CyclicalRoutingTask extends CoreTask
{
	
	/** 
	 * Initially set to -1, then set to the number of that the task 
	 * is supposed to be handled in, will countdown after every tick
	 */
	 
	public boolean finished = false;
	
	private int timeLeftForTask = -1;
	private int flitsToSend;
	private int sourceNodeNumber;
	private int destinationNodeNumber;
	private CyclicalDirection direction = CyclicalDirection.UNDETERMINED;
	
	/**
	 * Constructor for a routingTask.
	 * @param request to be represented by the created routingTask.
	 */
	public CyclicalRoutingTask(CyclicalTask request)
	{
		sourceNodeNumber = request.getSourceNodeNum();
		destinationNodeNumber = request.getDestinationNodeNum();
		flitsToSend = request.getFlitSize();
	}
	
	@Override
	public void simulateCycle()
	{
		if(timeLeftForTask == -1)
			throw new RuntimeException("Task cannot be run. Has not been setup.");
		if(finished) 
			throw new RuntimeException("Task is finished, should be " +
					"destroyed. Cannot simulate a new cycle.");
		
		super.simulateCycle();
		timeLeftForTask--;
		if(timeLeftForTask == 0) 
		{
			finished = true;
		}
	}

	/**
	 * @return the sourceNodeNumber
	 */
	public int getSourceNodeNumber()
	{
		return sourceNodeNumber;
	}

	/**
	 * @return the destinationNodeNumber
	 */
	public int getDestinationNodeNumber()
	{
		return destinationNodeNumber;
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

	/**
	 * @return the flitsToSend
	 */
	public int getFlitsToSend()
	{
		return flitsToSend;
	}
	/**
	 * @return the timeLeftForTask
	 */
	public int getTimeLeftForTask()
	{
		return timeLeftForTask;
	}
	/**
	 * @param timeLeftForTask the timeLeftForTask to set
	 */
	public void setTimeLeftForTask(int timeLeftForTask)
	{
		this.timeLeftForTask = timeLeftForTask;
	}
}
