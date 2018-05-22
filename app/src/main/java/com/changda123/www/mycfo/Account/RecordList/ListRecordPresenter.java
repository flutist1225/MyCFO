package com.changda123.www.mycfo.Account.RecordList;

import android.content.ContentValues;
import android.content.Context;

import com.changda123.www.mycfo.Account.AccountBaseModel;
import com.changda123.www.mycfo.BaseClass.BasePresenter;
import com.changda123.www.mycfo.BaseClass.IBaseModel;
import com.changda123.www.mycfo.BaseClass.ICallbackOnModelFinished;

import java.util.List;

public class ListRecordPresenter extends BasePresenter<IListRecordView> implements ICallbackOnModelFinished< List > {

    public static final String TAG = "MyCFO_ListRecordPresenter";

    IBaseModel mDataModel;

    public ListRecordPresenter(Context context){
        mDataModel = new AccountBaseModel(context);
    }

    public void listRecords(int days) {
        if (days < 1) {
            onFailure("invalid param: " + days);
            return;
        }

        mDataModel.getListOfRecords(days, this);
    }

    @Override
    public void onSuccess(List data) {
        getView().showRecords(data);
    }

    @Override
    public void onFailure(String msg) {
        getView().showFailureMessage(msg);
    }

    @Override
    public void onError() {
        getView().showErr();
    }

    @Override
    public void onComplete() {
        getView().hideLoading();
    }
}
