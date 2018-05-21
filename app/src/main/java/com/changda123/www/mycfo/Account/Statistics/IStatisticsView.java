package com.changda123.www.mycfo.Account.Statistics;

import android.content.ContentValues;

import com.changda123.www.mycfo.BaseClass.IBaseView;

import java.util.List;

public interface IStatisticsView extends IBaseView {
    void showRecords(List<ContentValues> data);
}
