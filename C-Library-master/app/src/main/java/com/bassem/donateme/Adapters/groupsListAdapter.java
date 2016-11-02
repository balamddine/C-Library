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
import com.bassem.donateme.classes.Groups;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class groupsListAdapter extends ArrayAdapter<Groups> {
    String userjson;
    JSONObject UserJson;
    Context context;
    int textViewResourceId;
    Groups myGroups = null;
    List<Groups> GROUPS;
    List<Groups> FilteredGROPUS;
    TextView txtgroupname = null;

    private ItemFilter mFilter = new ItemFilter();

    public groupsListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        FilteredGROPUS = FilteredGROPUS;
    }

    public groupsListAdapter(Context context, int textViewResourceId, List<Groups> items) {
        super(context, textViewResourceId, items);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.GROUPS = items;
        FilteredGROPUS = GROUPS;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.grouplayout, null);
        }
        SetLayoutElements(view);
        BindLayoutElemnts(view, position);
        return view;
    }

    private void BindLayoutElemnts(View view, int position) {
        /*SharedPreferences myprefs = this.context.getSharedPreferences("user", this.context.MODE_WORLD_READABLE);
        userjson = myprefs.getString("user", null);
        try {
            UserJson = new JSONObject(userjson);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        myGroups = (Groups) getItem(position);
        if(myGroups!=null)
            txtgroupname.setText(myGroups.getName().toString());
    }

    private void SetLayoutElements(View view) {
        txtgroupname = (TextView) view.findViewById(R.id.txtgroupname);
    }

    public int getCount() {
        if(GROUPS!=null)
        {
            return GROUPS.size();
        }
        return 0;
    }

    public Groups getItem(int position) {
        return GROUPS.get(position);
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

                final List<Groups> list = GROUPS;

                int count = list.size();
                final ArrayList<Groups> nlist = new ArrayList<Groups>(count);

                Groups filterableGroups;

                for (int i = 0; i < count; i++) {
                    filterableGroups= list.get(i);
                    if (filterableGroups.getName().toLowerCase().contains(filterString)) {
                        nlist.add(filterableGroups);
                    }
                }
                results.values = nlist;
                results.count = nlist.size();
            }
            else{
                results.values = FilteredGROPUS;
                results.count = FilteredGROPUS.size();

            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            GROUPS = (ArrayList<Groups>) results.values;
            notifyDataSetChanged();
        }

    }
}