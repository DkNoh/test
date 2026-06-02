import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DbInit {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:postgresql://localhost:11521/postgres";
        String user = "postgres";
        String password = "1234";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
             
            stmt.execute("ALTER TABLE employee ADD COLUMN IF NOT EXISTS dept_id VARCHAR(50);");
            stmt.execute("ALTER TABLE employee ADD COLUMN IF NOT EXISTS use_yn CHAR(1) DEFAULT 'Y';");
            stmt.execute("UPDATE employee SET dept_id = 'D001', use_yn = 'Y' WHERE emp_id = 'admin';");
            stmt.execute("UPDATE employee SET dept_id = 'D030', use_yn = 'Y' WHERE emp_id = 'user';");
            
            // 더미 데이터 좀 더 넣어주기
            for (int i=1; i<=15; i++) {
                String id = String.format("testuser%02d", i);
                String name = "테스터" + i;
                String dept = (i%2==0) ? "D001" : "D030";
                String use = (i%5==0) ? "N" : "Y";
                stmt.execute(String.format("INSERT INTO employee (emp_id, emp_name, dept_id, use_yn) VALUES ('%s', '%s', '%s', '%s') ON CONFLICT (emp_id) DO NOTHING;", id, name, dept, use));
            }
            
            System.out.println("DB Update Successful!");
        }
    }
}
