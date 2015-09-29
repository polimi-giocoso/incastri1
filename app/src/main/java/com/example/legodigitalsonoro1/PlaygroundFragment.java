package com.example.legodigitalsonoro1;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/**
 * class to manage the grid side of the screen
 *
 * @author chiara
 */
public class PlaygroundFragment extends Fragment implements OnItemClickListener {

    private MatchManager matchManager;

    private GridAdapter adapter;
    private ResultCallback activityCallback;

    private GridView grid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.playground_fragment, container, false);

        grid = (GridView) v.findViewById(R.id.playground);
        grid.setNumColumns(matchManager.getNumberOfWords());
        grid.setGravity(Gravity.CENTER);

        grid.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                // Ensure you call it only once :
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    grid.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    grid.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                int height = grid.getHeight();
                int width = grid.getColumnWidth();

                adapter = new GridAdapter(getActivity(), R.layout.grid_item,
                        matchManager.getCurrentSyllables(), height, width);

                grid.setAdapter(adapter);
                Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.populate_grid);
                grid.setAnimation(anim);
                anim.start();
            }
        });
        grid.setOnItemClickListener(this);
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        matchManager = MatchManager.getMatchManager();
        activityCallback = (ResultCallback) activity;
    }

    /**
     * call this method if you want to refresh the playground
     *
     * @param newMatch is true if you want to change all the cards, false if you want to change only a couple
     */
    public void updateAdapter(boolean newMatch) {
        if (!newMatch && matchManager.getLastSelectedWord() != null) {
            MyTextToSpeechManager.getTextToSpeech().speak(
                    matchManager.getLastSelectedWord().getFirst().getMySyll().toLowerCase(),
                    TextToSpeech.QUEUE_ADD, null);
        }
        if (newMatch) {
            adapter.setSelected(-1);
        } else {
            adapter.setSelected(matchManager.getLastSelectedSyllIndex());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            final int position, long id) {
        if (matchManager.isMyTurn() && !matchManager.getCurrentSyllables().get(position).isEmptySyll()) {

            int res;
            MyTextToSpeechManager.getTextToSpeech().speak(
                    matchManager.getCurrentSyllables()
                            .get(position).getMySyll().toLowerCase(),
                    TextToSpeech.QUEUE_ADD, null);

            res = matchManager.onSyllSelected(matchManager
                    .getCurrentSyllables().get(position));

            switch (res) {
                case 1:
                    // positive sound
                    adapter.clear();
                    adapter.addAll(matchManager.getCurrentSyllables());
                    adapter.setSelected(-1);
                    adapter.notifyDataSetChanged();

                    activityCallback.onResultChanged();
                    view.clearAnimation();
                    break;
                case 0:
                    // positive sound
                    adapter.setSelected(position);
                    break;
                default:
                    // negative sound
                    adapter.setSelected(-1);
                    view.clearAnimation();
                    break;
            }
            activityCallback.changeTurn(MatchManager.getMatchManager().getLastSelectedWord());
        }

    }


}
