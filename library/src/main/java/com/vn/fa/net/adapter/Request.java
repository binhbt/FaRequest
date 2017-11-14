package com.vn.fa.net.adapter;

import com.vn.fa.net.ApiService;

/**
 * Created by leobui on 10/25/2017.
 */

public class Request {
    private String baseUrl;
    private boolean isLogging;
    private Request.Factory requestFactory;
    public Request(String baseUrl, boolean isLogging, Request.Factory factory) {
        this.baseUrl = baseUrl;
        this.isLogging = isLogging;
        this.requestFactory = factory;
    }
    public <T> T create(final Class<T> clazz){
        if (this.requestFactory == null){
            //Default is RETROFIT request
            return new ApiService.Builder()
                    .baseUrl(baseUrl)
                    .logging(isLogging)
                    .build()
                    .create(clazz);
        }else{
            return this.requestFactory.create(baseUrl, isLogging, clazz);
        }

    }
    public static final class Builder {
        private String baseUrl;
        private boolean isLogging;
        private Request.Factory requestFactory;
        public Request.Builder baseUrl(String baseUrl){
            this.baseUrl = baseUrl;
            return this;
        }
        public Request.Builder logging(boolean isLogging){
            this.isLogging = isLogging;
            return this;
        }
        public Request build(){
            return new Request(this.baseUrl, this.isLogging, this.requestFactory);
        }
        public Request.Builder addRequestAdapterFactory(Request.Factory factory){
            this.requestFactory = factory;
            return this;
        }
    }

    public abstract static class Factory {
        protected String baseUrl;
        protected boolean isLogging;
        protected void init(){
            //Do nothing
            //Will exec after create
            //Overrite it if you need attach your code
        }
        public <T> T create(final String baseUrl, final boolean isLogging, final Class<T> clazz){
            this.baseUrl = baseUrl;
            this.isLogging = isLogging;
            init();
            return (T)this;
        }
    }
}
