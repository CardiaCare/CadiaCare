#ifdef	__cplusplus
extern "C" {
#endif

#include "ontology/smartcare.h"
#include <jni.h>

int get_his_subclasses(char *uri, char** subclass);

int kp_get_his(char** his_uri);

int kp_send_his_request( char* his_uri, char* patient_uri,char* his_document_type, char* searchstring, char* fieldname, char* datefrom, char* dateto, char**);

int kp_get_his_response(char* his_request_uri, char** his_response_uri, char** his_document_uri, char** his_document_type);

int kp_get_his_laboratory_analysis( char* his_document_uri,
        char** createdAt, char** author,
        char** organizationName, char** hemoglobin, char** erythrocyte, char** hematocrit);

int kp_get_his_blood_pressure_measurement(char* his_document_uri,
        char** createdAt, char** author,
        char** systolicPressure, char** diastolicPressure, char** pulse);

int kp_get_his_ECG_measurement(char* his_document_uri,
        char** createdAt, char** author,
        char** dataLocation);

int kp_get_his_demographic_data(char* his_document_uri,
        char** createdAt, char** author,
        char** name, char** surname, char** patronymic, char** birthDate, char** sex, char** residence, char** contactInformaiton);

int kp_get_his_doctor_examination(char* his_document_uri,
        char** createdAt, char** author,
        char** examinationReason, char** visitOrder, char** diagnoses, char** medications, char** smoking, char** drinking, char** height, char** weight,  char** diseasePredisposition);

void kp_sbcr_his_request(sslog_subscription_t *);
int kp_init_sbcr_his_response();


#ifdef	__cplusplus
}
#endif
