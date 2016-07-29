package com.bassem.donateme;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;


import com.bassem.donateme.Adapters.userListAdapter;
import com.bassem.donateme.Helpers.AsyncResponse;
import com.bassem.donateme.Helpers.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserFriends.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserFriends#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFriends extends Fragment implements AsyncResponse,SearchView.OnQueryTextListener {
    EditText txtEmail,txtPassword;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public   View myView = null;
    public   ListView listview;
    ArrayList<users> arlst=null;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public userListAdapter MyuserListAdapter= null ;
    private MenuItem searchMenuItem;
    private SearchView searchView;
   public LinearLayout layoutNoDataFound;
    private OnFragmentInteractionListener mListener;

    public UserFriends() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFriends.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFriends newInstance(String param1, String param2) {
        UserFriends fragment = new UserFriends();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
        GetFriendList();
        getActivity().setTitle("Friend list");
    }

    private void GetFriendList() {
        try {
            SharedPreferences myprefs =getActivity().getSharedPreferences("user", getActivity().MODE_WORLD_READABLE);
            String userjson = myprefs.getString("user",null);
            JSONObject UserJson =new JSONObject(userjson);
            users.GetFriendList(getContext(),this,UserJson.getString("ID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_user_profile_fragments_menu, menu);
        RelativeLayout badgeLayout = (RelativeLayout) menu.findItem(R.id.menu_friendReq).getActionView();
        TextView tv = (TextView) badgeLayout.findViewById(R.id.actionbar_notifcation_textview);
        tv.setText("12");

        SetSearchMenuItem(menu);

    }

    private void SetSearchMenuItem(Menu menu) {
        SearchManager searchManager = (SearchManager)getContext().getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.searchusr);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                GetFriendList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.fragment_user_friends, container, false);
        listview =(ListView)myView.findViewById(R.id.lstFriends);
        layoutNoDataFound = (LinearLayout)myView.findViewById(R.id.layoutNoDataFound);
        return myView;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void processFinish(String result) {

        String JsonStatus =Helper.GetJsonStatusResult(result);
        if(JsonStatus.equals("0"))
        {
            layoutNoDataFound.setVisibility(View.VISIBLE);
            return;
        }
        layoutNoDataFound.setVisibility(View.INVISIBLE);

        SharedPreferences myprefs = getActivity().getSharedPreferences("user",getActivity().MODE_WORLD_READABLE);
        myprefs.edit().putString("friends",result).apply();

        arlst = Helper.GetArrayListFromJsonString(result);
        final LayoutInflater mInflater = (LayoutInflater)getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        MyuserListAdapter =new userListAdapter(getActivity(), R.layout.friendslayout,arlst);
        MyuserListAdapter.setFriendFragmentView(true);
        if(listview!=null)
        {
            listview.setAdapter(null);
            listview.setAdapter(MyuserListAdapter);
            MyuserListAdapter.notifyDataSetChanged();
            listview.setTextFilterEnabled(true);
        }
    }
    AdapterView.AdapterContextMenuInfo menuinfo = null;
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {


        if (v.getId()==R.id.lstFriends) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(arlst.get(info.position).getName());
            String[] menuItems = getResources().getStringArray(R.array.userFriendmenu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        MyuserListAdapter.getFilter().filter(newText.toString().toLowerCase(Locale.getDefault()));
        return true;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
