package com.group7.dragonwars.engine.Database;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;

import java.util.ArrayList;
import java.util.List;

public class Database
{
	class Entry extends Object
	{
        public int GAMETIME;
        public float DAMAGEDEALT;
        public float DAMAGERECEIVED;
        public float DISTANCETRAVELED;
        public int GOLDCOLLECTED;
        public int UNITSKILLED;
        public int UNITSMADE;
	}

	private static final String DATABASE_CREATE =
        "create table high_scores(" +
        " GAMETIME INT NOT NULL," +
        " DAMAGEDEALT FLOAT NOT NULL," +
        " DAMAGERECEIVED FLOAT NOT NULL," +
        " DISTANCETRAVELED FLOAT NOT NULL," +
        " GOLDCOLLECTED INT NOT NULL," +
        " UNITSKILLED INT NOT NULL," +
        " UNITSMADE INT NOT NULL," +
        " PRIMARY KEY(GAMENAME)" +
        ");";

	private static final String DATABASE_NAME =	"dw_high_scores";

	private static final String DATABASE_TABLE_NAME = "high_scores";

	private static final int DATABASE_VERSION = 1;

	private SQLiteDatabase database;

	public Database(Context con)
	{
		//open the database if it exists else, creates a new data base with that name
		database = con.openOrCreateDatabase(DATABASE_NAME, 0, null);
	}

	public void CreateTable()
	{
		//creates the high_scores table
		database.execSQL(DATABASE_CREATE);
	}

	public void DeleteTable()
	{
		Close();
	}

	//should be called when the DB is no longer needed
	public void Close()
	{
		database.close();
	}

	//use to add a new high score to the database
	public void AddEntry(float damageDealt, float damageReceived, float distanceTraveled,
                         int goldCollected, int unitsKilled, int unitsMade)
	{
		//create content values
		ContentValues values = new ContentValues();

		values.put("GAMETIME", System.currentTimeMillis());
        values.put("DAMAGEDEALT", damageDealt);
        values.put("DAMAGERECEIVED", damageReceived);
        values.put("DISTANCETRAVELED", distanceTraveled);
        values.put("GOLDCOLLECTED", goldCollected);
        values.put("UNITSKILLED", unitsKilled);
        values.put("UNITSMADE", unitsMade);

		//add content values as a row
		database.insert(DATABASE_TABLE_NAME, null, values);
	}

	public List<Entry> GetEntries()
	{
		//gets all entries from the high scores table
		List<Entry> entries = new ArrayList<Entry>();

		//get cursor to DB from query
        String[] query = {"GAMETIME", "DAMAGEDEALT", "DAMAGERECEIVED", "DISTANCETRAVELED",
                          "GOLDCOLLECTED", "UNITSKILLED", "UNITSMADE"};

		Cursor cursor = database.query(
            DATABASE_TABLE_NAME,
            query,
            null, null, null, null, null);

		//count the number of entries
		int numberOfEntries = cursor.getCount();
		cursor.moveToFirst();
		for(int entry = 0; entry < numberOfEntries; entry++)
		{
			Entry record = new Entry();
			record.GAMETIME	 	    = cursor.getInt(0);
			record.DAMAGEDEALT	    = cursor.getFloat(1);
			record.DAMAGERECEIVED	= cursor.getFloat(2);
			record.DISTANCETRAVELED	= cursor.getFloat(3);
            record.GOLDCOLLECTED	= cursor.getInt(4);
            record.UNITSKILLED		= cursor.getInt(5);
            record.UNITSMADE        = cursor.getInt(6);
            entries.add(record);
            cursor.moveToNext();
		}

        return entries;
	}
}
