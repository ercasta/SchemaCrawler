SELECT /*+ PARALLEL(AUTO) */
  NULL AS ROUTINE_CATALOG,
  OWNER AS ROUTINE_SCHEMA,
  OBJECT_NAME AS ROUTINE_NAME,
  OBJECT_NAME AS SPECIFIC_NAME,
  'SQL' AS ROUTINE_BODY,
  DBMS_METADATA.GET_DDL(OBJECT_TYPE,OBJECT_NAME,OWNER) AS ROUTINE_DEFINITION
FROM
  ALL_PROCEDURES
WHERE
  UPPER(OBJECT_TYPE) IN ('FUNCTION', 'PROCEDURE')
  AND OWNER NOT IN ('ANONYMOUS','APEX_PUBLIC_USER','CTXSYS','DBSNMP','DIP','EXFSYS','FLOWS_%','FLOWS_FILES','LBACSYS','MDDATA','MDSYS','MGMT_VIEW','OLAPSYS','ORACLE_OCM','ORDDATA','ORDPLUGINS','ORDSYS','OUTLN','OWBSYS','SI_INFORMTN_SCHEMA','SPATIAL_CSW_ADMIN_USR','SPATIAL_WFS_ADMIN_USR','SYS','SYSMAN','SYSTEM','WKPROXY','WKSYS','WK_TEST','WMSYS','XDB','XS$NULL')
  AND OWNER NOT LIKE 'APEX%'
  AND AUTHID = 'CURRENT_USER'
ORDER BY
  ROUTINE_SCHEMA,
  ROUTINE_NAME