import java.sql.*;
import java.io.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main implements ActionListener {
    // command line reader
    private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    private Connection con;

    // user is allowed 3 login attempts
    private int loginAttempts = 0;

    // components of the login window
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JFrame mainFrame;

    public Main() {
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

    public void showMenu() {
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
                System.out.print("1.  Insert item\n");
                System.out.print("2.  Remove item\n");
                System.out.print("3.  Print part 3\n");
                System.out.print("4.  Print part 4\n");
                System.out.print("5.  Quit\n>> ");

                choice = Integer.parseInt(in.readLine());

                System.out.println(" ");

                switch(choice)
                {
                    case 1:
                        insertItem();
                        break;
                    case 2:
                        deleteItem();
                        break;
                    case 3:
                        part3();
                        break;
                    case 4:
                        part4();
                        break;
                    case 5:
                        quit = true;
                        break;
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

    public Tuple<PreparedStatement, String> getItemPreparedStatement() {
        String             upc = null;
        float              sellingPrice;
        int                stock;
        String             taxable;
        PreparedStatement ps = null;

        try {
            ps = con.prepareStatement("INSERT INTO item VALUES (?,?,?,?)");


            System.out.print("\nItem UPC: ");
            upc = in.readLine();
            ps.setString(1, upc);

            System.out.print("\nItem selling price: ");
            sellingPrice = Float.parseFloat(in.readLine());
            ps.setFloat(2, sellingPrice);

            System.out.print("\nItem Stock: ");
            stock = Integer.parseInt(in.readLine());

            ps.setInt(3, stock);

            System.out.print("\nIs Item taxable? (y/n): ");
            taxable = in.readLine();
            ps.setString(4, taxable);

        }
        catch(IOException ex) {
            System.out.println("IOException! Message: " + ex.getMessage());
        }
        catch(SQLException ex) {
            System.out.println("SQLException, Message: " + ex.getMessage());
        }

        return new Tuple<>(ps, upc);
    }

    public PreparedStatement getBookItemPreparedStatement(String upc) {
        String title;
        String publisher;
        String flagText;
        PreparedStatement ps = null;


        try {
            ps = con.prepareStatement("INSERT INTO book VALUES (?,?,?,?)");

            ps.setString(1, upc);

            System.out.print("\nBook title: ");
            title = in.readLine();
            ps.setString(2, title);

            System.out.print("\nBook publisher: ");
            publisher = in.readLine();
            ps.setString(3, publisher);

            System.out.print("\nIs the book a textbook? (y/n): ");
            flagText = in.readLine();
            ps.setString(4, flagText);

        }
        catch(IOException ex) {
            System.out.println("IOException! Message: " + ex.getMessage());
        }
        catch(SQLException ex) {
            System.out.println("SQLException, Message: " + ex.getMessage());
        }

        return ps;
    }

    public void insertItem() {
        boolean back = false;
        int choice;
        PreparedStatement ps;

        try {
            while (!back) {
                System.out.print("\n\nPlease choose one of the following: \n");
                System.out.print("1.  Insert item\n");
                System.out.print("2.  Insert book item\n");
                System.out.print("3.  Back\n");
                System.out.print(">> ");

                choice = Integer.parseInt(in.readLine());

                switch(choice) {
                    case 1:
                        Tuple<PreparedStatement, String> itemPS = getItemPreparedStatement();
                        executeStatement(itemPS.x, "item");
                        break;
                    case 2:
                        Tuple<PreparedStatement, String> itemPS2 = getItemPreparedStatement();
                        PreparedStatement bookPS = getBookItemPreparedStatement(itemPS2.y);
                        boolean success = executeStatement(itemPS2.x, "item");
                        if(success) {
                            executeStatement(bookPS, "book");
                        }
                        break;
                    case 3:
                        back = true;
                }
            }
        }
        catch(IOException ex) {
            System.out.println("IOException! Message: " + ex.getMessage());
        }

    }

    public void deleteItem() {
        String upc = null;
        PreparedStatement ps;
        String beforeRelation = getRelationPrint("item");

        System.out.print("\nItem UPC: ");
        try {
            upc = in.readLine();
        }
        catch(IOException ex) {
            System.out.println("IOException! Message: " + ex.getMessage());
            System.exit(-1);
        }

        boolean canDelete = checkCanDeleteItem(upc);
        boolean isBook = checkIsBook(upc);

        if(!canDelete) {
            printBeforeAfter("Cancelled.", "item", beforeRelation);
            return;
        }

        try {
            if(isBook) {
                deleteBook(upc);
            }

            ps = con.prepareStatement("DELETE FROM item WHERE upc = '" + upc + "'");

            int rowCount = ps.executeUpdate();

            if(rowCount == 0) {
                printBeforeAfter("Cancelled.", "item", beforeRelation);
            }

            con.commit();
            ps.close();

            printBeforeAfter("Success.", "item", beforeRelation);

        }
        catch(SQLException ex) {
            try {
                con.rollback();
                printBeforeAfter("Cancelled.", "item", beforeRelation);
            }
            catch (SQLException ex2) {
                System.out.println("Message: " + ex2.getMessage());
                System.exit(-1);
            }
        }
    }

    public boolean checkCanDeleteItem(String upc) {
        Statement statement;
        ResultSet rs;

        try {
            statement = con.createStatement();
            rs = statement.executeQuery("SELECT stock FROM item WHERE upc = '" + upc + "'");

            if(rs.next()) {
                int stock = rs.getInt(1);

                if(stock == 0) {
                    return true;
                }

            }
        }
        catch(SQLException ex) {
            System.out.println("Exception at getRelationPrint, Message: " + ex.getMessage());
        }

        return false;
    }

    public boolean checkIsBook(String upc) {
        Statement statement;
        ResultSet rs;

        try {
            statement = con.createStatement();
            rs = statement.executeQuery("SELECT * FROM book WHERE upc = '" + upc + "'");

            return rs.next();
        }
        catch(SQLException ex) {
            System.out.println("Exception at getRelationPrint, Message: " + ex.getMessage());
        }

        return false;
    }

    public void deleteBook(String upc) {
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM book WHERE upc = '" + upc + "'");
            ps.executeUpdate();
            con.commit();
            ps.close();
        }
        catch(SQLException ex) {
            try {
                con.rollback();
            }
            catch(SQLException ex2) {
                System.out.println("Message: " + ex2.getMessage());
                System.exit(-1);
            }
        }
    }


    public boolean executeStatement(PreparedStatement ps, String tableName) {
        boolean success = true;
        String beforeRelation = getRelationPrint(tableName);

        try {
            ps.executeUpdate();
            con.commit();

            ps.close();
            printBeforeAfter("Success", tableName, beforeRelation);
        }
        catch (SQLException ex) {
            // Failed - likely due to primary key constraint.
            System.out.println("Message: " + ex.getMessage());
            try
            {
                // undo the insert
                success = false;
                con.rollback();
                printBeforeAfter("Cancelled.", "item", beforeRelation);

            }
            catch (SQLException ex2)
            {
                System.out.println("Message: " + ex2.getMessage());
                System.exit(-1);
            }
        }
        return success;
    }

    public String getRelationPrint(String tableName) {
        Statement statement;
        ResultSet rs;

        try {
            statement = con.createStatement();
            rs = statement.executeQuery("SELECT * FROM " + tableName);

            return getResultSetPrint(rs).toString();
        }
        catch(SQLException ex) {
            System.out.println("Exception at getRelationPrint, Message: " + ex.getMessage());
        }

        return null;
    }

    public StringBuilder getResultSetPrint(ResultSet rs) throws SQLException {
        StringBuilder sb = new StringBuilder();
        ResultSetMetaData resultSetMetaData = rs.getMetaData();

        int numCols = resultSetMetaData.getColumnCount();
        sb.append("\n");

        for(int i=1; i<=numCols; i++) {
            sb.append(String.format("%-15s", resultSetMetaData.getColumnName(i)));
        }

        sb.append("\n");

        while(rs.next()) {
            for(int i=1; i<=numCols; i++) {
                sb.append(String.format("%-15s", rs.getString(i)));
            }
            sb.append("\n");
        }

        sb.append("\n");
        return sb;
    }

    public void printBeforeAfter(String status, String tableName, String beforeRelation){
        System.out.println(status);
        System.out.println(tableName + " relation; BEFORE:");
        System.out.print(beforeRelation);
        System.out.println(tableName + " relation; AFTER");
        System.out.print(getRelationPrint(tableName));
    }


    public void part3() {
        Statement statement;
        ResultSet resultSet;

        String item_book_view = "create or replace view item_book as" +
                "  select i.upc" +
                "  from item i, book b" +
                "  where i.upc = b.upc";

        String book_itemPurchase_view = "create or replace view book_itemPurchase as" +
                "  select ip.upc, ip.t_id, ip.quantity" +
                "  from itempurchase ip, item_book ib" +
                "  where ip.upc = ib.upc";

        String book_withinDateNQuantity_view = "create or replace view book_withinDateNQuantity as" +
                "  select bip.upc" +
                "  from book_itemPurchase bip, purchase p" +
                "  where bip.t_id = p.t_id" +
                "  and" +
                "  p.purchaseDate >= TO_DATE('2015-10-25', 'YYYY-MM-DD')" +
                "  and" +
                "  p.purchaseDate <= TO_DATE('2015-10-31', 'YYYY-MM-DD')" +
                "  group by bip.upc" +
                "  having sum(quantity) > 50";

        String bdq_isTextbook_view = "create or replace view bdq_isTextbook as" +
                "  select b.upc" +
                "  from book b, book_withinDateNQuantity bdq" +
                "  where b.upc = bdq.upc" +
                "  and b.flag_text = 'y'";

        String bdq_lessStock_view = "create or replace view book_lessStock as" +
                "  select b.upc" +
                "  from book b, item i" +
                "  where b.upc = i.upc" +
                "  and" +
                "  i.stock < 10";

        String finalSelectStatement = "select *" +
                " from book b" +
                " where b.upc in (" +
                "  select bls.upc" +
                "  from book_lessStock bls, bdq_isTextbook bdqt" +
                "  where bls.upc = bdqt.upc" +
                ")";

        try {
            statement = con.createStatement();
            statement.addBatch(item_book_view);
            statement.addBatch(book_itemPurchase_view);
            statement.addBatch(book_withinDateNQuantity_view);
            statement.addBatch(bdq_isTextbook_view);
            statement.addBatch(bdq_lessStock_view);

            statement.executeBatch();
            con.commit();
        }
        catch(SQLException ex) {
            // TODO
            System.out.println("YOLO");
        }

        try {
            statement = con.createStatement();
            resultSet = statement.executeQuery(finalSelectStatement);
            System.out.print(getResultSetPrint(resultSet));
        }
        catch(SQLException ex) {
            // TODO
            System.out.println(ex.getMessage());
        }
    }

    public void part4() {
        Statement statement;
        ResultSet resultSet;

        String item_withinDate_view = "create or replace view item_withinDate as" +
                "  select ip.upc, p.t_id" +
                "  from itemPurchase ip, purchase p" +
                "  where p.t_id = ip.t_id" +
                "  and" +
                "  p.purchaseDate >= TO_DATE('2015-10-25', 'YYYY-MM-DD')" +
                "  and" +
                "  p.purchaseDate <= TO_DATE('2015-10-31', 'YYYY-MM-DD')";

        String salessum_view = "create or replace view salessum_withindate as" +
                "  select ip.upc, sum(ip.quantity * i.sellingPrice) as salesSum" +
                "  from item i, itemPurchase ip" +
                "  where i.upc = ip.upc" +
                "  and ip.t_id in (" +
                "    select t_id" +
                "    from item_withinDate" +
                "    )" +
                "  group by ip.upc";

        String selectStatement = "select *" +
                " from item i" +
                " where i.upc in (" +
                "  select upc" +
                "  from (" +
                "    select *" +
                "    from salessum_withindate sswd" +
                "    order by sswd.salesSum desc" +
                "  )" +
                "  where rownum <= 3" +
                ")";
        try {
            statement = con.createStatement();
            statement.addBatch(salessum_view);
            statement.addBatch(item_withinDate_view);

            statement.executeBatch();
            con.commit();
        }
        catch(SQLException ex) {
            // TODO
            System.out.println("YOLO");
        }

        try {
            statement = con.createStatement();
            resultSet = statement.executeQuery(selectStatement);
            System.out.print(getResultSetPrint(resultSet));
        }
        catch(SQLException ex) {
            // TODO
            System.out.println(ex.getMessage());
        }

    }

    public static void main(String[] args) {
        Main m = new Main();
    }
}
