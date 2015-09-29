package com.example.legodigitalsonoro1;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * this class manage the state of the game and the communication of the state of the game
 * @author chiara
 *
 */
public class MatchActivity extends FragmentActivity implements ResultCallback {
	
	private static final String NEXT = "next";
	private static final String END = "end";
	
	public static final String SETTINGS = "settings";

	private String mode = new String();

	private PlaygroundFragment playgroundFragment;
	private ResultFragment resultFragment;

	private BluetoothService mBTService;
	private ImageButton nextMatch;
	private ImageView nextTurn;
	
	private boolean multi=false;

	
	/**
	 * game initialization
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match);

		//initialize the textToSpechObject
		MyTextToSpeechManager.InitializeTextToSpeech(getApplicationContext());

		//mode can be multi or single game
		Bundle b = getIntent().getExtras();
		mode = b.getString(MainActivity.MODE);

		//extract assets
		int t = 0, w = 0;
		InputStream f = null;
		AssetManager manager = getAssets();
		try {
			f = manager.open("words.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}

		//collect info on number of turn and words per each turn
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		t = pref.getInt("num_turn", 8);
		w = Integer.parseInt(pref.getString("num_words", "3"));
		
		

		//initialize match manager
		if (mode.equals(MainActivity.multiPlayerServer)) {
			//if this player is the master initialize words and send them to the slave
			mBTService = BluetoothService.getBluetoothService(mHandler);
			MatchManager.setMatchManager(mode, f, t, w);
			MatchManager.getMatchManager().startNewMatch();
			sendMessage(t + "," + w + ","
					+ MatchManager.getMatchManager().getWordsAsString());
			multi = true;
			setTurnButton();
		} else if (mode.equals(MainActivity.multiPlayerClient)) {
			//if this player is the slave initialize match with words received
			mBTService = BluetoothService.getBluetoothService(mHandler);
			String wordsAsString = b.getString(SETTINGS);
			MatchManager.setMatchManager(wordsAsString);
			MatchManager.getMatchManager().startNewMatch();
			multi = true;
			setTurnButton();
		} else {
			//single player
			MatchManager.setMatchManager(mode, f, t, w);
			MatchManager.getMatchManager().startNewMatch();
		}

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();

		playgroundFragment = new PlaygroundFragment();
		resultFragment = new ResultFragment();

		transaction.add(R.id.container2, playgroundFragment);
		transaction.add(R.id.container1, resultFragment);
		
		//button to go to the next turn
		nextMatch=(ImageButton) findViewById(R.id.next);
		nextMatch.setClickable(false);
		nextMatch.setVisibility(View.INVISIBLE);

		transaction.commit();
	}

	/**
	 * call this method to update the turn button in the multi case
	 */
	private void setTurnButton() {
		if (nextTurn == null) {
			nextTurn = (ImageView) findViewById(R.id.turn);
		}
		
		int id;
		
		if(MatchManager.getMatchManager().isMyTurn()){
			id = getResources().getIdentifier("green_button",
					"drawable", getPackageName());
		}else{
			id = getResources().getIdentifier("red_button",
					"drawable", getPackageName());			
		}
		nextTurn.setImageResource(id);
		
		
	}

	@Override
	public void onResultChanged() {
		if (resultFragment != null) {
			resultFragment.updateResult();
		}
		if (multi) {
			// Check that we're actually connected before trying anything
			if (mBTService.getState() != BluetoothService.STATE_CONNECTED) {
				Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
						.show();
				return;
			}

			// Check that there's actually something to send
			if (MatchManager.getMatchManager().getLastResultWord() != null) {
				sendMessage(MatchManager.getMatchManager().getLastResultWord()
						.getMyword());
			}
		}
		if(MatchManager.getMatchManager().isTurnFinished()){
			nextMatch.setClickable(true);
			nextMatch.setVisibility(View.VISIBLE);
		}else{
			nextMatch.setClickable(false);
			nextMatch.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * this method update the word in the playground section when the user push the update button
	 * if the words of the entire game are finished it terminate the game and if mode is multi update the other player
	 * @param v
	 */
	public void nextMatch(View v) {
		boolean end=MatchManager.getMatchManager().startNewMatch();
		nextMatch.setVisibility(View.INVISIBLE);
		nextMatch.setClickable(false);
		
		if (!end) {
			playgroundFragment.updateAdapter(true);
			resultFragment.updateResult();
			if (multi) {
				sendMessage(NEXT);
			}
		} else {
			if (multi) {
				sendMessage(END);
				mBTService.stopGame();
				endGame();
			} else {
				AlertDialog.Builder build = new AlertDialog.Builder(this);
				build.setMessage("Hai completato tutte le parole!");
				build.setTitle("Ottimo!");
				build.setPositiveButton("Ok", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences pref = PreferenceManager
								.getDefaultSharedPreferences(getApplicationContext());
						if (!pref.getString("email", "").isEmpty()) {
							new SendEmailAsyncTask().execute();
						}
						finish();
					}
				});
				build.setCancelable(false);
				build.show();
			}
			
		}
	}

