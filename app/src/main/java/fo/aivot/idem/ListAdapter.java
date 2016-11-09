package fo.aivot.idem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by hamburger on 16-02-2016.
 */
public class ListAdapter extends BaseAdapter {

    Context context;
    List<String[]> data;
    private static LayoutInflater inflater = null;

    public ListAdapter(Context context, List<String[]> data) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public String[] getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.row, null);

        TextView title = (TextView) vi.findViewById(R.id.title);
        TextView subtitle = (TextView) vi.findViewById(R.id.subtitle);
        TextView number = (TextView) vi.findViewById(R.id.number);
        String[] dataPos = getItem(position);

        String name = dataPos[1]+" "+dataPos[2];

        title.setText(name);
        number.setText(dataPos[3]);
        subtitle.setText(dataPos[0]);
        return vi;
    }
}

