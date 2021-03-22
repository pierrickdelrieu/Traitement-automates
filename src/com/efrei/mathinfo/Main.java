package com.efrei.mathinfo;

import java.io.FileNotFoundException;

import com.efrei.mathinfo.automates.Automaton;
import com.efrei.mathinfo.automates.Operations;
import com.efrei.mathinfo.io.FileReader;

public class Main {

	public static void main(String[] args) {
		try {
			// Creation of an Automaton instance
			Automaton a = FileReader.createAutomateObject("src/com/efrei/mathinfo/automaton.txt");
			
			System.out.println(a); // Display of the automaton with the toString method
			Operations.standardize(a);
			System.out.println(a);
		
		} catch (FileNotFoundException e) { // Error handling
			e.printStackTrace();
		}
	}
}
