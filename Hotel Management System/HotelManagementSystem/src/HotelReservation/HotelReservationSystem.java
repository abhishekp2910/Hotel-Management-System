package HotelReservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.Statement;

public class HotelReservationSystem {
	
	private static String url = "jdbc:mysql://localhost:3306/";
	
	private static  String db = "hotel_db";
	
	private static String username = "root";
	
	private static String password = "root";
	
	public static void main(String[] args) throws ClassNotFoundException,SQLException, InterruptedException {
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); //JDBC Driver loaded
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		try {
			Connection conn = DriverManager.getConnection(url+db,username,password);
			while(true) {
				System.out.println();
				System.out.println("HOTEL MANAGEMENT SYSTEM");
				Scanner sc = new Scanner(System.in);
				//Main-Menu
				System.out.println("1. Reserve A Room");
				System.out.println("2. Update Reservation");
				System.out.println("3. Delete Reservation");
				System.out.println("4. Get Room Number");
				System.out.println("5. View Reservation");
				System.out.println("0. Exit");
				System.out.println("CHOOSE OPTION :");
				//Taking user's input choice
				int choice = sc.nextInt();
				switch(choice) {
				case 1:
					reserveRoom(conn,sc);
					break;
					
				case 2:
					updateReservation(conn,sc); 
					break;
					
				case 3:
					deleteReservation(conn,sc);
					break;
				
				case 4:
					getRoomNumber(conn,sc);
					break;
				
				case 5:
					viewReservation(conn);
					break;
				
				case 0:
					exit();
					sc.close();
					return;
					
				default:
					System.out.println("Invalid Choice. Please try again!");
				}	
				
			}	
		
		}
		//Exception from Connection i.e. SQLException is handled here 
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private static void exit() throws InterruptedException {
		System.out.print("Exiting System");
		int i =5;
		while(i!=0) {
			System.out.print(".");
			Thread.sleep(500);
			i--;
		}
		System.out.println();
		System.out.println("Thank you for using Hotel Management System!See you back soon");
	}

	private static void getRoomNumber(Connection conn, Scanner sc) {
		try {
			System.out.println("Enter Reservation ID : ");
			int reservationId = sc.nextInt();
			System.out.println("Enter Guest Name : ");
			String guestName = sc.next();
			
			String query = "SELECT room_number FROM reservations "
					+ "WHERE reservation_id = "+reservationId+
					" AND guest_name = '" +guestName+ "'";
			
			try(Statement stm = conn.createStatement() ;
					ResultSet rs = stm.executeQuery(query)) {
				if(rs.next()) {
					int roomNumber = rs.getInt("room_number");
					System.out.println("Room number for Reservation ID "+reservationId+" and Guest "+guestName+" is : "+roomNumber);
				}else {
					System.out.println("Reservation not found for the given ID and guest name.");
				}
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		
	}

	private static void deleteReservation(Connection conn, Scanner sc) {
		try {
			System.out.println("Enter Reservation ID to delete : ");
			int reservationId = sc.nextInt();
			
			if(!reservationExists(conn,reservationId)) {
				System.out.println("Reservation not found for the given ID.");
				return;
			}
			String query = "DELETE FROM reservations WHERE reservation_id = " + reservationId;
			
			try(Statement stm = conn.createStatement()) {
				int affectedRows = stm.executeUpdate(query);
				if(affectedRows > 0) {
					System.out.println("Reservation deleted successfully!");
				}else {
					System.out.println("Reservtaion deletion failed.");
				}
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		
	}

	private static void updateReservation(Connection conn, Scanner sc) {
		try {
			System.out.println("Enter Reservation Id to update : ");
			int reservationId = sc.nextInt();
			sc.nextLine(); //Consumes new Line character
			
			if(!reservationExists(conn,reservationId)) {
				System.out.println("Reservation not found for the given ID");
				return;
			}
			
			System.out.println("Enter new guest name : ");
			String newGuestName = sc.nextLine();
			System.out.println("Enter new room number : ");
			int newRoomNumber = sc.nextInt();
			System.out.println("Enter new contact number : ");
			String newContactNumber = sc.next();
			
			
			String query = "UPDATE reservations SET guest_name = '" +newGuestName+ "', "+
			"room_number="+newRoomNumber+","+
			"contact_number = '" +newContactNumber+ "' "+
			"WHERE reservation_id = " +reservationId;
			
			try(Statement stm = conn.createStatement()) {
				int affectedRows =  stm.executeUpdate(query);
				if(affectedRows > 0) {
					System.out.println("Reservation updated successfully!");
				}else {
					System.out.println("Reservation update failed");
				}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		
		
	}

	private static boolean reservationExists(Connection conn, int reservationId) {
		
		String query = "SELECT reservation_id FROM reservations WHERE reservation_id = "+reservationId;
		try(Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query)) {
			return rs.next(); //If there is a result, the reservation exists
		}
		catch(SQLException e) {
			e.printStackTrace();
			return false;//handles database errors as needed 
		}
	
	}

	private static void reserveRoom(Connection conn, Scanner sc) {
		try {
			System.out.println("Enter name of the guest : ");
			String guestName = sc.next();
			sc.nextLine();
			System.out.println("Enter room number : ");
			int roomNumber = sc.nextInt();
			System.out.println("Enter contact number : ");
			String contactNumber = sc.next();
			
			String query = "INSERT INTO reservations(guest_name,room_number,contact_number)"+
							"VALUES('"+guestName+"','"+roomNumber+"','"+contactNumber+"')";
			
			try(Statement stm = conn.createStatement()) {
				int affectedRows = stm.executeUpdate(query);
				
				if(affectedRows > 0) {
					System.out.println("Reservation successful");
				}
				else {
					System.out.println("Reservation failed");
				}
			}
			
		}catch(SQLException e) { //Handles Statement Interface's exception
				e.printStackTrace();
			}
		
		}
	
	
	
	private static void viewReservation(Connection conn) {
		String query = "SELECT reservation_id , guest_name , room_number , contact_number , reservation_date FROM reservations";
		
		try(Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query)) {
			
			System.out.println("Current Reservations : ");
			System.out.println("*---------------*----------------*-------------*----------------*-----------------------*");
			System.out.println("|Reservation ID |      Guest     | Room Number | Contact Number |     reservation Date  | ");
			System.out.println("*---------------*----------------*-------------*----------------*-----------------------*");
			
			while(rs.next()) {
				int reservationId = rs.getInt("reservation_id");
				String guestName = rs.getString("guest_name");
				int roomNumber = rs.getInt("room_number");
				String contactNumber = rs.getString("contact_number");
				String reservationDate = rs.getTimestamp("reservation_date").toString();
				
				//displaying data in table like format
				System.out.printf("|%-15d|%-16s|%-13d|%-16s|%-19s  |\n",reservationId,guestName,roomNumber,contactNumber,reservationDate);
			}
			
			System.out.println("*---------------*----------------*-------------*----------------*-----------------------*");
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
