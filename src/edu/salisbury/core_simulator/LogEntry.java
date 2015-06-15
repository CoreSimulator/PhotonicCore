package edu.salisbury.core_simulator;


/**
 * A dataStructure for holding Log Data
 * @author timfoil
 */
public class LogEntry 
{
	private int timeStamp;
	private int sourceX;
	private int sourceY;
	private int destX;
	private int destY;
	private int packetSize;
	
	/**
	 * Constructor for LogEntry, takes all the data typical of a single log as parameters.
	 * 
	 * @param timeStamp of the LogEntry communication
	 * @param sourceX of the communication
	 * @param sourceY of the communication
	 * @param destX of the communication
	 * @param destY of the communication
	 * @param packetSize of the data sent (in flits)
	 */
	public LogEntry(
			int timeStamp, int sourceX, int sourceY, int destX, int destY, int packetSize)
	{
		this.timeStamp = timeStamp;
		this.sourceX = sourceX;
		this.sourceY = sourceY;
		this.destX = destX;
		this.destY = destY;
		this.packetSize = packetSize;
	}

	/**
	 * LogEntry getter for packetSize.
	 * @return packetSize
	 */
	public int packetSize() 
	{
		return packetSize;
	}
	
	/**
	 * LogEntry getter for sourceX.
	 * @return sourceX
	 */
	public int sourceX() 
	{
		return sourceX;
	}
	
	/**
	 * LogEntry getter for sourceY.
	 * @return sourceY
	 */
	public int sourceY() 
	{
		return sourceY;
	}
	
	/**
	 * LogEntry getter for destX.
	 * @return destX
	 */
	public int destX() 
	{
		return destX;
	}
	
	/**
	 * LogEntry getter for destY.
	 * @return destY
	 */
	public int destY() 
	{
		return destY;
	}
	
	/**
	 * LogEntry getter for timeStamp.
	 * @return timeStamp
	 */
	public int timeStamp() 
	{
		return timeStamp;
	}
	
	//PrettyPrints the data in this object
	public String toString() 
	{
		return "timeStamp: " + timeStamp + " sourceX: "+ sourceX + " sourceY: " +
				sourceY + " destX: " + destX + " destY: " + destY + " packetSize: " + packetSize;
		
	}
}
