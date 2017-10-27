--
-- [y] hybris Platform
--
-- Copyright (c) 2017 SAP SE or an SAP affiliate company.
-- All rights reserved.
--
-- This software is the confidential and proprietary information of SAP
-- ("Confidential Information"). You shall not disclose such Confidential
-- Information and shall use it only in accordance with the terms of the
-- license agreement you entered into with SAP.
--

-- ==========================================================================================
-- Drop indexes that will be recreated later on (columns removed)
-- ==========================================================================================

DROP INDEX "AK_TargetItemMeta" ON "TargetItemMeta";

DROP INDEX "AK_RawItemMeta" ON "RawItemMeta";

DROP INDEX "UX_TargetSystem_targetSystemName" ON "TargetSystem";

DROP INDEX "UX_DataHubFeed_name" ON "DataHubFeed";

DROP INDEX "UX_DataHubPool_poolName" ON "DataHubPool";

DROP INDEX "AK_CanonicalItemMeta" ON "CanonicalItemMeta";

-- ==========================================================================================
-- Remove unused columns
-- ==========================================================================================

ALTER TABLE "CanonicalAttrDef" DROP COLUMN "tenant";
ALTER TABLE "CanonicalAttrDef" DROP COLUMN "schemaless_attrs";

ALTER TABLE "TargetItemMeta" DROP COLUMN "tenant";
ALTER TABLE "TargetItemMeta" DROP COLUMN "schemaless_attrs";

ALTER TABLE "TargetSystemPub" DROP COLUMN "tenant";
ALTER TABLE "TargetSystemPub" DROP COLUMN "schemaless_attrs";

ALTER TABLE "PublicationAction" DROP COLUMN "tenant";
ALTER TABLE "PublicationAction" DROP COLUMN "schemaless_attrs";

ALTER TABLE "PublicationError" DROP COLUMN "tenant";
ALTER TABLE "PublicationError" DROP COLUMN "schemaless_attrs";

ALTER TABLE "RawItemStatusCount" DROP COLUMN "tenant";
ALTER TABLE "RawItemStatusCount" DROP COLUMN "schemaless_attrs";

ALTER TABLE "RawItemMeta" DROP COLUMN "tenant";
ALTER TABLE "RawItemMeta" DROP COLUMN "schemaless_attrs";

ALTER TABLE "DataLoadingAction" DROP COLUMN "tenant";
ALTER TABLE "DataLoadingAction" DROP COLUMN "schemaless_attrs";

ALTER TABLE "RawItem" DROP COLUMN "tenant";

ALTER TABLE "TargetSystem" DROP COLUMN "tenant";
ALTER TABLE "TargetSystem" DROP COLUMN "schemaless_attrs";

ALTER TABLE "CanonicalItem" DROP COLUMN "tenant";

ALTER TABLE "TargetAttrDef" DROP COLUMN "tenant";
ALTER TABLE "TargetAttrDef" DROP COLUMN "schemaless_attrs";

ALTER TABLE "DataHubFeed" DROP COLUMN "tenant";
ALTER TABLE "DataHubFeed" DROP COLUMN "schemaless_attrs";

ALTER TABLE "DataHubPool" DROP COLUMN "tenant";
ALTER TABLE "DataHubPool" DROP COLUMN "schemaless_attrs";

ALTER TABLE "CanonicalAttrModDef" DROP COLUMN "tenant";
ALTER TABLE "CanonicalAttrModDef" DROP COLUMN "schemaless_attrs";

ALTER TABLE "RawAttrModDef" DROP COLUMN "tenant";
ALTER TABLE "RawAttrModDef" DROP COLUMN "schemaless_attrs";

ALTER TABLE "TargetItem" DROP COLUMN "tenant";

ALTER TABLE "CanonicalItemStatusCount" DROP COLUMN "tenant";
ALTER TABLE "CanonicalItemStatusCount" DROP COLUMN "schemaless_attrs";

