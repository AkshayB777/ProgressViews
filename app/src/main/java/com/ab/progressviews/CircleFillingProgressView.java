package com.ab.progressviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Akshay Bhat.
 */
@SuppressWarnings("unused")
public class CircleFillingProgressView extends View {

    //Instance state strings
    private static final String BUNDLE_INSTANCE = "bundle_instance";
    private static final String PROGRESS_INSTANCE = "progress_instance";
    private static final String PROGRESS_MAX_INSTANCE = "progress_max_instance";
    private static final String PROGRESS_TEXT_SIZE_INSTANCE = "progress_text_size_instance";
    private static final String CIRCLE_COLOR_INSTANCE = "circle_color_instance";
    private static final String ARC_COLOR_INSTANCE = "arc_color_instance";
    private static final String PROGRESS_TEXT_COLOR_INSTANCE = "progress_text_color_instance";
    private static final String HIDE_PROGRESS_TEXT_INSTANCE = "hide_progress_text_instance";
    private static final String PROGRESS_TEXT_SUFFIX_INSTANCE = "progress_text_suffix_instance";
    private static final String FILL_FROM_INSTANCE = "fill_from_instance";
    private static final String FILL_FROM_START_ANGLE_INSTANCE = "fill_from_start_angle_instance";
    private static final String CUSTOM_START_ANGLE_SET_INSTANCE = "custom_start_angle_set_instance";
    private static final String TYPEFACE_PATH_INSTANCE = "typeface_path_instance";
    //Default color Values
    private static final String CIRCLE_DEFAULT_COLOR = "#F8A2BB";
    private static final String ARC_DEFAULT_COLOR = "#F75382";
    private static final String PROGRESS_TEXT_DEFAULT_COLOR = "#ffffff";

    //fill from attrs
    public enum FillFrom {
        BOTTOM, LEFT, TOP, RIGHT, BOTTOMLEFT, TOPLEFT, TOPRIGHT, BOTTOMRIGHT, CUSTOM
    }

    private FillFrom mFillFrom = FillFrom.BOTTOM;
    //paints, rect, colors
    private Paint mCirclePaint;
    private Paint mArcPaint;
    private TextPaint mProgressTextPaint;
    private Typeface mProgressTextTypeFace;
    private String mTypefacePath;
    private RectF mCircleRect = new RectF();
    private Rect mProgressTextBounds = new Rect();
    private int mCircleColor = Color.parseColor(CIRCLE_DEFAULT_COLOR);
    private int mArcColor = Color.parseColor(ARC_DEFAULT_COLOR);
    private int mProgressTextColor = Color.parseColor(PROGRESS_TEXT_DEFAULT_COLOR);
    //progress, angles, progress texts
    private int mProgress = 0;
    private float mProgressMax = 100f;
    private float mProgressTextSize = 20f;
    private float mProgressFactor = 0f;
    private float mFillFromStartAngle = 90f;
    private String mProgressTextSuffix = "%";
    private boolean mHideProgressText;
    private boolean mIsCustomStartAngleSet;

    public CircleFillingProgressView(Context context) {
        this(context, null);
    }

