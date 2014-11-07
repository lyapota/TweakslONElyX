package com.lyapota.peferences;

        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.res.TypedArray;
        import android.os.Bundle;
        import android.preference.DialogPreference;
        import android.support.annotation.NonNull;
        import android.text.TextUtils;
        import android.util.AttributeSet;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.SeekBar;
        import android.widget.TextView;

        import com.lyapota.tweakslonelyx.R;

public class DisplayGamma extends DialogPreference {

    private static final int[] BAR_COLORS = new int[] {
            R.string.color_red_title,
            R.string.color_green_title,
            R.string.color_blue_title
    };

    private GammaSeekBar[] mSeekBars;

    private String[] mCurrentColors;
    private String mOriginalColors;
    protected String mValue;

    public DisplayGamma(Context context, AttributeSet attrs) {
        super(context, attrs);

        mSeekBars = new GammaSeekBar[BAR_COLORS.length];
        mCurrentColors = new String[BAR_COLORS.length];

        setDialogLayoutResource(R.layout.display_gamma_calibration);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedString(mValue) : (String) defaultValue);
    }

    @Override
    public CharSequence getSummary() {
        CharSequence summary = super.getSummary();
        if (super.getSummary() == null)
            summary = getValue();
        return summary;
    }

    public String getValue() {
        if (mValue != null )
           return mValue;
        else
            return "255 255 255";
    }

    public void setValue(String value) {
        if (shouldPersist()) {
            persistString(value);
        }
        if (!value.equals(mValue)) {
            mValue = value;
            notifyChanged();
        }
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setNeutralButton(R.string.reset_button,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
    }

    @Override
    protected void onBindDialogView(@NonNull View view) {
        super.onBindDialogView(view);

        final ViewGroup container = (ViewGroup) view.findViewById(R.id.gamma_container);
        final LayoutInflater inflater = LayoutInflater.from(getContext());

            mOriginalColors = getValue();
            mCurrentColors = mOriginalColors.split(" ");

            ImageView sample = (ImageView) inflater.inflate(
                    R.layout.display_gamma_calibration_sample, container, false);
            container.addView(sample);

            for (int color = 0; color < BAR_COLORS.length; color++) {
                ViewGroup item = (ViewGroup) inflater.inflate(
                        R.layout.display_gamma_calibration_item, container, false);

                mSeekBars[color] = new GammaSeekBar(color, item);
                mSeekBars[color].setGamma(Integer.valueOf(mCurrentColors[color]));
                container.addView(item);
            }
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        AlertDialog d = (AlertDialog) getDialog();
        Button defaultsButton = d.getButton(DialogInterface.BUTTON_NEUTRAL);
        defaultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] defaultColors = mOriginalColors.split(" ");

                for (int color = 0; color < BAR_COLORS.length; color++) {
                    mSeekBars[color].setGamma(Integer.valueOf(defaultColors[color]));
                    mCurrentColors[color] = defaultColors[color];
                }
                setValue(TextUtils.join(" ", mCurrentColors));
            }
        });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (!positiveResult && mOriginalColors != null) {
                setValue(mOriginalColors);
        }
    }

    public void restore(Context context) {
            final String values = mOriginalColors;
            if (values != null) {
                setValue(values);
            }
    }

    private class GammaSeekBar implements SeekBar.OnSeekBarChangeListener {
        private int mColorIndex;
        private int mMin = 1;
        private int mMax = 255;
        private SeekBar mSeekBar;
        private TextView mValue;

        public GammaSeekBar(int colorIndex, ViewGroup container) {
            mColorIndex = colorIndex;
            mValue = (TextView) container.findViewById(R.id.color_value);
            mSeekBar = (SeekBar) container.findViewById(R.id.color_seekbar);

            TextView label = (TextView) container.findViewById(R.id.color_text);
            label.setText(container.getContext().getString(BAR_COLORS[colorIndex]));

            mSeekBar.setMax(mMax - mMin);
            mSeekBar.setProgress(0);
            mValue.setText(String.valueOf(mSeekBar.getProgress() + mMin));

            mSeekBar.setOnSeekBarChangeListener(this);
        }

        public void setGamma(int gamma) {
            mSeekBar.setProgress(gamma - mMin);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mCurrentColors[mColorIndex] = String.valueOf(progress + mMin);
                setValue(TextUtils.join(" ", mCurrentColors));
            }
            mValue.setText(String.valueOf(progress + mMin));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Do nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // Do nothing
        }
    }
}

