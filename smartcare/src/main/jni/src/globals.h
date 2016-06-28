//
// Created by nikolay on 20.01.16.
//

#ifndef SMARTCARE_QUESTIONNAIRE_GLOBALS_H
#define SMARTCARE_QUESTIONNAIRE_GLOBALS_H

#endif //SMARTCARE_QUESTIONNAIRE_GLOBALS_H
#include "ontology/smartcare.h"
#include <jni.h>

extern jobject *GlobalMainActivityClassObject;
extern JavaVM* JVM;
extern jclass *GlobalMainActivityClass;
extern sslog_node_t *GlobalNode;


extern jclass *class_question;
extern jclass *class_questionnaire;
extern jclass *class_answer;
extern jclass *class_answer_item;

extern jmethodID questionnaire_constructor;
extern jmethodID add_question;
extern jmethodID question_constructor;
extern jmethodID answer_constructor;
extern jmethodID add_answer;
extern jmethodID add_subanswer;
extern jmethodID item_constructor;
extern jmethodID add_answer_item_i;



