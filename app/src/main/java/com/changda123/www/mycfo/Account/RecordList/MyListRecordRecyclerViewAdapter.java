package com.changda123.www.mycfo.Account.RecordList;

import android.content.ContentValues;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.changda123.www.mycfo.Account.RecordList.ListRecordFragment.OnListFragmentInteractionListener;
import com.changda123.www.mycfo.CMyDBHelper;
import com.changda123.www.mycfo.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyListRecordRecyclerViewAdapter extends RecyclerView.Adapter<MyListRecordRecyclerViewAdapter.ViewHolder> {

    private static final int NORMAL_ITEM = 0;
    private static final int GROUP_ITEM = 1;

    private final List<ContentValues> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyListRecordRecyclerViewAdapter(List<ContentValues> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_listrecord, parent, false);
        /*
        if (viewType == NORMAL_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_item_view_item, parent, false);
            NormalItemHolder holder = new NormalItemHolder(mContext, itemView, listener, longClickListener);
            return holder;
        } else if (viewType == GROUP_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_item_view_list, parent, false);
            GroupItemHolder holder = new GroupItemHolder(mContext, itemView, listener, longClickListener);
            return holder;
        }
        return null;
*/
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getAsString(CMyDBHelper.FIELD_ID));
        holder.mContentView.setText(mValues.get(position).getAsString(CMyDBHelper.FIELD_EVENT));
        holder.mPriceView.setText(mValues.get(position).getAsString(CMyDBHelper.FIELD_PRICE));
        holder.mCategory.setText("[" + mValues.get(position).getAsString(CMyDBHelper.FIELD_CATEGORY)+ "]");

        Date nowDate = new Date(mValues.get(position).getAsLong(CMyDBHelper.FIELD_TIME));

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        //holder.mTimeView.setText(timeFormat.format(nowDate));
        holder.mDateView.setText(dateFormat.format(nowDate));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mDateView;
        public final TextView mTimeView;
        public final TextView mPriceView;
        public final TextView mCategory;

        public ContentValues mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.event);
            mTimeView = (TextView) view.findViewById(R.id.time);
            mPriceView = (TextView) view.findViewById(R.id.price);
            mDateView  = (TextView) view.findViewById(R.id.date);
            mCategory  = view.findViewById(R.id.category);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }


}
