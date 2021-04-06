package com.efrei.mathinfo.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.efrei.mathinfo.automates.Automaton;
import com.efrei.mathinfo.automates.Alphabet;
import com.efrei.mathinfo.automates.State;
import com.efrei.mathinfo.automates.StateType;

public class FileReader {

	// Class method
	public static Automaton createAutomateObject(String filePath) throws FileNotFoundException {

		File file = new File(filePath); // Path to the file containing the information on the PLC (see README.md)
		Scanner scanner = new Scanner(file); // Reading variable

		Automaton automate = new Automaton();

		// Initialization
		Alphabet alphabet = null;
		int line = 0;
		int transitions = 0;

		while (scanner.hasNextLine()) { // Read while we can
			String content = scanner.nextLine().trim(); // Remove unnecessary spaces (just in case)
			String[] values = content.split(" "); // Get each 'word' in an array

			// Separation of actions for each line
			switch (line) {
			case 0: // Line 1 contains the number of words in the alphabet
				alphabet = new Alphabet(Integer.valueOf(content)); // Build the alphabet object
				automate.setAlphabet(alphabet);
				break;
			case 1: //
				break;
			case 2: // Line 3 contains the input states
				loadStates(content, automate, StateType.ENTRY); // Load the entries
				break;
			case 3: // Line 4 contains the outpu states
				loadStates(content, automate, StateType.EXIT); // Load the exits
				break;
			case 4: // Line 5 contains the automaton transition number
				transitions = Integer.valueOf(content); // Let the number of transitions
				automate.setNumTransitions(transitions);
				break;
			default: // The other lines contain the transitions
				String[] word = content.split("[0-9!-@A-Z]"); // When we have '01*9', the split returns '*'

				values = content.split("[a-z*]"); // When we have '01*9', the split returns '01', '9'

				// If the first state is not already created in the automaton
				if (!automate.containsStateID(values[0])) {
					State state = new State(values[0]);
					automate.getStates().add(state);
				}

				// If the second state is not already created in the automaton
				if (!automate.containsStateID(values[1])) {
					State state = new State(values[1]);
					automate.getStates().add(state);
				}

				State state = automate.getByID(values[0]);
				State next = automate.getByID(values[1]);

				// As the split returns a list, to get the transitions we only
				// need to join it with no separators as the array is of size one
				String transitionWord = String.join("", word);

				state.addLink(transitionWord, next);

				// we add as we read the new letters we find for the alphabet 
				if (!alphabet.getDictionary().contains(transitionWord) && !transitionWord.equals("")) {
					alphabet.addWord(transitionWord);
				}

				break;
			}

			line++;
		}
		
		scanner.close();

		return automate;
	}

	/**
	 * The method is used to load the report into the automaton
	 * @param content Contains the identifier of the PLC
	 * @param automaton Automaton in which the new state must be added
	 * @param type State type: ENTRY, COMMON, EXIT
	 */
	private static void loadStates(String content, Automaton automaton, StateType type) {
		String[] values = content.split(" ");
		int numStates = Integer.valueOf(values[0]); // First argument is the number of states

		for (int i = 1; i <= numStates; i++) {
			if (!automaton.containsStateID(values[i])) { // If the automaton is still not created
				State state = new State(values[i]);
				state.addType(type);
				automaton.getStates().add(state);
			}

			else {
				automaton.getByID(values[i]).addType(type);
			}
		}
	}
}
