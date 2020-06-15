package com.yhw.common.email.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 邮件VO
 * @author yhw
 */
@NoArgsConstructor
@Data
public class EmailVO{


    /**
     * 主题名称
     */
    private String subject;

    /**
     * 邮件内容
     */
    private String text;

    /**
     * 文本发送类型 1=文本方式 2=附件,模板方式
     */
    private EmailSendTypeEnum sendTypeEnum;

    public void setSendTypeEnum(Integer type){
        this.sendTypeEnum = EmailSendTypeEnum.getType(type);
    }


    /**
     * 用于拓展html模板
     */
    private EmailFtlEnum emailFtlEnum;

    public void setEmailFtlEnum(Integer ftlEnumCode){
        this.emailFtlEnum = EmailFtlEnum.getCode(ftlEnumCode);
    }

    /**
     * 模板填充数据
     */
    private Object data;

    /**
     * 收件人，多个请用英文逗号拼接
     */
    private String addressees;

    /**
     * 抄送人，多个请用英文逗号拼接
     */
    private String ccs;

    /**
     * 附件地址，多个请用英文逗号拼接
     */
    private String attachments;

    /**
     * 附件名称，多个请用英文逗号拼接
     */
    private String attachmentNames;

    public EmailVO(String subject, String text, String addressees){
        this(subject,text,addressees,null);
    }

    public EmailVO(String subject, String text, String addressees, String ccs){
       this(subject,text,addressees,ccs,null,null);
    }

    public EmailVO(String subject, String text, String addressees, String attachments, String attachmentNames){
        this(subject,text,addressees,null,attachments,attachmentNames);
    }

    public EmailVO(String subject, String text, String addressees, String ccs, String attachments, String attachmentNames){
        this.subject = subject;
        this.text = text;
        this.addressees = addressees;
        this.ccs = ccs;
        this.attachments = attachments;
        this.attachmentNames = attachmentNames;
    }

}
