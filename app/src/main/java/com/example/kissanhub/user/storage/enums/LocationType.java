package com.example.kissanhub.user.storage.enums;

public enum LocationType {

    UK(1, "UK"),
    ENGLAND(2, "England"),
    SCOTLAND(3, "Scotland"),
    WALES(4, "Wales");

    private int mLocationId;
    private String mLocationName;

    LocationType(int locationId, String locationName) {
        mLocationId = locationId;
        mLocationName = locationName;
    }

    public int getLocationId() {
        return mLocationId;
    }

    public String getLocationName() {
        return mLocationName;
    }
}
