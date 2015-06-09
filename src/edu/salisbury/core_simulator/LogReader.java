package edu.salisbury.core_simulator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

public class LogReader 
{	
	public static CoreLog readLog(String fileName)
	{
		return createFile(fileName);
	}
	
	private static CoreLog createFile(String fileName)
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
	        				 Double.parseDouble(logData[0]), Integer.parseInt(logData[1]), 
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
}
