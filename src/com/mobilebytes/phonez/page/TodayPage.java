package com.mobilebytes.phonez.page;

import java.text.SimpleDateFormat;

import java.util.Date;

import java.util.TimeZone;

import com.mobilebytes.phonez.R;





import android.content.BroadcastReceiver;

import android.content.ContentResolver;

import android.content.Context;

import android.content.Intent;
import android.content.IntentFilter;


import android.content.pm.PackageManager;




import android.net.Uri;
import android.os.Handler;

import android.widget.TextView;



// TODO: Auto-generated Javadoc
/**
 * The Class TodayPage.
 */
public class TodayPage extends Page {


	/** The m date. */
	private TextView mDate;

	/** The m format. */
	private SimpleDateFormat mFormat;

	/** The m resolver. */
	private ContentResolver mResolver;



	/** The m package manager. */
    private PackageManager mPackageManager;

	/**
	 * Instantiates a new today page.
	 *
	 * @param context the context
	 */
	public TodayPage(final Context context) {
		super(context, "Today");
		mPackageManager = context.getPackageManager();
		inflate(context, R.layout.today, this);
		//setBackgroundColor(0xff3d6187);
		mDate = (TextView) findViewById(R.id.date);
		mFormat = new SimpleDateFormat("EEE dd-MMM hh:mm");
        Date date = new Date();
        mDate.setText(mFormat.format(date));

		IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        context.registerReceiver(mTimeIntentReceiver, filter);


        mResolver = context.getContentResolver();







	}

	// TODO check if its good place to unregisterReceiver
	/* (non-Javadoc)
	 * @see android.view.View#onDetachedFromWindow()
	 */
	@Override
	protected void onDetachedFromWindow() {
		getContext().unregisterReceiver(mTimeIntentReceiver);
		super.onDetachedFromWindow();
	}

	/** The m time intent receiver. */
	BroadcastReceiver mTimeIntentReceiver = new BroadcastReceiver() {
		@Override
        public void onReceive(Context context, Intent intent) {
			Date date;
            if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                String tz = intent.getStringExtra("time-zone");
                date = new Date(TimeZone.getTimeZone(tz).getID());
            } else {
                date = new Date();
            }
            mDate.setText(mFormat.format(date));
        }
    };

    /** The m handler. */
    Handler mHandler = new Handler();







}