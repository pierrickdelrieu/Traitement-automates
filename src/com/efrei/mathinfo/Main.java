package com.efrei.mathinfo;

import java.io.FileNotFoundException;
import java.util.Arrays;

import com.efrei.mathinfo.automates.Automaton;
import com.efrei.mathinfo.automates.Operations;
import com.efrei.mathinfo.automates.State;
import com.efrei.mathinfo.io.FileReader;

public class Main {

	public static void main(String[] args) {
		try {
			// Creation of an Automaton instance
			Automaton a = FileReader.createAutomateObject("src/com/efrei/mathinfo/automaton.txt");
			
			System.out.println(a); // Display of the automaton with the toString method
			Operations.standardize(a);
			System.out.println(a);
			
			State ab = a.getByID("0");
			State ad = a.getByID("1");
			State ac = a.getByID("2");
			State merged = Operations.mergeStates(ab, ad, ac);
			merged.setID(String.join("", ab.toString(), ad.toString(), ac.toString()));
			
			for (String key : merged.getLinks().keySet()) {
				System.out.println(merged.getID() + "->" + key + "->" + Arrays.toString(merged.getLinks().get(key).toArray()));
			}
			
		
		} catch (FileNotFoundException e) { // Error handling
			e.printStackTrace();
		}
	}
}
