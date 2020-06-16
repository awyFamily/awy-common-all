package com.awy.common.email.service;

import com.awy.common.email.model.EmailVO;

public interface IEmailService {

    boolean sendSimpleMail(EmailVO vo);

    boolean sendAttachmentsMail(EmailVO vo);
}
