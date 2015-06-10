package edu.salisbury.core_simulator;

/**
 * An abstract class which can be subclassed to implement {@link String analyse(CoreLog analysis)}.
 * LogAnalysers can be run by CoreLog
 * @author tptravitz
 *
 */
public abstract class LogAnalyser 
{
	public abstract String analyse(CoreLog analysis);
}
