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

import com.felipecsl.gifimageview.library.GifImageView;
import com.geno.chaoli.forum.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

// TODO: 16-3-29 2346 Buffer avatars.
public class AvatarView extends RelativeLayout
{
	public AvatarView(final Context context, final String imagePath, int userId, String username)
	{
		this(context, null);
		AsyncHttpClient client = new AsyncHttpClient();
		RelativeLayout v = (RelativeLayout) inflate(context, R.layout.avatar_view, this);
		final TextView t = (TextView) v.findViewById(R.id.avatarTxt);
		final ImageView i = (ImageView) v.findViewById(R.id.avatarImg);
		final GifImageView g = (GifImageView) v.findViewById(R.id.avatarGIFImg);
		t.setTextSize(20);
		if (imagePath == null)
		{
			t.setText(String.format("%s", username.toUpperCase().charAt(0)));
			i.setVisibility(INVISIBLE);
		}
		else
			client.get(context, Constants.avatarURL + "avatar_" + userId + "." + imagePath, new AsyncHttpResponseHandler()
			{
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
				{
					if (imagePath.toLowerCase().endsWith("gif"))
					{
						t.setVisibility(INVISIBLE);
						i.setVisibility(INVISIBLE);
						g.setVisibility(VISIBLE);
						g.setBytes(responseBody);
						g.startAnimation();
					}
					else
					{
						RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(), BitmapFactory.decodeByteArray(responseBody, 0, responseBody.length));
						drawable.setCircular(true);
						i.setImageDrawable(drawable);
						t.setVisibility(INVISIBLE);
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
				{
					Toast.makeText(context, android.R.string.httpErrorBadUrl, Toast.LENGTH_SHORT).show();
				}
			});
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
