package com.changda123.www.mycfo.BaseClass;

import android.content.ContentValues;

import java.util.List;

public interface IBaseModel {

    void addRecord(String strCategory,
                   String strEvent,
                   String strPrice,
                   String strLocation,
                   long lngTime,
                   String strWho,
                   String strPayType,
                   ICallbackOnModelFinished<Long> callback);

    void getListOfRecords(int days, ICallbackOnModelFinished<List> callback);

    void deleteRecord(long id, ICallbackOnModelFinished<String> callback);

    void updateRecord(long id,
                      String strCategory,
                      String strEvent,
                      String strPrice,
                      String strLocation,
                      long lngTime,
                      String strWho,
                      String strPayType,
                      ICallbackOnModelFinished<String> callback);
}
