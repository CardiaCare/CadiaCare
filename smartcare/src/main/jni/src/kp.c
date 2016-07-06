
/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include "ontology/smartcare.h"
#include <android/log.h>
#include <errno.h>
#include "agent.h"

#include "handlers.h"
#include "jni_utils.h"
#include "globals.h"

#define TAG "SS"
#define KP_NS_URI "http://oss.fruct.org/smartcare#"
#define KP_PREDICATE KP_NS_URI"sendAlarm"

/*
 *   Подключаемся к Интелектуальному пространству
 *  SmartSpace node initialization by hostname_, ip_,port
 */
JNIEXPORT jlong JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_connectSmartSpace( JNIEnv* env,
                                                  jobject thiz , jstring hostname, jstring ip, jint port) {

    const char *hostname_ = (*env)->GetStringUTFChars(env, hostname, NULL);
    if( hostname_ == NULL) {
        return -1;
    }
    const char *ip_ = (*env)->GetStringUTFChars(env, ip, NULL);
    if( ip_ == NULL ){
        return -1;
    }
    long node = kp_connect_smartspace( hostname_, ip_, port);

    if (node == -1) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "Can't join to SS");
        return -1;
    } else {
        __android_log_print(ANDROID_LOG_INFO, TAG, "KP joins to SS.");
    }
    /*
     * Initialize global references to java classes
     * to use'em in handlers etc.
     */
    if( -1 == init_global_instances(env, thiz) ) {
        return -1;
    }
    /*
     * Get Java Virtual Machine Instance
     * to use it in callbacks generally
     */
    if( -1 == init_JVM_instance(env) ){
        return -1;
    }
    /*
     * Return node descriptor
     * to use it further in subscriptions etc.
     */
    return (jlong) node;

}
/*
 *  Disconnect from smartspace
 *
 */
JNIEXPORT void JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_disconnectSmartSpace( JNIEnv* env,
                                                  jobject thiz , jlong nodeDescriptor){
    int result = kp_disconnect_smartspace(nodeDescriptor);

    if (result == -1)
        __android_log_print(ANDROID_LOG_INFO, TAG, "Node Error");
    else
        __android_log_print(ANDROID_LOG_INFO, TAG, "KP leaves SS...");
}

/***********************************************************************************************************/

/*
 * Удаляем произвольного индивида по uri
 *
*/
JNIEXPORT void JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_removeIndividual
( JNIEnv* env, jobject thiz , jlong nodeDescriptor, jstring individualUri)
{

    if (individualUri == NULL)
        return;
    const char * individual_uri= (*env)->GetStringUTFChars(env, individualUri, 0);



    int result = kp_remove_individual(nodeDescriptor, individual_uri);

    if (result == -1){
        __android_log_print(ANDROID_LOG_INFO, TAG, "Individual already removed");
    }
    else{
        __android_log_print(ANDROID_LOG_INFO, TAG, "remove Individual");
    }
}

JNIEXPORT void JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_removeAlarm
( JNIEnv* env, jobject thiz , jlong nodeDescriptor, jstring individualUri)
{
    const char * individual_uri= (*env)->GetStringUTFChars(env, individualUri, 0);

    int result = kp_remove_alarm(nodeDescriptor, individual_uri);

    if (result == -1){
        __android_log_print(ANDROID_LOG_INFO, TAG, "Node Error");
    }
    else{
        __android_log_print(ANDROID_LOG_INFO, TAG, "remove Individual");
    }
}
/*
 * Инициализируем индивид пациента (возвращает сгенерированный ЮРИ пациента)
 *
 */

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_initPatient
        (JNIEnv *env, jobject thiz, jlong nodeDescriptor)
{
    //TODO: корректно выделить память!!!
    char*  patient_uri  = (char *) malloc(30);

    kp_init_patient(&patient_uri, nodeDescriptor);

    __android_log_print(ANDROID_LOG_INFO, TAG, "patient_uri: %s\n", patient_uri);

    return (*env)->NewStringUTF(env, patient_uri);
    free(patient_uri);
}

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_initAuthRequest
        (JNIEnv *env, jobject thiz, jlong nodeDescriptor, jstring patientUri)
{

    const char * patient_uri= (*env)->GetStringUTFChars(env, patientUri, 0);
    //TODO: корректно выделить память!!!
    char*  auth_request_uri  = (char *) malloc(30);

    kp_init_auth_request(nodeDescriptor, patient_uri, &auth_request_uri);
    return (*env)->NewStringUTF(env, auth_request_uri);
}

