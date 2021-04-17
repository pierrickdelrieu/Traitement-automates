package com.efrei.mathinfo.automates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;

import com.efrei.mathinfo.io.Utils;

public class Operations {
	 	
	/**
	 * Checks if an automaton is asynchronous 
	 * 
	 * @param automaton
	 * 			the automaton to check
	 * 
	 * @return true if it is async
	 * 		   false if not
	 * */
	private static boolean isAsync(Automaton automaton) {
		for (State state : automaton.getStates()) { 
			for (String key : state.getLinks().keySet()) { 
				if (key.equals("*")) { // "" is an epsilon transition, if one state has such transition, the automaton is asynchronous
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	/**
	 * Synchronizes an automaton
	 * 
	 * @param automaton
	 * 			the automaton to synchronize
	 * 
	 * */
	private static void synchronize(Automaton automaton) {
		Map<State, State> epsilonStates = new HashMap<State, State>(); // takes state as key and the epsilon value of the state as the value of the map 
		List<State> newStates = new ArrayList<State>();
				
		for (State state : automaton.getStates()) {
			List<State> toMerge = synchronizeHelper(state); // we build the list of all the epsilon transitions for the state 
			State[] toMergeArr = toMerge.toArray(new State[0]); // we convert it to an array so that we can call mergeStates function on it 
			
			state.getLinks().remove(""); // we remove all epsilon transitions
			
			epsilonStates.put(state, mergeStates(toMergeArr)); // we put it in our map 
			
			State mergeState = epsilonStates.get(state); // we retrieve the value
		
			newStates.add(mergeState); // add it to our state list
		}
		
		
		automaton.changeStates(newStates);
	}
 
	private static List<State> synchronizeHelper(State state) {
		for (String key : state.getLinks().keySet()) {
			List<State> destinationsStates = state.getLinks().get(key);	// all the destinations for the transition 'key' 	
			List<State> epsilonDestinations = new ArrayList<State>(); // we are going to list all of the states we can go to from the epsilon transitions
			epsilonDestinations.add(state); // we can obviously go to the starting state, so we add it 
			
			List<State> travelStates = new ArrayList<State>(); // helper list to store all the other states we can go to from the starting state
			
			if (key.equals("")) { 
				for (State dState : destinationsStates) {
					
					if (!travelStates.contains(dState) && !dState.equals(state)) { // we add all the possible "travel states" in the travelStates list 
						travelStates.add(dState);
					}
				}
				
				for (State tState : travelStates) { 
					/*
					 * This section is a bit tricky. We've found all the travel states starting from the starting state 
					 * but if one of the travel states also has an epsilon transition, we must be able to add its own travel states to our 
					 * own epsilonDestinations list. 
					 * 
					 * So for each state in the travelStates list, we want to call the synchronizeHelper function again 
					 * so that it repeats the process and adds all the states we can go to, starting from the travelState. 
					 * */
					epsilonDestinations = mergeLists(epsilonDestinations, synchronizeHelper(tState));
				}
			}
			
			return epsilonDestinations;
		}
		
		return null;
	}
	
	public static void determinize(Automaton automaton) {
		
		if (!isDeterministic(automaton)) {
			
			System.out.println("\nDéterminisation de votre automate \n");
			
			if (isAsync(automaton)) {
				synchronize(automaton);
				return;
			}

			State[] entries = automaton.getStatesByType(StateType.ENTRY);
			State newEntry = mergeStates(entries);

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
						List<State> destinations = current.getLinks().get(key);
						State mergedState = findOrMerge(automaton, destinations.toArray(new State[0]));
						
						destinations.clear();
						destinations.add(mergedState);
						
						if (!newStates.contains(mergedState)) {
							toDetermine.add(mergedState);
						}
					}
					
					newStates.add(current);
				}
			}
			
			automaton.setNumTransitions(transitions);

			// Remove all the other entries but not the first one (which is the only one we
			// need)
			for (State entry : newStates) {
				if (entry != newStates.get(0)) {
					entry.removeType(StateType.ENTRY);
				}
			}

			automaton.setNumTransitions(transitions);
			
			automaton.display();
			System.out.println("Déterminisation terminée !");
		}
		
		else {
			System.err.println("\nVotre automate est déja déterministe");
		}
	}

	private static boolean isDeterministic(Automaton automaton) {

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
			System.err.println("\nL'automate que vous cherchez à compléter n'est pas déterministe");
			determinize(automaton);
		}
		
		if (!isCompleted(automaton)) { 
			System.out.println("\nComplétion de votre automate \n");
			
			if (isAsync(automaton)) {
				synchronize(automaton);
				return;
			}
			
			State bin = new State("P"); // we create the 'bin' state
			for (String key : automaton.getAlphabet().getDictionary()) {
				bin.addLink(key, bin); // we add as many loops on 'bin' as there are letters in the alphabet 
				automaton.setNumTransitions(automaton.getNumTransitions() + 1);
			}
			
			for (State state : automaton.getStates()) {				
				Set<String> keys = state.getLinks().keySet();
				
				for (String word : automaton.getAlphabet().getDictionary()) {
					if (!keys.contains(word)) {
						automaton.setNumTransitions(automaton.getNumTransitions() + 1);
						state.addLink(word, bin);
					}
				}
			}
			
			automaton.getStates().add(bin);
			
			automaton.display();
			System.out.println("Complétion terminée ! ");
		}
		
		else {
			System.err.println("\nVotre automate est déjà complet");
		}
	}

