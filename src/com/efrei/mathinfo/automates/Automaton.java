package com.efrei.mathinfo.automates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Automaton implements Cloneable {

	private List<State> states;
	private Alphabet alphabet;
	private int numTransitions;


	/**
	* Constructor <br>
	* Initialize all attributes to 0 or null
	*/
	public Automaton() {
		this.states = new ArrayList<State>();
		this.alphabet = null;
	}

	/**
	* Copy constructor <br>
	* Copy the entire state list, alphabet and transition number. It create new benchmarks. No reference of the copied automaton is re-used
	* @param automaton Automaton to copy
	*/
	private Automaton(Automaton automaton) {
		this.states = new ArrayList<State>();

		automaton.getStates().forEach(s -> {
			this.states.add(s.clone());
		});
		
		for (State state : this.states) {
			for (String key : state.getLinks().keySet()) {
				
				List<State> destinations = state.getLinks().get(key);
				destinations.replaceAll(s -> this.getByID(s.getID()));
			}
		}

		this.alphabet = new Alphabet(automaton.getAlphabet().getDictionary());
		this.numTransitions = Integer.valueOf(automaton.getNumTransitions());
	}

	public List<State> getStates() {
		return this.states;
	}

	public void changeStates(List<State> newStates) {
		this.states = newStates;
	}

	public void setAlphabet(Alphabet alphabet) {
		this.alphabet = alphabet;
	}

	public Alphabet getAlphabet() {
		return this.alphabet;
	}

	public void setNumTransitions(int numTransitions) {
		this.numTransitions = Integer.valueOf(numTransitions);
	}

	public int getNumTransitions() {
		return this.numTransitions;
	}

	/**
	 * The method checks if a state is already present in the PLC.
	 * 
	 * @param id Automaton identifier (example = 1 or A)
	 * @return {@code true} if the state is already present in the automaton false
	 *         if the state is not present in the automaton
	 */
	public boolean containsStateID(String id) {
		for (State state : this.states) {
			if (state.getID().equals(id)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * The method allows you to retrieve a state from its identifier
	 * 
	 * @param id State identifier
	 * @return the state with the id null if the state is not present in the
	 *         automaton
	 */
	public State getByID(String id) {

		for (State state : this.states) {
			if (state.getID().equals(id)) {
				return state;
			}
		}

		return null; // if the state is not present in the automaton
	}

	/**
	 * The method tells us which states have a particular type
	 *
	 * @param type to check if a state has it
	 * @return Array of states having the {@code StateType} type
	*/
	public State[] getStatesByType(StateType type) {

		List<State> filteredList = this.states.stream()
				.filter(state -> state.getTypes().contains(type))
				.collect(Collectors.toList());


		return filteredList.toArray(new State[0]);
	}

	/**
	 * The method tells us the states that don't have a particular type
	 *
	 * @param type to check if a state has it
	 * @return Array of states without the {@code StateType} type
	 */
	public State[] getAllStatesButType(StateType type) {
		List<State> st = new ArrayList<State>();

		for (State state : this.getStates()) {
			if (!state.getTypes().contains(type)) {
				st.add(state);
			}
		}

		return st.toArray(new State[0]);
	}

	/**
	 * The method copies the automaton
	 * @return new {@code Automaton} 
	*/
	public Automaton clone() {
		return new Automaton(this);
	}

	public void display() {
		System.out.println("Affichage de l'automate : \n");
		System.out.println(this);
	}

	/**
	 * The method tells us if he recognizes a word
	 *
	 * @param word entered by user to check
	 * @return {@code true} if the word is recognized {@code false} if the word is not recognized
	*/
	public boolean recognizesWord(String word) {

		State[] entries = this.getStatesByType(StateType.ENTRY);

		String letter;
		for (int i = 0; i < word.length(); i++) {
			letter = String.valueOf(word.charAt(i));
			if (!this.alphabet.getDictionary().contains(letter)) {
				return false;
			}
		}

		for (State entry : entries) {

			if (recognizesWordFromState(word, -1, entry)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * helper method that starts in a State to read a word 
	 *
	 * @param word entered by user to check
	 * @param index to see which character is being evaluated
	 * @param current the State in which we are
	 * @return {@code true} if final state is an exit, {@code false} otherwise 
	*/
	private boolean recognizesWordFromState(String word, int index, State current) {

		if (index == word.length() - 1) {
			return current.isExit();
		}

		else {
			String letter = String.valueOf(word.charAt(index + 1));
			List<State> destinations = current.getLinks().get(letter);
			
			if (destinations == null) {
				return false;
			}

			for (State destination : destinations) {
				
				if (destination != null && this.recognizesWordFromState(word, index + 1, destination)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public String toString() {
		String result = "";

		// Alphabet display
		result = this.alphabet.getDictionary().size() + " mots : "
				+ Arrays.toString(this.alphabet.getDictionary().toArray()) + "\n";

		// States display
		result += this.states.size() + " états : " + Arrays.toString(this.states.toArray()) + "\n";

		// Inputs displays
		State[] entries = this.getStatesByType(StateType.ENTRY);
		
		/*
		 * stream : allows you to use the filter method filter which returns a list containing
		 * the filter conditions (<type> -> <filter conditions>)
		 */
		result += entries.length + " entrées : " + Arrays.toString(entries) + "\n";

		// Output displays
		State[] exits = this.getStatesByType(StateType.EXIT);
		result += exits.length + " sorties : " + Arrays.toString(exits) + "\n";

		// Display of transitions
		result += this.numTransitions + " transitions : \n";

		for (State state : this.states) { // for each state among all states

			Set<String> allKeys = state.getLinks().keySet();

			for (String key : this.alphabet.getDictionary()) { // for each key among all keys
				if (!allKeys.contains(key)) {
					result += state.getID() + "->" + key + "->[x] \n";
				}

				else {
					result += state.getID() + "->" + key + "->" + Arrays.toString(state.getLinks().get(key).toArray())
							+ "\n";
				}
			}
		}

		return result;
	}
}
