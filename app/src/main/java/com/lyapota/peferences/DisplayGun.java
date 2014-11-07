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

public class DisplayGun extends DialogPreference {

    private static final int[] BAR_GUNS = new int[] {
            R.string.headphone_title,
            R.string.speaker_title,
            R.string.microphone_title,
            R.string.camera_microphone_title
    };

    private GunSeekBar[] mSeekBars;

    private String[] mCurrentGuns;
    private String mOriginalGuns;
    private String mValue;

    public DisplayGun(Context context, AttributeSet attrs) {
        super(context, attrs);

       mSeekBars = new GunSeekBar[BAR_GUNS.length];
       mCurrentGuns = new String[BAR_GUNS.length];

        setDialogLayoutResource(R.layout.display_gun_calibration);
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
            return "0 0 0 0";
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

        final ViewGroup container = (ViewGroup) view.findViewById(R.id.gun_container);
        final LayoutInflater inflater = LayoutInflater.from(getContext());

            mOriginalGuns = getValue();
            mCurrentGuns = mOriginalGuns.split(" ");

            ImageView sample = (ImageView) inflater.inflate(
                    R.layout.display_gun_calibration_sample, container, false);
            container.addView(sample);

            for (int gun = 0; gun < BAR_GUNS.length; gun++) {
                ViewGroup item = (ViewGroup) inflater.inflate(
                        R.layout.display_gun_calibration_item, container, false);

                mSeekBars[gun] = new GunSeekBar(gun, item);
                mSeekBars[gun].setGuns(Integer.valueOf(mCurrentGuns[gun]));
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
                    final String[] defaultGuns = mOriginalGuns.split(" ");

                    for (int gun = 0; gun < BAR_GUNS.length; gun++) {
                        mSeekBars[gun].setGuns(Integer.valueOf(defaultGuns[gun]));
                        mCurrentGuns[gun] = defaultGuns[gun];
                    }
                    setValue(TextUtils.join(" ", mCurrentGuns));
                }
        });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (!positiveResult && mOriginalGuns != null) {
                setValue(mOriginalGuns);
        }
    }

    public void restore(Context context) {
            final String values = mOriginalGuns;
            if (values != null) {
                setValue(values);
            }
    }

    private class GunSeekBar implements SeekBar.OnSeekBarChangeListener {
        private int mGunIndex;
        private int mMin = -20;
        private int mMax = 20;
        private SeekBar mSeekBar;
        private TextView mValue;

        public GunSeekBar(int gunIndex, ViewGroup container) {
            mGunIndex = gunIndex;
            mValue = (TextView) container.findViewById(R.id.gun_value);
            mSeekBar = (SeekBar) container.findViewById(R.id.gun_seekbar);

            TextView label = (TextView) container.findViewById(R.id.gun_text);
            label.setText(container.getContext().getString(BAR_GUNS[gunIndex]));

            mSeekBar.setMax(mMax - mMin);
            mSeekBar.setProgress(0);
            mValue.setText(String.valueOf(mSeekBar.getProgress() + mMin));

            mSeekBar.setOnSeekBarChangeListener(this);
        }

        public void setGuns(int gamma) {
            mSeekBar.setProgress(gamma - mMin);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mCurrentGuns[mGunIndex] = String.valueOf(progress + mMin);
                setValue(TextUtils.join(" ", mCurrentGuns));
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

