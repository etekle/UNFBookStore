import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Scanner;

public class UNFBookStore {
	private static String lName;
	private static String fName;
	private static String mInit;
	private static String email;
	private static String phone;
	private static String home;

	public static void main (String args[])	throws SQLException, ClassNotFoundException, ParseException
	{	
		Scanner scan = new Scanner(System.in);
		
		Properties prop = new Properties();
		InputStream input = null;

		String uid = "n01239628";
		String passwrd = "Spring20219628";
		String url = "";
	
		try {
			input = new FileInputStream("config.properties");

			// Load a properties file
			prop.load(input);
			url = prop.getProperty("database2");
			
			} // End try 
		
		catch (IOException ex) {
			ex.printStackTrace(); 
			} // End catch
		
		finally {
			if (input != null) { 
				try {
					input.close(); } // End try
				
				catch (IOException e) {
					e.printStackTrace(); } // End catch
			} // End if Statement
		} // End finally

		// Connect to the database
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection( url, uid, passwrd);

//----------------------------------------------------------------------------
		
		welcomeScreen();
		
		String access = " ";
		
		List<Cart> cart = new ArrayList<Cart>();
		ArrayList<Inventory> code;
		int rentcntr = 0;
		
		Statement stmt = conn.createStatement ();
		DecimalFormat df = new DecimalFormat("0.00");
		
		while(!access.equals("exit"))
		{	
			System.out.println("Are you logging in as a User or Admin?, enter 'exit' to end program");
			access = getString().toLowerCase();		
			switch (access)
			{
				case "user":
					boolean m = true;
					boolean y = false;
					while(m)
					{
						System.out.println("\nWould you like to: \n"
								+ "1. Search for a book\n"
								+ "2. Checkout\n"
								+ "3. Return a book\n"
								+ "4. Check your Balance/Fees\n"
								+ "Or 0 to Go Back");
						
						String user = getString();
						
						switch(user)
						{
							case "0":
								m = false;
								break;
							case "1":								
								String v = "";
								
								while (!v.equals("exit")) {
									System.out.println("\nWhat are you looking for?\n"
											+ "Enter an ISBN, description, price,\n" 
											+ "Or enter 'exit' to go back to main menu");								
									
									Scanner string = new Scanner(scan.next().toLowerCase());
									string.useDelimiter("\\$");
									v = string.next();							
									
									if(v.equals("exit")) break;
									
									String q = "SELECT INV_ISBN_ID, INV_TITLE, INV_GENRE, INV_CATEGORY, INV_SUBCATEGORY, INV_AUTHOR, "
											+ "INV_PUBLISHER, INV_FORMAT, INV_CONDITION, INV_PURCHASE_OPTION, INV_COST, INV_IN_STOCK, INV_COPIES\r\n" 
											+ "FROM inventory_table \r\n" 
											+ "WHERE INV_ON_DISPLAY = 'Y'\r\n" 
											+ "AND (INV_ISBN_ID LIKE '%" + v + "%' OR INV_TITLE LIKE '%" + v + "%' "
											+ "OR INV_GENRE LIKE '%" + v + "%' OR INV_CATEGORY LIKE '%" + v + "%'\r\n"
											+ "OR INV_SUBCATEGORY LIKE '%" + v + "%' OR INV_AUTHOR LIKE '%" + v + "%'\r\n" 
											+ "OR INV_PUBLISHER LIKE '%" + v + "%' OR INV_CONDITION LIKE '%" + v + "%'\r\n" 
											+ "OR INV_PURCHASE_OPTION LIKE '%" + v + "%' OR INV_COST LIKE '%" + v + "%'\r\n"
											+ "OR INV_FORMAT LIKE '%" + v + "%')";
									ResultSet rset = stmt.executeQuery(q);									
									
									int countr = 0;
									
									code = new ArrayList<Inventory>();
									
									String spc = " ";
									System.out.printf("     ISBN   | %-7sTitle%10s | %-5sGenre%11s | Category  | SubCat    | Author%7s"
											+ " | %9sPublisher%13s |    Format    | Cond | PrOpt |  Cost  | InStk | Copies\n"
											+ "---------------------------------------------------------------------------------------"
											+ "-----------------------------------------------------------------------------------------\n"
											,spc,spc,spc,spc,spc,spc,spc,spc,spc);
									
									while (rset.next()) {
										System.out.printf(++countr + ". ");
										String isbn = rset.getString("INV_ISBN_ID");
										String title = rset.getString("INV_TITLE");
										String genre = rset.getString("INV_GENRE");
										String cat = rset.getString("INV_CATEGORY");
										String sub = rset.getString("INV_SUBCATEGORY");
										String author = rset.getString("INV_AUTHOR");
										String publish = rset.getString("INV_PUBLISHER");
										String format = rset.getString("INV_FORMAT");
										String condit = rset.getString("INV_CONDITION");
										String purch = rset.getString("INV_PURCHASE_OPTION");
										float cost = rset.getFloat("INV_COST");
										String stock = rset.getString("INV_IN_STOCK");
										int copy = rset.getInt("INV_COPIES");
										
										code.add(new Inventory(isbn,title,genre,cat,sub,author,publish,format,condit,purch,cost,stock,copy));
										String price = df.format(cost) + "";
										
										System.out.printf("%s | %-22s | %-21s | %-9s | %-9s | %-13s | %-31s | %-13s | %-4s |  %-4s | %-6s |   %-2s  |  %d\n"
												,isbn, title, genre, cat, sub, author, publish, format, condit, purch, price, stock, copy);
									}  // while rset
									if(code.isEmpty())										
										System.out.println("0 results found for " + v);
									else
									{
										boolean pick = true;
										while(pick)	
										{
											System.out.println("Pick an item number to add to your cart,");
											try{
												
												System.out.println("Enter 'back' to search for other books,\n"
														+ "Enter 'list' to show the list of books again\n"
														+ "Or enter 'exit' to go back to the main menu");
												
												v = getString().toLowerCase();
												
												if(v.equals("exit") || v.equals("back")) break;
												else if(v.equals("list"))
												{
													System.out.println("");
													for(int i = 0; i < code.size(); i++)
													{
														System.out.printf("     ISBN   | %-7sTitle%10s | %-5sGenre%11s | Category  | SubCat    | Author%7s"
																+ " | %9sPublisher%13s |    Format    | Cond | PrOpt |  Cost  | InStk | Copies\n"
																+ "---------------------------------------------------------------------------------------"
																+ "-----------------------------------------------------------------------------------------\n"
																,spc,spc,spc,spc,spc,spc,spc,spc,spc);
														System.out.printf((i+1) + ". "); code.get(i).display();			
													}
													System.out.println("");
												}
												else{
													int num;
													
													num = Integer.parseInt(v) - 1;
													if(num < countr && num >= 0 && code.get(num).getCopies() > 0)
													{
														v = "";
														if(code.get(num).getPurch().equals("Rent") && rentcntr <= 2)
														{
															System.out.println(code.get(num).getTitle() + " is available for rent.");
															
															while(!v.equals("back"))
															{
																try
																{	
																	if(rentcntr == 0)
																		System.out.println("Would you like to rent 1 or 2 of this book?\n"
																				+ "OR Enter 'back' to pick another book.\n"
																				+ "OR Enter 'exit' to go back to main menu\n"
																				+ "Reminder you can only have 2 titles rented at a time");
																	else if(rentcntr == 1)
																		System.out.println("Would you like to rent some more?\n"
																				+ "OR Enter 'back' to pick another book.\n"
																				+ "OR Enter 'drop' to remove some books.\n"
																				+ "OR enter 'exit' to go back to main menu\n"
																				+ "Reminder you can only have 2 titles rented at a time");
																	else
																		System.out.println("You have 2 books in cart for rent. \n"
																				+ "Enter 'back' to pick another book.\n"
																				+ "Enter 'drop' to remove some books.\n"
																				+ "Or enter 'exit' to go back to main menu");
																		
																	v = getString().toLowerCase();
																	if(v.equals("back"))break;
																	else if(v.equals("exit")) break;
																	int b = Integer.parseInt(v);														
																	boolean present = false;
																	
																	if(b == 1 && code.get(num).getCopies() > 0 && rentcntr < 2)
																	{	
																		for(int i = 0; i < cart.size(); i++)
																		{
																			if(cart.get(i).getTitle().equals(code.get(num).getTitle()))
																			{
																				present = true;
																				cart.get(i).setQuantity(cart.get(i).getQuantity()+1);
																			}
																		}
																		if(present == false)
																		{
																			cart.add(new Cart(code.get(num).getISBN(),code.get(num).getTitle(),										
																					code.get(num).getPurch(),code.get(num).getCondition(),1, code.get(num).getCost()));
																		}
																		code.get(num).setCopies(code.get(num).getCopies() - 1);	
																		
																		rentcntr++;
																		System.out.println("1 copy of " + code.get(num).getTitle() + " has been addded to your cart");
																	}
																	else if(b == 2 && code.get(num).getCopies() > 1 && rentcntr < 2)
																	{	
																		for(int i = 0; i < cart.size(); i++)
																		{
																			if(cart.get(i).getTitle().equals(code.get(num).getTitle()))
																			{
																				present = true;
																				cart.get(i).setQuantity(cart.get(i).getQuantity()+2);
																			}
																		}
																		if(present == false)
																		{
																			cart.add(new Cart(code.get(num).getISBN(),code.get(num).getTitle(),										
																					code.get(num).getPurch(),code.get(num).getCondition(),2, code.get(num).getCost()));
																		}
																		code.get(num).setCopies(code.get(num).getCopies() - 2);	
																		
																		rentcntr += 2;
																		System.out.println("2 copies of " + code.get(num).getTitle() + " have been addded to your cart\n"
																				+ "you are at your rental limit.\n");
																	}
																	else if(rentcntr == 2)
																		System.out.println("You cannot rent more than 2 books at a time\n"
																				+ "Pick another item number to add to your cart\n"
																				+ "Enter 'drop' to take drop some rented book\n"
																				+ "Or enter 'exit' to go back to main menu");
																	else if(b > 2 || b < 0)
																		System.out.println("Please either choose to rent 1 or 2 instances of this book.");
																	else if(b == 0)
																		System.out.println("No books were added to your cart");
																	else if(b > code.get(num).getCopies())
																		System.out.println("Sorry but there aren't enough books in stock\n"
																				+ "\nPick another item number to add to cart");																
																}catch(NumberFormatException e)
																{
																	if(v.toLowerCase().equals("drop"))
																	{
																		rentcntr = drop(code,cart,rentcntr);
																		break;
																	}
																	else
																		System.out.println("Input Not Accepted. Please try again.");																
																}
															}
															if(v.equals("exit")) break;														
														}
														else if(code.get(num).getPurch().equals("Rent") && rentcntr > 2)
														{
															System.out.println("You already have two titles rented out\n"
																	+ "Would you like to delete one?");
														}
														else if(code.get(num).getPurch().equals("Buy"))
														{	
															System.out.println(code.get(num).getTitle() + " is available to buy.");
															
															while(!v.equals("back"))
															{										
																try{
																	if(code.get(num).getCopies() > 0 )
																		System.out.println("How many would you like to add? "
																			+ "Enter 'back' to pick another book,\n"
																			+ "Enter 'drop' to remove some books,\n"
																			+ "Or enter 'exit' to go back to main menu");
																	else if(!cart.isEmpty())
																		System.out.println("Enter the number of books you wish to add,\n"
																				+ "Enter 'back' to pick another book,\n" 
																				+ "Enter 'drop' to remove some books\n"
																				+ "Or enter 'exit' to go back to main menu");
																
																	v = getString().toLowerCase();
																	if(v.equals("back"))break;
																	else if(v.equals("exit"))break;
																	int b = Integer.parseInt(v);
																	if (b <= code.get(num).getCopies() && b > 0)
																	{
																		code.get(num).setCopies(code.get(num).getCopies() - b);
																		cart.add(new Cart(code.get(num).getISBN(),code.get(num).getTitle(), 
																				code.get(num).getPurch(),code.get(num).getCondition(), b, code.get(num).getCost()));
																		System.out.println(b + " copies of " + code.get(num).getTitle() 
																				+ " have been addded to your cart.\n");
																	}
																	else if(b > code.get(num).getCopies() || b < 0)
																		System.out.println("Sorry but there aren't enough books in stock.\n");
																	else if (b == 0)
																		System.out.println("No books were added to your cart");
																	else
																		System.out.println("Invalid Input, try again");
																}catch(NumberFormatException x)	{
																	if(v.equals("drop"))
																	{
																		rentcntr = drop(code,cart,rentcntr);
																		break;
																	}
																	else
																		System.out.println("Invalid Input");															
																}
															}
															if(v.equals("exit"))break;
														}
													}
													else if (num < countr && num >= 0 && code.get(num).getCopies() == 0)
														System.out.println("Sorry, this item is currenty out of stock");													
													else
														System.out.println("Item " + v + " does not exist\n");
												}										
											}catch(NumberFormatException e)
											{
												System.out.println("Input Not Accepted. Please try again.\n");
											}
										}
										if(v.equals("exit")) break;
									}
								} // End while v Loop
								
								break;
								
							case "2":
								y = checkout(cart,stmt,conn,y);
								break;
								
							case "3":
								y = returnBook(stmt,conn,y);
								break;
								
							case "4":
								y = checkBalance(stmt,conn, y);
								break;
								
							default:
								System.out.println("Invalid Input. Please try again!");
						}
					}
				break;
				
				case "admin":
					boolean n = true;
					y = false;
					while(n)
					{
						System.out.println("\nWould you like to: \n"
								+ "1. Locate Title\n"
								+ "2. Update Inventory\n"
								+ "3. Check User Balance & Apply Late Fees\n"
								+ "4. Generate Reports\n"
								+ "Or 0 to Go Back");
						String user = getString();
						String v = "";
						switch(user)
						{
							case "0":
								n = false;
								break;
							case "1":
								locate(stmt);
								break;
							case "2":
								v = "";
								
								while (!v.equals("exit")) {
									System.out.println("\nWould you like to add, delete, or update a title?\n" 
											+ "Or enter 'exit' to go back to main menu");							
									
									v = getString().toLowerCase();							
									
									if(v.equals("exit")) break;
									else if(v.equals("add"))
										add(conn);
									else if(v.equals("delete"))										
										delete(conn);
									else if(v.equals("update"))			
										update(conn);
									else
										System.out.println("Input Not Accepted, ");
									
								}
								break;
							case "3":
								balanceFees(stmt,conn);
								break;
							case "4":
								report(stmt,conn);
								break;							
						}
					}
				break;
				
				case "exit":
					break;
				
				default:
					System.out.println("Input Not Accepted. Please try again.");			
			}
		}
		scan.close();
	} // End main Method
	
