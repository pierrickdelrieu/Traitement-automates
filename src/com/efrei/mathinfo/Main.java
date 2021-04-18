package com.efrei.mathinfo;

import java.io.FileNotFoundException;
import java.util.Scanner;

import com.efrei.mathinfo.automates.Automaton;
import com.efrei.mathinfo.io.FileReader;
import com.efrei.mathinfo.automates.Operations;

public class Main {

	public static void main(String[] args) {
		try {
			// Creation of an Automaton instance
			Automaton a = FileReader.createAutomatonObject("src/com/efrei/mathinfo/automaton.txt");
			a.display();
			Operations.complete(a);

//			a.display();
//			Operations.standardize(a);
//			System.out.println("standard -- ");
//			System.out.println(a);
//
//			Operations.determinize(a);
//			Operations.complete(a);
//
			Automaton b = Operations.getComplementary(a.clone());

			Scanner s = new Scanner(System.in);

			String word = " ";

			do {
				System.out.println("Entrez un mot : ");
				word = s.nextLine();
				System.out.println("Mot saisi : " + word);
				System.out.println(a.recognizesWord(word));
				System.out.println(b.recognizesWord(word));

				a.display();
				b.display();
			} while(!word.equalsIgnoreCase("fin"));

		
		} catch (FileNotFoundException e) { // Error handling
			e.printStackTrace();
		}
	}
}
