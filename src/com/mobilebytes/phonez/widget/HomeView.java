package com.mobilebytes.phonez.widget;

import com.mobilebytes.phonez.MainActivity;
import com.mobilebytes.phonez.interpolator.BounceInterpolator;
import com.mobilebytes.phonez.interpolator.EasingType.Type;

import com.mobilebytes.phonez.page.Page;

import com.mobilebytes.phonez.page.RecentCalls;
import com.mobilebytes.phonez.page.RecentSMS;
import com.mobilebytes.phonez.page.TodayPage;

import android.app.ActivityGroup;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Interpolator;
import android.widget.Scroller;

// TODO: Auto-generated Javadoc
/**
 * The Class HomeView.
 */
public class HomeView extends ViewGroup {

	/*
	 * Log String TAG to denote
	 * HomeView events in LogCat
	 */
	/** The Constant TAG. */
	private static final String TAG = "HomeView";

	/*
	 * The minipanel is the displayable
	 * alpha trans page/menu panel
	 * and this sets the
	 * scale of thumbnails of the pages
	 * displayed
	 */
	/** The Constant MINIPANEL_SCALE. */
	private static final float MINIPANEL_SCALE = 0.2f;

	/*
	 * Number of pages the app has
	 * in the x-axis and y-axis dimensions
	 */
	/** The Constant NUM_PAGES_X. */
	private static final int NUM_PAGES_X = 3;

	/** The Constant NUM_PAGES_Y. */
	private static final int NUM_PAGES_Y = 1;

	/** The Constant SCROLL. */
	private static final int SCROLL = 1;

	/** The Constant JUSTIFY. */
	private static final int JUSTIFY = 2;

	/** The Constant LINEAR_SCROLL. */
	private static final int LINEAR_SCROLL = 3;

	/** The Constant ANIMATION_DURATION. */
	private static final int ANIMATION_DURATION = 750;

	/*
	 * Instance variable of the HomeView class
	 */
	/** The s instance. */
	private static HomeView sInstance;

	/*
	 * Scroller variables
	 */
	/** The m scroller. */
	private Scroller mScroller;

	/** The m linear scroller. */
	private Scroller mLinearScroller;


	/** The m gesture detector. */
	private GestureDetector mGestureDetector;

	/*
	 * page location
	 * along
	 * x-axis and y-axis
	 * ie width and height
	 */
	/** The m x. */
	private int mX;

	/** The m y. */
	private int mY;

	/*
	 * max widths and heights
	 * computed from
	 * numbers of pages in the
	 * x-axis and y-axis dimensions
	 */
	/** The m max x. */
	private int mMaxX;

	/** The m max y. */
	private int mMaxY;

	/*
	 * page width and height
	 */
	/** The m page width. */
	private int mPageWidth;

	/** The m page height. */
	private int mPageHeight;

	/*
	 * Pages two-dimensional array
	 */
	/** The m page layouts. */
	private Page[][] mPageLayouts;

	/*
	 * current page variable
	 */
	/** The m current page. */
	private Page mCurrentPage;


	/** The m first measure. */
	private boolean mFirstMeasure = true;

	/*
	 * An ActivityGroup can have multiple embedded
	 * activities so we are grabbing
	 * the our instance from
	 * the mActivity(ActivityGroup) variable
	 */
	/** The m activity. */
	private ActivityGroup mActivity;


	/** The m last motion x. */
	private float mLastMotionX;

	/** The m last motion y. */
	private float mLastMotionY;


	/** The m wallpaper. */
	private Bitmap mWallpaper;


	/** The m src. */
	private Rect mSrc;

	/** The m dst. */
	private Rect mDst;


	/** The m scrolling alpha. */
	private ScrollingAlpha mScrollingAlpha;


	/** The m panel paint. */
	private Paint mPanelPaint;


	/** The m mini panel bitmap. */
	private Bitmap mMiniPanelBitmap;


	/** The m mini round rect. */
	private RectF mMiniRoundRect;

