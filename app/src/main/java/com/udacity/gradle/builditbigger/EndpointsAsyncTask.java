package com.udacity.gradle.builditbigger;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import android.content.Intent;
import android.os.AsyncTask;

import java.io.IOException;
import java.lang.ref.WeakReference;

import co.maskyn.builditbigger.backend.myApi.MyApi;
import co.maskyn.mylibrary.JokesActivity;

class EndpointsAsyncTask extends AsyncTask<Void, Void, String> {
        private static MyApi myApiService = null;
        private final WeakReference<MainActivity> mainActivity;

        public EndpointsAsyncTask(MainActivity mainActivity) {
            this.mainActivity = new WeakReference<MainActivity>(mainActivity);
        }

        @Override
        protected String doInBackground(Void... params) {
            if(myApiService == null) {  // Only do this once
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // options for running against local devappserver
                        // - 10.0.2.2 is localhost's IP address in Android emulator
                        // - turn off compression when running against local devappserver
                        .setRootUrl("http://192.168.1.7:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end options for devappserver

                myApiService = builder.build();
            }

            try {
                return myApiService.tellJoke().execute().getData();
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // if the activity was not destroyed
            if (mainActivity.get() != null) {
                Intent intent = new Intent(mainActivity.get(), JokesActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, result);
                mainActivity.get().startActivity(intent);
            }

        }
    }