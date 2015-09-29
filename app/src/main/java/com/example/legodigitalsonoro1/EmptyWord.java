package com.example.legodigitalsonoro1;

/**
 * empty word for he initial result list
 * @author chiara
 *
 */
public class EmptyWord extends AbstractWord {
	
	final static String EMPTY_WORD="empty_word";
	final static String EMPTY_IMAGE="tessera_vuota";
	
	public EmptyWord() {
		setMyword(EMPTY_WORD);
		image=EMPTY_IMAGE;
	}

	@Override
	public void setMyword(String myword) {
		this.myword=EMPTY_WORD;		
	}

	@Override
	public boolean isEmptyWord() {
		return true;
	}

	@Override
	public String getImage() {
		return image;
	}

}