	/**
	 * Instantiates a new home view.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public HomeView(final Context context, AttributeSet attrs) {
		super(context, attrs);

		/*
		 * WidgetActivity is an ActivityGroup
		 * so once we grab the mActivity instance
		 * we can assign the HomeView sInstance
		 * the keyword of this
		 */
		mActivity = MainActivity.getInstance();
		sInstance = this;

		/*
		 * Try to get default wallpaper
		 */
		try {
			BitmapDrawable wallpaper = (BitmapDrawable) context.getWallpaper();
			mWallpaper = wallpaper.getBitmap();
		} catch (Exception e) {
			Log.d(TAG, "Cannot get wallpaper", e);
		}


		mScroller = new Scroller(context, (Interpolator) new BounceInterpolator(Type.OUT));
		mLinearScroller = new Scroller(context);

		/*
		 * mGestureDetector gets assigned a mGestureListener
		 * and and LongPressEnabled is set  to false
		 */
		mGestureDetector = new GestureDetector(mGestureListener);
		mGestureDetector.setIsLongpressEnabled(false);

		// populate 2D array
		mPageLayouts = new Page[NUM_PAGES_X][];
		for (int x = 0; x<NUM_PAGES_X; x++) {
			mPageLayouts[x] = new Page[NUM_PAGES_Y];
		}

		// initialize pages

		mPageLayouts[0][0] = new RecentCalls(context);

		// today page
		mPageLayouts[1][0] = new TodayPage(context);


		mPageLayouts[2][0] = new RecentSMS(context);


		// add pages
		for (int x = 0; x<NUM_PAGES_X; x++) {
			for (int y = 0; y<NUM_PAGES_Y; y++) {
				Page page = mPageLayouts[x][y];
				page.setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
				addView(page);
			}
		}

		// will go to Today Page
		mX = 1;
		mY = 0;

		mScrollingAlpha = new ScrollingAlpha();
		mPanelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPanelPaint.setStrokeWidth(2);
		mMiniRoundRect = new RectF();

