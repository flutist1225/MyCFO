package com.changda123.www.mycfo.Account.Statistics;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.changda123.www.mycfo.Account.AccountBaseModel;
import com.changda123.www.mycfo.BaseClass.BaseFragment;
import com.changda123.www.mycfo.R;
import com.changda123.www.mycfo.Util.BarChartManager;
import com.changda123.www.mycfo.Util.MyLog;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.view.View.VISIBLE;


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

    private static final String TAG = "StatisticalFragment";
    private static final int CATEGORY_ALL = 0;
    private static final int CATEGORY_CLOTH = 1;
    private static final int CATEGORY_EAT = 2;
    private static final int CATEGORY_HOUSE = 3;
    private static final int CATEGORY_RUN = 4;
    private static final int CATEGORY_HAPPY = 5;
    private static final int CATEGORY_LIFE = 6;
    private static final int CATEGORY_EDUCATION = 7;
    private static final int CATEGORY_INVEST = 8;
    private static final int CATEGORY_DEBT = 9;
    private static final int CATEGORY_COMMUNICATION = 10;

    private static final int PERIOD_YEAR  = AccountBaseModel.STATISTIC_PERIOD_BY_YEAR;
    private static final int PERIOD_MONTH = AccountBaseModel.STATISTIC_PERIOD_BY_MONTH;
    private static final int PERIOD_WEEK  = AccountBaseModel.STATISTIC_PERIOD_BY_WEEK;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private StatisticsPresenter mPresenter;
    private PieChart mPieChart;
    private BarChart mBarChart;
    private TextView mShowDate;
    private RadioGroup mRadioGroupCategory;
    private RadioGroup mRadioGroupPeriod;
    private int mYear = 2018;
    private int mMonthOfYear = 0;
    private int mDayOfMonth  = 1;

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
        mBarChart = (BarChart) mRootView.findViewById(R.id.bar_chart);
        mPieChart = (PieChart) mRootView.findViewById(R.id.pie_chart);
        Button changeDay = (Button) mRootView.findViewById(R.id.Sta_change_day);
        Button queryButton = (Button) mRootView.findViewById(R.id.Sta_button_query);
        mRadioGroupCategory = (RadioGroup) mRootView.findViewById(R.id.idStatisticCategory);
        mRadioGroupPeriod = (RadioGroup) mRootView.findViewById(R.id.idStatisticPeriod);
        mShowDate = (TextView) mRootView.findViewById(R.id.Sta_show_start_date);


        showDate(2018,1,1);
        changeDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(getContext());
            }
        });

        queryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                int category = getCategory();
                int period   = getPeriod();
                Date beginDate = new Date(mYear-1900, mMonthOfYear, mDayOfMonth);

                String categoryName = null;
                if(category != CATEGORY_ALL) {
                    RadioButton categoryButton = mRootView.findViewById(mRadioGroupCategory.getCheckedRadioButtonId());
                    categoryName = categoryButton.getText().toString();
                }

                MyLog.d(TAG, "category:"+category + " period:"+period + " year:"+mYear);
                mPresenter.querySubtotalByPeriod(categoryName, period, beginDate.getTime());

            }
        });
    }

    // 点击事件,日期
    public void setDate(Context context) {

        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                mYear = year;
                mMonthOfYear = monthOfYear;
                mDayOfMonth  = dayOfMonth;
                showDate(year,monthOfYear+1, dayOfMonth);
            }
        }, mYear, mMonthOfYear, mDayOfMonth).show();

    }

    private void showDate(int year, int month, int day){
        mShowDate.setText(String.format(Locale.CHINA, "%d-%d-%d", year, month, day));
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private int getCategory(){
        int radioId = mRadioGroupCategory.getCheckedRadioButtonId();
        int category;
        switch (radioId) {
            case R.id.Sta_radio_all:
                category = CATEGORY_ALL;
                break;
            case R.id.Sta_radio_cloth:
                category = CATEGORY_CLOTH;
                break;
            case R.id.Sta_radio_eat:
                category = CATEGORY_EAT;
                break;
            case R.id.Sta_radio_house:
                category = CATEGORY_HOUSE;
                break;
            case R.id.Sta_radio_run:
                category = CATEGORY_RUN;
                break;
            case R.id.Sta_radio_happy:
                category = CATEGORY_HAPPY;
                break;
            case R.id.Sta_radio_life:
                category = CATEGORY_LIFE;
                break;
            case R.id.Sta_radio_education:
                category = CATEGORY_EDUCATION;
                break;
            case R.id.Sta_radio_invest:
                category = CATEGORY_INVEST;
                break;
            case R.id.Sta_radio_debt:
                category = CATEGORY_DEBT;
                break;
            case R.id.Sta_radio_communication:
                category = CATEGORY_COMMUNICATION;
                break;
            default:
                category = -1;
                break;
        }
        return category;
    }
    private int getPeriod(){
        int radioId = mRadioGroupPeriod.getCheckedRadioButtonId();
        int value;
        switch (radioId) {
            case R.id.Sta_radio_year:
                value = PERIOD_YEAR;
                break;
            case R.id.Sta_radio_month:
                value = PERIOD_MONTH;
                break;
            case R.id.Sta_radio_week:
                value = PERIOD_WEEK;
                break;

            default:
                value = -1;
                break;
        }
        return value;
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

        BarChartManager barChartManager1 = new BarChartManager(mBarChart);
        int periodType = getPeriod();

        //x轴的数据
        ArrayList<Float> xValues = new ArrayList<>();
        //y轴的数据()
        ArrayList<Float> yValues = new ArrayList<>();
        String xFieldName;
        if(periodType == PERIOD_WEEK){
            xFieldName = mPresenter.getFieldNamePeriodWeek();
        }else if(periodType == PERIOD_YEAR){
            xFieldName = mPresenter.getFieldNamePeriodYear();
        }else{
            xFieldName = mPresenter.getFieldNamePeriodMonth();
        }
        for (ContentValues node : data){

            xValues.add(node.getAsFloat(xFieldName));
            yValues.add(node.getAsFloat(mPresenter.getFieldNameSumPrice()));

        }
        mBarChart.setVisibility(VISIBLE);
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