ALTER TABLE "CanonicalItemMeta" DROP COLUMN "tenant";
ALTER TABLE "CanonicalItemMeta" DROP COLUMN "schemaless_attrs";

ALTER TABLE "CompositionAction" DROP COLUMN "tenant";
ALTER TABLE "CompositionAction" DROP COLUMN "schemaless_attrs";

ALTER TABLE "CanItemPubStatus" DROP COLUMN "tenant";
ALTER TABLE "CanItemPubStatus" DROP COLUMN "schemaless_attrs";

ALTER TABLE "PublicationRetry" DROP COLUMN "tenant";
ALTER TABLE "PublicationRetry" DROP COLUMN "schemaless_attrs";

ALTER TABLE "CanonicalPubStatusCount" DROP COLUMN "tenant";
ALTER TABLE "CanonicalPubStatusCount" DROP COLUMN "schemaless_attrs";

ALTER TABLE "TargetSysExportCode" DROP COLUMN "tenant";

ALTER TABLE "TargetItemMetadata_dependsOn" DROP COLUMN "tenant";

ALTER TABLE "CanonicalItem_RawItem" DROP COLUMN "tenant";

ALTER TABLE "DataHubFeed_Pool" DROP COLUMN "tenant";

ALTER TABLE "ManagedTarItem_expCodeAttrMap" DROP COLUMN "tenant";

ALTER TABLE "hybris_sequences" DROP COLUMN "tenant";

-- ==========================================================================================
-- Remove unused tables
-- ==========================================================================================

SET quoted_identifier on;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'YTenant')
  BEGIN
    DECLARE @mytable1 nvarchar(256), @myconstraint1 nvarchar(256)
    DECLARE refcursor CURSOR FOR
      SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
      FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
      WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'YTenant'  OPEN refcursor
    FETCH NEXT FROM refcursor INTO @mytable1, @myconstraint1
    WHILE @@FETCH_STATUS = 0
      BEGIN
        EXEC ('ALTER TABLE '+@mytable1+' DROP CONSTRAINT '+@myconstraint1)
        FETCH NEXT FROM refcursor INTO @mytable1, @myconstraint1
      END
    CLOSE refcursor
    DEALLOCATE refcursor
    DROP TABLE "YTenant"
  END;

SET quoted_identifier on;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'YTypeCode')
  BEGIN
    DECLARE @mytable2 nvarchar(256), @myconstraint2 nvarchar(256)
    DECLARE refcursor CURSOR FOR
      SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
      FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
      WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'YTypeCode'  OPEN refcursor
    FETCH NEXT FROM refcursor INTO @mytable2, @myconstraint2
    WHILE @@FETCH_STATUS = 0
      BEGIN
        EXEC ('ALTER TABLE '+@mytable2+' DROP CONSTRAINT '+@myconstraint2)
        FETCH NEXT FROM refcursor INTO @mytable2, @myconstraint2
      END
    CLOSE refcursor
    DEALLOCATE refcursor
    DROP TABLE "YTypeCode"
  END;

SET quoted_identifier on;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'YSchemalessAttribute')
  BEGIN
    DECLARE @mytable3 nvarchar(256), @myconstraint3 nvarchar(256)
    DECLARE refcursor CURSOR FOR
      SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
      FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
      WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'YSchemalessAttribute'  OPEN refcursor
    FETCH NEXT FROM refcursor INTO @mytable3, @myconstraint3
    WHILE @@FETCH_STATUS = 0
      BEGIN
        EXEC ('ALTER TABLE '+@mytable3+' DROP CONSTRAINT '+@myconstraint3)
        FETCH NEXT FROM refcursor INTO @mytable3, @myconstraint3
      END
    CLOSE refcursor
    DEALLOCATE refcursor
    DROP TABLE "YSchemalessAttribute"
  END;

