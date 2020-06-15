package com.yhw.common.email.service.impl;

import com.yhw.common.email.model.EmailVO;
import com.yhw.common.email.service.IEmailService;
import com.yhw.common.email.util.FreemarkerUtil;
import freemarker.template.Configuration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.Assert;

import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

/**
 * @author yhw
 */
@Slf4j
@AllArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private final Configuration cfg;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String address;



    @Override
    public boolean sendSimpleMail(EmailVO vo){
        SimpleMailMessage message = (SimpleMailMessage)getMessage(vo);
        mailSender.send(message);
        log.info("邮件发送成功");
        return true;

    }


    @Override
    public boolean sendAttachmentsMail(EmailVO vo){
            MimeMessage mimeMessage = (MimeMessage)getMessage(vo);;
            mailSender.send(mimeMessage);
            log.info("邮件发送成功");
            return true;

    }

    /**
     * 获取发送邮件消息对象
     * @param vo 邮件VO
     * @return 消息对象
     */
    private Object getMessage(EmailVO vo){
        Object result;
        switch (vo.getSendTypeEnum()){
            case TEXT_SEND:
                result = getSimpleMailMessage(vo);
                break;
            case TEMPLATE_SEND:
                MimeMessage message = getMimeMessage(vo);
                Assert.notNull(message,"MimeMessage对象出错,请联系管理员！");
                result = message;
                break;
            default:
                result = null;
                break;
        }
        return result;
    }

    private MimeMessage getMimeMessage(EmailVO vo){
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(address);
            //附件
            String[] arr;
            if(vo.getAttachments() != null && !vo.getAttachments().isBlank()){
                arr = vo.getAttachments().replaceAll("，",",").split(",");
                String[] attachmentNames = vo.getAttachmentNames().replaceAll("，",",").split(",");
                int size = arr.length;
                Assert.isTrue(size == attachmentNames.length,"附件名称,附件地址长度不一致");
                FileSystemResource file;
                try{
                    for(int i = 0; i < size; i++){
                        helper.addAttachment(attachmentNames[i],(InputStreamSource) uploadFile(arr[i]));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            //接收人
            arr = vo.getAddressees().replaceAll("，",",").split(",");
            isEmail(arr,"接收人");
            if(arr.length == 1){
                helper.setTo(vo.getAddressees());
            }else {
                helper.setTo(arr);
            }
            //抄送人
            if(vo.getCcs() != null && !vo.getCcs().isBlank()){
                arr = vo.getCcs().replaceAll("，",",").split(",");
                isEmail(arr,"抄送人");
                if(arr.length == 1){
                    helper.setCc(vo.getCcs());
                }else {
                    helper.setCc(arr);
                }
            }

            helper.setSubject(vo.getSubject());

            if(vo.getText() != null && !vo.getText().isBlank()){
                Assert.notNull(vo.getEmailFtlEnum(),"使用模板模式,模板类型不能为空");
                Assert.notNull(vo.getData(),"使用模板模式,data不能为空");
                helper.setText(FreemarkerUtil.getFreemarkerDealText(cfg,vo.getEmailFtlEnum().getFilePath(),vo.getData()), true);
            }else{
                helper.setText(vo.getText());
            }
        }catch (Exception e){
            log.error("",e);
            return null;
        }
        return mimeMessage;
    }

    private SimpleMailMessage getSimpleMailMessage(EmailVO vo){
        SimpleMailMessage message = new SimpleMailMessage();
        String[] arr = vo.getAddressees().replaceAll("，",",").split(",");
        isEmail(arr,"接收人");
        if(arr.length == 1){
            message.setTo(vo.getAddressees());
        }else {
            message.setTo(arr);
        }
        if(vo.getCcs() != null && !vo.getCcs().isBlank()){
            arr = vo.getCcs().replaceAll("，",",").split(",");
            isEmail(arr,"抄送人");
            if(arr.length == 1){
                message.setCc(vo.getCcs());
            }else {
                message.setCc(arr);
            }
        }
        message.setSubject(vo.getSubject());
        message.setText(vo.getText());
        message.setFrom(address);
        return message;
    }

    private void isEmail(String[] arr,String type){
        if(arr!=null){
            int size = arr.length;
            for(int i=0;i<size;i++){
                Assert.isTrue(isEmail(arr[i]),arr[i]+","+type+"邮箱格式非法！");
            }
        }
    }

    private boolean isEmail(String email){
        return Pattern.matches("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$", email);
    }

    private InputStream uploadFile(String path){
        try {
            URL url = new URL(path);
            URLConnection conn = url.openConnection();
            //设置超时时间
            conn.setConnectTimeout(60*1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            return conn.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("upload timeOut or file not exists",e);
        }
    }
}
