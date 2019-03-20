package roombooking.fyp.fyproomviewfinal;

import android.util.Log;

import java.util.Date;

public class exampleItem {

    private String roomname;
    private String roomlocation;
    private String roomavailable;
    private String maxOccupancy;
    private String liveAvail;

    public exampleItem(String name, String location, String available, String maxPeople, String availableAt,String live) {

        roomname = name;
        roomlocation = "Location: " + location;

        if (available == "Available"){
            roomavailable = "Available";
        }
        else    {
            roomavailable = "Booked Until: " + availableAt;
        }
        maxOccupancy = "Max Occupancy: " + maxPeople;

liveAvail = live;
        //if (live == "0"){
          //  liveAvail = "Currently Empty";
        //}
       // else
       // {
          //  liveAvail = "Currently In use";
        //}



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
    public String getLiveAvail() {
        return liveAvail;
    }
}