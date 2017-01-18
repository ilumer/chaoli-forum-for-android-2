package com.daquexian.chaoli.forum.meta;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daquexian.chaoli.forum.model.Post;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 包含QuoteView和OnlineImgTextView
 * 用于显示帖子
 * Created by jianhao on 16-8-26.
 */
public class PostContentView extends LinearLayout {
    private final static String TAG = "PostContentView";
    private final static String QUOTE_START_TAG = "[quote";
    private final static Pattern QUOTE_START_PATTERN = Pattern.compile("\\[quote(=(\\d+?):@(.*?))?]");
    private final static String QUOTE_END_TAG = "[/quote]";
    private final static Pattern ATTACHMENT_PATTERN = Pattern.compile("\\[attachment:(.*?)]");

    private Context mContext;
    private Post mPost;
    private int mConversationId;
    private List<Post.Attachment> mAttachmentList;

    private Boolean mShowQuote = true;

    public PostContentView(Context context) {
        super(context);
        init(context);
    }
    public PostContentView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }
    public PostContentView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        init(context);
    }



    public void setPost(Post post) {
        removeAllViews();
        mPost = post;
        mAttachmentList = post.getAttachments();
        List<Post.Attachment> attachmentList = new ArrayList<>(post.getAttachments());
        String content = post.getContent();

        Matcher attachmentMatcher = ATTACHMENT_PATTERN.matcher(content);
        while (attachmentMatcher.find()) {
            String id = attachmentMatcher.group(1);
            for (int i = attachmentList.size() - 1; i >= 0; i--) {
                Post.Attachment attachment = attachmentList.get(i);
                if (attachment.getAttachmentId().equals(id)) {
                    attachmentList.remove(i);
                }
            }
        }

        int quoteStartPos, quoteEndPos = 0;
        String piece, quote;
        Matcher quoteMatcher = QUOTE_START_PATTERN.matcher(content);
        while (quoteEndPos != -1 && quoteMatcher.find(quoteEndPos)) {
            quoteStartPos = quoteMatcher.start();

            if (quoteEndPos != quoteStartPos) {
                piece = content.substring(quoteEndPos, quoteStartPos);
                addLaTeXView(piece);
            }
            quoteEndPos = pairedQuote(content, quoteStartPos);
            //quoteEndPos = content.indexOf(QUOTE_END_TAG, quoteStartPos) + QUOTE_END_TAG.length();
            if (quoteEndPos == -1) {
                piece = content.substring(quoteStartPos);
                addLaTeXView(piece);
                quoteEndPos = content.length();
            } else if (mShowQuote) {
                quote = content.substring(quoteStartPos + quoteMatcher.group().length(), quoteEndPos - QUOTE_END_TAG.length());
                addQuoteView(quote);
            } else {
                addQuoteView("...");
            }
        }
        if (quoteEndPos != content.length()) {
            piece = content.substring(quoteEndPos);
            addLaTeXView(piece);
        }

        SpannableStringBuilder builder = new SpannableStringBuilder();

        for (Post.Attachment attachment : attachmentList) {
            if (attachment.getFilename().endsWith(".jpg") || attachment.getFilename().endsWith(".png")) {
                String url = Constants.ATTACHMENT_IMAGE_URL + attachment.getAttachmentId() + attachment.getSecret();
                ImageView imageView = new ImageView(mContext);
                imageView.setMaxWidth(Constants.MAX_IMAGE_WIDTH);
                imageView.setAdjustViewBounds(true);
                imageView.setPadding(0, 0, 0, 10);
                Glide.with(mContext)
                        .load(url)
                        .placeholder(new ColorDrawable(ContextCompat.getColor(mContext,android.R.color.darker_gray)))
                        .into(imageView);
                addView(imageView);
            } else {
                try {
                    final String finalUrl = "https://chaoli.club/index.php/attachment/" + attachment.getAttachmentId() + "_" + URLEncoder.encode(attachment.getFilename(), "UTF-8");
                    int start = builder.length();
                    builder.append(attachment.getFilename());
                    builder.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            Log.d(TAG, "onClick() called with: view = [" + view + "]");
                            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl)));
                        }
                    }, start, start + attachment.getFilename().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    builder.append("\n\n");
                } catch (UnsupportedEncodingException e) {
                    Log.w(TAG, "parse: ", e);
                }
            }
        }

        if (builder.length() > 0) {
            TextView textView = new TextView(mContext);
            textView.setText(builder);
            /**
             * make links clickable
             */
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            addView(textView);
        }
    }



    private int pairedQuote(String str, int from) {
        int times = 0;
        for (int i = from; i < str.length(); i++) {
            if (str.substring(i).startsWith(QUOTE_START_TAG)) {
                times++;
            } else if (str.substring(i).startsWith(QUOTE_END_TAG)) {
                times--;
                if (times == 0) {
                    return i + QUOTE_END_TAG.length();
                }
            }
        }
        return -1;
    }

    private void addLaTeXView(String content) {
        OnlineImgTextView onlineImgTextView;
        onlineImgTextView = new OnlineImgTextView(mContext, mAttachmentList);
        onlineImgTextView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        onlineImgTextView.setText(content);
        /**
         * make links clickable
         */
        onlineImgTextView.setMovementMethod(LinkMovementMethod.getInstance());
        addView(onlineImgTextView);
        //laTeXtView.setOnLongClickListener();
    }

    private void addQuoteView(String content) {
        QuoteView quoteView = new QuoteView(mContext, mAttachmentList);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = params.rightMargin = 20;
        quoteView.setLayoutParams(params);
        quoteView.setOrientation(VERTICAL);
        quoteView.setText(content);
        addView(quoteView);
    }

    public void init(Context context) {
        mContext = context;
        removeAllViews();
    }

    public int getConversationId() {
        return mConversationId;
    }

    public void setConversationId(int mConversationId) {
        this.mConversationId = mConversationId;
    }

    public void showQuote(Boolean showQuote) {
        mShowQuote = showQuote;
    }
}
