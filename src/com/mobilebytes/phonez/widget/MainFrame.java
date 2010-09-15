package com.mobilebytes.phonez.widget;

import com.mobilebytes.phonez.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

// TODO: Auto-generated Javadoc
/**
 * The Class MainFrame.
 */
public class MainFrame extends FrameLayout {

	/** The m home view. */
	private HomeView mHomeView;

	/** The m enable dispatch. */
	private boolean mEnableDispatch;

	/**
	 * Instantiates a new main frame.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public MainFrame(Context context, AttributeSet attrs) {
		super(context, attrs);
		mEnableDispatch = true;
	}

	/* (non-Javadoc)
	 * @see android.view.View#onAttachedToWindow()
	 */
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		mHomeView = (HomeView) findViewById(R.id.homeView);
	}

	/* (non-Javadoc)
	 * @see android.view.ViewGroup#dispatchUnhandledMove(android.view.View, int)
	 */
	public boolean dispatchUnhandledMove(View focused, int direction) {
		if (mEnableDispatch) {
			mHomeView.move(direction);
		}
		return super.dispatchUnhandledMove(focused, direction);
	}

	/**
	 * Sets the dispatch unhandled move.
	 *
	 * @param enable the new dispatch unhandled move
	 */
	public void setDispatchUnhandledMove(boolean enable) {
		mEnableDispatch = enable;
	}
}