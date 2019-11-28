package ann.example.airpollutionmonitor.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ann.example.airpollutionmonitor.AppManager;
import ann.example.airpollutionmonitor.BaseActivity;
import ann.example.airpollutionmonitor.MainActivity;
import ann.example.airpollutionmonitor.Model.Location;
import ann.example.airpollutionmonitor.R;

public class LoadingActivity extends BaseActivity {
    private static final String TAG = "LoadingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        //onClearData(MainActivity.SHARED_PREFERENCE_TAG);
        ArrayList<Location> locations = onSearchData(MainActivity.SHARED_PREFERENCE_TAG);
        Log.d(TAG, (locations==null) + "");
        if (locations == null) {
            locations = new ArrayList<>();
            Log.d(TAG, "location 적용");
        }

        AppManager.getInstance().setLocations(locations);
        new Task().execute();
    }

    public class Task extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finish();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
    private void onClearData(String tag) {
        SharedPreferences sp = getSharedPreferences(tag, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    private ArrayList<Location> onSearchData(String tag) {
        SharedPreferences sp = getSharedPreferences(tag, MODE_PRIVATE);
        String strList = sp.getString(tag, "");

        Gson gson = new GsonBuilder().create();
        Type listType = new TypeToken<ArrayList<Location>>() {
        }.getType();

        ArrayList<Location> list = gson.fromJson(strList, listType);

        return list;
    }
}
