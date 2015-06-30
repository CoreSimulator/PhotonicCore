package edu.salisbury.photonic.core_simulator;

import java.util.ArrayList;

/**
 * A class designed to group similar {@link Analyzer}s into a single Analyzer.
 * @author timfoil
 *
 */
public abstract class AnalyzerSuite extends Analyzer 
{
	/**
	 * A list of {@link Analyzer Analyzers} that will be run when this 
	 * {@link AnalyzerSuite#analyze(CoreLog) String analyze(CoreLog log)} 
	 * is called. Add Analyzers to this list that you wish include in a
	 * sub-classed suite.
	 */
	protected ArrayList<Analyzer> suite = new ArrayList<Analyzer>();
	
	@Override
	/*
	 * Iterate through all of the Analyzers in the suite and return the formatted results.
	 * (non-Javadoc)
	 * @see edu.salisbury.core_simulator.Analyzer#analyze(edu.salisbury.core_simulator.CoreLog)
	 */
	public String analyze(CoreLog log) 
	{
		StringBuilder baseString = new StringBuilder();
		
		for(int i = 0; i < suite.size(); i++)
		{
			baseString.append(suite.get(i).analyze(log));
			baseString.append("\n\n");
		}
		
		return baseString.delete(baseString.length() - 2, baseString.length()).toString();
	}

}
