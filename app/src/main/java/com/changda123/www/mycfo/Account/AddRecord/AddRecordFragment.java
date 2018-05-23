package com.changda123.www.mycfo.Account.AddRecord;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.changda123.www.mycfo.BaseClass.BaseFragment;
import com.changda123.www.mycfo.MainActivity;
import com.changda123.www.mycfo.R;
import com.changda123.www.mycfo.Util.MyCommonApi;
import com.changda123.www.mycfo.Util.MyLog;
import com.changda123.www.mycfo.Util.RadioGroupEx;

import java.util.Calendar;
import java.util.Locale;


/**
 * 实现了UI的公共接口IBaseView和IAddRecordView接口
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddRecordFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddRecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddRecordFragment extends BaseFragment implements IAddRecordView {
    public static final String TAG = "MyCFO_AddRecordFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    AddRecordPresenter mPresenter;

    private RadioGroupEx mRadioGroupCaletory;
    private RadioButton mRadioButtonCalegory;
    private RadioGroup mRadioGroupPayType;
    private RadioButton mRadioButtonPayType;
    private TextView mTextEvent;
    private TextView mTextPrice;
    private TextView mTextLocation;
    private TextView mTextTime;
    private TextView mTextWho;
    private TextView mPayType;
    private Button mButtonAdd;

    private String mQuickKeyString;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AddRecordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddRecordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddRecordFragment newInstance(String param1, String param2) {
        AddRecordFragment fragment = new AddRecordFragment();
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
        MyLog.d(TAG, "AddRecordFragment.");
        mPresenter = new AddRecordPresenter(getContext());
        mPresenter.attachView(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //断开View引用
        mPresenter.detachView();
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
    public int getContentViewId() {
        return R.layout.fragment_add_record;
    }

    @Override
    protected void initMembersView(Bundle savedInstanceState) {
        final View view = mRootView;
        mRadioGroupCaletory = (RadioGroupEx) view.findViewById(R.id.idRadioGroupCalegory);

        mTextEvent = (TextView) view.findViewById(R.id.idEditTextEvent);
        mTextPrice = (TextView) view.findViewById(R.id.idEditTextPrice);
        mTextTime  = (TextView) view.findViewById(R.id.idEditTextTime);
        mTextLocation = (TextView) view.findViewById(R.id.idEditTextLocation);
        mTextWho   = (TextView) view.findViewById(R.id.idEditTextWho);
        mRadioGroupPayType = (RadioGroup) view.findViewById(R.id.idRadioGroupPayType);

        initDateToNow();
        mTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(getContext());
            }
        });

        Button quickKeyButtonStopCar      = (Button) view.findViewById(R.id.idButtonStopCar);
        Button quickKeyButtonBuyVegetable = (Button) view.findViewById(R.id.idButtonBuyVegetable);
        Button quickKeyButtonEatSomething = (Button) view.findViewById(R.id.idButtonEatSomething);
        Button quickKeyButtonRestuarant   = (Button) view.findViewById(R.id.idButtonRestaurant);

        quickKeyButtonStopCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextEvent.setText(R.string.quickKey_stop_car);
                mRadioGroupCaletory.check(R.id.radioButtonRun);
                mTextPrice.setText(R.string.const_string_10);
            }
        });
        quickKeyButtonBuyVegetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextEvent.setText(R.string.quickKey_buy_vegetable);
                mRadioGroupCaletory.check(R.id.radioButtonEat);
            }
        });
        quickKeyButtonEatSomething.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextEvent.setText(R.string.quickKey_eat_something);
                mRadioGroupCaletory.check(R.id.radioButtonEat);
            }
        });
        quickKeyButtonRestuarant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextEvent.setText(R.string.quickKey_restaurant);
                mRadioGroupCaletory.check(R.id.radioButtonEat);
            }
        });

        mButtonAdd = (Button) view.findViewById(R.id.idButtonAdd);
        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检查必选参数
                int radioId = mRadioGroupCaletory.getCheckedRadioButtonId();
                if((-1 == radioId) || TextUtils.isEmpty( mTextEvent.getText()) ||TextUtils.isEmpty( mTextPrice.getText())){

                    Toast.makeText(getActivity(), "前三个带有*的参数必须填写！", Toast.LENGTH_LONG).show();
                    return;
                }

                // 获取参数值
                mRadioButtonCalegory = (RadioButton) view.findViewById(radioId);
                Toast.makeText(getActivity(), "mRadioButtonCalegory.getText()：|" + mRadioButtonCalegory.getText() + "|", Toast.LENGTH_LONG).show();


                java.util.Date date = MyCommonApi.stringToDate(mTextTime.getText().toString(), "yyyy-MM-dd");
                long datetime = date.getTime();

                mRadioGroupPayType.getCheckedRadioButtonId();
                String payType = null;
                if(-1 != mRadioGroupPayType.getCheckedRadioButtonId()) {
                    mRadioButtonCalegory = (RadioButton) view.findViewById(radioId);
                    payType = mRadioButtonCalegory.getText().toString();
                }

                // Add new record
                mPresenter.AddRecord(mRadioButtonCalegory.getText().toString(),
                        mTextEvent.getText().toString(),
                        mTextPrice.getText().toString(),
                        mTextLocation.getText().toString(),
                        datetime,
                        mTextWho.getText().toString(),
                        payType);
                /*
                ((MainActivity)getActivity()).addNewRecord(
                        mRadioButtonCalegory.getText().toString(),
                        mTextEvent.getText().toString(),
                        mTextPrice.getText().toString(),
                        mTextLocation.getText().toString(),
                        datetime,
                        mTextWho.getText().toString(),
                        payType);
                */
            }
        });
    }

    public void setDate(Context context) {
        Calendar cal=Calendar.getInstance();
        int year=cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        int day=cal.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                showDate(year,monthOfYear+1, dayOfMonth);
            }
        }, year, month, day).show();

    }

    private void initDateToNow(){
        Calendar cal=Calendar.getInstance();
        int year=cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        int day=cal.get(Calendar.DAY_OF_MONTH);

        showDate(year, month+1, day);
    }
    private void showDate(int year, int month, int day){
        mTextTime.setText(String.format(Locale.CHINA, "%d-%d-%d", year, month, day));
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
