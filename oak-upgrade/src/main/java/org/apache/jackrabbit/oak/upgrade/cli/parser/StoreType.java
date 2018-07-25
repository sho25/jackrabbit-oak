begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|upgrade
operator|.
name|cli
operator|.
name|parser
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
operator|.
name|removeStart
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|azure
operator|.
name|util
operator|.
name|AzureConfigurationParserUtils
operator|.
name|KEY_ACCOUNT_NAME
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|azure
operator|.
name|util
operator|.
name|AzureConfigurationParserUtils
operator|.
name|KEY_CONNECTION_STRING
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|azure
operator|.
name|util
operator|.
name|AzureConfigurationParserUtils
operator|.
name|KEY_CONTAINER_NAME
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|azure
operator|.
name|util
operator|.
name|AzureConfigurationParserUtils
operator|.
name|KEY_DIR
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|azure
operator|.
name|util
operator|.
name|AzureConfigurationParserUtils
operator|.
name|KEY_STORAGE_URI
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|azure
operator|.
name|util
operator|.
name|AzureConfigurationParserUtils
operator|.
name|isCustomAzureConnectionString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|azure
operator|.
name|util
operator|.
name|AzureConfigurationParserUtils
operator|.
name|parseAzureConfigurationFromCustomConnection
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|azure
operator|.
name|util
operator|.
name|AzureConfigurationParserUtils
operator|.
name|parseAzureConfigurationFromUri
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|upgrade
operator|.
name|cli
operator|.
name|node
operator|.
name|Jackrabbit2Factory
operator|.
name|isJcr2Repository
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|upgrade
operator|.
name|cli
operator|.
name|node
operator|.
name|Jackrabbit2Factory
operator|.
name|isRepositoryXml
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|upgrade
operator|.
name|cli
operator|.
name|parser
operator|.
name|StoreArguments
operator|.
name|SEGMENT_AZURE_PREFIX
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|upgrade
operator|.
name|cli
operator|.
name|parser
operator|.
name|StoreArguments
operator|.
name|SEGMENT_OLD_PREFIX
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|upgrade
operator|.
name|cli
operator|.
name|node
operator|.
name|Jackrabbit2Factory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|upgrade
operator|.
name|cli
operator|.
name|node
operator|.
name|JdbcFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|upgrade
operator|.
name|cli
operator|.
name|node
operator|.
name|MongoFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|upgrade
operator|.
name|cli
operator|.
name|node
operator|.
name|SegmentAzureFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|upgrade
operator|.
name|cli
operator|.
name|node
operator|.
name|SegmentFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|upgrade
operator|.
name|cli
operator|.
name|node
operator|.
name|SegmentTarFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|upgrade
operator|.
name|cli
operator|.
name|node
operator|.
name|StoreFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|upgrade
operator|.
name|cli
operator|.
name|parser
operator|.
name|StoreArguments
operator|.
name|MigrationDirection
import|;
end_import

