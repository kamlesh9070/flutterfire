package org.dadabhagwan.AKonnect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Function;

public class ImageDownloadTask extends AsyncTask<Void, Void, Bitmap> {

  String strUrl;
  URL url;
  Function<Bitmap, Void> onPostExecute;

  ImageDownloadTask(String url, Function<Bitmap, Void> onPostExecute) {
    this.strUrl = url;
    this.onPostExecute = onPostExecute;
  }

  @Override
  protected Bitmap doInBackground(Void... voids) {
    URL url = stringToURL();
    HttpURLConnection connection = null;
    try {
      connection = (HttpURLConnection) url.openConnection();
      connection.connect();
      InputStream inputStream = connection.getInputStream();
      BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
      return BitmapFactory.decodeStream(bufferedInputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  // When all async task done
  protected void onPostExecute(Bitmap result) {
    this.onPostExecute(result);
  }

  protected URL stringToURL() {
    try {
      return new URL(strUrl);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return null;
  }
}
