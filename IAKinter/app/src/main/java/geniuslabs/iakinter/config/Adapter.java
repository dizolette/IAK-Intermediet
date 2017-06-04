package geniuslabs.iakinter.config;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static android.R.attr.data;
import static android.R.attr.thickness;

import geniuslabs.iakinter.R;

/**
 * Created by ecko on 5/27/2017.
 */

public class Adapter extends SimpleAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<HashMap<String, String>> arrayList;

    public Adapter(Context context, ArrayList<HashMap<String,String>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.arrayList = data;
        this.context = context;
        inflater.from(context);
    }

/*    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        return super.getView(position, convertView, parent);
//            View view = super.getView(position, convertView, parent);
    }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        ImageView image = (ImageView) view.findViewById(R.id.img_image);

        Picasso.with(context)
                .load(Server.cover + arrayList.get(position).get("image"))
                .placeholder(R.drawable.noimage)
                .fit().centerCrop()
                .error(R.drawable.nopreview)
                .into(image);

        return view;

    }
}
