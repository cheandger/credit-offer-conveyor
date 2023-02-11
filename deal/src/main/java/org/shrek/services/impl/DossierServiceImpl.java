package org.shrek.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shrek.model.EmailMessageDTO;
import lombok.RequiredArgsConstructor;
import org.shrek.services.DossierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service

@RequiredArgsConstructor
public class DossierServiceImpl implements DossierService {

    private static final Logger log = LoggerFactory.getLogger(DealServiceImpl.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Value("Your loan application approved! Now you should finish registration")
    private String FINISH_REGISTRATION_TOPIC;

    @Value(" Your loan application has passed the validation! You should complete the document request")
    private String CREATE_DOCUMENT_TOPIC;

    @Value("This documents should be signed. The signed  documents request needed")
    private String SEND_DOCUMENT_TOPIC;

    @Value("This is your Simple Electric Sign code for application")
    private String SEND_SES_TOPIC;

    @Value("Your Loan request was was approved.")
    private String CREDIT_ISSUED_TOPIC;

    @Value("Your loan request was denied")
    private String APPLICATION_DENIED_TOPIC;

    @Override
    public void sendMessage(EmailMessageDTO message) {
        String topic = evaluateTopic(message.getTheme());
        String jsonMessage;

        try {
            jsonMessage = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Cant map the json" + ex);
        }

        log.info("Sending message \"{}\" to kafka topic \"{}\"", jsonMessage, topic);
        kafkaTemplate.send(topic, jsonMessage);


    }


    public String evaluateTopic(EmailMessageDTO.ThemeEnum theme) {
        String topic = null;

        switch (theme) {
            case FINISH_REGISTRATION -> {
                topic = FINISH_REGISTRATION_TOPIC;
            }
            case CREATE_DOCUMENT -> {
                topic = CREATE_DOCUMENT_TOPIC;
            }
            case SEND_DOCUMENT -> {
                topic = SEND_DOCUMENT_TOPIC;
            }
            case SEND_SES -> {
                topic = SEND_SES_TOPIC;
            }
            case CREDIT_ISSUED -> {
                topic = CREDIT_ISSUED_TOPIC;
            }
            case APPLICATION_DENIED -> {
                topic = APPLICATION_DENIED_TOPIC;
            }
        }
        return topic;
    }


}