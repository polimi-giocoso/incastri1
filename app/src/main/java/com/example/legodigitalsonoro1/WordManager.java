package com.example.legodigitalsonoro1;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 
 * @author chiara
 *
 */
public class WordManager {

	private WordReader reader;
	private List<Schema> panels;
	private List<String> wordAsString;

	public WordManager(InputStream f, int turns, int numWords, boolean multi) {
		reader = new WordReader(f);
		ArrayList<Word> myWords = new ArrayList<Word>();

		if (multi) {
			wordAsString = reader.getWordsAsString(turns * numWords);

			for (String s : wordAsString) {
				Word w = new Word(s);
				myWords.add(w);
			}

		} else {
			myWords = reader.getSubList(turns * numWords);
		}

		createPanelList(myWords, turns, numWords);
	}

	private void createPanelList(ArrayList<Word> myWords, int turns,
			int numWords) {
		panels = new ArrayList<Schema>();
		int i;
		for (i = 0; i < turns; i++) {
			Schema schema = new Schema();

			int j;
			for (j = 0; j < numWords; j++) {
				schema.add(myWords.remove(0));
			}
			panels.add(schema);
		}
	}

	public List<String> getWordsAsString() {
		return wordAsString;
	}

	public WordManager(int numTurn, int numWords, String matchInfo) {

		ArrayList<Word> myWords = new ArrayList<Word>();
		StringTokenizer tokenizer2 = new StringTokenizer(matchInfo, "-");

		while (tokenizer2.hasMoreElements()) {
			String wordAsString = tokenizer2.nextToken();
			Word w = new Word(wordAsString);
			myWords.add(w);
		}

		createPanelList(myWords, numTurn, numWords);
	}

	public Schema getPanel() {
		if (!panels.isEmpty()) {
			return panels.remove(0);
		}
		return null;
	}
	
	public boolean noMorePanel(){
		return panels.isEmpty();
	}
}
