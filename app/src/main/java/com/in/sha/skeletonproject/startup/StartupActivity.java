package com.in.sha.skeletonproject.startup;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import com.in.sha.skeletonproject.R;
import com.in.sha.skeletonproject.base.BaseActivity;


/**
 * Created by sreepolavarapu on 1/11/16.
 */

public class StartupActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_generic);
    }
}
