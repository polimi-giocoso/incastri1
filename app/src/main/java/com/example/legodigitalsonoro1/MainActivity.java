package com.example.legodigitalsonoro1;

import java.lang.ref.WeakReference;

import com.example.legodigitalsonoro1.ResAdapter.AsyncDrawable;
import com.example.legodigitalsonoro1.ResAdapter.BitmapWorkerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

/**
 * this class manage the initialization game phase
 * @author chiara
 *
 */
public class MainActivity extends Activity {
	
	public static final String MODE="mode";
	public static final String singlePlayer="SinglePlayer";
	public static final String multiPlayerServer = "MultiPlayerServer";
	public static final String multiPlayerClient = "MultiPlayerClient";
	
	private Intent intent;
	private int REQUEST_ENABLE_BT;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  
        
        
        /*Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int maxHeight = size.y;
        
        ImageView title=(ImageView) findViewById(R.id.title);
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams((maxHeight/5)*8, maxHeight/2);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        title.setLayoutParams(params);
        
        RelativeLayout layout=(RelativeLayout) findViewById(R.id.button_container);
        LayoutParams paramLayout=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        
        ImageButton multi=(ImageButton) findViewById(R.id.multiple_players);
        ImageButton single=(ImageButton) findViewById(R.id.single_player);
        params=new RelativeLayout.LayoutParams(maxHeight/2, maxHeight/2);
        multi.setLayoutParams(params);
        layout.addView(multi);
        params.addRule(RelativeLayout.LEFT_OF,R.id.multiple_players);
        single.setLayoutParams(params);
        layout.addView(multi);
        
        layout.setLayoutParams(paramLayout);*/
        
        
    }
    
    private void loadBitmap(int resId, ImageView imageView) {
    	if (cancelPotentialWork(resId, imageView)) {
			final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
			Bitmap mPlaceHolderBitmap = null;
			final AsyncDrawable asyncDrawable = new AsyncDrawable(
					getResources(), mPlaceHolderBitmap, task);
			imageView.setImageDrawable(asyncDrawable);
			task.execute(resId);
		}
	}

	private boolean cancelPotentialWork(int data, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

		if (bitmapWorkerTask != null) {
			final int bitmapData = bitmapWorkerTask.data;
			if (bitmapData != data) {
				// Cancel previous task
				bitmapWorkerTask.cancel(true);
			} else {
				// The same work is already in progress
				return false;
			}
		}
		// No task associated with the ImageView, or an existing task was
		// cancelled
		return true;
	}

	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}
	
	static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		public AsyncDrawable(Resources res, Bitmap bitmap,
				BitmapWorkerTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(
					bitmapWorkerTask);
		}

		public BitmapWorkerTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
	}
	
	class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private int data = 0;

		public BitmapWorkerTask(ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage
			// collected
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			if (imageViewReference != null && bitmap != null) {
				final ImageView imageView = imageViewReference.get();
				final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
				if (this == bitmapWorkerTask && imageView != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		}

		@Override
		    protected Bitmap doInBackground(Integer... params) {
		        data = params[0];
		        return decodeSampledBitmapFromResource(getResources(), data, 100, 100);
		    }
	}
	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	@Override
    protected void onStart() {
    	super.onStart();
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }
    
    /**
     * method called when the user selects the settings button
     * @param v
     */
    public void changeSettings(View v){
    	 getFragmentManager().beginTransaction()
         .replace(android.R.id.content, new SettingsFragment())
         .addToBackStack("settings")
         .commit();
    }
    
    /**
     * method called when the user selects the single match button
     * @param b
     */
    public void startSingleMatch(View b){
    	intent=new Intent(this, MatchActivity.class);
    	intent.putExtra(MODE, singlePlayer);
    	startActivity(intent);
    }
    
    /**
     * method called when the player selects the multi player button
     * @param b
     */
	public void startMultiplePlayersMatch(View b) {

		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			
			AlertDialog.Builder builder= new Builder(this);
			builder.setTitle("Warning");
			builder.setMessage("Sorry, your device doesn't support Bluetooth connection.");
			builder.setCancelable(true);
			builder.setNeutralButton(R.string.ok, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			builder.create().show();
			
		} else {
			
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
			
			Intent intent=new Intent(this, ConnectionSetupActivity.class);
			startActivity(intent);
		}
	}
	

}
