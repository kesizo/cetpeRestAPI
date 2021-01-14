package com.kesizo.cetpe.backend.restapi.email.service;

import com.kesizo.cetpe.backend.restapi.email.model.EmailBody;

public interface EmailService {
        boolean sendEmail(EmailBody emailBody);
}
