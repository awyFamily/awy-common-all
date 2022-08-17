package com.awy.generation.ddd.gen;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.awy.generation.constants.DDDConstant;
import com.awy.generation.constants.GenerationConstant;
import com.awy.generation.ddd.model.GenerationModel;

import java.io.File;
import java.util.List;

/**
 * @author yhw
 * @date 2022-08-17
 */
public class GenerateDDDCode {

    private GenerationModel model;

    private boolean genDO = true;
    private boolean genFactory = true;
    private boolean genDomainService = true;
    private boolean genApplicationService = true;

    private GenerateDDDCode(){}

    public static GenerateDDDCode create(String dataGroupName, String artifactId, String domain) {
        return create(dataGroupName,artifactId,domain,"");
    }
    public static GenerateDDDCode create(String dataGroupName, String artifactId, String domain,String baseDomainServiceVersion) {
        return create(dataGroupName,"",artifactId,"",domain,baseDomainServiceVersion);
    }

    public static GenerateDDDCode create(String dataGroupName, String author, String artifactId, String packagePrefix, String domain,String baseDomainServiceVersion) {
        return new GenerateDDDCode(new GenerationModel(dataGroupName,author,artifactId,packagePrefix,domain,baseDomainServiceVersion));
    }

    public GenerateDDDCode(GenerationModel model) {
        this.model = model;
    }

    public GenerateDDDCode genDO(boolean genDO) {
        this.genDO = genDO;
        return this;
    }

    public GenerateDDDCode genFactory(boolean genFactory) {
        this.genFactory = genFactory;
        return this;
    }

    public GenerateDDDCode genDomainService(boolean genDomainService) {
        this.genDomainService = genDomainService;
        return this;
    }

    public GenerateDDDCode genApplicationService(boolean genApplicationService) {
        this.genDO = genApplicationService;
        return this;
    }

    public void run() {
        Assert.isFalse(model == null,"配置不能为空");
        Assert.isFalse(StrUtil.isBlank(model.getDomain()),"领域名称不能为空");
        this.genPO();
        this.genMapper();
        this.genRepository();
        this.genRepositoryImpl();
        if (genDO) {
            this.genDO();
        }
        if (genFactory) {
            this.genFactory();
        }
        if (genDomainService) {
            this.genDomainService();
        }
        if (genApplicationService) {
            this.genApplicationService();
        }
    }

    private void genPO() {
        FileReader reader = new FileReader("templates/DDDPOTemplate.template");
        String poStr = StrUtil.format(reader.readString(),model.getDataGroupName(),model.getAuthor(),model.getDateStr());
        String[] replaceArr = new String[] {model.getPackagePrefix(),model.getDomain(),model.getDomainEntityName(),model.getPoName()};
        String packageName = model.getPackagePrefix() + ".domain." + model.getDomain() + ".repository.po";
        genFile(poStr,replaceArr,packageName,DDDConstant.po_suffix);
    }

    private void genDO() {
        FileReader reader = new FileReader("templates/DDDEntityTemplate.template");
        String poStr = StrUtil.format(reader.readString(),model.getDataGroupName(),model.getAuthor(),model.getDateStr());
        String[] replaceArr = new String[] {model.getPackagePrefix(),model.getDomain(),model.getDomainEntityName()};
        String packageName = model.getPackagePrefix() + ".domain." + model.getDomain() + ".entity";
        genFile(poStr,replaceArr,packageName, GenerationConstant.point_java);
    }

    private void genMapper() {
        FileReader reader = new FileReader("templates/DDDMapperTemplate.template");
        String poStr = StrUtil.format(reader.readString(),model.getAuthor(),model.getDateStr());
        String[] replaceArr = new String[] {model.getPackagePrefix(),model.getDomain(),model.getDomainEntityName(),model.getPoName()};
        String packageName = model.getPackagePrefix() + ".domain." + model.getDomain() + ".repository.mapper";
        genFile(poStr,replaceArr,packageName, GenerationConstant.mapper_suffix + GenerationConstant.point_java);
    }

