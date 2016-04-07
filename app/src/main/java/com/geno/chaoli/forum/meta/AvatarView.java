package com.geno.chaoli.forum.meta;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.geno.chaoli.forum.R;

public class AvatarView extends RelativeLayout
{
	public AvatarView(final Context context, final String imagePath, int userId, String username)
	{
		this(context, null);
		RelativeLayout v = (RelativeLayout) inflate(context, R.layout.avatar_view, this);
		final TextView t = (TextView) v.findViewById(R.id.avatarTxt);
		final ImageView i = (ImageView) v.findViewById(R.id.avatarImg);
		t.setTextSize(20);

		if (imagePath == null)
		{
			t.setText(String.format("%s", username.toUpperCase().charAt(0)));
			i.setVisibility(INVISIBLE);
		}
		else
		{
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
