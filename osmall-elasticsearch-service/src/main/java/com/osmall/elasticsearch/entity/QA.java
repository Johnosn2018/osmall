package com.osmall.elasticsearch.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

/**
 * Created by yaoyongzhen on 18/4/29
 */
@Document(indexName = "dialog", type = "qa", shards = 1, replicas = 0)
public class QA {
    @Id
    private int id;

    private String Q;

    @Field(type = FieldType.Nested)
    private List<String> A;

    public QA() {
    }

    public QA(int id, String q, List<String> a) {
        this.id = id;
        Q = q;
        A = a;
    }

    public int getId() {
        return id;
    }

    public String getQ() {
        return Q;
    }

    public void setQ(String q) {
        Q = q;
    }

    public List<String> getA() {
        return A;
    }

    public void setA(List<String> a) {
        A = a;
    }

    @Override
    public String toString() {
        return  " QA { " +
                "   id = " + id + "," +
                "   Q = '" + Q + "'," +
                "   A = " + A +
                '}';
    }
}
