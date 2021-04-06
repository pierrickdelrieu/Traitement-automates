package com.efrei.mathinfo.automates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class Operations {

	public static void determinize(Automaton automaton) {
		if (!isDeterministic(automaton)) {

			State[] entries = automaton.getStatesByType(StateType.ENTRY);
			State newEntry = entries[0];

			// If the state has more than one entry
			if (entries.length > 1) {
				newEntry = mergeStates(entries);
			}

			Stack<State> toDetermine = new Stack<State>();
			toDetermine.add(newEntry);

			List<State> newStates = new ArrayList<State>();
			automaton.changeStates(newStates);

			int transitions = 0;

			while (!toDetermine.isEmpty()) {
				State current = toDetermine.pop(); // we retrieve the state to determine

				if (newStates.stream().noneMatch(state -> state.getID().equals(current.getID()))) {

					for (String key : current.getLinks().keySet()) {
						transitions++;
						List<State> destinations = current.getLinks().get(key); // we retrieve all destination of key

						if (destinations.size() > 1) {
							State[] destinationsArr = destinations.toArray(new State[0]);
							State mergedState = mergeStates(destinationsArr); // we merge the resulting states
							
							//see getById(mergedState.getID()) if null or not 

							destinations.clear(); // we clear the destination list only to add the merged state
							destinations.add(mergedState);

							if (!newStates.contains(mergedState)) {
								toDetermine.add(mergedState);
							}
						}

						else {
							toDetermine.add(destinations.get(0)); // we determine the next state
						}
					}

					newStates.add(current);

				}
			}

			// Remove all the other entries but not the first one (which is the only one we
			// need)
			for (State entry : newStates) {
				if (entry != newStates.get(0)) {
					entry.removeType(StateType.ENTRY);
				}
			}

			automaton.setNumTransitions(transitions);
		}
	}

	public static boolean isDeterministic(Automaton automaton) {

		State[] entries = automaton.getStatesByType(StateType.ENTRY);

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
		if (!isDeterministic(automaton)) { // cannot complete if the automaton isn't deterministic
			determinize(automaton);
		}
		
		if (!isCompleted(automaton)) { 
			
			State bin = new State("P"); // we create the 'bin' state
			for (String key : automaton.getAlphabet().getDictionary()) {
				bin.addLink(key, bin); // we add as many loops on 'bin' as there are letters in the alphabet 
			}
			
			for (State state : automaton.getStates()) {				
				Set<String> keys = state.getLinks().keySet();
				
				for (String word : automaton.getAlphabet().getDictionary()) {
					if (!keys.contains(word)) {
						state.addLink(word, bin);
					}
				}
			}
			
			automaton.getStates().add(bin);
		}
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
			State newEntry = new State("I");
			newEntry.addType(StateType.ENTRY);

			State[] entries = automaton.getStatesByType(StateType.ENTRY);

			for (State entry : entries) {
				for (String key : entry.getLinks().keySet()) {
					
					List<State> destinations = entry.getLinks().get(key);
					
					for (State state : destinations) {
						newEntry.addLink(key, state);
					}
				}

				entry.removeType(StateType.ENTRY);
			}

			automaton.getStates().add(newEntry);
		}
	}

	public static boolean isStandard(Automaton automaton) {

		State[] entries = automaton.getStatesByType(StateType.ENTRY);

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

		Automaton complementary = automaton.clone();

		for (State state : complementary.getStates()) { // Adding the EXIT type to all common
			if (!state.getType().contains(StateType.EXIT)) {
				state.addType(StateType.EXIT);
			}

			else {
				state.removeType(StateType.EXIT);
			}
		}

		return complementary;
	}

	public static State mergeStates(State... states) {

		if (states.length == 0) {
			return null;
		}

		else if (states.length == 1) {
			return states[0];
		}

		List<State> listStates = new ArrayList<State>(List.of(states));
		Iterator<State> itStates = listStates.iterator();

		State current = itStates.next().clone(); // we clone because we don't want to alter the initial states

		while (itStates.hasNext()) {
			State next = itStates.next().clone();
			current.mergeWith(next);
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

	public static Map<String, List<State>> mergeMaps(Map<String, List<State>> map1, Map<String, List<State>> map2) {

		Map<String, List<State>> newMap = new HashMap<String, List<State>>();

		if (!map1.isEmpty() && !map2.isEmpty()) {

			newMap.putAll(map1);

			for (String key : map2.keySet()) {
				if (!newMap.containsKey(key)) {
					newMap.put(key, map2.get(key));
				}

				else {
					List<State> nMapStates = newMap.get(key);
					List<State> map2States = map2.get(key);

					List<State> merged = mergeLists(nMapStates, map2States);
					newMap.remove(key);
					newMap.put(key, merged);
				}
			}
		}

		return (map1.isEmpty() && !map2.isEmpty()) ? map2 : map1;
	}
}