JNIEXPORT jint JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getAuthResponce
        (JNIEnv *env, jobject thiz, jlong nodeDescriptor, jstring authDescriptor)
{
    //get node from SmartSpace
    sleep(5);

    const char * auth_uri= (*env)->GetStringUTFChars(env, authDescriptor, 0);

    int result = kp_get_auth_responce(nodeDescriptor, auth_uri);
    if (result == -1) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "NOT PASSED");
        return -1;
    }
    else {
        __android_log_print(ANDROID_LOG_INFO, TAG, "PASSED");
        return 0;
    }
}


/*
 * Инициализируем индивид локации (возвращает сгенерированный ЮРИ локации)
 *
 */
JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_initLocation
        (JNIEnv *env, jobject thiz, jlong nodeDescriptor, jstring individualUri)
{
    //get individual from SmartSpace
    const char * individual_uri= (*env)->GetStringUTFChars(env, individualUri, 0);

    //TODO: корректно выделить память!!!
    char*  location_uri  = (char *) malloc(30);

    kp_init_location (nodeDescriptor, individual_uri, &location_uri);

    return (*env)->NewStringUTF(env, location_uri);
}
/*
 *
 * Отпраляем аларм  ТУДУ
 */
JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_sendAlarm
        ( JNIEnv* env, jobject thiz, jlong nodeDescriptor,jstring patientUri )
{
    const char *patient_uri = (*env)->GetStringUTFChars(env, patientUri, 0);

    char * alarm_uri = (char *) malloc(30);
    kp_send_alarm(nodeDescriptor, patient_uri, &alarm_uri);

    __android_log_print(ANDROID_LOG_INFO, TAG, "alarm_uri: %s\n", alarm_uri);
    return (*env)->NewStringUTF(env, alarm_uri);

}
/*
 *
 * Отпралвяем локацию. В разработке
 */
JNIEXPORT jint JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_sendLocation
            (JNIEnv *env, jobject thiz, jlong nodeDescriptor, jstring patientUri, jstring locationUri, jstring latitudeJ, jstring longitudeJ)
{
    const char * patient_uri= (*env)->GetStringUTFChars(env, patientUri, 0);
    const char * location_uri= (*env)->GetStringUTFChars(env, locationUri, 0);
    const char *latitude = (*env)->GetStringUTFChars(env, latitudeJ, 0);
    const char *longitude = (*env)->GetStringUTFChars(env, longitudeJ, 0);

    int result = kp_send_location(nodeDescriptor, patient_uri, location_uri, latitude, longitude);
    if (result == -1) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "Node Error");
        return -1;
    }
    else {
        __android_log_print(ANDROID_LOG_INFO, TAG, "Send Location");
        return 0;
    }
}

/*
 * Инициализируем индивид локации (возвращает сгенерированный ЮРИ локации)
 *
 */
JNIEXPORT jint JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_insertPersonName
        (JNIEnv *env, jobject thiz, jlong nodeDescriptor, jstring patientUri, jstring name)
{
    const char * patient_uri= (*env)->GetStringUTFChars(env, patientUri, 0);
    const char *new_name = (*env)->GetStringUTFChars(env, name, 0);

    int result = kp_insert_person_name( nodeDescriptor, patient_uri, new_name);
    if (result == -1) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "Node Error");
        return -1;
    }
    else {
        __android_log_print(ANDROID_LOG_INFO, TAG, "Insert person name");
        return 0;
    }
}

