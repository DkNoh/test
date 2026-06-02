import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DbAlter {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:postgresql://localhost:11521/postgres";
        String user = "postgres";
        String password = "1234";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
             
            stmt.execute("ALTER TABLE employee ADD COLUMN IF NOT EXISTS user_role VARCHAR(50) DEFAULT 'ROLE_USER';");
            stmt.execute("ALTER TABLE employee ADD COLUMN IF NOT EXISTS auth_read CHAR(1) DEFAULT 'Y';");
            stmt.execute("ALTER TABLE employee ADD COLUMN IF NOT EXISTS auth_approve CHAR(1) DEFAULT 'N';");
            stmt.execute("ALTER TABLE employee ADD COLUMN IF NOT EXISTS auth_campaign CHAR(1) DEFAULT 'N';");
            
            System.out.println("DB Update Successful!");
        }
    }
}
