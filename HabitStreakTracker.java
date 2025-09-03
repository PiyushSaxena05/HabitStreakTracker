package JDBC.Project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

class Info{
    String name;
    String habit;
    String id;
    int streak;
    int laststreak;

    Info(String name, String habit,String id){
        this.name = name;
        this.habit = habit;
        this.id = id;
        this.streak =0;
        this.laststreak = 0;
    }
}

public class HabitStreakTracker {

    private static ArrayList<Info> arr = new ArrayList<>();

    public static void add (Connection con , Scanner sc ) {
        String query ="Insert into habit(nme,habit,password)VALUES (?,?,?)";
        try (PreparedStatement p = con.prepareStatement(query)) {
            System.out.println("Please enter your name:");
            String name = sc.nextLine();

            System.out.println("Please enter your target");
            String target = sc.nextLine();

            System.out.println("Please set your password");
            String pass = sc.next();

            arr.add(new Info(name, target,pass));

            System.out.println("Please enter your password again to verify");
            String verify = sc.next();
            sc.nextLine();
            if(pass.equalsIgnoreCase(verify)){
                p.setString(1, name);
                p.setString(2,target);
                p.setString(3,pass);
            }
            p.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void streak(Connection con , Scanner sc ) throws SQLException {
        try {
            System.out.println("Which day streak");
            int day = sc.nextInt();
            System.out.println("Task completed y/n");
            String task = sc.next();
            sc.nextLine();
            System.out.println("Please enter your password: ");
            String password = sc.next();


            String fetchQuery = "SELECT streak FROM habit WHERE password = ?";
            int lastdaystreak = 0;
            try (PreparedStatement fetch = con.prepareStatement(fetchQuery)) {
                fetch.setString(1, password);
                var rs = fetch.executeQuery();
                if (rs.next()) {
                    lastdaystreak = rs.getInt("streak");
                } else {
                    System.out.println("No user found with given password!");
                    return;
                }
            }


            if (task.equalsIgnoreCase("y")) {
                String query = "UPDATE habit SET streak = ? WHERE password = ?";
                try (PreparedStatement p = con.prepareStatement(query)) {
                    p.setInt(1, lastdaystreak + 1);
                    p.setString(2, password);
                    p.executeUpdate();
                    System.out.println("Streak updated to " + (lastdaystreak + 1));
                }
            } else {
                String query = "UPDATE habit SET streak = 0 WHERE password = ?";
                try (PreparedStatement p = con.prepareStatement(query)) {
                    p.setString(1, password);
                    p.executeUpdate();
                    System.out.println("Streak reset to 0");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String url = "jdbc:mysql://localhost:3306/habit";
    private static final String user = "root";
    private static final String password = "PIYUSH@111WORD016";
    public static void main(String[] args) throws SQLException {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
        Connection con = DriverManager.getConnection(url,user,password);
        while(true) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Choose 1 to add ");
            System.out.println("then enter 2 to add streak");
            int option = sc.nextInt();
            sc.nextLine();
            switch (option){
                case 1:
                    HabitStreakTracker.add(con,sc);
                    break;
                case 2:
                    HabitStreakTracker.streak(con,sc);
                    break;
                default:
                    System.out.println("something went wrong");
            }

        }


    }
}
