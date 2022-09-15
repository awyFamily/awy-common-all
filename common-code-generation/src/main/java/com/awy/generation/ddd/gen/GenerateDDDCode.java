package com.awy.generation.ddd.gen;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.awy.generation.constants.DDDConstant;
import com.awy.generation.constants.GenerationConstant;
import com.awy.generation.ddd.model.GenerationModel;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
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
        String poStr = getFileContent("templates/DDDPOTemplate.template");
        poStr = StrUtil.format(poStr,model.getDataGroupName(),model.getAuthor(),model.getDateStr());
        String[] replaceArr = new String[] {model.getPackagePrefix(),model.getDomain(),model.getDomainEntityName(),model.getPoName()};
        String packageName = model.getPackagePrefix() + ".domain." + model.getDomain() + ".repository.po";
        genFile(poStr,replaceArr,packageName,DDDConstant.po_suffix);
    }

    private void genDO() {
        String poStr = getFileContent("templates/DDDEntityTemplate.template");
        poStr = StrUtil.format(poStr,model.getDataGroupName(),model.getAuthor(),model.getDateStr());
        String[] replaceArr = new String[] {model.getPackagePrefix(),model.getDomain(),model.getDomainEntityName()};
        String packageName = model.getPackagePrefix() + ".domain." + model.getDomain() + ".entity";
        genFile(poStr,replaceArr,packageName, GenerationConstant.point_java);
    }

    private void genMapper() {
        String poStr = getFileContent("templates/DDDMapperTemplate.template");
        poStr = StrUtil.format(poStr,model.getAuthor(),model.getDateStr());
        String[] replaceArr = new String[] {model.getPackagePrefix(),model.getDomain(),model.getDomainEntityName(),model.getPoName()};
        String packageName = model.getPackagePrefix() + ".domain." + model.getDomain() + ".repository.mapper";
        genFile(poStr,replaceArr,packageName, GenerationConstant.mapper_suffix + GenerationConstant.point_java);
    }

    private void genRepository() {
        String poStr = getFileContent("templates/DDDRepositoryTemplate.template");
        poStr = StrUtil.format(poStr,model.getDataGroupName(),model.getAuthor(),model.getDateStr());
        String[] replaceArr = new String[] {model.getPackagePrefix(),model.getDomain(),model.getDomainEntityName(),model.getPoName()};
        String packageName = model.getPackagePrefix() + ".domain." + model.getDomain() + ".repository.facade";
        genFile(poStr,replaceArr,packageName, DDDConstant.repository_suffix);
    }

    private void genRepositoryImpl() {
        String poStr = getFileContent("templates/DDDRepositoryImplTemplate.template");
        poStr = StrUtil.format(poStr,model.getDataGroupName(),model.getAuthor(),model.getDateStr());
        String[] replaceArr = new String[] {model.getPackagePrefix(),model.getDomain(),model.getDomainEntityName(),model.getPoName()};
        String packageName = model.getPackagePrefix() + ".domain." + model.getDomain() + ".repository.persistence";
        genFile(poStr,replaceArr,packageName, DDDConstant.repository_impl_suffix);
    }

    private void genFactory() {
        String poStr = getFileContent("templates/DDDFactoryTemplate.template");
        poStr = StrUtil.format(poStr,model.getDataGroupName(),model.getAuthor(),model.getDateStr());
        String[] replaceArr = new String[] {model.getPackagePrefix(),model.getDomain(),model.getDomainEntityName(),model.getPoName()};
        String packageName = model.getPackagePrefix() + ".domain." + model.getDomain() + ".service";
        genFile(poStr,replaceArr,packageName, DDDConstant.factory_suffix);
    }

    private void genDomainService() {
        String poStr = getFileContent("templates/DDDDomainServiceTemplate.template");
        poStr = StrUtil.format(poStr,model.getDataGroupName(),model.getAuthor(),model.getDateStr());
        String[] replaceArr = new String[] {model.getPackagePrefix(),model.getDomain(),model.getDomainEntityName(),model.getPoName(),model.getBaseDomainServiceVersion()};
        String packageName = model.getPackagePrefix() + ".domain." + model.getDomain() + ".service";
        genFile(poStr,replaceArr,packageName, DDDConstant.domain_service_suffix);
    }

    private void genApplicationService() {
        String poStr = getFileContent("templates/DDDApplicationServiceTemplate.template");
        poStr = StrUtil.format(poStr,model.getAuthor(),model.getDateStr());
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

        String packagePath =  getAbsolutePath();

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

    private String getFileContent(String relativePath) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(relativePath);
        return IoUtil.read(inputStream, Charset.forName("utf-8"));
    }

    private String getAbsolutePath() {
        return getArtifactPath() + File.separatorChar  + "src" +File.separatorChar + "main" + File.separatorChar + "java"+ File.separatorChar;
    }

    private String artifactPath;

    public String getArtifactPath() {
        if (StrUtil.isBlank(this.artifactPath)) {
            this.artifactPath = getPollExpectFilePath(new File(System.getProperty("user.dir")), model.getArtifactId());
            Assert.isFalse(StrUtil.isBlank(this.artifactPath),"没有找到模块:{}",model.getArtifactId());
        }
        return this.artifactPath;
    }

    private  String getPollExpectFilePath(File file, String expectFileName) {
        String relativePath = getExpectFilePath(file, expectFileName);
        if (StrUtil.isBlank(relativePath)) {
            String[] childrenFileList = file.list();
            File inner;
            //第二次循环
            for (String filename : childrenFileList) {
                inner = new File(file.getPath() + File.separatorChar + filename);
                if (!inner.isDirectory()) {
                    continue;
                }
                relativePath = getExpectFilePath(inner, expectFileName);
                if (StrUtil.isNotBlank(relativePath)) {
                    return relativePath;
                }
                //第三次循环
                String[] childrenInnerFileList = inner.list();
                for (String innerFilename : childrenInnerFileList) {
                    relativePath = getExpectFilePath(new File(inner.getPath() + File.separatorChar + innerFilename), expectFileName);
                    if (StrUtil.isNotBlank(relativePath)) {
                        return relativePath;
                    }
                }
            }
        }
        return relativePath;
    }

    private  String getExpectFilePath(File file,String expectFileName) {
        if (file != null && file.isDirectory()) {
            String path = file.getPath();
            String[] childrenFileList = file.list();

            for (String filename : childrenFileList) {
                if(expectFileName.equals(filename)) {
                    return path + File.separatorChar + filename;
                }
            }
        }
        return "";
    }
}
