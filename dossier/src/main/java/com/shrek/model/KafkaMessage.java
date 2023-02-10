package com.shrek.model;


import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class KafkaMessage {

    private String address;

    private MessageTheme theme;

    private Long applicationId;
}