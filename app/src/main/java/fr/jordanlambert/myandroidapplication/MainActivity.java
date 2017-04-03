package fr.jordanlambert.myandroidapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.jordanlambert.myandroidapplication.model.GlobalObject;
import fr.jordanlambert.myandroidapplication.tools.MyAdapter;
import fr.jordanlambert.myandroidapplication.tools.db.DatabaseHandler;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseHandler db;
    private ArrayList<String> myDataset = new ArrayList<>();
    private ArrayList<GlobalObject> initialCities = new ArrayList<>();
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EditText editTextCityID = (EditText) findViewById(R.id.textEditCityID);
        final Button sendQuery = (Button) findViewById(R.id.send);


        // Change theme based on hour
        Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR);

        if(hours > 18){
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.AppThemeLight);
        }

        setContentView(R.layout.activity_main);



        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(this, initialCities);
        mRecyclerView.setAdapter(mAdapter);

        myDataset.add("@3067");
        myDataset.add("@3071");
        myDataset.add("@5167");
        myDataset.add("@1452");
        myDataset.add("@1454");
        myDataset.add("@645");


        Log.i(TAG, "Taille : " + myDataset.size());

        db = new DatabaseHandler(this);
        db.open();
        List<GlobalObject> tmpCities = db.getAllObj();
        db.close();

        for(int i=0; i<tmpCities.size();i++) {
            myDataset.add("@" + tmpCities.get(i).getRxs().getObs().get(0).getMsg().getCity().getIdx());
        }

        for(int i=0; i<myDataset.size();i++) {
            getDataFromUrl("https://api.waqi.info/api/feed/"+myDataset.get(i)+"/obs.fr.json?token=950e003ec3f068fdfb1f76e10e14a1d15f927479");
            Log.d(TAG, "Get data for id : " + i);
        }

    }

    /**
     * This function get data from waqi api (https://api.waqi.info/api/) and notify the adapter of the new data
     *
     * @param  url         String
     * @return             Void
     */

    private void getDataFromUrl(String url) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        GlobalObject obj = gson.fromJson(response, GlobalObject.class);
                        initialCities.add(obj);
                        mAdapter.notifyDataSetChanged();
                        Log.d(TAG, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


}
