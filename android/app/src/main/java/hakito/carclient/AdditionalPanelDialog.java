package hakito.carclient;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdditionalPanelDialog extends DialogFragment {

    @BindView(R.id.led_seek_bar)
    SeekBar ledSeekBar;
    private PreferenceHelper preferenceHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Theme_Dialog);
        preferenceHelper = new PreferenceHelper(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_additional_panel, container, false);
        ButterKnife.bind(this, view);
        getDialog().setTitle("Additional panel");
        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        preferenceHelper.setLight(ledSeekBar.getProgress());
    }
}
