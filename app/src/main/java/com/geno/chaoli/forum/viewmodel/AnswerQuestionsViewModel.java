package com.geno.chaoli.forum.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;

import com.geno.chaoli.forum.binding.QuestionLayoutSelector;
import com.geno.chaoli.forum.model.BusinessQuestion;
import com.geno.chaoli.forum.model.Question;
import com.geno.chaoli.forum.utils.SignUpUtils;
import com.geno.chaoli.forum.view.AnswerQuestionsActivity;
import com.geno.chaoli.forum.view.IView;

import java.util.ArrayList;

/**
 * Created by jianhao on 16-9-21.
 */

public class AnswerQuestionsViewModel extends BaseViewModel {
    public ObservableArrayList<BusinessQuestion> questions = new ObservableArrayList<>();
    public QuestionLayoutSelector selector = new QuestionLayoutSelector();
    public ObservableBoolean pass = new ObservableBoolean(false);
    public String code;     // inviting code for passing or status code for failing
    public ObservableBoolean fail = new ObservableBoolean(false);

    public void getQuestions(String subject) {
        SignUpUtils.getQuestionObjList(new SignUpUtils.GetQuestionObserver() {
            @Override
            public void onGetQuestionObjList(ArrayList<BusinessQuestion> questionList) {
                questions.clear();
                questions.addAll(questionList);
            }
        }, subject);
    }

    public void submit() {
        SignUpUtils.submitAnswers(questions, new SignUpUtils.SubmitObserver() {
            @Override
            public void onAnswersPass(String code) {
                AnswerQuestionsViewModel.this.code = code;
                pass.notifyChange();
            }

            @Override
            public void onFailure(int statusCode) {
                AnswerQuestionsViewModel.this.code = String.valueOf(statusCode);
                fail.notifyChange();
            }
        });
    }
}
