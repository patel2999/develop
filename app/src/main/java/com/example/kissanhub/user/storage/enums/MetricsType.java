package com.example.kissanhub.user.storage.enums;

public enum MetricsType {

    MAX_TEMPERATURE (1, "Tmax"),
    MIN_TEMPERATURE (2, "Tmin"),
    RAINFALL (3, "Rainfall");

    private int mMetricsTypeId;
    private String mMetricsType;

    MetricsType(int metricsTypeId, String metricsType){
        mMetricsTypeId = metricsTypeId;
        mMetricsType = metricsType;
    }

    public int getMetricsTypeId() {
        return mMetricsTypeId;
    }

    public String getMetricsType() {
        return mMetricsType;
    }
}
