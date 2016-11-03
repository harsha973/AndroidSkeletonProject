package com.in.sha.skeletonproject.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.in.sha.skeletonproject.R;
import com.in.sha.skeletonproject.startup.StartupActivity;
import com.in.sha.skeletonproject.utils.Constants;
import com.in.sha.skeletonproject.utils.NavigationManager;
import com.in.sha.skeletonproject.utils.PreferencesUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends AppCompatActivity
{
	public static final String ACTION_SIGN_OUT = "co.in.sha.skeletonproject.ACTION_SIGN_OUT";
	public static final String TAG = BaseActivity.class.getSimpleName();

	/**
	 * Used as an exit navigation
	 * This should be one of the {@link android.support.annotation.IntDef} in {@link NavigationManager.ExitAnimation}.
	 * TYPE of animation is played in onDestroy() of the Activity.
	 */
	private @NavigationManager.ExitAnimation int mExitAnim;

	@Override
	protected void attachBaseContext(Context newBase)
	{
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Bundle bundle = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();

		if(bundle != null)
		{
			mExitAnim = bundle.getInt(Constants.EXTRA_ACTIVITY_EXIT_ANIMATION, NavigationManager.ExitAnimation.NONE);
		}

		//	Register broadcast to sing out
		LocalBroadcastManager.getInstance(this)
				.registerReceiver(signOutBroadcastReceiver, new IntentFilter(ACTION_SIGN_OUT));

	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
			{
				onBackPressed();

				return true;
			}
			default:
			{
				return super.onOptionsItemSelected(item);
			}
		}
	}

	/**
	 *
	 * @return The current fragment attached to id {@link R.id#fragment_container}
	 *
	 * <p><b>BIG  NOTE: </b></p> This considers our activity is using {@link R.id#fragment_container}
	 */
	public Fragment getCurrentFragment(){

		FragmentManager fragmentManager = getSupportFragmentManager();
		return fragmentManager.findFragmentById(R.id.fragment_container);
	}

	@Override
	public void onBackPressed() {

		//	Get the Top fragment and check if the fragment handles the back pressed event.
		Fragment fragment = getCurrentFragment();

		if( fragment != null &&
				fragment instanceof BaseFragment)
		{
			boolean isBackPressHandledByFragment = ((BaseFragment)fragment).onBackPressed();

			if(isBackPressHandledByFragment)
			{
				//	Do not handle back press if it i already handled in fragment.
				return;
			}
		}

		//	Fragment does not handle back pressed if you are here.
		super.onBackPressed();

		switch (mExitAnim)
		{
			case NavigationManager.ExitAnimation.SLIDE_OUT_RIGHT:
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
				break;
			case NavigationManager.ExitAnimation.NONE:
			default:
				break;
		}
	}

	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this)
				.unregisterReceiver(signOutBroadcastReceiver);
		super.onDestroy();
	}

	//  SIGN OUT BROADCAST RECEIVER
	private BroadcastReceiver signOutBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();

			//  TODO - Clear file system
			//  Clear preferences
			PreferencesUtils.clear(BaseActivity.this);
			//  Start Login Activity
			NavigationManager.navigateToActivityWithFlags(BaseActivity.this,
					StartupActivity.class,
					//	Flags to make sure StartupActivity should be called only once
					Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP,
					NavigationManager.EnterAnimation.NONE,
					NavigationManager.ExitAnimation.NONE);

		}
	};

}