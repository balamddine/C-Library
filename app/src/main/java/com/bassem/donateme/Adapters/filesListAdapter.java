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
import com.bassem.donateme.classes.files;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class filesListAdapter extends ArrayAdapter<files> {
    String userjson;
    JSONObject UserJson;
    Context context;
    int textViewResourceId;
    files myFiles = null;
    List<files> files;
    List<files> filteredFiles;
    TextView txtFname = null;
    TextView txtSharedWith =null;
    private ItemFilter mFilter = new ItemFilter();

    public filesListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        filteredFiles = filteredFiles;
    }

    public filesListAdapter(Context context, int textViewResourceId, List<files> items) {
        super(context, textViewResourceId, items);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.files = items;
        filteredFiles = files;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.fileslayout, null);
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
        myFiles = (files) getItem(position);
        txtFname.setText(myFiles.getName());
        txtSharedWith.setText(myFiles.getSharedWithUserName());
    }

    private void SetLayoutElements(View view) {
        txtFname = (TextView) view.findViewById(R.id.txtFilename);
        txtSharedWith = (TextView) view.findViewById(R.id.txtSharedWith);
    }

    public int getCount() {
        if(files !=null)
        {
            return files.size();
        }
        return 0;
    }

    public files getItem(int position) {
        return files.get(position);
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



                final List<files> list = files;

                int count = list.size();
                final ArrayList<files> nlist = new ArrayList<files>(count);

                files filterableFiles;

                for (int i = 0; i < count; i++) {
                    filterableFiles = list.get(i);
                    if (filterableFiles.getName().toLowerCase().contains(filterString)) {
                        nlist.add(filterableFiles);
                    }
                }
                results.values = nlist;
                results.count = nlist.size();
            }
            else{
                results.values = filteredFiles;
                results.count = filteredFiles.size();

            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            files = (ArrayList<files>) results.values;
            notifyDataSetChanged();
        }

    }
}