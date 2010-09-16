package com.mobilebytes.phonez.page;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.Contacts;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView.ScaleType;

import com.mobilebytes.phonez.R;

import com.mobilebytes.phonez.page.RecentCalls.PeopleAdapter.Person;
import com.mobilebytes.phonez.providers.ApplicationContentProvider;
import com.mobilebytes.phonez.widget.RecentBox;

// TODO: Auto-generated Javadoc
/**
 * The Class RecentCalls.
 */
public class RecentCalls extends Page {




	/** The m resolver. */
	private ContentResolver mResolver;

	/** The m calls adapter. */
	private CallsAdapter mCallsAdapter;





	/** The m package manager. */
    private PackageManager mPackageManager;

	/**
	 * Instantiates a new today page.
	 *
	 * @param context the context
	 */
	public RecentCalls(final Context context) {
		super(context, "Recent Calls");
		mPackageManager = context.getPackageManager();
		inflate(context, R.layout.calls, this);
		setBackgroundColor(0xff3d6187);
		//mDate = (TextView) findViewById(R.id.date);
		//mFormat = new SimpleDateFormat("EEE dd-MMM hh:mm");
       // Date date = new Date();
       // mDate.setText(mFormat.format(date));

		IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        //context.registerReceiver(mTimeIntentReceiver, filter);


        mResolver = context.getContentResolver();

        RecentBox calls = (RecentBox) findViewById(R.id.calls);
        mCallsAdapter = new CallsAdapter(calls.getMaxRecentItems());
		mResolver.registerContentObserver(android.provider.CallLog.Calls.CONTENT_URI, true, mCallsObserver);
		calls.setAdapter(mCallsAdapter);
		calls.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final Person person = (Person) mCallsAdapter.getItem(position);
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				String msg = getResources().getString(R.string.dialog_call_question, person.name);
				builder.setMessage(msg);
				builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + person.number));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
					}
				});
				builder.setNegativeButton(android.R.string.no, null);
				builder.show();
			}
		});





	}

	// TODO check if its good place to unregisterReceiver
	/* (non-Javadoc)
	 * @see android.view.View#onDetachedFromWindow()
	 */
	@Override
	protected void onDetachedFromWindow() {
		//getContext().unregisterReceiver(mTimeIntentReceiver);
		super.onDetachedFromWindow();
	}



    /** The m handler. */
    Handler mHandler = new Handler();

    /** The m calls observer. */
    ContentObserver mCallsObserver = new ContentObserver(mHandler) {
    	@Override
    	public void onChange(boolean selfChange) {
    		mCallsAdapter.update();
    	}
    };





    /**
     * The Class PeopleAdapter.
     */
    class PeopleAdapter extends BaseAdapter {

	    /** The m people. */
	    protected ArrayList<Person> mPeople;

	    /** The m max items. */
	    protected int mMaxItems;

    	/**
	     * Instantiates a new people adapter.
	     *
	     * @param maxItems the max items
	     */
	    public PeopleAdapter(int maxItems) {
			mMaxItems = maxItems;
			mPeople = new ArrayList<Person>();
			update();
		}

		/**
		 * Gets the count.
		 *
		 * @return the count
		 * @see android.widget.Adapter#getCount()
		 */
		public int getCount() {
			return mPeople.size();
		}

		/**
		 * Gets the item.
		 *
		 * @param position the position
		 * @return the item
		 * @see android.widget.Adapter#getItem(int)
		 */
		public Object getItem(int position) {
			return mPeople.get(position);
		}

		/**
		 * Gets the item id.
		 *
		 * @param position the position
		 * @return the item id
		 * @see android.widget.Adapter#getItemId(int)
		 */
		public long getItemId(int position) {
			return position;
		}

		/**
		 * Gets the view.
		 *
		 * @param position the position
		 * @param convertView the convert view
		 * @param parent the parent
		 * @return the view
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			Person person = mPeople.get(position);

			TextView tv = new TextView(getContext());
			if (person.icon != null) {
				tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			} else {
				tv.setGravity(Gravity.CENTER);
			}
			tv.setText(person.name);
			tv.setTextSize(17);
			tv.setTypeface(Typeface.DEFAULT_BOLD);
			tv.setTextColor(0xff000000);
			tv.setShadowLayer(2, 0, 0, 0xffffffff);
			if (person.icon != null) {
				FrameLayout frame = new FrameLayout(getContext());
				frame.setPadding(5, 5, 5, 5);

				ImageView iv = new ImageView(getContext());
				iv.setScaleType(ScaleType.FIT_END);
				iv.setImageBitmap(person.icon);
				iv.setAlpha(128);
				frame.addView(iv, new LayoutParams(LayoutParams.FILL_PARENT, 48));
				frame.addView(tv);
				return frame;
			}
//			Log.d("Today", "getView");
			return tv;
		}

		/**
		 * Populate people.
		 *
		 * @param numbers the numbers
		 */
		protected void populatePeople(List<String> numbers) {
			String[] peopleColumns = {
					android.provider.Contacts.People._ID,
					android.provider.Contacts.People.DISPLAY_NAME,
			};
			String selection = android.provider.Contacts.People.NUMBER + "=?";
			String[] selectionArgs = new String[1];
			mPeople.clear();
			Options options = new Options();
			options.inDither = false;
			options.inPreferredConfig = Config.ARGB_8888;
			for (String number : numbers) {
				Person person = new Person();
				selectionArgs[0] = number;
				Cursor c = mResolver.query(android.provider.Contacts.People.CONTENT_URI,
						peopleColumns, selection, selectionArgs, null);
				int idIdx = c.getColumnIndex(android.provider.Contacts.People._ID);
				int nameIdx = c.getColumnIndex(android.provider.Contacts.People.DISPLAY_NAME);
				if (c.getCount() == 1) {
					c.moveToFirst();
					person.name = c.getString(nameIdx);
					Uri uri = Uri.withAppendedPath(android.provider.Contacts.People.CONTENT_URI, c.getString(idIdx));
					person.icon = Contacts.People.loadContactPhoto(getContext(), uri, 0, options);
					// TODO: why icon has no transparency??? (ugly black background)
					if (person.icon != null) {
						Log.d("PeopleAdapter", "icon hasAlpha " + person.icon.hasAlpha());
					}
				} else {
					person.name = number;
				}
				person.number = number;
				c.close();
				mPeople.add(person);
			}
		}

		/**
		 * Update.
		 */
		public void update() {
		}

	    /**
    	 * The Class Person.
    	 */
    	class Person {

	    	/** The name. */
	    	String name;

	    	/** The number. */
	    	String number;

	    	/** The icon. */
	    	Bitmap icon;
	    }
    }

    /**
     * The Class CallsAdapter.
     */
    class CallsAdapter extends PeopleAdapter {

		/**
		 * Instantiates a new calls adapter.
		 *
		 * @param maxItems the max items
		 */
		public CallsAdapter(int maxItems) {
			super(maxItems);
		}

		/* (non-Javadoc)
		 * @see com.mobilebytes.phonez.page.TodayPage.PeopleAdapter#update()
		 */
		@Override
		public void update() {
			String[] callColumns = {
					android.provider.CallLog.Calls.NUMBER,
			};
			String callOrder = android.provider.CallLog.Calls.DATE + " desc";
			Cursor c = mResolver.query(android.provider.CallLog.Calls.CONTENT_URI, callColumns, null, null, callOrder);
			List<String> numbers = new ArrayList<String>();
			int numberIdx = c.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
			while(c.moveToNext()) {
				String number = c.getString(numberIdx);
				if (!numbers.contains(number)) {
					numbers.add(number);
					if (numbers.size() == mMaxItems) {
						break;
					}
				}
			}
			c.close();
			populatePeople(numbers);
			notifyDataSetChanged();
		}
    }






}