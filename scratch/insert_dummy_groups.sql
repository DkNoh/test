DELETE FROM TB_CONTACT_GROUP;
BEGIN
    FOR i IN 1..100 LOOP
        INSERT INTO TB_CONTACT_GROUP (GROUP_NM, GROUP_DESC, USE_YN, REG_ID, REG_DT)
        VALUES (
            'VIP 타겟 그룹 ' || i, 
            '테스트 목적으로 자동 생성된 주소록 타겟 그룹입니다. (Group ' || i || ')', 
            CASE WHEN MOD(i, 10) = 0 THEN 'N' ELSE 'Y' END, 
            'SYSTEM', 
            SYSDATE - (100 - i)
        );
    END LOOP;
    COMMIT;
END;
/
EXIT;
