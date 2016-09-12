package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.geno.chaoli.forum.model.Post;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 和OnlineImgTextView类似，只是继承了RadioButton
 * 用于答题时显示选项中的LaTeX
 * Created by jianhao on 16-9-4.
 */
public class OnlineImgRadioButton extends RadioButton {
    private Context mContext;
    private List<Post.Attachment> mAttachmentList;
    private String mText;
    private SpannableStringBuilder mSpannableStringBuilder;

    private OnCompleteListener mListener;

    public static final String SITE = "http://latex.codecogs.com/gif.latex?\\dpi{" + 440 / 2 + "}";

    //public static final Pattern PATTERN1 = Pattern.compile("(?i)(?<=\\$)(.+?)(?=\\$)");

    //public static final Pattern PATTERN2 = Pattern.compile("(?i)(?<=\\\\\\()(.+?)(?=\\\\\\))");
    private static final Pattern PATTERN1 = Pattern.compile("(?i)\\$\\$?(([^\\$]|\\n)+?)\\$?\\$");
    private static final Pattern PATTERN2 = Pattern.compile("(?i)\\\\[(\\[]((.|\\n)*?)\\\\[\\])]");
    private static final Pattern PATTERN3 = Pattern.compile("(?i)\\[tex]((.|\\n)*?)\\[/tex]");
    private static final Pattern IMG_PATTERN = Pattern.compile("(?i)\\[img](.*?)\\[/img]");
    private static final Pattern ATTACHMENT_PATTERN = Pattern.compile("(?i)\\[attachment:(.*?)]");
    //private static final Pattern PATTERN3 = Pattern.compile("(?i)\\\\begin\\{.*?\\}(.|\\n)*?\\\\end\\{.*?\\}");

    public static final String TAG = "OnlineImgTextView";

    public OnlineImgRadioButton(Context context, List<Post.Attachment> attachmentList)
    {
        super(context);
        init(context, attachmentList);
    }

