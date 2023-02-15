package com.shrek.servise;



@Service
@Slf4j
@RequiredArgsConstructor
public class MessageService {

    @Value("${custom.message.finish-registration.subject}")
    private String FINISH_REGISTRATION_SUBJECT;
    @Value("${custom.message.finish-registration.text}")
    private String FINISH_REGISTRATION_TEXT;
    @Value("${custom.message.create-document.subject}")
    private String CREATE_DOCUMENT_SUBJECT;
    @Value("${custom.message.create-document.text}")
    private String CREATE_DOCUMENT_TEXT;
    @Value("${custom.message.send-document.subject}")
    private String SEND_DOCUMENT_SUBJECT;
    @Value("${custom.message.send-document.text}")
    private String SEND_DOCUMENT_TEXT;
    @Value("${custom.message.send-ses.subject}")
    private String SEND_SES_SUBJECT;
    @Value("${custom.message.send-ses.text}")
    private String SEND_SES_TEXT;
    @Value("${custom.message.credit-issued.subject}")
    private String CREDIT_ISSUED_SUBJECT;
    @Value("${custom.message.credit-issued.text}")
    private String CREDIT_ISSUED_TEXT;
    @Value("${custom.message.application-denied.subject}")
    private String APPLICATION_DENIED_SUBJECT;
    @Value("${custom.message.application-denied.text}")
    private String APPLICATION_DENIED_TEXT;

