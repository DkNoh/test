import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class InsertDummy {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:11521/postgres";
        String user = "postgres";
        String password = "1234";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
             
             String sql = "INSERT INTO sms_history (campaign_name, send_type, sender_no, receiver_no, message, send_status, resend_yn, result_code, result_message, reserved_at, sent_at, created_at, updated_at) " +
             "SELECT '테스트 캠페인 ' || seq, " +
             "CASE WHEN seq % 3 = 0 THEN 'SMS' WHEN seq % 3 = 1 THEN 'LMS' ELSE 'ALIMTALK' END, " +
             "'01011112222', " +
             "'010' || lpad((seq % 10000)::text, 4, '0') || lpad((seq % 10000)::text, 4, '0'), " +
             "'테스트 메시지 내용 ' || seq, " +
             "CASE WHEN seq % 10 = 0 THEN 'FAIL' WHEN seq % 10 = 1 THEN 'WAIT' ELSE 'SUCCESS' END, " +
             "CASE WHEN seq % 5 = 0 THEN 'Y' ELSE 'N' END, " +
             "CASE WHEN seq % 10 = 0 THEN 'E001' ELSE '0000' END, " +
             "CASE WHEN seq % 10 = 0 THEN '에러 발생' ELSE '성공' END, " +
             "CURRENT_TIMESTAMP - (seq || ' minutes')::interval, " +
             "CURRENT_TIMESTAMP - (seq || ' minutes')::interval, " +
             "CURRENT_TIMESTAMP - (seq || ' minutes')::interval, " +
             "CURRENT_TIMESTAMP - (seq || ' minutes')::interval " +
             "FROM generate_series(1, 20000) as seq";
             
             int rows = stmt.executeUpdate(sql);
             System.out.println("Inserted " + rows + " rows successfully!");
             
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
