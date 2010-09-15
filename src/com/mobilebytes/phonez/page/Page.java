package com.mobilebytes.phonez.page;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

// TODO: Auto-generated Javadoc
/**
 * The Class Page.
 */
public class Page extends FrameLayout {

	/** The m title. */
	private String mTitle;

	/**
	 * Instantiates a new page.
	 *
	 * @param context the context
	 * @param title the title
	 */
	public Page(Context context, String title) {
		super(context);
		mTitle = (title != null)? title : "[No Title]";
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * On prepare options menu.
	 *
	 * @param menu the menu
	 */
	public void onPrepareOptionsMenu(Menu menu) {
	}

	/**
	 * On options item selected.
	 *
	 * @param item the item
	 * @return true, if successful
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}
}