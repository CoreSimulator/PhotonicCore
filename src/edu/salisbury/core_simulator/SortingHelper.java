package edu.salisbury.core_simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class contains various sorting methods which assist in
 * organizing data to display for {@link Analyzer} subclasses.
 * @author timfoil
 *
 */
public class SortingHelper 
{
	
	/**
	 * An implementation of binary insertion sort which sorts ints into an already sorted {@link ArrayList}.
	 * @param sortedList which the integer is to be inserted into
	 * @param toSort 
	 * @return the index at which the int was inserted at in the given sortedList
	 */
	public static int binaryInsertionSort(ArrayList<Integer> sortedList, int toSort)
	{
		if(sortedList == null) throw new NullPointerException();
		
		if(sortedList.isEmpty()) 
		{
			sortedList.add(toSort);
			return 0;
		}
			
		int min = 0;
		int max = sortedList.size() - 1;
		
		while(true)
		{
			int mid = (min + max)/2;
			if(min == max)
			{
				if( toSort > sortedList.get(mid))
				{
					sortedList.add(mid + 1, toSort);
					return mid + 1;
				} else 
				{
					sortedList.add(mid, toSort);
					return mid;
				}
			} 
			else if (min < max) 
			{
				if( toSort == sortedList.get(mid))
				{
					sortedList.add(mid, toSort);
					return mid;
				} 
				else if(toSort > sortedList.get(mid))
				{
					min = mid + 1;
				}
				else if(toSort < sortedList.get(mid))
				{
					max = mid - 1;
				}
			} 
			else if (min > max)
			{
				sortedList.add(min, toSort);
				return min;
			}
		}
		
	}

	/**
	 * Sorts a {@link HashMap} with any key and an {@link Integer} value sequentially into an 
	 * {@link ArrayList} of {@link java.util.Map.Entry Map.Entry}s.
	 * @param <K> Key type for the given HashMap
	 * @param mapToSort
	 * @return A sorted ArrayList containing map
	 */
	public static <K> ArrayList<Map.Entry<K, Integer>> SortHashMapByValue(HashMap<K, Integer> mapToSort) 
	{
		
		//Create a list to model where each entry is, use binaryInsertionSortMethod 
		//to add entries to their correct Index the Hashmap's Integer values are 
		//represented by the integers at each index
		ArrayList<Integer> valueList = new ArrayList<>();
		
		//ArrayList which will hold the sorted HashMap
		ArrayList<Map.Entry<K, Integer>> sortedMapList = new ArrayList<>();
		
		Set<Map.Entry<K, Integer>> pairs = mapToSort.entrySet();
		
		//Created an iterator to traverse through the Map Entry pairs
		Iterator<Map.Entry<K, Integer>> pairIterator = pairs.iterator();
		
		//Sort the entries by value
		while(pairIterator.hasNext())
		{
			Map.Entry<K, Integer> entry = pairIterator.next();
			
			int index = binaryInsertionSort(valueList, entry.getValue());
			sortedMapList.add(index, entry);
		}
		
		//return the sorted entries
		return sortedMapList;
	}
}
