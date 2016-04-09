package com.geno.chaoli.forum.meta;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.alibaba.fastjson.*;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jianhao on 16-3-27.
 */
public class SignUpUtils {
    public static final String GET_QUESTION_URL = "https://chaoli.club/reg-exam/get-q.php?tags=";
    public static final String CONFIRM_ANSWER_URL = "https://chaoli.club/reg-exam/confirm.php";
    public static final Map<String, String> subjectTags = new HashMap<String, String>(){{
        put("数学", "math");
        put("生物", "bio");
        put("化学", "chem");
        put("物理", "phys");
        put("综合", "");
    }};

    public static int ANSWERS_WRONG = -1;

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void getQuestionObjList(Context context, final SubmitObserver observer, String subject){
        String url = GET_QUESTION_URL + subject;
        client.get(context, url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                ArrayList<Question> questionList = "".equals(response) ? null : (ArrayList<Question>) JSON.parseArray(response, Question.class);
                observer.onGetQuestionObjList(questionList);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("error", "link");
                observer.onGetQuestionObjList(null);
            }
        });
        //String jsonStr = "{\"_id\":{\"$id\":\"55d7f15669cb38bb2e8b4574\"},\"question\":\"“如无必要，勿增实体”是（）剃刀原理的内容。\",\"choice\":true,\"options\":[\"飞利浦\",\"草薙\",\"无毁之湖光\",\"奥卡姆\"],\"multi_answer\":false}";
        //String jsonStr = "{\"$id\":\"55d7f15669cb38bb2e8b4574\",\"question\":\"“如无必要，勿增实体”是（）剃刀原理的内容。\",\"choice\":true,\"options\":[\"飞利浦\",\"草薙\",\"无毁之湖光\",\"奥卡姆\"],\"multi_answer\":false}";
        //String jsonStr = "{\"id\":\"a\", \"question\":\"b\", \"choice\":\"c\", \"options\":[\"d\"], \"multi_answer\":\"e\" }";
        //String jsonStr = "{\"hi\":\"sdf\"}";
        //ArrayList<Question> questionList = (ArrayList<Question>) JSONArray.parseArray(jsonStr, Question.class);
    }

    public static void submitAnswers(Context context, List<Question> questionList, final SubmitObserver observer){
        String str = JSON.toJSONString(questionList);
        RequestParams params = new RequestParams();
        params.put("questions", str);

        for (Question i: questionList) {
            if("true".equals(i.getChoice()))
                for (String answer : i.answers)
                    params.put(i._id.$id + "_opt[]", answer);
            else if("false".equals(i.getChoice()))
                params.put(i._id.$id + "_ans", i.answers.get(0));
        }

        params.put("simplified", "1");

        client.post(context, CONFIRM_ANSWER_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody).trim();
                if ("failed".equals(response)) {
                    observer.onFailure(ANSWERS_WRONG);
                } else {
                    observer.onAnswersPass(response);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                observer.onFailure(statusCode);
            }
        });
    }

    /* 要用静态类，否则fastjson无法解析 */
    public static class Question{

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

    public interface SubmitObserver{
        void onGetQuestionObjList(ArrayList<Question> questionList);
        void onAnswersPass(String code);
        void onFailure(int statusCode);
    }
}
