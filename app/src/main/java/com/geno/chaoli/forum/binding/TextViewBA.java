package com.geno.chaoli.forum.binding;

import android.databinding.BindingAdapter;
import android.text.TextPaint;
import android.widget.TextView;

/**
 * Created by jianhao on 16-10-3.
 */

public class TextViewBA {
    @BindingAdapter("app:bold")
    public static void setBold(TextView textView, Boolean isBold) {
        TextPaint tp = textView.getPaint();
        tp.setFakeBoldText(isBold);
    }
}