    public CircleFillingProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleFillingProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleFillingProgressView, defStyleAttr, 0);
        readAttributes(attributes);
        attributes.recycle();
        init();
    }

    /**
     * Read the attributes given in xml.
     * If particular attribute is not specified in xml, set it to default value.
     */
    private void readAttributes(TypedArray attributes) {
        mProgress = attributes.getInt(R.styleable.CircleFillingProgressView_circle_progress, 0);
        mProgressMax = (float) attributes.getInt(R.styleable.CircleFillingProgressView_circle_max, 100);
        mProgressTextSize = attributes.getDimension(R.styleable.CircleFillingProgressView_circle_text_size, getTextSizeFloat(20f));
        mCircleColor = attributes.getColor(R.styleable.CircleFillingProgressView_circle_unfinished_color, Color.parseColor(CIRCLE_DEFAULT_COLOR));
        mArcColor = attributes.getColor(R.styleable.CircleFillingProgressView_circle_finished_color, Color.parseColor(ARC_DEFAULT_COLOR));
        mProgressTextColor = attributes.getColor(R.styleable.CircleFillingProgressView_circle_text_color, Color.parseColor(PROGRESS_TEXT_DEFAULT_COLOR));
        mHideProgressText = attributes.getBoolean(R.styleable.CircleFillingProgressView_circle_hide_progress_text, false);
        mProgressTextSuffix = attributes.getString(R.styleable.CircleFillingProgressView_circle_suffix_text);
        mTypefacePath = attributes.getString(R.styleable.CircleFillingProgressView_circle_progress_text_typeface);
        setTypeFace(mTypefacePath, false);
        if (mProgressTextSuffix == null) {
            mProgressTextSuffix = "%";
        }
        int fillFrom = attributes.getInt(R.styleable.CircleFillingProgressView_circle_fill_from, 0);
        setFillFromInt(fillFrom);
        if (getFillFrom() == FillFrom.CUSTOM) {
            setFillFromStartAngle(attributes.getInt(R.styleable.CircleFillingProgressView_circle_custom_start_angle, 90));
        }
    }

    /**
     * Initialize paint and Fill From Values
     */
    private void init() {
        //Circle Paint
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.FILL);
        //Arc Paint
        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setColor(mArcColor);
        mArcPaint.setStyle(Paint.Style.FILL);
        //Text Color
        mProgressTextPaint = new TextPaint();
        mProgressTextPaint.setAntiAlias(true);
        mProgressTextPaint.setColor(mProgressTextColor);
        mProgressTextPaint.setTextAlign(Paint.Align.CENTER);
        mProgressTextPaint.setTextSize(mProgressTextSize);
        mProgressTextPaint.setTypeface(mProgressTextTypeFace);
        //set fill from angle
        initFillFromValues();
    }

    /**
     * Since progress view is always in square shape, pick the small value as progress view sides and align circle to center of the view.
     * Padding is not considered if the view is a square.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int x;
        int y;
        int size;
        int horizontalPadding = getPaddingLeft() + getPaddingRight();
        int verticalPadding = getPaddingTop() + getPaddingBottom();
        if (width > height) {
            size = height - verticalPadding;
            x = (width - size) / 2;
            y = verticalPadding / 2;
        } else if (height > width) {
            size = width - horizontalPadding;
            x = horizontalPadding / 2;
            y = (height - size) / 2;
        } else {
            size = width;
            x = 0;
            y = 0;
        }
        mCircleRect.set(x, y, x + size, y + size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //calculate sweep angles for arc
        float requiredAngle = mProgressFactor == 0f ? 0f : 360f / mProgressFactor;
        float startAngle = mFillFromStartAngle - requiredAngle / 2;
        float sweepAngle = 0f + requiredAngle;
        //draw circle and arc progress
        canvas.drawCircle(mCircleRect.left + mCircleRect.width() / 2, mCircleRect.top + mCircleRect.height() / 2, mCircleRect.width() / 2, mCirclePaint);
        canvas.drawArc(mCircleRect, startAngle, sweepAngle, true, mArcPaint);
        //calculate text center
        if (!mHideProgressText) {
            String progressText = mProgress + mProgressTextSuffix;
            mProgressTextPaint.getTextBounds(progressText, 0, progressText.length(), mProgressTextBounds);
            float x = mCircleRect.centerX() - mProgressTextBounds.exactCenterX() + mProgressTextBounds.width() / 2;
            float y = mCircleRect.centerY() - mProgressTextBounds.exactCenterY();
            //draw text progress
            canvas.drawText(progressText, x, y, mProgressTextPaint);
        }
    }

    /**
     * Set Start angle for the current fill from data
     */
    private void initFillFromValues() {
        switch (mFillFrom) {
            case BOTTOM:
                mFillFromStartAngle = 90f;
                break;
            case LEFT:
                mFillFromStartAngle = 180f;
                break;
            case TOP:
                mFillFromStartAngle = 270f;
                break;
            case RIGHT:
                mFillFromStartAngle = 0f;
                break;
            case BOTTOMLEFT:
                mFillFromStartAngle = 135f;
                break;
            case TOPLEFT:
                mFillFromStartAngle = 225f;
                break;
            case TOPRIGHT:
                mFillFromStartAngle = 315f;
                break;
            case BOTTOMRIGHT:
                mFillFromStartAngle = 45f;
                break;
            case CUSTOM:
                break;
            default:
                mFillFromStartAngle = 90f;
                break;
        }
    }

    /**
     * Get current progress
     *
     * @return current progress
     */
    public int getProgress() {
        return mProgress;
    }

    /**
     * Set progress of Progress View
     *
     * @param progress current progress
     */
    public void setProgress(int progress) {
        mProgress = progress;
        mProgressFactor = mProgressMax / mProgress;
        invalidate();
    }

    /**
     * Set typeface to the progress text
     *
     * @param path path of the font file (Ex: "fonts/my_font.otf")
     */
    public void setProgressTextTypeFace(String path) {
        setTypeFace(path, true);
    }

    /**
     * Check if the progress text hidden
     *
     * @return boolean value if progress text hidden or not
     */
    public boolean isProgressTextHidden() {
        return mHideProgressText;
    }

    /**
     * Hide/Show progress text
     *
     * @param hideProgressText true to hide progress text, false otherwise
     */
    public void setHideProgressText(boolean hideProgressText) {
        mHideProgressText = hideProgressText;
        invalidate();
    }

    /**
     * Get the progress text suffix
     *
     * @return Progress text suffix
     */
    public String getProgressTextSuffix() {
        return mProgressTextSuffix;
    }

    /**
     * Set Progress text suffix
     *
     * @param progressTextSuffix String suffix of progress
     *                           Pass empty string to remove suffix
     */
    public void setProgressTextSuffix(String progressTextSuffix) {
        mProgressTextSuffix = progressTextSuffix;
        invalidate();
    }

    /**
     * Get the progress text size in pixels
     *
     * @return progress text size in pixels
     */
    public float getProgressTextSize() {
        return getTextSizeInSP(mProgressTextSize);
    }

    /**
     * Set progress text size in SP.
     *
     * @param progressTextSize size in SP Unit.
     */
    public void setProgressTextSize(float progressTextSize) {
        mProgressTextSize = getTextSizeFloat(progressTextSize);
        mProgressTextPaint.setTextSize(mProgressTextSize);
        invalidate();
    }

    /**
     * Get Max value of progress
     *
     * @return Max progress value
     */
    public float getProgressMax() {
        return mProgressMax;
    }

    /**
     * Set max progress value
     *
     * @param progressMax max progress required
     */
    public void setProgressMax(float progressMax) {
        mProgressMax = progressMax;
        invalidate();
    }

    /**
     * Get progress text color
     *
     * @return Progress text color
     */
    public int getProgressTextColor() {
        return mProgressTextColor;
    }

    /**
     * Set progress Text Color
     *
     * @param progressTextColor Text color required for progress text
     */
    public void setProgressTextColor(int progressTextColor) {
        mProgressTextColor = progressTextColor;
        mProgressTextPaint.setColor(mProgressTextColor);
        invalidate();
    }

    /**
     * Get filling color of progress
     *
     * @return Color of the filling view
     */
    public int getFillingColor() {
        return mArcColor;
    }

    /**
     * Set the progress filling color
     *
     * @param fillingColor progress filling color
     */
    public void setFillingColor(int fillingColor) {
        mArcColor = fillingColor;
        mArcPaint.setColor(mArcColor);
        invalidate();
    }

    /**
     * Get the UnFilling progress color
     */
    public int getUnFillingColor() {
        return mCircleColor;
    }

    /**
     * Set UnFilling Progress color
     *
     * @param unFillingColor progress unFilling color
     */
    public void setUnFillingColor(int unFillingColor) {
        mCircleColor = unFillingColor;
        mCirclePaint.setColor(mCircleColor);
        invalidate();
    }

    /**
     * Get the progress fill from
     */
    public FillFrom getFillFrom() {
        return mFillFrom;
    }

    /**
     * Set the progress fill from
     * @param fillFrom Fill from Left/Right/Top/Bottom
     */
    public void setFillFrom(FillFrom fillFrom) {
        mFillFrom = fillFrom;
        initFillFromValues();
        invalidate();
    }

    /**
     * Only use if need custom start angle
     *
     * @param startAngle start angle where the progress starts filling from
     */
    public void setFillFromStartAngle(float startAngle) {
        mFillFromStartAngle = startAngle;
        mIsCustomStartAngleSet = true;
        mFillFrom = FillFrom.CUSTOM;
    }

    /**
     * Save progress data on recreation of Fragment/Activity
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_INSTANCE, super.onSaveInstanceState());
        bundle.putInt(PROGRESS_INSTANCE, mProgress);
        bundle.putFloat(PROGRESS_MAX_INSTANCE, mProgressMax);
        bundle.putFloat(PROGRESS_TEXT_SIZE_INSTANCE, mProgressTextSize);
        bundle.putInt(CIRCLE_COLOR_INSTANCE, mCircleColor);
        bundle.putInt(ARC_COLOR_INSTANCE, mArcColor);
        bundle.putInt(PROGRESS_TEXT_COLOR_INSTANCE, mProgressTextColor);
        bundle.putBoolean(HIDE_PROGRESS_TEXT_INSTANCE, mHideProgressText);
        bundle.putString(PROGRESS_TEXT_SUFFIX_INSTANCE, mProgressTextSuffix);
        bundle.putBoolean(CUSTOM_START_ANGLE_SET_INSTANCE, mIsCustomStartAngleSet);
        bundle.putFloat(FILL_FROM_START_ANGLE_INSTANCE, mFillFromStartAngle);
        bundle.putString(TYPEFACE_PATH_INSTANCE, mTypefacePath);
        bundle.putInt(FILL_FROM_INSTANCE, getIntFillFrom(mFillFrom));
        return bundle;
    }

    /**
     * Restore progress data after recreation of Fragment/Activity
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mProgress = bundle.getInt(PROGRESS_INSTANCE);
            mProgressMax = bundle.getFloat(PROGRESS_MAX_INSTANCE);
            mProgressTextSize = bundle.getFloat(PROGRESS_TEXT_SIZE_INSTANCE);
            mCircleColor = bundle.getInt(CIRCLE_COLOR_INSTANCE);
            mArcColor = bundle.getInt(ARC_COLOR_INSTANCE);
            mProgressTextColor = bundle.getInt(PROGRESS_TEXT_COLOR_INSTANCE);
            mHideProgressText = bundle.getBoolean(HIDE_PROGRESS_TEXT_INSTANCE);
            mProgressTextSuffix = bundle.getString(PROGRESS_TEXT_SUFFIX_INSTANCE);
            mTypefacePath = bundle.getString(TYPEFACE_PATH_INSTANCE);
            setTypeFace(mTypefacePath, false);
            if (bundle.getBoolean(CUSTOM_START_ANGLE_SET_INSTANCE)) {
                setFillFromStartAngle(bundle.getFloat(FILL_FROM_START_ANGLE_INSTANCE));
            } else {
                setFillFromInt(bundle.getInt(FILL_FROM_INSTANCE));
            }
            init();
            setProgress(mProgress);
            super.onRestoreInstanceState(bundle.getParcelable(BUNDLE_INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    /**
     * Getting textSize in Float
     */
    private float getTextSizeFloat(float progressTextSize) {
        return progressTextSize * getResources().getDisplayMetrics().scaledDensity;
    }

    /**
     * Getting textSize in SP
     */
    private float getTextSizeInSP(float progressTextSize) {
        return progressTextSize / getResources().getDisplayMetrics().scaledDensity;
    }

    /**
     * Get integer value from the enum FillFrom
     */
    private int getIntFillFrom(FillFrom progressFillFrom) {
        int fillFrom;
        switch (progressFillFrom) {
            case BOTTOM:
                fillFrom = 0;
                break;
            case LEFT:
                fillFrom = 1;
                break;
            case TOP:
                fillFrom = 2;
                break;
            case RIGHT:
                fillFrom = 3;
                break;
            case BOTTOMLEFT:
                fillFrom = 4;
                break;
            case TOPLEFT:
                fillFrom = 5;
                break;
            case TOPRIGHT:
                fillFrom = 6;
                break;
            case BOTTOMRIGHT:
                fillFrom = 7;
                break;
            case CUSTOM:
                fillFrom = 8;
                break;
            default:
                fillFrom = 0;
                break;
        }
        return fillFrom;
    }

    /**
     * Set fill from enum using integer value
     */
    private void setFillFromInt(int fillFrom) {
        switch (fillFrom) {
            case 0:
                mFillFrom = FillFrom.BOTTOM;
                break;
            case 1:
                mFillFrom = FillFrom.LEFT;
                break;
            case 2:
                mFillFrom = FillFrom.TOP;
                break;
            case 3:
                mFillFrom = FillFrom.RIGHT;
                break;
            case 4:
                mFillFrom = FillFrom.BOTTOMLEFT;
                break;
            case 5:
                mFillFrom = FillFrom.TOPLEFT;
                break;
            case 6:
                mFillFrom = FillFrom.TOPRIGHT;
                break;
            case 7:
                mFillFrom = FillFrom.BOTTOMRIGHT;
                break;
            case 8:
                mFillFrom = FillFrom.CUSTOM;
                break;
            default:
                mFillFrom = FillFrom.BOTTOM;
                break;
        }
    }

    /**
     * Set typeface to the progress text
     *
     * @param path path of the font file (Ex: "fonts/my_font.otf")
     * @param inValidateRequired Is redrawing required
     */
    private void setTypeFace(String path, boolean inValidateRequired) {
        if (!TextUtils.isEmpty(path)) {
            try {
                mTypefacePath = path;
                mProgressTextTypeFace = Typeface.createFromAsset(getContext().getAssets(), path);
            } catch (RuntimeException e) {
                e.printStackTrace();
                mTypefacePath = "";
                mProgressTextTypeFace = null;
            }
        } else {
            mTypefacePath = "";
            mProgressTextTypeFace = null;
        }
        if (inValidateRequired) {
            mProgressTextPaint.setTypeface(mProgressTextTypeFace);
            invalidate();
        }
    }
}
