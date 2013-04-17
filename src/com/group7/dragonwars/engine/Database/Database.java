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
		public String 	GAMENAME;
		public String 	PLAYER1NAME;
		public String 	PLAYER2NAME;
		public int 	SCORE;

	}
	private static final String DATABASE_CREATE =
			"create table high_scores(" +
			" GAMENAME VARCHAR (20) NOT NULL," +
			" PLAYER1NAME VARCHAR (20) NOT NULL," +
			" PLAYER2NAME VARCHAR (20) NOT NULL," +
			" SCORE INT NOT NULL," +
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
	public void AddEntry(String gamename, String player1, String player2, int score)
	{
		//create content values
		ContentValues values = new ContentValues();

		values.put("GAMENAME", gamename);
		values.put("PLAYER1NAME", player1);
		values.put("PLAYER2NAME", player2);
		values.put("SCORE", score);

		//add content values as a row
		database.insert(DATABASE_TABLE_NAME, null, values);
	}

	public List<Entry> GetEntries()
	{
		//gets all entries from the high scores table
		List<Entry> entries = new ArrayList<Entry>();

		//get cursor to DB from query
        String[] tmp = new String[] {"GAMENAME", "PLAYER1NAME", "PLAYER2NAME", "SCORE"};
		Cursor cursor = database.query(
            DATABASE_TABLE_NAME,
            tmp,
            null, null, null, null, null);

		//count the number of entries
		int numberOfEntries = cursor.getCount();
		cursor.moveToFirst();
		for(int entry = 0; entry < numberOfEntries; entry++)
		{
			Entry record = new Entry();
			record.GAMENAME	 	= cursor.getString(0);
			record.PLAYER1NAME	= cursor.getString(1);
			record.PLAYER1NAME	= cursor.getString(2);
			record.SCORE		= cursor.getInt(3);
            entries.add(record);
            cursor.moveToNext();
		}
        return entries;
	}
}
