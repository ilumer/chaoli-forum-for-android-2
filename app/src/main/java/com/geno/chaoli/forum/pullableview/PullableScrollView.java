package com.geno.chaoli.forum.pullableview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class PullableScrollView extends ScrollView implements Pullable
{

	public PullableScrollView(Context context)
	{
		super(context);
	}

	public PullableScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PullableScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
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
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), ((View)getParent().getParent().getParent()).getHeight());
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		mScrollListener.onScroll(l, t, oldl, oldt);
	}

	public interface ScrollListener{
		void onScroll(int l, int t, int oldl, int oldt);
	}

	@Override
	public boolean canPullDown()
	{
		if (getScrollY() == 0)
			return true;
		else
			return false;
	}

	@Override
	public boolean canPullUp()
	{
		if (getScrollY() >= (getChildAt(0).getHeight() - ((View)getParent().getParent().getParent()).getHeight()) && ((LinearLayout)getChildAt(0)).getChildCount() >= 10)
		//if (getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight()))
			return true;
		else
			return false;
	}

}
