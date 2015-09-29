package com.example.legodigitalsonoro1;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * class to manage the game state
 *
 * @author chiara
 */
public class MatchManager {

    private static WordManager wordManager;
    private static MatchManager matchManager;

    private static String player1 = "Giocatore1";
    private static String player2 = "Giocatore2";

    private Word lastSelectedWord;
    private boolean syllSelected = false;
    private boolean lastSelectedSyllableIfFirst = false;
    private Word lastResultWord;

    private boolean isMyTurn = false;
    private static int numTurn;
    private static int wordPerTurn;

    private ArrayList<Word> currentWords;
    private ArrayList<AbstractWord> currentResults;
    private ArrayList<Syllable> currentSyll = new ArrayList<Syllable>();

    private String bodyMail;
    private long lastInstant;
    private double startGameInstant;
    private String mode;
    private String myPlayer;
    private String otherPlayer;

    private MatchManager(String mode, InputStream f, int t, int w) {

        this.mode = mode;
        // if single player is always your turn
        // otherwise master set the game and wait for slave to start
        if (mode.equals(MainActivity.singlePlayer)) {
            wordManager = new WordManager(f, t, w, false);
            isMyTurn = true;
        } else {
            wordManager = new WordManager(f, t, w, true);
            isMyTurn = false;

        }

        numTurn = t;
        wordPerTurn = w;
        myPlayer = player1;
        otherPlayer = player2;
    }

    private MatchManager(String wordsAsString) {
        // slave player read the word and start to play
        StringTokenizer tokenizer = new StringTokenizer(wordsAsString, ",");

        mode = MainActivity.multiPlayerClient;
        String turns = tokenizer.nextToken();
        numTurn = Integer.parseInt(turns);
        String numwords = tokenizer.nextToken();
        wordPerTurn = Integer.parseInt(numwords);

        wordManager = new WordManager(numTurn, wordPerTurn,
                tokenizer.nextToken());
        isMyTurn = true;
        myPlayer = player2;
        otherPlayer = player1;
    }

    private void createResults() {
        int i;

        if (currentResults != null) {
            currentResults.clear();
        } else {
            currentResults = new ArrayList<AbstractWord>();
        }
        for (i = 0; i < wordPerTurn; i++) {
            EmptyWord empty = new EmptyWord();
            currentResults.add(empty);
        }
    }

    public static void setMatchManager(String mode, InputStream f, int t, int w) {
        matchManager = new MatchManager(mode, f, t, w);

    }

    public static void setMatchManager(String wordsAsString) {
        matchManager = new MatchManager(wordsAsString);
    }

    public boolean startNewMatch() {
        if (bodyMail == null) {
            bodyMail = new String();
            if (mode.equals(MainActivity.singlePlayer)) {
                bodyMail = "Nuova partita singola, " + numTurn * wordPerTurn
                        + " parole totali, " + wordPerTurn
                        + " parole per schermata.\n";
            } else {
                bodyMail = "Nuova partita multi-giocatore " + myPlayer + ", " + numTurn * wordPerTurn
                        + " parole totali, " + wordPerTurn
                        + " parole per schermata.\n";
            }
            startGameInstant = System.nanoTime();
        }
        lastSelectedWord = null;
        lastResultWord = null;


        if (!wordManager.noMorePanel()) {
            bodyMail = bodyMail + "\nNuova schermata: ";
            currentWords = (ArrayList<Word>) wordManager.getPanel()
                    .getWordsList();
            createResults();
            currentSyll.clear();

            ArrayList<Syllable> mySyll = new ArrayList<Syllable>();
            for (Word w : currentWords) {
                bodyMail = bodyMail + " " + w.getMyword() + " ";
                mySyll.add(w.getFirst());
                mySyll.add(w.getSecond());
            }
            bodyMail = bodyMail + "\n";

            if (!mode.equals(MainActivity.singlePlayer) & isMyTurn) {
                bodyMail = bodyMail + "\n" + myPlayer + ":\n";
            } else if (!mode.equals(MainActivity.singlePlayer)) {
                bodyMail = bodyMail + "\n" + otherPlayer + ":\n";
            }

            //put the syllables of the words in random order
            int i, j, max;
            max = mySyll.size();
            Random r = new Random();
            for (i = 0; i < max; i++) {
                if (mySyll.size() > 1) {
                    j = r.nextInt(mySyll.size() - 1);
                } else {
                    j = 0;
                }
                currentSyll.add(mySyll.remove(j));
            }
        } else {
            bodyMail = bodyMail + "\nTempo totale di gioco " + (((double) System.nanoTime() - startGameInstant) / 1000000000) + "s";
            return true;
        }
        return false;

    }

