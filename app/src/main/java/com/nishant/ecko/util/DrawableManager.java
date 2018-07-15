package com.nishant.ecko.util;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class DrawableManager {
    private final Map<String, SoftReference<Drawable>> drawableMap;
    public DrawableManager() {
        drawableMap =new HashMap<>();
    }

    private Drawable fetchDrawable(String urlString) {
        if (drawableMap.containsKey(urlString)) {
            return drawableMap.get(urlString).get();
        }
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) new URL(urlString).openConnection();
            InputStream is = new BufferedInputStream(httpURLConnection.getInputStream());

            Drawable drawable = Drawable.createFromStream(is, "src");

            if (drawable != null) {
                drawableMap.put(urlString, new SoftReference<Drawable>(drawable));
            }

            return drawable;
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            httpURLConnection.disconnect();
        }
    }

    public void fetchDrawableOnThread(final String urlString, final ImageView imageView, final ProgressBar progressBar) {
        if (drawableMap.containsKey(urlString)) {
            imageView.setImageDrawable(drawableMap.get(urlString).get());
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                imageView.setImageDrawable((Drawable) message.obj);
                progressBar.setVisibility(View.GONE);
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {

                Drawable drawable = fetchDrawable(urlString);
                Message message = handler.obtainMessage(1, drawable);
                handler.sendMessage(message);
            }
        };
        thread.start();
    }
}