SET quoted_identifier on;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'YTenantProperty')
  BEGIN
    DECLARE @mytable4 nvarchar(256), @myconstraint4 nvarchar(256)
    DECLARE refcursor CURSOR FOR
      SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
      FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
      WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'YTenantProperty'  OPEN refcursor
    FETCH NEXT FROM refcursor INTO @mytable4, @myconstraint4
    WHILE @@FETCH_STATUS = 0
      BEGIN
        EXEC ('ALTER TABLE '+@mytable4+' DROP CONSTRAINT '+@myconstraint4)
        FETCH NEXT FROM refcursor INTO @mytable4, @myconstraint4
      END
    CLOSE refcursor
    DEALLOCATE refcursor
    DROP TABLE "YTenantProperty"
  END;

SET quoted_identifier on;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'YDomainDefinition')
  BEGIN
    DECLARE @mytable5 nvarchar(256), @myconstraint5 nvarchar(256)
    DECLARE refcursor CURSOR FOR
      SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
      FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
      WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'YDomainDefinition'  OPEN refcursor
    FETCH NEXT FROM refcursor INTO @mytable5, @myconstraint5
    WHILE @@FETCH_STATUS = 0
      BEGIN
        EXEC ('ALTER TABLE '+@mytable5+' DROP CONSTRAINT '+@myconstraint5)
        FETCH NEXT FROM refcursor INTO @mytable5, @myconstraint5
      END
    CLOSE refcursor
    DEALLOCATE refcursor
    DROP TABLE "YDomainDefinition"
  END;

SET quoted_identifier on;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'loc_props')
  BEGIN
    DECLARE @mytable6 nvarchar(256), @myconstraint6 nvarchar(256)
    DECLARE refcursor CURSOR FOR
      SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
      FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
      WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'loc_props'  OPEN refcursor
    FETCH NEXT FROM refcursor INTO @mytable6, @myconstraint6
    WHILE @@FETCH_STATUS = 0
      BEGIN
        EXEC ('ALTER TABLE '+@mytable6+' DROP CONSTRAINT '+@myconstraint6)
        FETCH NEXT FROM refcursor INTO @mytable6, @myconstraint6
      END
    CLOSE refcursor
    DEALLOCATE refcursor
    DROP TABLE "loc_props"
  END;

SET quoted_identifier on;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'hybris_tenants')
  BEGIN
    DECLARE @mytable7 nvarchar(256), @myconstraint7 nvarchar(256)
    DECLARE refcursor CURSOR FOR
      SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
      FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
      WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'hybris_tenants'  OPEN refcursor
    FETCH NEXT FROM refcursor INTO @mytable7, @myconstraint7
    WHILE @@FETCH_STATUS = 0
      BEGIN
        EXEC ('ALTER TABLE '+@mytable7+' DROP CONSTRAINT '+@myconstraint7)
        FETCH NEXT FROM refcursor INTO @mytable7, @myconstraint7
      END
    CLOSE refcursor
    DEALLOCATE refcursor
    DROP TABLE "hybris_tenants"
  END;

SET quoted_identifier on;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'hybris_meta_data')
  BEGIN
    DECLARE @mytable8 nvarchar(256), @myconstraint8 nvarchar(256)
    DECLARE refcursor CURSOR FOR
      SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
      FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
      WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'hybris_meta_data'  OPEN refcursor
    FETCH NEXT FROM refcursor INTO @mytable8, @myconstraint8
    WHILE @@FETCH_STATUS = 0
      BEGIN
        EXEC ('ALTER TABLE '+@mytable8+' DROP CONSTRAINT '+@myconstraint8)
        FETCH NEXT FROM refcursor INTO @mytable8, @myconstraint8
      END
    CLOSE refcursor
    DEALLOCATE refcursor
    DROP TABLE "hybris_meta_data"
  END;

