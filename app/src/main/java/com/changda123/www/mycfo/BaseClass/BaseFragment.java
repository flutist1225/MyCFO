package com.changda123.www.mycfo.BaseClass;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment implements IBaseView {
    public abstract int getContentViewId();
    protected abstract void initMembersView(Bundle savedInstanceState);
    protected Context mContext;
    protected View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getContentViewId(), container, false);
        this.mContext = getActivity();
        initMembersView(savedInstanceState);
        return mRootView;
    }
    @Override
    public void showLoading() {
        checkActivityAttached();
        ((BaseActivity) mContext).showLoading();
    }

    @Override
    public void hideLoading() {
        checkActivityAttached();
        ((BaseActivity) mContext).hideLoading();
    }
    @Override
    public void showToast(String msg) {
        checkActivityAttached();
        ((BaseActivity) mContext).showToast(msg);
    }

    @Override
    public void showFailureMessage(String msg) {
        checkActivityAttached();
        ((BaseActivity) mContext).showFailureMessage(msg);
    }

    @Override
    public void showErr() {
        checkActivityAttached();
        ((BaseActivity) mContext).showErr();
    }
    protected boolean isAttachedContext(){
        return getActivity() != null;
    }

    /**
     * 检查activity连接情况
     */
    public void checkActivityAttached() {
        if (getActivity() == null) {
            throw new ActivityNotAttachedException();
        }
    }
    public static class ActivityNotAttachedException extends RuntimeException {
        public ActivityNotAttachedException() {
            super("Fragment has disconnected from Activity ! - -.");
        }
    }
}
