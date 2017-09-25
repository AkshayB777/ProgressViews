package com.ab.progressviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Akshay Bhat.
 */
public class CircleCircumferenceProgressView extends View {

    private static final String INSTANCE_STATE = "instance_state";
    private static final String INSTANCE_PROGRESS = "progress";
    private Paint mFillingPaint;
    private Paint mUnFillingPaint;
    private RectF mOvalRect = new RectF();
    private RectF mOvalRectTop = new RectF();
    private int mProgress;
    private float mFillingFactor = 0.0F;
    private int mStartAngle = -90;
    private int mSweepAngle = 0;

    public CircleCircumferenceProgressView(Context context) {
        super(context);
        init();
    }

    public CircleCircumferenceProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleCircumferenceProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public CircleCircumferenceProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mFillingPaint = new Paint();
        mFillingPaint.setStyle(Style.STROKE);
        mFillingPaint.setStrokeWidth(5.0F);
        mFillingPaint.setAntiAlias(true);
        mFillingPaint.setColor(Color.RED);
        mUnFillingPaint = new Paint();
        mUnFillingPaint.setStyle(Style.STROKE);
        mUnFillingPaint.setStrokeWidth(5.0F);
        mUnFillingPaint.setAntiAlias(true);
        mUnFillingPaint.setColor(Color.BLUE);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(size, size);
        mOvalRect.set(0.0F, 0.0F, (float) size, (float) size);
        mOvalRect.left += 3.0F;
        mOvalRect.top += 3.0F;
        mOvalRect.right -= 3.0F;
        mOvalRect.bottom -= 3.0F;
        mOvalRectTop.set(0.0F, 0.0F, (float) size, (float) size);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mFillingFactor == 0.0F) {
            mSweepAngle = 0;
        } else {
            mSweepAngle = (int) (360.0F / mFillingFactor);
        }
        canvas.drawArc(mOvalRect, -90, 360, false, mUnFillingPaint);
//        canvas.drawCircle(mOvalRectTop.width() / 2.0F, mOvalRectTop.height() / 2.0F, mOvalRectTop.width() / 2.0F, mUnFillingPaint);
        canvas.drawArc(mOvalRect, (float) mStartAngle, (float) mSweepAngle, false, mFillingPaint);
    }

    public void setProgress(int progress) {
        mProgress = progress;
        mFillingFactor = 100.0F / (float) progress;
        invalidate();
    }

    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_PROGRESS, mProgress);
        return bundle;
    }

    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mProgress = bundle.getInt(INSTANCE_PROGRESS);
            init();
            setProgress(mProgress);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
        } else {
            super.onRestoreInstanceState(state);
        }
    }
}