		mEnableScrolling = true;
	}

	/* (non-Javadoc)
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mPageWidth = MeasureSpec.getSize(widthMeasureSpec);
		mPageHeight = MeasureSpec.getSize(heightMeasureSpec);

		mMaxX = (NUM_PAGES_X - 1) * mPageWidth;
		mMaxY = (NUM_PAGES_Y - 1) * mPageHeight;
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(NUM_PAGES_X * mPageWidth, MeasureSpec.EXACTLY);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(NUM_PAGES_Y * mPageHeight, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mSrc = new Rect(0, 0, mPageWidth, mPageHeight);
		mDst = new Rect(0, 0, mPageWidth, mPageHeight);

		if (mFirstMeasure) {
			mFirstMeasure = false;
			mX *= mPageWidth;
			mY *= mPageHeight;
			scrollTo(mX, mY);
			setCurrentPage(mX, mY);
			Log.d(TAG, "initial scrollTo");
		}
	}

	/* (non-Javadoc)
	 * @see android.view.View#onSaveInstanceState()
	 */
	@Override
	protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        if (mPageWidth != 0 && mPageHeight != 0) {
        	return new SavedState(superState, mX/mPageWidth, mY/mPageHeight);
        } else {
        	return new SavedState(superState, 0, 0);
        }
	}

	/* (non-Javadoc)
	 * @see android.view.View#onRestoreInstanceState(android.os.Parcelable)
	 */
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mX = ss.mX;
        mY = ss.mY;
	}

	/**
	 * Dispatch draw.
	 *
	 * @param canvas the canvas
	 * @see android.view.ViewGroup#dispatchDraw(android.graphics.Canvas)
	 */
	protected void dispatchDraw(Canvas canvas) {
		// draw wallpaper
		// TODO: compute mSrc offset based on wallpaper width and this View width
		if (mWallpaper != null && mY > 0) {
			mSrc.offsetTo(mX/2, 0);
			mDst.offsetTo(mX, mPageHeight);
			canvas.drawBitmap(mWallpaper, mSrc, mDst, null);
		}

		// draw children
		for (int x = 0, xx = 0; x<NUM_PAGES_X; x++, xx += mPageWidth) {
			for (int y = 0, yy = 0; y<NUM_PAGES_Y; y++, yy += mPageHeight) {
				if (xx + mPageWidth > mX && mX + mPageWidth > xx &&
						yy + mPageHeight > mY && mY + mPageHeight > yy) {
					drawChild(canvas, mPageLayouts[x][y], getDrawingTime());
				}
			}
		}
		if (mScrollingAlpha.mTime > 0) {
			canvas.save();
			canvas.translate(mX, mY);

			int alpha0 = (int) (48 * mScrollingAlpha.mTime);
			mPanelPaint.setStyle(Style.FILL);
			mPanelPaint.setColor(0xff000000);
			mPanelPaint.setAlpha(alpha0);
			canvas.drawRect(0, 0, mPageWidth, mPageHeight, mPanelPaint);

			int alpha1 = (int) (192 * mScrollingAlpha.mTime);
			int alpha2 = (int) (255 * mScrollingAlpha.mTime);
//			Log.d(TAG, "a0 " + alpha0 + " a1 " + alpha1 + " a2 " + alpha2);

			float pageWidth = mPageWidth * MINIPANEL_SCALE;
			float pageHeight = mPageHeight * MINIPANEL_SCALE;
			float panelWidth = pageWidth * NUM_PAGES_X;
			float panelHeight = pageHeight * NUM_PAGES_Y;

			// translate with horizontal center padding
			canvas.translate((mPageWidth - panelWidth) / 2, 24);

			// draw panel background:
			//  1. background
			mPanelPaint.setColor(0xffffffff);
			mPanelPaint.setAlpha(alpha1);
			mMiniRoundRect.set(-4, -4, panelWidth+4, panelHeight+4);
			canvas.drawRoundRect(mMiniRoundRect, 10, 10, mPanelPaint);
			//  2. frame
			mPanelPaint.setStyle(Style.STROKE);
			mPanelPaint.setColor(0xffdddddd);
			mPanelPaint.setAlpha(alpha2);
			canvas.drawRoundRect(mMiniRoundRect, 10, 10, mPanelPaint);

			// draw mini pages
			mPanelPaint.setAlpha(alpha2);
			canvas.drawBitmap(mMiniPanelBitmap, 0, 0, mPanelPaint);

			// draw target page
			mPanelPaint.setStyle(Style.FILL);
			float x = Math.round(mX/(float) mPageWidth) * pageWidth;
			float y = Math.round(mY/(float) mPageHeight) * pageHeight;
			mMiniRoundRect.set(x-2, y-2, x+pageWidth+2, y+pageHeight+2);
			mPanelPaint.setColor(0xff6666aa);
			mPanelPaint.setAlpha(alpha1);
			canvas.drawRoundRect(mMiniRoundRect, 8, 8, mPanelPaint);

			// draw scrolled region
			mPanelPaint.setStyle(Style.STROKE);
			x = mX * MINIPANEL_SCALE;
			y = mY * MINIPANEL_SCALE;
			mPanelPaint.setColor(0xff666666);
			mPanelPaint.setAlpha(alpha1);
			mMiniRoundRect.offsetTo(x-2, y-2);
			canvas.drawRoundRect(mMiniRoundRect, 8, 8, mPanelPaint);

			canvas.restore();
		}
	}

	/**
	 * Sets the current page.
	 *
	 * @param x the x
	 * @param y the y
	 */
	private void setCurrentPage(int x, int y) {
		x /= mPageWidth;
		y /= mPageHeight;
		if (mCurrentPage != null) {
			mCurrentPage.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
		}
		mCurrentPage = mPageLayouts[x][y];
		mCurrentPage.setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
		mCurrentPage.requestFocus();
		if (mActivity != null) {
			mActivity.setTitle(mCurrentPage.getTitle());
		}

//		Log.d(TAG, "current page " + x + " " + y);
	}

	/*
	 * Need to catch fling and scroll only in
	 * the top edge, bottom edge,
	 * left edge, and right edge
	 * page fling zones
	 *
	 * The underlying page
	 * will handle fling and
	 * scrolls of
	 * panels using its
	 * own SimpleOnGesture Listener
	 * in the Page class we
	 * are extending
	 *
	 */
	/** The m gesture listener. */
	SimpleOnGestureListener mGestureListener = new SimpleOnGestureListener() {



		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			mScrollHandler.removeMessages(JUSTIFY);
			mScrollHandler.removeMessages(SCROLL);

			if (velocityY != 0) {
				// reset velocities if almost horizontal/vertical fling
				float tangent = Math.abs(velocityX / velocityY);
				if (tangent < 0.2) {
					velocityX = 0;
				} else
				if (tangent > 1/0.2) {
					velocityY = 0;
				}
			}
//			// default velocities were to high...
//			velocityX *= 0.5;
//			velocityY *= 0.5;
			mScroller.fling(mX, mY, (int) -velocityX, (int) -velocityY, 0, mMaxX, 0, mMaxY);
			mScrollHandler.sendEmptyMessage(SCROLL);
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			mScrollingAlpha.changeTo(1);
			int x = mX;
			int y = mY;
			x += distanceX;
			y += distanceY;
			x = ensureInRange(x, 0, mMaxX);
			y = ensureInRange(y, 0, mMaxY);
			if (x != mX || y != mY) {
				mScrollHandler.removeMessages(JUSTIFY);
				mScrollHandler.removeMessages(SCROLL);
				mX = x;
				mY = y;
				scrollTo(mX, mY);
			}
			return true;
		}

		private int ensureInRange(int v, int min, int max) {
			v = Math.max(v, min);
			v = Math.min(v, max);
			return v;
		}
	};

	/**
	 * Justify.
	 */
	private void justify() {
		int dx = getDelta(mX, mPageWidth);
		int dy = getDelta(mY, mPageHeight);
		if (dx != 0 || dy != 0) {
			mScroller.startScroll(mX, mY, dx, dy, ANIMATION_DURATION);
			mScrollHandler.sendEmptyMessage(JUSTIFY);
		} else {
			setCurrentPage(mX, mY);
		}
	}

	/**
	 * Gets the delta.
	 *
	 * @param v the v
	 * @param pageSize the page size
	 * @return the delta
	 */
	private int getDelta(int v, int pageSize) {
		int pageRounded = Math.round(v / (float) pageSize) * pageSize;
		return pageRounded - v;
	}

	/** The m scroll handler. */
	Handler mScrollHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == LINEAR_SCROLL) {
				if (!mLinearScroller.isFinished()) {
					mLinearScroller.computeScrollOffset();
					mX = mLinearScroller.getCurrX();
					mY = mLinearScroller.getCurrY();
					scrollTo(mX, mY);
					sendEmptyMessage(LINEAR_SCROLL);
				} else {
					setCurrentPage(mX, mY);
				}
			} else
			if (!mScroller.isFinished()) {
				mScroller.computeScrollOffset();
				mX = mScroller.getCurrX();
				mY = mScroller.getCurrY();
				scrollTo(mX, mY);
				sendEmptyMessage(msg.what);
			} else {
				if (msg.what == SCROLL) {
					justify();
				} else {
					setCurrentPage(mX, mY);
				}
			}
		}
	};

	/** The m enable scrolling. */
	private boolean mEnableScrolling;

	/* (non-Javadoc)
	 * @see android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mEnableScrolling) {
//			Log.d(TAG, "onInterceptTouchEvent " + ev.getDownTime());
	        float x = ev.getX();
	        float y = ev.getY();
	        int action = ev.getAction();

	        if (action == MotionEvent.ACTION_DOWN) {
	        	mLastMotionX = x;
	        	mLastMotionY = y;
	        	mGestureDetector.onTouchEvent(ev);
	        } else
	        if (action == MotionEvent.ACTION_MOVE) {
	        	int touchSlop = ViewConfiguration.getTouchSlop();
		        boolean xMoved = Math.abs(x - mLastMotionX) > touchSlop;
		        boolean yMoved = Math.abs(y - mLastMotionY) > touchSlop;
		        if (xMoved || yMoved) {
					return true;
				}
	        }
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mEnableScrolling) {
//			Log.d(TAG, "onTouchEvent " + event.getDownTime());
			boolean rc = mGestureDetector.onTouchEvent(event);
	        int action = event.getAction();
			if (action == MotionEvent.ACTION_DOWN) {
				mScrollingAlpha.changeTo(1);
			} else
			if (action == MotionEvent.ACTION_UP) {
				int dx = getDelta(mX, mPageWidth);
				int dy = getDelta(mY, mPageHeight);
				if (dx != 0 || dy != 0) {
					// needs justify
					mScrollingAlpha.changeTo(0);
				} else {
					mScrollingAlpha.changeTo(0, 0);
				}
				if (!rc) {
					justify();
				}
			}
			return true;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see android.view.ViewGroup#onLayout(boolean, int, int, int, int)
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Log.d(TAG, "onLayout " + r + " " + b + " count " + getChildCount());
		int w = MeasureSpec.makeMeasureSpec(mPageWidth, MeasureSpec.EXACTLY);
		int h = MeasureSpec.makeMeasureSpec(mPageHeight, MeasureSpec.EXACTLY);
		for (int x = 0; x<NUM_PAGES_X; x++) {
			for (int y = 0; y<NUM_PAGES_Y; y++) {
				int xx = x * mPageWidth;
				int yy = y * mPageHeight;
				mPageLayouts[x][y].measure(w, h);
				mPageLayouts[x][y].layout(xx, yy, xx+mPageWidth, yy+mPageHeight);
			}
		}
	}

	/**
	 * Gets the num columns.
	 *
	 * @return the num columns
	 */
	public int getNumColumns() {
		return NUM_PAGES_X;
	}

	/**
	 * Gets the num rows.
	 *
	 * @return the num rows
	 */
	public int getNumRows() {
		return NUM_PAGES_Y;
	}

	/**
	 * Gets the page.
	 *
	 * @param row the row
	 * @param column the column
	 * @return the page
	 */
	public Page getPage(int row, int column) {
		return mPageLayouts[column][row];
	}

	/**
	 * Scroll to page.
	 *
	 * @param row the row
	 * @param column the column
	 */
	public void scrollToPage(int row, int column) {
		int dstX = column * mPageWidth;
		int dstY = row * mPageHeight;
		int dx = dstX - mX;
		int dy = dstY - mY;
		if (dx != 0 || dy != 0) {
			mLinearScroller.startScroll(mX, mY, dx, dy, 2 * ANIMATION_DURATION);
			mScrollHandler.sendEmptyMessage(LINEAR_SCROLL);
		}
	}

	/**
	 * Move.
	 *
	 * @param direction the direction
	 */
	public void move(int direction) {
		int x = mX;
		int y = mY;
		x /= mPageWidth;
		y /= mPageHeight;

		if (direction == FOCUS_LEFT) {
			x--;
		} else
		if (direction == FOCUS_RIGHT) {
			x++;
		} else
		if (direction == FOCUS_UP) {
			y--;
		} else
		if (direction == FOCUS_DOWN) {
			y++;
		}
		if (x >= 0 && x < NUM_PAGES_X && y >= 0 && y < NUM_PAGES_Y) {
			scrollToPage(y, x);
		}
	}

	/**
	 * Gets the current page.
	 *
	 * @return the current page
	 */
	public Page getCurrentPage() {
		return mCurrentPage;
	}

	// does it really needs to be so complicated ?!
	/**
	 * The Class SavedState.
	 */
	static class SavedState extends BaseSavedState {

		/** The m x. */
		int mX;

		/** The m y. */
		int mY;

		/**
		 * Instantiates a new saved state.
		 *
		 * @param source the source
		 */
		public SavedState(Parcel source) {
			super(source);
			mX = source.readInt();
			mY = source.readInt();
		}

		/**
		 * Instantiates a new saved state.
		 *
		 * @param superState the super state
		 * @param x the x
		 * @param y the y
		 */
		private SavedState(Parcelable superState, int x, int y) {
			super(superState);
			mX = x;
			mY = y;
		}

		/* (non-Javadoc)
		 * @see android.view.AbsSavedState#writeToParcel(android.os.Parcel, int)
		 */
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(mX);
			dest.writeInt(mY);
		}

		/** The Constant CREATOR. */
		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
			}
			public SavedState[] newArray(int size) {
                return new SavedState[size];
			}
		};
	}

	/**
	 * The Class ScrollingAlpha.
	 */
	class ScrollingAlpha extends Handler {

		/** The Constant NUM_STEPS. */
		private static final int NUM_STEPS = 5;

		/** The m time. */
		float mTime;

		/** The m target time. */
		float mTargetTime;

		/** The m delta. */
		float mDelta;

		/**
		 * Change to.
		 *
		 * @param targetTime the target time
		 */
		public void changeTo(float targetTime) {
			changeTo(targetTime, 1000);
		}

		/**
		 * Change to.
		 *
		 * @param targetTime the target time
		 * @param delay the delay
		 */
		public void changeTo(float targetTime, int delay) {
			if (mTargetTime == targetTime) {
				return;
			}
			mDelta = (targetTime - mTargetTime) / NUM_STEPS;
			mTargetTime = targetTime;
			removeMessages(0);
			sendEmptyMessageDelayed(0, delay);
		}

		/* (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			if (mTime == 0 && mDelta > 0) {
				float pageWidth = mPageWidth * MINIPANEL_SCALE;
				float pageHeight = mPageHeight * MINIPANEL_SCALE;
				float panelWidth = pageWidth * NUM_PAGES_X;
				float panelHeight = pageHeight * NUM_PAGES_Y;
				Log.d(TAG, "create thumbnails bitmap");
				mMiniPanelBitmap = Bitmap.createBitmap((int) panelWidth, (int) panelHeight, Config.ARGB_8888);
				Canvas panelCanvas = new Canvas(mMiniPanelBitmap);
				for (int x=0; x<NUM_PAGES_X; x++) {
					for (int y=0; y<NUM_PAGES_Y; y++) {
						Bitmap miniPage = Bitmap.createBitmap((int) pageWidth, (int) pageHeight, Config.ARGB_8888);
						Canvas pageCanvas = new Canvas(miniPage);
						pageCanvas.scale(MINIPANEL_SCALE, MINIPANEL_SCALE);
						mPageLayouts[x][y].draw(pageCanvas);
						panelCanvas.drawBitmap(miniPage, x*pageWidth, y*pageHeight, null);
					}
				}
			}
			mTime += mDelta;

			if ((mDelta > 0 && mTime >= mTargetTime) || (mDelta < 0 && mTime <= mTargetTime)) {
				mTime = mTargetTime;
			} else {
				sendEmptyMessage(0);
			}
			invalidate();
		}
	}

	/**
	 * Gets the page width.
	 *
	 * @return the page width
	 */
	public int getPageWidth() {
		return mPageWidth;
	}

	/**
	 * Gets the page height.
	 *
	 * @return the page height
	 */
	public int getPageHeight() {
		return mPageHeight;
	}

	/**
	 * Gets the single instance of HomeView.
	 *
	 * @return single instance of HomeView
	 */
	public static HomeView getInstance() {
		return sInstance;
	}

	/**
	 * Enable scrolling.
	 *
	 * @param enable the enable
	 */
	public void enableScrolling(boolean enable) {
		mEnableScrolling = enable;
	}



}