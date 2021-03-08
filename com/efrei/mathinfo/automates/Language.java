package com.efrei.mathinfo.automates;

import java.util.ArrayList;
import java.util.List;

public class Language {

	private List<String> dictionary; 
	
	public Language() {
		this.dictionary = new ArrayList<String>();
	}
	
	public List<String> getDictionary() {
		return this.dictionary;
	}
	
	public void addWord(String word) {
		if (!this.dictionary.contains(word)) {
			this.dictionary.add(word);
		}
	}
}