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

DROP INDEX "AK_TargetItemMeta";

DROP INDEX "AK_RawItemMeta";

DROP INDEX "UX_TargetSystem_targetSystemName";

DROP INDEX "UX_DataHubFeed_name";

DROP INDEX "UX_DataHubPool_poolName";

DROP INDEX "AK_CanonicalItemMeta";

-- ==========================================================================================
-- Remove unused columns
-- ==========================================================================================

ALTER TABLE "CanonicalAttrDef" DROP ("tenant", "schemaless_attrs");

ALTER TABLE "TargetItemMeta" DROP ("tenant", "schemaless_attrs");

ALTER TABLE "TargetSystemPub" DROP ("tenant", "schemaless_attrs");

ALTER TABLE "PublicationAction" DROP ("tenant", "schemaless_attrs");

ALTER TABLE "PublicationError" DROP ("tenant", "schemaless_attrs");

ALTER TABLE "RawItemStatusCount" DROP ("tenant", "schemaless_attrs");

ALTER TABLE "RawItemMeta" DROP ("tenant", "schemaless_attrs");

ALTER TABLE "DataLoadingAction" DROP ("tenant", "schemaless_attrs");

ALTER TABLE "RawItem" DROP ("tenant");

ALTER TABLE "TargetSystem" DROP ("tenant", "schemaless_attrs");

ALTER TABLE "CanonicalItem" DROP ("tenant");

ALTER TABLE "TargetAttrDef" DROP ("tenant", "schemaless_attrs");

ALTER TABLE "DataHubFeed" DROP ("tenant", "schemaless_attrs");

ALTER TABLE "DataHubPool" DROP ("tenant", "schemaless_attrs");

ALTER TABLE "CanonicalAttrModDef" DROP ("tenant", "schemaless_attrs");

ALTER TABLE "RawAttrModDef" DROP ("tenant", "schemaless_attrs");

ALTER TABLE "TargetItem" DROP ("tenant");

ALTER TABLE "CanonicalItemStatusCount" DROP ("tenant", "schemaless_attrs");

ALTER TABLE "CanonicalItemMeta" DROP ("tenant", "schemaless_attrs");

ALTER TABLE "CompositionAction" DROP ("tenant", "schemaless_attrs");

ALTER TABLE "CanItemPubStatus" DROP ("tenant", "schemaless_attrs");

ALTER TABLE "PublicationRetry" DROP ("tenant", "schemaless_attrs");

ALTER TABLE "CanonicalPubStatusCount" DROP ("tenant", "schemaless_attrs");

ALTER TABLE "TargetSysExportCode" DROP ("tenant");

ALTER TABLE "TargetItemMetadata_dependsOn" DROP ("tenant");

ALTER TABLE "CanonicalItem_RawItem" DROP ("tenant");

-- Not possible to drop this column because of:
-- SAP DBTech JDBC: [3]: fatal error: ColDicVal (1000006,2) not found. See error trace for details.
-- ALTER TABLE "DataHubFeed_Pool" DROP ("tenant");

ALTER TABLE "ManagedTarItem_expCodeAttrMap" DROP ("tenant");

ALTER TABLE "hybris_sequences" DROP ("tenant");

-- ==========================================================================================
-- Remove unused tables
-- ==========================================================================================

DROP TABLE "YTenant" CASCADE;

DROP TABLE "YTypeCode" CASCADE;

DROP TABLE "YSchemalessAttribute" CASCADE;

DROP TABLE "YTenantProperty" CASCADE;

DROP TABLE "YDomainDefinition" CASCADE;

DROP TABLE "loc_props" CASCADE;

DROP TABLE "hybris_tenants" CASCADE;

DROP TABLE "hybris_meta_data" CASCADE;

DROP TABLE "hybris_type_codes" CASCADE;

DROP TABLE "schemaless_metadata" CASCADE;

DROP TABLE "schemaless_attrs" CASCADE;

DROP TABLE "hybris_engine_properties" CASCADE;

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
    "version" VARCHAR(128) NOT NULL
);

TRUNCATE TABLE "DataHubVersion";

INSERT INTO "DataHubVersion" values ('6.3.0');