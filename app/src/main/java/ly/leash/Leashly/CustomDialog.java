package ly.leash.Leashly;

/**
 * Created by schwallie on 1/25/2015.
 * Leashly!
 */

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class CustomDialog extends DialogFragment {
    static String DialogboxTitle;
    EditText txtname;
    Button btnDone;

    //---empty constructor required
    public CustomDialog() {

    }

    //---set the title of the dialog window
    public void setDialogTitle(String title) {
        DialogboxTitle = title;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(
                R.layout.alert_view, container);

        //---get the EditText and Button views
        txtname = (EditText) view.findViewById(R.id.txtName);
        btnDone = (Button) view.findViewById(R.id.btnDone);

        //---event handler for the button
        btnDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                //---gets the calling activity
                InputNameDialogListener activity = (InputNameDialogListener) getActivity();
                activity.onFinishInputDialog(txtname.getText().toString());

                //---dismiss the alert
                dismiss();
            }
        });

        //---show the keyboard automatically
        txtname.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //---set the title for the dialog
        getDialog().setTitle(DialogboxTitle);

        return view;
    }

    public interface InputNameDialogListener {
        void onFinishInputDialog(String inputText);
    }
}