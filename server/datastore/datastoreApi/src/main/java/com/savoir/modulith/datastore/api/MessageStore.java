package com.savoir.modulith.datastore.api;


public interface MessageStore {

    public String getMessage(String key);

    public void putMessage(String key, String message);
}
