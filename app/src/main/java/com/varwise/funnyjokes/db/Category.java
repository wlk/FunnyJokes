package com.varwise.funnyjokes.db;

public class Category {
    public final int _id;
    public final String name;

    public Category(int _id, String name){
        this._id = _id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
