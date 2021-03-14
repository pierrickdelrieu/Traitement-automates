package com.efrei.mathinfo;

import java.io.FileNotFoundException;

import com.efrei.mathinfo.automates.Automate;
import com.efrei.mathinfo.automates.State;
import com.efrei.mathinfo.files.FileReader;

public class Main {

	public static void main(String[] args) {
		try {
			Automate a = FileReader.createAutomateObject("/Users/benjaminlesieux/OneDrive/Bureau/git/Traitement-automates/com/efrei/mathinfo/test.txt");
			
			System.out.println(a);
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
