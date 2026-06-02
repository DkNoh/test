import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBInsert {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@localhost:1521/SMS";
        String user = "system";
        String pass = "1234";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {

            String plsql = "DECLARE " +
                    "  v_group_id TB_CONTACT_GROUP.GROUP_ID%TYPE; " +
                    "  CURSOR c_groups IS SELECT GROUP_ID FROM TB_CONTACT_GROUP FETCH FIRST 5 ROWS ONLY; " +
                    "  TYPE t_group_ids IS TABLE OF TB_CONTACT_GROUP.GROUP_ID%TYPE; " +
                    "  v_group_ids t_group_ids; " +
                    "BEGIN " +
                    "  OPEN c_groups; " +
                    "  FETCH c_groups BULK COLLECT INTO v_group_ids; " +
                    "  CLOSE c_groups; " +
                    "  IF v_group_ids.COUNT > 0 THEN " +
                    "    FOR i IN 1..50 LOOP " +
                    "      v_group_id := v_group_ids(MOD(i, v_group_ids.COUNT) + 1); " +
                    "      INSERT INTO TB_CONTACT (CONTACT_ID, CONTACT_NM, PHONE_NO, COMPANY_NM, REG_DT) " +
                    "      VALUES ('C' || TO_CHAR(i, 'FM0000'), '테스트고객' || i, '010-' || TO_CHAR(i, 'FM0000') || '-1234', '(주)테스트기업' || MOD(i,3), SYSDATE); " +
                    "      INSERT INTO TB_GROUP_CONTACT_MAP (GROUP_ID, CONTACT_ID, REG_DT) " +
                    "      VALUES (v_group_id, 'C' || TO_CHAR(i, 'FM0000'), SYSDATE); " +
                    "    END LOOP; " +
                    "    COMMIT; " +
                    "  END IF; " +
                    "END;";

            stmt.execute(plsql);
            System.out.println("SUCCESS: 50 contacts inserted.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
