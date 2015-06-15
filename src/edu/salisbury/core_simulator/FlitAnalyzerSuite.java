package edu.salisbury.core_simulator;

/**
 * Subclass of {@link AnalyzerSuite} an accumulation of communication {@link Analyzer} classes.
 * 
 * <p> This suite includes the following Analyzers:</p>
 * <ul><li> {@link FlitSenderAnalyzer}
 * <li> {@link FlitReceiverAnalyzer}
 * <li> {@link CumulativeIOFlitAnalyzer}</ul>
 * 
 * @author timfoil
 * 
 */
public class FlitAnalyzerSuite extends AnalyzerSuite
{
	/**
	 * Constructs a FlitAnalyzerSuite. With resultEntriesPerRow equal to 2.
	 */
	public FlitAnalyzerSuite()
	{
		suite.add(new FlitSenderAnalyzer());
		suite.add(new FlitReceiverAnalyzer());
		suite.add(new CumulativeIOFlitAnalyzer());
	}
	
	/**
	 * Constructor for FlitAnalyzerSuite
	 * @param resultEntriesPerRow for the resulting {@code String} of the experiment
	 */
	public FlitAnalyzerSuite(int resultEntriesPerRow)
	{
		suite.add(new FlitSenderAnalyzer(resultEntriesPerRow));
		suite.add(new FlitReceiverAnalyzer(resultEntriesPerRow));
		suite.add(new CumulativeIOFlitAnalyzer(resultEntriesPerRow));
	}
}
