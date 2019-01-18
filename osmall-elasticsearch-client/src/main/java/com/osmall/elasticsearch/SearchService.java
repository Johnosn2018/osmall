package com.osmall.elasticsearch;

/**
 * Created by yaoyongzhen on 18/4/29
 */
public interface SearchService {
    public String sayHello(String name);
    public String search(final String query);
    public int importAll();
}
