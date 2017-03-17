
package elezov.com.justrentit;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import elezov.com.justrentit.model.Category;

/**
 * Created by USER on 14.03.2017.
 */

public class CategorySpinnerAdapter extends ArrayAdapter<Category> {
    Context context;
    List<Category> data= new ArrayList<>();


    public CategorySpinnerAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void addData(List<Category> data){
        this.data=data;
    }

    public int getCount(){
        return data.size();
    }

    public Category getItem(int position)
    {
        return data.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label=(TextView) super.getView(position, convertView, parent);
        label.setTextSize(17);

        label.setText(data.get(position).getName());
        label.setTextColor(Color.WHITE);

        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label=(TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(data.get(position).getName());
        label.setTextSize(13);
        return label;
    }
}
