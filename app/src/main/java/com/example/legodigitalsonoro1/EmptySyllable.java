package com.example.legodigitalsonoro1;

/**
 * empty syllable for the grid list
 * @author chiara
 *
 */
public class EmptySyllable extends Syllable {
	
	private final static String empty="empty_syll";
	
	
	public EmptySyllable() {
		super(empty, null, false);
	}

	public boolean isEmptySyll(){
		return true;
	}
}
