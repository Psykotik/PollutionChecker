package fr.jordanlambert.myandroidapplication.tools;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that elementpublic Button mButton;
        MessageObject msg = mDataset.get(position).getRxs().getObs().get(0).getMsg();

        for(int i=0;i<msg.getIaqi().size();i++) {
            // Color buttons and cities with pm10 values
            if(msg.getIaqi().get(i).getP().contains("pm10")) {
                holder.mButton.setText(msg.getIaqi().get(i).getV().get(0).toString());
                holder.max.setText(msg.getIaqi().get(i).getV().get(2).toString());
                holder.min.setText(msg.getIaqi().get(i).getV().get(1).toString());
               
            }
            if(msg.getIaqi().get(i).getP().contains("t")) {
                String city = msg.getCity().getName();
                if(msg.getCity().getName().length()>20) {
                    city = msg.getCity().getName().substring(0,20)+"...";
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
}