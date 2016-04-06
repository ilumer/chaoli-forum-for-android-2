package com.geno.chaoli.forum;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.geno.chaoli.forum.meta.SignUpUtils;
import com.geno.chaoli.forum.meta.SignUpUtils.Question;

import com.alibaba.fastjson.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jianhao on 16-3-28.
 */
public class SignUpActivity extends Activity implements SignUpUtils.SubmitObserver {
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_sign_up);

        final String[] subjectTagsArr = SignUpUtils.subjectTags.keySet().toArray(new String[SignUpUtils.subjectTags.keySet().size()]);
        new AlertDialog.Builder(this).setTitle("请选择科目，综合类测试为6道题，至少需答对4道，分科测试为8道题，至少需答对6道。加油！")
                .setItems(subjectTagsArr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("click", String.valueOf(which));
                        Log.i("click", SignUpUtils.subjectTags.get(subjectTagsArr[which]));
                        SignUpUtils.getQuestionObjList(mContext, (SignUpUtils.SubmitObserver)mContext, SignUpUtils.subjectTags.get(subjectTagsArr[which]));
                    }
                })
                .setCancelable(false)
                /*.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        SignUpUtils.getQuestionObjList(mContext, (SignUpUtils.SubmitObserver)mContext, SignUpUtils.subjectTags.get("综合"));
                    }
                })*/
                .show();
    }

    public class QuestionAdapter extends BaseAdapter{
        Context mContext;
        List<Question> mQuestions;

        final int CHECK_BTN_ITEM_TYPE = 0;
        final int RADIO_BTN_ITEM_TYPE = 1;
        final int EDIT_TEXT_ITEM_TYPE = 2;

        QuestionAdapter(Context context, List<Question> questions){
            mContext = context;
            mQuestions = questions;
        }

        @Override
        public int getCount() {
            return mQuestions.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Question questionObj = (Question) getItem(position);
            final List<String> answers = questionObj.answers;
            final int[] LAYOUT_IDS = {R.layout.question_item_cb, R.layout.question_item_rb, R.layout.question_item_et};

            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(LAYOUT_IDS[getItemViewType(position)], null);
            }

            if(getItemViewType(position) == EDIT_TEXT_ITEM_TYPE){
                final EditText answer_et = (EditText)convertView.findViewById(R.id.answer_et);
                answer_et.setText(answers.size() == 0 ? "" : answers.get(0));
                answer_et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        answers.clear();
                        answers.add(s.toString());
                    }
                });
            }else {
                final int[] ids = {R.id.choice_1, R.id.choice_2, R.id.choice_3, R.id.choice_4};

                if (getItemViewType(position) == RADIO_BTN_ITEM_TYPE) {
                    RadioButton[] RadioBtnArray = {((RadioButton) convertView.findViewById(ids[0])), ((RadioButton) convertView.findViewById(ids[1])),
                            ((RadioButton) convertView.findViewById(ids[2])), ((RadioButton) convertView.findViewById(ids[3]))};
                    RadioGroup radioGroup = ((RadioGroup) convertView.findViewById(R.id.choices));

                    for (int i = 0; i < RadioBtnArray.length; i++) {
                        RadioBtnArray[i].setText(questionObj.getOptions().get(i));
                    }

                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            answers.clear();
                            for (int i = 0; i < ids.length; i++)
                                if (checkedId == ids[i]) {
                                    Log.i("equal", String.valueOf(i));
                                    answers.add(String.valueOf(i));
                                }
                            Log.i("radio", String.valueOf(answers.size()));
                        }
                    });

                    //如果放在setOnCheckedChangeListener之前会导致当view被复用时，触发onCheckedChanged事件，还持有原question引用的OnCheckedChangeListener会将不该清空的answers清空
                    radioGroup.check(answers.size() == 0 ? -1 : ids[Integer.parseInt(answers.get(0))]);
                } else {
                    CheckBox[] checkGroup = {((CheckBox) convertView.findViewById(ids[0])), ((CheckBox) convertView.findViewById(ids[1])),
                            ((CheckBox) convertView.findViewById(ids[2])), ((CheckBox) convertView.findViewById(ids[3]))};

                    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            int checkedId = buttonView.getId();

                            for (int i = 0; i < ids.length; i++)
                                if (checkedId == ids[i])
                                    if (isChecked)
                                        answers.add(String.valueOf(i));
                                    else
                                        answers.remove(String.valueOf(i));
                        }
                    };

                    for (int i = 0; i < checkGroup.length; i++) {
                        checkGroup[i].setText(questionObj.getOptions().get(i));
                        checkGroup[i].setOnCheckedChangeListener(onCheckedChangeListener);
                        checkGroup[i].setChecked(answers.contains(String.valueOf(i)));
                    }
                }
            }
            ((TextView)convertView.findViewById(R.id.content)).setText(questionObj.getQuestion());
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return mQuestions.get(position);
        }

        @Override
        public int getItemViewType(int position) {
            if(((Question) getItem(position)).getChoice().equals("false")) return EDIT_TEXT_ITEM_TYPE;
            return ((Question) getItem(position)).isMultiAnswer() ? CHECK_BTN_ITEM_TYPE : RADIO_BTN_ITEM_TYPE;
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }
    }

    @Override
    public void onGetQuestionObjList(final ArrayList<Question> questionObjList) {
        if(questionObjList != null && questionObjList.size() != 0) {
            Log.i("size", String.valueOf(questionObjList.size()));
            ListView listView = (ListView) findViewById(R.id.questions_list);
            View button = getLayoutInflater().inflate(R.layout.button_on_the_bottom_of_questions_list, null);
            listView.addFooterView(button);
            listView.setAdapter(new QuestionAdapter(this, questionObjList));

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("hi!", JSON.toJSONString(questionObjList));
                    SignUpUtils.submitAnswers(mContext, questionObjList, (SignUpUtils.SubmitObserver) mContext);
                }
            });
        }else{
            Toast toast = Toast.makeText(this, "获取题目错误，请稍后重试", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    @Override
    public void onAnswersPass(String code) {
        Log.i("注册通过", "https://chaoli.club/index.php/user/join?invite=" + code);
    }

    @Override
    public void onFailure(int statusCode) {
        if(statusCode == SignUpUtils.ANSWERS_WRONG){
            Log.i("d", "未能通过测试");
        }else{
            Log.e("error", String.valueOf(statusCode));
        }
    }
}
