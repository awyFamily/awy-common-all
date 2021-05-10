package com.awy.common.mongo.component;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import com.awy.common.mongo.model.MIndexEnum;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * @author yhw
 */
@Component
public class MongoComponent {

    @Resource
    MongoTemplate mongoTemplate;

    public MongoCollection<Document> getCollection(String collectionName){
        if(!mongoTemplate.collectionExists(collectionName)){
            return null;
        }
        return mongoTemplate.getCollection(collectionName);
    }

    public void createCollection(String collectionName){
        if(!mongoTemplate.collectionExists(collectionName)){
            mongoTemplate.createCollection(collectionName);
        }
    }

    /**
     * 删除集合
     * @param collectionName 集合名称
     */
    public void dropCollection(String collectionName){
        if(mongoTemplate.collectionExists(collectionName)){
            mongoTemplate.getCollection(collectionName).drop();
        }
    }

    public void createIndex(String collectionName, String indexKey){
        this.createIndex(collectionName,indexKey,MIndexEnum.asc,indexKey);
    }

    public void createIndex(String collectionName, String indexKey, MIndexEnum indexEnum){
        this.createIndex(collectionName,indexKey,indexEnum,indexKey);
    }

    public void createIndex(String collectionName, String indexKey, MIndexEnum indexEnum, String indexName){
        MongoCollection<Document> collection = getCollection(collectionName);
        if(collection != null){
            mongoTemplate.indexOps(collectionName).ensureIndex(indexEnum.createIndex(indexKey,indexName));
//            collection.createIndex(new Document(indexKey,indexEnum.getName())
//                    ,new IndexOptions().background(false).name(indexName));
        }
    }

    public <T> void insert(T object, String collectionName){
        mongoTemplate.insert(object,collectionName);
    }

    public <T> void insert(Collection<T> list, String collectionName){
        mongoTemplate.insert(list,collectionName);
    }

    public <T> void save(T object, String collectionName){
        mongoTemplate.save(object,collectionName);
    }

    public <T> void save(Collection<T> list, String collectionName){
        mongoTemplate.save(list,collectionName);
    }


    /**
     * CriteriaDefinition criteria = Criteria.where("column").is("parameter");//.and("createdAt").gt(DateUtil.parse("2019-10-05"))
     * Query query = Query.query(criteria);
     * @param query
     * @param collectionName
     * @return
     */
    public List<JSONObject> find(Query query, String collectionName){
//        mongoTemplate.remove(new Query(),"");//删除
        return mongoTemplate.find(query, JSONObject.class,collectionName);
    }

    /**
     * 分页查询
     * @param query
     * @param request
     * @param entityClass
     * @param collectionName
     * @param <T>
     * @return
     */
    public <T>  IPage<T>  getPage(Query query, PageRequest request,Class<T> entityClass, String collectionName){
        int count = (int) mongoTemplate.count(query, entityClass, collectionName);

        query.with(request);
        List<T> list = mongoTemplate.find(query, entityClass, collectionName);
        IPage<T> result = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(request.getPageNumber(), request.getPageSize(),count);
        if(CollUtil.isNotEmpty(list)){
            result.setRecords(list);
            return result;
        }
        return result;
    }
}
