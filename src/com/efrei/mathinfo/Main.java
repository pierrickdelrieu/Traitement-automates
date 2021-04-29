package com.efrei.mathinfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import com.efrei.mathinfo.automates.Automaton;
import com.efrei.mathinfo.automates.Operations;
import com.efrei.mathinfo.ihm.Menu;
import com.efrei.mathinfo.io.FileReader;

public class Main {

	public static void main(String[] args) throws IOException {
		// Creation of an Automaton instance

//		long t1 = System.currentTimeMillis();
//
//		for (int i = 1; i < 45; i++) {
//			PrintStream console = System.out;
//			File file = new File("src/com/efrei/mathinfo/traces/A1-trace" + String.valueOf(i) + ".txt");
//			
//			if (file.createNewFile()) {
//				PrintStream fileOut = new PrintStream("src/com/efrei/mathinfo/traces/A1-trace" + String.valueOf(i) + ".txt");
//				System.setOut(fileOut);
//				
//				System.out.println("Automate numéro : " + i);
//				Automaton a = FileReader.createAutomatonObject("src/com/efrei/mathinfo/files/A01-" + String.valueOf(i) + ".txt");
//				a.display();
//				Operations.minimize(a);
//
//				System.setOut(console);
//			}
//		}
		
		Menu.openMainMenu();
	}
}