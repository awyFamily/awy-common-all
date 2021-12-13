package com.awy.common.mongo.model;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;

/**
 * @author yhw
 */
public enum MIndexEnum {

    asc("1"),
    desc("-1"),
    geoHaystack("geoHaystack"),
    hashed("hashed"),
    text("text"),
    ;

    private String name;

    public String getName(){
        return this.name;
    }

    MIndexEnum(String name){
        this.name = name;
    }

    public IndexDefinition createIndex(String indexKey, String indexName){
        IndexDefinition index;
        switch (this){
            case asc:
                index = new Index(indexKey, Sort.Direction.ASC).named(indexName);
                break;
            case desc:
                index = new Index(indexKey, Sort.Direction.DESC).named(indexName);
                break;
            case geoHaystack:
                index = new GeospatialIndex(indexKey).named(indexName);
                break;
//            case hashed:
//                index = HashedIndex.hashed(indexKey);
//                break;
            default:
                index = TextIndexDefinition.builder().onField(indexKey).named(indexName).build();
                break;
        }
        return index;
    }

    public IndexDefinition creatMultiFieldTextIndex(String indexName, String... indexKeys){
        if(this != MIndexEnum.text){
            throw new RuntimeException("must be of type text index");
        }
        if(indexKeys == null || indexKeys.length == 0){
            throw new RuntimeException("indexKeys isEmpty");
        }
        return TextIndexDefinition.builder().onFields(indexKeys).named(indexName).build();
    }

}
