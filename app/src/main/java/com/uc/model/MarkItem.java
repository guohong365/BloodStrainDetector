package com.uc.model;

public class MarkItem {
    private int id;
    private int ShapeRes;

    public MarkItem(int id, int shapeRes){
        this.id=id;
        this.ShapeRes=shapeRes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShapeRes() {
        return ShapeRes;
    }

    public void setShapeRes(int shapeRes) {
        ShapeRes = shapeRes;
    }
}
