package org.shrek.services;

import com.shrek.model.EmailMessageDTO;

public interface DossierService {
    void sendMessage(EmailMessageDTO message);

    String evaluateTopic(EmailMessageDTO.ThemeEnum theme);
}
