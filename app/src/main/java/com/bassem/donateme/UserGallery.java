package com.bassem.donateme;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bassem.donateme.Adapters.categoriesListAdapter;
import com.bassem.donateme.Adapters.userListAdapter;
import com.bassem.donateme.Helpers.AsyncResponse;
import com.bassem.donateme.Helpers.BackgroundWorker;
import com.bassem.donateme.Helpers.Helper;
import com.bassem.donateme.classes.Categories;
import com.bassem.donateme.classes.users;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserGallery.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserGallery#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserGallery extends Fragment implements AsyncResponse,SearchView.OnQueryTextListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public   View myView = null;
    public   ListView listview;
    ArrayList<Categories> arlst=null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public categoriesListAdapter MyCategoriesListAdapter= null ;
    private MenuItem searchMenuItem;
    private SearchView searchView;

    private OnFragmentInteractionListener mListener;

    public UserGallery() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserGallery.
     */
    // TODO: Rename and change types and number of parameters
    public static UserGallery newInstance(String param1, String param2) {
        UserGallery fragment = new UserGallery();
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
      //  Helper.CheckInternetConnection(getContext());
        setHasOptionsMenu(true);
        getActivity().setTitle("Gallery");

        GetUserCategories();
    }

    private void GetUserCategories() {
        users CurrentUser = users.GetCurrentuser(getContext());
        HashMap PostData = new HashMap();
        PostData.put("call", "GetUserCategories");
        PostData.put("UserID",""+CurrentUser.getID());
        BackgroundWorker Worker = new BackgroundWorker(getContext(), this, PostData);
        Worker.execute(Helper.getPhpHelperUrl());
    }
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_gallery_fragment_menu, menu);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_user_gallery, container, false);
        listview =(ListView)myView.findViewById(R.id.lstGallery);
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
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        if (v.getId()==R.id.lstGallery) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            if(arlst.get(info.position).getID()==-1)// default category
            {
                return;
            }
            menu.setHeaderTitle(arlst.get(info.position).getName());
            String[] menuItems = getResources().getStringArray(R.array.gallerylisting);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getTitle().toString()) {
            case "Remove":
                try {
                    HashMap PostData = new HashMap();
                    PostData.put("call", "RemoveCategory");
                    PostData.put("CatID", "" + arlst.get(info.position).getID());
                    PostData.put("UserID", "" + users.GetCurrentuser(getContext()).getID());
                    BackgroundWorker Worker = new BackgroundWorker(getContext(), this, PostData);
                    Worker.execute(Helper.getPhpHelperUrl());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
        }
        return true;
    }

    @Override
    public void processFinish(String result) {
        try{
            String call = Helper.GetJsonCallResult(result,"categories");
            String msg = Helper.GetJsonMessageResult(result,"categories");
            if(call.equals("RemoveCategory"))
            {
                Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                ReloadFragment();
                return;
            }

            SharedPreferences myprefs = getActivity().getSharedPreferences("userGalleries",getActivity().MODE_WORLD_READABLE);
            myprefs.edit().putString("userGalleries",result).apply();

            arlst = Helper.GetCategoriesArrayListFromJsonString(result);
            final LayoutInflater mInflater = (LayoutInflater)getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            MyCategoriesListAdapter =new categoriesListAdapter(getActivity(), R.layout.categorieslayout,arlst);
            if(listview!=null)
            {
                listview.setAdapter(null);
                if (MyCategoriesListAdapter!=null)
                {
                    listview.setAdapter(MyCategoriesListAdapter);
                    MyCategoriesListAdapter.notifyDataSetChanged();
                    listview.setTextFilterEnabled(true);
                    SetListViewClickEvent();
                    registerForContextMenu(listview);
                }
            }
        }
        catch(Exception ex)
        {
            Toast.makeText(this.getContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    private void SetListViewClickEvent() {
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(getContext(), activity_user_Files.class);
                String CatID = ""+arlst.get(position).getID();
                String CatName = ""+arlst.get(position).getName();
                intent.putExtra("CatID", CatID);
                intent.putExtra("CatName", CatName);
                startActivity(intent);
            }
        });
    }

    private void ReloadFragment() {
        arlst.clear();
        MyCategoriesListAdapter.notifyDataSetChanged();
        GetUserCategories();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        MyCategoriesListAdapter.getFilter().filter(newText.toString().toLowerCase(Locale.getDefault()));
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
