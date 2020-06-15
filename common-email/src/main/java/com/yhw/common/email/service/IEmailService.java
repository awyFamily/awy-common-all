package com.yhw.common.email.service;

import com.yhw.common.email.model.EmailVO;

public interface IEmailService {

    boolean sendSimpleMail(EmailVO vo);

    boolean sendAttachmentsMail(EmailVO vo);
}
