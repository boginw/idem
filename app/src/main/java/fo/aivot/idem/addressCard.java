package fo.aivot.idem;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by hamburger on 17-02-2016.
 */
public class addressCard extends Card {

    protected TextView mTitle;
    protected TextView mSecondaryTitle;
    protected TextView lat;
    protected TextView lon;
    String[] address;
    Context context;




    public addressCard(Context context, int innerLayout, String[] addressInfo) {
        super(context, innerLayout);
        this.context=context;
        this.address = addressInfo;
        init();
    }

    /**
     * Init
     */
    private void init(){

        //No Header

        //Set a OnClickListener listener
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                TextView getLat = (TextView) view.findViewById(R.id.lon);
                TextView getLon = (TextView) view.findViewById(R.id.lat);
                TextView addressName = (TextView) view.findViewById(R.id.mainAddress);
                try {
                    String uri = "http://maps.google.com/maps?q=loc:"+getLat.getText()+","+getLon.getText()+" ("+addressName.getText()+")";
                    Intent i = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse(uri));
                    i.setClassName("com.google.android.apps.maps",
                            "com.google.android.maps.MapsActivity");
                    context.startActivity(i);
                    mTracker tracker = new mTracker(context);
                    tracker.trackEvent("Person", "Maps", "Open");
                } catch (ActivityNotFoundException activityException) {
                }
            }
        });
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        //Retrieve elements
        mTitle = (TextView) parent.findViewById(R.id.mainAddress);
        mSecondaryTitle = (TextView) parent.findViewById(R.id.subAddress);
        lat = (TextView) parent.findViewById(R.id.lat);
        lon = (TextView) parent.findViewById(R.id.lon);


        if (mTitle!=null)
            mTitle.setText(address[0]+" "+address[1]);

        if (mSecondaryTitle!=null)
            mSecondaryTitle.setText(address[3]+" "+address[2]+",\n"+address[4]);

        if (lat!=null)
            lat.setText(address[5]);

        if (lon!=null)
            lon.setText(address[6]);
    }
}