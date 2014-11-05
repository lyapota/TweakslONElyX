/*
 * Copyright 2012 Jay Weisskopf
 *
 * Licensed under the MIT License (see LICENSE.txt)
 */

package com.lyapota.peferences;

        import android.content.Context;
        import android.content.res.TypedArray;
        import android.preference.DialogPreference;
        import android.util.AttributeSet;
        import android.view.View;
        import android.widget.SeekBar;
        import android.widget.TextView;

        import com.lyapota.tweakslonelyx.R;

/**
 * @author Jay Weisskopf
 */
public class SliderPreference extends DialogPreference {

    protected final static int SEEKBAR_RESOLUTION = 10000;

    protected float mValue;
    protected int mSeekBarValue;
    protected CharSequence[] mSummaries;
    protected int index;
    TextView message;
    SeekBar seekbar;

    /**
     * @param context
     * @param attrs
     */
    public SliderPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public SliderPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context, attrs);
    }

    private void setup(Context context, AttributeSet attrs) {
        setDialogLayoutResource(R.layout.slider_preference_dialog);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SliderPreference);
        try {
            setSummary(a.getTextArray(R.styleable.SliderPreference_android_summary));
        } catch (Exception e) {
            // Do nothing
        }
        a.recycle();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getFloat(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedFloat(mValue) : (Float) defaultValue);
    }

    @Override
    public CharSequence getSummary() {
        if (mSummaries != null && mSummaries.length > 0) {
            index = (int) (mValue * mSummaries.length);
            index = Math.min(index, mSummaries.length - 1);
            return mSummaries[index];
        } else {
            return super.getSummary();
        }
    }

    public void setSummary(CharSequence[] summaries) {
        mSummaries = summaries;
    }

    @Override
    public void setSummary(CharSequence summary) {
        super.setSummary(summary);
        mSummaries = null;
    }

    @Override
    public void setSummary(int summaryResId) {
        try {
            setSummary(getContext().getResources().getString(summaryResId));
        } catch (Exception e) {
            super.setSummary(summaryResId);
        }
    }

    public float getValue() {
        return mValue;
    }

    public float getIndex() {
        return index;
    }

    public void setValue(float value) {
        value = Math.max(0, Math.min(value, 1)); // clamp to [0, 1]
        if (shouldPersist()) {
            persistFloat(value);
        }
        if (value != mValue) {
            mValue = value;
            notifyChanged();
        }
    }

    private CharSequence setMessageInternal() {
        if (mSummaries != null && mSummaries.length > 0) {
            float newValue = (float) mSeekBarValue / SEEKBAR_RESOLUTION;

            index = (int) (newValue * mSummaries.length);
            index = Math.min(index, mSummaries.length - 1);

            message.setText(mSummaries[index]);
            newValue = (float) index / (mSummaries.length - 1);
            int newSeekBarValue = (int) (newValue * SEEKBAR_RESOLUTION);
            seekbar.setProgress(newSeekBarValue);

            return mSummaries[index];
        } else {
            return message.getText();
        }
    }

    @Override
    protected View onCreateDialogView() {
        mSeekBarValue = (int) (mValue * SEEKBAR_RESOLUTION);
        View view = super.onCreateDialogView();

        seekbar = (SeekBar) view.findViewById(R.id.slider_preference_seekbar);
        seekbar.setMax(SEEKBAR_RESOLUTION);
        seekbar.setProgress(mSeekBarValue);

        message = (TextView) view.findViewById(android.R.id.message);
        setDialogMessage(setMessageInternal());


        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    SliderPreference.this.mSeekBarValue = progress;
                    SliderPreference.this.setMessageInternal();
                }
            }
        });
        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        final float newValue = (float) mSeekBarValue / SEEKBAR_RESOLUTION;
        if (positiveResult && callChangeListener(newValue)) {
            setValue(newValue);
        }
        super.onDialogClosed(positiveResult);
    }
}
// TODO: Save and res