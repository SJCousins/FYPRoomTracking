package roombooking.fyp.fyproomviewfinal;

import android.util.Log;

import java.util.Date;

public class exampleItem {

    private String roomname;
    private String roomlocation;
    private String roomavailable;
    private String maxOccupancy;

    public exampleItem(String name, String location, String available, String maxPeople, String availableAt) {

        roomname = name;
        roomlocation = "Location: " + location;

        if (available == "true"){
            roomavailable = "available";
        }
        else    {
            roomavailable = "Next Available At: " + availableAt;
        }
        maxOccupancy = "Max Occupancy: " + maxPeople;

    }



    public String getName() {
        return roomname;
    }
    public String getLocation() {
        return roomlocation;
    }
    public String getAvailable() {
        return roomavailable;
    }
    public String getMaxOccupancy() {
        return maxOccupancy;
    }
}