    public OnlineImgRadioButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, null);
    }

    public OnlineImgRadioButton(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, null);
    }

    public void setText(String text){
        text = removeNewlineInFormula(text);
        //Log.d(TAG, "setText: text = " + text);
        text += '\n';
        //Log.d(TAG, "setText: " + text);
        mText = text;
        SpannableStringBuilder builder = SFXParser3.parse(mContext, text, mAttachmentList);
        setText(builder);

        retrieveLaTeXImg(builder);
    }

    public void setText(String text, OnCompleteListener listener) {
        setListener(listener);
        setText(text);
    }

    private void retrieveLaTeXImg(final SpannableStringBuilder builder) {
        String text = builder.toString();

        Matcher m1 = PATTERN1.matcher(text);
        Matcher m2 = PATTERN2.matcher(text);
        Matcher m3 = PATTERN3.matcher(text);
        Matcher imgMatcher = IMG_PATTERN.matcher(text);
        Matcher attachmentMatcher = ATTACHMENT_PATTERN.matcher(text);

        _retrieveLaTeXImg(builder, m1, m2, m3, imgMatcher, attachmentMatcher);
    }
    private void _retrieveLaTeXImg(final SpannableStringBuilder builder, final Matcher m1, final Matcher m2, final Matcher m3, final Matcher imgMatcher,
                                   final Matcher attachmentMatcher) {
        String formula;
        Boolean flag1 = false, flag2 = false, flag3 = false, flagImg = false, flagAttachment = false;
        if ((flagAttachment = attachmentMatcher.find()) || (flagImg = imgMatcher.find()) || (flag1 = m1.find()) || (flag2 = m2.find()) || (flag3 = m3.find())) {
            int start, end;
            if (flagAttachment) {
                start = attachmentMatcher.start();
                end = attachmentMatcher.end();
                formula = attachmentMatcher.group(1);
            } else if (flagImg) {
                start = imgMatcher.start();
                end = imgMatcher.end();
                formula = imgMatcher.group(1);
            } else if (flag3) {
                start = m3.start();
                end = m3.end();
                formula = m3.group(1);
            } else if (flag2) {
                start = m2.start();
                end = m2.end();
                formula = m2.group(1);//.replaceAll("[ \\t\\r\\n]", "");
            } else {
                start = m1.start();
                end = m1.end();
                formula = m1.group(1);//.replaceAll("[ \\t\\r\\n]", "");
            }
            //Log.d(TAG, "_retrieveLaTeXImg: " + formula);
//                if(!flagImg) formula = URLEncoder.encode(formula, "UTF-8");
                final int fStart = start, fEnd = end;
                //String url = flagImg ? formula : SITE + formula;
                String url = "";
                if (flagImg) {
                    url = formula;
                } else if (flagAttachment) {
                    for (int i = mAttachmentList.size() - 1; i >= 0; i--) {
                        Post.Attachment attachment = mAttachmentList.get(i);
                        if (attachment.getAttachmentId().equals(formula)) {
                            if (attachment.getFilename().endsWith(".jpg") || attachment.getFilename().endsWith(".png")) {
                                url = Constants.ATTACHMENT_IMAGE_URL + attachment.getAttachmentId() + attachment.getSecret();
                            }
                        }
                    }
                } else {
                    url = SITE + formula;
                }
                //Log.d(TAG, "_retrieveLaTeXImg: url = " + url);
                final Boolean finalFlagImg = flagImg;
                final Boolean finalFlagAttachment = flagAttachment;
                Glide.with(getContext()).load(url).asBitmap().into(new SimpleTarget<Bitmap>()
                {
                    @Override
                    public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation)
                    {
                        // post to avoid ConcurrentModificationException, from https://github.com/bumptech/glide/issues/375
                        post(new Runnable() {
                            @Override
                            public void run() {
                                if(finalFlagAttachment || finalFlagImg) builder.setSpan(new ImageSpan(getContext(), resource), fStart, fEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                else builder.setSpan(new CenteredImageSpan(getContext(), resource), fStart, fEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                setText(builder);
                                _retrieveLaTeXImg(builder, m1, m2, m3, imgMatcher, attachmentMatcher);
                            }
                        });
                    }
                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        e.printStackTrace();
                        _retrieveLaTeXImg(builder, m1, m2, m3, imgMatcher, attachmentMatcher);
                    }
                });
        } else {
            mSpannableStringBuilder = builder;
            if (mListener != null) {
                mListener.onComplete(builder);
            }
        }
    }

    private String removeNewlineInFormula(String str){
        Matcher m1 = PATTERN1.matcher(str);
        Matcher m2 = PATTERN2.matcher(str);
        Matcher m3 = PATTERN3.matcher(str);
        Boolean flag3 = false, flag2 = false, flag1 = false;
        // remove all spaces, codecogs returns error if formula contains spaces
        while ((flag1 = m1.find()) || (flag2 = m2.find()) || (flag3 = m3.find())) {
            String oldStr;
            if (flag3) oldStr = m3.group();
            else if (flag2) oldStr = m2.group();
            else oldStr = m1.group();
            String newStr = oldStr.replaceAll("[\\n\\r]", "");
            str = str.replace(oldStr, newStr);
        }

        //Log.d(TAG, "removeNewlineInFormula: str = " + str);
        return str;
    }


    public void setListener(OnCompleteListener listener){
        mListener = listener;
    }

    public interface OnCompleteListener {
        void onComplete(SpannableStringBuilder spannableStringBuilder);
    }

    @Override
    public String toString()
    {
        return mText;
    }

    public SpannableStringBuilder getSpannableStringBuilder(){
        return mSpannableStringBuilder;
    }

    private void init(Context context, List<Post.Attachment> attachmentList) {
        mContext = context;
        mAttachmentList = attachmentList;
    }
}
