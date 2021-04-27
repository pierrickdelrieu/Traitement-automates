package com.efrei.mathinfo.ihm;

import com.efrei.mathinfo.automates.Automaton;
import com.efrei.mathinfo.automates.Operations;
import com.efrei.mathinfo.io.FileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Menu {

	private static Automaton currentAutomaton;
	private static final String header = "****************************\n";
	private static final String path = "src/com/efrei/mathinfo";
	private static final Scanner sc = new Scanner(System.in);

	public static void openMainMenu() throws FileNotFoundException {
		File dir = new File(path + "/files");
		boolean onMenu = true;
		int numFiles = dir.listFiles().length;

		while (onMenu) {
			log(header);
			log("Il y a " + numFiles + " automate(s) dans le dossier, choisissez votre automate: \n");
			log("Entrez q pour quitter\n");
			log("Votre choix : ");

			if (sc.hasNextInt()) {
				int choice = sc.nextInt();

				if (choice > numFiles || choice < 0) {
					clearConsolRun();
					log("Cet automate n'existe pas");
					continue;
				} else {
					currentAutomaton = FileReader
							.createAutomatonObject(path + "/files/A01-" + String.valueOf(choice) + ".txt");
					clearConsolRun();
					openOperationsMenu();

				}
			}

			else {

				if (sc.nextLine().equalsIgnoreCase("q")) {
					onMenu = false;
					break;
				}

				else {
					log("Veuillez entrer un nombre entre 1 et " + numFiles);
				}
			}
		}

		sc.close();
	}

	public static void openOperationsMenu() {
		boolean onMenu = true;
		int choice;

		while (onMenu) {
			log(header + "\n");
			log("Quel op�ration souhaitez-vous faire ?\n");
			log("1. Standardisation\n2. Compl�tion\n3. D�terminisation\n4. Minimisation\n5. Lecture de mot\n6. Compl�mentaire\n\nr. Retour\n");
			log("Votre entr�e : ");

			if (sc.hasNextInt()) {
				choice = sc.nextInt();
				clearConsolRun();

				switch (choice) {
				case 1:
					Operations.standardize(currentAutomaton);
					break;
				case 2:
					Operations.complete(currentAutomaton);
					break;
				case 3:
					Operations.determinize(currentAutomaton);
					break;
				case 4:
					Operations.minimize(currentAutomaton);
					break;
				case 5:
					openWordMenu();
					break;
				case 6:
					Operations.getComplementary(currentAutomaton);
					break;
				default:
					log("Op�ration non reconnue.");
					break;
				}
			}
			
			else if (sc.hasNextLine()){
				if (sc.nextLine().equalsIgnoreCase("r")) {
					onMenu = false;
					clearConsolRun();
				}
				
				else {
					clearConsolRun();
					log("Veuillez entrer un chiffre entre 1 et 6 (ou r pour retourner en arri�re)\n");
				}
			}
		}
	}

	public static void openWordMenu() {

		System.out.println("Saisissez votre mot � v�rifier, �crire fin pour arr�ter.");
		String word;
		boolean onMenu = true;

		while (onMenu) {
			
			log("Entrez votre mot : ");
			word = sc.nextLine();
			clearConsolRun();
			
			if (word.equalsIgnoreCase("fin")) {
				onMenu = false;
				clearConsolRun();
			}
			
			if (currentAutomaton.recognizesWord(word) == true) {
				log(word + " est reconnu par l'automate.");
			} else {
				log(word + " n'est pas reconnu par l'automate.");
			}
		} 
	}

	public static void log(String str) {
		System.out.printf(str);
	}

	public static void clearConsolRun() {
		for(int i = 0; i < 80*300; i++) // Default Height of cmd is 300 and Default width is 80
			System.out.print("\n"); // Prints a backspace
	}
}
