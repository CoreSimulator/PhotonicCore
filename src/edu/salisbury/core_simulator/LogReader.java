package edu.salisbury.core_simulator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Class with static utility methods for reading logFiles.
 * @author timfoil
 *
 */
public class LogReader 
{	
	/**
	 * Creates a CoreLog object from a log file.
	 * Each entry in the file has the format: timeStamp sourceX sourceY destX destY
	 * Each entry in the file is separated by a newline
	 * 
	 * @throws FileNotFoundException when a file cannot be found
	 * @throws IOException when an IO error occurs when reading the given log file
	 * @param fileName of the log file to be read into a CoreLog object
	 * @return a CoreLog of the read filename
	 */
	public static CoreLog readLog(String fileName)
	{
		CoreLog logFromFile = new CoreLog();
		try {
	         BufferedReader myBufferedReader = 
	             new BufferedReader(new FileReader(fileName));
	         String line = null;
	         Pattern lastNumInLineRegex = Pattern.compile("\\s+");
	         while((line = myBufferedReader.readLine()) != null) 
	         {
	        	 String[] logData = lastNumInLineRegex.split(line);
	        	 
	        	 //Should be only 6 pieces of data in the line
	        	 if(logData.length == 6)
	        	 { 
	        		 
	        		 LogEntry entryToAdd = new LogEntry(
	        				 Integer.parseInt(logData[0]), Integer.parseInt(logData[1]), 
	        				 Integer.parseInt(logData[2]), Integer.parseInt(logData[3]), 
	        				 Integer.parseInt(logData[4]), Integer.parseInt(logData[5]));
	        		 
	        		 logFromFile.addEntry(entryToAdd);
	        	 }
	         }    
	
	         //close file
	         myBufferedReader.close();
	         
	     }
	     catch(FileNotFoundException e) {
	         System.out.println(
	             "Unable to find log file '" + 
	             fileName + "'");                
	     }
	     catch(IOException e) {
	         System.out.println("Error when attempting to read the log file '" + fileName + "'");                   

	     }
		return logFromFile;
	}
	
	/**
	 * Similar to {@link CoreLog readLog(String fileName)} but ignores repeater log entries.
	 *  
	 * @throws FileNotFoundException when a file cannot be found
	 * @throws IOException when an IO error occurs when reading the given log file
	 * @param fileName of the log file to be read into a CoreLog object
	 * @return a CoreLog of the read filename
	 */
	public static CoreLog readLogIgnoreRepeaters(String fileName)
	{
		CoreLog logFromFile = new CoreLog();
		try 
		{
	         BufferedReader myBufferedReader = 
	             new BufferedReader(new FileReader(fileName));
	         String line = null;
	         Pattern lastNumInLineRegex = Pattern.compile("\\s+");
	         while((line = myBufferedReader.readLine()) != null) 
	         {
	        	 String[] logData = lastNumInLineRegex.split(line);
	        	 
	        	 //Should be only 6 pieces of data in the line
	        	 if(logData.length == 6)
	        	 { 
	        		 LogEntry entryToAdd = new LogEntry(
	        				 Integer.parseInt(logData[0]), Integer.parseInt(logData[1]), 
	        				 Integer.parseInt(logData[2]), Integer.parseInt(logData[3]), 
	        				 Integer.parseInt(logData[4]), Integer.parseInt(logData[5]));
	        		 
	        		 if(entryToAdd.destX() != 0 && entryToAdd.sourceX() != 0) 
	        		 { 
	        			 logFromFile.addEntry(entryToAdd);
	        		 }
	        	 }
	         }    
	
	         //close file
	         myBufferedReader.close();
	         
	     }
	     catch(FileNotFoundException e) 
	     {
	         System.out.println(
	             "Unable to find log file '" + 
	             fileName + "'");                
	     }
	     catch(IOException e) 
	     {
	         System.out.println("Error when attempting to read the log file '" + fileName + "'");                   
	     }
		return logFromFile;
	}

	
}
