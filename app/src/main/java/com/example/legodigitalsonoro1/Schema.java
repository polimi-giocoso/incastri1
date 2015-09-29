package com.example.legodigitalsonoro1;

import java.util.ArrayList;
import java.util.List;

/**
 * class to manage words in a turn
 * @author chiara
 *
 */
public class Schema {
	
	private List<Word> myWords;

	public Schema() {
		myWords=new ArrayList<Word>();
	}
	
	public List<Word> getWordsList(){
		return myWords;		
	}

	public void add(Word w) {
		myWords.add(w);
	}

}
