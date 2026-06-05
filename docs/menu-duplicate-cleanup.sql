-- Menu duplicate cleanup helper.
-- Execute SELECT first, choose the duplicate MENU_CD to remove, then run DELETE statements.

SELECT
    MENU_CD,
    MENU_NM,
    MENU_URL,
    UP_MENU_CD,
    SORT_ORD,
    USE_YN
FROM TB_MENU
WHERE MENU_URL = '/statistics/marketing-optout'
   OR MENU_NM LIKE '%마케팅%'
   OR MENU_NM LIKE '%철회%'
ORDER BY UP_MENU_CD, SORT_ORD, MENU_CD;

-- Replace __DUPLICATE_MENU_CD__ with the MENU_CD that should be removed.
-- Keep the row whose MENU_CD, parent, sort order, and display name match the intended menu structure.
--
-- DELETE FROM TB_MENU_AUTH
-- WHERE MENU_CD = '__DUPLICATE_MENU_CD__';
--
-- DELETE FROM TB_MENU
-- WHERE MENU_CD = '__DUPLICATE_MENU_CD__';
--
-- COMMIT;

-- Department send statistics and hourly statistics split.
-- Keep hourly statistics at /sms/dept-stat.
-- Move department send statistics to /statistics/dept-send-stat.

SELECT
    c.MENU_CD,
    c.MENU_NM,
    c.MENU_URL,
    c.UP_MENU_CD,
    p.MENU_NM AS PARENT_MENU_NM,
    c.SORT_ORD,
    c.USE_YN
FROM TB_MENU c
LEFT JOIN TB_MENU p ON c.UP_MENU_CD = p.MENU_CD
WHERE c.MENU_URL = '/sms/dept-stat'
   OR c.MENU_URL = '/statistics/dept-send-stat'
   OR c.MENU_NM LIKE '%시간대%'
   OR c.MENU_NM LIKE '%부서별%'
ORDER BY p.SORT_ORD, c.SORT_ORD, c.MENU_CD;

UPDATE TB_MENU
SET MENU_URL = '/statistics/dept-send-stat'
WHERE MENU_URL = '/sms/dept-stat'
  AND MENU_NM LIKE '%부서별%'
  AND MENU_NM LIKE '%발송%';

-- User management duplicate cleanup.
-- Keep: 시스템관리(계정관리) > 사용자관리
-- Remove: 일반 시스템관리 > 사용자관리

SELECT
    c.MENU_CD,
    c.MENU_NM,
    c.MENU_URL,
    c.UP_MENU_CD,
    p.MENU_NM AS PARENT_MENU_NM,
    c.SORT_ORD,
    c.USE_YN
FROM TB_MENU c
LEFT JOIN TB_MENU p ON c.UP_MENU_CD = p.MENU_CD
WHERE c.MENU_URL = '/account/user-manage'
   OR c.MENU_NM LIKE '%사용자%'
ORDER BY p.SORT_ORD, c.SORT_ORD, c.MENU_CD;

DELETE FROM TB_MENU_AUTH
WHERE MENU_CD IN (
    SELECT c.MENU_CD
    FROM TB_MENU c
    LEFT JOIN TB_MENU p ON c.UP_MENU_CD = p.MENU_CD
    WHERE c.MENU_URL = '/account/user-manage'
      AND NVL(p.MENU_NM, ' ') NOT LIKE '%계정%'
);

DELETE FROM TB_MENU
WHERE MENU_CD IN (
    SELECT c.MENU_CD
    FROM TB_MENU c
    LEFT JOIN TB_MENU p ON c.UP_MENU_CD = p.MENU_CD
    WHERE c.MENU_URL = '/account/user-manage'
      AND NVL(p.MENU_NM, ' ') NOT LIKE '%계정%'
);

-- COMMIT;
