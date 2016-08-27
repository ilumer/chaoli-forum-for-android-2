package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geno.chaoli.forum.R;

/**
 * Created by jianhao on 16-8-26.
 */
public class QuoteView extends LinearLayout {
    LaTeXtView mTextView;
    Button mButton;
    Boolean mCollapsed;
    Context mContext;

    private static final String TAG = "QuoteView";
    private static final int bgColor = Color.LTGRAY;

    public QuoteView(Context context) {
        super(context);
        init(context);
    }
    public QuoteView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }
    public QuoteView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        init(context);
    }

    public void setText(String content) {
        mTextView.setText(content);
        mTextView.post(new Runnable() {
            @Override
            public void run() {
                if (mTextView.getLineCount() > 3) {
                    collapse();
                    mButton.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    mButton.setBackgroundColor(bgColor);
                    mButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mCollapsed) {
                                expand();
                            } else {
                                collapse();
                            }
                        }
                    });
                    addView(mButton);
                }
            }
        });
    }

    private void collapse(){
        Log.d(TAG, "collapse() called with: " + "");
        mTextView.setMaxLines(3);
        mTextView.setEllipsize(TextUtils.TruncateAt.END);
        mButton.setText(R.string.expand);
        mCollapsed = true;
    }

    private void expand(){
        mTextView.setMaxLines(Integer.MAX_VALUE);
        mTextView.setEllipsize(null);
        mButton.setText(R.string.collapse);
        mCollapsed = false;
    }

    private void init(Context context) {
        mContext = context;
        mCollapsed = false;
        mTextView = new LaTeXtView(context);
        mButton = new Button(context);
        mTextView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mTextView.setBackgroundColor(bgColor);
        addView(mTextView);
    }
}
