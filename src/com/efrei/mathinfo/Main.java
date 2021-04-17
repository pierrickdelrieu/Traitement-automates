package com.efrei.mathinfo;

import java.io.FileNotFoundException;

import com.efrei.mathinfo.automates.Automaton;
import com.efrei.mathinfo.automates.Operations;
import com.efrei.mathinfo.automates.State;
import com.efrei.mathinfo.io.FileReader;

public class Main {

	public static void main(String[] args) {
		try {
			// Creation of an Automaton instance
			Automaton a = FileReader.createAutomatonObject("src/com/efrei/mathinfo/automaton.txt");
   			a.display();
			//Operations.standardize(a);
			//System.out.println("standard -- ");
			//System.out.println(a);
   			
			Operations.determinize(a);
			Operations.complete(a);
			Operations.standardize(a);

			Automaton complement = Operations.getComplementary(a);
 			
			//Automaton b = Operations.getComplementary(a);
			//System.out.println(b);
		} catch (FileNotFoundException e) { // Error handling
			e.printStackTrace();
		}
	}
}