	private static boolean checkBalance(Statement stmt, Connection conn, boolean y) throws SQLException {
		// TODO Auto-generated method stub
		
		PreparedStatement pstmt;
		
		String v = "";
		if(y == false)
		{	
			System.out.println("Please enter your email adress or customer id");
			v = getString().toLowerCase();
		}
		else
			v = email;
		
		String q = "SELECT DISTINCT customer_table.CUS_ID, CUS_LNAME, CUS_FNAME, CUS_MINITIAL FROM customer_table, transaction_table "
				+ " WHERE (customer_table.CUS_ID = "+ v +" OR customer_table.CUS_EMAIL_ADDRESS = '" + v + "') "
				+ " AND transaction_table.CUS_ID = customer_table.CUS_ID";
		
		ResultSet rset = stmt.executeQuery(q);	
		
		String cusID = rset.getString("customer_table.CUS_ID");
		String lname = rset.getString("CUS_LNAME");
		String fname = rset.getString("CUS_LNAME");
		String cusname = rset.getString("CUS_LNAME");
		
		System.out.println(cusID + "|" + lname + "|" + fname + "|" + cusname + "\n" 
				+ "=======================================================================");
		
		q = " SELECT DISTINCT inventory_table.INV_TITLE, RENT_QTY , TRANS_PURCHASE_DATE, RENT_LATE_FEE "
				+ "FROM customer_table, transaction_table, rent_table, inventory_table"
				+" WHERE (customer_table.CUS_ID = "+ v +" OR customer_table.CUS_EMAIL_ADDRESS = '" + v +"')"
				+ " AND transaction_table.CUS_ID = customer_table.CUS_ID"
				+ " AND transaction_table.TRANS_ID = rent_table.TRANS_ID"
				+ " AND rent_table.INV_ISBN_ID = inventory_table.INV_ISBN_ID"
				+ " AND RENT_QTY != 0";
		
		rset = stmt.executeQuery(q);
		
		while(rset.next())
		{
			String title = rset.getString("inventory_table.INV_TITLE");
			String rent = rset.getString("RENT_QTY");
			String date = rset.getString("TRANS_PURCHASE_DATE");
			String fee = rset.getString("RENT_LATE_FEE");
		
			System.out.println(title + "| " + rent + "| " + date + "| $" + fee); 
		}
		
		q = "SELECT SUM(BILL_BALANCE) AS BILL_BALANCE FROM customer_table, transaction_table, billing_table "
		+ " WHERE (customer_table.CUS_ID = "+ v +" OR customer_table.CUS_EMAIL_ADDRESS = '"+ v +"')"
		+ " AND transaction_table.CUS_ID = customer_table.CUS_ID"
		+ " AND transaction_table.TRANS_ID = billing_table.TRANS_ID";
		
		rset = stmt.executeQuery(q);
		
		String bill = rset.getString("BILL_BALANCE");
		
		System.out.println("You have a balance of $" + bill);
		
		System.out.println("Would you like to pay now? Y/N");
		
		v = "";
		
		while(!v.equals("N") && !v.equals("Y"))
		{
			v = getString().toUpperCase();
			if(v.length() > 1)
				v = v.substring(0,1);	
			if(!v.equals("Y") && !v.equals("N"))
			System.out.println("Input Not Accepted, Please Try Again");
		}
		
		System.out.println("Enter your credit/debit card info");
		System.out.println("Enter the numbers on the front of the card\n"
				+ "(type any number)\n");
		while(true)
		{
			try
			{
				String b = getString();				
				int w = Integer.parseInt(b);
				break;
			}
			catch(NumberFormatException e)
			{
				System.out.println("Invalid Input. Please enter again");
			}
		}
		
		System.out.println("Enter the three numbers on the back/front of the card\n");
		
		while(true)
		{
			try
			{
				String b = getString();				
				int w = Integer.parseInt(b);
				if(w >= 0 && w <= 9999)break;
				else
					System.out.println("numbers are outside of the range");
			}
			catch(NumberFormatException e)
			{
				System.out.println("Invalid Inpit. Please enter again");
			}
		}
		
		q = "SELECT RENT_ID, RENT_LATE_FEE FROM rent_table, customer_table, transaction_table "
				+ " WHERE  customer_table.CUS_ID = transaction_table.CUS_ID "
				+ " AND transaction_table.TRANS_ID = rent_table.TRANS_ID "
				+ " AND (customer_table.CUS_ID = " + v + " OR customer_table.CUS_EMAIL_ADDRESS = '" + v +"')";

		rset = stmt.executeQuery(q);
		
		while(rset.next())
		{
			String rent = rset.getString("RENT_ID");
			String fee = rset.getString("RENT_LATE_FEE");
		
			System.out.println(rent + " | $" + fee);
			pstmt = conn.prepareStatement ("UPDATE rent_table\r\n" + 
					"			SET RENT_LATE_FEE = '0.00'\r\n" + 
					"			WHERE RENT_ID = '?'");
			
			pstmt.setString(1, rent);
			
			pstmt.executeUpdate();
			
			pstmt = conn.prepareStatement("UPDATE billing_table"
			+ " SET BILL_BALANCE = '0.00'"
			+ " WHERE BILL_BALANCE LIKE (SELECT BILL_BALANCE FROM rent_table"
			+ " WHERE rent_table.BILL_ID = billing_table.BILL_ID"
			+ " AND RENT_ID = '?')"
			+ " AND BILL_BALANCE != 0"
			+ " AND BILL_ID != '0.00'");
			
			pstmt.setString(1, rent);
			
			pstmt.executeUpdate();
			
			
		}
		
		q = "SELECT purchase_table.TRANS_ID FROM purchase_table, customer_table, transaction_table\r\n" + 
				" WHERE  customer_table.CUS_ID = transaction_table.CUS_ID\r\n" + 
				" AND transaction_table.TRANS_ID = purchase_table.TRANS_ID \r\n" + 
				" AND (customer_table.CUS_ID = " + v + " OR customer_table.CUS_EMAIL_ADDRESS = '"+ v +"')";

		rset = stmt.executeQuery(q);
		
		while(rset.next())
		{
			String trans = rset.getString("purchase_table.TRANS_ID");
		
			pstmt = conn.prepareStatement ("UPDATE billing_table\r\n" + 
					"			SET BILL_BALANCE = '0.00'\r\n" + 
					"			WHERE TRANS_ID = '?'");
			
			pstmt.setString(1, trans);
			
			pstmt.executeUpdate();			
			
		}
		
		System.out.println("All your dues have been paid for.");
		
				
		return y;
	}

