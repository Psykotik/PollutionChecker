package fr.jordanlambert.myandroidapplication.tools.db;

/**
 * Created by jordan on 27/03/2017.
 */

interface DatabaseStruct {

    // Location table name
    String TABLE_LOCATION = "location";

    // Generic id column
    String KEY_ID = "id";

    // Location Table Columns names
    String KEY_LOCATION_CITY = "city_code";

    String CREATE_LOCATION_TABLE = "" +
            "CREATE TABLE "+TABLE_LOCATION+" (" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_LOCATION_CITY + " TEXT)";
}
