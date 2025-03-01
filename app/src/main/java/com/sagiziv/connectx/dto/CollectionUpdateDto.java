package com.sagiziv.connectx.dto;

import java.util.Set;

public final class CollectionUpdateDto {
    private Set<String> update;
    private int updateOperation;

    public CollectionUpdateDto() {
    }

    public Set<String> getUpdate() {
        return update;
    }

    public CollectionUpdateDto setUpdate(Set<String> update) {
        this.update = update;
        return this;
    }

    public int getUpdateOperation() {
        return updateOperation;
    }

    public CollectionUpdateDto setUpdateOperation(int updateOperation) {
        this.updateOperation = updateOperation;
        return this;
    }

    public static final class UpdateOperations {
        public static final byte SET = 0;
        public static final byte ADD = 1;
        public static final byte DELETE = 2;
    }
}
