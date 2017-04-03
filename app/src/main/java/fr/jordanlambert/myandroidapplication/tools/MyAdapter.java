package fr.jordanlambert.myandroidapplication.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import fr.jordanlambert.myandroidapplication.R;
import fr.jordanlambert.myandroidapplication.model.GlobalObject;
import fr.jordanlambert.myandroidapplication.model.MessageObject;

/**
 * Created by jordan on 27/03/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<GlobalObject> mDataset;
    private Context mCtx;
    private String TAG = "MyAdapter";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public Button mButton;
        public TextView global;
        public TextView gps;
        public TextView max;
        public TextView min;
        public TextView lastUpdate;
        public ViewHolder(View v) {
            super(v);
            mButton = (Button) v.findViewById(R.id.button);
            global = (TextView) v.findViewById(R.id.globalinfo);
            gps = (TextView) v.findViewById(R.id.gps);
            max = (TextView) v.findViewById(R.id.pm10Max);
            min = (TextView) v.findViewById(R.id.pm10Min);
            lastUpdate = (TextView) v.findViewById(R.id.lastUpdate);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context ctx, ArrayList<GlobalObject> initialCities) {
        mCtx = ctx;
        mDataset = initialCities;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that elementpublic Button mButton;
        final MessageObject msg = mDataset.get(position).getRxs().getObs().get(0).getMsg();

        // Send email button
        ImageButton shareBtn = (ImageButton) holder.itemView.findViewById(R.id.button_share);
        // Refresh email button
        ImageButton refreshButton = (ImageButton) holder.itemView.findViewById(R.id.button_refresh);
        // City cell
        final TableLayout cityCell = (TableLayout) holder.itemView.findViewById(R.id.contentTableLayout);

        shareBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.i("MyAdapter", "Sharebutton");
                sendEmail();
            }
        });
        refreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.i(TAG, "refreshButton cell " + position );
            }
        });
        cityCell.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.i(TAG, "cityCell " + position);

            }
        });



        for(int i=0;i<msg.getIaqi().size();i++) {
            // Color buttons and cities with pm10 values
            if(msg.getIaqi().get(i).getP().contains("pm10")) {
                String mButtonValue = msg.getIaqi().get(i).getV().get(0).toString();
                String maxValue = msg.getIaqi().get(i).getV().get(2).toString();
                String minValue = msg.getIaqi().get(i).getV().get(1).toString();
                Integer temperatureValue = msg.getIaqi().get(i).getV().get(0);
                holder.mButton.setText(mButtonValue);
                holder.max.setText(maxValue);
                holder.min.setText(minValue);
                if(msg.getIaqi().get(i).getV().get(0)<50) {
                    holder.global.setTextColor(ContextCompat.getColor(mCtx, R.color.green));
                    holder.mButton.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.green));
                } else if(msg.getIaqi().get(i).getV().get(0)<100) {
                    holder.global.setTextColor(ContextCompat.getColor(mCtx, R.color.orange));
                    holder.mButton.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.orange));
                } else {
                    holder.global.setTextColor(ContextCompat.getColor(mCtx, R.color.red));
                    holder.mButton.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.red));
                }
            }
            if(msg.getIaqi().get(i).getP().contains("t")) {
                String city = msg.getCity().getName();
                if(msg.getCity().getName().length()>25) {
                    city = msg.getCity().getName().substring(0,25)+"...";
                }
                holder.global.setText(city+" "+msg.getIaqi().get(i).getV().get(0)+"°C");
            }


        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(msg.getTimestamp()*1000);
        holder.lastUpdate.setText(calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
        holder.gps.setText(msg.getCity().getGeo()[0].substring(0,5)+", "+msg.getCity().getGeo()[1].substring(0,5));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
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

        /*try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending mail.", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Log.e("MyAdapter", "There is no email client installed");
        }*/
    }
}

