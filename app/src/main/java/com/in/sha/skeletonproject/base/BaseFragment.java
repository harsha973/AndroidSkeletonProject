package com.in.sha.skeletonproject.base;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;

import com.in.sha.skeletonproject.R;
import com.in.sha.skeletonproject.dialog.AlertDialogFragment;
import com.in.sha.skeletonproject.dialog.DialogTags;
import com.in.sha.skeletonproject.dialog.callback.AlertDialogCallBack;
import com.in.sha.skeletonproject.models.ErrorResponseModel;
import com.in.sha.skeletonproject.utils.GeneralUtils;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by sree on 14/06/15.
 */
public class BaseFragment extends Fragment implements AlertDialogCallBack {
    private String TAG = BaseFragment.class.getSimpleName();

    protected MaterialProgressBar mProgressBar;
    //  Register all subscriptions in this fragment to variable.
    //  This has code to unsubscribe in onDestroy()
    protected CompositeSubscription mCompositeSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = (MaterialProgressBar) view.findViewById(R.id.material_circular_progress);
    }

    @Override
    public void onDestroy() {
        //  Just in case
        unblockTouches();
        if( mCompositeSubscription.hasSubscriptions() &&
                !mCompositeSubscription.isUnsubscribed())
        {
            mCompositeSubscription.unsubscribe();
        }

        super.onDestroy();
    }

    @CallSuper
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //  Clearing menu, some times when activity is created from savedstateinstance,
        //  Menu is duplicated. Make sure in child class super.onCreateOptionsMenu is called before
        //  inflating menu.
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    protected void setTitle(int stringResID)
    {
        ActionBar actionBar = ((BaseActivity) getActivity()).getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setTitle(stringResID);
        }
    }

    /**
     * Method to show circular loading progress. Will reference progress
     * named {R.id.material_circular_progress}
     * @param shouldBlockTouches    Will block all the UI touches if it is set to true
     */
    protected void showProgress(boolean shouldBlockTouches)
    {
        if(mProgressBar != null)
        {
            mProgressBar.setVisibility(View.VISIBLE);

            //  BLOCK TOUCHES IF NEEDED
            if(shouldBlockTouches &&
                    GeneralUtils.isContextActive(getActivity()))
            {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }

    }

    /**
     * Method to hide circular loading progress.
     */
    public void hideProgress()
    {
        if(mProgressBar != null)
        {
            mProgressBar.setVisibility(View.GONE);
        }

        // UNBLOCK TOUCHES
        unblockTouches();
    }

    /**
     * Unblock touches imposed when showing progress
     */
    private void unblockTouches()
    {
        // UNBLOCK TOUCHES
        if(GeneralUtils.isContextActive(getActivity()))
        {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
    //  ---- ALERT DIALOGS
    /**
     * Displays error dialog
     * @param errorModel    Error model from response object
     * @param tag   The tag to differentiate dialog callbacks
     */
    public void showErrorDialog(ErrorResponseModel errorModel, String tag)
    {
        if(GeneralUtils.isContextActive(getActivity()))
        {
            AlertDialogFragment dialogFragment = AlertDialogFragment.newInstance(getString(R.string.error),
                    errorModel.getMessage(),
                    tag);

            showDialog(dialogFragment);
        }
    }


    /**
     * Displays error dialog
     * @param errorMessage    Error from response object
     * @param tag   The tag to differentiate dialog callbacks
     */
    public void showErrorDialog(String errorMessage, String tag)
    {
        if(GeneralUtils.isContextActive(getActivity())) {
            AlertDialogFragment dialogFragment = AlertDialogFragment.newInstance(getString(R.string.error),
                    errorMessage,
                    tag);

            showDialog(dialogFragment);
        }
    }

    /**
     * Displays  dialog with the provided params
     */
    protected void showDialog(String title, String message, String poistiveButonText, String negativeButtonText, String tag)
    {
        if(GeneralUtils.isContextActive(getActivity()))
        {
            AlertDialogFragment dialogFragment = AlertDialogFragment.newInstance(title,
                    message,
                    poistiveButonText,
                    negativeButtonText,
                    tag);

            showDialog(dialogFragment);
        }
    }

    /**
     * Displays  dialog with the provided params
     */
    protected void showDialog(String title, String message, String poistiveButonText, @DialogTags String tag)
    {
        if(GeneralUtils.isContextActive(getActivity()))
        {
            AlertDialogFragment dialogFragment = AlertDialogFragment.newInstance(title,
                    message,
                    poistiveButonText,
                    tag);

            showDialog(dialogFragment);
        }
    }

    private void showDialog(AlertDialogFragment dialogFragment)
    {
        dialogFragment.registerCallBack(this);
        dialogFragment.show(getFragmentManager(), TAG);
    }

    //  ---- END ALERT DIALOGS

    @Override
    public void onConfirmed(@DialogTags String tag) {
        //  Handle this in child classes
    }

    @Override
    public void onCancelled(@DialogTags String tag) {
        //  Handle this in child classes
    }

    /**
     * Back pressed for the Fragment. Override this method in child classes of needed.
     *
     * @return  False, if it can be passed to activity to handle.
     * True, if fragment handled backpress
     *
     * <p><b>Note</b></p> Its not good to have logic in back pressed as it may lag user to navigate back.
     * But in some cases we have to handle. No other option :(
     */
    public boolean onBackPressed()
    {
        return false;
    }
}
