package edu.salisbury.photonic.log_analyisis;


/**
 * Subclass of {@link AnalyzerSuite} an accumulation of communication {@link Analyzer} classes.
 * 
 * <p> This suite includes the following Analyzers:</p>
 * <ul><li> {@link DirectionalFlitPairAnalyzer}
 * <li> {@link NonDirectionalFlitPairAnalyzer}</ul>
 * 
 * @author timfoil
 * 
 */
public class FlitPairAnalyzerSuite extends AnalyzerSuite 
{
	/**
	 * Constructs a FlitPairAnalyzerSuite. With resultEntriesPerRow equal to 2.
	 */
	public FlitPairAnalyzerSuite()
	{
		suite.add(new DirectionalFlitPairAnalyzer());
		suite.add(new NonDirectionalFlitPairAnalyzer());
	}
	
	/**
	 *  Constructor for FlitPairAnalyzerSuite
	 * @param resultEntriesPerRow for the resulting {@code String} of the experiment
	 */
	public FlitPairAnalyzerSuite(int resultEntriesPerRow)
	{
		suite.add(new DirectionalFlitPairAnalyzer(resultEntriesPerRow));
		suite.add(new NonDirectionalFlitPairAnalyzer(resultEntriesPerRow));
	}
}
