package com.leochuan;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import static com.leochuan.CircleLayoutManager.CENTER_ON_TOP;
import static com.leochuan.CircleLayoutManager.LEFT_ON_TOP;
import static com.leochuan.CircleLayoutManager.RIGHT_ON_TOP;
import static com.leochuan.CircleLayoutManager.assertZAlignmentState;

/**
 * An implementation of {@link ViewPagerLayoutManager}
 * which zooms the center item
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class ScaleLayoutManager extends ViewPagerLayoutManager {

    private int itemSpace;
    private float minScale;
    private float maxScale;
    private float moveSpeed;
    private float maxAlpha;
    private float minAlpha;
    private int zAlignment;

    public ScaleLayoutManager(Context context, int itemSpace) {
        this(new Builder(context, itemSpace));
    }

    public ScaleLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, 0);
    }

    public ScaleLayoutManager(Context context, int itemSpace, int orientation) {
        this(new Builder(context, itemSpace).setOrientation(orientation));
    }

    public ScaleLayoutManager(Context context, int itemSpace, int orientation, boolean reverseLayout) {
        this(new Builder(context, itemSpace).setOrientation(orientation).setReverseLayout(reverseLayout));
    }

    public ScaleLayoutManager(Builder builder) {
        this(builder.context, builder.itemSpace, builder.minScale, builder.maxScale, builder.maxAlpha, builder.minAlpha,
                builder.orientation, builder.moveSpeed, builder.maxVisibleItemCount, builder.distanceToBottom,
                builder.reverseLayout, builder.zAlignment);
    }

    private ScaleLayoutManager(Context context, int itemSpace, float minScale, float maxScale, float maxAlpha, float minAlpha,
                               int orientation, float moveSpeed, int maxVisibleItemCount, int distanceToBottom,
                               boolean reverseLayout, int zAlignment) {
        super(context, orientation, reverseLayout);
        setEnableBringCenterToFront(true);
        setDistanceToBottom(distanceToBottom);
        setMaxVisibleItemCount(maxVisibleItemCount);
        this.itemSpace = itemSpace;
        this.minScale = minScale;
        this.maxScale = maxScale;
        this.moveSpeed = moveSpeed;
        this.maxAlpha = maxAlpha;
        this.minAlpha = minAlpha;
        this.zAlignment = zAlignment;
    }

    public int getItemSpace() {
        return itemSpace;
    }

    public void setItemSpace(int itemSpace) {
        assertNotInLayoutOrScroll(null);
        if (this.itemSpace == itemSpace) {
            return;
        }
        this.itemSpace = itemSpace;
        removeAllViews();
    }

    public float getMinScale() {
        return minScale;
    }

    public void setMinScale(float minScale) {
        assertNotInLayoutOrScroll(null);
        if (this.minScale == minScale) {
            return;
        }
        this.minScale = minScale;
        removeAllViews();
    }

    public float getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(float maxScale) {
        assertNotInLayoutOrScroll(null);
        if (this.maxScale == maxScale) {
            return;
        }
        this.maxScale = maxScale;
        removeAllViews();
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(float moveSpeed) {
        assertNotInLayoutOrScroll(null);
        if (this.moveSpeed == moveSpeed) {
            return;
        }
        this.moveSpeed = moveSpeed;
    }

    public float getMaxAlpha() {
        return maxAlpha;
    }

    public void setMaxAlpha(float maxAlpha) {
        assertNotInLayoutOrScroll(null);
        if (maxAlpha > 1) {
            maxAlpha = 1;
        }
        if (this.maxAlpha != maxAlpha) {
            this.maxAlpha = maxAlpha;
            requestLayout();
        } else {
            return;
        }
    }

    public float getMinAlpha() {
        return minAlpha;
    }

    public void setMinAlpha(float minAlpha) {
        assertNotInLayoutOrScroll(null);
        if (minAlpha < 0) {
            minAlpha = 0;
        }
        if (this.minAlpha == minAlpha) {
            return;
        }
        this.minAlpha = minAlpha;
        requestLayout();
    }

    @Override
    protected float getInterval() {
        return itemSpace + mDecoratedMeasurement;
    }

    @Override
    protected void setItemViewProperty(View itemView, float targetOffset) {
        float scale = calculateScale(targetOffset + mSpaceMain);
        itemView.setScaleX(scale);
        itemView.setScaleY(scale);
        final float alpha = calAlpha(targetOffset);
        itemView.setAlpha(alpha);
    }

    private float calAlpha(float targetOffset) {
        final float offset = Math.abs(targetOffset);
        float alpha = (minAlpha - maxAlpha) / mInterval * offset + maxAlpha;
        if (offset >= mInterval) {
            alpha = minAlpha;
        }
        return alpha;
    }

    @Override
    protected float getDistanceRatio() {
        if (moveSpeed == 0) {
            return Float.MAX_VALUE;
        }
        return 1 / moveSpeed;
    }

    @Override
    protected float getViewElevation(View itemView, float targetOffset) {
        if (zAlignment == LEFT_ON_TOP) {
            return -targetOffset;
        } else if (zAlignment == RIGHT_ON_TOP) {
            return targetOffset;
        } else {
            return -Math.abs(targetOffset);
        }
    }

    public int getZAlignment() {
        return zAlignment;
    }

    public void setZAlignment(int zAlignment) {
        assertNotInLayoutOrScroll(null);
        assertZAlignmentState(zAlignment);
        if (this.zAlignment == zAlignment) {
            return;
        }
        this.zAlignment = zAlignment;
        requestLayout();
    }

    /**
     * @param x start positon of the view you want scale
     * @return the scale rate of current scroll mOffset
     */
    private float calculateScale(float x) {
        return calculateScale(x, minScale, maxScale);
    }

    private float calculateScale(float x, float minScale, float maxScale) {
        float deltaX = Math.abs(x - mSpaceMain);
        if (deltaX - mDecoratedMeasurement > 0) {
            deltaX = mDecoratedMeasurement;
        }
        return maxScale - deltaX / mDecoratedMeasurement * (maxScale - minScale);
    }

    public static class Builder {
        private static final float SCALE_RATE = 0.8f;
        private static final float DEFAULT_SPEED = 1f;
        private static float MIN_ALPHA = 1f;
        private static float MAX_ALPHA = 1f;

        private int itemSpace;
        private int orientation;
        private float minScale;
        private float maxScale;
        private float moveSpeed;
        private float maxAlpha;
        private float minAlpha;
        private boolean reverseLayout;
        private Context context;
        private int maxVisibleItemCount;
        private int distanceToBottom;
        private int zAlignment;

        public Builder(Context context, int itemSpace) {
            //item之间的间隙
            this.itemSpace = itemSpace;
            this.context = context;
            orientation = HORIZONTAL;
            //缩放的比例值
            maxScale = 1f;
            minScale = SCALE_RATE;
            this.moveSpeed = DEFAULT_SPEED;
            //透明度变化
            minAlpha = MIN_ALPHA;
            maxAlpha = MAX_ALPHA;
            reverseLayout = false;
            distanceToBottom = ViewPagerLayoutManager.INVALID_SIZE;
            maxVisibleItemCount = ViewPagerLayoutManager.DETERMINE_BY_MAX_AND_MIN;
            //z轴
            zAlignment = CENTER_ON_TOP;
        }

        public Builder setOrientation(int orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder setMinScale(float minScale) {
            this.minScale = minScale;
            return this;
        }

        public Builder setMaxScale(float maxScale) {
            this.maxScale = maxScale;
            return this;
        }

        public Builder setReverseLayout(boolean reverseLayout) {
            this.reverseLayout = reverseLayout;
            return this;
        }

        public Builder setMaxAlpha(float maxAlpha) {
            if (maxAlpha > 1) {
                maxAlpha = 1;
            }
            this.maxAlpha = maxAlpha;
            return this;
        }

        public Builder setMinAlpha(float minAlpha) {
            if (minAlpha < 0) {
                minAlpha = 0;
            }
            this.minAlpha = minAlpha;
            return this;
        }

        public Builder setMoveSpeed(float moveSpeed) {
            this.moveSpeed = moveSpeed;
            return this;
        }

        public Builder setMaxVisibleItemCount(int maxVisibleItemCount) {
            this.maxVisibleItemCount = maxVisibleItemCount;
            return this;
        }

        public Builder setDistanceToBottom(int distanceToBottom) {
            this.distanceToBottom = distanceToBottom;
            return this;
        }

        public Builder setZAlignment(int zAlignment) {
            assertZAlignmentState(zAlignment);
            this.zAlignment = zAlignment;
            return this;
        }

        public ScaleLayoutManager build() {
            return new ScaleLayoutManager(this);
        }
    }
}

