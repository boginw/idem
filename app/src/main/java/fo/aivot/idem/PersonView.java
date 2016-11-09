package fo.aivot.idem;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.xgc1986.ripplebutton.widget.RippleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardView;

public class PersonView extends ActionBarActivity {
    JSONObject contact = null;
    Context context;
    mTracker tracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        try {
            String contactJSON = getIntent().getSerializableExtra("personObject").toString();
            contact = new JSONObject(contactJSON);
            getSupportActionBar().setTitle(contact.getString("firstName")+" "+contact.getString("lastName"));
            JSONArray phoneNumbers = contact.getJSONArray("phoneNumbers");
            ViewGroup layout = (ViewGroup) findViewById(R.id.phoneLayout);

            for (int i=0; i < phoneNumbers.length(); i++) {
                String Number = phoneNumbers.getJSONObject(i).getString("phoneNumber");
                String subType = phoneNumbers.getJSONObject(i).getString("subType");


                Card card = new phoneCard(this,R.layout.phone_card_layout,Number,subType);
                CardView cardView = new CardView(this);//(CardView) findViewById(R.id.phoneNumber);

                cardView.setLayoutParams(
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics())));
                cardView.setCard(card);

                layout.addView(cardView);
            }

            JSONObject address = contact.getJSONObject("ftAddress");

            String[] addressInfo = new String[]{
                    address.getString("streetName"),
                    address.getString("streetNumber"),
                    address.getString("city"),
                    address.getString("zip"),
                    address.getString("countryName"),
                    address.getJSONObject("coord").getString("x"),
                    address.getJSONObject("coord").getString("y")
            };

            layout = (ViewGroup) findViewById(R.id.addressLayout);

            Card card = new addressCard(this,R.layout.address_card_layout,addressInfo);
            CardView cardView = new CardView(this);//(CardView) findViewById(R.id.phoneNumber);

            cardView.setCard(card);
            layout.addView(cardView);


            RippleButton rb = (RippleButton)findViewById(R.id.save_button);
            rb.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //save contact
                    JSONArray phoneNumbers = null;
                    Intent newIntent = new Intent(Intent.ACTION_INSERT,
                            ContactsContract.Contacts.CONTENT_URI);
                    try {
                        phoneNumbers = contact.getJSONArray("phoneNumbers");
                        for (int i=0; i < phoneNumbers.length(); i++) {
                            String Number = "+298"+phoneNumbers.getJSONObject(i).getString("phoneNumber");
                            Integer subType = (
                                    phoneNumbers.getJSONObject(i).getString("subType").equals("Fartelefon") ?
                                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE :
                                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME
                            );
                            switch (i){
                                case 0:
                                    newIntent.putExtra(ContactsContract.Intents.Insert.PHONE, Number);
                                    newIntent.putExtra(ContactsContract.Intents.Insert.PHONE_ISPRIMARY, 1);
                                    newIntent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, subType);
                                    break;
                                case 1:
                                    newIntent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, Number);
                                    newIntent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE	, subType);
                                    break;
                                case 2:
                                    newIntent.putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE, Number);
                                    newIntent.putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE_TYPE	, subType);
                                    break;
                            }


                        }
                        JSONObject address = contact.getJSONObject("ftAddress");
                        ArrayList<ContentValues> data = new ArrayList<ContentValues>();
                        ContentValues name = new ContentValues();
                        name.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);
                        name.put(ContactsContract.CommonDataKinds.StructuredPostal.STREET, address.getString("streetName") + " " + address.getString("streetNumber"));
                        name.put(ContactsContract.CommonDataKinds.StructuredPostal.CITY, address.getString("city"));
                        name.put(ContactsContract.CommonDataKinds.StructuredPostal.REGION, contact.getString("area"));
                        name.put(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, address.getString("zip"));
                        name.put(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, address.getString("countryName"));
                        int addressType = ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME;
                        name.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, addressType);
                        data.add(name);
                        newIntent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);
                        newIntent.putExtra(ContactsContract.Intents.Insert.NAME, contact.getString("firstName")+" "+contact.getString("lastName"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(newIntent);
                    tracker.trackEvent("Person", "Save","Contact");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tracker = new mTracker(context);
        tracker.trackScreenView("Person");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

}
