#include "his.h"
#include "globals.h"
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include <string.h>
#define RDF_TYPE "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
/*
 * Генерация добавки для URI
 */

int kp_get_his(char** his_uri){
   
    list_t* hises;

    hises = sslog_node_get_individuals_by_class(GlobalNode, CLASS_HOSPITALINFORMATIONSYSTEM);

    if (list_is_null_or_empty(hises) == true) {
        printf("There are no such individuals.\n");
        return -1;
    }
    sslog_individual_t *his = NULL;
    list_head_t *pos = NULL;

    list_for_each(pos, &hises->links)
    {
        list_t *node = list_entry(pos, list_t, links);
        his = (sslog_individual_t *) node->data;
        sslog_triple_t *his_uri_from_triple = sslog_individual_to_triple (his);
        *his_uri  = his_uri_from_triple->subject;
        return 0;
    } 
}

int kp_send_his_request(char* his_uri, char* patient_uri,  char* his_document_type,
            char* search_string, char* field_name, char* date_from, char* date_to, char** request_uri){


    sslog_individual_t *his = sslog_node_get_individual_by_uri(GlobalNode, his_uri);
    sslog_individual_t *patient = sslog_node_get_individual_by_uri(GlobalNode, patient_uri);

    char * _his_request_uri = sslog_generate_uri(CLASS_HISREQUEST);
    char *his_request_uri = generate_uri(_his_request_uri);

    sslog_individual_t *his_request = sslog_new_individual(CLASS_HISREQUEST, his_request_uri);
    his_request_glob = his_request;
    if (his_request == NULL) {
        return -1;
    }

    if (search_string == NULL){
        sslog_insert_property(his_request, PROPERTY_SEARCHSTRING, search_string);
    }
    if ( field_name == NULL){
        sslog_insert_property(his_request, PROPERTY_FIELDNAME, field_name);
    }
    if (date_from == NULL){
        sslog_insert_property(his_request, PROPERTY_DATEFROM, date_from);
    }

    if ( date_to == NULL){
        sslog_insert_property(his_request, PROPERTY_DATETO, date_to);
    }

    *request_uri = his_request_uri;
    sslog_node_insert_individual(GlobalNode, his_request);

    //object property of request - REQUESTSDOCUMENT
    char* class_uri;
        if (strcmp(his_document_type, "http://oss.fruct.org/smartcare#DemographicData") == 0) {
            class_uri =  sslog_entity_get_uri (CLASS_DEMOGRAPHICDATA);
        } 
        else if (strcmp(his_document_type, "http://oss.fruct.org/smartcare#BloodPressureMeasurement") == 0){
                class_uri =  sslog_entity_get_uri (CLASS_BLOODPRESSUREMEASUREMENT);
        }
        else if (strcmp(his_document_type, "http://oss.fruct.org/smartcare#LaboratoryAnalysis") == 0){
                class_uri =  sslog_entity_get_uri (CLASS_LABORATORYANALYSIS);
        }
        else if (strcmp(his_document_type, "http://oss.fruct.org/smartcare#DoctorExamination") == 0){
                class_uri =  sslog_entity_get_uri (CLASS_DOCTOREXAMINATION);
        }
        else if (strcmp(his_document_type, "http://oss.fruct.org/smartcare#ECGMeasurement") == 0){
                class_uri =  sslog_entity_get_uri (CLASS_ECGMEASUREMENT);
        }
    char* pred_uri =  sslog_entity_get_uri (PROPERTY_REQUESTSDOCUMENT);
    sslog_triple_t *class_triple = sslog_new_triple_detached(
            class_uri,
            RDF_TYPE,
            "http://www.w3.org/2000/01/rdf-schema#Class",
            SS_RDF_TYPE_URI, SS_RDF_TYPE_URI);
    sslog_node_insert_triple(GlobalNode, class_triple);

    sslog_triple_t *type_triple = sslog_new_triple_detached(
            his_request_uri,
            pred_uri,
            class_uri,
            SS_RDF_TYPE_URI, SS_RDF_TYPE_URI);
    sslog_node_insert_triple(GlobalNode, type_triple);

    //object property of request - RELATESTO
    sslog_node_insert_property(GlobalNode, his_request, PROPERTY_RELATESTO, patient);
    //object property of request - HASREQUEST
    sslog_node_insert_property(GlobalNode, his, PROPERTY_HASREQUEST, his_request);

    return 0;

}

