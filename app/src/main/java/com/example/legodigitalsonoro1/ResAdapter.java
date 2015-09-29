package com.example.legodigitalsonoro1;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * class to manage the content of the result list
 * @author chiara
 *
 */
public class ResAdapter extends ArrayAdapter<AbstractWord> {

	private Context context;
	private ArrayList<AbstractWord> words;
	private int columnWidht;

	public ResAdapter(Context context, int resource,
			ArrayList<AbstractWord> words, int width) {
		super(context, resource, words);
		this.context = context;
		this.words = words;
		columnWidht=width;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		convertView = LayoutInflater.from(context).inflate(R.layout.res_item,
				null);
		holder = new ViewHolder();
		holder.res = (ImageView) convertView.findViewById(R.id.result_image);
		RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams((columnWidht/4)*3,(columnWidht/4)*3);
		holder.res.setLayoutParams(params);
		
		holder.eng = (ImageView) convertView.findViewById(R.id.eng);
		RelativeLayout.LayoutParams params2=new RelativeLayout.LayoutParams((columnWidht/4), (columnWidht/4));
		params2.addRule(RelativeLayout.RIGHT_OF,holder.res.getId());
		holder.eng.setLayoutParams(params2);

		final int index = position;
		if (words.get(position) != null) {

			AbstractWord word = getItem(position);
			int id = context.getResources().getIdentifier(word.getImage(),
					"drawable", context.getPackageName());

			loadBitmap(id, holder.res);

			if (word instanceof Word) {
				holder.res.setClickable(true);
				holder.eng.setClickable(true);
				

				holder.res.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						MyTextToSpeechManager.getTextToSpeech().speak(
								getItem(index).getMyword(),
								TextToSpeech.QUEUE_ADD, null);
					}
				});
				
				holder.eng.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						MyTextToSpeechManager.getEnglishTextToSpeech().speak(
								((Word) getItem(index)).getEng(),
							TextToSpeech.QUEUE_ADD, null);
					}
				});
			} else {
				holder.res.setClickable(false);
				holder.eng.setClickable(false);
			}
		}
		return convertView;
	}

	@Override
	public int getCount() {
		return words.size();
	}

	@Override
	public AbstractWord getItem(int position) {
		return words.get(position);
	}

	public class ViewHolder {
		ImageView res;
		ImageView eng;
	}
	
	@Override
	public boolean isEnabled(int position) {
		if(words.get(position) instanceof EmptyWord){
			return false;
		}else{
			return true;
		}
	}

	public void loadBitmap(int resId, ImageView imageView) {
		if (cancelPotentialWork(resId, imageView)) {
			final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
			Bitmap mPlaceHolderBitmap = null;
			final AsyncDrawable asyncDrawable = new AsyncDrawable(
					context.getResources(), mPlaceHolderBitmap, task);
			imageView.setImageDrawable(asyncDrawable);
			task.execute(resId);
		}
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

	public static boolean cancelPotentialWork(int data, ImageView imageView) {
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
		        return decodeSampledBitmapFromResource(context.getResources(), data, 100, 100);
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

}
