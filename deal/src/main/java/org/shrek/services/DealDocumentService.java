package org.shrek.services;

public interface DealDocumentService {


    void sendDocuments(Long applicationId);

    void signDocuments(Long applicationId);

    void sendCode(Long applicationId, Integer sesCode);


}
