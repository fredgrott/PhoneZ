package com.mobilebytes.phonez;

import com.mobilebytes.phonez.widget.HomeView;



import android.app.ActivityGroup;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

// TODO: Auto-generated Javadoc
/**
 * The Class MainActivity.
 */
public class MainActivity extends ActivityGroup {

	/** The s instance. */
	private static MainActivity sInstance;

	/** The m home view. */
	private HomeView mHomeView;



    /**
     * Gets the single instance of MainActivity.
     *
     * @return single instance of MainActivity
     */
    public static MainActivity getInstance(){
    	return sInstance;
    }

    /* (non-Javadoc)
     * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sInstance = this;

        setFullscreen();
        setNoTitle();
        setContentView(R.layout.main);
    }

    /**
     * Sets the fullscreen.
     */
    public void setFullscreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Sets the no title.
     */
    public void setNoTitle() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
	protected void onStart() {
		super.onStart();
		//mThumbnails.setup(mHomeView);
	}

	/**
	 * On prepare options menu.
	 *
	 * @param menu the menu
	 * @return true, if successful
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		mHomeView.getCurrentPage().onPrepareOptionsMenu(menu);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * On options item selected.
	 *
	 * @param item the item
	 * @return true, if successful
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		return mHomeView.getCurrentPage().onOptionsItemSelected(item);
	}
}