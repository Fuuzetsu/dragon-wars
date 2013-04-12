package com.group7.dragonwars.engine.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class Database
{
	//Database Name "dw_high_scores"
	//Table Name 	"HIGH_SCORES"
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
		String host = "jdbc:derby://localhost:1527/dw_high_scores"; //this is an example of where the DB may be hosted.
		
		conn = DriverManager.getConnection( dbhost, uName, uPass);  //connect to DB
		
		Statement statement = conn.createStatement( );
	}
	
	public void ResetScores() throws SQLException
	{
		//Delete table "dw_high_scores"
		String SQL = "DROP TABLE dw_high_scores";
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
		SQL = "CREATE TABLE dw_high_scores( GAMENAME VARCHAR (20) NOT NULL, PLAYER1NAME VARCHAR (20) NOT NULL, PLAYER2NAME VARCHAR (20) NOT NULL, SCORE INT NOT NULL, PRIMARY KEY (GAMENAME));";
		result = statement.executeQuery( SQL );
	}
	
	public void AddHighScore(String gamename, String player1name, String player2name, int score) throws SQLException
	{
		String SQL = "INSERT INTO dw_high_score VALUES ('"+ gamename +"', '"+ player1name + "', '" + player2name + "', " + score + ")";
		result = statement.executeQuery( SQL );
	}
	
	public void PullHighscores() throws SQLException
	{
		String SQL = "select * from dw_high_scores";
		result = statement.executeQuery( SQL );
	}
}