begin_enum
specifier|public
enum|enum
name|StoreType
block|{
name|JCR2_XML
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|String
name|argument
parameter_list|)
block|{
return|return
name|isRepositoryXml
argument_list|(
name|argument
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|StoreFactory
name|createFactory
parameter_list|(
name|String
index|[]
name|paths
parameter_list|,
name|MigrationDirection
name|direction
parameter_list|,
name|MigrationOptions
name|migrationOptions
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSupportLongNames
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
block|,
name|JCR2_DIR
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|String
name|argument
parameter_list|)
block|{
return|return
name|isJcr2Repository
argument_list|(
name|argument
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|StoreFactory
name|createFactory
parameter_list|(
name|String
index|[]
name|paths
parameter_list|,
name|MigrationDirection
name|direction
parameter_list|,
name|MigrationOptions
name|migrationOptions
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSupportLongNames
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
block|,
name|JCR2_DIR_XML
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|String
name|argument
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|StoreFactory
name|createFactory
parameter_list|(
name|String
index|[]
name|paths
parameter_list|,
name|MigrationDirection
name|direction
parameter_list|,
name|MigrationOptions
name|migrationOptions
parameter_list|)
block|{
return|return
operator|new
name|StoreFactory
argument_list|(
operator|new
name|Jackrabbit2Factory
argument_list|(
name|paths
index|[
literal|0
index|]
argument_list|,
name|paths
index|[
literal|1
index|]
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSupportLongNames
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
block|,
name|JDBC
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|String
name|argument
parameter_list|)
block|{
return|return
name|argument
operator|.
name|startsWith
argument_list|(
literal|"jdbc:"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|StoreFactory
name|createFactory
parameter_list|(
name|String
index|[]
name|paths
parameter_list|,
name|MigrationDirection
name|direction
parameter_list|,
name|MigrationOptions
name|migrationOptions
parameter_list|)
block|{
name|String
name|username
operator|,
name|password
expr_stmt|;
if|if
condition|(
name|direction
operator|==
name|MigrationDirection
operator|.
name|SRC
condition|)
block|{
name|username
operator|=
name|migrationOptions
operator|.
name|getSrcUser
argument_list|()
expr_stmt|;
name|password
operator|=
name|migrationOptions
operator|.
name|getSrcPassword
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|username
operator|=
name|migrationOptions
operator|.
name|getDstUser
argument_list|()
expr_stmt|;
name|password
operator|=
name|migrationOptions
operator|.
name|getDstPassword
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|StoreFactory
argument_list|(
operator|new
name|JdbcFactory
argument_list|(
name|paths
index|[
literal|0
index|]
argument_list|,
name|migrationOptions
operator|.
name|getCacheSizeInMB
argument_list|()
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
name|direction
operator|==
name|MigrationDirection
operator|.
name|SRC
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSupportLongNames
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
block|,
name|MONGO
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|String
name|argument
parameter_list|)
block|{
return|return
name|argument
operator|.
name|startsWith
argument_list|(
literal|"mongodb://"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|StoreFactory
name|createFactory
parameter_list|(
name|String
index|[]
name|paths
parameter_list|,
name|MigrationDirection
name|direction
parameter_list|,
name|MigrationOptions
name|migrationOptions
parameter_list|)
block|{
return|return
operator|new
name|StoreFactory
argument_list|(
operator|new
name|MongoFactory
argument_list|(
name|paths
index|[
literal|0
index|]
argument_list|,
name|migrationOptions
operator|.
name|getCacheSizeInMB
argument_list|()
argument_list|,
name|direction
operator|==
name|MigrationDirection
operator|.
name|SRC
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSupportLongNames
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
block|,
name|SEGMENT
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|String
name|argument
parameter_list|)
block|{
return|return
name|argument
operator|.
name|startsWith
argument_list|(
name|SEGMENT_OLD_PREFIX
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|StoreFactory
name|createFactory
parameter_list|(
name|String
index|[]
name|paths
parameter_list|,
name|MigrationDirection
name|direction
parameter_list|,
name|MigrationOptions
name|migrationOptions
parameter_list|)
block|{
name|String
name|path
init|=
name|removeStart
argument_list|(
name|paths
index|[
literal|0
index|]
argument_list|,
name|SEGMENT_OLD_PREFIX
argument_list|)
decl_stmt|;
return|return
operator|new
name|StoreFactory
argument_list|(
operator|new
name|SegmentFactory
argument_list|(
name|path
argument_list|,
name|migrationOptions
operator|.
name|isDisableMmap
argument_list|()
argument_list|,
name|direction
operator|==
name|MigrationDirection
operator|.
name|SRC
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSupportLongNames
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
block|,
name|SEGMENT_AZURE
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|String
name|argument
parameter_list|)
block|{
return|return
name|argument
operator|.
name|startsWith
argument_list|(
literal|"az:"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|StoreFactory
name|createFactory
parameter_list|(
name|String
index|[]
name|paths
parameter_list|,
name|MigrationDirection
name|direction
parameter_list|,
name|MigrationOptions
name|migrationOptions
parameter_list|)
block|{
name|String
name|path
init|=
name|removeStart
argument_list|(
name|paths
index|[
literal|0
index|]
argument_list|,
name|SEGMENT_AZURE_PREFIX
argument_list|)
decl_stmt|;
if|if
condition|(
name|isCustomAzureConnectionString
argument_list|(
name|path
argument_list|)
condition|)
block|{
comment|// azure configuration specified through connection string
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|config
init|=
name|parseAzureConfigurationFromCustomConnection
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
operator|new
name|StoreFactory
argument_list|(
operator|new
name|SegmentAzureFactory
operator|.
name|Builder
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|KEY_DIR
argument_list|)
argument_list|,
name|direction
operator|==
name|MigrationDirection
operator|.
name|SRC
argument_list|)
operator|.
name|connectionString
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|KEY_CONNECTION_STRING
argument_list|)
argument_list|)
operator|.
name|containerName
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|KEY_CONTAINER_NAME
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
comment|// azure configuration specified through URI
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|config
init|=
name|parseAzureConfigurationFromUri
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
operator|new
name|StoreFactory
argument_list|(
operator|new
name|SegmentAzureFactory
operator|.
name|Builder
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|KEY_DIR
argument_list|)
argument_list|,
name|direction
operator|==
name|MigrationDirection
operator|.
name|SRC
argument_list|)
operator|.
name|accountName
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|KEY_ACCOUNT_NAME
argument_list|)
argument_list|)
operator|.
name|uri
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|KEY_STORAGE_URI
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSupportLongNames
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
block|,
name|SEGMENT_TAR
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|String
name|argument
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|StoreFactory
name|createFactory
parameter_list|(
name|String
index|[]
name|paths
parameter_list|,
name|MigrationDirection
name|direction
parameter_list|,
name|MigrationOptions
name|migrationOptions
parameter_list|)
block|{
return|return
operator|new
name|StoreFactory
argument_list|(
operator|new
name|SegmentTarFactory
argument_list|(
name|paths
index|[
literal|0
index|]
argument_list|,
name|migrationOptions
operator|.
name|isDisableMmap
argument_list|()
argument_list|,
name|direction
operator|==
name|MigrationDirection
operator|.
name|SRC
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSupportLongNames
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
block|;
specifier|public
specifier|static
name|StoreType
name|getMatchingType
parameter_list|(
name|String
name|argument
parameter_list|)
block|{
for|for
control|(
name|StoreType
name|t
range|:
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|t
operator|.
name|matches
argument_list|(
name|argument
argument_list|)
condition|)
block|{
return|return
name|t
return|;
block|}
block|}
return|return
name|SEGMENT_TAR
return|;
block|}
specifier|public
specifier|abstract
name|boolean
name|matches
parameter_list|(
name|String
name|argument
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|StoreFactory
name|createFactory
parameter_list|(
name|String
index|[]
name|paths
parameter_list|,
name|MigrationDirection
name|direction
parameter_list|,
name|MigrationOptions
name|migrationOptions
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|isSupportLongNames
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|isSegment
parameter_list|()
block|{
return|return
name|this
operator|==
name|SEGMENT
operator|||
name|this
operator|==
name|SEGMENT_TAR
operator|||
name|this
operator|==
name|SEGMENT_AZURE
return|;
block|}
block|}
end_enum

end_unit

