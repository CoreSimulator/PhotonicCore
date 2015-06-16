package edu.salisbury.core_simulator;

import edu.salisbury.core_simulator.Analyzer;
import edu.salisbury.core_simulator.AnalyzerSuite;

/**
 * Subclass of {@link AnalyzerSuite} an accumulation of communication {@link Analyzer} classes.
 * 
 * <p> This suite includes the following Analyzers:</p>
 * <ul><li> {@link DirectionalPairAnalyzer}
 * <li> {@link NonDirectionalPairAnalyzer}</ul>
 * 
 * @author timfoil
 * 
 */
public class PairAnalyzerSuite extends AnalyzerSuite 
{
	/**
	 * Constructs a PairAnalyzerSuite. With resultEntriesPerRow equal to 2.
	 */
	public PairAnalyzerSuite()
	{
		suite.add(new DirectionalPairAnalyzer());
		suite.add(new NonDirectionalPairAnalyzer());
	}
	
	/**
	 *  Constructor for PairAnalyzerSuite
	 * @param resultEntriesPerRow for the resulting {@code String} of the experiment
	 */
	public PairAnalyzerSuite(int resultEntriesPerRow)
	{
		suite.add(new DirectionalPairAnalyzer(resultEntriesPerRow));
		suite.add(new NonDirectionalPairAnalyzer(resultEntriesPerRow));
	}
}
