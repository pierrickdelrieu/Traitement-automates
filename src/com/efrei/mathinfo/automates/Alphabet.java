package com.efrei.mathinfo.automates;

import java.util.ArrayList;
import java.util.List;

public class Alphabet implements Cloneable {

	// Instance
	private List<String> dictionary;

	// Constructor
	public Alphabet(int size) {
		this.dictionary = new ArrayList<String>(size);
		
		for (int i = 0; i < size; i++) {
			this.dictionary.add(String.valueOf(Character.valueOf((char) ('a' + i))));
		}
	}
	
	public Alphabet(List<String> dictionary) {
		this.dictionary = new ArrayList<String>(dictionary);
	}
	
	public List<String> getDictionary() {
		return this.dictionary;
	}

	// Method of adding a character to the alphabet
	public void addWord(String word) {
		if (!this.dictionary.contains(word)) {
			this.dictionary.add(word);
		}
	}
}
