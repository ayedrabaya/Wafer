import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.DriverManager;
// import java.sql.SQLException;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.text.DefaultCaret;

import org.json.JSONObject;
// import org.json.JSONException;


// import com.mysql.jdbc.Statement;
import java.sql.*;

public class test1 {

    public static void main(String[] args) {
        
        // Create a new JFrame and set its properties
        JFrame frame = new JFrame("Wafer Receiver");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new BorderLayout());
   // Create a new JTextArea for displaying JSON objects
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
// Set the caret policy of the JTextArea to always update

        DefaultCaret caret = (DefaultCaret)textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        // Add a JScrollPane to the JTextArea
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);
        // Add a JLabel for displaying a message
        JLabel label = new JLabel("Receiving JSON objects");
        frame.add(label, BorderLayout.NORTH);
        // Create a new JButton for saving JSON objects to the database
        JButton saveButton = new JButton("Save to Database");
        frame.add(saveButton, BorderLayout.SOUTH);

 // Create a new Timer to periodically check for new JSON objects
        Timer timer = new Timer(10000, new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                try {
                    // Connect to the URL that provides the JSON objects
                    URL url = new URL("http://localhost:4445/wafers");
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

                    String json;
                    while ((json = in.readLine()) != null) {
                        // Append each new JSON object to the JTextArea
                        textArea.append(json + "\n");
                    }
                    in.close();

                } catch (MalformedURLException me) {
                    System.out.println("MalformedURLException: " + me);
                } catch (IOException ioe) {
                    System.out.println("IOException: " + ioe);
                }
            }
        });
        // Start the Timer
        timer.start();
        // Make the JFrame visible
        frame.setVisible(true);
        // Add an ActionListener to the saveButton to save JSON objects to the database
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String json = textArea.getText();
                try {
                    // Connect to the database
                    String url = "jdbc:mysql://localhost:3307/wafers";
                    String username = "root";
                    String password = "12345";
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection  connection = (Connection) DriverManager.getConnection(url,username, password);
                        System.out.println("Connection established successfully.");                   
        
                    // Create a new JSONObject from the JSON string
                    JSONObject json1 = new JSONObject(json);
                    // Create a SQL query to insert the JSON object into the database
                    String sql = "insert into wafer(Etime,Stime,Diameter,numchip) values(?,?,?,?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, json1.getString("Etime"));
                    statement.setString(2, json1.getString("Stime"));
                    statement.setString(3, String.valueOf(json1.getInt("Diameter")));
                    statement.setString(4, String.valueOf(json1.getInt("Numchip")));
                    ////////////////////////////////////////////////
                    // Execute the SQL query
                    statement.executeUpdate();
                    connection.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                } 
            }
        });
        
    }
}
