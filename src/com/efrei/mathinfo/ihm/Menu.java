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
			log("Il y a " + numFiles + " automate(s) dans le dossier, choisissez votre automate: ");
			log("Entrez q pour quitter");
			log(header);
			log("Votre choix : ");

			if (sc.hasNextInt()) {
				int choice = sc.nextInt();

				if (choice > numFiles || choice < 0) {
					log("Cet automate n'existe pas");
					continue;
				}

				currentAutomaton = FileReader
						.createAutomatonObject(path + "/files/A01-" + String.valueOf(choice) + ".txt");
				openOperationsMenu();
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
			log("Quel opération souhaitez-vous faire ?\n");
			log("1. Standardisation\n2. Complétion\n3. Déterminisation\n4. Minimisation\n5. Lecture de mot\n6. Complémentaire\n\nr. Retour\n");
			log("Votre entrée : ");

			if (sc.hasNextInt()) {
				choice = sc.nextInt();

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
					log("Opération non reconnue.");
					break;
				}
			}
			
			else if (sc.hasNextLine()){
				if (sc.nextLine().equalsIgnoreCase("r")) {
					onMenu = false;
				}
				
				else {
					log("Veuillez entrer un chiffre entre 1 et 6 (ou r pour retourner en arrière)");
				}
			}
		}
	}

	public static void openWordMenu() {

		System.out.println("Saisissez votre mot à vérifier, écrire fin pour arrêter.");
		String word;
		boolean onMenu = true;

		while (onMenu) {
			
			log("Entrez votre mot : ");
			word = sc.nextLine();
			
			if (word.equalsIgnoreCase("fin")) {
				onMenu = false;
			}
			
			if (currentAutomaton.recognizesWord(word) == true) {
				log(word + " est reconnu par l'automate.");
			} else {
				log(word + " n'est pas reconnu par l'automate.");
			}
		} 
	}

	public static void log(String str) {
		System.out.println(str);
	}

	public static void clear() {

	}
}