JNIEXPORT jint JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_updatePersonName
        (JNIEnv *env, jobject thiz, jlong nodeDescriptor, jstring patientUri, jstring name)
{
    const char * patient_uri= (*env)->GetStringUTFChars(env, patientUri, 0);
    const char *new_name = (*env)->GetStringUTFChars(env, name, 0);

    int result = kp_update_person_name(nodeDescriptor, patient_uri, new_name);

    if (result == -1) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "Node Error");
        return -1;
    }
    else {
        __android_log_print(ANDROID_LOG_INFO, TAG, "Update person name");
        return 0;
    }
}

/***********************************************************************************************************/


JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getQuestionnaire
        (JNIEnv* env, jobject thiz , jlong nodeDescriptor){

    sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL ){
        __android_log_print(ANDROID_LOG_INFO, TAG, "Node Error");
        return NULL;
    }
    char* questionnaire_uri;
    sslog_individual_t *questionnaire_ss =  kp_get_questionnaire(node, &questionnaire_uri);

    /**************/
    //jobject *questionnaire;
    //questionnaire = (*env)->NewObject(env, class_questionnaire, questionnaire_constructor, (*env)->NewStringUTF(env, questionnaire_uri) );

    //char* question_uri;
    //char* next_question_uri;

    //jobject first_question;
    //first_question = kp_get_first_question(env, questionnaire, node, questionnaire_ss, &next_question_uri);
    //question_uri = next_question_uri;
    //(*env)->CallVoidMethod(env, questionnaire, add_question, first_question);

    //jobject next_question;
   // while (next_question_uri != NULL) {
      //  next_question = kp_get_next_question(env, questionnaire, node, questionnaire_ss, question_uri, &next_question_uri);
    //    (*env)->CallVoidMethod(env, questionnaire, add_question, next_question);
  //      question_uri = next_question_uri;
//    }
    /**************/
    return (*env)->NewStringUTF(env, questionnaire_uri);
}

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getQuestionnaireVersion
(JNIEnv *env, jobject thiz, jlong nodeDescriptor, jstring questionnireUri){

 sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        return -1;
    }
const char * questionnire_uri= (*env)->GetStringUTFChars(env, questionnireUri, 0);

sslog_individual_t *questionnaire = sslog_node_get_individual_by_uri(node, questionnire_uri);

    char *version;
    version = (char *) sslog_node_get_property(node, questionnaire, PROPERTY_VERSION);

    return (*env)->NewStringUTF(env, version);
}

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getQuestionnaireSeverUri
(JNIEnv *env, jobject thiz, jlong nodeDescriptor, jstring questionnireUri){

 sslog_node_t *node = (sslog_node_t *) nodeDescriptor;
    if (node == NULL){
        return -1;
    }
const char * questionnire_uri= (*env)->GetStringUTFChars(env, questionnireUri, 0);

sslog_individual_t *questionnaire = sslog_node_get_individual_by_uri(node, questionnire_uri);

    char *server_uri;
    server_uri = (char *) sslog_node_get_property(node, questionnaire, PROPERTY_QUESTIONNAIREURI);
    return (*env)->NewStringUTF(env, server_uri);
}

/*
 *
 * Отправляем Feedback
 */
JNIEXPORT jint JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_sendFeedback
        ( JNIEnv* env, jobject thiz, jlong nodeDescriptor,jstring patientUri, jstring feedbackDate )
{
    const char *patient_uri = (*env)->GetStringUTFChars(env, patientUri, 0);
    const char *feedback_date = (*env)->GetStringUTFChars(env, feedbackDate, 0);

    //char * alarm_uri = (char *) malloc(30);
    int result = kp_send_feedback(nodeDescriptor, patient_uri, feedback_date);
    if (result == -1) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "Node Error");
        return -1;
    }
    else {
        __android_log_print(ANDROID_LOG_INFO, TAG, "Send Feedback");
        return 0;
    }
}
