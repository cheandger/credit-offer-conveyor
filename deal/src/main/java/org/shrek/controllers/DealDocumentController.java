package org.shrek.controllers;


import com.shrek.controller.DealDocumentServiceApi;
import org.shrek.services.DealDocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DealDocumentController implements DealDocumentServiceApi {
    DealDocumentService dealDocumentService;

    public DealDocumentController(DealDocumentService dealDocumentService) {
        this.dealDocumentService = dealDocumentService;
    }



    @Override
    public ResponseEntity<Void> sendCode(Long applicationId, String body) {
        return DealDocumentService.(applicationId, body);
    }

    @Override
    public ResponseEntity<Void> sendDocuments(Long applicationId) {
        return DealDocumentServiceApi.super.sendDocuments(applicationId);
    }

    @Override
    public ResponseEntity<Void> signDocuments(Long applicationId) {
        return DealDocumentServiceApi.super.signDocuments(applicationId);
    }

