package com.piyush.BankApp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class BankApp 
{
	Scanner scan;
	public static void main(String[] args) 
	{
		BankApp ba = new BankApp();
		ba.startBankApp();
	}
	void startBankApp()
	{
		System.out.println("\n========================================================");
		
		System.out.println("Enter any option");
		System.out.println("1.Add Account");
		System.out.println("2.Fund Transfer");
		System.out.println("3.Mini Statement");
		System.out.println("4.Exit");
		
		System.out.println("\nSelect Any Option");
		System.out.println("========================================================");
		
		getUserInput();
	}
	void getUserInput()
	{
		scan = new Scanner(System.in);
		int userOption = scan.nextInt();
		System.out.println("-----------------------------------------------------");
		
		if(userOption == 1)
		{
			addAccount();
		}
		else if(userOption == 2)
		{
			fundTransfer();
		}
		else if(userOption == 3)
		{
			miniStatement();
		}
		else
		{
			System.out.println("Bank App Closed");
			System.exit(0);
		}
	}
	void addAccount()
	{
		System.out.println("Enter Id : ");
		int id = scan.nextInt();
		
		System.out.println("Entedr Name : ");
		String name = scan.next();
		
		System.out.println("Enter email: ");
		String email = scan.next();
		
		System.out.println("Enter Contact : ");
		String contact = scan.next();
		
		System.out.println("Enter Account_no : ");
		int account = scan.nextInt();

		Connection con=null;
		try {
			
			con=DbBankConnection.getConnect();	
			con.setAutoCommit(false);
			//--------------------------CREATING ACCOUNT------------------
			PreparedStatement ps1 = con.prepareStatement("insert into  bankusers values(?,?,?,?,?)");
			
			ps1.setInt(1, id);
			ps1.setString(2, name);
			ps1.setString(3, email);
			ps1.setString(4, contact);
			ps1.setInt(5, account);
			
			int rowCount1 = ps1.executeUpdate();
			//---------------------------------------------------------
			
			//--------------------------DEPOSIT MONEY--------------------------
            PreparedStatement ps2 = con.prepareStatement("insert into total_amount values(?,?,?)");
			
			ps2.setInt(1, id);
			ps2.setInt(2, account);
			ps2.setInt(3, 5000);
			
			int rowCount2 = ps2.executeUpdate();
			//---------------------------------------------------------
			
			if(rowCount1>0 && rowCount2>0)
			{
				con.commit();
				System.out.println("Account Created Successfully");
			}
			else
			{
				con.rollback();
				System.out.println("Account Creation Failed");
			}
			
		}
		catch(Exception e) {
			
			try {
				con.rollback();
			}
			catch(Exception ee)
			{
				System.out.println(ee);
			}
			System.out.println(e);
		}
		startBankApp();
	}
	void fundTransfer()
	{
		System.out.println("Enter From Account No");
		int from_accno = scan.nextInt();
		
		System.out.println("Enter To Account No");
		int to_accno = scan.nextInt();
		
		System.out.println("Enter Amount: ");
		int deposit_amount = scan.nextInt();
		
		int from_bal=0,to_bal=0;
		
		Connection con = null;
		try 
		{
			con = DbBankConnection.getConnect();
			con.setAutoCommit(false);
			
			PreparedStatement ps = con.prepareStatement("select balance from total_amount where account_no=?");
			ps.setInt(1, from_accno);
			ResultSet rs = ps.executeQuery();
			while(rs.next())
			{
				from_bal= rs.getInt(1);
			}
			
			PreparedStatement ps1 = con.prepareStatement("select balance from total_amount where account_no=?");
			ps1.setInt(1, to_accno);
			ResultSet rs1 = ps1.executeQuery();
			while(rs1.next())
			{
				to_bal= rs1.getInt(1);
			}
			
			int new_from_bal = from_bal - deposit_amount;
			int new_to_bal = to_bal + deposit_amount;
			
			PreparedStatement ps2 = con.prepareStatement("update total_amount set balance=? where account_no=?");
			ps2.setInt(1, new_from_bal);
			ps2.setInt(2,from_accno);
			int rowCount1 = ps2.executeUpdate();
			
			
			PreparedStatement ps3 = con.prepareStatement("update total_amount set balance=? where account_no=?");
			ps3.setInt(1, new_to_bal);
			ps3.setInt(2,to_accno);
			int rowCount2 = ps3.executeUpdate();
			
			
			Date d = new Date();
		    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		    String date1 = sdf1.format(d);
		        
		    SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
		    String time1 = sdf2.format(d);
			
			PreparedStatement ps4 = con.prepareStatement("insert into mini_statement values(?,?,?,?,?)");
			ps4.setInt(1,from_accno);
			ps4.setInt(2,deposit_amount);
			ps4.setString(3,"d");
			ps4.setString(4,date1);
			ps4.setString(5,time1);
			int rowCount3 = ps4.executeUpdate();
			
			
			
			PreparedStatement ps5 = con.prepareStatement("insert into mini_statement values(?,?,?,?,?)");
			ps5.setInt(1,to_accno);
			ps5.setInt(2,deposit_amount);
			ps5.setString(3,"c");
			ps5.setString(4,date1);
			ps5.setString(5,time1);
			int rowCount4 = ps5.executeUpdate();
				
			
			if(rowCount1>0 && rowCount2>0 && rowCount3>0 && rowCount4>0)
			{
				con.commit();
				System.out.println("Amount Deposit Successfully");
			}
			else {
				con.rollback();
				System.out.println("Transaction Failed");
			}
			
		}
		catch(Exception e)
		{
			try {
				con.rollback();
			}
			catch(Exception ee)
			{
				System.out.println(ee);
			}
			System.out.println(e);
		}
		
		startBankApp();
	}
	void miniStatement()
	{
		System.out.println("Enter Account Number : ");
		int accno = scan.nextInt();
		Connection con=null;
		try {
			con=DbBankConnection.getConnect();
			PreparedStatement ps = con.prepareStatement("select * from mini_statement where account_no=?");
			
			ps.setInt(1, accno);
			
			ResultSet rs = ps.executeQuery();
			while(rs.next())
			{
				System.out.print(rs.getInt(2));
				System.out.print("\t"+rs.getString(3));
				System.out.print("\t"+rs.getString(4));
				System.out.print(rs.getString(5));
				
			}
		}
		catch(Exception e)
		{
			System.out.println(e);		
		}
		startBankApp();
		
		//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	}
}
