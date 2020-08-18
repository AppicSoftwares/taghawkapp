package com.taghawk.model;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RemoveFirebaseListenerModel {
    private Query query;
    private ValueEventListener valueEventListener;

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public ValueEventListener getValueEventListener() {
        return valueEventListener;
    }

    public void setValueEventListener(ValueEventListener valueEventListener) {
        this.valueEventListener = valueEventListener;
    }
}
