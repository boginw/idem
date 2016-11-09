package fo.aivot.idem;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by hamburger on 17-02-2016.
 */
public class phoneCard extends Card {

    protected TextView mTitle;
    protected TextView mSecondaryTitle;
    protected ImageView sms;
    String phoneNumber;
    String phoneNumberType;
    Context context;
    /**
     * Constructor with a custom inner layout
     * @param context
     */
    public phoneCard(Context context, String pn, String pnt) {
        this(context, R.layout.phone_card_layout, pn, pnt);
        phoneNumber = pn;
        phoneNumberType = pnt;
        this.context = context;
    }

    /**
     *  @param context
     * @param innerLayout
     * @param number
     * @param subType
     */
    public phoneCard(Context context, int innerLayout, String number, String subType) {
        super(context, innerLayout);
        init();
        phoneNumber = number;
        phoneNumberType = subType;
        this.context = context;
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
                TextView phone = (TextView) view.findViewById(R.id.phoneNumber);
                try {
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                    Boolean turnedOn = sharedPrefs.getBoolean("prefSaveInstant", false);
                    String DialOrCall = (turnedOn?Intent.ACTION_CALL:Intent.ACTION_DIAL);

                    Intent callIntent = new Intent(DialOrCall);
                    callIntent.setData(Uri.parse("tel:+298" + phone.getText().toString()));
                    getContext().startActivity(callIntent);
                    mTracker tracker = new mTracker(context);
                    tracker.trackEvent("Person", "Call", "Phone");

                } catch (ActivityNotFoundException activityException) {
                    Log.d("Calling a Phone Number", "Call failed" + activityException);
                }
            }
        });
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        //Retrieve elements
        mTitle = (TextView) parent.findViewById(R.id.phoneNumber);
        mSecondaryTitle = (TextView) parent.findViewById(R.id.phoneNumberType);
        sms = (ImageView) parent.findViewById(R.id.imageView2);

        if (mTitle!=null) {
            mTitle.setText(phoneNumber);
        }

        if (mSecondaryTitle!=null) {
            mSecondaryTitle.setText(phoneNumberType);
        }

        if(!phoneNumberType.equals("Fastnet")){
            //Log.v("Type",mSecondaryTitle);
            sms.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", "+298"+phoneNumber, null)));
                }
            });
        }else{
            sms.setVisibility(View.GONE);
        }

    }
}