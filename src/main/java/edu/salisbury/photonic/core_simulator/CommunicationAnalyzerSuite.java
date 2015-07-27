package edu.salisbury.photonic.core_simulator;

/**
 * Subclass of {@link AnalyzerSuite} an accumulation of communication {@link Analyzer} classes.
 * 
 * <p> This suite includes the following Analyzers:</p>
 * <ul><li> {@link SenderAnalyzer}
 * <li> {@link ReceiverAnalyzer}
 * <li> {@link CumulativeIOAnalyzer}</ul>
 * 
 * @author timfoil
 * 
 */
public class CommunicationAnalyzerSuite extends AnalyzerSuite 
{
	/**
	 * Constructs a CommunicationAnalyzerSuite. With resultEntriesPerRow equal to 2.
	 */
	public CommunicationAnalyzerSuite()
	{
		suite.add(new SenderAnalyzer());
		suite.add(new ReceiverAnalyzer());
		suite.add(new CumulativeIOAnalyzer());
	}
	
	/**
	 *  Constructor for CommunicationAnalyzerSuite
	 * @param resultEntriesPerRow for the resulting {@code String} of the experiment
	 */
	public CommunicationAnalyzerSuite(int resultEntriesPerRow)
	{
		suite.add(new SenderAnalyzer(resultEntriesPerRow));
		suite.add(new ReceiverAnalyzer(resultEntriesPerRow));
		suite.add(new CumulativeIOAnalyzer(resultEntriesPerRow));
	}
}
