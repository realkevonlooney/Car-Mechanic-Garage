package com.example.model;

public class Vehicle {
    //encapsulation demonstration
    private int id;
    private int ownerId;
    private String make;
    private String model;
    private int year;
    private String powertrain;
    private String type;

    public Vehicle(int id, int ownerId, String make, String model, int year, String powertrain, String type) {
        this.id = id;
        this.ownerId = ownerId;
        this.make = make;
        this.model = model;
        this.year = year;
        this.powertrain = powertrain;
        this.type = type;
    }

    public int getId() { return id; }
    public int getOwnerId() { return ownerId; }
    public String getDisplay() {
        return id + ": " + year + " " + make + " " + model + " (" + powertrain + ", " + type + ")";
    }
}