/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>

/* Header for class com_petrsu_cardiacare_smartcarequestionnaire_MainActivity */

#ifndef _Included_com_petrsu_cardiacare_smartcare_SmartCareLibrary
#define _Included_com_petrsu_cardiacare_smartcare_SmartCareLibrary
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_petrsu_cardiacare_smartcarevolunteer_MainActivity
 * Method:    connectSmartSpace
 * Signature: (Ljava/lang/String;Ljava/lang/String;I)I
 */
JNIEXPORT jlong JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_connectSmartSpace
  (JNIEnv *, jobject, jstring, jstring, jint);

/*
 * Class:     com_petrsu_cardiacare_smartcarevolunteer_MainActivity
 * Method:    disconnectSmartSpace
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_disconnectSmartSpace
  (JNIEnv *, jobject, jlong);

/*****************************************************************************************/

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_initPatient
  (JNIEnv *, jobject, jlong);

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_initPatientWithId
    (JNIEnv *, jobject, jlong, jstring);

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_initAuthRequest
        (JNIEnv *, jobject, jlong, jstring);

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_initLocation
        (JNIEnv *, jobject, jlong, jstring);

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_sendAlarm
        (JNIEnv *, jobject, jlong, jstring);

JNIEXPORT jint JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_sendLocation
        (JNIEnv *, jobject, jlong, jstring, jstring, jstring, jstring);

JNIEXPORT jint JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_sendFeedback
         (JNIEnv *, jobject, jlong, jstring, jstring);

JNIEXPORT void JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_removeIndividual
        (JNIEnv*, jobject, jlong, jstring);

JNIEXPORT void JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_removeAlarm
(JNIEnv*, jobject, jlong, jstring);

JNIEXPORT jint JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_insertPersonName
        (JNIEnv *, jobject , jlong , jstring , jstring );

JNIEXPORT jint JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_updatePersonName
        (JNIEnv *, jobject , jlong , jstring , jstring );

JNIEXPORT jint JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getAuthResponce
        (JNIEnv *, jobject , jlong , jstring);

/*****************************************************************************************/



JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getQuestionnaire
(JNIEnv *, jobject, jlong);

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getQuestionnaireVersion
(JNIEnv *, jobject, jlong,jstring);

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getQuestionnaireSeverUri
(JNIEnv *, jobject, jlong,jstring);


/*****************************************************************************************/

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getHis
        ( JNIEnv* env, jobject thiz, jlong );

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_sendHisRequest
        ( JNIEnv* env, jobject thiz, jlong,  jstring hisUri, jstring patientUri,
            jstring hisDocumentType, jstring searchstring,
            jstring fieldName, jstring dateFrom,
            jstring dateTo);

JNIEXPORT jint JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_removeHisRequest
         ( JNIEnv* env, jobject thiz, jlong ,  jstring , jstring );

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getHisResponce
         ( JNIEnv* env, jobject thiz, jlong,  jstring );

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getHisDocument
         ( JNIEnv* env, jobject thiz, jlong ,  jstring );

JNIEXPORT jobject JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getHisBloodPressureResult
        ( JNIEnv* env, jobject thiz, jlong,  jstring hisDocumentUri);

JNIEXPORT jobject JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getHisDemographicData
        ( JNIEnv* env, jobject thiz, jlong,  jstring hisDocumentUri);

JNIEXPORT jobject JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getHislaboratoryStudy
        ( JNIEnv* env, jobject thiz, jlong,  jstring hisDocumentUri);

JNIEXPORT jobject JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_getHisDoctorExamination
        ( JNIEnv* env, jobject thiz, jlong,  jstring hisDocumentUri);

JNIEXPORT jstring JNICALL Java_com_petrsu_cardiacare_smartcare_SmartCareLibrary_setHisId
        (JNIEnv* env, jobject thiz, jlong nodeDescriptor, jstring hisId, jstring patientId);


#ifdef __cplusplus
}
#endif
#endif
/* Header for class com_petrsu_cardiacare_smartcarequestionnaire_MainActivity_PatientListTask */

#ifndef _Included_com_petrsu_cardiacare_smartcare_SmartCare_PatientListTask
#define _Included_com_petrsu_cardiacare_smartcare_SmartCare_PatientListTask
#ifdef __cplusplus
extern "C" {
#endif
#ifdef __cplusplus
}
#endif
#endif
