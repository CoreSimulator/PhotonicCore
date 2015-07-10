package edu.salisbury.photonic.core_simulator;

import java.util.ArrayList;
import java.util.Map;

/**
 * An abstract class which can be sub-classed to implement {@link String analyze(CoreLog analysis)}.
 * <p>Analyzer objects can be added to {@link CoreLog} objects using {@link CoreLog#addExperiment(Analyzer)}
 * and {@link CoreLog#runExperiments()} to run as experiments. Experiments can also be simply run on a 
 * CoreLog using {@link CoreLog#performAnalysis(Analyzer)}.
 * @author tptravitz
 *
 */
public abstract class Analyzer 
{
	/** 
	 * Number of results that are displayed per row in the {@link Analyzer#sortMapEntriesByDescendingValue(ArrayList) 
	 * &ltK&gt String sortMapEntriesByDescendingValue(ArrayList &ltMap.Entry &ltK, Integer&gt&gt sortedList) } method
	 */
	public int resultEntriesPerRow = 2;
	
	public String resultDescription = "Results of Analysis";
	
	/**
	 * This method should be overridden by subclasses to implement an experiment that
	 * can be run when added to a {@link CoreLog} object
	 * @param log which will be analyzed by an implemented experiment
	 * @return the results of the experiment
	 */
	public abstract String analyze(CoreLog log);
	
	/**
	 * Takes a sorted {@link ArrayList} of {@link Map.Entry} results and formats it into 
	 * a {@code String} that can be used to describe the result of the test.
	 * 
	 * @param sortedList of map entries
	 * @return a formatted {@code string} showing the results of the program
	 */
	public <K> String sortMapEntriesByDescendingValue(ArrayList<Map.Entry<K, Integer>> sortedList)
	{
		//used to build the result string
		StringBuilder baseString = new StringBuilder();
		
		baseString.append(resultDescription + ":\n");
		
		for(int i = sortedList.size() - 1, entriesInRow = 0; i >= 0; i--)
		{
			Map.Entry<K, Integer> entry = sortedList.get(i);
			entriesInRow++;
			if(entriesInRow < resultEntriesPerRow)
			{
				baseString.append(sortedList.size() - i + ".) " + entry.getKey() + ": "+ entry.getValue());
				baseString.append(",\t");
			} 
			else
			{
				entriesInRow = 0;
				baseString.append(sortedList.size() - i + ".) " + entry.getKey() + ": "+ entry.getValue());
				baseString.append(" \n");
			}
		}
		
		//trim the last two characters off the end
		return baseString.delete(baseString.length()-2, baseString.length()).toString();
	} 
}
