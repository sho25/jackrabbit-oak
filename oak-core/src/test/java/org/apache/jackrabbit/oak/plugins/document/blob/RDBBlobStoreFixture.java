begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|blob
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|sql
operator|.
name|DataSource
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
name|plugins
operator|.
name|document
operator|.
name|rdb
operator|.
name|RDBBlobStore
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
name|plugins
operator|.
name|document
operator|.
name|rdb
operator|.
name|RDBDataSourceFactory
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
name|plugins
operator|.
name|document
operator|.
name|rdb
operator|.
name|RDBOptions
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

begin_class
specifier|public
specifier|abstract
class|class
name|RDBBlobStoreFixture
block|{
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
name|RDBBlobStoreFixture
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TABLEPREFIX
init|=
literal|"bstest_"
decl_stmt|;
specifier|public
specifier|abstract
name|RDBBlobStore
name|createRDBBlobStore
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|void
name|dispose
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|isAvailable
parameter_list|()
function_decl|;
specifier|public
specifier|static
specifier|final
name|RDBBlobStoreFixture
name|RDB_DB2
init|=
operator|new
name|MyFixture
argument_list|(
literal|"RDB-DB2"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb-db2-jdbc-url"
argument_list|,
literal|"jdbc:db2://localhost:50000/OAK"
argument_list|)
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb-db2-jdbc-user"
argument_list|,
literal|"oak"
argument_list|)
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb-db2-jdbc-passwd"
argument_list|,
literal|"geheim"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|RDBBlobStoreFixture
name|RDB_MYSQL
init|=
operator|new
name|MyFixture
argument_list|(
literal|"RDB-MySQL"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb-mysql-jdbc-url"
argument_list|,
literal|"jdbc:mysql://localhost:3306/oak"
argument_list|)
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb-mysql-jdbc-user"
argument_list|,
literal|"root"
argument_list|)
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb-mysql-jdbc-passwd"
argument_list|,
literal|"geheim"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|RDBBlobStoreFixture
name|RDB_ORACLE
init|=
operator|new
name|MyFixture
argument_list|(
literal|"RDB-Oracle"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb-oracle-jdbc-url"
argument_list|,
literal|"jdbc:oracle:thin:@localhost:1521:orcl"
argument_list|)
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb-oracle-jdbc-user"
argument_list|,
literal|"system"
argument_list|)
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb-oracle-jdbc-passwd"
argument_list|,
literal|"geheim"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|RDBBlobStoreFixture
name|RDB_MSSQL
init|=
operator|new
name|MyFixture
argument_list|(
literal|"RDB-MSSql"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb-mssql-jdbc-url"
argument_list|,
literal|"jdbc:sqlserver://localhost:1433;databaseName=OAK"
argument_list|)
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb-mssql-jdbc-user"
argument_list|,
literal|"sa"
argument_list|)
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb-mssql-jdbc-passwd"
argument_list|,
literal|"geheim"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|RDBBlobStoreFixture
name|RDB_H2
init|=
operator|new
name|MyFixture
argument_list|(
literal|"RDB-H2(file)"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb-h2-jdbc-url"
argument_list|,
literal|"jdbc:h2:file:./target/hs-bs-test"
argument_list|)
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb-h2-jdbc-user"
argument_list|,
literal|"sa"
argument_list|)
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb-h2-jdbc-passwd"
argument_list|,
literal|""
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|RDBBlobStoreFixture
name|RDB_DERBY
init|=
operator|new
name|MyFixture
argument_list|(
literal|"RDB-Derby(embedded)"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb-derby-jdbc-url"
argument_list|,
literal|"jdbc:derby:./target/derby-bs-test;create=true"
argument_list|)
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb-derby-jdbc-user"
argument_list|,
literal|"sa"
argument_list|)
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb-derby-jdbc-passwd"
argument_list|,
literal|""
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|RDBBlobStoreFixture
name|RDB_PG
init|=
operator|new
name|MyFixture
argument_list|(
literal|"RDB-Postgres"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb-postgres-jdbc-url"
argument_list|,
literal|"jdbc:postgresql:oak"
argument_list|)
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb-postgres-jdbc-user"
argument_list|,
literal|"postgres"
argument_list|)
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb-postgres-jdbc-passwd"
argument_list|,
literal|"geheim"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
class|class
name|MyFixture
extends|extends
name|RDBBlobStoreFixture
block|{
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|DataSource
name|dataSource
decl_stmt|;
specifier|private
name|RDBBlobStore
name|bs
decl_stmt|;
specifier|private
name|RDBOptions
name|options
init|=
operator|new
name|RDBOptions
argument_list|()
operator|.
name|tablePrefix
argument_list|(
name|TABLEPREFIX
argument_list|)
operator|.
name|dropTablesOnClose
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|public
name|MyFixture
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|url
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|passwd
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
try|try
block|{
name|dataSource
operator|=
name|RDBDataSourceFactory
operator|.
name|forJdbcUrl
argument_list|(
name|url
argument_list|,
name|username
argument_list|,
name|passwd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Database instance not available at "
operator|+
name|url
operator|+
literal|", skipping tests..."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|RDBBlobStore
name|createRDBBlobStore
parameter_list|()
block|{
name|bs
operator|=
operator|new
name|RDBBlobStore
argument_list|(
name|dataSource
argument_list|,
name|options
argument_list|)
expr_stmt|;
return|return
name|bs
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAvailable
parameter_list|()
block|{
return|return
name|dataSource
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|bs
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|bs
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|bs
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

