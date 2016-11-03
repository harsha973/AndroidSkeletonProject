package com.in.sha.skeletonproject.dialog;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by sreepolavarapu on 26/07/16.
 * Tags for Dialogs used in the entire application.
 */
@StringDef({
        DialogTags.DEFAULT,
        DialogTags.DO_NOTHING,
        DialogTags.SIGN_OUT,
        DialogTags.WARNING_NO})

@Retention(RetentionPolicy.SOURCE)
public @interface DialogTags {

    String DEFAULT = "DEFAULT";
    String DO_NOTHING = "DO_NOTHING";
    String WARNING_NO = "WARNING_NO";
    String SIGN_OUT = "SIGN_OUT";
}
