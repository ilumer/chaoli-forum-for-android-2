package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.geno.chaoli.forum.model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jianhao on 16-10-22.
 */

public class OnlineImgImpl {
    public List<Post.Attachment> mAttachmentList;
    public String mText;
    private List<Formula> mFormulaList;

    private OnCompleteListener mListener;

    private IOnlineImgView mView;

    private int maxWidthPixels;	//图片的最大宽度

    public static final String SITE = "http://latex.codecogs.com/gif.latex?\\dpi{220}";

    private static final Pattern PATTERN1 = Pattern.compile("(?i)\\$\\$?((.|\\n)+?)\\$\\$?");
    private static final Pattern PATTERN2 = Pattern.compile("(?i)\\\\[(\\[]((.|\\n)*?)\\\\[\\])]");
    private static final Pattern PATTERN3 = Pattern.compile("(?i)\\[tex]((.|\\n)*?)\\[/tex]");
    private static final Pattern IMG_PATTERN = Pattern.compile("(?i)\\[img](.*?)\\[/img]");
    private static final Pattern ATTACHMENT_PATTERN = Pattern.compile("(?i)\\[attachment:(.*?)]");
    private static final Pattern PATTERN4 = Pattern.compile("(?i)\\\\begin\\{.*?\\}(.|\\n)*?\\\\end\\{.*?\\}");
    private static final Pattern PATTERN5 = Pattern.compile("(?i)\\$\\$(.+?)\\$\\$");

    private static final String TAG = "OnlineImgImpl";

    public OnlineImgImpl(IOnlineImgView view) {
        maxWidthPixels = (int) (Resources.getSystem().getDisplayMetrics().widthPixels * 0.9);
        mView = view;
    }

    public void setText(String text){
        text = removeNewlineInFormula(text);
        //Log.d(TAG, "setText: text = " + text);
        text += '\n';
        //Log.d(TAG, "setText: " + text);
        mText = text;
        SpannableStringBuilder builder = SFXParser3.parse(((View) mView).getContext(), text, mAttachmentList);
        //SpannableStringBuilder builder = new SpannableStringBuilder(text);
        mView.setText(builder);

        retrieveOnlineImg(builder);
    }

    public void setView(IOnlineImgView view) {
        mView = view;
    }
    /**
     * 此操作是异步的，注意
     * @param builder 包含公式的文本，以SpannableStringBuilder身份传入
     */
    private void retrieveOnlineImg(final SpannableStringBuilder builder) {
        String text = builder.toString();

        mFormulaList = getAllFormulas(text);

        retrieveFormulaOnlineImg(builder, 0);
    }

    /**
     * 获取所有起始位置和终止位置不相交的公式
     * @param string 包含公式的字符串
     * @return 公式List
     */
    private List<Formula> getAllFormulas(String string) {
        Matcher m1 = PATTERN1.matcher(string);
        Matcher m2 = PATTERN2.matcher(string);
        Matcher m3 = PATTERN3.matcher(string);
        Matcher m4 = PATTERN4.matcher(string);
        Matcher m5 = PATTERN5.matcher(string);
        Matcher imgMatcher = IMG_PATTERN.matcher(string);
        Matcher attachmentMatcher = ATTACHMENT_PATTERN.matcher(string);

        List<Formula> formulaList = new ArrayList<>();
        String content;
        int type;

        // TODO: 16-10-22 replace it with a loop
        Boolean flag1 = false, flag2 = false, flag3 = false, flag4 = false, flag5 = false, flagImg = false, flagAttachment = false;
        while ((flagAttachment = attachmentMatcher.find()) || (flagImg = imgMatcher.find()) || (flag1 = m1.find()) || (flag2 = m2.find()) || (flag3 = m3.find())
                || (flag4 = m4.find()) || (flag5 = m5.find())) {
            int start, end;
            if (flagAttachment) {
                start = attachmentMatcher.start();
                end = attachmentMatcher.end();
                content = attachmentMatcher.group(1);
                type = Formula.TYPE_ATT;
            } else if (flagImg) {
                start = imgMatcher.start();
                end = imgMatcher.end();
                content = imgMatcher.group(1);
                type = Formula.TYPE_IMG;
            } else if (flag5) {
                start = m5.start();
                end = m5.end();
                content = m5.group(1);
                type = Formula.TYPE_5;
            } else if (flag4) {
                start = m4.start();
                end = m4.end();
                content = m4.group(0);
                type = Formula.TYPE_4;
            } else if (flag3) {
                start = m3.start();
                end = m3.end();
                content = m3.group(1);
                type = Formula.TYPE_3;
            } else if (flag2) {
                start = m2.start();
                end = m2.end();
                content = m2.group(1);//.replaceAll("[ \\t\\r\\n]", "");
                type = Formula.TYPE_2;
            } else {
                start = m1.start();
                end = m1.end();
                content = m1.group(1);
                type = Formula.TYPE_1;
            }
            String url = "";
            if (flagImg) {
                url = content;
            } else if (flagAttachment) {
                for (int i = mAttachmentList.size() - 1; i >= 0; i--) {
                    Post.Attachment attachment = mAttachmentList.get(i);
                    if (attachment.getAttachmentId().equals(content)) {
                        if (attachment.getFilename().endsWith(".jpg") || attachment.getFilename().endsWith(".png")) {
                            url = Constants.ATTACHMENT_IMAGE_URL + attachment.getAttachmentId() + attachment.getSecret();
                        }
                    }
                }
            } else {
                url = SITE + content;
            }
            formulaList.add(new Formula(start, end, content, url, type));
        }
        removeOverlappingFormula(formulaList);
        return formulaList;
    }

