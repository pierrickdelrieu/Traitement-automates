package com.efrei.mathinfo;

import java.io.FileNotFoundException;
import java.util.Arrays;

import com.efrei.mathinfo.automates.Automaton;
import com.efrei.mathinfo.automates.Identifier;
import com.efrei.mathinfo.automates.Operations;
import com.efrei.mathinfo.io.FileReader;

public class Main {

	public static void main(String[] args) {
		try {
			// Creation of an Automaton instance
			Automaton a = FileReader.createAutomatonObject("src/com/efrei/mathinfo/automaton.txt");
			a.display();

			
			Operations.minimize(a);			
			
//			Operations.complete(a);
//			
//			// TODO: fix minimization
//			Operations.minimize(a);
			
//			Automaton b = Operations.getComplementary(a.clone());
//			Scanner s = new Scanner(System.in);
//
//			String word = " ";
//
//			do {
//				System.out.println("Entrez un mot : ");
//				word = s.nextLine();
//				System.out.println("Mot saisi : " + word);
//				System.out.println("a : " + a.recognizesWord(word));
//				System.out.println("b : " + b.recognizesWord(word));
//
//			} while(!word.equalsIgnoreCase("fin"));
//			
//			System.out.println("Vous avez fini !");

		
		} catch (FileNotFoundException e) { // Error handling
			e.printStackTrace();
		}
	}
}
