package com.efrei.mathinfo.automates;

import java.util.ArrayList;
import java.util.List;

public class Alphabet {

	private List<String> dictionary;


	/**
	* Constructor <br>
	* Automatic creation of a list containing the size first letter of the alphabet
	* @param size Number of letters of the automaton alphabet
	**/
	public Alphabet(int size) {
		this.dictionary = new ArrayList<String>(size);
		
		for (int i = 0; i < size; i++) {
			this.dictionary.add(String.valueOf(Character.valueOf((char) ('a' + i))));
		}
	}
	
	/**
	* Copy constructor <br>
	* @param dictionary List containing the alphabet to copy
	**/
	public Alphabet(List<String> dictionary) {
		this.dictionary = new ArrayList<String>(dictionary);
	}
	
	/**
	* Getter of alphabet list
	**/
	public List<String> getDictionary() {
		return this.dictionary;
	}

	/**
	* Add a word in the alphabet
	* @param word the String to add
	**/
	public void addWord(String word) {
		if (!this.dictionary.contains(word)) {
			this.dictionary.add(word);
		}
	}
	
	/**
	* Delete a word from the alphabet
	* @param word Word to remove
	**/
	public void removeWord(String word) {
		if (this.dictionary.contains(word)) {
			this.dictionary.remove(word);
		}
	}
}