    /**
     * 获取特定的公式渲染出的图片，结束时会调用回调函数（假如存在listener的话）
     * @param builder 包含公式的builder
     * @param i 公式在mFormulaList中的下标
     */
    private void retrieveFormulaOnlineImg(final SpannableStringBuilder builder, final int i) {
        if (i >= mFormulaList.size()) {
            if (mListener != null) {
                mListener.onComplete(builder);
            }
            return;
        }
        Formula formula = mFormulaList.get(i);
        Log.d(TAG, "retrieveFormulaOnlineImg: " + formula.url);
        final int finalType = formula.type;
        final int finalStart = formula.start;
        final int finalEnd = formula.end;



		/*new MyOkHttp.MyOkHttpClient().get(formula.url)
				.enqueue(new MyOkHttp.Callback1() {
					@Override
					public void onFailure(Call call, IOException e) {
						e.printStackTrace();
						retrieveFormulaOnlineImg(builder, i + 1);
					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						String responseStr = response.body().string();
						Bitmap resource = BitmapFactory.decodeByteArray(responseStr.getBytes(), 0, responseStr.getBytes().length);
						response.body().close();
						if(finalType == Formula.TYPE_ATT || finalType == Formula.TYPE_IMG) builder.setSpan(new ImageSpan(getContext(), resource), finalStart, finalEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
						else builder.setSpan(new CenteredImageSpan(getContext(), resource), finalStart, finalEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
						new Handler(ChaoliApplication.getAppContext().getMainLooper()).post(new Runnable() {
							@Override
							public void run() {
								setText(builder);
							}
						});
						retrieveFormulaOnlineImg(builder, i + 1);
					}
				});*/
        Glide.with(((View)mView).getContext()).load(formula.url).asBitmap().into(new SimpleTarget<Bitmap>()
        {
            @Override
            public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation)
            {
                // post to avoid ConcurrentModificationException, from https://github.com/bumptech/glide/issues/375
                ((View)mView).post(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap newImage;
                        if (resource.getWidth() > maxWidthPixels) {
                            int newHeight = resource.getHeight() * maxWidthPixels / resource.getWidth();
                            newImage = Bitmap.createScaledBitmap(resource, maxWidthPixels, newHeight, true);
                        } else {
                            newImage = resource;
                        }
                        if(finalType == Formula.TYPE_ATT || finalType == Formula.TYPE_IMG) builder.setSpan(new ImageSpan(((View)mView).getContext(), newImage), finalStart, finalEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        else builder.setSpan(new CenteredImageSpan(((View)mView).getContext(), resource), finalStart, finalEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        mView.setText(builder);
                        retrieveFormulaOnlineImg(builder, i + 1);
                    }
                });
            }
            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                e.printStackTrace();
                retrieveFormulaOnlineImg(builder, i + 1);
            }
        });
    }

    /**
     * 一个常用的算法-w- 去掉相交的区间
     * @param formulaList 每个LaTeX公式的起始下标和终止下标组成的List
     */
    private void removeOverlappingFormula(List<Formula> formulaList) {
        Collections.sort(formulaList, new Comparator<Formula>() {
            @Override
            public int compare(Formula p1, Formula p2) {
                return p1.start - p2.start;
            }
        });
        int size = formulaList.size();
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size;) {
                if (formulaList.get(j).start < formulaList.get(i).end) {
                    formulaList.remove(j);
                    size--;
                } else j++;
            }
        }
    }

    private String removeNewlineInFormula(String str){
        Matcher m1 = PATTERN1.matcher(str);
        Matcher m2 = PATTERN2.matcher(str);
        Matcher m3 = PATTERN3.matcher(str);
        Matcher m4 = PATTERN4.matcher(str);
        Matcher m5 = PATTERN5.matcher(str);
        Boolean flag5 = false, flag4 = false, flag3 = false, flag2 = false, flag1 = false;
        while ((flag1 = m1.find()) || (flag2 = m2.find()) || (flag3 = m3.find()) || (flag4 = m4.find()) || (flag5 = m5.find())) {
            String oldStr;
            if (flag5) oldStr = m5.group();
            else if (flag4) oldStr = m4.group();
            else if (flag3) oldStr = m3.group();
            else if (flag2) oldStr = m2.group();
            else oldStr = m1.group();
            String newStr = oldStr.replaceAll("[\\n\\r]", "");
            str = str.replace(oldStr, newStr);
        }

        return str;
    }


    public void setListener(OnCompleteListener listener){
        mListener = listener;
    }

    public interface OnCompleteListener {
        void onComplete(SpannableStringBuilder spannableStringBuilder);
    }

    private static class Formula {
        static final int TYPE_1 = 1;
        static final int TYPE_2 = 2;
        static final int TYPE_3 = 3;
        static final int TYPE_4 = 4;
        static final int TYPE_5 = 5;
        static final int TYPE_IMG = 4;
        static final int TYPE_ATT = 5;
        int start, end;
        String content, url;
        int type;

        Formula(int start, int end, String content, String url, int type) {
            this.start = start;
            this.end = end;
            this.content = content;
            this.url = url;
            this.type = type;
        }
    }
}
