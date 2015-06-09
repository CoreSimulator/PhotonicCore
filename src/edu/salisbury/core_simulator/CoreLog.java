package edu.salisbury.core_simulator;

import java.util.ArrayList;

public class CoreLog 
{
	private ArrayList<LogEntry> log = null;
	
	public CoreLog()
	{
		log = new ArrayList<LogEntry>();
	}
	
	public CoreLog(ArrayList<LogEntry> log)
	{
		this.log = log;
	}
	
	public void addEntry(LogEntry entry)
	{
		log.add(entry);
	}
	
	public int logSize()
	{
		return log.size();
	}
	
	public LogEntry getEntry(int index)
	{
		return log.get(index);
	}
	
	//To String for pretty printing of log file
	
	public String toString()
	{
		//Edge cases for if log doesn't exist or doesn't have any entries
		if(log == null) return null;
		else if(log.size() == 0) return "";
		
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for(LogEntry entry : log)
		{
			i++;
			builder.append("log event: "+ i + " " + entry.toString()+ "\n");
		}
		
		//TODO remove the last /n from the string
		String toReturn = builder.toString();
		return toReturn.substring(0, toReturn.length()-1);
	}
}
