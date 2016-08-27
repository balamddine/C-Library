package com.bassem.donateme.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.bassem.donateme.R;
import com.bassem.donateme.classes.Categories;
import com.bassem.donateme.classes.users;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class categoriesListAdapter extends ArrayAdapter<Categories> {
    String userjson;
    JSONObject UserJson;
    Context context;
    int textViewResourceId;
    Categories myCategories = null;
    List<Categories> CATEGORIES;
    List<Categories> FilteredCATEGORIES;
    TextView txtFname = null;

    private ItemFilter mFilter = new ItemFilter();

    public categoriesListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        FilteredCATEGORIES = FilteredCATEGORIES;
    }

    public categoriesListAdapter(Context context, int textViewResourceId, List<Categories> items) {
        super(context, textViewResourceId, items);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.CATEGORIES = items;
        FilteredCATEGORIES = CATEGORIES;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.categorieslayout, null);
        }
        SetLayoutElements(view);
        BindLayoutElemnts(view, position);
        return view;
    }

    private void BindLayoutElemnts(View view, int position) {
        SharedPreferences myprefs = this.context.getSharedPreferences("user", this.context.MODE_WORLD_READABLE);
        userjson = myprefs.getString("user", null);
        try {
            UserJson = new JSONObject(userjson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        myCategories = (Categories) getItem(position);
        txtFname.setText(myCategories.getName().toString());
    }

    private void SetLayoutElements(View view) {
        txtFname = (TextView) view.findViewById(R.id.txtFname);
    }

    public int getCount() {
        if(CATEGORIES!=null)
        {
            return CATEGORIES.size();
        }
        return 0;
    }

    public Categories getItem(int position) {
        return CATEGORIES.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                String filterString = constraint.toString().toLowerCase();



                final List<Categories> list = CATEGORIES;

                int count = list.size();
                final ArrayList<Categories> nlist = new ArrayList<Categories>(count);

                Categories filterableCategories;

                for (int i = 0; i < count; i++) {
                    filterableCategories= list.get(i);
                    if (filterableCategories.getName().toLowerCase().contains(filterString)) {
                        nlist.add(filterableCategories);
                    }
                }
                results.values = nlist;
                results.count = nlist.size();
            }
            else{
                results.values = FilteredCATEGORIES;
                results.count = FilteredCATEGORIES.size();

            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            CATEGORIES = (ArrayList<Categories>) results.values;
            notifyDataSetChanged();
        }

    }
}