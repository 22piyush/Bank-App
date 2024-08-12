package com.piyush.BankApp;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbBankConnection {
	
	// **************************SINGLETON CLASS TYPE****************************
	
	static Connection con;
	static 
	{
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost:3306/bankapp","root" ,"Piyush@54321");
			
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	static Connection getConnect()
	{
		return con;
	}

}
