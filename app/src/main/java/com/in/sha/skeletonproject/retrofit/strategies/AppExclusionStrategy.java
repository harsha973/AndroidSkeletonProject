package com.in.sha.skeletonproject.retrofit.strategies;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.in.sha.skeletonproject.retrofit.RetroFitServiceFactory;

/**
 * Excludes any field (or class) that is tagged with an "@link Ignore"
 */
public  class AppExclusionStrategy implements ExclusionStrategy {
    public boolean shouldSkipClass(Class<?> clazz) {
        return clazz.getAnnotation(RetroFitServiceFactory.Ignore.class) != null;
    }

    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(RetroFitServiceFactory.Ignore.class) != null;
    }
}