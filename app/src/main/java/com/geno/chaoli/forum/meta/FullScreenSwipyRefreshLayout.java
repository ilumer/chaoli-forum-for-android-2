package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;

/**
 * Created by jianhao on 16-5-16.
 */
public class FullScreenSwipyRefreshLayout extends SwipyRefreshLayout {
    public FullScreenSwipyRefreshLayout(Context context){
        super(context);
    }
    public FullScreenSwipyRefreshLayout(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for(int i = 0; i < count; i++){
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), ((View)getParent().getParent()).getHeight());
    }
}