    public int onSyllSelected(Syllable syll) {
        Word currWord = getWordFromString(syll.getWord());

        // Check if there is an already selected syllable
        if (!syllSelected) {
            syllSelected = true;
            lastSelectedWord = currWord;
            bodyMail = bodyMail + "prima sillaba valida selezionata " + currWord.myword + " " + syll.getMySyll() + ", ";
            lastInstant = System.nanoTime();
            lastSelectedSyllableIfFirst = syll.isFirstSyll();
            return 0;
        } else {
            syllSelected = false;
            // Check if the clicked syllable is the right one
            if(lastSelectedSyllableIfFirst && lastSelectedWord.getSecond().getMySyll().equals(syll.getMySyll())){
                lastResultWord = currWord;
                updateResults();
                removeSyll(lastSelectedWord.getMyword());
                currentWords.remove(lastSelectedWord);

                double time = (((double) System.nanoTime() - lastInstant) / 1000000000);
                bodyMail = bodyMail
                        + lastResultWord.getMyword()
                        + " completata in "
                        + time
                        + "s,\n";
                lastSelectedSyllableIfFirst = false;
                return 1;
            } else {
                bodyMail = bodyMail + "[" + currWord.getMyword() + " " + syll.getMySyll() + "], ";
                lastSelectedSyllableIfFirst = false;
                return -1;
            }
        }
    }

    private Word getWordFromString(String s) {
        Word currWord = null;
        for (Word w : currentWords) {
            if (w.getMyword().equals(s)) {
                currWord = w;
                break;
            }
        }
        return currWord;
    }

    private void updateResults() {
        int i = 0;
        for (i = 0; i < wordPerTurn; i++) {
            AbstractWord w = currentResults.get(i);
            if (w.isEmptyWord()) {
                currentResults.set(i, lastResultWord);
                break;
            }
        }
    }

    public void updateResult(String word) {
        Word curResult = getWordFromString(word);
        lastResultWord = curResult;

        updateResults();
        removeSyll(lastResultWord.getMyword());
        currentWords.remove(lastResultWord);
        bodyMail = bodyMail + word + " completata\n";
    }

    private void removeSyll(String myword) {
        int i = 0;
        for (Syllable syll : currentSyll) {
            if (syll.getWord().equals(myword)) {
                i = currentSyll.indexOf(syll);
                EmptySyllable empty = new EmptySyllable();
                currentSyll.set(i, empty);
            }
        }
    }

    public ArrayList<Word> getCurrentWords() {
        return currentWords;
    }

    public ArrayList<AbstractWord> getCurrentResults() {
        return currentResults;
    }

    public Word getLastSelectedWord() {
        return lastSelectedWord;
    }

    public ArrayList<Syllable> getCurrentSyllables() {
        return currentSyll;
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }

    public boolean changeTurn() {
        if (!mode.equals(MainActivity.singlePlayer)) {
            isMyTurn = !isMyTurn;
            if (isMyTurn) {
                bodyMail = bodyMail + "\n" + myPlayer + ":\n";
            } else {
                bodyMail = bodyMail + "\n" + otherPlayer + ":\n";
            }
        }
        return isMyTurn;
    }

    /**
     * @return the matchManager
     */
    public static MatchManager getMatchManager() {
        return matchManager;
    }

    public boolean isTurnFinished() {
        return currentWords.isEmpty();
    }

    public int getNumberOfWords() {
        return wordPerTurn;
    }

    public String getWordsAsString() {
        ArrayList<String> words = (ArrayList<String>) wordManager
                .getWordsAsString();

        int i = 0;
        String r = words.get(i);
        i++;
        while (i < words.size()) {
            r = r + "-" + words.get(i);
            i++;
        }
        return r;
    }

    public Word getLastResultWord() {
        return lastResultWord;
    }

    public void updateState(String first) {
        Word curWord = getWordFromString(first);
        lastSelectedWord = curWord;
//        lastSelectedSyllableIfFirst = true;

        bodyMail = bodyMail + first + " " + curWord.getFirst().getMySyll();
        changeTurn();
    }

    public int getLastSelectedSyllIndex() {
        int i = 0;
        for (Syllable s : currentSyll) {
            if (getLastSelectedWord().getMyword().equals(s.getWord()) & s.isFirstSyll()) {
                break;
            }
            i++;
        }
        return i;
    }

    public String getBodyMail() {
        return bodyMail;
    }

    public long getLastInstant() {
        return lastInstant;
    }

    public void setInstant() {
        lastInstant = System.nanoTime();
    }

}
