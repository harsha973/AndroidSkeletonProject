package com.in.sha.skeletonproject.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.in.sha.skeletonproject.R;
import com.in.sha.skeletonproject.dialog.callback.AlertDialogCallBack;


/**
 * Created by sreepolavarapu on 29/09/15.
 */
public class AlertDialogFragment extends DialogFragment {

    public static final String MESSAGE = "message";
    public static final String TAG = "Tag";
    public static final String TITLE = "title";
    public static final String POSITVE_BUTTON_TEXT = "p_button_text";
    public static final String NEGATIVE_BUTTON_TEXT = "n_button_text";
    public static final String HAS_NEGATIVE_BUTTON = "negative_button";

    private AlertDialogCallBack mAlertCallback;

    private String mTitle;
    private String mMessage;
    private @DialogTags
    String mTag;
    private String mPositiveButtonText;
    private String mNegativeButtonText;

    private boolean mHasNegativeButton;

    /**
     * Creates an alert dialog without any negative button. Uses OK text for the positive button
     * @param title Title of the Dialog
     * @param message   Message for the dialog
     * @param tag   Used for the callback
     * @return  The created AlertDialogFragment
     */
    public static AlertDialogFragment newInstance(String title, String message, String tag) {
        return newInstance(title, message, null, null, tag);
    }


    /**
     * Creates instance of Alert Dialog with provided data.
     * @param title Title of the Dialog
     * @param message   Message for the dialog
     * @param positiveButtonText    Positive Button text
     * @param negativeButtonText    Negative button text. Pass @null to ignore negative button
     * @param tag   Used for the callback
     * @return  The created AlertDialogFragment
     */
    public static AlertDialogFragment newInstance(String title, String message,
                                                  String positiveButtonText,
                                                  String negativeButtonText,
                                                  @DialogTags String tag) {

        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(MESSAGE, message);
        args.putString(TAG, tag);
        args.putString(POSITVE_BUTTON_TEXT, positiveButtonText);
        args.putBoolean(HAS_NEGATIVE_BUTTON, negativeButtonText != null);
        args.putString(NEGATIVE_BUTTON_TEXT, negativeButtonText);

        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates an alert dialog without any negative button.
     * @param title Title of the Dialog
     * @param message   Message for the dialog
     * @param positiveButtonText    Positive Button text
     * @param tag   Used for the callback
     * @return  The created AlertDialogFragment
     */
    public static AlertDialogFragment newInstance(String title, String message,
                                                  String positiveButtonText,
                                                  @DialogTags String tag) {

        return newInstance(title, message, positiveButtonText, null, tag);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(getArguments() != null)
        {
            //noinspection WrongConstant
            mTag = getArguments().getString(TAG);
            mTitle = getArguments().getString(TITLE);
            mMessage = getArguments().getString(MESSAGE);
            mPositiveButtonText = getArguments().getString(POSITVE_BUTTON_TEXT,
                    getString(android.R.string.ok));
            mNegativeButtonText = getArguments().getString(NEGATIVE_BUTTON_TEXT,
                    getString(R.string.cancel));
            mHasNegativeButton = getArguments().getBoolean(HAS_NEGATIVE_BUTTON);

        }
        setCancelable(false);
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(),R.style.CommonAlertDialog);
        alertDialogBuilder.setTitle(mTitle);
        alertDialogBuilder.setMessage(mMessage);

        alertDialogBuilder.setPositiveButton(mPositiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAlertCallback.onConfirmed(mTag);
            }
        });
        if(mHasNegativeButton)
        {
            alertDialogBuilder.setNegativeButton(mNegativeButtonText, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    mAlertCallback.onCancelled(mTag);
                }
            });
        }

        return alertDialogBuilder.create();
    }

    public void registerCallBack(AlertDialogCallBack dialogInterface)
    {
        this.mAlertCallback = dialogInterface;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
