package edu.salisbury.core_simulator;

import java.util.ArrayList;

/**
 * An object that represents a log file that supervises communications between cores.
 * CoreLogs can be created by adding entries to an empty log or by loading a log from
 * a file using {@link LogReader}
 * @author tptravitz
 *
 */
public class CoreLog 
{
	private ArrayList<LogEntry> log;
	private ArrayList<LogAnalyser> experiments = new ArrayList<LogAnalyser>();
	
	/**
	 * Constructor for a CoreLog.
	 * 
	 */
	public CoreLog()
	{
		log = new ArrayList<LogEntry>();
	}
	
	/**
	 * Constructor for a CoreLog. The most recent logs are last in the list.
	 * 
	 * @param log of LogEntry actions contained sequentially inside an Arraylist container
	 */
	public CoreLog(ArrayList<LogEntry> log)
	{
		this.log = log;
	}
	
	/**
	 * Add a LogEntry to this CoreLog.
	 * 
	 * @param entry to add to this CoreLog
	 * @return Boolean indicating if the action occurred successfully
	 */
	public boolean addEntry(LogEntry entry)
	{
		return log.add(entry);
	}
	
	/**
	 * Returns the size of the CoreLog.
	 * 
	 * @return size of the log
	 */
	public int logSize()
	{
		return log.size();
	}
	
	/**
	 * Returns a logEntry specified by the index.
	 * 
	 * @param index of the entry
	 * @return LogEntry specified by the index
	 */
	public LogEntry getEntry(int index)
	{
		return log.get(index);
	}
	
	/**
	 * Pretty printing of log file
	 * 
	 * @return String representing this log object
	 */
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
		
		//remove the last /n from the string
		String toReturn = builder.toString();
		return toReturn.substring(0, toReturn.length()-1);
	}
	
	/**
	 * Performs the specified LogAnalyser test on this CoreLog.
	 * 
	 * @param A LogAnalyser test to run on this Log.
	 * @return The results of the test
	 */
	public String performAnalysis(LogAnalyser test)
	{
		return test.analyse(this);	
	}
	
	/**
	 * Add an experiment to this CoreLog. All the experiments that
	 * have been added will be run when the runExperiments() method 
	 * is called.
	 * 
	 * @param entry to add to this CoreLog
	 * @return Boolean indicating if the action occurred successfully
	 */
	public boolean addExperiment(LogAnalyser experiment)
	{
		return experiments.add(experiment);
	}
	
	/**
	 * Clear all experiments that have been added to this CoreLog.
	 */
	public void clearExperiments()
	{
		experiments.clear();
	}
	
	/**
	 * Run all experiments that have been added to this CoreLog object
	 * and return the output.
	 * 
	 * @return String of the output of the experiments appended, null if no tests.
	 */
	public String runExperiments()
	{
		//Edge cases for if log doesn't exist or doesn't have any entries
		if(experiments.size() == 0) return null;
		
		StringBuilder builder = new StringBuilder();
		for(LogAnalyser experiment : experiments)
		{
			builder.append(experiment.analyse(this)+ "\n");
		}
		
		//remove the last /n from the string
		String toReturn = builder.toString();
		return toReturn.substring(0, toReturn.length()-1);
	}
}
