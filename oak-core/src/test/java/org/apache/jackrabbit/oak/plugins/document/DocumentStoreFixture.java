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
name|com
operator|.
name|mongodb
operator|.
name|BasicDBObject
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
name|memory
operator|.
name|MemoryDocumentStore
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
name|mongo
operator|.
name|MongoDocumentStore
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
name|RDBDocumentStore
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
name|util
operator|.
name|MongoConnection
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

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DB
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|DocumentStoreFixture
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
name|DocumentStoreFixture
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DocumentStoreFixture
name|MEMORY
init|=
operator|new
name|MemoryFixture
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DocumentStoreFixture
name|RDB_H2
init|=
operator|new
name|RDBFixture
argument_list|(
literal|"RDB-H2(file)"
argument_list|,
literal|"jdbc:h2:file:./target/ds-test"
argument_list|,
literal|"sa"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DocumentStoreFixture
name|RDB_PG
init|=
operator|new
name|RDBFixture
argument_list|(
literal|"RDB-Postgres"
argument_list|,
literal|"jdbc:postgresql:oak"
argument_list|,
literal|"postgres"
argument_list|,
literal|"geheim"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DocumentStoreFixture
name|RDB_DB2
init|=
operator|new
name|RDBFixture
argument_list|(
literal|"RDB-DB2"
argument_list|,
literal|"jdbc:db2://localhost:50000/OAK"
argument_list|,
literal|"oak"
argument_list|,
literal|"geheim"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DocumentStoreFixture
name|RDB_MYSQL
init|=
operator|new
name|RDBFixture
argument_list|(
literal|"RDB-MySQL"
argument_list|,
literal|"jdbc:mysql://localhost:3306/oak"
argument_list|,
literal|"root"
argument_list|,
literal|"geheim"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DocumentStoreFixture
name|RDB_ORACLE
init|=
operator|new
name|RDBFixture
argument_list|(
literal|"RDB-Oracle"
argument_list|,
literal|"jdbc:oracle:thin:@localhost:1521:orcl"
argument_list|,
literal|"system"
argument_list|,
literal|"geheim"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DocumentStoreFixture
name|RDB_MSSQL
init|=
operator|new
name|RDBFixture
argument_list|(
literal|"RDB-MSSql"
argument_list|,
literal|"jdbc:sqlserver://10.40.202.13:1453;databaseName=oak"
argument_list|,
literal|"oak"
argument_list|,
literal|"geheimRoot1234"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|DocumentStoreFixture
name|MONGO
init|=
operator|new
name|MongoFixture
argument_list|(
literal|"mongodb://localhost:27017/oak"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TABLEPREFIX
init|=
literal|"dstest_"
decl_stmt|;
specifier|public
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|DocumentStore
name|createDocumentStore
parameter_list|(
name|int
name|clusterId
parameter_list|)
function_decl|;
specifier|public
name|DocumentStore
name|createDocumentStore
parameter_list|()
block|{
return|return
name|createDocumentStore
argument_list|(
literal|1
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isAvailable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|// get underlying datasource if RDB persistence
specifier|public
name|DataSource
name|getRDBDataSource
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|// return false if the multiple instances will not share the same persistence
specifier|public
name|boolean
name|hasSinglePersistence
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|dispose
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
specifier|static
class|class
name|MemoryFixture
extends|extends
name|DocumentStoreFixture
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"Memory"
return|;
block|}
annotation|@
name|Override
specifier|public
name|DocumentStore
name|createDocumentStore
parameter_list|(
name|int
name|clusterId
parameter_list|)
block|{
return|return
operator|new
name|MemoryDocumentStore
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasSinglePersistence
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|public
specifier|static
class|class
name|RDBFixture
extends|extends
name|DocumentStoreFixture
block|{
name|DataSource
name|dataSource
decl_stmt|;
name|DocumentStore
name|store1
decl_stmt|,
name|store2
decl_stmt|;
name|String
name|name
decl_stmt|;
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
name|RDBFixture
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
name|DocumentStore
name|createDocumentStore
parameter_list|(
name|int
name|clusterId
parameter_list|)
block|{
if|if
condition|(
name|clusterId
operator|==
literal|1
condition|)
block|{
name|store1
operator|=
operator|new
name|RDBDocumentStore
argument_list|(
name|dataSource
argument_list|,
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setClusterId
argument_list|(
literal|1
argument_list|)
argument_list|,
name|options
argument_list|)
expr_stmt|;
return|return
name|store1
return|;
block|}
elseif|else
if|if
condition|(
name|clusterId
operator|==
literal|2
condition|)
block|{
name|store2
operator|=
operator|new
name|RDBDocumentStore
argument_list|(
name|dataSource
argument_list|,
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setClusterId
argument_list|(
literal|2
argument_list|)
argument_list|,
name|options
argument_list|)
expr_stmt|;
return|return
name|store2
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"expect clusterId == 1 or == 2"
argument_list|)
throw|;
block|}
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
name|DataSource
name|getRDBDataSource
parameter_list|()
block|{
return|return
name|dataSource
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
name|store1
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|store1
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|this
operator|.
name|store1
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|store2
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|store2
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|this
operator|.
name|store2
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|static
class|class
name|MongoFixture
extends|extends
name|DocumentStoreFixture
block|{
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_URI
init|=
literal|"mongodb://localhost:27017/oak-test"
decl_stmt|;
specifier|private
name|String
name|uri
decl_stmt|;
specifier|public
name|MongoFixture
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_URI
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MongoFixture
parameter_list|(
name|String
name|dbUri
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|dbUri
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"MongoDB"
return|;
block|}
annotation|@
name|Override
specifier|public
name|DocumentStore
name|createDocumentStore
parameter_list|(
name|int
name|clusterId
parameter_list|)
block|{
try|try
block|{
name|MongoConnection
name|connection
init|=
operator|new
name|MongoConnection
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|DB
name|db
init|=
name|connection
operator|.
name|getDB
argument_list|()
decl_stmt|;
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|db
argument_list|)
expr_stmt|;
return|return
operator|new
name|MongoDocumentStore
argument_list|(
name|db
argument_list|,
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAvailable
parameter_list|()
block|{
try|try
block|{
name|MongoConnection
name|connection
init|=
operator|new
name|MongoConnection
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|connection
operator|.
name|getDB
argument_list|()
operator|.
name|command
argument_list|(
operator|new
name|BasicDBObject
argument_list|(
literal|"ping"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|()
block|{
try|try
block|{
name|MongoConnection
name|connection
init|=
operator|new
name|MongoConnection
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|connection
operator|.
name|getDB
argument_list|()
operator|.
name|dropDatabase
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{             }
block|}
block|}
block|}
end_class

end_unit