    private void genRepository() {
        FileReader reader = new FileReader("templates/DDDRepositoryTemplate.template");
        String poStr = StrUtil.format(reader.readString(),model.getDataGroupName(),model.getAuthor(),model.getDateStr());
        String[] replaceArr = new String[] {model.getPackagePrefix(),model.getDomain(),model.getDomainEntityName(),model.getPoName()};
        String packageName = model.getPackagePrefix() + ".domain." + model.getDomain() + ".repository.facade";
        genFile(poStr,replaceArr,packageName, DDDConstant.repository_suffix);
    }

    private void genRepositoryImpl() {
        FileReader reader = new FileReader("templates/DDDRepositoryImplTemplate.template");
        String poStr = StrUtil.format(reader.readString(),model.getDataGroupName(),model.getAuthor(),model.getDateStr());
        String[] replaceArr = new String[] {model.getPackagePrefix(),model.getDomain(),model.getDomainEntityName(),model.getPoName()};
        String packageName = model.getPackagePrefix() + ".domain." + model.getDomain() + ".repository.persistence";
        genFile(poStr,replaceArr,packageName, DDDConstant.repository_impl_suffix);
    }

    private void genFactory() {
        FileReader reader = new FileReader("templates/DDDFactoryTemplate.template");
        String poStr = StrUtil.format(reader.readString(),model.getDataGroupName(),model.getAuthor(),model.getDateStr());
        String[] replaceArr = new String[] {model.getPackagePrefix(),model.getDomain(),model.getDomainEntityName(),model.getPoName()};
        String packageName = model.getPackagePrefix() + ".domain." + model.getDomain() + ".service";
        genFile(poStr,replaceArr,packageName, DDDConstant.factory_suffix);
    }

    private void genDomainService() {
        FileReader reader = new FileReader("templates/DDDDomainServiceTemplate.template");
        String poStr = StrUtil.format(reader.readString(),model.getDataGroupName(),model.getAuthor(),model.getDateStr());
        String[] replaceArr = new String[] {model.getPackagePrefix(),model.getDomain(),model.getDomainEntityName(),model.getPoName(),model.getBaseDomainServiceVersion()};
        String packageName = model.getPackagePrefix() + ".domain." + model.getDomain() + ".service";
        genFile(poStr,replaceArr,packageName, DDDConstant.domain_service_suffix);
    }

    private void genApplicationService() {
        FileReader reader = new FileReader("templates/DDDApplicationServiceTemplate.template");
        String poStr = StrUtil.format(reader.readString(),model.getAuthor(),model.getDateStr());
        String[] replaceArr = new String[] {model.getPackagePrefix(),model.getDomain(),model.getDomainEntityName(),model.toLowerCaseByFirstChar(model.getDomainEntityName())};
        String packageName = model.getPackagePrefix() + ".application.service";
        genFile(poStr,replaceArr,packageName, DDDConstant.application_service_suffix);
    }

    private void genFile(String fileContentStr,String[] replaceArr,String packageName,String fileSuffix) {
        if (replaceArr != null && replaceArr.length > 0) {
            for (int i = 0; i < replaceArr.length; i++) {
                fileContentStr = fileContentStr.replaceAll("\\{" + i + "}", replaceArr[i]);
            }
        }

        String packagePath =  getRelativePath();

        List<String> split = StrUtil.split(packageName, ".");
        for (String s : split) {
            packagePath += s + File.separatorChar;
        }

        File file = new File(packagePath + model.getDomainEntityName() + fileSuffix);
        if (!file.exists()) {
            FileWriter fileWriter = FileWriter.create(file);
            fileWriter.write(fileContentStr);
        }
    }

    private String getRelativePath() {
        return System.getProperty("user.dir")  +  File.separatorChar + model.getArtifactId() + File.separatorChar  + "src" +File.separatorChar + "main" + File.separatorChar + "java"+ File.separatorChar;
    }
}
