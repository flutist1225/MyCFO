package com.changda123.www.mycfo.Account.RecordList;

import android.content.ContentValues;

import com.changda123.www.mycfo.BaseClass.IBaseView;

import java.util.List;

public interface IListRecordView extends IBaseView {

    void showRecords(List<ContentValues> data);
}
