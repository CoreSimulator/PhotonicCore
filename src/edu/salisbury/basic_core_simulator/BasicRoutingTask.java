package edu.salisbury.basic_core_simulator;

import edu.salisbury.core_simulator.Coordinate;
import edu.salisbury.core_simulator.CoreSimOverseer;
import edu.salisbury.core_simulator.CoreTask;

/**
 * Tasks for {@link BasicHeadNode BasicHeadNodes}.
 * 
 * After the HeadNode decides how to route a task, BasicRoutingTask also 
 * serves as a sort of memory for the HeadNode to let it know which
 * connections between cores are currently being occupied.
 * @author timfoil
 *
 */
public class BasicRoutingTask extends CoreTask
{
	
	/** initially set to -1, then set to the number of that the task 
	 * is supposed to be handled in, will countdown after every tick
	 */
	 
	public boolean finished = false;
	
	private int timeLeftForTask = -1;
	private int flitsToSend;
	private Coordinate sourceNode;
	private Coordinate destinationNode;
	private BasicDirection direction = BasicDirection.UNDETERMINED;
	
	private int creationTime;

	
	public BasicRoutingTask(BasicTask request)
	{
		sourceNode = request.getSourceNode();
		destinationNode = request.getDestinationNode();
		creationTime = request.getTaskCreationTime();
		flitsToSend = request.getFlitSize();
	}
	
	
	
	@Override
	public void simulateCycle()
	{
		if(creationTime == 1047) System.out.println("CurrentCycle: "+CoreSimOverseer.cycles+
				" Cycles run: "+ taskTime + " Cycles left to run: "+ timeLeftForTask);
		if(timeLeftForTask == -1)
			throw new RuntimeException("Task cannot be run. Has not been setup.");
		if(finished) 
			throw new RuntimeException("Task is finished, should be " +
					"destroyed. Cannot simulate a new cycle.");
		
		super.simulateCycle();
		timeLeftForTask--;
		if(timeLeftForTask == 0) 
		{
			//add option for debug-mode
			if(true)
			{
				System.out.println("RoutingTask, creationCycle: " + creationTime + 
						" deletionCycle: " + (CoreSimOverseer.cycles+1) + 
						" Source->Dest:Direction " + this.sourceNode + "->" + this.destinationNode +
						":" + direction+ "\n");
			}
			finished = true;
		}
	}

	/**
	 * @return the sourceNode
	 */
	public Coordinate getSourceNode()
	{
		return sourceNode;
	}

	/**
	 * @return the destinationNode
	 */
	public Coordinate getDestinationNode()
	{
		return destinationNode;
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
