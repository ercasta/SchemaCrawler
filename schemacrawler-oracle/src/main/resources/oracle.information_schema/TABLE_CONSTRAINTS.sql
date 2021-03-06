SELECT /*+ PARALLEL(AUTO) */
  NULL AS CONSTRAINT_CATALOG,
  CONSTRAINTS.OWNER AS CONSTRAINT_SCHEMA,
  CONSTRAINTS.CONSTRAINT_NAME,
  NULL AS TABLE_CATALOG,
  CONSTRAINTS.OWNER AS TABLE_SCHEMA,
  CONSTRAINTS.TABLE_NAME,
  CASE CONSTRAINTS.CONSTRAINT_TYPE WHEN 'C' THEN 'CHECK' WHEN 'U' THEN 'UNIQUE' WHEN 'P' THEN 'PRIMARY KEY' WHEN 'R' THEN 'FOREIGN KEY' END 
    AS CONSTRAINT_TYPE,
  CASE WHEN CONSTRAINTS.DEFERRABLE = 'NOT DEFERRABLE' THEN 'N' ELSE 'Y' END 
    AS IS_DEFERRABLE,
  CASE WHEN CONSTRAINTS.DEFERRED = 'IMMEDIATE' THEN 'N' ELSE 'Y' END 
    AS INITIALLY_DEFERRED
FROM
  ALL_CONSTRAINTS CONSTRAINTS
  INNER JOIN ALL_USERS USERS
    ON CONSTRAINTS.OWNER = USERS.USERNAME
WHERE
  USERS.USERNAME NOT IN 
    ('ANONYMOUS', 'APEX_PUBLIC_USER', 'BI', 'CTXSYS', 'DBSNMP', 'DIP', 
    'EXFSYS', 'FLOWS_30000', 'FLOWS_FILES', 'HR', 'IX', 'LBACSYS', 
    'MDDATA', 'MDSYS', 'MGMT_VIEW', 'OE', 'OLAPSYS', 'ORACLE_OCM', 
    'ORDPLUGINS', 'ORDSYS', 'OUTLN', 'OWBSYS', 'PM', 'SCOTT', 'SH', 
    'SI_INFORMTN_SCHEMA', 'SPATIAL_CSW_ADMIN_USR', 'SPATIAL_WFS_ADMIN_USR', 
    'SYS', 'SYSMAN', 'SYSTEM', 'TSMSYS', 'WKPROXY', 'WKSYS', 'WK_TEST', 
    'WMSYS', 'XDB', 'XS$NULL')  
  AND NOT REGEXP_LIKE(USERS.USERNAME, '^APEX_[0-9]{6}$')
  AND NOT REGEXP_LIKE(USERS.USERNAME, '^FLOWS_[0-9]{5,6}$')
  AND CONSTRAINTS.TABLE_NAME NOT LIKE 'BIN$%'
  AND CONSTRAINT_TYPE IN ('C', 'U', 'P', 'R')
