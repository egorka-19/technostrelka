package com.example.main_screen.model;

public class ArtObject {
    private String id;
    private String name;
    private String description;
    private int imageResourceId;
    private double latitude;
    private double longitude;
    private String address;
    private int audioResourceId;

    public ArtObject() {
        // Default constructor required for Firebase
    }

    public ArtObject(String id, String name, String description, int imageResourceId, double latitude, double longitude, String address, int audioResourceId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageResourceId = imageResourceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.audioResourceId = audioResourceId;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public int getImageResourceId() { return imageResourceId; }
    public void setImageResourceId(int imageResourceId) { this.imageResourceId = imageResourceId; }
    
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getAudioResourceId() { return audioResourceId; }
    public void setAudioResourceId(int audioResourceId) { this.audioResourceId = audioResourceId; }
} 