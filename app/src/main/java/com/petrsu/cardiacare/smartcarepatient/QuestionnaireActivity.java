package com.petrsu.cardiacare.smartcarepatient;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.petrsu.cardiacare.smartcare.Answer;
import com.petrsu.cardiacare.smartcare.AnswerItem;
import com.petrsu.cardiacare.smartcare.Question;
import com.petrsu.cardiacare.smartcare.Response;
import com.petrsu.cardiacare.smartcare.ResponseItem;

import java.util.LinkedList;

/* Отображение опросника */

public class QuestionnaireActivity extends AppCompatActivity {

    RecyclerView QuestionnaireRecyclerView;
    RecyclerView.Adapter QuestionnaireAdapter;
    RecyclerView.LayoutManager QuestionnaireLayoutManager;
    public Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        QuestionnaireRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        QuestionnaireLayoutManager= new LinearLayoutManager(getApplicationContext());
        QuestionnaireRecyclerView.setLayoutManager(QuestionnaireLayoutManager);

        LinkedList<Question> questionnaire = MainActivity.questionnaire.getQuestions();
        int[] Types = new int[questionnaire.size()];

        for (int i = 0; i < questionnaire.size(); i++) {
            Question question = questionnaire.get(i);
            Answer answer = question.getAnswer();
            switch(answer.getType()) {
                case "Text":
                    Types[i] = RecyclerViewAdapter.TextField;
                    break;
                case "MultipleChoise":
                    Types[i] = RecyclerViewAdapter.Multiplechoice;
                    break;
                case "SingleChoise":
                    Types[i] = RecyclerViewAdapter.Singlechoice;
                    break;
                case "BipolarQuestion":
                    Types[i] = RecyclerViewAdapter.Bipolarquestion;
                    break;
                case "Dichotomous":
                    Types[i] = RecyclerViewAdapter.Dichotomous;
                    break;
                case "GuttmanScale":
                    Types[i] = RecyclerViewAdapter.Guttmanscale;
                    break;
                case "LikertScale":
                    Types[i] = RecyclerViewAdapter.Likertscale;
                    break;
                case "ContinuousScale":
                    Types[i] = RecyclerViewAdapter.Continuousscale;
                    break;
                default:
                    Types[i] = RecyclerViewAdapter.DefaultValue;
            }
        }

        QuestionnaireAdapter = new RecyclerViewAdapter(MainActivity.questionnaire.getQuestions(), Types, context);
        QuestionnaireRecyclerView.setAdapter(QuestionnaireAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        LinkedList<Question> questionnnaire = MainActivity.questionnaire.getQuestions();

        for (int i = 0; i < QuestionnaireRecyclerView.getChildCount(); i++) {
            Question question = questionnnaire.get(i);
            Answer answer = question.getAnswer();
            Response resp = new Response(questionnnaire.get(i).getUri(), questionnnaire.get(i).getUri());

            switch(answer.getType()) {
                case "Text":
                    ResponseItem TextAnswer = new ResponseItem(answer.getUri(), "textItem", "fileUri");
                    AnswerItem AnswerItemForTextField = new AnswerItem(answer.getItems().get(0));
                    AnswerItem AnswerText = new AnswerItem(AnswerItemForTextField.getUri(), AnswerItemForTextField.getItemScore(), ((EditText)QuestionnaireRecyclerView.getChildAt(i).findViewById(R.id.editText)).getText().toString());
                    TextAnswer.addLinkedAnswerItem(AnswerText);
                    resp.addResponseItem(TextAnswer);
                    MainActivity.feedback.addResponse(resp);
                    break;
                case "MultipleChoise":
                    ResponseItem MultipleChoiseAnswer = new ResponseItem(answer.getUri(), "textItem", "fileUri");
                    for(int j = 0; j < answer.getItems().size(); j++) {
                        AnswerItem AnswerItemForMultipleChoise = new AnswerItem(answer.getItems().get(j));
                        if(( (CheckBox) ((LinearLayout)QuestionnaireRecyclerView.getChildAt(i).findViewById(R.id.LinearMultiple)).getChildAt(j)).isChecked()) {
                            AnswerItem AnswerMultipleChoise = new AnswerItem(AnswerItemForMultipleChoise.getUri(), AnswerItemForMultipleChoise.getItemScore(), AnswerItemForMultipleChoise.getItemText()/**/);
                            MultipleChoiseAnswer.addLinkedAnswerItem(AnswerMultipleChoise);
                            resp.addResponseItem(MultipleChoiseAnswer);
                        }
                    }
                    MainActivity.feedback.addResponse(resp);
                    break;
                case "SingleChoise":
                    break;
                case "BipolarQuestion":
                    break;
                case "Dichotomous":
                    break;
                case "GuttmanScale":
                    break;
                case "LikertScale":
                    break;
                case "ContinuousScale":
                    break;
                default:
            }
        }
    }
}