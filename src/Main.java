import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
    }
}
