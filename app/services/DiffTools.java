package services;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;

/**
 *******************************************************************************
 *Class responsible to provide tools used by the controller 
 *@author      Alessandro Hunhoff 
 *@author 	   ale0711@gmail.com
 *******************************************************************************
 **/

public class DiffTools {

	public DiffTools(){}

	/**
	 * Method to compare the left and right strings inputs
	 * @param left - left string that will be compared with the right
	 * @param right - right string that will be compared with the right
	 * @return LinkedList<String> - list containing offset and length (how many different chars in sequence) 
	 */
	public LinkedList<String> diffString(String left, String right){

		LinkedList<String> strList = new LinkedList<String>();
		Map<Integer, Integer> difference = new LinkedHashMap<Integer, Integer>();
		int lenght = 0, offset = 0;

		if (left.length() == right.length()) {
			for (int i = 0; i < left.length(); i++) {
				lenght=0;
				if (left.charAt(i) != right.charAt(i)) {
					for (int j = i; j < left.length(); j++) {
						if ((left.charAt(j) != right.charAt(j)) & (j+1 != left.length())) {
							lenght++;
						} else {
							if(j+1 == left.length()) lenght++;
							difference.put(i,lenght);
							i=j;
							break;
						}
					}
				}
			}	
		}

		Set<Integer> keys = difference.keySet();
		Iterator<Integer> itr = keys.iterator();
		while (itr.hasNext()) { 
			offset = itr.next();
			strList.add("Offset: "+offset+" & Lenght: "+difference.get(offset));
		}
		return strList;
	}
	
	/**
	 * Method to check if a string is base64
	 * @param stringToBeChecked - string to be checked
	 * @return boolean - true is is base64 
	 */
	public boolean checkBase64(String stringToBeChecked){
		return Base64.isBase64(stringToBeChecked.getBytes());
	}
}
