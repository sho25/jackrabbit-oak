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
name|plugins
operator|.
name|document
operator|.
name|rdb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DatabaseMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Defines variation in the capabilities of different RDBs.  */
end_comment

begin_enum
specifier|public
enum|enum
name|RDBBlobStoreDB
block|{
name|H2
argument_list|(
literal|"H2"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|checkVersion
parameter_list|(
name|DatabaseMetaData
name|md
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|RDBJDBCTools
operator|.
name|versionCheck
argument_list|(
name|md
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|,
name|description
argument_list|)
return|;
block|}
block|}
block|,
name|DERBY
argument_list|(
literal|"Apache Derby"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|checkVersion
parameter_list|(
name|DatabaseMetaData
name|md
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|RDBJDBCTools
operator|.
name|versionCheck
argument_list|(
name|md
argument_list|,
literal|10
argument_list|,
literal|11
argument_list|,
name|description
argument_list|)
return|;
block|}
block|}
block|,
name|DB2
argument_list|(
literal|"DB2"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|checkVersion
parameter_list|(
name|DatabaseMetaData
name|md
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|RDBJDBCTools
operator|.
name|versionCheck
argument_list|(
name|md
argument_list|,
literal|10
argument_list|,
literal|1
argument_list|,
name|description
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDataTableCreationStatement
parameter_list|(
name|String
name|tableName
parameter_list|)
block|{
return|return
literal|"create table "
operator|+
name|tableName
operator|+
literal|" (ID varchar("
operator|+
name|RDBBlobStore
operator|.
name|IDSIZE
operator|+
literal|") not null primary key, DATA blob("
operator|+
name|MINBLOB
operator|+
literal|"))"
return|;
block|}
block|}
block|,
name|MSSQL
argument_list|(
literal|"Microsoft SQL Server"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|checkVersion
parameter_list|(
name|DatabaseMetaData
name|md
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|RDBJDBCTools
operator|.
name|versionCheck
argument_list|(
name|md
argument_list|,
literal|11
argument_list|,
literal|0
argument_list|,
name|description
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDataTableCreationStatement
parameter_list|(
name|String
name|tableName
parameter_list|)
block|{
return|return
literal|"create table "
operator|+
name|tableName
operator|+
literal|" (ID varchar("
operator|+
name|RDBBlobStore
operator|.
name|IDSIZE
operator|+
literal|") not null, DATA varbinary(max)"
operator|+
literal|"constraint "
operator|+
name|tableName
operator|+
literal|"_PK primary key clustered (ID ASC))"
return|;
block|}
block|}
block|,
name|MYSQL
argument_list|(
literal|"MySQL"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|checkVersion
parameter_list|(
name|DatabaseMetaData
name|md
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|RDBJDBCTools
operator|.
name|versionCheck
argument_list|(
name|md
argument_list|,
literal|5
argument_list|,
literal|5
argument_list|,
name|description
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDataTableCreationStatement
parameter_list|(
name|String
name|tableName
parameter_list|)
block|{
return|return
literal|"create table "
operator|+
name|tableName
operator|+
literal|" (ID varchar("
operator|+
name|RDBBlobStore
operator|.
name|IDSIZE
operator|+
literal|") not null primary key, DATA mediumblob)"
return|;
block|}
block|}
block|,
name|ORACLE
argument_list|(
literal|"Oracle"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|checkVersion
parameter_list|(
name|DatabaseMetaData
name|md
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|RDBJDBCTools
operator|.
name|versionCheck
argument_list|(
name|md
argument_list|,
literal|12
argument_list|,
literal|1
argument_list|,
literal|12
argument_list|,
literal|1
argument_list|,
name|description
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getMetaTableCreationStatement
parameter_list|(
name|String
name|tableName
parameter_list|)
block|{
return|return
literal|"create table "
operator|+
name|tableName
operator|+
literal|" (ID varchar("
operator|+
name|RDBBlobStore
operator|.
name|IDSIZE
operator|+
literal|") not null primary key, LVL number, LASTMOD number)"
return|;
block|}
block|}
block|,
name|POSTGRES
argument_list|(
literal|"PostgreSQL"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|checkVersion
parameter_list|(
name|DatabaseMetaData
name|md
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|RDBJDBCTools
operator|.
name|versionCheck
argument_list|(
name|md
argument_list|,
literal|9
argument_list|,
literal|5
argument_list|,
literal|9
argument_list|,
literal|4
argument_list|,
name|description
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDataTableCreationStatement
parameter_list|(
name|String
name|tableName
parameter_list|)
block|{
return|return
literal|"create table "
operator|+
name|tableName
operator|+
literal|" (ID varchar("
operator|+
name|RDBBlobStore
operator|.
name|IDSIZE
operator|+
literal|") not null primary key, DATA bytea)"
return|;
block|}
block|}
block|,
name|DEFAULT
argument_list|(
literal|"default"
argument_list|)
block|{     }
block|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RDBBlobStoreDB
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// blob size we need to support
specifier|private
specifier|static
specifier|final
name|int
name|MINBLOB
init|=
literal|2
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|public
name|String
name|checkVersion
parameter_list|(
name|DatabaseMetaData
name|md
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
literal|"Unknown database type: "
operator|+
name|md
operator|.
name|getDatabaseProductName
argument_list|()
return|;
block|}
specifier|public
name|String
name|getDataTableCreationStatement
parameter_list|(
name|String
name|tableName
parameter_list|)
block|{
return|return
literal|"create table "
operator|+
name|tableName
operator|+
literal|" (ID varchar("
operator|+
name|RDBBlobStore
operator|.
name|IDSIZE
operator|+
literal|") not null primary key, DATA blob)"
return|;
block|}
specifier|public
name|String
name|getMetaTableCreationStatement
parameter_list|(
name|String
name|tableName
parameter_list|)
block|{
return|return
literal|"create table "
operator|+
name|tableName
operator|+
literal|" (ID varchar("
operator|+
name|RDBBlobStore
operator|.
name|IDSIZE
operator|+
literal|") not null primary key, LVL int, LASTMOD bigint)"
return|;
block|}
specifier|protected
name|String
name|description
decl_stmt|;
specifier|private
name|RDBBlobStoreDB
parameter_list|(
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|description
return|;
block|}
annotation|@
name|NotNull
specifier|public
specifier|static
name|RDBBlobStoreDB
name|getValue
parameter_list|(
name|String
name|desc
parameter_list|)
block|{
for|for
control|(
name|RDBBlobStoreDB
name|db
range|:
name|RDBBlobStoreDB
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|db
operator|.
name|description
operator|.
name|equals
argument_list|(
name|desc
argument_list|)
condition|)
block|{
return|return
name|db
return|;
block|}
elseif|else
if|if
condition|(
name|db
operator|==
name|DB2
operator|&&
name|desc
operator|.
name|startsWith
argument_list|(
literal|"DB2/"
argument_list|)
condition|)
block|{
return|return
name|db
return|;
block|}
block|}
name|LOG
operator|.
name|error
argument_list|(
literal|"DB type "
operator|+
name|desc
operator|+
literal|" unknown, trying default settings"
argument_list|)
expr_stmt|;
name|DEFAULT
operator|.
name|description
operator|=
name|desc
operator|+
literal|" - using default settings"
expr_stmt|;
return|return
name|DEFAULT
return|;
block|}
block|}
end_enum

end_unit

