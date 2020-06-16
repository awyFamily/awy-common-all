package com.awy.common.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Map;

public class MongoClientTest {

    /*public static void main(String[] args) {
        String database = "myTest";
        String username = "test";
        String password = "test";
        MongoCredential credential =  MongoCredential.createCredential(username,database,password.toCharArray());//验证对象
        MongoClientOptions options =  MongoClientOptions.builder().sslEnabled(false).build();//连接操作对象
        MongoClient client = new MongoClient(new ServerAddress("localhost",6618),credential,options);//连接对象

        //构造mongoTemplate
        MongoTemplate mongoTemplate = new  MongoTemplate(client,database);
        System.out.println(mongoTemplate);
        MongoCollection<Document> iot_data_detail = mongoTemplate.getCollection("iot_data_detail");
        System.out.println(iot_data_detail.find().first());
        Document first = iot_data_detail.find().first();

        MongoCursor<Document> iterator = iot_data_detail.find().iterator();
        Document next;
        while (iterator.hasNext()){
            next = iterator.next();
        }
        first.toJson();



    }*/



    public MongoTemplate getMongoTemplate(String collectionName){
        //创建集合
        /*if(!mongoTemplate.collectionExists(collectionName)){
            mongoTemplate.createCollection(collectionName);
        }*/
        return null;
    }


    public static BsonDocument getQuerySn(String obj){
        //简单查询
        BsonDocument bsonDocument = new BsonDocument();
        bsonDocument.append("sn",new BsonString(obj));
//        bsonDocument.append("sn")
        return bsonDocument;
    }

    public static void main(String[] args) {
        String database = "";
        String username = "";
        String password = "";
        String host = "";
        Integer port = 3717;

        MongoCredential credential = MongoCredential.createCredential(username,database,password.toCharArray());
        MongoClientOptions options = MongoClientOptions.builder().sslEnabled(false).build();
        MongoClient client = new MongoClient(new ServerAddress(host,port),credential,options);
        MongoTemplate mongoTemplate = new MongoTemplate(client,database);

        //("sn").is("TDMQ20190422008").and

//        { "find" : "iot_data_detail", "filter" : { "sn" : "YBF30470000000002", "$and" : [{ "createdAt" : { "$gt" : { "$date" : "2019-10-04T16:00:00Z" } } }] } }
//        CriteriaDefinition criteria = Criteria.where("sn").is("YBF30470000000002").andOperator(Criteria.where("createdAt").gt(DateUtil.parse("2019-10-05")));

        //{ "find" : "iot_data_detail", "filter" : { "sn" : "YBF30470000000002", "createdAt" : { "$gt" : { "$date" : "2019-10-04T16:00:00Z" } } } }
        CriteriaDefinition criteria = Criteria.where("sn").is("16061840");//.and("createdAt").gt(DateUtil.parse("2019-10-05"))
        Query query = Query.query(criteria);

        List<Map> iotDataDetails = mongoTemplate.find(query, Map.class, "iot_data_detail");

        /*for (IotDataDetail detail : iotDataDetails) {
            System.out.println(detail);
        }*/
        System.out.println(iotDataDetails.size());
        System.out.println(iotDataDetails.get(0));
//        documents = iot_data_detail.find(getQuerySn(sn));

    }
}
