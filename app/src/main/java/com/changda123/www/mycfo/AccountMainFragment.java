package com.changda123.www.mycfo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountMainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountMainFragment extends Fragment {

    private static final String TAG = "MyCFO_AccountMainFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View mView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AccountMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountMainFragment newInstance(String param1, String param2) {
        AccountMainFragment fragment = new AccountMainFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MyLog.d(TAG, "TabMenuActivity  onCreate" );
        mView = inflater.inflate(R.layout.fragment_account_main, container, false);
        initTabView();
        return mView;
    }

    private void initTabView(){
        TabLayout tabLayout = (TabLayout)mView.findViewById(R.id.tab) ;
        ViewPager viewpager = (ViewPager)mView.findViewById(R.id.viewpager);
        List<Fragment> fragments = new ArrayList<>();

        fragments.add(new AddRecordFragment());
        fragments.add(new ListRecordFragment());
        fragments.add(new StatisticalFragment());

        TitleFragmentPagerAdapter adapter = new TitleFragmentPagerAdapter(getChildFragmentManager(), fragments, new String[]{"新增交易", "交易记录", "统计报表"});
        viewpager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewpager);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
