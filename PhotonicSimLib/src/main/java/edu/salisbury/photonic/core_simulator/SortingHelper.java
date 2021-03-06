package edu.salisbury.photonic.core_simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.salisbury.photonic.log_analyisis.Analyzer;

/**
 * This class contains various sorting methods which assist in
 * organizing data to display for {@link Analyzer} subclasses.
 * @author timfoil
 *
 */
public class SortingHelper 
{
	/**
	 * An interface for those wishing to use binaryInsertionSort and reverse binaryInsertionSort,
	 * on classes other than just Integers. Implement this interface on the class you wish to be 
	 * sortable and have it return its value to sort by using getSortableVal()
	 * @author timfoil
	 */
	public static interface SortableValue
	{
		public abstract Integer getSortableVal();
	}

	
	/**
	 * An implementation of binary insertion sort which sorts ints into an already sorted 
	 * {@link List}.
	 * @param sortedList which the integer is to be inserted into
	 * @param toSort integer to sort
	 * @return the index at which the int was inserted at in the given sortedList
	 */
	public static int binaryInsertionSort(List<Integer> sortedList, Integer toSort)
	{
		if(sortedList == null || toSort == null) throw new NullPointerException();
		
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
				} 
				else if(toSort <= sortedList.get(mid))
				{
					sortedList.add(mid, toSort);
					return mid;
				}
				else
				{
					throw new RuntimeException("Error Binary sort implementation");
				}
			} 
			else if (min < max) 
			{
				if( toSort.intValue() == sortedList.get(mid))
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
				else
				{
					throw new RuntimeException("Error Binary sort implementation");
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
	 * An implementation of binary insertion sort which sorts ints in reverse order into an already 
	 * sorted {@link List}.
	 * @param sortedList which the integer is to be inserted into
	 * @param toSort integer to sort
	 * @return the index at which the int was inserted at in the given sortedList
	 */
	public static int reverseBinaryInsertionSort(List<Integer> sortedList, Integer toSort)
	{
		if(sortedList == null || toSort == null) throw new NullPointerException();
		
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
					sortedList.add(mid, toSort);
					return mid;
				} 
				else if(toSort <= sortedList.get(mid))
				{
					sortedList.add(mid + 1, toSort);
					return mid + 1;
				}
				else
				{
					throw new RuntimeException("Error in binary sort implementation");
				}
			} 
			else if (min < max) 
			{
				if( toSort.intValue() == sortedList.get(mid))
				{
					sortedList.add(mid, toSort);
					return mid;
				} 
				else if(toSort < sortedList.get(mid))
				{
					min = mid + 1;
				}
				else if(toSort > sortedList.get(mid))
				{
					max = mid - 1;
				}
				else
				{
					throw new RuntimeException("Error Binary sort implementation");
				}
			} 
			else if (min > max)
			{
				sortedList.add(min, toSort);
				return min;
			}
			else
			{
				throw new RuntimeException("Error Binary sort implementation");
			}
		}
	}

	
	/**
	 * An implementation of binary insertion sort which sorts ints into an already sorted 
	 * {@link List}.
	 * @param sortedList which the integer is to be inserted into
	 * @param toSort integer to sort
	 * @return the index at which the int was inserted at in the given sortedList
	 */
	public static <T extends SortableValue> int binaryInsertionSort(
			List<T> sortedList, T toSort)
	{
		if(sortedList == null || toSort == null || toSort.getSortableVal() == null) 
		{
			throw new NullPointerException();
		}
		
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
				if( toSort.getSortableVal() > sortedList.get(mid).getSortableVal())
				{
					sortedList.add(mid + 1, toSort);
					return mid + 1;
				} 
				else if(toSort.getSortableVal() <= sortedList.get(mid).getSortableVal())
				{
					sortedList.add(mid, toSort);
					return mid;
				}
				else
				{
					throw new RuntimeException("Error Binary sort implementation");
				}
			} 
			else if (min < max) 
			{
				if( toSort.getSortableVal().intValue() == sortedList.get(mid).getSortableVal())
				{
					sortedList.add(mid, toSort);
					return mid;
				} 
				else if(toSort.getSortableVal() > sortedList.get(mid).getSortableVal())
				{
					min = mid + 1;
				}
				else if(toSort.getSortableVal() < sortedList.get(mid).getSortableVal())
				{
					max = mid - 1;
				}
				else
				{
					throw new RuntimeException("Error Binary sort implementation");
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
	 * An implementation of binary insertion sort which sorts SortableValues in reverse order into 
	 * an already sorted {@link List}.
	 * @param sortedList which the integer is to be inserted into
	 * @param toSort integer to sort
	 * @return the index at which the int was inserted at in the given sortedList
	 */
	public static <T extends SortableValue> int reverseBinaryInsertionSort(
			List<T> sortedList, T toSort)
	{
		if(sortedList == null || toSort == null || toSort.getSortableVal() == null) 
		{
			throw new NullPointerException();
		}
		
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
				if( toSort.getSortableVal() > sortedList.get(mid).getSortableVal())
				{
					sortedList.add(mid, toSort);
					return mid;
				} 
				else if(toSort.getSortableVal() <= sortedList.get(mid).getSortableVal())
				{
					sortedList.add(mid + 1, toSort);
					return mid + 1;
				}
				else
				{
					throw new RuntimeException("Error in binary sort implementation");
				}
			} 
			else if (min < max) 
			{
				if( toSort.getSortableVal().intValue() == sortedList.get(mid).getSortableVal())
				{
					sortedList.add(mid, toSort);
					return mid;
				} 
				else if(toSort.getSortableVal() < sortedList.get(mid).getSortableVal())
				{
					min = mid + 1;
				}
				else if(toSort.getSortableVal() > sortedList.get(mid).getSortableVal())
				{
					max = mid - 1;
				}
				else
				{
					throw new RuntimeException("Error Binary sort implementation");
				}
			} 
			else if (min > max)
			{
				sortedList.add(min, toSort);
				return min;
			}
			else
			{
				throw new RuntimeException("Error Binary sort implementation");
			}
		}
	}
	
	
	/**
	 * Sorts a {@link HashMap} with any key and an {@link Integer} value sequentially into an 
	 * {@link List} of {@link java.util.Map.Entry Map.Entry}s.
	 * @param <K> Key type for the given HashMap
	 * @param mapToSort
	 * @return A sorted ArrayList containing map
	 */
	public static <K> List<Map.Entry<K, Integer>> SortHashMapByValue(HashMap<K, Integer> mapToSort) 
	{
		
		//Create a list to model where each entry is, use binaryInsertionSortMethod 
		//to add entries to their correct Index the Hashmap's Integer values are 
		//represented by the integers at each index
		List<Integer> valueList = new ArrayList<>();
		
		//ArrayList which will hold the sorted HashMap
		List<Map.Entry<K, Integer>> sortedMapList = new ArrayList<>();
		
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
