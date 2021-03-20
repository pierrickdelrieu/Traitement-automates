package com.efrei.mathinfo.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.efrei.mathinfo.automates.Automate;
import com.efrei.mathinfo.automates.Language;
import com.efrei.mathinfo.automates.State;
import com.efrei.mathinfo.automates.StateType;

public class FileReader {

	public static Automate createAutomateObject(String filePath) throws FileNotFoundException {

		File file = new File(filePath);
		Scanner scanner = new Scanner(file);

		Automate automate = new Automate();
		Language language = null;
		int line = 0;
		int transitions = 0;

		while (scanner.hasNextLine()) { // read while we can
			String content = scanner.nextLine().trim(); // remove unnecessary spaces (just in case)
			String[] values = content.split(" "); // get each 'word' in an array
			
			switch (line) {
			case 0:
				language = new Language(Integer.valueOf(content)); // build the language object 
				automate.setLanguage(language);
				break;
			case 1:
				break;
			case 2:
				loadStates(content, automate, StateType.ENTRY); // load the entries
				break;
			case 3:
				loadStates(content, automate, StateType.EXIT); // load the exits 
				break;
			case 4:
				transitions = Integer.valueOf(content); // get the number of transitions 
				automate.setNumTransitions(transitions);
				break;
			default:
				String[] word = content.split("[0-9]"); // when we have '01*9', the split returns '*'

				values = content.split("[a-zA-Z*]"); // when we have '01*9', the split returns '01', '9'

				if (!automate.containsStateID(values[0])) {
					State state = new State(values[0]);
					state.addType(StateType.COMMON);
					automate.getStates().add(state);
				}

				if (!automate.containsStateID(values[1])) {
					State state = new State(values[1]);
					state.addType(StateType.COMMON);
					automate.getStates().add(state);
				}

				State state = automate.getByID(values[0]);
				State next = automate.getByID(values[1]);

				// As the split returns a list, to get the transitions we only
				// need to join it with no separators as the array is of size one
				String transitionWord = String.join("", word);

				state.addLink(transitionWord, next);

				// we add as we read the new letters we find for the alphabet 
				if (!language.getDictionary().contains(transitionWord) && !transitionWord.equals("*")) {
					language.addWord(transitionWord);
				}

				break;
			}

			line++;
		}
		
		scanner.close();

		return automate;
	}

	private static void loadStates(String content, Automate automate, StateType type) {
		String[] values = content.split(" ");
		int numStates = Integer.valueOf(values[0]); // first argument is the number of states 

		for (int i = 1; i <= numStates; i++) {
			if (!automate.containsStateID(values[i])) {
				State state = new State(values[i]);
				state.addType(type);
				automate.getStates().add(state);
			}

			else {
				automate.getByID(values[i]).addType(type);
			}
		}
	}
}
