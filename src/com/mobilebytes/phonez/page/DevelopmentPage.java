package com.mobilebytes.phonez.page;

import com.mobilebytes.phonez.R;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class DevelopmentPage.
 */
public class DevelopmentPage extends Page {

	/**
	 * Instantiates a new development page.
	 *
	 * @param context the context
	 * @param s the s
	 */
	public  DevelopmentPage(Context context, String s) {
		super(context, "Under Developement Page " + s);
		TextView tv = new TextView(context);
		Drawable construction = getResources().getDrawable(R.drawable.companylogo);
		Configuration configuration = context.getResources().getConfiguration();
		if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			float scale = 0.9f;
			construction.setBounds(0, 0, (int) (construction.getIntrinsicWidth()*scale), (int) (construction.getIntrinsicHeight()*scale));
			tv.setCompoundDrawables(construction, null, null, null);
			tv.setPadding(16, 0, 0, 0);
		} else {
			construction.setBounds(0, 0, construction.getIntrinsicWidth(), construction.getIntrinsicHeight());
			tv.setCompoundDrawables(null, construction, null, null);
		}
		tv.setGravity(Gravity.CENTER);
		tv.setText("page " + s + " under developement");
		tv.setTextColor(0xffd0d0d0);
		tv.setTextSize(40);
		tv.setBackgroundColor(0xff448243);
		addView(tv);
	}
}