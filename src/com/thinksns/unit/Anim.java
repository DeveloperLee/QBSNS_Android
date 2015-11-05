package com.thinksns.unit;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import com.thinksns.android.R;

/**
 * 动画工具类
 */
public class Anim {
	public static void exit(Activity obj) {
		obj.overridePendingTransition(android.R.anim.slide_out_right,
				android.R.anim.slide_in_left);
	}

	public static void in(Activity obj) {
		obj.overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}

	public static void refresh(Context context, View v) {
		refresh(context, v, R.drawable.spinner_black_16);
	}

	public static void refreshMiddle(Context context, View v) {
		refresh(context, v, R.drawable.spinner_black_20);
	}

	public static void refreshBig(Context context, View v) {
		refresh(context, v, R.drawable.spinner_black_48);
	}
	//刷新动画
	public static void refresh(Context context, View v, int id) {
		v.setBackgroundDrawable(context.getResources().getDrawable(id));
		Animation anim = AnimationUtils.loadAnimation(context,
				R.anim.title_progress);
		// 动画一直不断保持变化
		LinearInterpolator lir = new LinearInterpolator();
		anim.setInterpolator(lir);
		v.startAnimation(anim);
	}
}
