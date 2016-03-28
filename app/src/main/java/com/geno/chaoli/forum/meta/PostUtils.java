package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class PostUtils
{
	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void reply(final Context context, int conversationId, String content, final ReplyObserver observer)
	{
		CookieUtils.saveCookie(client, context);
		String url = Constants.replyURL + "/" + conversationId;
		RequestParams param = new RequestParams();
		param.put("conversationId", conversationId + "");
		param.put("content", content);
		param.put("userId", LoginUtils.getUserId());
		param.put("token", LoginUtils.getToken());
		client.post(context, url, param, new AsyncHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
			{
				observer.onReplySuccess();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
			{
				String response = statusCode + ": " + new String(responseBody);
				Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
				observer.onReplyFailure(statusCode);
			}
		});
	}

	public static void edit(final Context context, int postId, String content, final EditObserver observer)
	{
		CookieUtils.saveCookie(client, context);
		String url = Constants.editURL + "/" + postId;
		RequestParams param = new RequestParams();
		param.put("content", content);
		param.put("save", "true");
		param.put("userId", LoginUtils.getUserId());
		param.put("token", LoginUtils.getToken());
		client.post(context, url, param, new AsyncHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
			{
				observer.onEditSuccess();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
			{
				observer.onEditFailure(statusCode);
			}
		});
	}

	public interface ReplyObserver
	{
		void onReplySuccess();
		void onReplyFailure(int statusCode);
	}

	public interface EditObserver
	{
		void onEditSuccess();
		void onEditFailure(int statusCode);
	}
}
