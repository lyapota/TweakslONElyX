
package com.lyapota.peferences;

        import android.content.Context;
        import android.content.res.TypedArray;
        import android.preference.DialogPreference;
        import android.util.AttributeSet;
        import android.view.View;
        import android.widget.SeekBar;
        import android.widget.TextView;

        import com.lyapota.tweakslonelyx.R;

public class SliderPreference extends DialogPreference {

    protected final static int SEEKBAR_RESOLUTION = 100;

    protected int mSeekBarValue;
    protected CharSequence[] mSummaries;
    protected int index;
    protected int originalValue;
    TextView text;
    SeekBar seekbar;

    public SliderPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

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
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedInt(index) : (Integer) defaultValue);
    }

    @Override
    public CharSequence getSummary() {
        CharSequence summary;
        if (mSummaries != null)
            summary = mSummaries[getValue()];
        else
            summary = super.getSummary();
        return summary;
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

    public int getValue() {
        return index;
    }

    public void setValue(int value) {
        value = Math.max(0, Math.min(value, SEEKBAR_RESOLUTION));
        if (shouldPersist()) {
            persistInt(value);
        }
        if (value != index) {
            index = value;
            notifyChanged();
        }
    }

    private CharSequence setTextInternal() {
        int newValue;

        newValue = mSummaries.length * mSeekBarValue / SEEKBAR_RESOLUTION;
        newValue = Math.min(newValue, mSummaries.length - 1);
        setValue(newValue);

        text.setText(mSummaries[newValue]);
        float newPosition = (float) newValue / (mSummaries.length - 1);
        int newSeekBarValue = (int) (newPosition * SEEKBAR_RESOLUTION);
        seekbar.setProgress(newSeekBarValue);

        return mSummaries[index];
    }

    @Override
    protected View onCreateDialogView() {
        if (mSummaries != null)
            mSeekBarValue = (index + 1) * (SEEKBAR_RESOLUTION / mSummaries.length);
        else
            mSeekBarValue = index;

        View view = super.onCreateDialogView();

        seekbar = (SeekBar) view.findViewById(R.id.slider_preference_seekbar);
        seekbar.setMax(SEEKBAR_RESOLUTION);
        seekbar.setProgress(mSeekBarValue);

        text = (TextView) view.findViewById(R.id.text);
        if (mSummaries != null)
            setTextInternal();
        originalValue = index;

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
                    if (mSummaries != null)
                        SliderPreference.this.setTextInternal();
                }
            }
        });
        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        int newValue;

        if (mSummaries != null)
            newValue = mSummaries.length * mSeekBarValue / SEEKBAR_RESOLUTION;
        else
            newValue = mSeekBarValue;

        if (positiveResult && callChangeListener(newValue)) {
            setValue(newValue);
        } else
            setValue(originalValue);
        super.onDialogClosed(positiveResult);
    }
}