package com.efrei.mathinfo.automates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unused")
public class Operations {

	// No instance

	public static void determinize(Automaton automaton) {
		if (!isDeterministic(automaton)) {
			Automaton newAutomaton = new Automaton();
			List<State> newStates = new ArrayList<State>();

			Object[] entries = automaton.getStatesByType(StateType.ENTRY);

			if (entries.length > 1) {
			}
		}
	}

	public static boolean isDeterministic(Automaton automaton) {

		Object[] entries = automaton.getStatesByType(StateType.ENTRY);

		if (entries.length > 1) { // check if the automaton has more than 1 entry, if it has, then it isn't
									// deterministic
			return false;
		}

		else {
			for (State state : automaton.getStates()) { // For all the states
				for (String key : state.getLinks().keySet()) { // We check all the transitions
					if (state.getLinks().get(key).size() > 1) { // and check if one returns more than one state as its
																// destination
						return false;
					}
				}
			}
		}

		return true;
	}

	public static void complete(Automaton automaton) {

	}

	public static boolean isCompleted(Automaton automaton) {

		// we check if the automaton is deterministic, if not -> it isn't completed
		if (!isDeterministic(automaton)) {
			return false;
		}

		else {
			for (State state : automaton.getStates()) {

				int transitions = state.getLinks().keySet().size();

				// we check if it has the same number of keys as the number of letter in the
				// alphabet
				if (transitions < automaton.getAlphabet().getDictionary().size()) {
					return false;
				}
			}
		}

		return true;
	}

	public static void standardize(Automaton automaton) {
		if (!isStandard(automaton)) {
			State newEntry = new State("§");
			newEntry.addType(StateType.ENTRY);

			Object[] entries = automaton.getStatesByType(StateType.ENTRY);

			for (Object entry : entries) {
				for (String key : ((State) entry).getLinks().keySet()) {
					for (State state : ((State) entry).getLinks().get(key)) {
						newEntry.addLink(key, state);
					}
				}

				((State) entry).removeType(StateType.ENTRY);
			}

			automaton.getStates().add(newEntry);
		}
	}

	public static boolean isStandard(Automaton automaton) {

		// We are unable to convert it to a State, but it does not matter, we just need
		// the string value of the objet, which is the id of the state
		Object[] entries = automaton.getStatesByType(StateType.ENTRY);

		if (entries.length > 1) { // check if the automaton has more than 1 entry, if it has, then it isn't
									// standard
			return false;
		}

		else {

			State entry = (State) entries[0]; // We have only one entry, so we know it is at index 0

			for (State state : automaton.getStates()) { // We check all the states of the automaton
				for (String key : state.getLinks().keySet()) { // With all their transitions

					// to see if one state has a transition that brings it back to the entry
					Object[] listOfTransitions = state.getLinks().get(key).stream()
							.filter(destination -> destination.getID().equals(entry.getID())).toArray();

					if (listOfTransitions.length >= 1) {
						return false;
					}
				}
			}
		}

		return true;
	}

	public static void minimize(Automaton automaton) {

	}

	public static boolean isMinimized(Automaton automaton) {
		return false;
	}

	public static Automaton getComplementary(Automaton automaton) {
		return null;
	}

	public static State mergeStates(State... states) {

		if (states.length == 0) {
			return null;
		}

		else if (states.length == 1) {
			return states[0];
		}

		List<State> listStates = List.of(states);
		Iterator<State> itStates = listStates.iterator();

		State current = itStates.next().clone();

		while (itStates.hasNext()) {
			State next = itStates.next();

			// for each key and destinations of the next state
			// we merge into the current state by merging the oldState list and the newState
			// list
			next.getLinks().forEach((key, destinations) -> current.getLinks().merge(key, destinations,
					(oldState, newState) -> mergeLists(oldState, newState))); // this line states that oldState and
																				// newState will be merged into
																				// a new list by mergeLists(a,b)
		}
		
		return current;
	}

	public static List<State> mergeLists(List<State> l1, List<State> l2) {

		List<State> states = new ArrayList<State>();

		states.addAll(l1);

		for (State state : l2) {
			if (!states.contains(state)) {
				states.add(state);
			}
		}

		return states;
	}
}
