package com.uc.activity;

public abstract class AbstractRequestParams implements RequestParams {
    protected int action;

    public AbstractRequestParams(){}
    public AbstractRequestParams(int action){
        this.action=action;
    }

    @Override
    public int getAction() {
        return action;
    }

    @Override
    public void setAction(int action) {
        this.action=action;
    }
}
