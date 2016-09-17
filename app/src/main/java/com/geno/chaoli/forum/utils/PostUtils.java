package com.geno.chaoli.forum.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.geno.chaoli.forum.meta.Constants;
import com.geno.chaoli.forum.network.MyOkHttp;
import com.geno.chaoli.forum.utils.LoginUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PostUtils
{
	public static final String TAG = "PostUtils";

	public static void reply(final Context context, int conversationId, String content, final ReplyObserver observer)
	{
		new MyOkHttp.MyOkHttpClient()
				.add("conversationId", String.valueOf(conversationId))
				.add("content", content)
				.add("userId", String.valueOf(LoginUtils.getUserId()))
				.add("token", LoginUtils.getToken())
				.post(Constants.replyURL + conversationId)
				.enqueue(context, new MyOkHttp.Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
						observer.onReplyFailure(-1);
					}

					@Override
					public void onResponse(Call call, Response response, String responseStr) throws IOException {
						if (response.code() != 200) observer.onReplyFailure(response.code());
						else observer.onReplySuccess();
					}
				});
	}

	public static void edit(final Context context, int postId, String content, final EditObserver observer)
	{
		new MyOkHttp.MyOkHttpClient()
				.add("content", content)
				.add("save", "true")
				.add("userId", String.valueOf(LoginUtils.getUserId()))
				.add("token", LoginUtils.getToken())
				.post(Constants.editURL + postId)
				.enqueue(context, new MyOkHttp.Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
						observer.onEditFailure(-1);
					}

					@Override
					public void onResponse(Call call, Response response, String responseStr) throws IOException {
						if (response.code() != 200) observer.onEditFailure(response.code());
						else observer.onEditSuccess();
					}
				});
	}

	/*@Deprecated
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

	@Deprecated
	public static void quote(final Context context, int conversationId, String content, final QuoteObserver observer)
	{
		CookieUtils.saveCookie(client, context);
		RequestParams param = new RequestParams();
		param.put("conversationId", conversationId);
		param.put("content", content);
		param.put("userId", LoginUtils.getUserId());
		param.put("token", LoginUtils.getToken());
		Log.d(TAG, "quote: " + Constants.quoteURL+ conversationId);
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
	}*/

	public static void delete(final Context context, int postId, final DeleteObserver observer)
	{
		new MyOkHttp.MyOkHttpClient()
				.add("userId", String.valueOf(LoginUtils.getUserId()))
				.add("token", LoginUtils.getToken())
				.post(Constants.deleteURL + postId)		// Or get?
				.enqueue(context, new MyOkHttp.Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
						observer.onDeleteFailure(-1);
					}

					@Override
					public void onResponse(Call call, Response response, String responseStr) throws IOException {
						if (response.code() != 200) observer.onDeleteFailure(response.code());
						else observer.onDeleteSuccess();
					}
				});
	}

	public static void restore(final Context context, int postId, final RestoreObserver observer)
	{
		new MyOkHttp.MyOkHttpClient()
				.add("userId", String.valueOf(LoginUtils.getUserId()))
				.add("token", LoginUtils.getToken())
				.post(Constants.deleteURL + postId)		// Or get?
				.enqueue(context, new MyOkHttp.Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
						observer.onRestoreFailure(-1);
					}

					@Override
					public void onResponse(Call call, Response response, String responseStr) throws IOException {
						if (response.code() != 200) observer.onRestoreFailure(response.code());
						else observer.onRestoreSuccess();
					}
				});
	}

	public static Boolean canEdit(int postId) {
		return false;
	}

	public static Boolean canDelete(int postId) {
		return false;
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

	public interface QuoteObserver
	{
		void onQuoteSuccess();
		void onQuoteFailure(int statusCode);
	}

	public interface DeleteObserver
	{
		void onDeleteSuccess();
		void onDeleteFailure(int statusCode);
	}

	public interface RestoreObserver
	{
		void onRestoreSuccess();
		void onRestoreFailure(int statusCode);
	}
}
