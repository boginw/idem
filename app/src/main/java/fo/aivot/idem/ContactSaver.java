package fo.aivot.idem;

import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by hamburger on 17-02-2016.
 */
public class ContactSaver {

    public ContactSaver(){
        Intent intent = new Intent(
                ContactsContract.Intents.SHOW_OR_CREATE_CONTACT,
                Uri.parse("tel:" + 12346));
        intent.putExtra(ContactsContract.Intents.EXTRA_FORCE_CREATE, true);
        //startActivity(intent);
    }
}