	/**
	 * this method terminate the game
	 */
	private void endGame() {
		AlertDialog.Builder build = new AlertDialog.Builder(this);
		build.setMessage("Avete completato tutte le parole!");
		build.setTitle("Bravi!");
		build.setPositiveButton("Ok", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				SharedPreferences pref = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				if (!pref.getString("email", "").isEmpty()) {
					new SendEmailAsyncTask().execute();
				}
				mBTService.stop();
				finish();
			}
		});
		build.setCancelable(false);
		build.show();
	}

	/**
	 * this method call the bluetooth service to send message to the other player when mode is multi
	 * @param message
	 */
	private void sendMessage(String message) {
		// Check that we're actually connected before trying anything
		if (mBTService.getState() != BluetoothService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mBTService.write(send);
		}
	}
	
	
	@Override
	public void changeTurn(Word lastSelectedWord) {
		if (multi) {
			MatchManager.getMatchManager().changeTurn();
			// Check that we're actually connected before trying anything
			if (mBTService.getState() != BluetoothService.STATE_CONNECTED) {
				Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
						.show();
				return;
			}

			// Check that there's actually something to send
			if(lastSelectedWord!=null){
				String message = lastSelectedWord.getMyword();
				sendMessage(message);
			}
			setTurnButton();
		}
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BluetoothService.MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case BluetoothService.STATE_CONNECTED:
					break;
				case BluetoothService.STATE_CONNECTING:
					break;
				case BluetoothService.STATE_LISTEN:
				case BluetoothService.STATE_NONE:
				case BluetoothService.STATE_STOPPED:
					break;
				}
				break;
			case BluetoothService.MESSAGE_READ:
				byte[] readBuf=(byte[])msg.obj;
            	String readMessage=new String(readBuf, 0, msg.arg1);
            	manageMessage(readMessage);
				break;
			case BluetoothService.MESSAGE_TOAST:
				break;
			}
		}
	};

	/**
	 * with this method we manage the message received from the other player
	 * the message can be the word to complete, the word completed, the next match message
	 * or the end game message
	 * @param readMessage
	 */
	protected void manageMessage(String readMessage) {
		StringTokenizer tokenizer = new StringTokenizer(readMessage, " ");

		String word = tokenizer.nextToken();

		if (word.equals(MatchActivity.NEXT)) {
			MatchManager.getMatchManager().startNewMatch();
			playgroundFragment.updateAdapter(true);
			resultFragment.updateResult();
		} else if(word.equals(MatchActivity.END)){
			mBTService.stopGame();
			endGame();
		}else {
			// if the word is the one that I started put this word in the
			// results
			// otherwise complete the word
			if (MatchManager.getMatchManager().getLastSelectedWord() != null
					&& MatchManager.getMatchManager().getLastSelectedWord().getMyword().equals(word)) {
				MatchManager.getMatchManager().updateResult(word);
				setTurnButton();
				playgroundFragment.updateAdapter(true);
				resultFragment.updateResult();
			} else {
				MatchManager.getMatchManager().updateState(word);
				setTurnButton();
				playgroundFragment.updateAdapter(false);
				MatchManager.getMatchManager().setInstant();
			}
		}
		if(MatchManager.getMatchManager().isTurnFinished()){
			nextMatch.setClickable(true);
			nextMatch.setVisibility(View.VISIBLE);
		}else{
			nextMatch.setClickable(false);
			nextMatch.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	public void onBackPressed() {
		AlertDialog.Builder build=new Builder(this);
		build.setTitle("Sei sicuro di voler terminare la partita?");
		build.setMessage("Se termini questa partita non potrai recuperarla.");
		build.setPositiveButton("Termina", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(multi){
					mBTService.stopGame();
				}
				finish();
			}
		});
		build.setNeutralButton("Continua",new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		build.show();
	}
	
	class SendEmailAsyncTask extends AsyncTask <Void, Void, Boolean> {
	    Mail m = new Mail();

	    public SendEmailAsyncTask() {
	        if (BuildConfig.DEBUG) Log.v(SendEmailAsyncTask.class.getName(), "SendEmailAsyncTask()");
	        SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			String toArr= pref.getString("email", "");
	        m.setTo(toArr);
	        m.setFrom();
	        m.setSubject();
	        m.setBody(MatchManager.getMatchManager().getBodyMail());
	    }

	    @Override
	    protected Boolean doInBackground(Void... params) {
	        if (BuildConfig.DEBUG) Log.v(SendEmailAsyncTask.class.getName(), "doInBackground()");
	        try {
	            m.sendMail();
	            return true;
	        }catch (AuthenticationFailedException e) {
	            Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
	            e.printStackTrace();
	            return false;
	        } catch (MessagingException e) {
	            Log.e(SendEmailAsyncTask.class.getName(), m.getTo() + "failed");
	            e.printStackTrace();
	            return false;
	        }   catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
	}

}
