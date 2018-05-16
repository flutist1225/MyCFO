package com.changda123.www.mycfo;

import android.content.ContentValues;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.changda123.www.mycfo.ListRecordFragment.OnListFragmentInteractionListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyListRecordRecyclerViewAdapter extends RecyclerView.Adapter<MyListRecordRecyclerViewAdapter.ViewHolder> {

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
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getAsString(CMyDBHelper.FIELD_ID));
        holder.mContentView.setText(mValues.get(position).getAsString(CMyDBHelper.FIELD_EVENT));
        holder.mPriceView.setText(mValues.get(position).getAsString(CMyDBHelper.FIELD_PRICE));

        Date nowDate = new Date(mValues.get(position).getAsLong(CMyDBHelper.FIELD_TIME));

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        holder.mTimeView.setText(timeFormat.format(nowDate));
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

        public ContentValues mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.event);
            mTimeView = (TextView) view.findViewById(R.id.time);
            mPriceView = (TextView) view.findViewById(R.id.price);
            mDateView  = (TextView) view.findViewById(R.id.date);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }


}
