package com.mobilebytes.phonez.widget;

import java.util.ArrayList;
import java.util.List;

import com.mobilebytes.phonez.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class RecentBox extends LinearLayout implements OnClickListener {

	private int mMaxRecentItems;
	private Adapter mAdapter;
	private LinearLayout mItems;
	private OnItemClickListener mOnItemClickListener;
	private List<View> mViews;

	public RecentBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecentBox);
		int headerTextSize = a.getDimensionPixelSize(R.styleable.RecentBox_headerTextSize, 20);
		String headerText = a.getString(R.styleable.RecentBox_headerText);
		mMaxRecentItems = a.getInteger(R.styleable.RecentBox_maxRecentItems, 3);
		a.recycle();

		setOrientation(VERTICAL);
		TextView header = new TextView(context);
		header.setTextSize(headerTextSize);
		if (headerText != null) {
			header.setText(headerText);
		} else {
			header.setText("RecentBox.headerText");
		}
		addView(header);
		mItems = new LinearLayout(context);
		mItems.setOrientation(HORIZONTAL);
		addView(mItems, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		mViews = new ArrayList<View>();
	}

	public void setAdapter(Adapter adapter) {
		if (mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataSetObserver);
		}
		mAdapter = adapter;
		if (mAdapter != null) {
			adapter.registerDataSetObserver(mDataSetObserver);
			setChildren();
		}
	}


    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

	private void setChildren() {
		mViews.clear();
		mItems.removeAllViews();
		int count = mAdapter.getCount();
		LayoutParams params = new LayoutParams(0, LayoutParams.FILL_PARENT);
		params.weight = 1;
		for (int i=0; i<count; i++) {
			View item = mAdapter.getView(i, null, mItems);
			item.setBackgroundResource(android.R.drawable.btn_default_small);
			item.setClickable(true);
			item.setFocusable(true);
			item.setOnClickListener(this);
			mItems.addView(item, params);
			mViews.add(item);
		}
	}

	public int getMaxRecentItems() {
		return mMaxRecentItems;
	}

	DataSetObserver mDataSetObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			setChildren();
		}
		@Override
		public void onInvalidated() {
			setChildren();
		}
	};

	public void onClick(View v) {
		if (mOnItemClickListener != null) {
			int position = mViews.indexOf(v);
			mOnItemClickListener.onItemClick(null, v, position, position);
		}
	}
}