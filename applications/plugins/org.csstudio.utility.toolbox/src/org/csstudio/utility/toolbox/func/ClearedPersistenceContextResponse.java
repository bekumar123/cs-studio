package org.csstudio.utility.toolbox.func;


public class ClearedPersistenceContextResponse<T> {

    private T response;

    public ClearedPersistenceContextResponse(T response) {
        super();
        this.response = response;
    }

    public T getResponse() { 
        return response;
    }

}