int kp_get_his_response(char* his_request_uri, char** his_response_uri, char** his_document_uri, char** his_document_type){

    sslog_individual_t *his_request = sslog_node_get_individual_by_uri(GlobalNode, his_request_uri);

    if(his_request == NULL) {
        printf(" no his_request\n");
        return -1;
    }
 
    sslog_individual_t *his_response = ( sslog_individual_t *) sslog_node_get_property(GlobalNode,his_request,PROPERTY_HASRESPONSE);
    if(his_response == NULL) {
        printf(" no his_response\n");
        return -1;
    }

    his_response_glob =  his_response;

    sslog_node_populate(GlobalNode, his_response);

    char *status;
    status = (char *) sslog_get_property(his_response, PROPERTY_STATUS);
    if (strcmp(status, "ERROR") == 0){
        printf("Error\n");
        return -1;
    }

    //TODO несколько документов
    sslog_individual_t * his_document = (sslog_individual_t *) sslog_get_property(his_response, PROPERTY_HASDOCUMENT);
    if (his_document != NULL){
        char* document_uri;
        document_uri  =  sslog_entity_get_uri (his_document);
        *his_document_uri  =  document_uri;
        char* subclass;
        get_his_subclasses( document_uri , &subclass);
        *his_document_type = subclass;
    }

    return 0;
}

int kp_init_sbcr_his_response(){

    printf("kp_init_sbcr_his_response\n");
    extern void kp_sbcr_his_request(sslog_subscription_t *);
    void (*pRequestHandler)(sslog_subscription_t *) = &kp_sbcr_his_request;

    sslog_subscription_t *sbcrRequest = NULL;
    sbcrRequest = sslog_new_subscription(GlobalNode, true);

    list_t* properties = list_new();
    list_add_data(properties, PROPERTY_HASRESPONSE);
    sslog_sbcr_add_individual(sbcrRequest, his_request_glob, properties);

    if(sbcrRequest == NULL) {
        return -1;
    }

    sslog_sbcr_set_changed_handler(sbcrRequest, pRequestHandler);

    if(sslog_sbcr_subscribe(sbcrRequest) != SSLOG_ERROR_NO) {
        return -1;
    }

printf("kp_init_sbcr_his_response end \n");

}
void kp_sbcr_his_request(sslog_subscription_t *request_sbcr){
    sslog_sbcr_changes_t *changes = sslog_sbcr_get_changes_last(request_sbcr);

    const list_t *new_response =
            sslog_sbcr_ch_get_triples(changes, SSLOG_ACTION_INSERT);

    if( new_response != NULL ){
        sslog_individual_t *his_response = (sslog_individual_t *) sslog_node_get_property(GlobalNode, his_request_glob, PROPERTY_HASRESPONSE);
        his_response_glob = his_response;
    }

}

int get_his_subclasses( char *uri, char** subclass){
    sslog_triple_t *req_triple = sslog_new_triple_detached(
            uri,
            RDF_TYPE,
            SS_RDF_SIB_ANY,
            SS_RDF_TYPE_URI, SS_RDF_TYPE_URI);

    list_t *uris = sslog_node_query_triple(GlobalNode, req_triple);
    sslog_free_triple(req_triple);
  
    list_head_t *iterator = NULL;
    char *answer_class_uri;
    list_for_each(iterator, &uris->links){
        list_t *list_node = list_entry(iterator, list_t, links);
        char *_answer_class_uri = (char *) ((sslog_triple_t*) list_node->data)->object;
        if(_answer_class_uri != NULL){
            answer_class_uri = _answer_class_uri;
            //TODO break;
            *subclass = answer_class_uri;
        }   
    }
    *subclass = answer_class_uri;
    list_free_with_nodes(uris, NULL);
}


int kp_get_his_laboratory_analysis(char* his_document_uri,
        char** createdAt, char** author,
        char** organizationName, char** hemoglobin, char** erythrocyte, char** hematocrit){


    sslog_individual_t *his_document = sslog_node_get_individual_by_uri(GlobalNode, his_document_uri);
    sslog_node_populate(GlobalNode, his_document);

    *createdAt = (char *) sslog_get_property(his_document, PROPERTY_CREATEDAT);
    *author = (char *) sslog_get_property(his_document, PROPERTY_AUTHOR);

    *organizationName = (char *) sslog_get_property(his_document, PROPERTY_ORGANIZATIONNAME);
    *hemoglobin = (char *) sslog_get_property(his_document, PROPERTY_HEMOGLOBIN);
    *erythrocyte = (char *) sslog_get_property(his_document, PROPERTY_ERYTHROCYTE);
    *hematocrit = (char *) sslog_get_property(his_document, PROPERTY_HEMATOCRIT);

}


