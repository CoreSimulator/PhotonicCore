package edu.salisbury.core_simulator;

/**
 * A basic abstract building block for a simulator class. Lays down some of the standards that
 * a subclassed simulator are expected to follow.
 * @author timfoil
 *
 */
public abstract class CoreSimOverseer
{	
	/*The current cycle that Simulator is on*/
	private int cycles = 0; 
	private int logIndex = 0;
	
	/**The architecture that this simulator overseer uses to simulate tasks*/
	protected CoreArchitecture simulation;
	
	/**
	 * Gives a task that needs to be simulated to the underlying architecture to handle.
	 * @param entry An logEntry that describes a task that needs to be handled.
	 */
	protected abstract void delegateTaskToNode(LogEntry entry);
	
	/**
	 * Simulates this simulator's architecture for a single cycle 
	 */
	protected abstract void simulateCycle();
	
	public void simulateWithLog(CoreLog log)
	{
		//continue simulating until no more log entries to create tasks
		//from and no more tasks to complete
		while (logIndex < log.logSize() || !allTasksFinished())
		{
			//delegate tasks to cores
			while( logIndex < log.logSize() && log.getEntry(logIndex).timeStamp() == cycles)
			{
				delegateTaskToNode(log.getEntry(logIndex++));
			}
			//run a cycle
			simulateCycle();
			cycles++;
		}
	}
	
	/**
	 * Getter for the current cycle the simulator is on.
	 * Be careful when calling this as the part of the simulator you are working on may be finished 
	 * with its cycle but this number does not increment until every object that needs to be has 
	 * been simulated.
	 * @return The current cycle
	 */
	public int currentCycle()
	{
		return cycles;
	}
	
	/**
	 * 
	 * @return 	<code>true</code> If all tasks requested to be simulated are finished;
	 * 			<code>false</code> otherwise
	 */
	public abstract boolean allTasksFinished();
}
