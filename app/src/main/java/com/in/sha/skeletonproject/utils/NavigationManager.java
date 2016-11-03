package com.in.sha.skeletonproject.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;

import com.in.sha.skeletonproject.R;
import com.in.sha.skeletonproject.base.BaseActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by dallaskuhn on 19/08/15.
 * Use this Helper to navigate between activities.
 */
public class NavigationManager
{
	/**
	 * Starts the Activity with the animation specified.
	 * @param context
	 * 				The context from which it is being called
	 *
	 * @param activityClass
	 * 				Activity class to navigate to
	 *
	 * @param enterAnimation
	 * 				{@link EnterAnimation} for {@link BaseActivity} to enter. Can specify {@link EnterAnimation#NONE}
	 * @param exitAnim
	 * 				{@link ExitAnimation} for  to activityClass. Animation Performed in {@link BaseActivity#onBackPressed()}. Can specify {@link ExitAnimation#NONE}
	 */
	public static void navigateToActivity(Context context, Class activityClass, @EnterAnimation int enterAnimation, @ExitAnimation int exitAnim)
	{
		Intent i = new Intent(context, activityClass);

		if(exitAnim != ExitAnimation.NONE)
		{
			Bundle bundle = new Bundle();
			bundle.putInt(Constants.EXTRA_ACTIVITY_EXIT_ANIMATION, exitAnim);
			i.putExtras(bundle);
		}

		startActivityWithAnim(context, i, enterAnimation);
	}

	/**
	 * Starts the Activity with the bundle and enterAnimation specified.
	 * @param context
	 * 				The context from which it is being called
	 * @param clazz
	 * 				Activity class to navigate to
	 * @param extras
	 * 				Extras for the {@link BaseActivity} to open.
	 * @param enterAnimation
	 * 				{@link EnterAnimation} for {@link BaseActivity} to enter. Can specify {@link EnterAnimation#NONE}
	 * @param exitAnim
	 * 				{@link ExitAnimation} for  to activityClass. Animation Performed in {@link BaseActivity#onBackPressed()}. Can specify {@link ExitAnimation#NONE}
	 */
	public static void navigateToActivityWithExtras(Context context, Class clazz, Bundle extras, @EnterAnimation int enterAnimation, @ExitAnimation int exitAnim)
	{
		Intent i = new Intent(context, clazz);
		extras.putInt(Constants.EXTRA_ACTIVITY_EXIT_ANIMATION, exitAnim);
		i.putExtras(extras);
		startActivityWithAnim(context, i, enterAnimation);
	}

	/**
	 * Starts the Activity with the flags and animation specified.
	 * @param context
	 * 				The context from which it is being called
	 * @param clazz
	 * 				{@link BaseActivity} class to navigate to
	 * @param flags
	 * 				Flags for the {@link BaseActivity} to open.
	 * @param enterAnimation
	 * 				{@link EnterAnimation} for {@link BaseActivity} to enter. Can specify {@link EnterAnimation#NONE}
	 * @param exitAnim
	 * 				{@link ExitAnimation} for  to activityClass. Animation Performed in {@link BaseActivity#onBackPressed()}. Can specify {@link ExitAnimation#NONE}
	 */
	public static void navigateToActivityWithFlags(Context context, Class clazz, int flags, @EnterAnimation int enterAnimation, @ExitAnimation int exitAnim)
	{
		Intent i = new Intent(context, clazz);
		i.setFlags(flags);

		if(exitAnim != ExitAnimation.NONE)
		{
			Bundle bundle = new Bundle();
			bundle.putInt(Constants.EXTRA_ACTIVITY_EXIT_ANIMATION, exitAnim);
			i.putExtras(bundle);
		}

		startActivityWithAnim(context, i, enterAnimation);

	}

	/**
	 * Starts the Activity with the flags and animation specified.
	 * @param context
	 * 				The context from which it is being called
	 * @param clazz
	 * 				{@link BaseActivity} class to navigate to
	 * @param flags
	 * 				Flags for the {@link BaseActivity} to open.
	 * @param extras
	 * 				Extras for the {@link BaseActivity} to open.
	 * @param enterAnimation
	 * 				{@link EnterAnimation} for {@link BaseActivity} to enter. Can specify {@link EnterAnimation#NONE}
	 * @param exitAnim
	 * 				{@link ExitAnimation} for  to activityClass. Animation Performed in {@link BaseActivity#onBackPressed()}. Can specify {@link ExitAnimation#NONE}
	 */
	public static void navigateToActivityWithExtrasAndFlags(Context context, Class clazz, Bundle extras, int flags, @EnterAnimation int enterAnimation, @ExitAnimation int exitAnim)
	{
		Intent i = new Intent(context, clazz);
		i.setFlags(flags);
		extras.putInt(Constants.EXTRA_ACTIVITY_EXIT_ANIMATION, exitAnim);
		i.putExtras(extras);
		startActivityWithAnim(context, i, enterAnimation);

	}

	/**
	 * Starts the Intent with the animation specified.
	 * @param context
	 * 				The context from which it is being called
	 * @param intent
	 * 				Intent used to start ativity
	 * @param enterAnimation
	 * 				{@link EnterAnimation} for {@link BaseActivity} to enter. Can specify {@link EnterAnimation#NONE}
     */
	private static void startActivityWithAnim(Context context, Intent intent, @EnterAnimation int enterAnimation)
	{
		context.startActivity(intent);

		if(context instanceof BaseActivity)
		{
			BaseActivity activity = (BaseActivity) context;
			switch (enterAnimation)
			{
				case EnterAnimation.SLIDE_IN_RIGHT:
					activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
					break;
				case EnterAnimation.FADE_IN:
					activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					break;
				case EnterAnimation.NONE:
					break;
			}
		}

	}

	/*
	 * Inner classes, interfaces, enums
	 */
	@IntDef( {EnterAnimation.SLIDE_IN_RIGHT, EnterAnimation.FADE_IN, EnterAnimation.NONE} )
	@Retention(RetentionPolicy.SOURCE)
	public @interface EnterAnimation {

		int NONE = 0;
		int FADE_IN = 1;
		int SLIDE_IN_RIGHT = 2;
	}

	@IntDef( {ExitAnimation.SLIDE_OUT_RIGHT, ExitAnimation.NONE} )
	@Retention(RetentionPolicy.SOURCE)
	public @interface ExitAnimation{

		int NONE = 0;
		int SLIDE_OUT_RIGHT = 1;
	}
}
