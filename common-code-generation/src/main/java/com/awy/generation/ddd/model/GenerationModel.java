package com.awy.generation.ddd.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author yhw
 * @date 2022-08-17
 */
@NoArgsConstructor
@Data
public class GenerationModel {

    private String dataGroupName;

    private String author;

    private String dateStr;

    public GenerationModel(String dataGroupName, String author, String artifactId, String packagePrefix, String domain,String baseDomainServiceVersion) {
        this.dataGroupName = dataGroupName;
        this.author = author;
        this.artifactId = artifactId;
        this.packagePrefix = packagePrefix;
        this.domain = domain;
        this.baseDomainServiceVersion = baseDomainServiceVersion;
    }

    public String getDataGroupName() {
        if (StrUtil.isBlank(this.dataGroupName)) {
            return "com.awy";
        }
        return this.dataGroupName;
    }

    public String getAuthor() {
        if (StrUtil.isBlank(this.author)) {
            return System.getenv("USERNAME");
        }
        return this.author;
    }

    public String getDateStr() {
        if (StrUtil.isBlank(this.dateStr)) {
            LocalDateTime now = LocalDateTime.now();
            return now.getYear() + "-" + now.getMonth().getValue() + "-" + now.getDayOfMonth();
        }
        return this.dateStr;
    }


    private String artifactId;

    private String packagePrefix;

    public String getPackagePrefix() {
        if (StrUtil.isBlank(this.packagePrefix)) {
            return this.artifactId.replaceAll("_",".").replaceAll("-",".");
        }
        return this.packagePrefix;
    }

    /**
     * 领域名称
     */
    private String domain;

    /**
     * DomainEntity 名称
     */
    private String domainEntityName;

    public String getDomainEntityName() {
        if (StrUtil.isBlank(this.domainEntityName)) {
            return toUpperCaseByFirstChar(this.domain);
        }
        return this.domainEntityName;
    }

    private String poName;

    public String getPoName() {
        if (StrUtil.isBlank(this.poName)) {
            return getDomainEntityName() + "PO";
        }
        return this.poName;
    }

    private String baseDomainServiceVersion;

    public String getBaseDomainServiceVersion() {
        if (StrUtil.isBlank(this.baseDomainServiceVersion)) {
            return "";
        }
        return this.baseDomainServiceVersion;
    }

    public String toLowerCaseByFirstChar(String str) {
        return StrUtil.replace(str,0,1,str.substring(0,1).toLowerCase());
    }

    public String toUpperCaseByFirstChar(String str) {
        return StrUtil.replace(str,0,1,str.substring(0,1).toUpperCase());
    }
}
