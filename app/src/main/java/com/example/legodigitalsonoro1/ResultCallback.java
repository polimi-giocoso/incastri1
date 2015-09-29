package com.example.legodigitalsonoro1;

/**
 * implements this interface to update the UI on the state of the game
 * @author chiara
 *
 */
public interface ResultCallback {
	
	/**
	 * call this method when result list is changed and you want to update the UI
	 */
	public void onResultChanged();

	/**
	 * call this method when turn is changed and you want to update the UI
	 * @param lastSelectedWord
	 */
	public void changeTurn(Word lastSelectedWord);

}
