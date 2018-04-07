package com.newagesmb.version3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

public class ImageLoaderReco {

	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;
	Handler handler = new Handler();// handler to display images in UI thread
	Context _context;
	public ImageLoaderReco(Context context) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
		this._context=context;
	}

	final int stub_id = R.drawable.placeholder;

	public void DisplayImage(String url, ImageView imageView) {
		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);

		} else {

			queuePhoto(url, imageView);
			imageView.setImageResource(stub_id);
		}
	}
	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {
		File f = fileCache.getFile(url);

		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null)
			return b;

		// from web
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			Utils.CopyStream(is, os);
			os.close();
			conn.disconnect();
			bitmap = decodeFile(f);

			return bitmap;
		} catch (Throwable ex) {
			Log.v("GetBitmap:", ex.toString());
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError)
				memoryCache.clear();
			return null;
		}
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {	try {
		// decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		FileInputStream stream1 = new FileInputStream(f);
		BitmapFactory.decodeStream(stream1, null, o);
		stream1.close();

		// Find the correct scale value. It should be the power of 2.
		
		int REQUIRED_SIZE = getScreenWidth();
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int img_width=width_tmp;
		int scale = 1;
		
		if(width_tmp > REQUIRED_SIZE)
			width_tmp = REQUIRED_SIZE;
		
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE|| height_tmp / 2 < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}
		
		/*Log.v("REQUIRED_SIZE:width_tmp,height_tmp,scale",REQUIRED_SIZE+"**"+width_tmp+"**"+height_tmp+"**"+scale);*/
		float desiredScale = (float) REQUIRED_SIZE / img_width;
		
		// decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inJustDecodeBounds = false;
		o2.inDither = false;
		o2.inSampleSize = scale;
		o2.inScaled = false;
		o2.inPreferredConfig = Bitmap.Config.ARGB_8888;
		FileInputStream stream2 = new FileInputStream(f);
		Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
		// Resize
		Matrix matrix = new Matrix();
		matrix.postScale(desiredScale, desiredScale);
		Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		bitmap = null;
		
		stream2.close();
		return scaledBitmap;
	} catch (FileNotFoundException e) {
	} catch (IOException e) {
		e.printStackTrace();
	}
	return null;
	}
	   @SuppressLint("NewApi")
		public int getScreenWidth() {
	        int columnWidth;
	        WindowManager wm = (WindowManager) _context.getSystemService(Context.WINDOW_SERVICE);
	        Display display = wm.getDefaultDisplay();
	 
	        final Point point = new Point();
	        try {
	            display.getSize(point);
	        } catch (java.lang.NoSuchMethodError ignore) { // Older device
	            point.x = display.getWidth();
	            point.y = display.getHeight();
	        }
	        columnWidth = point.x;
	        return columnWidth;
	    }
	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			try {
				if (imageViewReused(photoToLoad))
					return;
				Bitmap bmp = getBitmap(photoToLoad.url);
				memoryCache.put(photoToLoad.url, bmp);
				if (imageViewReused(photoToLoad))
					return;
				BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
				handler.post(bd);
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);
			else
				photoToLoad.imageView.setImageResource(stub_id);
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

}
