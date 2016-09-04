package com.geno.chaoli.forum.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jianhao on 16-9-3.
 * 注册时的问题
 */
public class Question {
    public class id{
        public String $id;
    }
    public id _id;

    public String getQuestion() {
        return question;
    }

    public Boolean isMultiAnswer(){
        return Boolean.valueOf(multi_answer);
    }

    public List<String> getOptions() {
        return options;
    }

    public String getChoice() {
        return choice;
    }

    public String question, choice, multi_answer;

    public List<String> options = new ArrayList<>();
    public List<String> answers = new ArrayList<>();
}
