package com.efrei.mathinfo;

import java.io.FileNotFoundException;

import com.efrei.mathinfo.automates.Alphabet;
import com.efrei.mathinfo.automates.Automaton;
import com.efrei.mathinfo.io.FileReader;

public class Main {

	public static void main(String[] args) {
		try {
			// Creation of an Automaton instance
			Automaton a = FileReader.createAutomatonObject("src/com/efrei/mathinfo/automaton.txt");
//			a.display();
//			Operations.standardize(a);
//			System.out.println("standard -- ");
//			System.out.println(a);
//
//			Operations.determinize(a);
//			Operations.complete(a);
//
//			Automaton b = Operations.getComplementary(a);
		
			
			System.out.println(a.recognizesWord("abb"));
		} catch (FileNotFoundException e) { // Error handling
			e.printStackTrace();
		}
	}
}
