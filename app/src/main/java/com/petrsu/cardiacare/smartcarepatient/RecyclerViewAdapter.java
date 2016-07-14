package com.petrsu.cardiacare.smartcarepatient;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.petrsu.cardiacare.smartcare.Answer;
import com.petrsu.cardiacare.smartcare.AnswerItem;
import com.petrsu.cardiacare.smartcare.Question;

import java.util.LinkedList;


import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.petrsu.cardiacare.smartcare.*;

import java.text.NumberFormat;
import java.util.LinkedList;

import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;

import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.os.Bundle;

import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * Kristina Shevtsova
 * Kirill Orloff
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    LinkedList <Question> Questions;
    int[] TypesQuestions;
    Context context;

    public static final int TextField = 0;//newtrue
    public static final int Multiplechoice = 1;//newtrue
    public static final int Singlechoice = 2;//newtrue
    public static final int Bipolarquestion = 3;//newtrue
    public static final int Guttmanscale = 4;//newtrue
    public static final int Likertscale = 5;//newtrue
    public static final int Continuousscale = 6;//newtrue
    public static final int Dichotomous = 7;//mb
    public static final int DefaultValue = 8;

    // ответник
    LinkedList<Response> feedback = MainActivity.feedback.getResponses();

    public RecyclerViewAdapter(LinkedList <Question> Questions, int[] Types, Context context) {
        this.Questions = Questions;
        TypesQuestions = Types;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int Type) {
        View v;
        if (Type == Dichotomous) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dichotomous_card, viewGroup, false);
            return new DichotomousViewHolder(v);
        } else if (Type == Singlechoice){
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.singlechoice_card, viewGroup, false);
            return new SingleChoiceViewHolder(v);
        } else if (Type == TextField){
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.textfield_card, viewGroup, false);
            return new TextFieldViewHolder(v);
        } else if (Type == Bipolarquestion){
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bipolarquestion_card, viewGroup, false);
            return new BipolarQuestionViewHolder(v);
        } else if (Type == Multiplechoice){
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.multiplechoice_card, viewGroup, false);
            return new MultipleChoiceViewHolder(v);
        } else if (Type == Likertscale){
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.likertscale_card, viewGroup, false);
            return new LikertScaleViewHolder(v);
        } else if (Type == Guttmanscale) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.guttmanscale_card, viewGroup, false);
            return new GuttmanScaleViewHolder(v);
        } else if (Type == Continuousscale) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.continuousscale_card, viewGroup, false);
            return new ContinuousScaleViewHolder(v);
        } else {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.singlechoice_card, viewGroup, false);
            return new SingleChoiceViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (viewHolder.getItemViewType() == Dichotomous) {
            Question question = Questions.get(position);
            Answer answer = question.getAnswer();
            LinkedList<AnswerItem> answeritem = answer.getItems();
            DichotomousViewHolder holder = (DichotomousViewHolder) viewHolder;
            holder.DichotomousQuestion.setText(question.getDescription());
            if (answeritem.size() > 0) {
                AnswerItem Item = answeritem.get(0);
                holder.DichotomousAnswer1.setText(Item.getItemText());
                Item = answeritem.get(1);
                holder.DichotomousAnswer2.setText(Item.getItemText());
            }
            ///////////////////////////////////////////////////////////////////////////////////////////////////
            /*
            Question question = Questions.get(position);
            Answer answer = question.getAnswer();
            LinkedList<AnswerItem> answeritem = answer.getItems();
            DichotomousViewHolder holder = (DichotomousViewHolder) viewHolder;
            holder.DichotomousQuestion.setText(question.getDescription());
            holder.uri = question.getUri();
            RadioButton[] DichotomousAnswers = new RadioButton[answeritem.size()];
            if (answeritem.size() > 0) {
                for (int j = 0; j < answeritem.size(); j++) {
                    AnswerItem Item = answeritem.get(j);
                    DichotomousAnswers[j] = new RadioButton(context);
                    DichotomousAnswers[j].setId(j);
                    DichotomousAnswers[j].setText(Item.getItemText());
                    //тут отображение ответов
                    for(int fbc = 0; fbc < feedback.size(); fbc++){
                        if(question.getUri() == feedback.get(fbc).getUri()) {
                            for (int aic = 0; aic < feedback.get(fbc).getResponseItems().get(0).getLinkedItems().size(); aic++) {
                                if(question.getAnswer().getItems().get(j).getUri() == feedback.get(fbc).getResponseItems().get(0).getLinkedItems().get(aic).getUri()){
                                    //
                                    DichotomousAnswers[j].setChecked(true);///
                                }
                            }
                        }
                    }
                    holder.DichotomousGroup.addView(DichotomousAnswers[j]);
                }
            }
            */
            ///////////////////////////////////////////////////////////////////////////////////////////////////
        } else if (viewHolder.getItemViewType() == Bipolarquestion) {
            Question question = Questions.get(position);
            Answer answer = question.getAnswer();
            LinkedList<AnswerItem> answeritem = answer.getItems();
            BipolarQuestionViewHolder holder = (BipolarQuestionViewHolder) viewHolder;
            holder.uri = question.getUri();

            //видимо тут отображать ответ буду
            for(int fbc = 0; fbc < feedback.size(); fbc++){
                if(question.getUri().equals(feedback.get(fbc).getUri())) {
                    holder.BipolarQuestionValue.setText(feedback.get(fbc).getResponseItems().get(0).getLinkedItems().get(0).getItemText().toString());
                    holder.BipolarQuestionSeekBar.setProgress(Integer.parseInt(holder.BipolarQuestionValue.getText().toString()));
                }
            }

            holder.BipolarQuestionQuestion.setText(question.getDescription());
            if (answeritem.size() > 0) {
                AnswerItem Item = answeritem.get(0);
                holder.BipolarQuestionSeekBar.setProgress(Integer.parseInt(Item.getItemText().replaceAll("[\\D]", "")));
                Item = answeritem.get(1);
                holder.BipolarQuestionSeekBar.setMax(Integer.parseInt(Item.getItemText().replaceAll("[\\D]", "")));
            }
        } else if (viewHolder.getItemViewType() == Multiplechoice) {
            Question question = Questions.get(position);
            Answer answer = question.getAnswer();
            LinkedList<AnswerItem> answeritem = answer.getItems();
            MultipleChoiceViewHolder holder = (MultipleChoiceViewHolder) viewHolder;
            holder.MultipleChoiceQuestion.setText(question.getDescription());
            holder.uri = question.getUri();
            CheckBox[] MultipleChoiceAnswers = new CheckBox[answeritem.size()];
            if (answeritem.size() > 0) {
                for (int j = 0; j < answeritem.size(); j++) {
                    AnswerItem Item = answeritem.get(j);
                    MultipleChoiceAnswers[j] = new CheckBox(context);
                    MultipleChoiceAnswers[j].setId(j);
                    MultipleChoiceAnswers[j].setText(Item.getItemText());

                    final String uri = question.getUri();

                    MultipleChoiceAnswers[j].setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                            System.out.println("Touch! Multiple " + view.getId() + " " + uri);
                            if (isChecked) {
                                for (int i = 0; i < MainActivity.questionnaire.getQuestions().size(); i++) {
                                    if (MainActivity.questionnaire.getQuestions().get(i).getUri().equals(uri)) {
                                        int flag = 0;
                                        for (int j = 0; j < MainActivity.feedback.getResponses().size(); j++) {
                                            if (MainActivity.feedback.getResponses().get(j).getUri().equals(uri)) {
                                                //MainActivity.feedback.getResponses().get(j).getResponseItems().clear();//?
                                                //вопрос
                                                Question questionMultipleChoice = MainActivity.questionnaire.getQuestions().get(i);
                                                //тип ответов
                                                Answer answerMultipleChoice = questionMultipleChoice.getAnswer();
                                                //ответ выбранный
                                                AnswerItem answeritemMultipleChoice = answerMultipleChoice.getItems().get(view.getId());
                                                //по Response Item пройтись и найти нужный, если нет то создать
                                                // другое если есть ResponseItem, то по нему смотрим сколько AnswerItem'ов если есть нужный, то ничего иначе добавляем
                                                ResponseItem itemMultipleChoice;
                                                if(MainActivity.feedback.getResponses().get(j).getResponseItems().size() != 0) {
                                                    itemMultipleChoice = MainActivity.feedback.getResponses().get(j).getResponseItems().get(0);
                                                    for(int z = 0; z < itemMultipleChoice.getLinkedItems().size(); z++){
                                                        if(!itemMultipleChoice.getLinkedItems().get(z).getItemText().equals(view.getText())){
                                                            itemMultipleChoice.addLinkedAnswerItem(answeritemMultipleChoice);
                                                            MainActivity.feedback.getResponses().get(j).getResponseItems().get(0).addLinkedAnswerItem(answeritemMultipleChoice);
                                                            System.out.println("Touch! Multiple OLD");
                                                        }
                                                    }
                                                }else {
                                                    //System.out.println("Touch! Multiple NEW ANSWER"+MainActivity.feedback.getResponses().get(j).getResponseItems().size());
                                                    itemMultipleChoice = new ResponseItem(answerMultipleChoice.getUri(), answerMultipleChoice.getType(), answerMultipleChoice.getUri());
                                                    itemMultipleChoice.addLinkedAnswerItem(answeritemMultipleChoice);
                                                    MainActivity.feedback.getResponses().get(j).addResponseItem(itemMultipleChoice);
                                                    System.out.println("Touch! Multiple NEW ANSWER");
                                                }
                                                ////////////////////////////////////////////////////////////////

                                                flag++;
                                            }
                                        }
                                        if (flag == 0) {
                                            //вопрос
                                            Question questionMultipleChoice = MainActivity.questionnaire.getQuestions().get(i);
                                            //тип ответов
                                            Answer answerMultipleChoice = questionMultipleChoice.getAnswer();
                                            //ответ выбранный
                                            AnswerItem answeritemMultipleChoice = answerMultipleChoice.getItems().get(view.getId());
                                            Response responseMultipleChoice = new Response(questionMultipleChoice.getUri(), questionMultipleChoice.getUri());
                                            ResponseItem itemMultipleChoice = new ResponseItem(answerMultipleChoice.getUri(), answerMultipleChoice.getType(), answerMultipleChoice.getUri());
                                            itemMultipleChoice.addLinkedAnswerItem(answeritemMultipleChoice);
                                            responseMultipleChoice.addResponseItem(itemMultipleChoice);
                                            MainActivity.feedback.addResponse(responseMultipleChoice);
                                        }
                                    }
                                }
                            }
                        }
                    });

                    //тут пишу проверку с ответами
                    for(int fbc = 0; fbc < feedback.size(); fbc++){
                        if(question.getUri().equals(feedback.get(fbc).getUri())) {
                            for (int aic = 0; aic < feedback.get(fbc).getResponseItems().get(0).getLinkedItems().size(); aic++) {
                                if(question.getAnswer().getItems().get(j).getUri().equals(feedback.get(fbc).getResponseItems().get(0).getLinkedItems().get(aic).getUri())){
                                    //
                                    MultipleChoiceAnswers[j].setChecked(true);///
                                }
                            }
                        }
                    }
                    //восстанавливаю отображение
                    holder.MultipleChoiceLayout.addView(MultipleChoiceAnswers[j]);
                }
            }
        } else if (viewHolder.getItemViewType() == Singlechoice) {
            Question question = Questions.get(position);
            Answer answer = question.getAnswer();
            LinkedList<AnswerItem> answeritem = answer.getItems();
            SingleChoiceViewHolder holder = (SingleChoiceViewHolder) viewHolder;
            holder.SingleChoiceQuestion.setText(question.getDescription());
            holder.uri = question.getUri();
            RadioButton[] SingleChoiceAnswers = new RadioButton[answeritem.size()];
            if (answeritem.size() > 0) {
                for (int j = 0; j < answeritem.size(); j++) {
                    AnswerItem Item = answeritem.get(j);
                    SingleChoiceAnswers[j] = new RadioButton(context);
                    SingleChoiceAnswers[j].setId(j);
                    SingleChoiceAnswers[j].setText(Item.getItemText());
                    //тут отображение ответов
                    for(int fbc = 0; fbc < feedback.size(); fbc++){
                        System.out.println("Touch! отображение в сигле до вопросов");
                        if(question.getUri().equals(feedback.get(fbc).getUri())) {
                            for (int aic = 0; aic < feedback.get(fbc).getResponseItems().get(0).getLinkedItems().size(); aic++) {
                                System.out.println("Touch! отображение в сигле до ответов");
                                if(question.getAnswer().getItems().get(j).getUri().equals(feedback.get(fbc).getResponseItems().get(0).getLinkedItems().get(aic).getUri())){
                                    //
                                    SingleChoiceAnswers[j].setChecked(true);///
                                    System.out.println("Touch! отображение нашло в сингле");
                                }
                            }
                        }
                    }
                    holder.SingleChoiceGroup.addView(SingleChoiceAnswers[j]);
                }
            }
        } else if (viewHolder.getItemViewType() == TextField) {
            Question question = Questions.get(position);
            TextFieldViewHolder holder = (TextFieldViewHolder) viewHolder;
            holder.TextFieldQuestion.setText(question.getDescription());
            holder.uri = question.getUri();
            //тут пишу проверку с ответами
            for(int fbc = 0; fbc < feedback.size(); fbc++){
                if(question.getUri().equals(feedback.get(fbc).getUri())) {
                    holder.TextFieldAnswer.setText(feedback.get(fbc).getResponseItems().get(0).getLinkedItems().get(0).getItemText().toString());
                }
            }
        } else if (viewHolder.getItemViewType() == Likertscale) {
            Question question = Questions.get(position);
            Answer answer = question.getAnswer();
            LinkedList<AnswerItem> answeritem = answer.getItems();
            LikertScaleViewHolder holder = (LikertScaleViewHolder) viewHolder;
            holder.LikertScaleQuestion.setText(question.getDescription());
            holder.uri = question.getUri();
            RadioButton[] LikertScaleAnswers = new RadioButton[answeritem.size()];
            if (answeritem.size() > 0) {
                for (int j = 0; j < answeritem.size(); j++) {
                    AnswerItem Item = answeritem.get(j);
                    LikertScaleAnswers[j] = new RadioButton(context);
                    LikertScaleAnswers[j].setId(j);
                    LikertScaleAnswers[j].setText(Item.getItemText());
                    //тут отображение ответов
                    for(int fbc = 0; fbc < feedback.size(); fbc++){
                        if(question.getUri().equals(feedback.get(fbc).getUri())) {
                            for (int aic = 0; aic < feedback.get(fbc).getResponseItems().get(0).getLinkedItems().size(); aic++) {
                                if(question.getAnswer().getItems().get(j).getUri().equals(feedback.get(fbc).getResponseItems().get(0).getLinkedItems().get(aic).getUri())){
                                    //
                                    LikertScaleAnswers[j].setChecked(true);///
                                }
                            }
                        }
                    }
                    holder.LikertScaleGroup.addView(LikertScaleAnswers[j]);
                }
            }
        } else if (viewHolder.getItemViewType() == Guttmanscale) {
            Question question = Questions.get(position);
            Answer answer = question.getAnswer();
            LinkedList<AnswerItem> answeritem = answer.getItems();
            GuttmanScaleViewHolder holder = (GuttmanScaleViewHolder) viewHolder;
            holder.GuttmanScaleQuestion.setText(question.getDescription());
            holder.uri = question.getUri();
            RadioButton[] GuttmanScaleAnswers = new RadioButton[answeritem.size()];
            if (answeritem.size() > 0) {
                for (int j = 0; j < answeritem.size(); j++) {
                    AnswerItem Item = answeritem.get(j);
                    GuttmanScaleAnswers[j] = new RadioButton(context);
                    GuttmanScaleAnswers[j].setId(j);
                    GuttmanScaleAnswers[j].setText(Item.getItemText());
                    //тут отображение ответов
                    for(int fbc = 0; fbc < feedback.size(); fbc++){
                        if(question.getUri().equals(feedback.get(fbc).getUri())) {
                            for (int aic = 0; aic < feedback.get(fbc).getResponseItems().get(0).getLinkedItems().size(); aic++) {
                                if(question.getAnswer().getItems().get(j).getUri().equals(feedback.get(fbc).getResponseItems().get(0).getLinkedItems().get(aic).getUri())){
                                    //
                                    GuttmanScaleAnswers[j].setChecked(true);///
                                }
                            }
                        }
                    }
                    holder.GuttmanScaleGroup.addView(GuttmanScaleAnswers[j]);
                }
            }
        } else if (viewHolder.getItemViewType() == Continuousscale) {
            Question question = Questions.get(position);
            Answer answer = question.getAnswer();
            LinkedList<AnswerItem> answeritem = answer.getItems();
            ContinuousScaleViewHolder holder = (ContinuousScaleViewHolder) viewHolder;
            holder.uri = question.getUri();

            //видимо тут отображать ответ буду
            for(int fbc = 0; fbc < feedback.size(); fbc++){
                if(question.getUri().equals(feedback.get(fbc).getUri())) {
                    holder.ContinuousScaleValue.setText(feedback.get(fbc).getResponseItems().get(0).getLinkedItems().get(0).getItemText().toString());
                    holder.ContinuousScaleSeekBar.setProgress(Integer.parseInt(holder.ContinuousScaleValue.getText().toString()));
                }
            }

            holder.ContinuousScaleQuestion.setText(question.getDescription());
            if (answeritem.size() > 0) {
                AnswerItem Item = answeritem.get(0);
                holder.ContinuousScaleSeekBar.setProgress(Integer.parseInt(Item.getItemText().replaceAll("[\\D]", "")));
                Item = answeritem.get(1);
                int Max = Integer.parseInt(Item.getItemText().replaceAll("[\\D]", ""));
                holder.ContinuousScaleSeekBar.setMax(Max);
                //Item = answeritem.get(2);
                //int Step = Integer.parseInt(Item.getItemText().replaceAll("[\\D]", ""));
                int Step = 10;
                TextView[] ContinuousScaleAnswers = new TextView[Step + 1];
                for (int j = 0; j < Step + 1; j++) {
                    ContinuousScaleAnswers[j] = new TextView(context);
                    ContinuousScaleAnswers[j].setId(j);
                    ContinuousScaleAnswers[j].setText(Integer.toString((Max / Step * j)));
                    ContinuousScaleAnswers[j].setLayoutParams(holder.params);
                    holder.ContinuousScaleIntervals.addView(ContinuousScaleAnswers[j]);
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

    public class DichotomousViewHolder extends ViewHolder {
        TextView DichotomousQuestion;
        RadioButton DichotomousAnswer1, DichotomousAnswer2;

        public DichotomousViewHolder(View v) {
            super(v);
            this.DichotomousQuestion = (TextView) v.findViewById(R.id.DichotomousQuestion);
            this.DichotomousAnswer1 = (RadioButton) v.findViewById(R.id.DichotomousAnswer1);
            this.DichotomousAnswer2 = (RadioButton) v.findViewById(R.id.DichotomousAnswer2);
        }

        //////////////////////////////////////////////////////////////////////////////////////////////
        /*
        TextView DichotomousQuestion;
        RadioGroup DichotomousGroup;
        RadioButton DichotomousAnswer;
        String uri;

        public DichotomousViewHolder(View v) {
            super(v);
            this.DichotomousQuestion = (TextView) v.findViewById(R.id.DichotomousQuestion);
            this.DichotomousGroup = (RadioGroup) v.findViewById(R.id.DichotomousAnswers);
            this.DichotomousAnswer = (RadioButton) v.getParent();
            DichotomousGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    System.out.println("Touch! Dichotomous "+checkedId+" "+uri);
                    for (int i = 0; i < MainActivity.questionnaire.getQuestions().size(); i++) {
                        if(MainActivity.questionnaire.getQuestions().get(i).getUri() == uri) {
                            int flag = 0;
                            for (int j = 0; j < MainActivity.feedback.getResponses().size(); j++) {
                                if(MainActivity.feedback.getResponses().get(j).getUri() == uri) {
                                    MainActivity.feedback.getResponses().get(j).getResponseItems().clear();
                                    //вопрос
                                    Question questionDichotomous = MainActivity.questionnaire.getQuestions().get(i);
                                    //тип ответов
                                    Answer answerDichotomous = questionDichotomous.getAnswer();
                                    //ответ выбранный
                                    AnswerItem answeritemDichotomous = answerDichotomous.getItems().get(checkedId);
                                    ResponseItem itemDichotomous = new ResponseItem(answerDichotomous.getUri(), answerDichotomous.getType(), answerDichotomous.getUri());
                                    itemDichotomous.addLinkedAnswerItem(answeritemDichotomous);
                                    MainActivity.feedback.getResponses().get(j).addResponseItem(itemDichotomous);
                                    flag++;
                                }
                            }
                            if(flag == 0) {
                                //вопрос
                                Question questionDichotomous = MainActivity.questionnaire.getQuestions().get(i);
                                //тип ответов
                                Answer answerDichotomous = questionDichotomous.getAnswer();
                                //ответ выбранный
                                AnswerItem answeritemDichotomous = answerDichotomous.getItems().get(checkedId);
                                Response responseDichotomous = new Response(questionDichotomous.getUri(), questionDichotomous.getUri());
                                ResponseItem itemDichotomous = new ResponseItem(answerDichotomous.getUri(), answerDichotomous.getType(), answerDichotomous.getUri());
                                itemDichotomous.addLinkedAnswerItem(answeritemDichotomous);
                                responseDichotomous.addResponseItem(itemDichotomous);
                                MainActivity.feedback.addResponse(responseDichotomous);
                            }
                        }
                    }
                }
            });
        }
        */
        //////////////////////////////////////////////////////////////////////////////////////////////
    }

    public class SingleChoiceViewHolder extends ViewHolder {
        TextView SingleChoiceQuestion;
        RadioGroup SingleChoiceGroup;
        RadioButton SingleChoiceAnswer;
        String uri;

        public SingleChoiceViewHolder(View v) {
            super(v);
            this.SingleChoiceQuestion = (TextView) v.findViewById(R.id.SingleChoiceQuestion);
            this.SingleChoiceGroup = (RadioGroup) v.findViewById(R.id.SingleChoiceAnswers);
            this.SingleChoiceAnswer = (RadioButton) v.getParent();

            SingleChoiceGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    System.out.println("Touch! SingleChoice "+checkedId+" "+uri);
                    for (int i = 0; i < MainActivity.questionnaire.getQuestions().size(); i++) {
                        if(MainActivity.questionnaire.getQuestions().get(i).getUri().equals(uri)) {
                            int flag = 0;
                            for (int j = 0; j < MainActivity.feedback.getResponses().size(); j++) {
                                if(MainActivity.feedback.getResponses().get(j).getUri().equals(uri)) {
                                    MainActivity.feedback.getResponses().get(j).getResponseItems().clear();
                                    //вопрос
                                    Question questionSingleChoice = MainActivity.questionnaire.getQuestions().get(i);
                                    //тип ответов
                                    Answer answerSingleChoice = questionSingleChoice.getAnswer();
                                    //ответ выбранный
                                    AnswerItem answeritemSingleChoice = answerSingleChoice.getItems().get(checkedId);
                                    ResponseItem itemSingleChoice = new ResponseItem(answerSingleChoice.getUri(), answerSingleChoice.getType(), answerSingleChoice.getUri());
                                    itemSingleChoice.addLinkedAnswerItem(answeritemSingleChoice);
                                    MainActivity.feedback.getResponses().get(j).addResponseItem(itemSingleChoice);
                                    flag++;
                                }
                            }
                            if(flag == 0) {
                                //вопрос
                                Question questionSingleChoice = MainActivity.questionnaire.getQuestions().get(i);
                                //тип ответов
                                Answer answerSingleChoice = questionSingleChoice.getAnswer();
                                //ответ выбранный
                                AnswerItem answeritemSingleChoice = answerSingleChoice.getItems().get(checkedId);
                                Response responseSingleChoice = new Response(questionSingleChoice.getUri(), questionSingleChoice.getUri());
                                ResponseItem itemSingleChoice = new ResponseItem(answerSingleChoice.getUri(), answerSingleChoice.getType(), answerSingleChoice.getUri());
                                itemSingleChoice.addLinkedAnswerItem(answeritemSingleChoice);
                                responseSingleChoice.addResponseItem(itemSingleChoice);
                                MainActivity.feedback.addResponse(responseSingleChoice);
                            }
                        }
                    }
                }
            });

        }
    }

    public class TextFieldViewHolder extends ViewHolder {
        TextView TextFieldQuestion;
        EditText TextFieldAnswer;
        String uri;
        public TextFieldViewHolder(View v) {
            super(v);
            this.TextFieldQuestion = (TextView) v.findViewById(R.id.TextQuestion);
            // get answer
            this.TextFieldAnswer = (EditText) v.findViewById(R.id.editText);
            //this.TextFieldAnswer.setText("текст при создании");

            TextFieldAnswer.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    System.out.println("Touch! TextField "+hasFocus+" "+uri);
                    if(false == hasFocus) {
                        for (int i = 0; i < MainActivity.questionnaire.getQuestions().size(); i++) {
                            if(MainActivity.questionnaire.getQuestions().get(i).getUri().equals(uri)) {
                                int flag = 0;
                                for (int j = 0; j < MainActivity.feedback.getResponses().size(); j++) {
                                    if (MainActivity.feedback.getResponses().get(j).getUri().equals(uri)) {
                                        MainActivity.feedback.getResponses().get(j).getResponseItems().clear();
                                        //вопрос
                                        Question questionTextField = MainActivity.questionnaire.getQuestions().get(i);
                                        //тип ответов
                                        Answer answerTextField = questionTextField.getAnswer();
                                        //ответ выбранный
                                        AnswerItem answeritemTextField = new AnswerItem(answerTextField.getItems().get(0).getUri(), answerTextField.getItems().get(0).getItemScore(), TextFieldAnswer.getText().toString());
                                        ResponseItem itemTextField = new ResponseItem(answerTextField.getUri(), answerTextField.getType(), answerTextField.getUri());
                                        itemTextField.addLinkedAnswerItem(answeritemTextField);
                                        MainActivity.feedback.getResponses().get(j).addResponseItem(itemTextField);
                                        flag++;
                                    }
                                }
                                if(flag == 0) {
                                    //вопрос
                                    Question questionTextField = MainActivity.questionnaire.getQuestions().get(i);
                                    //тип ответов
                                    Answer answerTextField = questionTextField.getAnswer();
                                    //ответ выбранный
                                    AnswerItem answeritemTextField = new AnswerItem(answerTextField.getItems().get(0).getUri(), answerTextField.getItems().get(0).getItemScore(), TextFieldAnswer.getText().toString());
                                    Response responseTextField = new Response(questionTextField.getUri(), questionTextField.getUri());
                                    ResponseItem itemTextField = new ResponseItem(answerTextField.getUri(), answerTextField.getType(), answerTextField.getUri());
                                    itemTextField.addLinkedAnswerItem(answeritemTextField);
                                    responseTextField.addResponseItem(itemTextField);
                                    MainActivity.feedback.addResponse(responseTextField);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    public class MultipleChoiceViewHolder extends ViewHolder {
        TextView MultipleChoiceQuestion;
        LinearLayout MultipleChoiceLayout;
        CheckBox MultipleChoiceAnswer;
        String uri;

        public MultipleChoiceViewHolder(View v) {
            super(v);
            this.MultipleChoiceQuestion = (TextView) v.findViewById(R.id.MultipleChoiceQuestion);
            this.MultipleChoiceLayout = (LinearLayout) v.findViewById(R.id.LinearMultiple);
            this.MultipleChoiceAnswer = (CheckBox) v.getParent();
        }
    }

    public class BipolarQuestionViewHolder extends ViewHolder {
        TextView BipolarQuestionQuestion;
        TextView BipolarQuestionValue;
        SeekBar BipolarQuestionSeekBar;
        String uri;

        public BipolarQuestionViewHolder(View v) {
            super(v);
            this.BipolarQuestionQuestion = (TextView) v.findViewById(R.id.BipolarQuestionQuestion);
            this.BipolarQuestionSeekBar = (SeekBar) v.findViewById(R.id.BipolarQuestionSeekBar);
            this.BipolarQuestionValue = (TextView) v.findViewById(R.id.BipolarQuestionValue);
            this.BipolarQuestionValue.setText(String.valueOf(BipolarQuestionSeekBar.getProgress()));

            BipolarQuestionSeekBar.setOnSeekBarChangeListener(
                    new SeekBar.OnSeekBarChangeListener() {
                        int progress = 0;

                        public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                            progress = progressValue;
                        }

                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        public void onStopTrackingTouch(SeekBar seekBar) {
                            BipolarQuestionValue.setText(String.valueOf(progress));
                            System.out.println("Touch! Bipolar "+uri+" "+BipolarQuestionValue.getText());////////////////
                            for (int i = 0; i < MainActivity.questionnaire.getQuestions().size(); i++) {
                                if(MainActivity.questionnaire.getQuestions().get(i).getUri().equals(uri)) {
                                    int flag = 0;
                                    for (int j = 0; j < MainActivity.feedback.getResponses().size(); j++) {
                                        if (MainActivity.feedback.getResponses().get(j).getUri().equals(uri)) {
                                            MainActivity.feedback.getResponses().get(j).getResponseItems().clear();
                                            //вопрос
                                            Question questionBipolarQuestion = MainActivity.questionnaire.getQuestions().get(i);
                                            //тип ответов
                                            Answer answerBipolarQuestion = questionBipolarQuestion.getAnswer();
                                            //ответ выбранный
                                            AnswerItem answeritemBipolarQuestion = new AnswerItem(answerBipolarQuestion.getItems().get(0).getUri(), answerBipolarQuestion.getItems().get(0).getItemScore(), BipolarQuestionValue.getText().toString());
                                            ResponseItem itemBipolarQuestion = new ResponseItem(answerBipolarQuestion.getUri(), answerBipolarQuestion.getType(), answerBipolarQuestion.getUri());
                                            itemBipolarQuestion.addLinkedAnswerItem(answeritemBipolarQuestion);
                                            MainActivity.feedback.getResponses().get(j).addResponseItem(itemBipolarQuestion);
                                            flag++;
                                        }
                                    }
                                    if(flag == 0) {
                                        //вопрос
                                        Question questionBipolarQuestion = MainActivity.questionnaire.getQuestions().get(i);
                                        //тип ответов
                                        Answer answerBipolarQuestion = questionBipolarQuestion.getAnswer();
                                        //ответ выбранный
                                        AnswerItem answeritemBipolarQuestion = new AnswerItem(answerBipolarQuestion.getItems().get(0).getUri(), answerBipolarQuestion.getItems().get(0).getItemScore(), BipolarQuestionValue.getText().toString());
                                        Response responseBipolarQuestion = new Response(questionBipolarQuestion.getUri(), questionBipolarQuestion.getUri());
                                        ResponseItem itemBipolarQuestion = new ResponseItem(answerBipolarQuestion.getUri(), answerBipolarQuestion.getType(), answerBipolarQuestion.getUri());
                                        itemBipolarQuestion.addLinkedAnswerItem(answeritemBipolarQuestion);
                                        responseBipolarQuestion.addResponseItem(itemBipolarQuestion);
                                        MainActivity.feedback.addResponse(responseBipolarQuestion);
                                    }
                                }
                            }
                        }
                    }
            );
        }
    }

    public class LikertScaleViewHolder extends ViewHolder {
        TextView LikertScaleQuestion;
        RadioGroup LikertScaleGroup;
        RadioButton LikertScaleAnswer;
        String uri;

        public LikertScaleViewHolder(View v) {
            super(v);
            this.LikertScaleQuestion = (TextView) v.findViewById(R.id.LikertScaleQuestion);
            this.LikertScaleGroup = (RadioGroup) v.findViewById(R.id.LikertScaleAnswers);
            this.LikertScaleAnswer = (RadioButton) v.getParent();

            LikertScaleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    System.out.println("Touch! LikertScale "+checkedId+" "+uri);
                    for (int i = 0; i < MainActivity.questionnaire.getQuestions().size(); i++) {
                        if(MainActivity.questionnaire.getQuestions().get(i).getUri().equals(uri)) {
                            int flag = 0;
                            for (int j = 0; j < MainActivity.feedback.getResponses().size(); j++) {
                                if(MainActivity.feedback.getResponses().get(j).getUri().equals(uri)) {
                                    MainActivity.feedback.getResponses().get(j).getResponseItems().clear();
                                    //вопрос
                                    Question questionLikertScale = MainActivity.questionnaire.getQuestions().get(i);
                                    //тип ответов
                                    Answer answerLikertScale = questionLikertScale.getAnswer();
                                    //ответ выбранный
                                    AnswerItem answeritemLikertScale = answerLikertScale.getItems().get(checkedId);
                                    ResponseItem itemLikertScale = new ResponseItem(answerLikertScale.getUri(), answerLikertScale.getType(), answerLikertScale.getUri());
                                    itemLikertScale.addLinkedAnswerItem(answeritemLikertScale);
                                    MainActivity.feedback.getResponses().get(j).addResponseItem(itemLikertScale);
                                    flag++;
                                }
                            }
                            if(flag == 0) {
                                //вопрос
                                Question questionLikertScale = MainActivity.questionnaire.getQuestions().get(i);
                                //тип ответов
                                Answer answerLikertScale = questionLikertScale.getAnswer();
                                //ответ выбранный
                                AnswerItem answeritemLikertScale = answerLikertScale.getItems().get(checkedId);
                                Response responseLikertScale = new Response(questionLikertScale.getUri(), questionLikertScale.getUri());
                                ResponseItem itemLikertScale = new ResponseItem(answerLikertScale.getUri(), answerLikertScale.getType(), answerLikertScale.getUri());
                                itemLikertScale.addLinkedAnswerItem(answeritemLikertScale);
                                responseLikertScale.addResponseItem(itemLikertScale);
                                MainActivity.feedback.addResponse(responseLikertScale);
                            }
                        }
                    }
                }
            });
        }
    }

    public class GuttmanScaleViewHolder extends ViewHolder {
        TextView GuttmanScaleQuestion;
        RadioGroup GuttmanScaleGroup;
        RadioButton GuttmanScaleAnswer;
        String uri;

        public GuttmanScaleViewHolder(View v) {
            super(v);
            this.GuttmanScaleQuestion = (TextView) v.findViewById(R.id.GuttmanScaleQuestion);
            this.GuttmanScaleGroup = (RadioGroup) v.findViewById(R.id.GuttmanScaleAnswers);
            this.GuttmanScaleAnswer = (RadioButton) v.getParent();
            GuttmanScaleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    System.out.println("Touch! GuttmanScale "+checkedId+" "+uri);
                    for (int i = 0; i < MainActivity.questionnaire.getQuestions().size(); i++) {
                        if(MainActivity.questionnaire.getQuestions().get(i).getUri().equals(uri)) {
                            int flag = 0;
                            for (int j = 0; j < MainActivity.feedback.getResponses().size(); j++) {
                                if(MainActivity.feedback.getResponses().get(j).getUri().equals(uri)) {
                                    MainActivity.feedback.getResponses().get(j).getResponseItems().clear();
                                    //вопрос
                                    Question questionGuttmanScale = MainActivity.questionnaire.getQuestions().get(i);
                                    //тип ответов
                                    Answer answerGuttmanScale = questionGuttmanScale.getAnswer();
                                    //ответ выбранный
                                    AnswerItem answeritemGuttmanScale = answerGuttmanScale.getItems().get(checkedId);
                                    ResponseItem itemGuttmanScale = new ResponseItem(answerGuttmanScale.getUri(), answerGuttmanScale.getType(), answerGuttmanScale.getUri());
                                    itemGuttmanScale.addLinkedAnswerItem(answeritemGuttmanScale);
                                    MainActivity.feedback.getResponses().get(j).addResponseItem(itemGuttmanScale);
                                    flag++;
                                }
                            }
                            if(flag == 0) {
                                //вопрос
                                Question questionGuttmanScale = MainActivity.questionnaire.getQuestions().get(i);
                                //тип ответов
                                Answer answerGuttmanScale = questionGuttmanScale.getAnswer();
                                //ответ выбранный
                                AnswerItem answeritemGuttmanScale = answerGuttmanScale.getItems().get(checkedId);
                                Response responseGuttmanScale = new Response(questionGuttmanScale.getUri(), questionGuttmanScale.getUri());
                                ResponseItem itemGuttmanScale = new ResponseItem(answerGuttmanScale.getUri(), answerGuttmanScale.getType(), answerGuttmanScale.getUri());
                                itemGuttmanScale.addLinkedAnswerItem(answeritemGuttmanScale);
                                responseGuttmanScale.addResponseItem(itemGuttmanScale);
                                MainActivity.feedback.addResponse(responseGuttmanScale);
                            }
                        }
                    }
                }
            });
        }
    }

    public class ContinuousScaleViewHolder extends ViewHolder {
        TextView ContinuousScaleQuestion;
        TextView ContinuousScaleAnswer;
        TextView ContinuousScaleValue;
        SeekBar ContinuousScaleSeekBar;
        LinearLayout ContinuousScaleIntervals;
        LinearLayout.LayoutParams params;
        String uri;

        public ContinuousScaleViewHolder(View v) {
            super(v);
            this.ContinuousScaleQuestion = (TextView) v.findViewById(R.id.ContinuousScaleQuestion);
            this.ContinuousScaleIntervals = (LinearLayout) v.findViewById(R.id.ContinuousScaleIntervals);
            this.params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0f;
            this.ContinuousScaleAnswer = (TextView) v.getParent();
            this.ContinuousScaleSeekBar = (SeekBar) v.findViewById(R.id.ContinuousScaleSeekBar);
            this.ContinuousScaleValue = (TextView) v.findViewById(R.id.ContinuousScaleValue);
            this.ContinuousScaleValue.setText(String.valueOf(ContinuousScaleSeekBar.getProgress()));
            ContinuousScaleSeekBar.setOnSeekBarChangeListener(
                    new SeekBar.OnSeekBarChangeListener() {
                        int Step = 10;
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            progress = ((int)Math.round(progress/Step))*Step;
                            seekBar.setProgress(progress);
                            ContinuousScaleValue.setText(progress + "");
                        }

                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        public void onStopTrackingTouch(SeekBar seekBar) {
                            System.out.println("Touch! ContinuousScale "+uri+" "+ContinuousScaleValue.getText());////////////////
                            for (int i = 0; i < MainActivity.questionnaire.getQuestions().size(); i++) {
                                if(MainActivity.questionnaire.getQuestions().get(i).getUri().equals(uri)) {
                                    int flag = 0;
                                    for (int j = 0; j < MainActivity.feedback.getResponses().size(); j++) {
                                        if (MainActivity.feedback.getResponses().get(j).getUri().equals(uri)) {
                                            MainActivity.feedback.getResponses().get(j).getResponseItems().clear();
                                            //вопрос
                                            Question questionContinuousScale = MainActivity.questionnaire.getQuestions().get(i);
                                            //тип ответов
                                            Answer answerContinuousScale = questionContinuousScale.getAnswer();
                                            //ответ выбранный
                                            AnswerItem answeritemContinuousScale = new AnswerItem(answerContinuousScale.getItems().get(0).getUri(), answerContinuousScale.getItems().get(0).getItemScore(), ContinuousScaleValue.getText().toString());
                                            ResponseItem itemContinuousScale = new ResponseItem(answerContinuousScale.getUri(), answerContinuousScale.getType(), answerContinuousScale.getUri());
                                            itemContinuousScale.addLinkedAnswerItem(answeritemContinuousScale);
                                            MainActivity.feedback.getResponses().get(j).addResponseItem(itemContinuousScale);
                                            flag++;
                                        }
                                    }
                                    if(flag == 0) {
                                        //вопрос
                                        Question questionContinuousScale = MainActivity.questionnaire.getQuestions().get(i);
                                        //тип ответов
                                        Answer answerContinuousScale = questionContinuousScale.getAnswer();
                                        //ответ выбранный
                                        AnswerItem answeritemContinuousScale = new AnswerItem(answerContinuousScale.getItems().get(0).getUri(), answerContinuousScale.getItems().get(0).getItemScore(), ContinuousScaleValue.getText().toString());
                                        Response responseContinuousScale = new Response(questionContinuousScale.getUri(), questionContinuousScale.getUri());
                                        ResponseItem itemContinuousScale = new ResponseItem(answerContinuousScale.getUri(), answerContinuousScale.getType(), answerContinuousScale.getUri());
                                        itemContinuousScale.addLinkedAnswerItem(answeritemContinuousScale);
                                        responseContinuousScale.addResponseItem(itemContinuousScale);
                                        MainActivity.feedback.addResponse(responseContinuousScale);
                                    }
                                }
                            }
                        }
                    }
            );
        }
    }
}