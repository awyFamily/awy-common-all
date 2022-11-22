//package com.awy.common.mongo;
//
//import com.mongodb.MongoClient;
//import com.mongodb.MongoClientOptions;
//import com.mongodb.MongoCredential;
//import com.mongodb.ServerAddress;
//import org.bson.BsonDocument;
//import org.bson.BsonString;
//import org.springframework.data.mongodb.core.MongoTemplate;
////import org.springframework.data.mongodb.core.query.Criteria;
////import org.springframework.data.mongodb.core.query.CriteriaDefinition;
////import org.springframework.data.mongodb.core.query.Query;
//
//import java.util.List;
//import java.util.Map;
//
//public class MongoClientTest {
//
//
//
//    public static BsonDocument getQuerySn(String obj){
//        //简单查询
//        BsonDocument bsonDocument = new BsonDocument();
//        bsonDocument.append("sn",new BsonString(obj));
////        bsonDocument.append("sn")
//        return bsonDocument;
//    }
//
//    private static MongoTemplate getMongoTemplate(String host,Integer port,String database,String username,String password){
//        MongoCredential credential = MongoCredential.createCredential(username,database,password.toCharArray());
//        MongoClientOptions options = MongoClientOptions.builder().sslEnabled(false).build();
//        MongoClient client = new MongoClient(new ServerAddress(host,port),credential,options);
//        return new MongoTemplate(client,database);
//    }
//
///*    public static void main(String[] args) {
//        MongoTemplate mongoTemplate = getMongoTemplate("127.0.0.1",27017,"dev_base_cloud","dev_cloud","dev_cloud");
//
//        //("sn").is("TDMQ20190422008").and
//
////        { "find" : "iot_data_detail", "filter" : { "sn" : "YBF30470000000002", "$and" : [{ "createdAt" : { "$gt" : { "$date" : "2019-10-04T16:00:00Z" } } }] } }
////        CriteriaDefinition criteria = Criteria.where("sn").is("YBF30470000000002").andOperator(Criteria.where("createdAt").gt(DateUtil.parse("2019-10-05")));
//
//        //{ "find" : "iot_data_detail", "filter" : { "sn" : "YBF30470000000002", "createdAt" : { "$gt" : { "$date" : "2019-10-04T16:00:00Z" } } } }
//        CriteriaDefinition criteria = Criteria.where("sn").is("16061840");//.and("createdAt").gt(DateUtil.parse("2019-10-05"))
//        Query query = Query.query(criteria);
//
//        //List<Map> iotDataDetails = mongoTemplate.find(query, Map.class, "yy_planting_standard_calendar");
//        List<PlantingStandardCalendarPO> plantingStandardCalendars = mongoTemplate.findAll(PlantingStandardCalendarPO.class, "yy_planting_standard_calendar");
//
//        *//*for (IotDataDetail detail : iotDataDetails) {
//            System.out.println(detail);
//        }*//*
//        System.out.println(plantingStandardCalendars.size());
//        System.out.println(plantingStandardCalendars.get(0));
////        documents = iot_data_detail.find(getQuerySn(sn));
//
//        MongoTemplate mongoNewTemplate = getMongoTemplate("127.0.0.1",3717,"dev_base_cloud","base","base");
//        mongoNewTemplate.insert(plantingStandardCalendars,PlantingStandardCalendarPO.class);
//    }*/
//}
