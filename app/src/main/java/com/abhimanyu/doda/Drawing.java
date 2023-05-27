package com.abhimanyu.doda;

public class Drawing {
    private String id;
    private String name;
    private String additionTime; // Updated to String type
    private int markerCount;
    private String imageUrl;

    public Drawing() {
        // Default constructor required for Firebase
    }

    public Drawing(String id, String name, String additionTime, int markerCount, String imageUrl) {
        this.id = id;
        this.name = name;
        this.additionTime = additionTime;
        this.markerCount = markerCount;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAdditionTime() {
        return additionTime;
    }

    public int getMarkerCount() {
        return markerCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
