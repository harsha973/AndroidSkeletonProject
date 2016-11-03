package com.in.sha.skeletonproject.dialog.callback;

import com.in.sha.skeletonproject.dialog.DialogTags;

/**
 * Created by sreepolavarapu on 29/09/15.
 */
public interface AlertDialogCallBack {

    void onConfirmed(@DialogTags String tag);
    void onCancelled(@DialogTags String tag);
}