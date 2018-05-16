package com.changda123.www.mycfo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddRecordFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddRecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddRecordFragment extends Fragment {
    public static final String TAG = "MyCFO_AddRecordFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View mView;


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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_add_record, container, false);
        initView(mView);
        return mView;
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

    private void initView(final View view){

        mRadioGroupCaletory = (RadioGroupEx) view.findViewById(R.id.idRadioGroupCalegory);

        mTextEvent = (TextView) view.findViewById(R.id.idEditTextEvent);
        mTextPrice = (TextView) view.findViewById(R.id.idEditTextPrice);
        mTextTime  = (TextView) view.findViewById(R.id.idEditTextTime);
        mTextLocation = (TextView) view.findViewById(R.id.idEditTextLocation);
        mTextWho   = (TextView) view.findViewById(R.id.idEditTextWho);
        mRadioGroupPayType = (RadioGroup) view.findViewById(R.id.idRadioGroupPayType);

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

                java.util.Date date = new java.util.Date();
                long datetime = date.getTime();

                mRadioGroupPayType.getCheckedRadioButtonId();
                String payType = null;
                if(-1 != mRadioGroupPayType.getCheckedRadioButtonId()) {
                    mRadioButtonCalegory = (RadioButton) view.findViewById(radioId);
                    payType = mRadioButtonCalegory.getText().toString();
                }

                // Add new record
                ((MainActivity)getActivity()).addNewRecord(
                        mRadioButtonCalegory.getText().toString(),
                        mTextEvent.getText().toString(),
                        mTextPrice.getText().toString(),
                        mTextLocation.getText().toString(),
                        datetime,
                        mTextWho.getText().toString(),
                        payType);

            }
        });

    }
}
