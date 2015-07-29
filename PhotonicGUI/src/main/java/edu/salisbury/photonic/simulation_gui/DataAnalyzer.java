package edu.salisbury.photonic.simulation_gui;

import edu.salisbury.photonic.core_simulator.CoreLog;
import edu.salisbury.photonic.core_simulator.LogReader;
import edu.salisbury.photonic.log_analyisis.NonDirectionalPairAnalyzer;

public class DataAnalyzer {
	
	public static CoreLog coreLog = new CoreLog();
	public static NonDirectionalPairAnalyzer dominantFlow = new NonDirectionalPairAnalyzer();
	
	public static void analyzeDominantFlow(int startingIndex, int endingIndex, String fileName) {
		coreLog = LogReader.readLogIgnoreRepeaters(fileName);
		CoreLog specifiedSection = new CoreLog();
		specifiedSection = coreLog.subLog(startingIndex, endingIndex);
		String message = "The dominant flows for the section (" + startingIndex + "," + endingIndex + ") of this log: \n";
		MainGUI.printToConsoleDA(message + dominantFlow.analyze(specifiedSection));
	}
	
	public static void analyzeDominantFlow(int numberOfSections, String fileName) {
		coreLog = LogReader.readLogIgnoreRepeaters(fileName);
		String message;
		switch(numberOfSections) {
			case 1:
				message = "The dominant flow enumration across the full log: \n";
				MainGUI.printToConsoleDA(message + dominantFlow.analyze(coreLog));
				break;
			case 2:
				CoreLog firstHalf = new CoreLog();
				firstHalf = coreLog.subLog(0, coreLog.logSize()/2 - 1);
				message = "The first half of this log's dominant flows: \n";
				message += dominantFlow.analyze(firstHalf);
				
				CoreLog secondHalf = new CoreLog();
				secondHalf = coreLog.subLog(coreLog.logSize()/2, coreLog.logSize() - 1);
				message += "\n\nThe second half of this log's dominant flows: \n";
				MainGUI.printToConsoleDA(message + dominantFlow.analyze(secondHalf));
				break;
			case 4:
				CoreLog firstForth = new CoreLog();
				firstForth = coreLog.subLog(0, coreLog.logSize()/4 - 1);
				message = "The first forth of this log's dominant flows: \n";
				message += dominantFlow.analyze(firstForth);
				
				CoreLog secondForth = new CoreLog();
				secondForth = coreLog.subLog(coreLog.logSize()/4, coreLog.logSize()/2 - 1);
				message += "\n\nThe second forth of this log's dominant flows: \n";
				message += dominantFlow.analyze(secondForth);
				
				CoreLog thirdForth = new CoreLog();
				thirdForth = coreLog.subLog(coreLog.logSize()/2, coreLog.logSize()*3/4 - 1);
				message += "\n\nThe third forth of this log's dominant flows: \n";
				message += dominantFlow.analyze(thirdForth);
				
				CoreLog lastForth = new CoreLog();
				lastForth = coreLog.subLog(coreLog.logSize()*3/4, coreLog.logSize() - 1);
				message += "\n\nThe last forth of this log's dominant flows: \n";
				MainGUI.printToConsoleDA(message + dominantFlow.analyze(lastForth));
				break;
			default:
				//throw new ...
		}//end switch-case
	}
	
}//end class
