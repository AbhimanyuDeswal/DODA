package com.abhimanyu.doda;

public class Drawing {
    private String id;
    private String name;
    private String additionTime;
    private int markerCount;

    public Drawing() {
        // Empty constructor needed for Firebase database operations
    }

    public Drawing(String id, String name, String additionTime, int markerCount) {
        this.id = id;
        this.name = name;
        this.additionTime = additionTime;
        this.markerCount = markerCount;
    }

    // Getters and setters for the Drawing class

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdditionTime() {
        return additionTime;
    }

    public void setAdditionTime(String additionTime) {
        this.additionTime = additionTime;
    }

    public int getMarkerCount() {
        return markerCount;
    }

    public void setMarkerCount(int markerCount) {
        this.markerCount = markerCount;
    }
}