int kp_get_his_blood_pressure_measurement(char* his_document_uri,
        char** createdAt, char** author,
        char** systolicPressure, char** diastolicPressure, char** pulse){

    sslog_individual_t *his_document = sslog_node_get_individual_by_uri(GlobalNode, his_document_uri);
    sslog_node_populate(GlobalNode, his_document);

    sslog_node_populate(GlobalNode, his_document);

    *createdAt = (char *) sslog_get_property(his_document, PROPERTY_CREATEDAT);
    *author = (char *) sslog_get_property(his_document, PROPERTY_AUTHOR);

    *systolicPressure = (char *) sslog_get_property(his_document, PROPERTY_SYSTOLICPRESSURE);
    *diastolicPressure = (char *) sslog_get_property(his_document, PROPERTY_DIASTOLICPRESSURE);
    *pulse = (char *) sslog_get_property(his_document, PROPERTY_PULSE);

}

int kp_get_his_ECG_measurement( char* his_document_uri,
        char** createdAt, char** author,
        char** dataLocation){

    sslog_individual_t *his_document = sslog_node_get_individual_by_uri(GlobalNode, his_document_uri);
    sslog_node_populate(GlobalNode, his_document);


    *createdAt = (char *) sslog_get_property(his_document, PROPERTY_CREATEDAT);
    *author = (char *) sslog_get_property(his_document, PROPERTY_AUTHOR);

    *dataLocation = (char *) sslog_get_property(his_document, PROPERTY_DATALOCATION);

}

int kp_get_his_demographic_data( char* his_document_uri,
        char** createdAt, char** author,
        char** name, char** surname, char** patronymic, char** birthDate, char** sex, char** residence, char** contactInformaiton){


    sslog_individual_t *his_document = sslog_node_get_individual_by_uri(GlobalNode, his_document_uri);
    sslog_node_populate(GlobalNode, his_document);

    *createdAt = (char *) sslog_get_property(his_document, PROPERTY_CREATEDAT);
    *author = (char *) sslog_get_property(his_document, PROPERTY_AUTHOR);

    *name = (char *) sslog_get_property(his_document, PROPERTY_NAME);
    *surname = (char *) sslog_get_property(his_document, PROPERTY_SURNAME);
    *patronymic = (char *) sslog_get_property(his_document, PROPERTY_PATRONYMIC);
    *birthDate = (char *) sslog_get_property(his_document, PROPERTY_BIRTHDATE);
    *sex = (char *) sslog_get_property(his_document, PROPERTY_SEX);
    *residence = (char *) sslog_get_property(his_document, PROPERTY_RESIDENCE);
    *contactInformaiton = (char *) sslog_get_property(his_document, PROPERTY_CONTACTINFORMATION);

}

int kp_get_his_doctor_examination(char* his_document_uri,
        char** createdAt, char** author,
        char** examinationReason, char** visitOrder, char** diagnoses, char** medications, char** smoking, char** drinking, char** height, char** weight,  char** diseasePredisposition){


    sslog_individual_t *his_document = sslog_node_get_individual_by_uri(GlobalNode, his_document_uri);
    sslog_node_populate(GlobalNode, his_document);

    *createdAt = (char *) sslog_get_property(his_document, PROPERTY_CREATEDAT);
    *author = (char *) sslog_get_property(his_document, PROPERTY_AUTHOR);

    *examinationReason = (char *) sslog_get_property(his_document, PROPERTY_EXAMINATIONREASON);
    *visitOrder = (char *) sslog_get_property(his_document, PROPERTY_VISITORDER);
    *diagnoses = (char *) sslog_get_property(his_document, PROPERTY_DIAGNOSES);
    *medications = (char *) sslog_get_property(his_document, PROPERTY_MEDICATIONS);
    *smoking = (char *) sslog_get_property(his_document, PROPERTY_SMOKING);
    *drinking = (char *) sslog_get_property(his_document, PROPERTY_DRINKING);
    *height = (char *) sslog_get_property(his_document, PROPERTY_HEIGHT);
    *weight = (char *) sslog_get_property(his_document, PROPERTY_WEIGHT);
    *diseasePredisposition = (char *) sslog_get_property(his_document, PROPERTY_DISEASEPREDISPOSITION);

}


