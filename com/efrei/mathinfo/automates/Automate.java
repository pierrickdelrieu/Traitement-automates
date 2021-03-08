package com.efrei.mathinfo.automates;

import java.util.List;

public class Automate {

	private List<State> states;
	
	private Language lang;
	
	public Automate(List<State> states, Language lang) {
		this.states = states;
		this.lang = lang;
	}
	
	public List<State> getStates() {
		return this.states;
	}
	
	public Language getLang() {
		return this.lang;
	}
}
