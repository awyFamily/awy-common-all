package com.awy.generation.constants;

/**
 * @author yhw
 * @date 2022-08-17
 */
public interface DDDConstant {

    String po = "po";

    String po_suffix = "PO"  + GenerationConstant.point_java;

    String repository_suffix = "Repository" + GenerationConstant.point_java;

    String repository_impl_suffix = "RepositoryImpl" + GenerationConstant.point_java;

    /**
     * domain.repository.facade
     */
    String facade = "facade";

    /**
     * domain.repository.persistence
     */
    String persistence = "persistence";


    String factory_suffix = "Factory"  + GenerationConstant.point_java;

    /**
     * application.
     */
    String application = "application";

    String domain_service_suffix = "DomainService" + GenerationConstant.point_java;

    String application_service_suffix = "ApplicationService" + GenerationConstant.point_java;

    String api_suffix = "Api" + GenerationConstant.point_java;

    String assembler_suffix = "Assembler" + GenerationConstant.point_java;
}
