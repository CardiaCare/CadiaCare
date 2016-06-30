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

/**
 * Kristina Shevtsova
 * Kirill Orloff
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    LinkedList <Question> Questions;
    int[] TypesQuestions;
    Context context;

    public static final int TextField = 0;
    public static final int Multiplechoice = 1;
    public static final int Singlechoice = 2;
    public static final int Bipolarquestion = 3;
    public static final int Guttmanscale = 4;
    public static final int Likertscale = 5;
    public static final int Continuousscale = 6;
    public static final int Dichotomous = 7;
    public static final int DefaultValue = 8;

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
        } else if (viewHolder.getItemViewType() == Bipolarquestion) {
            Question question = Questions.get(position);
            Answer answer = question.getAnswer();
            LinkedList<AnswerItem> answeritem = answer.getItems();
            BipolarQuestionViewHolder holder = (BipolarQuestionViewHolder) viewHolder;
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
            CheckBox[] MultipleChoiceAnswers = new CheckBox[answeritem.size()];
            if (answeritem.size() > 0) {
                for (int j = 0; j < answeritem.size(); j++) {
                    AnswerItem Item = answeritem.get(j);
                    MultipleChoiceAnswers[j] = new CheckBox(context);
                    MultipleChoiceAnswers[j].setId(j);
                    MultipleChoiceAnswers[j].setText(Item.getItemText());
                    holder.MultipleChoiceLayout.addView(MultipleChoiceAnswers[j]);
                }
            }
        } else if (viewHolder.getItemViewType() == Singlechoice) {
            Question question = Questions.get(position);
            Answer answer = question.getAnswer();
            LinkedList<AnswerItem> answeritem = answer.getItems();
            SingleChoiceViewHolder holder = (SingleChoiceViewHolder) viewHolder;
            holder.SingleChoiceQuestion.setText(question.getDescription());
            RadioButton[] SingleChoiceAnswers = new RadioButton[answeritem.size()];
            if (answeritem.size() > 0) {
                for (int j = 0; j < answeritem.size(); j++) {
                    AnswerItem Item = answeritem.get(j);
                    SingleChoiceAnswers[j] = new RadioButton(context);
                    SingleChoiceAnswers[j].setId(j);
                    SingleChoiceAnswers[j].setText(Item.getItemText());
                    holder.SingleChoiceGroup.addView(SingleChoiceAnswers[j]);
                }
            }
        } else if (viewHolder.getItemViewType() == TextField) {
            Question question = Questions.get(position);
            TextFieldViewHolder holder = (TextFieldViewHolder) viewHolder;
            holder.TextFieldQuestion.setText(question.getDescription());
        } else if (viewHolder.getItemViewType() == Likertscale) {
            Question question = Questions.get(position);
            Answer answer = question.getAnswer();
            LinkedList<AnswerItem> answeritem = answer.getItems();
            LikertScaleViewHolder holder = (LikertScaleViewHolder) viewHolder;
            holder.LikertScaleQuestion.setText(question.getDescription());
            RadioButton[] LikertScaleAnswers = new RadioButton[answeritem.size()];
            if (answeritem.size() > 0) {
                for (int j = 0; j < answeritem.size(); j++) {
                    AnswerItem Item = answeritem.get(j);
                    LikertScaleAnswers[j] = new RadioButton(context);
                    LikertScaleAnswers[j].setId(j);
                    LikertScaleAnswers[j].setText(Item.getItemText());
                    holder.LikertScaleGroup.addView(LikertScaleAnswers[j]);
                }
            }
        } else if (viewHolder.getItemViewType() == Guttmanscale) {
            Question question = Questions.get(position);
            Answer answer = question.getAnswer();
            LinkedList<AnswerItem> answeritem = answer.getItems();
            GuttmanScaleViewHolder holder = (GuttmanScaleViewHolder) viewHolder;
            holder.GuttmanScaleQuestion.setText(question.getDescription());
            RadioButton[] GuttmanScaleAnswers = new RadioButton[answeritem.size()];
            if (answeritem.size() > 0) {
                for (int j = 0; j < answeritem.size(); j++) {
                    AnswerItem Item = answeritem.get(j);
                    GuttmanScaleAnswers[j] = new RadioButton(context);
                    GuttmanScaleAnswers[j].setId(j);
                    GuttmanScaleAnswers[j].setText(Item.getItemText());
                    holder.GuttmanScaleGroup.addView(GuttmanScaleAnswers[j]);
                }
            }
        } else if (viewHolder.getItemViewType() == Continuousscale) {
            Question question = Questions.get(position);
            Answer answer = question.getAnswer();
            LinkedList<AnswerItem> answeritem = answer.getItems();
            ContinuousScaleViewHolder holder = (ContinuousScaleViewHolder) viewHolder;
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
    }

    public class SingleChoiceViewHolder extends ViewHolder {
        TextView SingleChoiceQuestion;
        RadioGroup SingleChoiceGroup;
        RadioButton SingleChoiceAnswer;

        public SingleChoiceViewHolder(View v) {
            super(v);
            this.SingleChoiceQuestion = (TextView) v.findViewById(R.id.SingleChoiceQuestion);
            this.SingleChoiceGroup = (RadioGroup) v.findViewById(R.id.SingleChoiceAnswers);
            this.SingleChoiceAnswer = (RadioButton) v.getParent();
        }
    }

    public class TextFieldViewHolder extends ViewHolder {
        TextView TextFieldQuestion;
        EditText TextFieldAnswer;
        public TextFieldViewHolder(View v) {
            super(v);
            this.TextFieldQuestion = (TextView) v.findViewById(R.id.TextQuestion);
            // get answer
            this.TextFieldAnswer = (EditText) v.findViewById(R.id.editText);
        }
    }

    public class MultipleChoiceViewHolder extends ViewHolder {
        TextView MultipleChoiceQuestion;
        LinearLayout MultipleChoiceLayout;
        CheckBox MultipleChoiceAnswer;

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
                    }
                }
            );
        }
    }

    public class LikertScaleViewHolder extends ViewHolder {
        TextView LikertScaleQuestion;
        RadioGroup LikertScaleGroup;
        RadioButton LikertScaleAnswer;

        public LikertScaleViewHolder(View v) {
            super(v);
            this.LikertScaleQuestion = (TextView) v.findViewById(R.id.LikertScaleQuestion);
            this.LikertScaleGroup = (RadioGroup) v.findViewById(R.id.LikertScaleAnswers);
            this.LikertScaleAnswer = (RadioButton) v.getParent();
        }
    }

    public class GuttmanScaleViewHolder extends ViewHolder {
        TextView GuttmanScaleQuestion;
        RadioGroup GuttmanScaleGroup;
        RadioButton GuttmanScaleAnswer;

        public GuttmanScaleViewHolder(View v) {
            super(v);
            this.GuttmanScaleQuestion = (TextView) v.findViewById(R.id.GuttmanScaleQuestion);
            this.GuttmanScaleGroup = (RadioGroup) v.findViewById(R.id.GuttmanScaleAnswers);
            this.GuttmanScaleAnswer = (RadioButton) v.getParent();
        }
    }

    public class ContinuousScaleViewHolder extends ViewHolder {
        TextView ContinuousScaleQuestion;
        TextView ContinuousScaleAnswer;
        TextView ContinuousScaleValue;
        SeekBar ContinuousScaleSeekBar;
        LinearLayout ContinuousScaleIntervals;
        LinearLayout.LayoutParams params;

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

                        }
                    }
            );
        }
    }
}