	private static boolean isCompleted(Automaton automaton) {

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
			
			if (isAsync(automaton)) {
				synchronize(automaton);
				return;
			}
			
			System.out.println("\nStandardisation de votre automate : \n");
			
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
			
			automaton.display();
			System.out.println("Votre automate est désormais standardisé ! \n");
		}
		
		else {
			System.err.println("Votre automate est déjà standardisé ! \n");
		}
	}

	private static boolean isStandard(Automaton automaton) {

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

	private static boolean isMinimized(Automaton automaton) {
		return false;
	}

	public static Automaton getComplementary(Automaton automaton) {

		Automaton complementary = automaton.clone();
		
		System.out.println("Création de l'automate complémentaire à votre automate \n");

		for (State state : complementary.getStates()) { // Adding the EXIT type to all common
			if (!state.getType().contains(StateType.EXIT)) {
				state.addType(StateType.EXIT);
			}

			else {
				state.removeType(StateType.EXIT);
			}
		}
		
		complementary.display();
		System.out.println("Création de l'automate complémentaire terminée ! \n");

		return complementary;
	}

	protected static State mergeStates(State... states) {
				
		if (states.length == 0) {
			return null;
		}

		else if (states.length == 1) {
			return states[0];
		}

		State current = new State(states[0]);
				
		for (int i = 1; i < states.length; i++) {
			State next = states[i];
			
			if (next.equals(current)) {
				continue;
			}
			
			current.mergeWith(next);
		}
				
		return current;
	}
	
	protected static State findOrMerge(Automaton automaton, State... states) {
		
		String possibleID = makeID(states);
		State possibleState = automaton.getByID(possibleID);
		
		if (possibleState != null) {
			return possibleState;
		}
		
		else {
			return mergeStates(states);
		}
	}

	protected static <T> List<T> mergeLists(List<T> list1, List<T> list2) {

		List<T> newList = new ArrayList<>();
		
		newList.addAll(list1);

		for (T t : list2) {
			if (!newList.contains(t)) {
				newList.add(t);
			}
		}
		
		//Collections.sort(states);
		
		return newList;
	}

	protected static Map<String, List<State>> mergeMaps(Map<String, List<State>> map1, Map<String, List<State>> map2) {

		Map<String, List<State>> newMap = new HashMap<String, List<State>>();
		
		if (map1.isEmpty() && !map2.isEmpty()) {
			newMap.putAll(map2);
		}
		
		else if (map2.isEmpty()) {
			newMap.putAll(map1);
		}
		
		else if (!map1.isEmpty() && !map2.isEmpty()) {
			newMap.putAll(map1);
			map2.forEach((key, destinationStates) -> {
				newMap.merge(key, destinationStates, (v1, v2) -> mergeLists(v1, v2));
			});
		}
		
		return newMap;
	}
	
	protected static String makeID(State... states) {
		String possibleID = states[0].getID();
		
		for (int i = 1; i < states.length; i++) {
			if (!possibleID.contains(states[i].getID())) {
				possibleID = String.join("", possibleID, states[i].getID());
			}
			
			else if (states[i].getID().contains(possibleID)){
				possibleID = states[i].getID();
			}
		}
				
		return removeDuplicates(possibleID);
	}
	
	protected static String removeDuplicates(String string) {
		char[] chars = string.toCharArray();
		Arrays.sort(chars);
		Set<Character> charSet = new LinkedHashSet<Character>();
		for (char c : chars) {
		    charSet.add(c);
		}

		StringBuilder sb = new StringBuilder();
		for (Character character : charSet) {
		    sb.append(character);
		}
		
		return sb.toString();
	}
}
