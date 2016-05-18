package com.geno.chaoli.forum.meta;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.geno.chaoli.forum.R;

public class AvatarView extends RelativeLayout
{
	final String TAG = "AvatarView";

	String mImagePath, mUsername;
	int mUserId;
	Boolean firstLoad = true;
	RelativeLayout v;
	TextView t;
	ImageView i;
	public AvatarView(final Context context, final String imagePath, int userId, String username)
	{
		this(context, null);
		update(context, imagePath, userId, username);
	}

	public void update(Context context, String imagePath, int userId, String username) {
		mImagePath = imagePath;
		mUserId = userId;
		mUsername = username;
		if(firstLoad) {
			v = (RelativeLayout) inflate(context, R.layout.avatar_view, this);
			t = (TextView) v.findViewById(R.id.avatarTxt);
			i = (ImageView) v.findViewById(R.id.avatarImg);
			t.setTextSize(20);
			firstLoad = false;
		}

		if (Constants.NONE.equals(imagePath) || imagePath == null)
		{
			t.setText(String.format("%s", username.toUpperCase().charAt(0)));
			Log.d(TAG, t.getText().toString());
			Log.d(TAG, String.valueOf(t.getVisibility() == VISIBLE));
			i.setVisibility(INVISIBLE);
		}
		else
		{
			Log.d(TAG, "here2");
			Glide.with(context).load(Constants.avatarURL + "avatar_" + userId + "." + imagePath).into(i);
			t.setVisibility(INVISIBLE);
		}
	}

	public AvatarView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public AvatarView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public AvatarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
	}
}
