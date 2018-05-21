package com.changda123.www.mycfo.Account.AddRecord;

import com.changda123.www.mycfo.Account.AccountBaseModel;
import com.changda123.www.mycfo.BaseClass.BasePresenter;
import com.changda123.www.mycfo.BaseClass.IBaseModel;
import com.changda123.www.mycfo.BaseClass.ICallbackOnModelFinished;

public class AddRecordPresenter extends BasePresenter< IAddRecordView > implements ICallbackOnModelFinished<Long> {

    IBaseModel mDataModel;

    public AddRecordPresenter(){
        mDataModel = new AccountBaseModel(getView().getContext());
    }

    public void AddRecord(String strCategory,
                          String strEvent,
                          String strPrice,
                          String strLocation,
                          long   lngTime,
                          String strWho,
                          String strPayType){

        if (!isViewAttached()){
            //如果没有View引用就不加载数据
            return;
        }
        mDataModel.addRecord(strCategory,strEvent,strPrice,strLocation,lngTime,strWho,strPayType, this);
    }

    @Override
    public void onSuccess(Long data) {

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
