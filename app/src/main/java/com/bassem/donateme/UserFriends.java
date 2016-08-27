package com.bassem.donateme;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.bassem.donateme.Adapters.userListAdapter;
import com.bassem.donateme.Helpers.AsyncResponse;
import com.bassem.donateme.Helpers.BackgroundWorker;
import com.bassem.donateme.Helpers.Helper;
import com.bassem.donateme.classes.users;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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
    SharedPreferences myprefs;
    String userjson;
    JSONObject UserJson;
    TextView actionbar_notifcation_textview;
    ImageView imgFriendReqNotification;
    String PopUpRequestIntentResult ="";
    private static final int FILE_SELECT_CODE = 0;
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
       // Helper.CheckInternetConnection(getContext());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
        getActivity().setTitle("Friend list");

        myprefs =getActivity().getSharedPreferences("user", getActivity().MODE_WORLD_READABLE);
        userjson = myprefs.getString("user",null);
        try {
            UserJson=new JSONObject(userjson);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        GetFriendRequestNotifications();
        GetFriendList();


    }

    private void GetFriendRequestNotifications() {
        try {
            users.GetAllFriendRequestsNotifications(getContext(),this,UserJson.getString("ID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void GetFriendList() {
        try {
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
        actionbar_notifcation_textview = (TextView) badgeLayout.findViewById(R.id.actionbar_notifcation_textview);
        imgFriendReqNotification =(ImageView) badgeLayout.findViewById(R.id.imgFriendReqNotification);
        imgFriendReqNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent myPopUpRequestIntent = new Intent(getContext(), activity_friend_requestPopUp.class);
                    myPopUpRequestIntent.putExtra("jsonResult",PopUpRequestIntentResult);
                    startActivity(myPopUpRequestIntent);
            }
        });
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
        String call = Helper.GetJsonCallResult(result,"user");
        String JsonStatus =Helper.GetJsonStatusResult(result,"user");
        if(call.equals("DeleteFriend"))
        {
           // getActivity().finish();
           // startActivity(getActivity().getIntent());
           ReloadFragment();
            return;
        }
        if(JsonStatus.equals("0"))
        {
            layoutNoDataFound.setVisibility(View.VISIBLE);
            return;
        }

        if (call.equals("GetFriendsRequest"))
        {
            PopUpRequestIntentResult =result;
            arlst = Helper.GetArrayListFromJsonString(result);
            int s = arlst.size();
            actionbar_notifcation_textview.setText(""+s);
        }
        else{

            layoutNoDataFound.setVisibility(View.INVISIBLE);

            SharedPreferences myprefs = getActivity().getSharedPreferences("friends",getActivity().MODE_WORLD_READABLE);
            myprefs.edit().putString("friends",result).apply();

            arlst = Helper.GetArrayListFromJsonString(result);
            final LayoutInflater mInflater = (LayoutInflater)getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            MyuserListAdapter =new userListAdapter(getActivity(), R.layout.friendslayout,arlst);
            MyuserListAdapter.setFriendFragmentView(true);
            if(listview!=null)
            {
                listview.setAdapter(null);
                if (MyuserListAdapter!=null)
                {
                    listview.setAdapter(MyuserListAdapter);
                    MyuserListAdapter.notifyDataSetChanged();
                    listview.setTextFilterEnabled(true);
                    registerForContextMenu(listview);
                }

            }

        }

    }

    private void ReloadFragment() {
        arlst.clear();
        MyuserListAdapter.notifyDataSetChanged();

        GetFriendList();
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
    public static int FriendID=0;
    @Override
    public boolean onContextItemSelected(MenuItem item) {
       final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch(item.getTitle().toString()) {
            case "Unfriend":
                try {
                    HashMap PostData = new HashMap();
                    PostData.put("call", "DeleteFriend");
                    PostData.put("ID",UserJson.getString("ID").toString());
                    PostData.put("FriendID",""+arlst.get(info.position).getID());
                    BackgroundWorker Worker = new BackgroundWorker(getContext(), this, PostData);
                    Worker.execute(Helper.getPhpHelperUrl());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            case "View Profile":
                Intent FriendProfileIntent = new Intent(getContext(),editprofile.class);
                FriendProfileIntent.putExtra("FriendEmail",arlst.get(info.position).getEmail());
                startActivity(FriendProfileIntent);
                return true;
            case "Share":
                FriendID = arlst.get(info.position).getID();
                SetSharing();
                
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void SetSharing() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimetypes =Helper.GetmimeTypes();
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
       //
    try {
        startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
    } catch (android.content.ActivityNotFoundException ex) {
        Toast.makeText(getContext(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
    }


    }
    public static final int RESULT_OK = -1;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri fileUri = data.getData();
                   long size = Helper.GetFileSize(getActivity(),fileUri);
                    Log.d("File", "File Uri: " + fileUri.toString());
                    String Fid = ""+data.getStringExtra("FID");
                  //  File myFile = new File(fileUri.getPath());
                  //  String s = myFile.getAbsolutePath();
                    // Get the path
                    Intent intent = new Intent(getContext(),activity_file_sharing.class);
                    intent.putExtra("Filepath", fileUri.toString());
                    intent.putExtra("FriendID", ""+FriendID);
                    intent.putExtra("Filesize", size);

                    startActivity(intent);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
