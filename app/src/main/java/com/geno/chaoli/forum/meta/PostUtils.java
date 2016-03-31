package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.geno.chaoli.forum.ReplyAction;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class PostUtils
{
	public static final String TAG = "PostUtils";

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void reply(final Context context, int conversationId, String content, final ReplyObserver observer)
	{
		CookieUtils.saveCookie(client, context);
		RequestParams param = new RequestParams();
		param.put("conversationId", conversationId + "");
		param.put("content", content);
		param.put("userId", LoginUtils.getUserId());
		param.put("token", LoginUtils.getToken());
		client.post(context, Constants.replyURL + conversationId, param, new AsyncHttpResponseHandler()
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
				Log.d(TAG, "onFailure: " + response);
				Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
				observer.onReplyFailure(statusCode);
			}
		});
	}

	public static void edit(final Context context, int postId, String content, final EditObserver observer)
	{
		CookieUtils.saveCookie(client, context);
		RequestParams param = new RequestParams();
		param.put("content", content);
		param.put("save", "true");
		param.put("userId", LoginUtils.getUserId());
		param.put("token", LoginUtils.getToken());
		client.post(context, Constants.editURL + postId, param, new AsyncHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
			{
				Toast.makeText(context, new String(responseBody), Toast.LENGTH_SHORT).show();
				observer.onEditSuccess();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
			{
				Toast.makeText(context, statusCode + new String(responseBody), Toast.LENGTH_SHORT).show();
				observer.onEditFailure(statusCode);
			}
		});
	}

	public static void preQuote(final Context context, int postId)
	{
		CookieUtils.saveCookie(client, context);
		RequestParams param = new RequestParams();
		param.put("userId", LoginUtils.getUserId());
		param.put("token", LoginUtils.getToken());
		client.get(context, Constants.preQuoteURL + postId, param, new AsyncHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
			{
				Toast.makeText(context, "Pre Quote success. " + new String(responseBody), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
			{
				Toast.makeText(context, "Pre Quote fail: " + statusCode + new String(responseBody), Toast.LENGTH_SHORT).show();
				Log.d(TAG, "onFailure: " + new String(responseBody));
			}
		});
	}

	public static void quote(final Context context, int conversationId, String content, final QuoteObserver observer)
	{
		CookieUtils.saveCookie(client, context);
		RequestParams param = new RequestParams();
		param.put("conversationId", conversationId);
		param.put("content", content);
		param.put("userId", LoginUtils.getUserId());
		param.put("token", LoginUtils.getToken());
		client.post(context, Constants.quoteURL + conversationId, param, new AsyncHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
			{
				Toast.makeText(context, "Quote success.", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "onSuccess: " + new String(responseBody));
				observer.onQuoteSuccess();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
			{
				Toast.makeText(context, "Quote failed: " + statusCode + new String(responseBody), Toast.LENGTH_SHORT).show();
				observer.onQuoteFailure(statusCode);
			}
		});
	}

	/*public static void quote(final Context context, final Post post, final QuoteObserver observer)
	{
		CookieUtils.saveCookie(client, context);
		client.get(context, Constants.preQuoteURL + "/" + post.conversationId + "/" + post.floor + "&userId=" + LoginUtils.getUserId() + "&token=" + LoginUtils.getToken(), new AsyncHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
			{
				Toast.makeText(context, new String(responseBody), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
			{
				Toast.makeText(context, statusCode + new String(responseBody), Toast.LENGTH_SHORT).show();
			}
		});
		client.get(context, Constants.quoteURL + "/" + post.postId + "&userId=" + LoginUtils.getUserId() + "&token=" + LoginUtils.getToken(), new AsyncHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
			{
				Intent reply = new Intent(context, ReplyAction.class);
				reply.putExtra("conversationId", post.conversationId);
				reply.putExtra("replyMsg", "[quote=" + post.postId + ":@" + post.username + "]" + post.content + "[/quote]");
				context.startActivity(reply);
				observer.onQuoteSuccess();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
			{
				Toast.makeText(context, "Quote error " + statusCode + ": " + new String(responseBody), Toast.LENGTH_SHORT).show();
				observer.onQuoteFailure(statusCode);
			}
		});
	}*/

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

	public interface QuoteObserver
	{
		void onQuoteSuccess();
		void onQuoteFailure(int statusCode);
	}
}
