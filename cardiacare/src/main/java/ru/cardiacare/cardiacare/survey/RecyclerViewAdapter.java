package ru.cardiacare.cardiacare.survey;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.petrsu.cardiacare.smartcare.survey.Answer;
import com.petrsu.cardiacare.smartcare.survey.AnswerItem;
import com.petrsu.cardiacare.smartcare.survey.Question;
import com.petrsu.cardiacare.smartcare.survey.Respond;
import com.petrsu.cardiacare.smartcare.survey.Response;
import com.petrsu.cardiacare.smartcare.survey.ResponseItem;

import java.util.LinkedList;


import ru.cardiacare.cardiacare.*;
import ru.cardiacare.cardiacare.R;

/* Расстановка вопросов по карточкам */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private LinkedList<Question> Questions;
    private int[] TypesQuestions;
    private Context context;

    private LinkedList<Respond> responds = QuestionnaireActivity.feedback.getResponds();;

    public RecyclerViewAdapter(LinkedList<Question> Questions, int[] Types, Context context) {
        this.Questions = Questions;
        TypesQuestions = Types;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int Type) {
        View v;
        if ((Type == QuestionnaireActivity.Dichotomous) || (Type == QuestionnaireActivity.Singlechoice) || (Type == QuestionnaireActivity.Likertscale) || (Type == QuestionnaireActivity.Guttmanscale)) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(ru.cardiacare.cardiacare.R.layout.card_radio_buttons, viewGroup, false);
            return new RadioButtonsViewHolder(v);
        } else if (Type == QuestionnaireActivity.TextField) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_textfield, viewGroup, false);
            return new TextFieldViewHolder(v);
        } else if ((Type == QuestionnaireActivity.Bipolarquestion) || (Type == QuestionnaireActivity.Continuousscale)) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_scale, viewGroup, false);
            return new ScaleViewHolder(v);
        } else if (Type == QuestionnaireActivity.Multiplechoice) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_check_boxes, viewGroup, false);
            return new CheckBoxesViewHolder(v);
        } else {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_radio_buttons, viewGroup, false);
            return new RadioButtonsViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if ((viewHolder.getItemViewType() == QuestionnaireActivity.Dichotomous) || (viewHolder.getItemViewType() == QuestionnaireActivity.Singlechoice) || (viewHolder.getItemViewType() == QuestionnaireActivity.Likertscale) || (viewHolder.getItemViewType() == QuestionnaireActivity.Guttmanscale)) {
            Question question = Questions.get(position);
            LinkedList<Answer> answers = question.getAnswers();
            //for (int j = 0; j < answers.size(); j++) {
            //Answer answer = answers.get(j);
            Answer answer = answers.get(0);
            LinkedList<AnswerItem> answerItem = answer.getItems();
            RadioButtonsViewHolder holder = (RadioButtonsViewHolder) viewHolder;
            holder.radioButtonsQuestion.setText(question.getDescription());
            holder.questionId = question.getId();
            RadioButton[] radioButtonsAnswers = new RadioButton[answerItem.size()];
            if (answerItem.size() > 0) {
                for (int i = 0; i < answerItem.size(); i++) {
                    AnswerItem item = answerItem.get(i);
                    radioButtonsAnswers[i] = new RadioButton(context);
                    radioButtonsAnswers[i].setId(i);
                    radioButtonsAnswers[i].setText(item.getItemText());
                    for (int j = 0; j < responds.size(); j++) {
                        if (question.getId().equals(responds.get(j).getQuestionId())) {
                            for (int k = 0; k < responds.get(j).getResponses().size(); k++) {
                                for (int l = 0; l < responds.get(j).getResponses().get(k).getResponseItems().size(); l++) {
                                    if (question.getAnswers().get(0).getItems().get(i).getId().equals(responds.get(j).getResponses().get(k).getResponseItems().get(l).getLinkedItems_id())) {
                                        radioButtonsAnswers[i].setChecked(true);
                                    }
                                }
                            }
                        }
                    }
                    if (holder.radioButtonsGroup.getChildCount() < answerItem.size()) {
                        holder.radioButtonsGroup.addView(radioButtonsAnswers[i]);
                    }
                }
            }
        } else if ((viewHolder.getItemViewType() == QuestionnaireActivity.Continuousscale) || (viewHolder.getItemViewType() == QuestionnaireActivity.Bipolarquestion)) {
            Question question = Questions.get(position);
            LinkedList<Answer> answers = question.getAnswers();
            //for (int j = 0; j < answers.size(); j++) {
            //Answer answer = answers.get(j);
            Answer answer = answers.get(0);
            LinkedList<AnswerItem> answerItem = answer.getItems();
            ScaleViewHolder holder = (ScaleViewHolder) viewHolder;
            holder.questionId = question.getId();

            holder.ScaleQuestion.setText(question.getDescription());
            if (answerItem.size() > 0) {
                AnswerItem Item1 = answerItem.get(0);
                AnswerItem Item2 = answerItem.get(1);
                holder.ScaleSeekBar.setProgress(Integer.parseInt((Item1.getItemScore() + Item2.getItemScore()).replaceAll("[\\D]", "")) / 2);
                int Max = Integer.parseInt(Item2.getItemScore().replaceAll("[\\D]", ""));
                holder.ScaleSeekBar.setMax(Max);
//                int Step = 10;
//                TextView[] ScaleAnswers = new TextView[Step + 1];
//                for (int j = 0; j < Step + 1; j++) {
//                    ScaleAnswers[j] = new TextView(context);
//                    ScaleAnswers[j].setId(j);
//                    ScaleAnswers[j].setText(Integer.toString((Max / Step * j)));
//                    ScaleAnswers[j].setLayoutParams(holder.params);
//                    holder.ScaleIntervals.addView(ScaleAnswers[j]);
//                }
                for (int j = 0; j < responds.size(); j++) {
                    if (question.getId().equals(responds.get(j).getQuestionId())) {
//                    holder.ScaleValue.setText(responds.get(fbc).getResponseItems().get(0).getLinkedItems().get(0).getItemText().toString());
                        holder.ScaleValue.setText(responds.get(j).getResponses().get(0).getResponseItems().get(0).getResponseScore());
                        holder.ScaleSeekBar.setProgress(Integer.parseInt(holder.ScaleValue.getText().toString()));
                    }
                }
            }
        } else if (viewHolder.getItemViewType() == QuestionnaireActivity.Multiplechoice) {
            Question question = Questions.get(position);
            LinkedList<Answer> answers = question.getAnswers();
            //for (int j = 0; j < answers.size(); j++) {
            //Answer answer = answers.get(j);
            Answer answer = answers.get(0);
            LinkedList<AnswerItem> answerItem = answer.getItems();
            CheckBoxesViewHolder holder = (CheckBoxesViewHolder) viewHolder;
            holder.CheckBoxesQuestion.setText(question.getDescription());
            holder.id = question.getId();
            CheckBox[] CheckBoxesAnswers = new CheckBox[answerItem.size()];
            if (answerItem.size() > 0) {
                for (int i = 0; i < answerItem.size(); i++) {
                    AnswerItem Item = answerItem.get(i);
                    CheckBoxesAnswers[i] = new CheckBox(context);
                    CheckBoxesAnswers[i].setId(i);
                    CheckBoxesAnswers[i].setText(Item.getItemText());
                    final Integer questionId = question.getId();
                    for (int j = 0; j < responds.size(); j++) {
                        if (question.getId().equals(responds.get(j).getQuestionId())) {
                            for (int k = 0; k < responds.get(j).getResponses().get(0).getResponseItems().size(); k++) {
                                if (question.getAnswers().get(0).getItems().get(i).getId().equals(responds.get(j).getResponses().get(0).getResponseItems().get(k).getLinkedItems_id())) {
                                    CheckBoxesAnswers[i].setChecked(true);
                                }
                            }
                        }
                    }
                    if (holder.CheckBoxesLayout.getChildCount() < answerItem.size()) {
                        holder.CheckBoxesLayout.addView(CheckBoxesAnswers[i]);
                    }

                    CheckBoxesAnswers[i].setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                            System.out.println("Touch! Multiple " + view.getId() + " " + questionId);
                            view.isShown();
                            for (int j = 0; j < QuestionnaireHelper.questionnaire.getQuestions().size(); j++) {
                                if (QuestionnaireHelper.questionnaire.getQuestions().get(j).getId().equals(questionId)) {
                                    int flag = 0;
                                    for (int k = 0; k < QuestionnaireActivity.feedback.getResponds().size(); k++) {
                                        if (QuestionnaireActivity.feedback.getResponds().get(k).getQuestionId().equals(questionId)) {
                                            Question question = QuestionnaireHelper.questionnaire.getQuestions().get(j);
                                            LinkedList<Answer> answers = question.getAnswers();
                                            //for (int j = 0; j < answers.size(); j++) {
                                            //Answer answer = answers.get(j);
                                            Answer answer = answers.get(0);
                                            AnswerItem answerItem = answer.getItems().get(view.getId());
                                            if (QuestionnaireActivity.feedback.getResponds().get(k).getResponses().size() != 0) {
                                                int flag2 = 0;
                                                int responseItemsCount = QuestionnaireActivity.feedback.getResponds().get(k).getResponses().get(0).getResponseItems().size();
                                                for (int l = 0; l < responseItemsCount; l++) {
                                                    if (QuestionnaireActivity.feedback.getResponds().get(k).getResponses().get(0).getResponseItems().get(l).getLinkedItems_id().equals(answerItem.getId())) {
                                                        if (QuestionnaireActivity.feedback.getResponds().get(k).getResponses().get(0).getResponseItems().size() == 1) {
                                                            QuestionnaireActivity.feedback.getResponds().remove(k);
                                                            flag2++;
                                                            break;
                                                        } else {
                                                            QuestionnaireActivity.feedback.getResponds().get(k).getResponses().get(0).getResponseItems().remove(l);
                                                            flag2++;
                                                            break;
                                                        }
                                                    }
                                                }
                                                if (flag2 == 0) {
                                                    ResponseItem responseItem = new ResponseItem(answerItem.getId());
                                                    QuestionnaireActivity.feedback.getResponds().get(k).getResponses().get(0).addResponseItem(responseItem);
                                                }
                                                flag++;
                                            }
                                        }
                                    }
                                    if (flag == 0) {
                                        Question question = QuestionnaireHelper.questionnaire.getQuestions().get(j);
                                        LinkedList<Answer> answers = question.getAnswers();
                                        //for (int j = 0; j < answers.size(); j++) {
                                        //Answer answer = answers.get(j);
                                        Answer answer = answers.get(0);
                                        //Answer answer = question.getAnswer();
                                        AnswerItem answerItem = answer.getItems().get(view.getId());
                                        Respond respond = new Respond(question.getId());
                                        Response response = new Response(answer.getId());
                                        ResponseItem responseItem = new ResponseItem(answerItem.getId());
                                        response.addResponseItem(responseItem);
                                        respond.addResponse(response);
                                        QuestionnaireActivity.feedback.addRespond(respond);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        } else if (viewHolder.getItemViewType() == QuestionnaireActivity.TextField) {
            Question question = Questions.get(position);
            TextFieldViewHolder holder = (TextFieldViewHolder) viewHolder;
            holder.TextFieldQuestion.setText(question.getDescription());
            holder.questionId = question.getId();
            for (int j = 0; j < responds.size(); j++) {
                if (question.getId().equals(responds.get(j).getQuestionId())) {
//                    holder.TextFieldAnswer.setText(responds.get(fbc).getResponseItems().get(0).getLinkedItems().get(0).getItemText().toString());
                    holder.TextFieldAnswer.setText(responds.get(j).getResponses().get(0).getResponseText());
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return Questions.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TypesQuestions[position];
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class RadioButtonsViewHolder extends ViewHolder {
        TextView radioButtonsQuestion;
        RadioGroup radioButtonsGroup;
        RadioButton radioButtonsAnswer;
        Integer questionId;

        public RadioButtonsViewHolder(View v) {
            super(v);
            this.radioButtonsQuestion = (TextView) v.findViewById(R.id.RadioButtonsQuestion);
            this.radioButtonsGroup = (RadioGroup) v.findViewById(R.id.RadioButtonsAnswers);
            this.radioButtonsAnswer = (RadioButton) v.getParent();

            radioButtonsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    System.out.println("Touch! Dichotomous " + checkedId + " " + questionId);
                    for (int i = 0; i < QuestionnaireHelper.questionnaire.getQuestions().size(); i++) {
                        if (QuestionnaireHelper.questionnaire.getQuestions().get(i).getId().equals(questionId)) {
                            int flag = 0;
                            for (int j = 0; j < QuestionnaireActivity.feedback.getResponds().size(); j++) {
                                if (QuestionnaireActivity.feedback.getResponds().get(j).getQuestionId().equals(questionId)) {
                                    int responsesCount = QuestionnaireActivity.feedback.getResponds().get(j).getResponses().size();
                                    for (int k = 0; k < responsesCount; k++) {
                                        QuestionnaireActivity.feedback.getResponds().get(j).getResponses().remove(k);
                                    }
                                    Question question = QuestionnaireHelper.questionnaire.getQuestions().get(i);
                                    LinkedList<Answer> answers = question.getAnswers();
                                    //for (int j = 0; j < answers.size(); j++) {
                                    //Answer answer = answers.get(j);
                                    Answer answer = answers.get(0);
                                    //Answer answer = question.getAnswer();
                                    AnswerItem answerItem = answer.getItems().get(checkedId);
                                    Response response = new Response(answer.getId());
                                    ResponseItem responseItem = new ResponseItem(answerItem.getId());
                                    response.addResponseItem(responseItem);
                                    QuestionnaireActivity.feedback.getResponds().get(j).addResponse(response);
                                    flag++;
                                }
                            }
                            if (flag == 0) {
                                Question question = QuestionnaireHelper.questionnaire.getQuestions().get(i);
                                LinkedList<Answer> answers = question.getAnswers();
                                //for (int j = 0; j < answers.size(); j++) {
                                //Answer answer = answers.get(j);
                                Answer answer = answers.get(0);
                                //Answer answer = question.getAnswer();
                                AnswerItem answerItem = answer.getItems().get(checkedId);
                                Respond respond = new Respond(question.getId());
                                Response response = new Response(answer.getId());
                                ResponseItem responseItem = new ResponseItem(answerItem.getId());
                                response.addResponseItem(responseItem);
                                respond.addResponse(response);
                                QuestionnaireActivity.feedback.addRespond(respond);
                            }
                        }
                    }
                }
            });
        }
    }


    private class TextFieldViewHolder extends ViewHolder {
        TextView TextFieldQuestion;
        EditText TextFieldAnswer;
        Integer questionId;

        public TextFieldViewHolder(View v) {
            super(v);
            this.TextFieldQuestion = (TextView) v.findViewById(R.id.TextFieldQuestion);
            this.TextFieldAnswer = (EditText) v.findViewById(R.id.TextFieldAnswer);
//            this.TextFieldAnswer.setText("текст при создании");

            TextFieldAnswer.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    for (int i = 0; i < QuestionnaireHelper.questionnaire.getQuestions().size(); i++) {
                        if (QuestionnaireHelper.questionnaire.getQuestions().get(i).getId().equals(questionId)) {
                            int flag = 0;
                            for (int j = 0; j < QuestionnaireActivity.feedback.getResponds().size(); j++) {
                                if (QuestionnaireActivity.feedback.getResponds().get(j).getQuestionId().equals(questionId)) {
                                    int responsesCount = QuestionnaireActivity.feedback.getResponds().get(j).getResponses().size();
                                    for (int k = 0; k < responsesCount; k++) {
                                        QuestionnaireActivity.feedback.getResponds().get(j).getResponses().remove(k);
                                    }
                                    Question question = QuestionnaireHelper.questionnaire.getQuestions().get(i);
                                    LinkedList<Answer> answers = question.getAnswers();
                                    //for (int j = 0; j < answers.size(); j++) {
                                    //Answer answer = answers.get(j);
                                    Answer answer = answers.get(0);
                                    //Answer answer = question.getAnswer();
                                    Response response = new Response(answer.getId());
                                    response.setResponseText(TextFieldAnswer.getText().toString());
                                    QuestionnaireActivity.feedback.getResponds().get(j).addResponse(response);
                                    flag++;
                                }
                            }
                            if (flag == 0) {
                                Question question = QuestionnaireHelper.questionnaire.getQuestions().get(i);
                                LinkedList<Answer> answers = question.getAnswers();
                                //for (int j = 0; j < answers.size(); j++) {
                                //Answer answer = answers.get(j);
                                Answer answer = answers.get(0);
                                //Answer answer = question.getAnswer();
                                Respond respond = new Respond(question.getId());
                                Response response = new Response(answer.getId());
                                response.setResponseText(TextFieldAnswer.getText().toString());
                                respond.addResponse(response);
                                QuestionnaireActivity.feedback.addRespond(respond);
                            }
                        }
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

        }
    }

    private class CheckBoxesViewHolder extends ViewHolder {
        TextView CheckBoxesQuestion;
        LinearLayout CheckBoxesLayout;
        CheckBox CheckBoxesAnswer;
        Integer id;

        public CheckBoxesViewHolder(View v) {
            super(v);
            this.CheckBoxesQuestion = (TextView) v.findViewById(R.id.CheckBoxesQuestion);
            this.CheckBoxesLayout = (LinearLayout) v.findViewById(R.id.LinearCheckBoxes);
            this.CheckBoxesAnswer = (CheckBox) v.getParent();
        }
    }

    private class ScaleViewHolder extends ViewHolder {
        TextView ScaleQuestion;
        //        TextView ContinuousScaleAnswer;
        TextView ScaleValue;
        SeekBar ScaleSeekBar;
        LinearLayout ScaleIntervals;
        LinearLayout.LayoutParams params;
        Integer questionId;

        public ScaleViewHolder(View v) {
            super(v);
            this.ScaleQuestion = (TextView) v.findViewById(R.id.ScaleQuestion);
            this.ScaleIntervals = (LinearLayout) v.findViewById(R.id.ScaleIntervals);
            this.params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0f;
//            this.ContinuousScaleAnswer = (TextView) v.getParent();
            this.ScaleSeekBar = (SeekBar) v.findViewById(R.id.ScaleSeekBar);
            this.ScaleValue = (TextView) v.findViewById(R.id.ScaleValue);
            this.ScaleValue.setText(String.valueOf(ScaleSeekBar.getProgress()));
            this.ScaleSeekBar.setOnSeekBarChangeListener(
                    new SeekBar.OnSeekBarChangeListener() {
                        int Step = 10;

                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            progress = ((int) Math.round(progress / Step)) * Step;
                            seekBar.setProgress(progress);
                            ScaleValue.setText(progress + "");
                        }

                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        public void onStopTrackingTouch(SeekBar seekBar) {
                            System.out.println("Touch! ContinuousScale " + questionId + " " + ScaleValue.getText());
                            for (int i = 0; i < QuestionnaireHelper.questionnaire.getQuestions().size(); i++) {
                                if (QuestionnaireHelper.questionnaire.getQuestions().get(i).getId().equals(questionId)) {
                                    int flag = 0;
                                    for (int j = 0; j < QuestionnaireActivity.feedback.getResponds().size(); j++) {
                                        if (QuestionnaireActivity.feedback.getResponds().get(j).getQuestionId().equals(questionId)) {
                                            int responsesCount = QuestionnaireActivity.feedback.getResponds().get(j).getResponses().size();
                                            for (int k = 0; k < responsesCount; k++) {
                                                QuestionnaireActivity.feedback.getResponds().get(j).getResponses().remove(k);
                                            }
                                            Question question = QuestionnaireHelper.questionnaire.getQuestions().get(i);
                                            LinkedList<Answer> answers = question.getAnswers();
                                            //for (int j = 0; j < answers.size(); j++) {
                                            //Answer answer = answers.get(j);
                                            Answer answer = answers.get(0);
                                            //Answer answer = question.getAnswer();
                                            Response response = new Response(answer.getId());
                                            ResponseItem responseItem = new ResponseItem(ScaleValue.getText().toString());
                                            response.addResponseItem(responseItem);
                                            QuestionnaireActivity.feedback.getResponds().get(j).addResponse(response);
                                            flag++;
                                        }
                                    }
                                    if (flag == 0) {
                                        Question question = QuestionnaireHelper.questionnaire.getQuestions().get(i);
                                        LinkedList<Answer> answers = question.getAnswers();
                                        //for (int j = 0; j < answers.size(); j++) {
                                        //Answer answer = answers.get(j);
                                        Answer answer = answers.get(0);
                                        //Answer answer = question.getAnswer();
                                        Respond respond = new Respond(question.getId());
                                        Response response = new Response(answer.getId());
                                        ResponseItem responseItem = new ResponseItem(ScaleValue.getText().toString());
                                        response.addResponseItem(responseItem);
                                        respond.addResponse(response);
                                        QuestionnaireActivity.feedback.addRespond(respond);
                                    }
                                }
                            }
                        }
                    }
            );
        }
    }
}