package com.geno.chaoli.forum;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by jianhao on 16-4-16.
 */

@Deprecated
public class FullScreenScrollView extends ScrollView {
    public FullScreenScrollView(Context context){
        super(context);
    }
    public FullScreenScrollView(Context context, AttributeSet set){
        super(context, set);
    }
    public FullScreenScrollView(Context context, AttributeSet set, int defStyle){
        super(context, set, defStyle);
    }

    ScrollListener mScrollListener;

    public void setScrollListener(ScrollListener scrollListener){
        mScrollListener = scrollListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for(int i = 0; i < count; i++){
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), ((View)getParent().getParent()).getHeight());
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mScrollListener.onScroll(l, t, oldl, oldt);
    }

    public interface ScrollListener{
        void onScroll(int l, int t, int oldl, int oldt);
    }
}
