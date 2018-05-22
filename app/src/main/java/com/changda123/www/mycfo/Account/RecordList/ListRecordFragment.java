package com.changda123.www.mycfo.Account.RecordList;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.changda123.www.mycfo.BaseClass.BaseFragment;
import com.changda123.www.mycfo.MainActivity;
import com.changda123.www.mycfo.R;
import com.changda123.www.mycfo.Util.MyLog;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ListRecordFragment extends BaseFragment implements IListRecordView{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = "MyCFO_ListRecordFragment";
    private RadioGroup mRadioGroupQueryRange;
    private Button mButtonQuery;

    // TODO: Customize parameters
    private int mColumnCount = 1;
    private RecyclerView mRecyclerView;
    private OnListFragmentInteractionListener mListener;
    private boolean mIsSetAdapter = false;

    ListRecordPresenter mPresenter;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListRecordFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ListRecordFragment newInstance(int columnCount) {
        ListRecordFragment fragment = new ListRecordFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.d(TAG, "ListRecordFragment  onCreate ok" );
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        mPresenter = new ListRecordPresenter(getContext());
        mPresenter.attachView(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //断开View引用
        mPresenter.detachView();
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_listrecord_list;
    }

    @Override
    protected void initMembersView(Bundle savedInstanceState) {
        final View view = mRootView;
        Context context = view.getContext();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewId);
        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        mRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));


        mRadioGroupQueryRange = (RadioGroup) view.findViewById(R.id.idRadioGroupQueryRange);

        mButtonQuery = (Button)view.findViewById(R.id.idButtonQueryList);

        mButtonQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int days;
                // 检查必选参数
                int radioId = mRadioGroupQueryRange.getCheckedRadioButtonId();
                switch (radioId) {
                    case R.id.idRadioButton1week:
                        days = 7;
                        break;
                    case R.id.idRadioButton1month:
                        days = 30;
                        break;
                    case R.id.idRadioButton3month:
                        days = 92;
                        break;
                    case R.id.idRadioButton6month:
                        days = 183;
                        break;
                    case R.id.idRadioButton1year:
                        days = 365;
                        break;
                    default:
                        days = 30;
                        break;
                }
                MyLog.d(TAG, "initView  days:" + days);
                mPresenter.listRecords(days);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MyLog.d(TAG, "ListRecordFragment  onAttach ok" );
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void showRecords(List<ContentValues> data) {
        setRecyclerViewAdpter(data);
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
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(ContentValues item);
    }

    void setRecyclerViewAdpter(List recordList){

        if(null != recordList) {
            mRecyclerView.setAdapter(new MyListRecordRecyclerViewAdapter(recordList, mListener));

        }
    }
}
