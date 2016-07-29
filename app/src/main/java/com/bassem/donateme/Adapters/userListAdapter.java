package com.bassem.donateme.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bassem.donateme.Helpers.AsyncResponse;
import com.bassem.donateme.Helpers.BackgroundWorker;
import com.bassem.donateme.Helpers.CircleTransform;
import com.bassem.donateme.Helpers.Helper;
import com.bassem.donateme.R;
import com.bassem.donateme.users;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class userListAdapter extends ArrayAdapter<users> {
    String userjson;
    JSONObject UserJson;
    Context context;
    int textViewResourceId;
    boolean IsFriendFragmentView = false;
    String filterText = "Name";
    ImageView frdicon = null;
    TextView txtFname = null;
    users myUser = null;
    List<users> USERS;
    List<users> FilteredUSERS;

    private ItemFilter mFilter = new ItemFilter();

    public String getFilterText() {
        return filterText;
    }

    public void setFilterText(String filterText) {
        this.filterText = filterText;
    }

    public boolean isFriendFragmentView() {
        return IsFriendFragmentView;
    }

    public void setFriendFragmentView(boolean friendFragmentView) {
        IsFriendFragmentView = friendFragmentView;
    }

    public userListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        FilteredUSERS = USERS;
    }

    public userListAdapter(Context context, int textViewResourceId, List<users> items) {
        super(context, textViewResourceId, items);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.USERS = items;
        FilteredUSERS = USERS;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.friendslayout, null);
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

        myUser = (users) getItem(position);
        try {
            SetAddFriendButton(view, myUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        txtFname.setText(myUser.getName());
        String imageUrl = null;
        if (!myUser.getImage().toString().equals("")) {
            imageUrl = Helper.getIfHttpUserImageUrl(myUser.getImage());
            Picasso.with(context).load(imageUrl).transform(new CircleTransform()).into(frdicon);
        } else {
            Picasso.with(context).load(R.mipmap.noimage).transform(new CircleTransform()).into(frdicon);
        }
    }

    private void SetLayoutElements(View view) {
        frdicon = (ImageView) view.findViewById(R.id.frdicon);
        txtFname = (TextView) view.findViewById(R.id.txtFname);
    }

    private void SetAddFriendButton(final View view, final users myUser) throws JSONException {
        ImageView btnaddasfriend = (ImageView) view.findViewById(R.id.btnaddasfriend);
        if (this.IsFriendFragmentView) {
            btnaddasfriend.setVisibility(View.INVISIBLE);
        } else {
            btnaddasfriend.setVisibility(View.VISIBLE);
        }

        final JSONObject FriendJson = new JSONObject(myUser.toJSON());
        String call = "";
        if (FriendJson.getString("Accepted") != null && FriendJson.getString("Accepted").toString().equals("0")) {
            call = "CancelFriendRequest";
            btnaddasfriend.setImageResource(R.mipmap.canceluser);

        } else {
            call = "AddAsAFriend";
            btnaddasfriend.setImageResource(R.mipmap.adduser);
        }

        final String finalCall = call;
        btnaddasfriend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    HashMap PostData = new HashMap();
                    PostData.put("call", finalCall);
                    PostData.put("ID", UserJson.getString("ID").toString());
                    PostData.put("FriendID", FriendJson.getString("ID").toString());
                    PostData.put("Name", UserJson.getString("Name").toString());
                    BackgroundWorker Worker = new BackgroundWorker(view.getContext(), (AsyncResponse) view.getContext(), PostData);
                    Worker.execute(Helper.getPhpHelperUrl());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    public int getCount() {
        return USERS.size();
    }

    public users getItem(int position) {
        return USERS.get(position);
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



                final List<users> list = USERS;

                int count = list.size();
                final ArrayList<users> nlist = new ArrayList<users>(count);

                users filterableUsers;

                for (int i = 0; i < count; i++) {
                    filterableUsers = list.get(i);
                    if (filterableUsers.getName().toLowerCase().contains(filterString)) {
                        nlist.add(filterableUsers);
                    }
                }
                results.values = nlist;
                results.count = nlist.size();
            }
            else{
                results.values = FilteredUSERS ;
                results.count = FilteredUSERS.size();

            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            USERS = (ArrayList<users>) results.values;
            notifyDataSetChanged();
        }

    }
}