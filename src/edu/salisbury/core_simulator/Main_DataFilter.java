package edu.salisbury.core_simulator;

import edu.salisbury.photonic.core_simulator.CoreLog;
import edu.salisbury.photonic.core_simulator.LogReader;
import edu.salisbury.photonic.core_simulator.NonDirectionalPairAnalyzer;

//import java.util.ArrayList;

public class Main_DataFilter {

	public static final String FILENAME = "flow_barnes.log";
	
	/**
	 * @param args
	 */	
	public static void main(String[] args) {
		
		CoreLog coreLog = new CoreLog();
		coreLog = LogReader.readLogIgnoreRepeaters(FILENAME);
		NonDirectionalPairAnalyzer dominantFlow = new NonDirectionalPairAnalyzer();
		
		CoreLog firstHalf = new CoreLog();
		firstHalf = coreLog.subLog(0, coreLog.logSize()/2 - 1);
		System.out.println("The first half of the barnes log dominant flows: ");
		System.out.println(dominantFlow.analyze(firstHalf));
		
		CoreLog secondHalf = new CoreLog();
		secondHalf = coreLog.subLog(coreLog.logSize()/2, coreLog.logSize() - 1);
		System.out.println("\nThe second half of the barnes log dominant flows: ");
		System.out.println(dominantFlow.analyze(secondHalf));
		
		CoreLog firstForth = new CoreLog();
		firstForth = coreLog.subLog(0, coreLog.logSize()/4 - 1);
		System.out.println("\nThe first forth of the barnes log dominant flows: ");
		System.out.println(dominantFlow.analyze(firstForth));
		
		CoreLog secondForth = new CoreLog();
		secondForth = coreLog.subLog(coreLog.logSize()/4, coreLog.logSize()/2 - 1);
		System.out.println("\nThe second forth of the barnes log dominant flows: ");
		System.out.println(dominantFlow.analyze(secondForth));
		
		CoreLog thirdForth = new CoreLog();
		thirdForth = coreLog.subLog(coreLog.logSize()/2, coreLog.logSize()*3/4 - 1);
		System.out.println("\nThe third forth of the barnes log dominant flows: ");
		System.out.println(dominantFlow.analyze(thirdForth));
		
		CoreLog lastForth = new CoreLog();
		lastForth = coreLog.subLog(coreLog.logSize()*3/4, coreLog.logSize() - 1);
		System.out.println("\nThe last forth of the barnes log dominant flows: ");
		System.out.println(dominantFlow.analyze(lastForth));
		
	}//end main
}
