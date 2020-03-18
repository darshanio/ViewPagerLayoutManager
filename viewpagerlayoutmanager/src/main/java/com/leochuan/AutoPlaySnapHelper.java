package com.leochuan;

import android.os.Handler;
import android.os.Looper;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Used by {@link AutoPlayRecyclerView} to implement auto play effect
 */

public class AutoPlaySnapHelper extends CenterSnapHelper {
    public final static int TIME_INTERVAL = 2000;

    public final static int LEFT = 1;
    public final static int RIGHT = 2;
    protected Runnable autoPlayRunnable;
    protected Handler handler;
    protected int timeInterval;
    protected boolean runnableAdded;
    protected int direction;

    public AutoPlaySnapHelper(int timeInterval, int direction) {
        checkTimeInterval(timeInterval);
        checkDirection(direction);
        handler = new Handler(Looper.getMainLooper());
        this.timeInterval = timeInterval;
        this.direction = direction;
    }

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) throws IllegalStateException {
        if (mRecyclerView == recyclerView) {
            return; // nothing to do
        }
        if (mRecyclerView != null) {
            destroyCallbacks();
        }
        mRecyclerView = recyclerView;
        if (mRecyclerView != null) {
            final RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
            if (!(layoutManager instanceof ViewPagerLayoutManager)) {
                return;
            }

            setupCallbacks();
            mGravityScroller = new Scroller(mRecyclerView.getContext(),
                    new DecelerateInterpolator());

            snapToCenterView((ViewPagerLayoutManager) layoutManager,
                    ((ViewPagerLayoutManager) layoutManager).onPageChangeListener);

            ((ViewPagerLayoutManager) layoutManager).setInfinite(true);

            autoPlayRunnable = new Runnable() {
                @Override
                public void run() {
                    onRun((ViewPagerLayoutManager) layoutManager);
                }
            };
            handler.postDelayed(autoPlayRunnable, timeInterval);
            runnableAdded = true;
        }
    }

    @Override
    protected void destroyCallbacks() {
        super.destroyCallbacks();
        if (runnableAdded) {
            handler.removeCallbacks(autoPlayRunnable);
            runnableAdded = false;
        }
    }

    protected void onRun(@NonNull ViewPagerLayoutManager layoutManager) {
        final int currentPosition =
                layoutManager.getCurrentPositionOffset() *
                        (layoutManager.getReverseLayout() ? -1 : 1);
        ScrollHelper.smoothScrollToPosition(mRecyclerView,
                layoutManager, direction == RIGHT ? currentPosition + 1 : currentPosition - 1);
        handler.postDelayed(autoPlayRunnable, timeInterval);
    }

    public void pause() {
        if (runnableAdded) {
            handler.removeCallbacks(autoPlayRunnable);
            runnableAdded = false;
        }
    }

    public void start() {
        if (!runnableAdded) {
            handler.postDelayed(autoPlayRunnable, timeInterval);
            runnableAdded = true;
        }
    }

    public void setTimeInterval(int timeInterval) {
        checkTimeInterval(timeInterval);
        this.timeInterval = timeInterval;
    }

    public void setDirection(int direction) {
        checkDirection(direction);
        this.direction = direction;
    }

    protected void checkDirection(int direction) {
        if (direction != LEFT && direction != RIGHT) {
            throw new IllegalArgumentException("direction should be one of left or right");
        }
    }

    protected void checkTimeInterval(int timeInterval) {
        if (timeInterval <= 0) {
            throw new IllegalArgumentException("time interval should greater than 0");
        }
    }
}
