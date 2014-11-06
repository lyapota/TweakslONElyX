package com.lyapota.peferences;

        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.res.Resources;
        import android.os.Bundle;
        import android.os.Parcel;
        import android.os.Parcelable;
        import android.preference.DialogPreference;
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
        import com.lyapota.util.GunCorrector;

public class DisplayGun extends DialogPreference {
    private static final String TAG = "GunsCalibration";

    private static final int[] BAR_GUNS = new int[] {
            R.string.headphone_title,
            R.string.speaker_title,
            R.string.microphone_title,
            R.string.camera_microphone_title
    };

    private GunSeekBar[][] mSeekBars;

    private String[][] mCurrentGuns;
    private String[] mOriginalGuns;
    private int mNumberOfControls;

    public DisplayGun(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isSupported()) {
            return;
        }

        mNumberOfControls = GunCorrector.getNumberOfControls();
        mSeekBars = new GunSeekBar[mNumberOfControls][BAR_GUNS.length];

        mOriginalGuns = new String[mNumberOfControls];
        mCurrentGuns = new String[mNumberOfControls][];

        setDialogLayoutResource(R.layout.display_gun_calibration);
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
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        final ViewGroup container = (ViewGroup) view.findViewById(R.id.gun_container);
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        final Resources res = container.getResources();
        final String[] gunDescriptors = res.getStringArray(R.array.gun_descriptors);

        // Create multiple sets of seekbars, depending on the
        // number of controls the device has
        for (int index = 0; index < mNumberOfControls; index++) {
            mOriginalGuns[index] = GunCorrector.getCurGuns(index);
            mCurrentGuns[index] = mOriginalGuns[index].split(" ");

            ImageView sample = (ImageView) inflater.inflate(
                    R.layout.display_gun_calibration_sample, container, false);
            container.addView(sample);

            if (mNumberOfControls != 1) {
                TextView header = (TextView) inflater.inflate(
                        R.layout.display_gun_calibration_header, container, false);

                if (index < gunDescriptors.length) {
                    header.setText(gunDescriptors[index]);
                } else {
                    header.setText(res.getString(
                            R.string.gun_tuning_control_set_header, index + 1));
                }
                container.addView(header);
            }

            for (int gun = 0; gun < BAR_GUNS.length; gun++) {
                ViewGroup item = (ViewGroup) inflater.inflate(
                        R.layout.display_gun_calibration_item, container, false);

                mSeekBars[index][gun] = new GunSeekBar(index, gun, item);
                mSeekBars[index][gun].setGuns(Integer.valueOf(mCurrentGuns[index][gun]));

                container.addView(item);
            }
        }
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        // can't use onPrepareDialogBuilder for this as we want the dialog
        // to be kept open on click
        AlertDialog d = (AlertDialog) getDialog();
        Button defaultsButton = d.getButton(DialogInterface.BUTTON_NEUTRAL);
        defaultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int index = 0; index < mSeekBars.length; index++) {
                    final String[] defaultGuns = mOriginalGuns[index].split(" ");

                    for (int gun = 0; gun < BAR_GUNS.length; gun++) {
                        mSeekBars[index][gun].setGuns(Integer.valueOf(defaultGuns[gun]));
                        mCurrentGuns[index][gun] = defaultGuns[gun];
                    }
                    GunCorrector.setGuns(index,
                            TextUtils.join(" ", mCurrentGuns[index]));
                }
            }
        });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (!positiveResult && mOriginalGuns != null) {
            for (int i = 0; i < mNumberOfControls; i++) {
                GunCorrector.setGuns(i, mOriginalGuns[i]);
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (getDialog() == null || !getDialog().isShowing()) {
            return superState;
        }

        // Save the dialog state
        final SavedState myState = new SavedState(superState);
        myState.controlCount = mNumberOfControls;
        myState.currentGuns = mCurrentGuns;
        myState.originalGuns = mOriginalGuns;

        // Restore the old state when the activity or dialog is being paused
        for (int i = 0; i < mNumberOfControls; i++) {
            GunCorrector.setGuns(i, mOriginalGuns[i]);
        }
        mOriginalGuns = null;

        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        mNumberOfControls = myState.controlCount;
        mOriginalGuns = myState.originalGuns;
        mCurrentGuns = myState.currentGuns;

        for (int index = 0; index < mNumberOfControls; index++) {
            for (int gun = 0; gun < BAR_GUNS.length; gun++) {
                mSeekBars[index][gun].setGuns(Integer.valueOf(mCurrentGuns[index][gun]));
            }
            GunCorrector.setGuns(index, TextUtils.join(" ", mCurrentGuns[index]));
        }
    }

    public static boolean isSupported() {
        try {
            return GunCorrector.isSupported();
        } catch (NoClassDefFoundError e) {
            // Hardware abstraction framework isn't installed
            return false;
        }
    }

    public void restore(Context context) {
        if (!isSupported()) {
            return;
        }

        for (int i = 0; i < GunCorrector.getNumberOfControls(); i++) {
            final String values = mOriginalGuns[i];
            if (values != null) {
                GunCorrector.setGuns(i, values);
            }
        }
    }

    private static class SavedState extends BaseSavedState {
        int controlCount;
        String[] originalGuns;
        String[][] currentGuns;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            controlCount = source.readInt();
            originalGuns = source.createStringArray();
            currentGuns = new String[controlCount][];
            for (int i = 0; i < controlCount; i++) {
                currentGuns[i] = source.createStringArray();
            }
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(controlCount);
            dest.writeStringArray(originalGuns);
            for (int i = 0; i < controlCount; i++) {
                dest.writeStringArray(currentGuns[i]);
            }
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {

                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    private class GunSeekBar implements SeekBar.OnSeekBarChangeListener {
        private int mControlIndex;
        private int mGunIndex;
        private int mOriginal;
        private int mMin;
        private SeekBar mSeekBar;
        private TextView mValue;

        public GunSeekBar(int controlIndex, int gunIndex, ViewGroup container) {
            mControlIndex = controlIndex;
            mGunIndex = gunIndex;

            mMin = GunCorrector.getMinValue(controlIndex);

            mValue = (TextView) container.findViewById(R.id.gun_value);
            mSeekBar = (SeekBar) container.findViewById(R.id.gun_seekbar);

            TextView label = (TextView) container.findViewById(R.id.gun_text);
            label.setText(container.getContext().getString(BAR_GUNS[gunIndex]));

            mSeekBar.setMax(GunCorrector.getMaxValue(controlIndex) - mMin);
            mSeekBar.setProgress(0);
            mValue.setText(String.valueOf(mSeekBar.getProgress() + mMin));

            // this must be done last, we don't want to apply our initial value to the hardware
            mSeekBar.setOnSeekBarChangeListener(this);
        }

        public void setGuns(int gamma) {
            mSeekBar.setProgress(gamma - mMin);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mCurrentGuns[mControlIndex][mGunIndex] = String.valueOf(progress + mMin);
                GunCorrector.setGuns(mControlIndex,
                        TextUtils.join(" ", mCurrentGuns[mControlIndex]));
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

