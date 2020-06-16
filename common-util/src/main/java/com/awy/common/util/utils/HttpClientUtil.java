package com.awy.common.util.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.time.Duration;

public class HttpClientUtil {

    private HttpClientUtil(){}

    public static String download(String uri,String temFolderName,String fileName){
        String filePath = FileUtil.getFilePath(temFolderName,fileName);
        try {
            //HttpResponse.BodyHandlers.ofInputStream();
            HttpClient.newHttpClient().send(getHttpRequest(uri), HttpResponse.BodyHandlers.ofFile(Paths.get(filePath))).body();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public static HttpRequest getHttpRequest(String uri){
        return getHttpRequest(uri,60);
    }

    public static HttpRequest getHttpRequest(String uri,long seconds){
        return HttpRequest.newBuilder(URI.create(uri)).timeout(Duration.ofSeconds(seconds)).build();
    }

}
