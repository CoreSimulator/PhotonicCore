package edu.salisbury.photonic.core_simulator;

import java.util.ArrayList;

import edu.salisbury.photonic.log_analyisis.Analyzer;

/**
 * An object that represents a log file that supervises communications between cores.
 * CoreLogs can be created by adding entries to an empty log or by loading a log from
 * a file using {@link LogReader}
 * @author timfoil
 *
 */
public class CoreLog 
{
	private ArrayList<LogEntry> log;
	private ArrayList<Analyzer> experiments = new ArrayList<Analyzer>();
	
	/**
	 * Constructor for a CoreLog.
	 * 
	 */
	public CoreLog()
	{
		log = new ArrayList<LogEntry>();
	}
	
	/**
	 * Constructor for a coreLog. Takes a parameter which serves as an estimate for the
	 * size of the coreLog.
	 * 
	 * @param estimatedSize
	 */
	public CoreLog(int estimatedSize)
	{
		log = new ArrayList<LogEntry>(estimatedSize);
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
		
		//remove the last "/n" from the string
		return builder.delete(builder.length()-1, builder.length()).toString();
		
	}
	
	/**
	 * Takes a log and returns a string in a format that can be read by LogReader.
	 * 
	 * @return a string in a format that can be read by LogReader
	 */
	public String toLogFormat()
	{
		//Edge cases for if log doesn't exist or doesn't have any entries
		if(log == null) return null;
		else if(log.size() == 0) return "";
		
		StringBuilder builder = new StringBuilder();
		for(LogEntry entry : log)
		{
			
			builder.append(entry.timeStamp()).append(" ");
			builder.append(entry.sourceX()).append(" ");
			builder.append(entry.sourceY()).append(" ");
			builder.append(entry.destX()).append(" ");
			builder.append(entry.destY()).append(" ");
			builder.append(entry.packetSize()).append("\n");
		}
		
		//remove the last "/n" from the string
		return builder.delete(builder.length()-1, builder.length()).toString();
	}
	
	/**
	 * Makes a coreLog from the given indices of the current CoreLog.
	 * 
	 * @param beginIndex the beginning of the coreLog subArray (inclusive)
	 * @param endIndex the end of the coreLog subArray (exclusive)
	 * @return A subLog specified by the given indices
	 */
	public CoreLog subLog(int beginIndex, int endIndex)
	{
		if(beginIndex < 0 || endIndex < 0)
		{
			throw new ArrayIndexOutOfBoundsException("This method requires non-negative indices."); 
		} 
		else if(beginIndex > this.logSize() || endIndex > this.logSize())
		{
			throw new ArrayIndexOutOfBoundsException("One of the indeces are greater than this" +
					"log's size."); 
		} 
		else if(beginIndex > endIndex)
		{
			throw new IllegalArgumentException("This method requires the beginIndex to be smaller" +
					"than endIndex");
		}
		
		CoreLog subLog = new CoreLog(endIndex - beginIndex);
		for(int i = beginIndex; i < endIndex; i++)
		{
			subLog.addEntry(this.getEntry(i));
		}
		
		return subLog;
	}
	
	/**
	 * Makes a coreLog from the given indices of the current CoreLog. Adjusts timestamps so that
	 * the first log entry's timestamp will occur at 0 and every entry thereafter will have their
	 * entry's timestamps adjusted accordingly.
	 * 
	 * @param beginIndex the beginning of the coreLog subArray (inclusive)
	 * @param endIndex the end of the coreLog subArray (exclusive)
	 * @return A subLog specified by the given indices
	 */
	public CoreLog subLogAdjustTimeStamps(int beginIndex, int endIndex)
	{
		if(beginIndex < 0 || endIndex < 0)
		{
			throw new ArrayIndexOutOfBoundsException("This method requires non-negative indices."); 
		} 
		else if(beginIndex > this.logSize() || endIndex > this.logSize())
		{
			throw new ArrayIndexOutOfBoundsException("One of the indeces are greater than this" +
					"log's size."); 
		} 
		else if(beginIndex > endIndex)
		{
			throw new IllegalArgumentException("This method requires the beginIndex to be smaller" +
					"than endIndex");
		}
		
		CoreLog subLog = new CoreLog(endIndex - beginIndex);
		int specifiedAmount = subLog.getEntry(beginIndex).timeStamp();
		for(int i = beginIndex; i < endIndex; i++)
		{
			LogEntry currentEntry = this.getEntry(i);
			subLog.addEntry(new LogEntry(currentEntry.timeStamp() - specifiedAmount, 
					currentEntry.sourceX(), currentEntry.sourceY(), currentEntry.destX(), 
					currentEntry.destY(), currentEntry.packetSize()));
		}
		
		return subLog;
	}
	
	/**
	 * Performs the specified LogAnalyser test on this CoreLog.
	 * 
	 * @param test to run on this Log.
	 * @return The results of the test
	 */
	public String performAnalysis(Analyzer test)
	{
		return test.analyze(this);	
	}
	
	/**
	 * Add an experiment to this CoreLog. All the experiments that
	 * have been added will be run when the runExperiments() method 
	 * is called.
	 * 
	 * @param test to add to this CoreLog
	 * @return Boolean indicating if the action occurred successfully
	 */
	public boolean addExperiment(Analyzer test)
	{
		return experiments.add(test);
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
		for(Analyzer experiment : experiments)
		{
			builder.append(experiment.analyze(this)+ "\n\n");
		}
		
		//remove the last "/n/n" from the string
		return builder.delete(builder.length() - 2, builder.length()).toString();
	}
}
