package com.changda123.www.mycfo;

import android.content.ContentValues;

import java.util.ArrayList;

interface OnResult {
    void  onSuccess(ArrayList<ContentValues> list);
    void  onError(String errMsg);
}
