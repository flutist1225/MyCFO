package com.changda123.www.mycfo.Account.Statistics;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;


import com.changda123.www.mycfo.BaseClass.BaseFragment;
import com.changda123.www.mycfo.R;
import com.changda123.www.mycfo.Util.BarChartManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatisticalFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatisticalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticalFragment extends BaseFragment implements IStatisticsView {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private StatisticsPresenter mPresenter;
    private PieChart mPieChart;
    private BarChart mBarChart;


    public StatisticalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticalFragment newInstance(String param1, String param2) {
        StatisticalFragment fragment = new StatisticalFragment();
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
        mPresenter = new StatisticsPresenter(getContext());
        mPresenter.attachView(this);
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_statistical;
    }

    @Override
    protected void initMembersView(Bundle savedInstanceState) {
        mBarChart = (BarChart) mRootView.findViewById(R.id.bar_chart_total);
        mPieChart = (PieChart) mRootView.findViewById(R.id.pie_chart_total);
        BarChart barChartone   = (BarChart) mRootView.findViewById(R.id.bar_chart_one);
        TextView sumValue      = (TextView) mRootView.findViewById(R.id.statisticIdtext);


        //mPresenter.querySubtotalGroupByCategory(1, 2018);
        Date beginDate = new Date(2018-1900, 1, 1);
        mPresenter.queryTotalGroupByPeriod(3, beginDate.getTime());
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
    public void showRecords(List<ContentValues> data) {
        //showPieChart(data);
        showBarChart(data);
    }

    private void showBarChart(List<ContentValues> data) {
        int periodType = 3;
        BarChartManager barChartManager1 = new BarChartManager(mBarChart);

        //x轴的数据
        ArrayList<Float> xValues = new ArrayList<>();
        //y轴的数据()
        ArrayList<Float> yValues = new ArrayList<>();
        String xFieldName;
        if(periodType == 3){
            xFieldName = mPresenter.getFieldNamePeriodWeek();
        }else if(periodType == 1){
            xFieldName = mPresenter.getFieldNamePeriodWeek();
        }else{
            xFieldName = mPresenter.getFieldNamePeriodMonth();
        }
        for (ContentValues node : data){

            xValues.add(node.getAsFloat(xFieldName));
            yValues.add(node.getAsFloat(mPresenter.getFieldNameSumPrice()));

        }

        barChartManager1.showBarChart(xValues, yValues, getString(R.string.statistic_period_compare_all), Color.RED);
    }

    private void showPieChart(List<ContentValues> data) {
        Description description = new Description();
        description.setText(getString(R.string.statistic_category_compare));
        mPieChart.setDescription(description);
        mPieChart.setNoDataText(getString(R.string.statistic_no_data));

        List<Integer>  colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.YELLOW);
        colors.add(Color.GRAY);
        colors.add(Color.RED);
        colors.add(Color.MAGENTA);
        colors.add(Color.LTGRAY);
        colors.add(Color.BLACK);
        colors.add(Color.DKGRAY);
        colors.add(Color.CYAN);

        List<PieEntry> valueList = new ArrayList<>();
        for (ContentValues node : data){
            PieEntry pieData = new PieEntry(Float.parseFloat((String) node.get(mPresenter.getFieldNameSumPrice())), node.getAsString(mPresenter.getFieldNameCategory()));
            valueList.add(pieData);
        }

        PieDataSet dataSet = new PieDataSet(valueList, getString(R.string.column_category));
        dataSet.setColors(colors);
        dataSet.setValueTextSize(20f);

        PieData pieData = new PieData(dataSet);
        // pieData.setValueFormatter(new PercentFormatter());
        mPieChart.setData(pieData);
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
