package edu.salisbury.core_simulator;

public abstract class CoreSimOverseer
{	
	public static int cycles = 0; //TODO make nonstatic
	private int logIndex = 0;
	
	protected abstract void delegateTaskToNode(LogEntry entry);
	
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
				//TODO delete System.out.println("logEntry/logSize = "+ logIndex + "/" + (log.logSize()-1));
				delegateTaskToNode(log.getEntry(logIndex++));
			}
			
			//run a cycle
			simulateCycle();
			cycles++;
		}
	}
	
	public abstract boolean allTasksFinished();
}
