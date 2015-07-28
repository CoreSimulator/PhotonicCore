package edu.salisbury.photonic.TestingStuff;

import edu.salisbury.photonic.core_simulator.CoreLog;
import edu.salisbury.photonic.core_simulator.LogReader;


public class LogSectionAnalyzer 
{

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		System.out.println("Run task has been executed");
		CoreLog barnesLogFile = LogReader.readLogIgnoreRepeaters("flow_barnes.log");
		System.out.println("Length of repeaterLess BarnesLog: " + barnesLogFile.logSize());
		breakLogIntoSections(10, barnesLogFile);
	}
	
	public static CoreLog[] breakLogIntoSections(int numberOfSections, CoreLog log)
	{
		CoreLog[] logSections = new CoreLog[numberOfSections];
		
		int logSize = log.logSize();
		int incrSize = (int) Math.ceil((double) logSize/numberOfSections);
		
		for(int i = 0, endIndex = 0, beginIndex; i < numberOfSections; i++)
		{
			beginIndex = endIndex;
			endIndex += incrSize;
			endIndex = (endIndex > logSize) ? logSize : endIndex;
			
			//System.out.println("(" + beginIndex + ", " + endIndex + ")");// neat debug statement
			
			logSections[i] = log.subLog(beginIndex, endIndex);
		}
		return logSections;
	}
	

}