SET quoted_identifier on;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'hybris_type_codes')
  BEGIN
    DECLARE @mytable9 nvarchar(256), @myconstraint9 nvarchar(256)
    DECLARE refcursor CURSOR FOR
      SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
      FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
      WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'hybris_type_codes'  OPEN refcursor
    FETCH NEXT FROM refcursor INTO @mytable9, @myconstraint9
    WHILE @@FETCH_STATUS = 0
      BEGIN
        EXEC ('ALTER TABLE '+@mytable9+' DROP CONSTRAINT '+@myconstraint9)
        FETCH NEXT FROM refcursor INTO @mytable9, @myconstraint9
      END
    CLOSE refcursor
    DEALLOCATE refcursor
    DROP TABLE "hybris_type_codes"
  END;

SET quoted_identifier on;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'schemaless_metadata')
  BEGIN
    DECLARE @mytable10 nvarchar(256), @myconstraint10 nvarchar(256)
    DECLARE refcursor CURSOR FOR
      SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
      FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
      WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'schemaless_metadata'  OPEN refcursor
    FETCH NEXT FROM refcursor INTO @mytable10, @myconstraint10
    WHILE @@FETCH_STATUS = 0
      BEGIN
        EXEC ('ALTER TABLE '+@mytable10+' DROP CONSTRAINT '+@myconstraint10)
        FETCH NEXT FROM refcursor INTO @mytable10, @myconstraint10
      END
    CLOSE refcursor
    DEALLOCATE refcursor
    DROP TABLE "schemaless_metadata"
  END;

SET quoted_identifier on;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'schemaless_attrs')
  BEGIN
    DECLARE @mytable11 nvarchar(256), @myconstraint11 nvarchar(256)
    DECLARE refcursor CURSOR FOR
      SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
      FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
      WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'schemaless_attrs'  OPEN refcursor
    FETCH NEXT FROM refcursor INTO @mytable11, @myconstraint11
    WHILE @@FETCH_STATUS = 0
      BEGIN
        EXEC ('ALTER TABLE '+@mytable11+' DROP CONSTRAINT '+@myconstraint11)
        FETCH NEXT FROM refcursor INTO @mytable11, @myconstraint11
      END
    CLOSE refcursor
    DEALLOCATE refcursor
    DROP TABLE "schemaless_attrs"
  END;

SET quoted_identifier on;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'hybris_engine_properties')
  BEGIN
    DECLARE @mytable12 nvarchar(256), @myconstraint12 nvarchar(256)
    DECLARE refcursor CURSOR FOR
      SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
      FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
      WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'hybris_engine_properties'  OPEN refcursor
    FETCH NEXT FROM refcursor INTO @mytable12, @myconstraint12
    WHILE @@FETCH_STATUS = 0
      BEGIN
        EXEC ('ALTER TABLE '+@mytable12+' DROP CONSTRAINT '+@myconstraint12)
        FETCH NEXT FROM refcursor INTO @mytable12, @myconstraint12
      END
    CLOSE refcursor
    DEALLOCATE refcursor
    DROP TABLE "hybris_engine_properties"
  END;

-- ==========================================================================================
-- Recreate indexes
-- ==========================================================================================

CREATE UNIQUE INDEX "AK_TargetItemMeta" ON "TargetItemMeta" ("itemmetadataid");

CREATE UNIQUE INDEX "AK_RawItemMeta" ON "RawItemMeta" ("itemmetadataid");

CREATE UNIQUE INDEX "UX_TargetSystem_targetSystemName" ON "TargetSystem" ("targetsystemname");

CREATE UNIQUE INDEX "UX_DataHubFeed_name" ON "DataHubFeed" ("name");

CREATE UNIQUE INDEX "UX_DataHubPool_poolName" ON "DataHubPool" ("poolname");

CREATE UNIQUE INDEX "AK_CanonicalItemMeta" ON "CanonicalItemMeta" ("itemmetadataid");

-- ==========================================================================================
-- Create DataHubVersion table
-- ==========================================================================================

CREATE TABLE "DataHubVersion"
(
  "version" NVARCHAR(128) NOT NULL
);

TRUNCATE TABLE "DataHubVersion";

INSERT INTO "DataHubVersion" values ('6.3.0');
