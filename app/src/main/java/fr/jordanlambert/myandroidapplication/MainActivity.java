package fr.jordanlambert.myandroidapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import fr.jordanlambert.myandroidapplication.model.GlobalObject;
import fr.jordanlambert.myandroidapplication.tools.MyAdapter;
import fr.jordanlambert.myandroidapplication.tools.db.DatabaseHandler;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseHandler db;
    private ArrayList<String> myDataset = new ArrayList<>(); //{"@3067", "@3071"};
    private ArrayList<GlobalObject> initialCities = new ArrayList<>();
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Send email button
        ImageButton shareBtn = (ImageButton) findViewById(R.id.button_share);
        /*shareBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.i(TAG, "Sharebutton");
                sendEmail();
            }
        });*/




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
        myDataset.add("@5771");
        myDataset.add("@4070");
        /*int base = 3000;
        for(int i=0; i<10; i++) {
                myDataset.add("@"+base);
                base = base+1;
        }*/

        Log.i(TAG, "Taille :" + myDataset.size());

        db = new DatabaseHandler(this);
        db.open();
        List<GlobalObject> tmpCities = db.getAllObj();
        db.close();

        for(int i=0; i<tmpCities.size();i++) {
            myDataset.add("@" + tmpCities.get(i).getRxs().getObs().get(0).getMsg().getCity().getIdx());
        }

        for(int i=0; i<myDataset.size();i++) {
            getDataFromUrl("https://api.waqi.info/api/feed/"+myDataset.get(i)+"/obs.fr.json?token=950e003ec3f068fdfb1f76e10e14a1d15f927479");
            Log.d(TAG, "Get data for id :" + i);
        }

    }

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

    protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {""};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Try PollutionCheckin Application !");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hey ! Wassup ? I'm trying a new cool application, wanna try it ? Go for Psykotik github and search for PollutionChecker Repo. Hope you'll enjoy it ma boi !");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending mail.", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }


}
