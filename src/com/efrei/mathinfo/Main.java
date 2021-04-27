package com.efrei.mathinfo;

import java.io.FileNotFoundException;

import com.efrei.mathinfo.automates.Automaton;
import com.efrei.mathinfo.automates.Operations;
import com.efrei.mathinfo.io.FileReader;

public class Main {

	public static void main(String[] args) {
		try {
			// Creation of an Automaton instance
			
			
			long t1 = System.currentTimeMillis();
//
//			for (int i = 1; i < 45; i++) {
//				System.out.println("Automate numéro : " + i);
//				Automaton a = FileReader.createAutomatonObject("src/com/efrei/mathinfo/A01-" + String.valueOf(i) + ".txt");
//				a.display();
//				Operations.minimize(a);
//				
//				System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
//			}
//			
			//TODO automate 11 merge d'entrée ne se fait pas lors de la minimisation de l'état A -- 28  
			Automaton a = FileReader.createAutomatonObject("src/com/efrei/mathinfo/A01-36.txt");
			a.display();
			//Operations.standardize(a);
			Operations.minimize(a);
//			
//			
//			System.out.println(
//					"Execution de tous les algorithmes en " + String.valueOf(System.currentTimeMillis() - t1) + "ms");

		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		}
	}
}