	public static boolean checkout(List<Cart> cart, Statement stmt, Connection conn, boolean y) throws SQLException {
		
		DecimalFormat df = new DecimalFormat("0.00");
		int cusID = 0;
		
		boolean present = false;
		
		if(!cart.isEmpty())
			System.out.println("These are the items in your cart.");
		else
			System.out.println("Your cart is empty.");
		for(int i = 0; i < cart.size(); i++)
		{
			System.out.printf((i+1) + ". "); cart.get(i).display();			
		}
		System.out.println("");
		
		if(!cart.isEmpty())
		{
					
			System.out.println("Are you ready to check out? Y/N");
			
			String v = getString().toUpperCase();
			if(v.length() > 1)
				v = v.substring(0,1);
			if(v.equals("Y"))
			{	
				if(y == false)
				{
					System.out.println("Do you have an existing account? Y/N");
					while(true)
					{
						v = getString().toUpperCase();
						if(v.length() > 1)
							v = v.substring(0,1);
						if(v.equals("Y"))
						{
							System.out.println("Enter your email or cus id");
							v = getString().toLowerCase();						
							
							String q = "SELECT CUS_EMAIL_ADDRESS, CUS_ID\r\n" + 
									" FROM customer_table\r\n"
									+ " WHERE CUS_EMAIL_ADDRESS = '"+ v +"' OR CUS_ID = '"+ v +"'";
							
							ResultSet rset = stmt.executeQuery(q);	
							
							String check = rset.getString("CUS_EMAIL_ADDRESS").toLowerCase();
							String cusID2 = rset.getString("CUS_ID");
								
							if(check.equals(v) || cusID2.equals(v))
							{
								if(check.equals(v))
									email = v;								
								present = true;								
								y = true;
								break;								
							}
							else
							{
								System.out.println("No account found.");
								System.out.println("Would you like to re-enter your account info\n"
										+ "Enter 'Y' to re-enter or 'N' to create a new account");
								
							}
							
						}
						else if (v.equals("N"))
							break;
						else
							System.out.println("Input Not Accepted. Please type y/n");
					}
				}
				
				if(y == false)
				{
					System.out.println("Please enter you Last Name, First Name, Middle Int. (if N/A press enter NA), \n"
								+ " Email Address, Phone Number (i.e 123-456-7890), and Home Address");
					
					System.out.print("Last Name: ");
					lName = getString();
					System.out.print("First Name: ");
					fName = getString();
					System.out.print("Middle Initial: ");
					mInit = getString().toUpperCase();
					if(mInit.equals("NA"))
						mInit = "";
					else if(mInit.length() > 1)
						mInit= mInit.substring(0,1);
					System.out.print("Email Address: ");
					email = getString();
					System.out.print("Phone Number: ");
					phone = getString();
					System.out.print("Home Address: ");
					home = getString();
					
					String s = "";
					while(!s.equals("Y"))
					{
						System.out.println("Is this information correct Y/N\n"
							+ "1. Last Name: " + lName + "\n2. First Name: " + fName
							+ "\n3. Middidle Initial: " + mInit + "\n4. Email Address: "
							+ email + "\n5. Phone Number: " + phone + "\n6. Home Address: " + home);
						
						s = getString().toUpperCase();
						if(s.length() > 1)
							s = s.substring(0,1);
						
						if(s.equals("N"))
						{
							String p = "";
							
							while(p.equals("Y"))
							{
								System.out.println("Pick the number choice you wish to change\n"
										+ "Or enter 'back");
								s = getString().toLowerCase();
								if(s == "1" || s == "2" || s == "3" || s == "4" || s == "5" || s == "6")
								{
									System.out.println("What would you like to change it to?");
									if(s == "3")
										System.out.println("If you do not have a middle initial enter NA");
									p = getString();
								}
								if(s.equals("back")) break;
								switch(s)
								{								
									case "1":
										lName = p;
										break;
									case "2":									
										fName = p;
										break;
									case "3":
										if(p.toUpperCase().equals("NA"))
											mInit = "";
										else if(p.length() > 1)
											mInit = p.substring(0,1);
										break;
									case "4":
										email = p;
										break;
									case "5":
										phone = p;
										break;
									case "6":
										home = p;
										break;									
									case "back":
										break;
									default:
										System.out.println("Input Not Accepted, Please Try Again");
								}							
								if(s == "1" || s == "2" || s == "3" || s == "4" || s == "5" || s == "6")
								{
									p ="";
									System.out.println("Would you like to change something else Y/N");
									while(p != "N" && p != "Y")
									{	
										p = getString().toUpperCase();
										if(p.length() > 1)
											p = p.substring(0,1);
										if(p != "N" && p != "Y")
										System.out.println("Input Not Accepted, Please Try Again");
									}
								}						
							}
						}
						else if(!s.equals("Y"))
						System.out.println("Input Not Accepted");					
					}	
					y = true;			
				}				
				
				String q = "SELECT CUS_EMAIL_ADDRESS, CUS_ID\r\n" + 
						" FROM customer_table";
				
				ResultSet rset = stmt.executeQuery(q);	
				
				while (rset.next()) {
					String check = rset.getString("CUS_EMAIL_ADDRESS");
					cusID = rset.getInt("CUS_ID");
					if(check.equals(email))
						present = true;
				}
				
				if(present == true)
				{
					System.out.println("Welcome back");
				}
				if(present != true)
				{	
					PreparedStatement  pstmt =
		      			conn.prepareStatement ("INSERT INTO customer_table " +
		      					"VALUES (?, ?, ?, ?, ?, ?, ?)");
					
					pstmt.setInt(1, cusID + 1);
					pstmt.setString(2, lName);
					pstmt.setString(3, fName);
					pstmt.setString(4, mInit);
					pstmt.setString(5, email);
					pstmt.setString(6, phone);
					pstmt.setString(7, home);
					
					pstmt.executeUpdate();
					
					System.out.println("You have been added to the database");				
				}	
				
				q = "SELECT TRANS_ID\r\n" + 
						" FROM transaction_table";
				
				rset = stmt.executeQuery(q);
				
				int transID = 0;
				int transQt = 0;
				float transAmount = 0;
				
				while (rset.next()) {
					transID = rset.getInt("TRANS_ID");					
				}			
				for(int i = 0; i < cart.size(); i++)
				{ 
					transQt = cart.get(i).getQuantity();
					transAmount += (transQt * cart.get(i).getPrice());
					transQt = 0;
				}			
				
				PreparedStatement pstmt = conn.prepareStatement ("INSERT INTO transaction_table " +
		   					"VALUES (?, ?, ?, ?, ?)"); 
				
				pstmt.setInt(1, transID + 1);
				pstmt.setInt(2, transQt);
				pstmt.setString(3, df.format(transAmount));
				pstmt.setString(4, getDate());
				pstmt.setInt(5, cusID);
				
				pstmt.executeUpdate();					
				
				pstmt = conn.prepareStatement ("INSERT INTO billing_table " +
      					"VALUES (?, ?, ?, ?, ?)"); 
		
				pstmt.setInt(1, transID+1);
				pstmt.setInt(2, transID+1);
				pstmt.setInt(3, transQt);
				pstmt.setString(4, df.format(transAmount));
				pstmt.setString(5, df.format(transAmount));
				
				pstmt.executeUpdate();
				
				for(int i = 0; i < cart.size(); i++)
				{
					if(cart.get(i).getPurch().equals("Buy"))
					{
						
						q = "SELECT PURCHASE_ID\r\n" + 
								" FROM purchase_table";
						rset = stmt.executeQuery(q);
						
						int purchID = 0;
						
						while (rset.next()) {
							purchID = rset.getInt("PURCHASE_ID") + 1;					
						}					
						
						pstmt =
							conn.prepareStatement ("INSERT INTO purchase_table " +
									"VALUES (?, ?, ?, ?, ?, ?, ?)");
							
							pstmt.setInt(1, purchID);
							pstmt.setInt(2, transID+1);
							pstmt.setInt(3, transID+1);
							pstmt.setString(4, cart.get(i).getISBN());
							pstmt.setString(5, df.format(cart.get(i).getPrice()*cart.get(i).getQuantity()));
							pstmt.setString(6, getDate());
							pstmt.setString(7, getBuyReturnDate());
							
							pstmt.executeUpdate();					
					}
					else
					{
						q = "SELECT RENT_ID\r\n" + 
								" FROM rent_table";
						rset = stmt.executeQuery(q);
						
						int rentID = 0;
						
						while (rset.next()) {
							rentID = rset.getInt("PURCHASE_ID") + 1;					
						}
						
						pstmt =
							conn.prepareStatement ("INSERT INTO rent_table " +
									"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
							
							pstmt.setInt(1, rentID);
							pstmt.setInt(2, transID+1);
							pstmt.setInt(3, transID+1);
							pstmt.setString(4, cart.get(i).getISBN());
							pstmt.setString(5, df.format(cart.get(i).getPrice()*cart.get(i).getQuantity()));	
							pstmt.setInt(6, cart.get(i).getQuantity());							
							pstmt.setString(7, cart.get(i).getCondition());
							if(cart.get(i).getCondition().toLowerCase().equals("New"))
								pstmt.setInt(8, 4);
							else
								pstmt.setInt(8, 5);
							pstmt.setString(9, getDate());
							pstmt.setString(10, getRentReturnDate());
							pstmt.setString(11, null);
							pstmt.setString(12, cart.get(i).getCondition());
							pstmt.setInt(13, 0);					
							
							pstmt.executeUpdate();	
					}
				}				
				
				for(int i = 0; i < cart.size();i++)
				{
					q = "SELECT INV_COPIES"
							+ " FROM inventory table"
							+ " WHERE INV_ISBN_ID = " + cart.get(i).getISBN();
					rset = stmt.executeQuery(q);	
										
					int copy = rset.getInt("INV_COPIES");						
										
					q = " UPDATE inventory_table\r\n" + 
							" SET INV_COPIES = '?' \r\n" 
							+ " WHERE INV_ISBN_ID =" + cart.get(i).getISBN();
					pstmt =
							conn.prepareStatement (q);
							
							pstmt.setInt(1, copy - cart.get(i).getQuantity());					
							
							pstmt.executeUpdate();	
				}
				
				cart = new ArrayList<Cart>();
			}// End are you ready to check out
		}
		return y;
	}
	
	public static boolean returnBook(Statement stmt, Connection conn, boolean y) throws SQLException, ParseException
	{
		PreparedStatement pstmt;
		String v = "";
		if(y == false)
		{
			System.out.println("Please Enter your your receipt number (billing id) or your email");
			v = getString().toLowerCase();
		}
		else
			 v = email;
		while(!v.equals("exit"))
		{			
			String q = "SELECT CUS_EMAIL_ADDRESS, BILL_ID FROM "
					+ "billing_table, customer_table,transaction_table\r\n" + 
					" WHERE (CUS_EMAIL_ADDRESS = '" + v + "'\r\n" + 
					" OR BILL_ID = '" + v + "')\r\n" + 
					" AND billing_table.TRANS_ID = transaction_table.TRANS_ID\r\n" + 
					" AND transaction_table.CUS_ID = customer_table.CUS_ID";
			ResultSet rset = stmt.executeQuery(q);
			
			int countr = 0;
			
			while (rset.next()) {
				countr++;
				String genre = rset.getString("CUS_EMAIL_ADDRESS");
				String cost = rset.getString("BILL_ID");			
			}
			if(countr == 0)
			{
				System.out.println("No Result Found For" + v);
				System.out.println("Enter again or type 'exit' to return to main menu");
				v = getString().toLowerCase();
			}
			else
			{
				email = v;
				y = true;
				while(true)
				{
					System.out.println("Are you returning a 'Rent'(rented) or 'Buy'(bought) book?");
					String b = getString().toLowerCase();
					while(true)
					if(b.equals("buy"))
					{
						System.out.println("Please type the ISBN of the book you are returning");
						b = getString();
						
						q = "SELECT DISTINCT PURCHASE_ID, purchase_table.INV_ISBN_ID, INV_COST, PURCHASE_QTY, purchase_table.TRANS_PURCHASE_DATE\r\n" + 
								" FROM transaction_table, billing_table, purchase_table, customer_table, inventory_table\r\n" + 
								" WHERE purchase_table.TRANS_ID = transaction_table.TRANS_ID\r\n" + 
								" AND (purchase_table.BILL_ID = "+ v +" OR CUS_EMAIL_ADDRESS = '"+  v +"')\r\n" + 
								" AND transaction_table.CUS_ID = customer_table.CUS_ID\r\n" + 
								" AND purchase_table.INV_ISBN_ID = "+ b + "\r\n" + 
								" AND inventory_table.INV_ISBN_ID = purchase_table.INV_ISBN_ID";
						
						rset = stmt.executeQuery(q);						
						
						List<DateCheck> quant = new ArrayList<DateCheck>();
						
						int rtrn = 0;
						boolean m = false;
						
						while (rset.next()) {
							
							String purchID = rset.getString("PURCHASE_ID");
							String isbn = rset.getString("purchase_table.INV_ISBN_ID");
							float invCost = rset.getFloat("INV_COST");
							int qty = rset.getInt("PURCHASE_QTY");
							String date = rset.getString("TRANS_PURCHASE_DATE");						
							if(getDayDiff(date) <= 30 && qty != 0)
							{
								rtrn += qty;
								quant.add(new DateCheck(purchID, isbn, invCost, qty, date));
							}
							else
								m = true;
							
						}
						
						int amnt = 0;
						
						while(true)
						try
						{
							System.out.println("How many books are you returning?");	
							amnt = getInt();
							if(amnt < 0 || amnt > rtrn)
								System.out.println("You don't have " + amnt + " books");
							else if (rtrn == 0 && m == true)
								System.out.println("Sorry but some of these books are passed the available return date.");
							else if(amnt == 0)
								System.out.println("What are you doing here you don't have anything to return?\n"
										+ "Did you come  to see lil' o' me?");
							else
								break;
						}catch(NumberFormatException e)
						{
							System.out.println("Input Not Accepted, Enter a number.");
							amnt = getInt();
						}
						
						for(int i = 0; i < quant.size(); i++)
						{								
							q = "UPDATE purchase_table "
									+ " SET PURCHASE_QTY = ?, "
									+ " PURCHASE_RETURN_DATE = ? "
									+ " PURCHASE_COST = FORMAT((SELECT (PURCHASE_QTY * INV_COST) AS PURCHASE_COST "
									+ " FROM inventory_table WHERE purchase_table.INV_ISBN_ID = inventory_table.INV_ISBN_ID),2) "
									+ " WHERE PURCHASE_ID = '?'";
							
							pstmt = conn.prepareStatement(q);
							
							if(amnt >= quant.get(i).getQuant())
							{	
								pstmt.setInt(1, 0);
								pstmt.setString(2, getDate());
							}
							else								
							{
								pstmt.setInt(1, amnt - quant.get(i).getQuant());
								pstmt.setString(2, null);
							}
							
							pstmt.setString(3, quant.get(i).getPurchID());								
							pstmt.executeUpdate();
							
							q = "SELECT BILL_ID FROM purchase_table WHERE PURCHASE_ID = '" + quant.get(i).getPurchID() +"'";
							
							rset = stmt.executeQuery(q);
							
							String billID = rset.getString("BILL_ID");
							
							q = "UPDATE billing_table"
								+ " SET BILL_BALANCE =  FORMAT((SELECT(BILL_BALANCE - (? * INV_COST)) AS BILL_BALANCE"
								+ " FROM inventory_table, purchase_table WHERE purchase_table.INV_ISBN_ID = inventory_table.INV_ISBN_ID"
								+ " AND billing_table.BILL_ID = purchase_table.BILL_ID" 
								+ " AND PURCHASE_ID = '?'),2)"
								+ " WHERE BILL_ID = '?'";
							
							pstmt = conn.prepareStatement(q);
							
							if(amnt >= quant.get(i).getQuant())
								pstmt.setInt(1, quant.get(i).getQuant());
							else								
								pstmt.setInt(1, amnt);
							pstmt.setString(2, quant.get(i).getPurchID());
							pstmt.setString(3, billID);
							pstmt.executeUpdate();								
							
							q = "UPDATE inventory_table "
								+ " SET INV_COPIES = (INV_COPIES + ?)"
								+ " WHERE INV_ISBN_ID = '?'";
							
							pstmt = conn.prepareStatement(q);
							
							if(amnt >= quant.get(i).getQuant())
								pstmt.setInt(1, quant.get(i).getQuant());
							else								
								pstmt.setInt(1, amnt);
							pstmt.setString(2, quant.get(i).getISBN());
							pstmt.executeUpdate();		
							
							amnt -= quant.get(i).getQuant();
							
						}					
						
						break;						
						
					}	
					else if(b.equals("rent"))
					{	
						System.out.println("Please type the ISBN of the book you are returning");
						b = getString();
						
						q = "SELECT DISTINCT RENT_ID, rent_table.INV_ISBN_ID, INV_COST, RENT_QTY, RENT_CHECKOUT_DATETIME \r\n" + 
								" FROM transaction_table, billing_table, rent_table, customer_table, inventory_table\r\n" + 
								" WHERE rent_table.TRANS_ID = transaction_table.TRANS_ID\r\n" + 
								" AND (rent_table.BILL_ID = '"+ v +"' OR CUS_EMAIL_ADDRESS = '" + v +"')\r\n" + 
								" AND transaction_table.CUS_ID = customer_table.CUS_ID\r\n" + 
								" AND rent_table.INV_ISBN_ID = "+ b +"\r\n" + 
								" AND inventory_table.INV_ISBN_ID = rent_table.INV_ISBN_ID";
						
						rset = stmt.executeQuery(q);
						
						List<DateCheck> quant = new ArrayList<DateCheck>();
						
						int rtrn = 0;
						boolean m = false;
						
						while (rset.next()) {
							
							String purchID = rset.getString("RENT_ID");
							String isbn = rset.getString("rent_table.INV_ISBN_ID");
							float invCost = rset.getFloat("INV_COST");
							int qty = rset.getInt("RENT_QTY");
							String date = rset.getString("RENT_CHECKOUT_DATETIME");						
							if(getDayDiff(date) <= 15 && qty != 0)
							{
								rtrn += qty;
								quant.add(new DateCheck(purchID, isbn, invCost, qty, date));
							}
							else
								m = true;							
						}
						
						int amnt = 0;
						
						while(true)
						try
						{
							System.out.println("How many books are you returning?");	
							amnt = getInt();
							if(amnt < 0 || amnt > rtrn)
								System.out.println("You don't have " + amnt + "numbers of books");
							else if (rtrn == 0 && m == true)
								System.out.println("Sorry but some of these books are passed the available return date.");
							else if(amnt == 0)
								System.out.println("What are you doing here you don't have anything to return?\n"
										+ "Did you come  to see lil' o' me?");
							else
								break;
						}catch(NumberFormatException e)
						{
							System.out.println("Input Not Accepted, Enter a number.");
							amnt = getInt();
						}
						
						for(int i = 0; i < quant.size(); i++)
						{								
							q = "UPDATE rent_table "
									+ " SET RENT_QTY = ?, "
									+ " RENT_CHECKOUT_DATETIME = ? "
									+ " RENT_COST = FORMAT((SELECT (RENT_QTY * INV_COST) AS RENT_COST "
									+ " FROM inventory_table WHERE rent_table.INV_ISBN_ID = inventory_table.INV_ISBN_ID),2) "
									+ " WHERE RENT_ID = '?'";
							
							pstmt = conn.prepareStatement(q);
							
							if(amnt >= quant.get(i).getQuant())
							{	
								pstmt.setInt(1, 0);
								pstmt.setString(2, getDate());
							}
							else								
							{
								pstmt.setInt(1, amnt - quant.get(i).getQuant());
								pstmt.setString(2, null);
							}
							
							pstmt.setString(3, quant.get(i).getPurchID());								
							pstmt.executeUpdate();
							
							q = "SELECT BILL_ID FROM rent_table WHERE rent_ID = '" + quant.get(i).getPurchID() +"'";
							
							rset = stmt.executeQuery(q);
							
							String billID = rset.getString("BILL_ID");
							
							q = "UPDATE billing_table"
								+ " SET BILL_BALANCE =  FORMAT((SELECT(BILL_BALANCE - (? * INV_COST)) AS BILL_BALANCE"
								+ " FROM inventory_table, rent_table WHERE purchase_table.INV_ISBN_ID = inventory_table.INV_ISBN_ID"
								+ " AND billing_table.BILL_ID = rent_table.BILL_ID" 
								+ " AND RENT_ID = '?'),2)"
								+ " WHERE BILL_ID = '?'";
							
							pstmt = conn.prepareStatement(q);
							
							if(amnt >= quant.get(i).getQuant())
								pstmt.setInt(1, quant.get(i).getQuant());
							else								
								pstmt.setInt(1, amnt);
							pstmt.setString(2, quant.get(i).getPurchID());
							pstmt.setString(3, billID);
							pstmt.executeUpdate();								
							
							q = "UPDATE inventory_table "
								+ " SET INV_COPIES = (INV_COPIES + ?)"
								+ " WHERE INV_ISBN_ID = '?'";
							
							pstmt = conn.prepareStatement(q);
							
							if(amnt >= quant.get(i).getQuant())
								pstmt.setInt(1, quant.get(i).getQuant());
							else								
								pstmt.setInt(1, amnt);
							pstmt.setString(2, quant.get(i).getISBN());
							pstmt.executeUpdate();		
							
							amnt -= quant.get(i).getQuant();								
													
							
						}					
						
						break;				
					}
					else
					{
						System.out.println("Input Not Accepted, Enter 'Buy' or 'Rent'");
						b = getString().toLowerCase();
					}
					
					System.out.println("Would you like to return another Y/N ?");
					b = getString().toUpperCase();
					
					while(true)
					if(b.equals("N")) break;
					else if(!b.equals("Y"))
						System.out.println("Input Not Accepted, Please enter y/n");
					
					if(b.equals("N")) break;				
					
				}
				
			}
		}
		return y;
				
	}

	private static int getDayDiff(String date) throws ParseException {
				
		 Calendar cal1 = new GregorianCalendar();
	     Calendar cal2 = new GregorianCalendar();

	     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	     Date date2 = sdf.parse(LocalDate.now()+"");
	     cal1.setTime(date2);
	     date2 = sdf.parse(date);
	     cal2.setTime(date2);

	     return (int) ( (cal1.getTime().getTime() - cal2.getTime().getTime()) / (1000 * 60 * 60 * 24));
		
	}

	private static void report(Statement stmt, Connection conn) throws SQLException {
		
		LocalDate currentdate = LocalDate.now();
		
		System.out.println("Which Title/Genre do you wish to search for");
		String v = getString();
		
		System.out.println("Report By Title");
		
		String q = "SELECT INV_TITLE, INV_GENRE, ROUND(SUM(RENT_COST + PURCHASE_COST),2) "
				+ "AS COST FROM inventory_table, rent_table, purchase_table\r\n"
				+ " Where purchase_table.INV_ISBN_ID = inventory_table.INV_ISBN_ID\r\n" 
				+ " AND rent_table.INV_ISBN_ID = inventory_table.INV_ISBN_ID\r\n"
				+ " AND (INV_TITLE LIKE '%"+ v +"%' OR INV_GENRE LIKE '%" + v +"%')";
		ResultSet rset = stmt.executeQuery(q);
		
		int countr = 0;
		
		while (rset.next()) {
			System.out.print((++countr) + ". ");
			String title = rset.getString("INV_TITLE");
			String genre = rset.getString("INV_GENRE");
			String cost = rset.getString("COST");
			
			System.out.printf("%-22s | %-22s |$%s\n"
					, title, genre, cost);				
		}
		if(countr == 0)
			System.out.println("No Result Found.");	
		
		System.out.println("");
		
		Calendar c = Calendar.getInstance();
		
		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		
		DateFormat df = new SimpleDateFormat("yyyy-M-d");
		String d = df.format(c.getTime());
		
		c.add(Calendar.DATE, 6);
		
		String f = df.format(c.getTime());	
		
		System.out.println("Weekly Earnings: ");
		
		q = "SELECT CONVERT(TRANS_PURCHASE_DATE, DATE) TRANS_PURCHASE_DATE, "
				+ "ROUND(SUM(TRANS_AMOUNT),2)  AS TRANS_AMOUNT "
				+ "FROM transaction_table "
				+ "WHERE (TRANS_PURCHASE_DATE BETWEEN CAST('" + d + "' AS DATE) AND "
				+ "CAST('"+ f +"' AS DATE))";
	     
		countr = 0;
		rset = stmt.executeQuery(q);
		while (rset.next()) {
			
			String date = rset.getString("TRANS_PURCHASE_DATE");
			String cost = rset.getString("TRANS_AMOUNT");
			
			if(date != null)
			{
				System.out.print((++countr) + ". ");
				System.out.printf("%-22s | $%s\n" , date, cost);
			}
			else
				System.out.println("You made $0.00 this week.");
		}
		if(countr == 0)
			System.out.println("You made $0.00 this week.");
		
		System.out.println("");
		
		System.out.println("Monthly Earnings: ");
		
		d = currentdate.getYear() + "-" + currentdate.getMonthValue() + "-" + 1;
		f = currentdate.getYear() + "-" + currentdate.getMonthValue() + "-" + getMaxDays(currentdate.getMonthValue());
		
		q = "SELECT CONVERT(TRANS_PURCHASE_DATE, DATE) TRANS_PURCHASE_DATE, "
				+ "ROUND(SUM(TRANS_AMOUNT),2) AS TRANS_AMOUNT "
				+ "FROM transaction_table "
				+ "WHERE (TRANS_PURCHASE_DATE BETWEEN CAST('" + d + "' AS DATE) AND "
				+ "CAST('"+ f +"' AS DATE))";
	     
		countr = 0;
		rset = stmt.executeQuery(q);
		while (rset.next()) {
			
			String date = rset.getString("TRANS_PURCHASE_DATE");
			String cost = rset.getString("TRANS_AMOUNT");
			
			if(date != null)
			{
				System.out.print((++countr) + ". ");
				System.out.printf("%-22s | $%s\n" , date, cost);
			}
			else
				System.out.println("You made $0.00 this month.");			
		}
		if(countr == 0)
			System.out.println("You made $0.00 this month.");
		
		System.out.println("");
		
		System.out.println("Yearly Earnings: ");
		
		d = currentdate.getYear() + "-" + 1 + "-" + 1;
		f = currentdate.getYear() + "-" + 12 + "-" + getMaxDays(12);
		
		q = "SELECT CONVERT(TRANS_PURCHASE_DATE, DATE) TRANS_PURCHASE_DATE, "
				+ "ROUND(SUM(TRANS_AMOUNT),2) AS TRANS_AMOUNT "
				+ "FROM transaction_table "
				+ "WHERE (TRANS_PURCHASE_DATE BETWEEN CAST('" + d + "' AS DATE) AND "
				+ "CAST('"+ f +"' AS DATE))";
	     
		countr = 0;
		rset = stmt.executeQuery(q);
		while (rset.next()) {
			
			String date = rset.getString("TRANS_PURCHASE_DATE");
			String cost = rset.getString("TRANS_AMOUNT");
			
			if(date != null)
			{
				System.out.print((++countr) + ". ");
				System.out.printf("%-22s | $%s\n" , date, cost);
			}
			else
				System.out.println("You made $0.00 this month.");			
		}
		if(countr == 0)
			System.out.println("You made $0.00 this year.");	
		
		System.out.println("");
		
	}
	

	private static void balanceFees(Statement stmt, Connection conn) throws SQLException, ParseException {
		
		DecimalFormat df = new DecimalFormat("0.00");
		PreparedStatement pstmt;		
		
		System.out.println("Which user would you like to look for?");
		
		String v = getString();
		while(true)
		{
			String q =	"SELECT DISTINCT RENT_ID, RENT_COST, RENT_QTY, RENT_CHECKOUT_DATETIME,"
					+ " RENT_LATE_FEE FROM rent_table, customer_table, transaction_table"
					+ " WHERE RENT_ACTUAL_RETURN_DATE IS NOT NULL"
					+ " AND RENT_QTY > 0"
					+ " AND transaction_table.CUS_ID = " + v
					+ " AND transaction_table.TRANS_ID = rent_table.TRANS_ID";
			ResultSet rset = stmt.executeQuery(q);
			
			while(rset.next())
			{
				String rentID = rset.getString("RENT_ID");
				float rentCost = rset.getFloat("RENT_COST");
				int rentQty = rset.getInt("RENT_QTY");
				int rentPeriod = rset.getInt("RENT_PERIOD");
				String rentDate = rset.getString("RENT_CHECKOUT_DATETIME");
				float rentFee = rset.getFloat("RENT_LATE_FEE");
				
				if(getDayDiff(rentDate) > 0 && getDayDiff(rentDate) <= 15)
				{
					
					q = "UPDATE rent_table"
							+" SET RENT_LATE_FEE = ROUND(RENT_LATE_FEE + 2.00, 2)"
							+" WHERE RENT_ID = '?'";
					pstmt = conn.prepareStatement(q);
					
					pstmt.setString(1, rentID);
					pstmt.executeUpdate();
					
				}
				else if(getDayDiff(rentDate) > 15)
				{
					q = "UPDATE rent_table"
							+" SET RENT_LATE_FEE = ROUND(?, 2)"
							+" WHERE RENT_ID = '?'";
					pstmt = conn.prepareStatement(q);
					
					pstmt.setString(1, rentID);
					pstmt.setFloat(2, rentCost);
					pstmt.executeUpdate();
					
				}
				
			}
			
			q =	"SELECT TRANS_ID FROM billing_table";
			rset = stmt.executeQuery(q);
			
			while(rset.next())
			{
				String transID = getString();
				
				q ="UPDATE billing_table"
				+" SET BILL_BALANCE = FORMAT((SELECT SUM(RENT_COST) + SUM(RENT_LATE_FEE) AS COST FROM rent_table"
				+" WHERE TRANS_ID = '?') + (SELECT  SUM(PURCHASE_COST) AS COST FROM purchase_table"
				+" WHERE TRANS_ID = '?'),2)"
				+" WHERE TRANS_ID = '?'";
				
				pstmt = conn.prepareStatement(q);
				
				pstmt.setString(1,transID);
				pstmt.setString(2,transID);
				pstmt.setString(3,transID);
				
				pstmt.executeUpdate();	
				
			}		
			
			while(true)
			{
				System.out.println("Enter the ID or email: ");		
				String s = getString();
					
				q = "SELECT customer_table.CUS_FNAME, customer_table.CUS_LNAME, inventory_table.INV_TITLE, "
						+ " rent_table.RENT_PERIOD, rent_table.RENT_EXPECTED_RETURN_DATE, \r\n" + 
						" rent_table.RENT_ACTUAL_RETURN_DATE, BILL_BALANCE FROM billing_table, " +
						" customer_table, transaction_table, rent_table, inventory_table\r\n" + 
						" WHERE billing_table.TRANS_ID = transaction_table.TRANS_ID\r\n" + 
						" AND transaction_table.CUS_ID = customer_table.CUS_ID\r\n" + 
						" AND rent_table.TRANS_ID = transaction_table.TRANS_ID\r\n" + 
						" AND rent_table.INV_ISBN_ID = inventory_table.INV_ISBN_ID\r\n" + 
						" AND (customer_table.CUS_ID = '" + s + "' OR customer_table.CUS_EMAIL_ADDRESS = '" + s + "')";
				rset = stmt.executeQuery(q);									
				int countr = 0;
				
				while (rset.next()) {
					System.out.print((++countr) + ". ");
					String fname = rset.getString("CUS_FNAME");
					String lname = rset.getString("CUS_LNAME");
					String title = rset.getString("INV_TITLE");
					String period = rset.getString("RENT_PERIOD");
					String rentex = rset.getString("RENT_EXPECTED_RETURN_DATE");
					String rentat = rset.getString("RENT_ACTUAL_RETURN_DATE");
					float balance = rset.getFloat("BILL_BALANCE");
									
					String price = df.format(balance) + "";
					
					System.out.printf("%s | %-10s | %-21s | %-4s | %-13s | %-13s | $%s\n"
							,fname, lname, title, period, rentex, rentat, price);				
				}
				if(countr == 0)
					System.out.println("No Result Found.");
				
				System.out.println("Would you like to look for another? Y/N");
				
				v = getString().toUpperCase();
				while(true)
					if(v.equals("N") || v.equals("Y")) break;
					else if(!v.equals("Y"))
					{	
						System.out.println("Input Not Accepted, Please enter y/n");
						v = getString().toUpperCase();
					}
				
				if(v.equals("N")) break;
				
			}
		}
		
	}

	public static void locate(Statement stmt) throws SQLException
	{
		DecimalFormat df = new DecimalFormat("0.00");
		String v = "";
		
		while (!v.equals("exit")) {
			System.out.println("\nWhat are you looking for?\n"
					+ "Enter an ISBN or Title name\n" 
					+ "Or enter 'exit' to go back to main menu");							
			
			v = getString().toLowerCase();							
			
			if(v.equals("exit")) break;
			
			String q = "SELECT * \r\n" + 
					"FROM inventory_table \r\n" + 
					"WHERE INV_ISBN_ID LIKE '%" + v + "%' " +
					"OR INV_TITLE LIKE '%" + v + "%'";
			ResultSet rset = stmt.executeQuery(q);									
			int countr = 0;
			
			while (rset.next()) {
				System.out.print((++countr) + ". ");
				String isbn = rset.getString("INV_ISBN_ID");
				String title = rset.getString("INV_TITLE");
				String genre = rset.getString("INV_GENRE");
				String cat = rset.getString("INV_CATEGORY");
				String sub = rset.getString("INV_SUBCATEGORY");
				String author = rset.getString("INV_AUTHOR");
				String publish = rset.getString("INV_PUBLISHER");
				String format = rset.getString("INV_FORMAT");
				String condit = rset.getString("INV_CONDITION");
				String digital = rset.getString("INV_DIGITAL_STATUS");
				String purch = rset.getString("INV_PURCHASE_OPTION");
				float cost = rset.getFloat("INV_COST");
				String stock = rset.getString("INV_IN_STOCK");
				String display = rset.getNString("INV_ON_DISPLAY");
				int copy = rset.getInt("INV_COPIES");
								
				String price = df.format(cost) + "";
				
				System.out.printf("%s | %-22s | %-21s | %-9s | %-9s | %-13s | %-30s | %-9s | %-4s | %s | %-4s | $%-6s | %s | %s | %d\n"
						,isbn, title, genre, cat, sub, author, publish, format, condit, digital, purch, price, stock, display, copy);				
			}
			if(countr == 0)
				System.out.println("No Result Found.");			
		}
	}
	
	public static void update(Connection conn)throws SQLException {
		
		DecimalFormat df = new DecimalFormat("0.00");
		Statement stmt = conn.createStatement ();	
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		String v = "";
		while(!v.equals("N"))
		{		
			String q = "SELECT * \r\n" + 
					"FROM inventory_table \r\n";
			ResultSet rset = stmt.executeQuery(q);									
			int countr = 0;
			
			String spc = " ";
			
			System.out.printf("     ISBN   | %-7sTitle%10s | %-5sGenre%11s | Category  | SubCat    | Author%7s"
					+ " | %9sPublisher%13s| Format    | Cond | Digit | PrOpt |  Cost  | InStock | DISP | Copies\n"
					+ "------------------------------------------------------------------------------------------------"
					+ "-----------------------------------------------------------------------------------------------\n"
					,spc,spc,spc,spc,spc,spc,spc,spc,spc);
			
			
			while (rset.next()) {
				System.out.print((++countr) + ". ");
				String isbn = rset.getString("INV_ISBN_ID");
				String title = rset.getString("INV_TITLE");
				String genre = rset.getString("INV_GENRE");
				String cat = rset.getString("INV_CATEGORY");
				String sub = rset.getString("INV_SUBCATEGORY");
				String author = rset.getString("INV_AUTHOR");
				String publish = rset.getString("INV_PUBLISHER");
				String format = rset.getString("INV_FORMAT");
				String condit = rset.getString("INV_CONDITION");
				String digital = rset.getString("INV_DIGITAL_STATUS");
				String purch = rset.getString("INV_PURCHASE_OPTION");
				float cost = rset.getFloat("INV_COST");
				String stock = rset.getString("INV_IN_STOCK");
				String display = rset.getNString("INV_ON_DISPLAY");
				int copy = rset.getInt("INV_COPIES");
								
				String price = df.format(cost) + "";
				
				System.out.printf("%s | %-22s | %-21s | %-9s | %-9s | %-13s | %-30s | %-9s | %-4s | %s | %-4s | $%-6s | %s | %s | %d\n"
						,isbn, title, genre, cat, sub, author, publish, format, condit, digital, purch, price, stock, display, copy);					
			}
			if(countr == 0)
				System.out.println("No Result Found.");
			else
			{
				System.out.println(countr);
				int row = 0;
				while(true)
				{
					try
					{
						System.out.print("Enter the row number you wish to update: ");
						row = getInt();
						if(row > 0 && row <= countr)
							break;
						else
							System.out.println("Row " + row + " does not exist");
						
					}catch(NumberFormatException e)
					{
						System.out.println("Input Not Accepted");
					}
				}
				String column = "";
				String change = "";
				float cost = 0;
				int copy = 0;
				while(true)
				{
					try
					{
						System.out.print("Enter the column name you wish to update: ");
						column = getString().toUpperCase();
						if(column.equals("INV_TITLE")||column.equals("INV_GENRE")||column.equals("INV_CATEGORY")||column.equals("INV_SUBCATEGORY") || column.equals("SUBCAT")
								||column.equals("INV_AUTHOR")||column.equals("INV_PUBLISHER")||	column.equals("INV_FORMAT") || column.equals("INV_CONDITION"))
						{
							if(column.equals("SUBCAT"))
								column = "INV_SUBCATEGORY";
							System.out.println("What would you like to change it to?");
							change = getString();
							
							while(true)
							{									
								System.out.println(change + " Is this correct? Y/N");
								v = getString().toUpperCase();
								if(v.length() > 1)
									v = v.substring(0,1);
								if(v.equals("Y")) break;								
								else if(v.equals("N"))
								{
									System.out.println("What would you like to change it to?");
									change = getString();
									if(v.length() > 1)
										v = v.substring(0,1);
								}
								else if (!v.equals("N"));
									System.out.println("Input Not Accepted");
							}
						}
						else if(column.equals("INV_ON_DISPLAY") || column.equals("INV_DIGITAL_STATUS"))
						{
							System.out.println("Enter Y/N? for Display and Digital Status");
							change = getString().toUpperCase();
							if(change.length() > 1)
									change = change.substring(0,1);
							
							while(!change.equals("Y") || !change.equals("N"))
							{								
								if(!change.equals("Y") || !change.equals("N"))
								{
									System.out.println("Input Not Accepted, please try again");
									change = getString().toUpperCase();
								}
								else if(change.equals("Y") || change.equals("N"));
								{
									while(true)
									{									
										
										System.out.println(change + " Is this correct? Y/N");
											v = getString().toUpperCase();
										if(v.length() > 1)
											v = v.substring(0,1);
										if(v.equals("Y")) break;								
										else if(v.equals("N"))
										{
											System.out.println("What would you like to change it to?");
											change = getString();
											if(v.length() > 1)
												v = v.substring(0,1);
										}
										else if (!v.equals("N"));
											System.out.println("Input Not Accepted");										
									}									
								}
							}
						}
						else if(column.equals("INV_COST"))
						{							
							while(true)
							{
								System.out.println("What would you like to change it to?");
								System.out.print("INV_COST: ");
								try	{
									Scanner string = new Scanner(scan.next());
									string.useDelimiter("\\$");
									cost = string.nextFloat();			
									if(cost < 0)
										System.out.println("Cost cannot be negative");
									else 
									{
										while(true)
										{									
											System.out.println(cost + " Is this correct? Y/N");
											v = getString().toUpperCase();
											if(v.length() > 1)
												v = v.substring(0,1);
											if(v.equals("Y")) break;								
											else if(v.equals("N"))
											{
												System.out.println("What would you like to change it to?");
												string = new Scanner(scan.next());
												string.useDelimiter("\\$");
												cost = string.nextFloat();
											}
											else if (!v.equals("N"));
												System.out.println("Input Not Accepted");
										}
										break;
									}
								}catch(InputMismatchException e){
									System.out.println("Input Not Accepted, please try again");				
								}catch(NumberFormatException e)
								{
									System.out.println("Input Not Accepted, please try again");	
								}
							}
						}
						else if(column.equals("INV_COPY"))
						{
							System.out.println("What would you like to change it to?");							
							while(true)
							{		
								System.out.print("INV_COPIES: ");
								try	{
										copy = getInt();
										if(copy < 0)
											System.out.println("Please enter a positive integer.");
										else
										{
											while(true)
											{									
												System.out.println(copy + " Is this correct? Y/N");
												v = getString().toUpperCase();
												if(v.length() > 1)
													v = v.substring(0,1);
												if(v.equals("Y")) break;								
												else if(v.equals("N"))
												{
													System.out.println("What would you like to change it to?");
													cost = getInt();
												}
												else if (!v.equals("N"));
													System.out.println("Input Not Accepted");
											}
											break;
										}
								}catch(NumberFormatException e)	{
									System.out.println("Input Not Accepted, please try again");				
								}
							}
						}
						else if (column.equals("INV_ISBN_ID"))
						{
							System.out.println("What would you like to change it to?");
							change = getString();
							
							String isbn = "";
							v = "";
							while(true)
							{
								q = "SELECT INV_ISBN_ID \r\n" + 
									"FROM inventory_table \r\n" + 
									"WHERE INV_ISBN_ID LIKE '%" + change + "%'";
							
								rset = stmt.executeQuery(q);		
								
								while (rset.next()) {
									isbn = rset.getString("INV_ISBN_ID");
									if(change.equals(isbn))
									{
										System.out.println("This ISBN already exists, please enter another");
										change = getString();
										break;
									}				
								}
								if(!change.equals(isbn))
								{
									while(true)
									{									
										System.out.println(change + " Is this correct? Y/N");
										v = getString().toUpperCase();
										if(v.length() > 1)
											v = v.substring(0,1);
										if(v.equals("Y")) break;								
										else if(v.equals("N"))
										{
											System.out.println("What would you like to change it to?");
											change = getString();
											break;
										}
										else if (!v.equals("N"));
											System.out.println("Input Not Accepted");									
									}
									
								}
								if(v.equals("Y"))
									break;
							}
						}
						else
							System.out.println("Column " + column + " does not exist");
						
						System.out.println("Would you like to change another column for this book? Y/N");
						while(true)
						{							
							v = getString().toUpperCase();
							if(v.length() > 1)
								v = v.substring(0,1);
							if(v.equals("N")) break;								
							else if(v.equals("Y"))
							{
								System.out.println("What would you like to change it to?");
								cost = getInt();
							}
							else if (!v.equals("N"));
								System.out.println("Input Not Accepted");
						}
						if (v.equals("N")) break;
						
					}catch(NumberFormatException e)
					{
						System.out.println("Input Not Accepted");
					}
				}
				q = "UPDATE inventory_table\r\n" + 
						"SET ? = '?' \r\n" 
						+ "WHERE INV_ISBN_ID = (SELECT  INV_ISBN_ID FROM(SELECT INV_ISBN_ID "
						+ "FROM inventory_table ORDER BY INV_ISBN_ID LIMIT ?, 1) AS R)";
				PreparedStatement pstmt = conn.prepareStatement(q);
				
				pstmt.setString(1, column);
				if(column.equals("INV_COST"))
					pstmt.setFloat(2, cost);
				else if (column.equals("INV_COPIES"))
					pstmt.setInt(2, copy);
				else
					pstmt.setString(2, change);
				pstmt.setInt(3, row - 1);
				pstmt.executeUpdate();
				
				System.out.println("Your book has been updated");
				
				System.out.println("Would you like to change another book's data? Y/N");
				v = getString().toUpperCase();
				if(v.length() > 1)
					v = v.substring(0,1);
				while(!v.equals("Y") || !v.equals("N"))
				{
					System.out.println("Input Not Accepted, please enter Y or N");
					v = getString().toUpperCase();
					if(v.length() > 1)
						v = v.substring(0,1);
				}
			}
		}
		
		System.out.println("The Book(s) have been updated");
	}

	public static void delete(Connection conn)throws SQLException {
		
		DecimalFormat df = new DecimalFormat("0.00");
		String v = "";
		while(!v.equals("N"))
		{
			Statement stmt = conn.createStatement ();			
			
			String q = "SELECT * \r\n" + 
					"FROM inventory_table \r\n";
			ResultSet rset = stmt.executeQuery(q);									
			int countr = 0;
			
			String spc = " ";
			
			System.out.printf("     ISBN   | %-7sTitle%10s | %-5sGenre%11s | Category  | SubCat    | Author%7s"
					+ " | %9sPublisher%13s| Format    | Cond | Digit | PrOpt |  Cost  | InStock | DISP | Copies\n"
					+ "-------------------------------------------------------------------------------------------------------"
					+ "-----------------------------------------------------------------------------------------------------\n"
					,spc,spc,spc,spc,spc,spc,spc,spc,spc);
			
			while (rset.next()) {
				System.out.print((++countr) + ". ");
				String isbn = rset.getString("INV_ISBN_ID");
				String title = rset.getString("INV_TITLE");
				String genre = rset.getString("INV_GENRE");
				String cat = rset.getString("INV_CATEGORY");
				String sub = rset.getString("INV_SUBCATEGORY");
				String author = rset.getString("INV_AUTHOR");
				String publish = rset.getString("INV_PUBLISHER");
				String format = rset.getString("INV_FORMAT");
				String condit = rset.getString("INV_CONDITION");
				String digital = rset.getString("INV_DIGITAL_STATUS");
				String purch = rset.getString("INV_PURCHASE_OPTION");
				float cost = rset.getFloat("INV_COST");
				String stock = rset.getString("INV_IN_STOCK");
				String display = rset.getNString("INV_ON_DISPLAY");
				int copy = rset.getInt("INV_COPIES");
								
				String price = df.format(cost) + "";
				
				System.out.printf("%s | %-22s | %-21s | %-9s | %-9s | %-13s | %-30s | %-9s | %-4s |   %-3s | %-4s  | $%-5s |    %s    |   %s   | %d\n"
						,isbn, title, genre, cat, sub, author, publish, format, condit, digital, purch, price, stock, display, copy);					
			}
			if(countr == 0)
				System.out.println("No Result Found.");
			else
			{
				int row = 0;
				while(true)
				{
					try
					{
						System.out.print("Enter the row number you wish to delete: ");
						row = getInt();
						if(row > 0 && row <= countr)
							break;
						else
							System.out.println("Row " + row + " does not exist");
						
					}catch(NumberFormatException e)
					{
						System.out.println("Input Not Accepted");
					}
				}
				q = "DELETE FROM inventory_table WHERE INV_ISBN_ID = (SELECT INV_ISBN_ID FROM "
						+ "(SELECT INV_ISBN_ID FROM inventory_table ORDER BY INV_ISBN_ID LIMIT ?, 1) AS R)";
				PreparedStatement pstmt = conn.prepareStatement(q);
				
				pstmt.setInt(1, row - 1);
				pstmt.executeUpdate();
				
				System.out.println("Your book has been deleted");
				
				System.out.println("Would you like to delete another book? Y/N");
				v = getString().toUpperCase();
				if(v.length() > 1)
					v = v.substring(0,1);
				while(!v.equals("Y") || !v.equals("N"))
				{
					System.out.println("Input Not Accepted, please enter Y or N");
					v = getString().toUpperCase();
					if(v.length() > 1)
						v = v.substring(0,1);
				}
			}
		}
		
		System.out.println("Book(s) has been deleted.");
	}
	
	public static void add(Connection conn) throws SQLException {
		
		LocalDate currentdate = LocalDate.now();
		String v = "";
		while(!v.equals("N"))
		{
			@SuppressWarnings("resource")
			Scanner scan = new Scanner(System.in);
			Statement stmt = conn.createStatement ();
			DecimalFormat df = new DecimalFormat("0.00");
			
			System.out.print("Book ISBN: ");
			String isbn = getString();
			String isbn2 = "";
			while(!isbn.equals(isbn2))
			{
				String q = "SELECT INV_ISBN_ID \r\n" + 
						"FROM inventory_table \r\n" + 
						"WHERE INV_ISBN_ID = '" + isbn + "'";
				
				ResultSet rset = stmt.executeQuery(q);		
					
				while (rset.next()) {
					isbn2 = rset.getString("INV_ISBN_ID");
					if(isbn.equals(isbn2))
					{
						System.out.println("This ISBN already exists, please enter another");
						isbn = getString();
						break;
					}				
				}
				break;
			}		
			System.out.print("INV_TITLE: ");
			String title = getString();
			System.out.print("INV_GENRE: ");
			String genre = getString();
			System.out.print("INV_CATEGORY: ");
			String category = getString();
			System.out.print("INV_SUBCATEGORY \nIf N/A enter NA: ");
			String subCat = getString().toUpperCase();
			if(subCat.equals("NA"))
				subCat = "";
			System.out.print("INV_AUTHOR: ");
			String author = getString();
			System.out.print("INV_PUBLISHER: ");
			String publish = getString();
			System.out.print("INV_FORMAT: ");
			String format = getString();
			System.out.print("INV_CONDITION: ");
			String condition = getString();
			System.out.print("INV_DIGITAL_STATUS \nenter Y/N: ");
			String digital = getString().toUpperCase();
			while(!digital.equals("Y") && !digital.equals("N"))
			{
				if(digital.length() > 1)
					digital= digital.substring(0,1);
				if(!digital.equals("Y") && !digital.equals("N"))
				{
					System.out.println("Input Not Accepted, please try again");
					digital = getString().toUpperCase();
				}
			}
			System.out.print("INV_PURCHASE_OPTION \nenter Rent/Buy");
			String purch = getString();
			while(!purch.equals("Rent") && !purch.equals("Buy"))
			{
				if(!purch.equals("Y") && !purch.equals("N"))
				{
					System.out.println("Input Not Accepted, please enter 'Rent' or 'Buy'");
					purch = getString();
				}
			}
			float cost = 0;
			while(true)
			{
				System.out.print("INV_COST: ");
				try	{
					Scanner string = new Scanner(scan.next().toLowerCase());
					string.useDelimiter("\\$");
					cost = string.nextFloat();			
					if(cost < 0)
						System.out.println("Cost cannot be negative");
					else
						break;
				}catch(InputMismatchException e){
					System.out.println("Input Not Accepted, please try again");				
				}
			}
			System.out.print("INV_ON_DISPLAY \nenter Y/N: ");
			String display = getString().toUpperCase();
			while(!display.equals("Y") && !display.equals("N"))
			{
				if(display.length() > 1)
					display = display.substring(0,1);
				if(!display.equals("Y") && !display.equals("N"))
				{
					System.out.println("Input Not Accepted, please try again");
					display = getString().toUpperCase();
				}
			}
			int copy = 0;
			while(true)
			{		
				System.out.print("INV_COPIES: ");
				try	{
						copy = getInt();
						if(copy < 0)
							System.out.println("Please enter a positive integer.");
						else
							break;
				}catch(NumberFormatException e)	{
					System.out.println("Input Not Accepted, please try again");				
				}
			}
			int year = 0;
			System.out.println("Year of Release: ");
			while(true)
			{
				try	{	
					year = getInt();
					if(year > 1500 && year <= currentdate.getYear()) break;
					else if(year > currentdate.getYear())
						System.out.println("Too far ahead of our time");
					else if(year <= 1600 && year >= 0 )
						System.out.println("I don't think we have books that old");
					else
						System.out.println("we don't have negative years");
				}catch(NumberFormatException e)	{
					System.out.println("Input Not Accepted, please enter a number ");
				}
				
			}
			int month = 0;
			String z = "";
			System.out.println("Book Release Month:");
			while(true)
			{
				try{
					z = getString();
					month = Integer.parseInt(z);		
					if(month > 0 && ((month <= 12 && year < currentdate.getYear()) 
							|| ( month <= currentdate.getMonthValue() && year == currentdate.getYear())))
						break;
					
					else if (!(month < 1 || month > 12))
						System.out.println("That Month hasn't come yet");
					else 
						System.out.println("Intput Not Accepted");
				}catch(NumberFormatException e)
				{
					try 
					{	
						java.util.Date date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(z + "");
						
						Calendar cal = Calendar.getInstance();
						cal.setTime(date);
						
						month = cal.get(Calendar.MONTH) + 1;
						break;
					}catch(ParseException x)
					{
						System.out.println("Input Not Accepted");
					}
				}
			}
			int day = 0;
			int maxDays = getMaxDays(month);
			System.out.println("Book Release Day: ");
			while(true)
			{
				try	{	
					day = getInt();
					if(day > 0 && ((day <= currentdate.getDayOfMonth() &&  year == currentdate.getYear()) 
							|| ( day <= maxDays && year < currentdate.getYear()))) break;
					else if(!(day < 1 || day > maxDays))
						System.out.println("the month does not have that many days");
					else
						System.out.println("Input Not Accepted, enter available numbers in the month");
				}catch(NumberFormatException e)	{
					System.out.println("Input Not Accepted, please enter a number ");
				}
				
			}
			
			String s = "";
			while(!s.equals("Y"))
			{
				System.out.println("Is this information correct Y/N\n"
					+ "1. ISBN_ID: " + isbn + "\n2. INV_TITLE: " + title
					+ "\n3. INV_GENRE: " + genre + "\n4. INV_CATEGORY: "
					+ category + "\n5. INV_SUBCATEGORY: " + subCat + "\n6. INV_AUTHOR: "
					+ author + "\n7. INV_PUBLISHER: " + publish + "\n8. INV_FORMAT: "
					+ format + "\n9. INV_CONDITION: " + condition + "\n10. INV_DIGITAL_STATUS: "
					+ digital + "\n11.INV_PURCHASE_OPTION: " + purch + "\n12. INV_COST: $" + cost
					+ "\n13. INV_ON_DISPLAY: " + display + "\n14. COPIES: " + copy 
					+"\n15. INV_RELEASE_DATE" + month + "/" + day + "/" + year);
				
				s = getString().toUpperCase();
				if(s.length() > 1)
					s = s.substring(0,1);
				
				if(s.equals("N"))
				{
					String p = "";
					
					while(s != "back" || p != "Y")
					{
						try{
							System.out.println("Pick the number choice you wish to change\n"
								+ "Or enter 'back");
							s = getString();
							if(s.equals("back"))break;
							int b = Integer.parseInt(s);
							if(b > 0 && b <= 15)
							{
								System.out.println("What would you like to change it to?");
								if(b == 3)
									System.out.println("If you do not have a middle initial enter NA");
								p = getString();
							}
							switch(b)
							{								
								case 1:
									isbn = getString();
									isbn2 = "";
									while(!isbn.equals(isbn2))
									{
										String q = "SELECT INV_ISBN_ID \r\n" + 
											"FROM inventory_table \r\n" + 
											"WHERE INV_ISBN_ID = '" + isbn + "'";
									
										ResultSet rset = stmt.executeQuery(q);		
										
										while (rset.next()) {
											isbn2 = rset.getString("INV_ISBN_ID");
											if(isbn.equals(isbn2))
											{
												System.out.println("This ISBN already exists, please enter another");
												isbn = getString();
											}				
										}
									}	
									break;
								case 2:									
									title = getString();
									break;
								case 3:
									genre = getString();
									break;
								case 4:
									category = getString();
									break;
								case 5:
									System.out.print("If N/A enter NA: ");
									subCat = getString().toUpperCase();
									if(subCat.equals("NA"))
										subCat = "";
									break;
								case 6:
									author = getString();
									break;
								case 7:
									publish = getString();
									break;
								case 8:
									format = getString();
									break;
								case 9:
									condition = getString();
									break;
								case 10:
									digital = getString().toUpperCase();
									while(!digital.equals("Y") || !digital.equals("N"))
									{
										if(digital.length() > 1)
										digital= digital.substring(0,1);
										if(!digital.equals("Y") && !digital.equals("N"))
										{
											System.out.println("Input Not Accepted, please try again");
											digital = getString().toUpperCase();
										}
									}
									break;
								case 11:
									purch = getString();
									while(!purch.equals("Rent") || !purch.equals("Buy"))
									{
										System.out.println("Input Not Accepted, please enter 'Rent' or 'Buy'");
										purch = getString();
										purch = purch.substring(0,1).toUpperCase() + purch.substring(1);
										
									}
									break;
								case 12:
									while(true)
									{
										try	{
											Scanner string = new Scanner(scan.next().toLowerCase());
											string.useDelimiter("\\$");
											cost = string.nextFloat();				
											if(cost < 0)
												System.out.println("Cost cannot be negative");
											else
												break;
										}catch(InputMismatchException e){
											System.out.println("Input Not Accepted, please try again");				
										}
									}
									break;
								case 13:
									display = getString().toUpperCase();
									while(!display.equals("Y") || !display.equals("N"))
									{
										if(display.length() > 1)
										display = display.substring(0,1);
										if(!display.equals("Y") && !display.equals("N"))
										{
											System.out.println("Input Not Accepted, please try again");
											display = getString().toUpperCase();
										}
									}
									break;
								case 14:
									while(true)
									{		
										System.out.print("INV_COPIES: ");
										try	{
												copy = getInt();
												if(copy < 0)
													System.out.println("Please enter a positive integer.");
												else
													break;
										}catch(NumberFormatException e)	{
											System.out.println("Input Not Accepted, please try again");				
										}
									}
									break;
								case 15:
									System.out.println("Year of Release: ");
									while(true)
									{
										try	{	
											year = getInt();
											if(year > 1500 && year <= currentdate.getYear()) break;
											else if(year > currentdate.getYear())
												System.out.println("Too far ahead of our time");
											else if(year <= 1600 && year >= 0 )
												System.out.println("I don't think we have books that old");
											else
												System.out.println("we don't have negative years");
										}catch(NumberFormatException e)	{
											System.out.println("Input Not Accepted, please enter a number ");
										}
										
									}
									System.out.println("Book Release Month:");
									while(true)
									{
										try{
											z = getString();
											month = Integer.parseInt(z);		
											if(month > 0 && ((month <= 12 && year < currentdate.getYear()) 
													|| ( month <= currentdate.getMonthValue() && year == currentdate.getYear())))
												break;
											
											else if (!(month < 1 || month > 12))
												System.out.println("That Month hasn't come yet");
											else 
												System.out.println("Intput Not Accepted");
										}catch(NumberFormatException e)
										{
											try 
											{	
												Date date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(z + "");
												
												Calendar cal = Calendar.getInstance();
												cal.setTime(date);
												
												month = cal.get(Calendar.MONTH) + 1;
												break;
											}catch(ParseException x)
											{
												System.out.println("Input Not Accepted");
											}
										}
									}
									System.out.println("Book Release Day: ");
									while(true)
									{
										try	{	
											day = getInt();
											if(day > 0 && ((day <= currentdate.getDayOfMonth() &&  year == currentdate.getYear()) 
													|| ( day <= maxDays && year < currentdate.getYear()))) break;
											else if(!(day < 1 || day > maxDays))
												System.out.println("the month does not have that many days");
											else
												System.out.println("Input Not Accepted, enter available numbers in the month");
										}catch(NumberFormatException e)	{
											System.out.println("Input Not Accepted, please enter a number ");
										}
										
									}									
									break;
								default:
									System.out.println("Selection " + s + " does not exist");
							}							
							if(b > 0 && b <= 15)
							{
								System.out.println("Would you like to change something else Y/N");
								p = getString().toUpperCase();
								if(p.length() > 1)
									p = p.substring(0,1);
								if(p.length() > 1)
									p = p.substring(0,1);
								while(p != "N" || p != "Y")
								{
									System.out.println("Input Not Accepted, Please Try Again");
								}
							}
						}catch(NumberFormatException e)
						{
							System.out.println("Selection " + s + " does not exist");
						}
					}
				}
				else if(!s.equals("Y"))
				System.out.println("Input Not Accepted");
			}
			
			PreparedStatement pstmt = conn.prepareStatement ("INSERT INTO inventory_table " +
						"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"); 
	
			pstmt.setString(1, isbn);
			pstmt.setString(2, title);
			pstmt.setString(3, genre);
			pstmt.setString(4, category);
			pstmt.setString(5, subCat);
			pstmt.setString(6, author);
			pstmt.setString(7, publish);
			pstmt.setString(8, format);
			pstmt.setString(9, condition);
			pstmt.setString(10, digital);
			pstmt.setString(11, purch);
			pstmt.setString(12, df.format(cost));
			if(copy >= 0)
				pstmt.setString(13, "Y");
			else
				pstmt.setString(13, "N");
			pstmt.setString(14, display);
			pstmt.setInt(15, copy);
			pstmt.setString(16, year + "-" + month + "-" + day);
			
			
			pstmt.executeUpdate();
			
			System.out.println("Your book has been added");
			
			System.out.println("Would you like to add another book? Y/N");
			v = getString().toUpperCase();
			if(v.length() > 1)
				v = v.substring(0,1);
			while(!v.equals("Y") || !v.equals("N"))
			{
				System.out.println("Input Not Accepted, please enter Y or N");
				v = getString().toUpperCase();
				if(v.length() > 1)
					v = v.substring(0,1);
			}
		}
		
		System.out.println("Book(s) have been added");
	}

	
	
	public static int drop(ArrayList<Inventory> code, List<Cart> cart, int counter) 
	{
		int num = 0;
		String v = "";
		if(!cart.isEmpty())
			System.out.println("Which book do you wish to drop");
		else
			System.out.println("Your cart is empty.");
		
		for(int i = 0; i < cart.size(); i++)
		{
			System.out.printf((i+1) + ". "); cart.get(i).display();			
		}
		while(v != "back")	
		{
			if(!cart.isEmpty()) 
			{
				v = getString().toLowerCase();
				if(v.equals("back"))break;
				num = Integer.parseInt(v) - 1;	
				
				if(num >= 0 && num < cart.size() && !cart.isEmpty())
				{
					System.out.println("How many would you like to take out?");
					int num2 = getInt();
					if(cart.get(num).getQuantity() > 0 && cart.get(num).getQuantity() >= num2)
					{
						if(cart.get(num).getPurch().equals("Rent"))							
							counter -= num2;
						for(int i = 0; i < code.size(); i++)
						{
							if(code.get(i).getISBN().equals(cart.get(num).getISBN()))
								code.get(i).setCopies(code.get(i).getCopies() + num2);
						}
						cart.get(num).setQuantity(cart.get(num).getQuantity() - num2);
						
						if(cart.get(num).getQuantity() == 0)
						{					
							System.out.println(cart.get(num).getTitle() + " has been removed from your cart\n");
							cart.remove(cart.get(num));	
						}
					}
					else
						System.out.println("You don't have that many books in you cart");
				}
				else if(num < 0 && num > cart.size() - 1 && !cart.isEmpty())
					System.out.println("Out of range");
				if(!cart.isEmpty())
				{
					System.out.println("Would you like to remove some more books? Y/N");
					v = getString().toUpperCase();
					if(v.length() > 1)
						v = v.substring(0,1);
					if(v.equals("N")) break;
					else if(!v.equals("Y"))
						while(true)
						{
							System.out.println("Input not accepted please enter Y/N");
							v = getString().toUpperCase();
							if(v.length() > 1)
								v = v.substring(0,1);
							if(v.equals("N")) 
							{
								v = "back";
								break;
							}
						}
				}
			}
			else
				break;
		}	
		return counter;		
	}

	public static void welcomeScreen()
	{
		System.out.println("               ..--..                  ..--..\n" +
				"         :syhhhhyssosyhhho-      -yhhysooossyhhy+:\n" +
				"      oMM-`               `/hh..hh/`               ` -NM+      \r\n" + 
				"--::/smoM`                   oNN+                   `NsNyo+++%\r\n" + 
				"mh//mm./M`   /%&&&&&&&&#.     mm    *%&&&&&&&&#*    `N/-Nh--yN\r\n" + 
				"ms +N. /M` #*           *&&   yh  &%,         ./#   `N/ .N+ oN\r\n" + 
				"ms +N` /M`                    yh                    `N/  m+ oN\r\n" + 
				"ms +N` /M`                    yh                    `N/  m+ oN\r\n" + 
				"ms +N` /M`  *%&&&&&&&&&(      yh    ,#&&&&&&&&&#.   `N/  m+ oN\r\n" + 
				"ms +N` /M` ((,         *%&,   yh  &&#,         *#,  `N/  m+ oN\r\n" + 
				"ms +N` /M`                    yh                    `N/  m+ oN\r\n" + 
				"ms +N` /M`                    yh                    `N/  m+ oN\r\n" + 
				"ms +N` /M`   (&&&&&&&&&&(.    yh  Welcome to The    `N/  m+ oN\r\n" + 
				"ms +N` /M` (%/         .(&&   yh   UNF Book Store   `N/  m+ oN\r\n" + 
				"ms +N` /M`                    yh                    `N/  m+ oN\r\n" + 
				"ms +N` /M`                    yh                    `N/  m+ oN\r\n" + 
				"ms +N` /M` `.-://++++//-.     yh    .:/++++++/::-`  `N/  m+ oN\r\n" + 
				"ms +N` yNhyso+/:::::::/+syh.  yh  hhs+/:::::::/+osyhhNy  m+ oN\r\n" + 
				"ms +N`yN-_..............._`/mNNm/`,................._`dy`m+ oN\r\n" + 
				"ms +NyN-_................._,-NN,....................._`hhN+ oN\r\n" + 
				"ms +MMyssyyhhhhhhhhhyyyssohdymmsdhossyyyhhhhhhhhhhhyyyssMM+ oN\r\n" + 
				"ms                                                          oN\r\n" + 
				"mmhhhhhhhhyyssso++//:::--/ohhyhhhs+---:://++oossyyhhhhhhhhhhmN\r\n");
	}

	public static String getDate()
	{
		LocalDate currentdate = LocalDate.now();
	    return  currentdate.getYear() + "-" + currentdate.getMonthValue() + "-" 
				+  currentdate.getDayOfMonth();
	}
	
	public static String getRentReturnDate()
	{
		LocalDate currentdate = LocalDate.now().plusDays(15);
	    return currentdate.getYear() + "-" +  currentdate.getMonthValue()
	    	+ "-"  + currentdate.getDayOfMonth();
	}

	
	public static String getBuyReturnDate()
	{
		LocalDate currentdate = LocalDate.now().plusDays(30);
	    //Getting the current day
	    int currentDay = currentdate.getDayOfMonth();
	    //Getting the current month
	    int currentMonth = currentdate.getMonthValue();
	    //getting the current year
	    int currentYear = currentdate.getYear();
	    
	    return currentYear + "-" + currentMonth + "-" + currentDay;
	}
	
	public static int getMaxDays(int month)
	{
		LocalDate currentdate = LocalDate.of(1, month, 1);
	    //Getting the current month
	    return currentdate.lengthOfMonth();		
	}
	
	public static String getString() {
		try {
		    StringBuffer buffer = new StringBuffer();
	        int c = System.in.read();
	        while (c != '\n' && c != -1) {
	    	  buffer.append((char)c);
	          c = System.in.read();
	          }
	        return buffer.toString().trim();
	        }
		catch (IOException e){return "";}
	}
	
	public static int getInt()
	{
      String s= getString();
      return Integer.parseInt(s);
	}
}

