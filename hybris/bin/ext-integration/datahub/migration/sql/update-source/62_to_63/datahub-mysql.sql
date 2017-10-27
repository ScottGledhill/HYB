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

DROP INDEX `AK_TargetItemMeta` ON `TargetItemMeta`;

DROP INDEX `AK_RawItemMeta` ON `RawItemMeta`;

DROP INDEX `UX_TargetSystem_targetSystemName` ON `TargetSystem`;

DROP INDEX `UX_DataHubFeed_name` ON `DataHubFeed`;

DROP INDEX `UX_DataHubPool_poolName` ON `DataHubPool`;

DROP INDEX `AK_CanonicalItemMeta` ON `CanonicalItemMeta`;

-- ==========================================================================================
-- Remove unused columns
-- ==========================================================================================

ALTER TABLE `CanonicalAttrDef` DROP COLUMN `tenant`;
ALTER TABLE `CanonicalAttrDef` DROP COLUMN `schemaless_attrs`;

ALTER TABLE `TargetItemMeta` DROP COLUMN `tenant`;
ALTER TABLE `TargetItemMeta` DROP COLUMN `schemaless_attrs`;

ALTER TABLE `TargetSystemPub` DROP COLUMN `tenant`;
ALTER TABLE `TargetSystemPub` DROP COLUMN `schemaless_attrs`;

ALTER TABLE `PublicationAction` DROP COLUMN `tenant`;
ALTER TABLE `PublicationAction` DROP COLUMN `schemaless_attrs`;

ALTER TABLE `PublicationError` DROP COLUMN `tenant`;
ALTER TABLE `PublicationError` DROP COLUMN `schemaless_attrs`;

ALTER TABLE `RawItemStatusCount` DROP COLUMN `tenant`;
ALTER TABLE `RawItemStatusCount` DROP COLUMN `schemaless_attrs`;

ALTER TABLE `RawItemMeta` DROP COLUMN `tenant`;
ALTER TABLE `RawItemMeta` DROP COLUMN `schemaless_attrs`;

ALTER TABLE `DataLoadingAction` DROP COLUMN `tenant`;
ALTER TABLE `DataLoadingAction` DROP COLUMN `schemaless_attrs`;

ALTER TABLE `RawItem` DROP COLUMN `tenant`;

ALTER TABLE `TargetSystem` DROP COLUMN `tenant`;
ALTER TABLE `TargetSystem` DROP COLUMN `schemaless_attrs`;

ALTER TABLE `CanonicalItem` DROP COLUMN `tenant`;

ALTER TABLE `TargetAttrDef` DROP COLUMN `tenant`;
ALTER TABLE `TargetAttrDef` DROP COLUMN `schemaless_attrs`;

ALTER TABLE `DataHubFeed` DROP COLUMN `tenant`;
ALTER TABLE `DataHubFeed` DROP COLUMN `schemaless_attrs`;

ALTER TABLE `DataHubPool` DROP COLUMN `tenant`;
ALTER TABLE `DataHubPool` DROP COLUMN `schemaless_attrs`;

ALTER TABLE `CanonicalAttrModDef` DROP COLUMN `tenant`;
ALTER TABLE `CanonicalAttrModDef` DROP COLUMN `schemaless_attrs`;

ALTER TABLE `RawAttrModDef` DROP COLUMN `tenant`;
ALTER TABLE `RawAttrModDef` DROP COLUMN `schemaless_attrs`;

ALTER TABLE `TargetItem` DROP COLUMN `tenant`;

ALTER TABLE `CanonicalItemStatusCount` DROP COLUMN `tenant`;
ALTER TABLE `CanonicalItemStatusCount` DROP COLUMN `schemaless_attrs`;

ALTER TABLE `CanonicalItemMeta` DROP COLUMN `tenant`;
ALTER TABLE `CanonicalItemMeta` DROP COLUMN `schemaless_attrs`;

ALTER TABLE `CompositionAction` DROP COLUMN `tenant`;
ALTER TABLE `CompositionAction` DROP COLUMN `schemaless_attrs`;

ALTER TABLE `CanItemPubStatus` DROP COLUMN `tenant`;
ALTER TABLE `CanItemPubStatus` DROP COLUMN `schemaless_attrs`;

ALTER TABLE `PublicationRetry` DROP COLUMN `tenant`;
ALTER TABLE `PublicationRetry` DROP COLUMN `schemaless_attrs`;

ALTER TABLE `CanonicalPubStatusCount` DROP COLUMN `tenant`;
ALTER TABLE `CanonicalPubStatusCount` DROP COLUMN `schemaless_attrs`;

ALTER TABLE `TargetSysExportCode` DROP COLUMN `tenant`;

ALTER TABLE `TargetItemMetadata_dependsOn` DROP COLUMN `tenant`;

ALTER TABLE `CanonicalItem_RawItem` DROP COLUMN `tenant`;

ALTER TABLE `DataHubFeed_Pool` DROP COLUMN `tenant`;

ALTER TABLE `ManagedTarItem_expCodeAttrMap` DROP COLUMN `tenant`;

ALTER TABLE `hybris_sequences` DROP COLUMN `tenant`;

-- ==========================================================================================
-- Remove unused tables
-- ==========================================================================================

DROP TABLE IF EXISTS `YTenant`;

DROP TABLE IF EXISTS `YTypeCode`;

DROP TABLE IF EXISTS `YSchemalessAttribute`;

DROP TABLE IF EXISTS `YTenantProperty`;

DROP TABLE IF EXISTS `YDomainDefinition`;

DROP TABLE IF EXISTS `loc_props`;

DROP TABLE IF EXISTS `hybris_tenants`;

DROP TABLE IF EXISTS `hybris_meta_data`;

DROP TABLE IF EXISTS `hybris_type_codes`;

DROP TABLE IF EXISTS `schemaless_metadata`;

DROP TABLE IF EXISTS `schemaless_attrs`;

DROP TABLE IF EXISTS `hybris_engine_properties`;

-- ==========================================================================================
-- Recreate indexes
-- ==========================================================================================

CREATE UNIQUE INDEX `AK_TargetItemMeta` ON `TargetItemMeta` (`itemmetadataid`);

CREATE UNIQUE INDEX `AK_RawItemMeta` ON `RawItemMeta` (`itemmetadataid`);

CREATE UNIQUE INDEX `UX_TargetSystem_targetSystemName` ON `TargetSystem` (`targetsystemname`);

CREATE UNIQUE INDEX `UX_DataHubFeed_name` ON `DataHubFeed` (`name`);

CREATE UNIQUE INDEX `UX_DataHubPool_poolName` ON `DataHubPool` (`poolname`);

CREATE UNIQUE INDEX `AK_CanonicalItemMeta` ON `CanonicalItemMeta` (`itemmetadataid`);

-- ==========================================================================================
-- Create DataHubVersion table
-- ==========================================================================================

CREATE TABLE `DataHubVersion`
(
    `version` VARCHAR(128) NOT NULL
);

TRUNCATE TABLE "DataHubVersion";

INSERT INTO `DataHubVersion` values ('6.3.0');

