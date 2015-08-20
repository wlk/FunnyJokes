package com.varwise.funnyjokes.db;

public class Joke {
    public final int _id;
    public final String content;
    public final int categoryId;

    public Joke(int _id, String content, int categoryId){
        this._id = _id;
        this.content = content;
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return content;
    }
}
