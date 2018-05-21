package com.changda123.www.mycfo.Account.Statistics;

import com.changda123.www.mycfo.Account.AccountBaseModel;
import com.changda123.www.mycfo.BaseClass.BasePresenter;
import com.changda123.www.mycfo.BaseClass.ICallbackOnModelFinished;

import java.util.List;

public class StatisticsPresenter extends BasePresenter<IStatisticsView> implements ICallbackOnModelFinished<List> {

    AccountBaseModel mDataModel;

    public StatisticsPresenter(){
        mDataModel = new AccountBaseModel(getView().getContext());
    }


    public void querySubtotalGroupByCategory(int periodType, int periodValue){
        mDataModel.querySubtotalGroupByCategory(periodType, periodValue, this);
    }

    public void queryTotalGroupByPeriod(int periodType, long beginTime){
        mDataModel.queryTotalGroupByPeriod(periodType, beginTime, this);
    }

    public void querySubtotalByCategory(String catogery, int periodType, long beginTime){
        mDataModel.querySubtotalByCategory(catogery, periodType, beginTime, this);
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
