package com.ab.progressviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Akshay Bhat.
 */
public class ProgressRectFillingView extends View {

    private static final String INSTANCE_STATE = "instance_state";
    private static final String INSTANCE_PROGRESS = "progress";

    private RectF mFillingRect = new RectF();
    private int mFillingColor = Color.parseColor("#C70039");
    private int mFillingTintColor = Color.parseColor("#E17594");
    private int mWidth;
    private int mHeight;
    private float mFillingFactor = 0f;
    private Paint mPaint;
    private TextPaint mProgressTextPaint;
    private int mProgress;

    public ProgressRectFillingView(Context context) {
        super(context);
        init();
    }

    public ProgressRectFillingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressRectFillingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProgressRectFillingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mFillingRect.left = 0;//constant
        mFillingRect.right = mWidth;//constant
        mFillingRect.bottom = mHeight;//constant
        mFillingRect.top = mHeight;
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mFillingColor);

        mProgressTextPaint = new TextPaint();
        mProgressTextPaint.setAntiAlias(true);
        mProgressTextPaint.setColor(Color.YELLOW);
        mProgressTextPaint.setTextAlign(Paint.Align.CENTER);
        mProgressTextPaint.setTextSize(25);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //calculate
        if (mFillingFactor == 0f) {
            mFillingRect.top = mHeight;
        } else {
            mFillingRect.top = mHeight - (int) (mHeight / mFillingFactor);
        }
        //draw
        canvas.drawColor(mFillingTintColor);
        mPaint.setColor(mFillingColor);
        canvas.drawRect(mFillingRect, mPaint);
        String progressText = mProgress+"%";
        int textHeight = (int) (mProgressTextPaint.descent() + mProgressTextPaint.ascent());
        int x = getWidth() / 2 - (int) mProgressTextPaint.measureText(progressText) / 2;
        int y = getHeight() / 2 - textHeight / 2;
        canvas.drawText(progressText,x,y,mProgressTextPaint);
    }

    public void setProgress(int progress) {
        mProgress = progress;
        mFillingFactor = 100f / progress;
        invalidate();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_PROGRESS, mProgress);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mProgress = bundle.getInt(INSTANCE_PROGRESS);
            init();
            setProgress(mProgress);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
