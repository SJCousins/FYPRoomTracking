package roombooking.fyp.fyproomviewfinal;

import android.util.Log;

public class exampleItem {

    private String roomname;
    private String roomlocation;
    private String roomavailable;

    public exampleItem(String name, String location, String available) {

        roomname = name;
        roomlocation = location;

        if (available == "true"){
            roomavailable = "available";
        }
        else    {
            roomavailable = "unavailable";
        }

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
}