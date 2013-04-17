package com.group7.dragonwars.engine.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.List;

public class Database
{
	//Database Name "dw_high_scores"
	//Table Name 	"high_scores"
	//UserName		"Player"
	//Password		"Password"
	
	//User name for DB
	private String uName = "Player";
	
	//user password for DB
	private String uPass = "Password";
	
	//DB connection
	private Connection conn;
	
	//SQL statement
	Statement statement;
	
	//Query result
	ResultSet result;
	
	List<String> scores;
	
	public Database(String dbhost) throws SQLException
	{
		try
		{
			String host = "jdbc:derby://localhost:1527/dw_high_scores"; //this is an example of where the DB may be hosted.
			
			conn = DriverManager.getConnection( dbhost, uName, uPass);  //connect to DB
			
			Statement statement = conn.createStatement( );
		}
		catch(Exception e)
		{
			System.out.println("DB offline.\n High score functionality unavailable");
			throw new SQLException();
		}
	}
	
	public void deleteLocalScores()
	{
		//delete local copy of scores
		scores.clear();
	}
	
	public void ResetScores() throws SQLException
	{
		//delete the local copy of scores
		deleteLocalScores();
		
		//Delete table "high_scores"
		String SQL = "DROP TABLE high_scores";
		result = statement.executeQuery( SQL );
		
		
		/*
		 * CREATE TABLE dw_high_scores(
		   column1 datatype,
		   ...
		   columnN datatype,
		   PRIMARY KEY( the primary key )
		   );
		 */
		
		//Create table "dw_high_scores"; (gamename, player1name, player2name, score, set gamename as primary key)
		SQL = "CREATE TABLE high_scores( GAMENAME VARCHAR (20) NOT NULL, PLAYER1NAME VARCHAR (20) NOT NULL, PLAYER2NAME VARCHAR (20) NOT NULL, SCORE INT NOT NULL, PRIMARY KEY (GAMENAME));";
		result = statement.executeQuery( SQL );
	}
	
	public void AddHighScore(String gamename, String player1name, String player2name, int score) throws SQLException
	{
		//Add local high score
		scores.add("'"+ gamename +"', '"+ player1name + "', '" + player2name + "', " + score);
		
		//Add high score to table by using INSERT
		String SQL = "INSERT INTO high_score VALUES ('"+ gamename +"', '"+ player1name + "', '" + player2name + "', " + score + ")";
		result = statement.executeQuery( SQL );
	}
	
	public void PullHighscores() throws SQLException
	{
		//delete the local copy of scores
		deleteLocalScores();
		
		//get all rows from the table
		String SQL = "select * from high_scores";
		result = statement.executeQuery( SQL );
		
		
		//get metadata, used to parse row
		//ResultSetMetaData metadata = result.getMetaData();
		
		//get number of rows in the table
		int numRows = result.getFetchSize();
		
		for(int rowNum = 1; rowNum <= numRows; rowNum++)
		{
			//set current row
			result.absolute(rowNum);
			
			//get row column by column 
			String rowData = "";
			
			for(int column = 1; column <= 4; column++)
			{
				rowData += result.getString(column);
			}
			
			//add the row to the set of scores ready for printing
			scores.add(rowData);
		}
	}
}

