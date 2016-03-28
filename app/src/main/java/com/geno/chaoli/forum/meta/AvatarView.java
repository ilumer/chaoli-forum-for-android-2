package com.geno.chaoli.forum.meta;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.Toast;

import com.geno.chaoli.forum.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class AvatarView extends ImageView
{
	public Context context;
	public String image;

	public AvatarView(final Context context, String imagePath, int userId, String username)
	{
		this(context, null);
		AsyncHttpClient client = new AsyncHttpClient();
		if (imagePath == null)
		{
			int x = (int) getResources().getDimension(R.dimen.avatar_diameter);
			Bitmap b = Bitmap.createBitmap(x, x, Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(b);
			c.drawText(username.substring(0, 1), 0, 0, new Paint());
			AvatarView.this.setImageBitmap(b);
		}
		else
		{
			String imageURL = Constants.avatarURL + "/avatar_" + userId + "." + imagePath;
			client.get(context, imageURL, new AsyncHttpResponseHandler()
			{
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
				{
					AvatarView.this.setImageBitmap(BitmapFactory.decodeByteArray(responseBody, 0, responseBody.length));
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
				{
					Toast.makeText(context, android.R.string.httpErrorBadUrl, Toast.LENGTH_SHORT).show();
				}
			});
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

	@TargetApi(21)
	public AvatarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
	}
}
