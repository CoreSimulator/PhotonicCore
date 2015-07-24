package edu.salisbury.photonic.core_simulator;

public class SuperUltimateSweetestSuite extends AnalyzerSuite {

		/**
		 * Constructs a FlitAnalyzerSuite. With resultEntriesPerRow equal to 2.
		 */
		public SuperUltimateSweetestSuite()
		{
			suite.add(new CommunicationAnalyzerSuite());
			suite.add(new FlitAnalyzerSuite());
			suite.add(new FlitPairAnalyzerSuite());
			suite.add(new PairAnalyzerSuite());
		}
		
		/**
		 * Constructor for FlitAnalyzerSuite
		 * @param resultEntriesPerRow for the resulting {@code String} of the experiment
		 */
		public SuperUltimateSweetestSuite(int resultEntriesPerRow)
		{
			suite.add(new CommunicationAnalyzerSuite(resultEntriesPerRow));
			suite.add(new FlitAnalyzerSuite(resultEntriesPerRow));
			suite.add(new FlitPairAnalyzerSuite(resultEntriesPerRow));
			suite.add(new PairAnalyzerSuite(resultEntriesPerRow));
		}

}
