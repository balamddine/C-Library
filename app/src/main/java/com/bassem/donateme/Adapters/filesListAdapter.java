package com.bassem.donateme.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bassem.donateme.R;
import com.bassem.donateme.classes.files;
import com.bassem.donateme.sharing_download;

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
    ImageView img_Download = null;
    boolean CanDownload=false;
    boolean IsRepository=false;
    private ItemFilter mFilter = new ItemFilter();

    public filesListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        filteredFiles = filteredFiles;
    }

    public boolean isCanDownload() {
        return CanDownload;
    }

    public void setCanDownload(boolean canDownload) {
        CanDownload = canDownload;
    }

    public filesListAdapter(Context context, int textViewResourceId, List<files> items, boolean Candownload,boolean IsRepository) {
        super(context, textViewResourceId, items);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.files = items;
        filteredFiles = files;
        this.CanDownload = Candownload;
        this.IsRepository = IsRepository;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            if(IsRepository){
                view = vi.inflate(R.layout.filerepositorylayout, null);
            }else{
                view = vi.inflate(R.layout.fileslayout, null);
            }

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
        if(img_Download!=null)
        {
            if (CanDownload)
            {
                img_Download.setVisibility(View.VISIBLE);
                SetDownloadBtn(myFiles);

            }
            else
            {
                img_Download.setVisibility(View.INVISIBLE);
            }
        }

    }

    private void SetDownloadBtn(final files myFiles) {
        img_Download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent downloadIntent = new Intent(context,sharing_download.class);
                downloadIntent.putExtra("FileID",""+myFiles.getID());
                downloadIntent.putExtra("FileName",myFiles.getName());
                context.startActivity(downloadIntent);
            }
        });
    }

    private void SetLayoutElements(View view) {
        txtFname = (TextView) view.findViewById(R.id.txtFilename);
        txtSharedWith = (TextView) view.findViewById(R.id.txtSharedWith);
        img_Download = (ImageView) view.findViewById(R.id.img_Download);
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