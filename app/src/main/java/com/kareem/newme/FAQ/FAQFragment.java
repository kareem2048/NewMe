package com.kareem.newme.FAQ;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.kareem.newme.Connections.VolleyRequest;
import com.kareem.newme.Constants;
import com.kareem.newme.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FAQFragment extends Fragment  {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private MyFAQRecyclerViewAdapter myFAQRecyclerViewAdapter;
    private boolean isInit;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FAQFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FAQFragment newInstance(int columnCount) {
        FAQFragment fragment = new FAQFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_faq_list, container, false);

        // Set the adapter
        View view = fragmentView.findViewById(R.id.faq_list);
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            myFAQRecyclerViewAdapter = new MyFAQRecyclerViewAdapter();
            recyclerView.setAdapter(myFAQRecyclerViewAdapter);
            grabData();
            isInit = true;
        }
        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        grabData();
    }

    private void grabData()
    {
        if (myFAQRecyclerViewAdapter == null) return;
        Map<String , String> stringMap = new HashMap<>();
        stringMap.put("req", "getFaq");
        VolleyRequest volleyRequest = new VolleyRequest(Constants.BASE_URL,stringMap,getActivity()) {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(String response) {
                myFAQRecyclerViewAdapter.getmValues().clear();
            FAQArray faqs = new Gson().fromJson(response, FAQArray.class);
                myFAQRecyclerViewAdapter.getmValues().addAll(faqs.getFaqs());
                myFAQRecyclerViewAdapter.notifyDataSetChanged();
            }
        };
        volleyRequest.start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

}
