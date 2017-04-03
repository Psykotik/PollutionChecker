package fr.jordanlambert.myandroidapplication.tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import fr.jordanlambert.myandroidapplication.R;
import fr.jordanlambert.myandroidapplication.model.GlobalObject;
import fr.jordanlambert.myandroidapplication.model.MessageObject;

/**
 * Created by jordan on 27/03/2017.
 */

@SuppressWarnings("ALL")
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
        // Sound button
        ImageButton soundButton = (ImageButton) holder.itemView.findViewById(R.id.button_sound);

        // Sound creation
        final MediaPlayer mp = MediaPlayer.create(mCtx, R.raw.ah);

        shareBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.i(TAG, "shareButton cell " + position);
                String city = msg.getCity().getName();
                Integer pollution = msg.getIaqi().get(0).getV().get(0);
                sendEmail(city, pollution);
            }
        });
        refreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.i(TAG, "refreshButton cell " + position );

                CharSequence text = "Refreshing data … Be patient !";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(mCtx, text, duration);
                toast.show();

            }
        });
        cityCell.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.i(TAG, "cityCell " + position);

            }
        });
        soundButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.i(TAG, "SoundButton" + position);

                if(msg.getIaqi().get(0).getV().get(0) > 50) {
                    mp.setVolume(50,50);
                    mp.start();

                    CharSequence text = "The pollution level is above the maximum 50 units recommended. Be careful !";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(mCtx, text, duration);
                    toast.show();

                } else if(msg.getIaqi().get(0).getV().get(0) > 100) {
                    Log.d(TAG, "Call notifnication");
                    Intent intent = new Intent(mCtx, MyAdapter.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(mCtx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder b = new NotificationCompat.Builder(mCtx);

                    b.setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.drawable.notification_icon)
                            .setContentTitle("Alert : " + msg.getCity().getName() + "is over polluated" )
                            .setContentText(" This city has a pollution level near " + msg.getIaqi().get(0).getV().get(0).toString() + ". Be careful !")
                            .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                            .setContentIntent(contentIntent);


                    NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(1, b.build());
                }
                else {
                    CharSequence text = "The pollution level is under the maximum 50 units recommended. All is fine !";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(mCtx, text, duration);
                    toast.show();
                }

            }
        });



        for(int i=0;i<msg.getIaqi().size();i++) {
            // Color buttons and cities with pm10 values
            if(msg.getIaqi().get(i).getP().contains("pm10")) {
                String mButtonValue = msg.getIaqi().get(i).getV().get(0).toString();
                String maxValue = msg.getIaqi().get(i).getV().get(2).toString();
                String minValue = msg.getIaqi().get(i).getV().get(1).toString();
                holder.mButton.setText(mButtonValue);
                holder.max.setText(maxValue);
                holder.min.setText(minValue);
                if(msg.getIaqi().get(i).getV().get(0)<50) {
                    holder.global.setTextColor(ContextCompat.getColor(mCtx, R.color.green));
                    holder.mButton.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.green));
                } else if(msg.getIaqi().get(i).getV().get(0)<100) {
                    holder.global.setTextColor(ContextCompat.getColor(mCtx, R.color.orange));
                    holder.mButton.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.orange));
                } else if(msg.getIaqi().get(i).getV().get(0)<150) {
                    holder.global.setTextColor(ContextCompat.getColor(mCtx, R.color.red));
                    holder.mButton.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.red));
                } else if(msg.getIaqi().get(i).getV().get(0)<200) {
                    holder.global.setTextColor(ContextCompat.getColor(mCtx, R.color.purple));
                    holder.mButton.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.purple));
                } else {
                    holder.global.setTextColor(ContextCompat.getColor(mCtx, R.color.brown));
                    holder.mButton.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.brown));
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


    /**
     * This function return the size of the dataset, invoked by the layout manager
     *
     * @return mDataset.size()
     */
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    /**
     * This function is called to send email with two args (city and pollution)
     * It will call installed mail client to send it, via intent.
     * Also, a predefined template is created, so the user can directly send the mail to one of his contact
     *
     * @param  city         String for the city name
     * @param  pollution    Integer for pollution value
     */

    protected void sendEmail(String city, Integer pollution) {
        Log.i("Send email", "");
        String[] TO = {""};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        String link_val = "https://github.com/Psykotik/PollutionChecker";
        String body = "The pollution level at " + city + " is about " + pollution + " units.\nIf you want to try this new application, go for <a href=\"" + link_val + "\">" + link_val + "</a>. Hope you'll enjoy it !\n See you soon.";
        String subject = "Pollution level at " + city + " !";
        if(pollution >= 50) {
            body = "Warning ! The pollution level at " + city + " is about " + pollution + " units, and it can be dangerous !\nIf you want to try this new application, go for <a href=\"" + link_val + "\">" + link_val + "</a>. Hope you'll enjoy it !\n See you soon.";
            subject = "High pollution level at " + city + " !";
        }

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml( body ));
        try {
            mCtx.startActivity(Intent.createChooser(emailIntent, "Send mail..."));

            Log.i("Finished sending mail.", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Log.e("MyAdapter", "There is no email client installed");
        }
    }
}

