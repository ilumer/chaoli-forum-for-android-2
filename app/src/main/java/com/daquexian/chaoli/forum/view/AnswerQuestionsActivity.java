package com.daquexian.chaoli.forum.view;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.Toast;

import com.daquexian.chaoli.forum.R;
import com.daquexian.chaoli.forum.databinding.ActivityAnswerQuestionsBinding;
import com.daquexian.chaoli.forum.utils.SignUpUtils;

import com.daquexian.chaoli.forum.viewmodel.AnswerQuestionsVM;
import com.daquexian.chaoli.forum.viewmodel.BaseViewModel;

/**
 * 注册时显示问题、回答问题的Activity
 * Created by jianhao on 16-3-28.
 */
public class AnswerQuestionsActivity extends BaseActivity {
    Context mContext;
    Boolean isFirst = true;

    AnswerQuestionsVM viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        init();

        configToolbar(R.string.answer_quesiton);
    }

    private void init(){
        setViewModel(new AnswerQuestionsVM());
        ActivityAnswerQuestionsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_answer_questions);
        binding.setViewModel(this.viewModel);

        binding.questionRv.setLayoutManager(new LinearLayoutManager(mContext));
        binding.questionRv.setNestedScrollingEnabled(false);

        final String[] subjectTagsArr = SignUpUtils.subjectTags.keySet().toArray(new String[SignUpUtils.subjectTags.keySet().size()]);
        new AlertDialog.Builder(this).setTitle("请选择科目，综合类测试为6道题，至少需答对4道，分科测试为8道题，至少需答对6道。加油！")
                .setItems(subjectTagsArr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //SignUpUtils.getQuestionObjList((SignUpUtils.SubmitObserver) mContext, SignUpUtils.subjectTags.get(subjectTagsArr[which]));
                        String subject = SignUpUtils.subjectTags.get(subjectTagsArr[which]);
                        viewModel.getQuestions(subject);
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                })
                .show();

        viewModel.pass.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                Toast.makeText(getApplicationContext(), "恭喜,答题通过", Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString("inviteCode", viewModel.code);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(AnswerQuestionsActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        viewModel.fail.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if(Integer.valueOf(viewModel.code) == SignUpUtils.ANSWERS_WRONG){
                    Dialog dialog = new AlertDialog.Builder(mContext).setMessage(R.string.you_dont_answer_enough_quesitions_correctly)
                            .setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    isFirst = false;
                                    init();
                                }
                            }).setNegativeButton(R.string.dont_try_again, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((Activity)mContext).finish();
                                }
                            }).create();
                    dialog.show();
                }else{
                    Log.e("error", viewModel.code);
                }
            }
        });
    }

    /*public class QuestionAdapter extends BaseAdapter{
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
            //ViewDataBinding
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
                    OnlineImgRadioButton[] RadioBtnArray = {((OnlineImgRadioButton) convertView.findViewById(ids[0])), ((OnlineImgRadioButton) convertView.findViewById(ids[1])),
                            ((OnlineImgRadioButton) convertView.findViewById(ids[2])), ((OnlineImgRadioButton) convertView.findViewById(ids[3]))};
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
                    OnlineImgCheckBox[] checkGroup = {((OnlineImgCheckBox) convertView.findViewById(ids[0])), ((OnlineImgCheckBox) convertView.findViewById(ids[1])),
                            ((OnlineImgCheckBox) convertView.findViewById(ids[2])), ((OnlineImgCheckBox) convertView.findViewById(ids[3]))};

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
            ((OnlineImgTextView)convertView.findViewById(R.id.content)).setText(questionObj.getQuestion());
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
    }*/

    /*@Override
    public void onGetQuestionObjList(final ArrayList<BusinessQuestion> questionObjList) {
        if(questionObjList != null && questionObjList.size() != 0) {
            ListView listView = (ListView) findViewById(R.id.questions_list);
            View button = getLayoutInflater().inflate(R.layout.button_on_the_bottom_of_questions_list, null);
            if(isFirst)
                listView.addFooterView(button);
            listView.setAdapter(new QuestionAdapter(this, questionObjList));

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
    }

    @Override
    public void onFailure(int statusCode) {
    }*/

    @Override
    public void setViewModel(BaseViewModel viewModel) {
        this.viewModel = (AnswerQuestionsVM) viewModel;
    }
}
