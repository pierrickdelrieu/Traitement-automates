package com.efrei.mathinfo.ihm;
import com.efrei.mathinfo.automates.Automaton;
import java.io.File;
import java.util.Scanner;

public class Menu {
    public static int choice_automaton;
    public static int choice_operation;
    public final static String header = "****************************\n";

    public final static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            }
            else {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static void openHomeMenu() {
        File directory = new File("src/com/efrei/mathinfo/files");

        if(directory.listFiles() != null) {
            int file_counter = 0;
            do {
                clearConsole();
                System.out.printf(header);
                System.out.printf("              HOME\n\n");
                System.out.printf("Saisir 0 pour quitter\n");
                System.out.printf("Choix de l'automate :\n");

                file_counter = directory.listFiles().length;
                for (int i=0; i<file_counter; i++) {
                    System.out.printf((i+1) + " - " + directory.listFiles()[file_counter-i-1].getName() + "\n");
                }

                Scanner sc = new Scanner(System.in);
                System.out.println("Veuillez saisir un numéro :");
                choice_automaton = Integer.parseInt(sc.nextLine());
                System.out.println("Vous avez saisi : " + choice_automaton);
            }while(((choice_automaton < 0) || (choice_automaton>file_counter)) && (choice_automaton != 0));
        }
        else {
            System.out.printf("Erreur lors de la lecture dans le dossier files");
        }


    }

    public static void openOperationsMenu() {
        do {
            System.out.printf("Quelle opération souhaitez-vous faire ?");
            System.out.printf("1. Complétion\n" +
                    "2. Standardisation\n" +
                    "3. Déterminisation\n" +
                    "4. Minimisation\n" +
                    "5. Synchronisation\n\n" +
                    "6. Retour");
            Scanner sc = new Scanner(System.in);
            System.out.println("Veuillez saisir le numéro de l'opération : ");
            choice_operation = Integer.parseInt(sc.nextLine());
        }


    }

}
