package com.zenika.dorm.core.dao.neo4j;

/**
 * @author Antoine ROUAZE <antoine.rouaze AT zenika.com>
 */
public abstract class Neo4jNode {

    private String uri;
    private Neo4jResponse response;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Neo4jResponse getResponse() {
        return response;
    }

    public void setResponse(Neo4jResponse response) {
        this.response = response;
    }
}