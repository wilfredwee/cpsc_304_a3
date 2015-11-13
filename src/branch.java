
// We need to import the java.sql package to use JDBC
import java.sql.*;

// for reading from the command line
import java.io.*;

// for the login window
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/*
 * This class implements a graphical login window and a simple text
 * interface for interacting with the branch table 
 */
public class branch implements ActionListener
{
    // command line reader 
    private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    private Connection con;

    // user is allowed 3 login attempts
    private int loginAttempts = 0;

    // components of the login window
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JFrame mainFrame;


    /*
     * constructs login window and loads JDBC driver
     */
    public branch()
    {
        mainFrame = new JFrame("User Login");

        JLabel usernameLabel = new JLabel("Enter username: ");
        JLabel passwordLabel = new JLabel("Enter password: ");

        usernameField = new JTextField(10);
        passwordField = new JPasswordField(10);
        passwordField.setEchoChar('*');

        JButton loginButton = new JButton("Log In");

        JPanel contentPane = new JPanel();
        mainFrame.setContentPane(contentPane);


        // layout components using the GridBag layout manager

        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        contentPane.setLayout(gb);
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // place the username label
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.insets = new Insets(10, 10, 5, 0);
        gb.setConstraints(usernameLabel, c);
        contentPane.add(usernameLabel);

        // place the text field for the username
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(10, 0, 5, 10);
        gb.setConstraints(usernameField, c);
        contentPane.add(usernameField);

        // place password label
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.insets = new Insets(0, 10, 10, 0);
        gb.setConstraints(passwordLabel, c);
        contentPane.add(passwordLabel);

        // place the password field
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(0, 0, 10, 10);
        gb.setConstraints(passwordField, c);
        contentPane.add(passwordField);

        // place the login button
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(5, 10, 10, 10);
        c.anchor = GridBagConstraints.CENTER;
        gb.setConstraints(loginButton, c);
        contentPane.add(loginButton);

        // register password field and OK button with action event handler
        passwordField.addActionListener(this);
        loginButton.addActionListener(this);

        // anonymous inner class for closing the window
        mainFrame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });

        // size the window to obtain a best fit for the components
        mainFrame.pack();

        // center the frame
        Dimension d = mainFrame.getToolkit().getScreenSize();
        Rectangle r = mainFrame.getBounds();
        mainFrame.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

        // make the window visible
        mainFrame.setVisible(true);

        // place the cursor in the text field for the username
        usernameField.requestFocus();

        try
        {
            // Load the Oracle JDBC driver
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        }
        catch (SQLException ex)
        {
            System.out.println("Message: " + ex.getMessage());
            System.exit(-1);
        }
    }


    /*
     * connects to Oracle database named ug using user supplied username and password
     */
    private boolean connect(String username, String password)
    {
        String connectURL = "jdbc:oracle:thin:@dbhost.ugrad.cs.ubc.ca:1522:ug";

        try
        {
            con = DriverManager.getConnection(connectURL,username,password);

            System.out.println("\nConnected to Oracle!");
            return true;
        }
        catch (SQLException ex)
        {
            System.out.println("Message: " + ex.getMessage());
            return false;
        }
    }


    /*
     * event handler for login window
     */
    public void actionPerformed(ActionEvent e)
    {
        if ( connect(usernameField.getText(), String.valueOf(passwordField.getPassword())) )
        {
            // if the username and password are valid,
            // remove the login window and display a text menu
            mainFrame.dispose();
            showMenu();
        }
        else
        {
            loginAttempts++;

            if (loginAttempts >= 3)
            {
                mainFrame.dispose();
                System.exit(-1);
            }
            else
            {
                // clear the password
                passwordField.setText("");
            }
        }

    }


    /*
     * displays simple text interface
     */
    private void showMenu()
    {
        int choice;
        boolean quit;

        quit = false;

        try
        {
            // disable auto commit mode
            con.setAutoCommit(false);

            while (!quit)
            {
                System.out.print("\n\nPlease choose one of the following: \n");
                System.out.print("1.  Insert branch\n");
                System.out.print("2.  Delete branch\n");
                System.out.print("3.  Update branch\n");
                System.out.print("4.  Show branch\n");
                System.out.print("5.  Quit\n>> ");

                choice = Integer.parseInt(in.readLine());

                System.out.println(" ");

                switch(choice)
                {
                    case 1:  insertBranch(); break;
                    case 2:  deleteBranch(); break;
                    case 3:  updateBranch(); break;
                    case 4:  showBranch(); break;
                    case 5:  quit = true;
                }
            }

            con.close();
            in.close();
            System.out.println("\nGood Bye!\n\n");
            System.exit(0);
        }
        catch (IOException e)
        {
            System.out.println("IOException!");

            try
            {
                con.close();
                System.exit(-1);
            }
            catch (SQLException ex)
            {
                System.out.println("Message: " + ex.getMessage());
            }
        }
        catch (SQLException ex)
        {
            System.out.println("Message: " + ex.getMessage());
        }
    }


    /*
     * inserts a branch
     */
    private void insertBranch()
    {
        int                bid;
        String             bname;
        String             baddr;
        String             bcity;
        int                bphone;
        PreparedStatement  ps;

        try
        {
            ps = con.prepareStatement("INSERT INTO branch VALUES (?,?,?,?,?)");

            System.out.print("\nBranch ID: ");
            bid = Integer.parseInt(in.readLine());
            ps.setInt(1, bid);

            System.out.print("\nBranch Name: ");
            bname = in.readLine();
            ps.setString(2, bname);

            System.out.print("\nBranch Address: ");
            baddr = in.readLine();

            if (baddr.length() == 0)
            {
                ps.setString(3, null);
            }
            else
            {
                ps.setString(3, baddr);
            }

            System.out.print("\nBranch City: ");
            bcity = in.readLine();
            ps.setString(4, bcity);

            System.out.print("\nBranch Phone: ");
            String phoneTemp = in.readLine();
            if (phoneTemp.length() == 0)
            {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            else
            {
                bphone = Integer.parseInt(phoneTemp);
                ps.setInt(5, bphone);
            }

            ps.executeUpdate();

            // commit work
            con.commit();

            ps.close();
        }
        catch (IOException e)
        {
            System.out.println("IOException!");
        }
        catch (SQLException ex)
        {
            System.out.println("Message: " + ex.getMessage());
            try
            {
                // undo the insert
                con.rollback();
            }
            catch (SQLException ex2)
            {
                System.out.println("Message: " + ex2.getMessage());
                System.exit(-1);
            }
        }
    }


    /*
     * deletes a branch
     */
    private void deleteBranch()
    {
        int                bid;
        PreparedStatement  ps;

        try
        {
            ps = con.prepareStatement("DELETE FROM branch WHERE branch_id = ?");

            System.out.print("\nBranch ID: ");
            bid = Integer.parseInt(in.readLine());
            ps.setInt(1, bid);

            int rowCount = ps.executeUpdate();

            if (rowCount == 0)
            {
                System.out.println("\nBranch " + bid + " does not exist!");
            }

            con.commit();

            ps.close();
        }
        catch (IOException e)
        {
            System.out.println("IOException!");
        }
        catch (SQLException ex)
        {
            System.out.println("Message: " + ex.getMessage());

            try
            {
                con.rollback();
            }
            catch (SQLException ex2)
            {
                System.out.println("Message: " + ex2.getMessage());
                System.exit(-1);
            }
        }
    }


    /*
     * updates the name of a branch
     */
    private void updateBranch()
    {
        int                bid;
        String             bname;
        PreparedStatement  ps;

        try
        {
            ps = con.prepareStatement("UPDATE branch SET branch_name = ? WHERE branch_id = ?");

            System.out.print("\nBranch ID: ");
            bid = Integer.parseInt(in.readLine());
            ps.setInt(2, bid);

            System.out.print("\nBranch Name: ");
            bname = in.readLine();
            ps.setString(1, bname);

            int rowCount = ps.executeUpdate();
            if (rowCount == 0)
            {
                System.out.println("\nBranch " + bid + " does not exist!");
            }

            con.commit();

            ps.close();
        }
        catch (IOException e)
        {
            System.out.println("IOException!");
        }
        catch (SQLException ex)
        {
            System.out.println("Message: " + ex.getMessage());

            try
            {
                con.rollback();
            }
            catch (SQLException ex2)
            {
                System.out.println("Message: " + ex2.getMessage());
                System.exit(-1);
            }
        }
    }


    /*
     * display information about branches
     */
    private void showBranch()
    {
        String     bid;
        String     bname;
        String     baddr;
        String     bcity;
        String     bphone;
        Statement  stmt;
        ResultSet  rs;

        try
        {
            stmt = con.createStatement();

            rs = stmt.executeQuery("SELECT * FROM branch");

            // get info on ResultSet
            ResultSetMetaData rsmd = rs.getMetaData();

            // get number of columns
            int numCols = rsmd.getColumnCount();

            System.out.println(" ");

            // display column names;
            for (int i = 0; i < numCols; i++)
            {
                // get column name and print it

                System.out.printf("%-15s", rsmd.getColumnName(i+1));
            }

            System.out.println(" ");

            while(rs.next())
            {
                // for display purposes get everything from Oracle
                // as a string

                // simplified output formatting; truncation may occur

                bid = rs.getString("branch_id");
                System.out.printf("%-10.10s", bid);

                bname = rs.getString("branch_name");
                System.out.printf("%-20.20s", bname);

                baddr = rs.getString("branch_addr");
                if (rs.wasNull())
                {
                    System.out.printf("%-20.20s", " ");
                }
                else
                {
                    System.out.printf("%-20.20s", baddr);
                }

                bcity = rs.getString("branch_city");
                System.out.printf("%-15.15s", bcity);

                bphone = rs.getString("branch_phone");
                if (rs.wasNull())
                {
                    System.out.printf("%-15.15s\n", " ");
                }
                else
                {
                    System.out.printf("%-15.15s\n", bphone);
                }
            }

            // close the statement;
            // the ResultSet will also be closed
            stmt.close();
        }
        catch (SQLException ex)
        {
            System.out.println("Message: " + ex.getMessage());
        }
    }


    public static void main(String args[])
    {
        branch b = new branch();
    }
}