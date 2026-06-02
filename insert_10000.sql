BEGIN
  FOR seq IN 1..10000 LOOP
    INSERT INTO sms_history (
      campaign_name, 
      send_type, 
      sender_no, 
      receiver_no, 
      message, 
      send_status, 
      resend_yn, 
      result_code, 
      result_message, 
      reserved_at, 
      sent_at, 
      created_at, 
      updated_at
    ) VALUES (
      '대규모 테스트 캠페인 ' || seq,
      CASE WHEN MOD(seq, 3) = 0 THEN 'SMS' WHEN MOD(seq, 3) = 1 THEN 'LMS' ELSE 'ALIMTALK' END,
      '15881234',
      '010' || LPAD(MOD(seq, 10000), 4, '0') || LPAD(MOD(seq, 10000), 4, '0'),
      '대규모 성능 테스트 메시지입니다. 일련번호: ' || seq,
      CASE WHEN MOD(seq, 10) = 0 THEN 'FAIL' WHEN MOD(seq, 10) = 1 THEN 'WAIT' ELSE 'SUCCESS' END,
      CASE WHEN MOD(seq, 5) = 0 THEN 'Y' ELSE 'N' END,
      CASE WHEN MOD(seq, 10) = 0 THEN 'E001' ELSE '0000' END,
      CASE WHEN MOD(seq, 10) = 0 THEN '에러 발생' ELSE '정상 처리' END,
      SYSDATE - (seq / 24 / 60), -- seq 분 단위 과거로 설정
      SYSDATE - (seq / 24 / 60),
      SYSDATE - (seq / 24 / 60),
      SYSDATE - (seq / 24 / 60)
    );
  END LOOP;
  COMMIT;
END;
/
EXIT;
