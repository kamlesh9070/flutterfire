package org.dadabhagwan.AKonnect.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConnectivityReceiver extends BroadcastReceiver {

  private static final String TAG = "[ConnectivityReceiver]";

  private ConnectivityReceiverListener mConnectivityReceiverListener;

  ConnectivityReceiver(ConnectivityReceiverListener listener) {
    mConnectivityReceiverListener = listener;
  }


  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d(TAG, "Inside onReceive");
    mConnectivityReceiverListener.onNetworkConnectionChanged(isConnected(context));
  }

  public static boolean isConnected(Context context) {
    ConnectivityManager cm = (ConnectivityManager)
      context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
  }

  public interface ConnectivityReceiverListener {
    void onNetworkConnectionChanged(boolean isConnected);
  }
}
