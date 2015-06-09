package edu.salisbury.core_simulator;

//A dataStructure for holding Log Data
public class LogEntry 
{
	private double timeStamp;
	private int sourceX;
	private int sourceY;
	private int destX;
	private int destY;
	private int packetSize;
	
	public LogEntry(
			double timeStamp, int sourceX, int sourceY, int destX, int destY, int packetSize)
	{
		this.timeStamp = timeStamp;
		this.sourceX = sourceX;
		this.sourceY = sourceY;
		this.destX = destX;
		this.destY = destY;
		this.packetSize = packetSize;
	}

	public int packetSize() 
	{
		return packetSize;
	}
	
	public int sourceX() 
	{
		return sourceX;
	}
	
	public int sourceY() 
	{
		return sourceY;
	}
	
	public int destX() 
	{
		return destX;
	}
	
	public int destY() 
	{
		return destY;
	}
	
	public double timeStamp() 
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
