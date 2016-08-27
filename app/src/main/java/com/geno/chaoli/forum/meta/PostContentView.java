package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.text.style.QuoteSpan;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jianhao on 16-8-26.
 */
public class PostContentView extends LinearLayout {
    private final static Pattern QUOTE_START_PATTERN = Pattern.compile("\\[quote(=(\\d+?):@(.*?))?]");
    private final static String QUOTE_END_TAG = "[/quote]";

    private Context mContext;

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

    public void setText(String content) {
        int quoteStartPos, quoteEndPos = 0;
        String piece, quote;
        LaTeXtView laTeXtView;
        Matcher quoteMatcher = QUOTE_START_PATTERN.matcher(content);
        //while ((quoteStartPos = content.indexOf(QUOTE_START_TAG, quoteEndPos)) >= 0) {
        while (quoteMatcher.find()) {
            quoteStartPos = quoteMatcher.start();

            if (quoteEndPos != quoteStartPos) {
                piece = content.substring(quoteEndPos, quoteStartPos);
                laTeXtView = new LaTeXtView(mContext);
                laTeXtView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                laTeXtView.setText(piece);
                addView(laTeXtView);

            }
            quoteEndPos = content.indexOf(QUOTE_END_TAG, quoteStartPos) + QUOTE_END_TAG.length();
            if (quoteEndPos == -1) {
                piece = content.substring(quoteStartPos);
                laTeXtView = new LaTeXtView(mContext);
                laTeXtView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                laTeXtView.setText(piece);
                addView(laTeXtView);
                quoteEndPos = content.length();
            } else {
                quote = content.substring(quoteStartPos + quoteMatcher.group().length(), quoteEndPos - QUOTE_END_TAG.length());
                QuoteView quoteView = new QuoteView(mContext);
                quoteView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                quoteView.setOrientation(VERTICAL);
                quoteView.setText(quote);
                addView(quoteView);
            }
        }
        if (quoteEndPos != content.length()) {
            piece = content.substring(quoteEndPos);
            laTeXtView = new LaTeXtView(mContext);
            laTeXtView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            laTeXtView.setText(piece);
            addView(laTeXtView);
        }
    }

    public void init(Context context) {
        mContext = context;
        removeAllViews();
    }
}
