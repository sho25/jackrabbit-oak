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
name|rdb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|PreparedStatement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
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
name|java
operator|.
name|sql
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

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
name|commons
operator|.
name|StringUtils
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
name|blob
operator|.
name|CachingBlobStore
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
name|DocumentStoreException
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
name|memory
operator|.
name|AbstractBlob
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
name|spi
operator|.
name|blob
operator|.
name|AbstractBlobStore
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
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|AbstractIterator
import|;
end_import

begin_class
specifier|public
class|class
name|RDBBlobStore
extends|extends
name|CachingBlobStore
implements|implements
name|Closeable
block|{
comment|/**      * Creates a {@linkplain RDBBlobStore} instance using the provided      * {@link DataSource} using the given {@link RDBOptions}.      */
specifier|public
name|RDBBlobStore
parameter_list|(
name|DataSource
name|ds
parameter_list|,
name|RDBOptions
name|options
parameter_list|)
block|{
try|try
block|{
name|initialize
argument_list|(
name|ds
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
literal|"initializing RDB blob store"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**      * Creates a {@linkplain RDBBlobStore} instance using the provided      * {@link DataSource} using default {@link RDBOptions}.      */
specifier|public
name|RDBBlobStore
parameter_list|(
name|DataSource
name|ds
parameter_list|)
block|{
name|this
argument_list|(
name|ds
argument_list|,
operator|new
name|RDBOptions
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|tablesToBeDropped
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"attempting to drop: "
operator|+
name|this
operator|.
name|tablesToBeDropped
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|tname
range|:
name|this
operator|.
name|tablesToBeDropped
control|)
block|{
name|Connection
name|con
init|=
literal|null
decl_stmt|;
try|try
block|{
name|con
operator|=
name|this
operator|.
name|ch
operator|.
name|getRWConnection
argument_list|()
expr_stmt|;
try|try
block|{
name|Statement
name|stmt
init|=
name|con
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|stmt
operator|.
name|execute
argument_list|(
literal|"drop table "
operator|+
name|tname
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"attempting to drop: "
operator|+
name|tname
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"attempting to drop: "
operator|+
name|tname
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|con
operator|!=
literal|null
condition|)
block|{
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"on close "
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|this
operator|.
name|ch
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|finalize
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|ch
operator|!=
literal|null
operator|&&
name|this
operator|.
name|callStack
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"finalizing RDBDocumentStore that was not disposed"
argument_list|,
name|this
operator|.
name|callStack
argument_list|)
expr_stmt|;
block|}
block|}
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
name|RDBBlobStore
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
comment|// ID size we need to support; is 2 * (hex) size of digest length
specifier|private
specifier|static
specifier|final
name|int
name|IDSIZE
decl_stmt|;
static|static
block|{
try|try
block|{
name|MessageDigest
name|md
init|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
name|AbstractBlobStore
operator|.
name|HASH_ALGORITHM
argument_list|)
decl_stmt|;
name|IDSIZE
operator|=
name|md
operator|.
name|getDigestLength
argument_list|()
operator|*
literal|2
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"can't determine digest length for blob store"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Exception
name|callStack
decl_stmt|;
specifier|private
name|RDBConnectionHandler
name|ch
decl_stmt|;
comment|// from options
specifier|private
name|String
name|dataTable
decl_stmt|;
specifier|private
name|String
name|metaTable
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|tablesToBeDropped
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|void
name|initialize
parameter_list|(
name|DataSource
name|ds
parameter_list|,
name|RDBOptions
name|options
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|tablePrefix
init|=
name|options
operator|.
name|getTablePrefix
argument_list|()
decl_stmt|;
if|if
condition|(
name|tablePrefix
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
operator|!
name|tablePrefix
operator|.
name|endsWith
argument_list|(
literal|"_"
argument_list|)
condition|)
block|{
name|tablePrefix
operator|+=
literal|"_"
expr_stmt|;
block|}
name|this
operator|.
name|dataTable
operator|=
name|tablePrefix
operator|+
literal|"DATASTORE_DATA"
expr_stmt|;
name|this
operator|.
name|metaTable
operator|=
name|tablePrefix
operator|+
literal|"DATASTORE_META"
expr_stmt|;
name|this
operator|.
name|ch
operator|=
operator|new
name|RDBConnectionHandler
argument_list|(
name|ds
argument_list|)
expr_stmt|;
name|Connection
name|con
init|=
name|this
operator|.
name|ch
operator|.
name|getRWConnection
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|String
name|baseName
range|:
operator|new
name|String
index|[]
block|{
literal|"DATASTORE_META"
block|,
literal|"DATASTORE_DATA"
block|}
control|)
block|{
name|String
name|tableName
init|=
name|tablePrefix
operator|+
name|baseName
decl_stmt|;
try|try
block|{
name|PreparedStatement
name|stmt
init|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"select ID from "
operator|+
name|tableName
operator|+
literal|" where ID = ?"
argument_list|)
decl_stmt|;
name|stmt
operator|.
name|setString
argument_list|(
literal|1
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeQuery
argument_list|()
expr_stmt|;
name|con
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
comment|// table does not appear to exist
name|con
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|String
name|dbtype
init|=
name|con
operator|.
name|getMetaData
argument_list|()
operator|.
name|getDatabaseProductName
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Attempting to create table "
operator|+
name|tableName
operator|+
literal|" in "
operator|+
name|dbtype
argument_list|)
expr_stmt|;
name|Statement
name|stmt
init|=
name|con
operator|.
name|createStatement
argument_list|()
decl_stmt|;
if|if
condition|(
name|baseName
operator|.
name|equals
argument_list|(
literal|"DATASTORE_META"
argument_list|)
condition|)
block|{
name|String
name|ct
decl_stmt|;
if|if
condition|(
literal|"Oracle"
operator|.
name|equals
argument_list|(
name|dbtype
argument_list|)
condition|)
block|{
name|ct
operator|=
literal|"create table "
operator|+
name|tableName
operator|+
literal|" (ID varchar("
operator|+
name|IDSIZE
operator|+
literal|") not null primary key, LVL number, LASTMOD number)"
expr_stmt|;
block|}
else|else
block|{
name|ct
operator|=
literal|"create table "
operator|+
name|tableName
operator|+
literal|" (ID varchar("
operator|+
name|IDSIZE
operator|+
literal|") not null primary key, LVL int, LASTMOD bigint)"
expr_stmt|;
block|}
name|stmt
operator|.
name|execute
argument_list|(
name|ct
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|ct
decl_stmt|;
if|if
condition|(
literal|"PostgreSQL"
operator|.
name|equals
argument_list|(
name|dbtype
argument_list|)
condition|)
block|{
name|ct
operator|=
literal|"create table "
operator|+
name|tableName
operator|+
literal|" (ID varchar("
operator|+
name|IDSIZE
operator|+
literal|") not null primary key, DATA bytea)"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"DB2"
operator|.
name|equals
argument_list|(
name|dbtype
argument_list|)
operator|||
operator|(
name|dbtype
operator|!=
literal|null
operator|&&
name|dbtype
operator|.
name|startsWith
argument_list|(
literal|"DB2/"
argument_list|)
operator|)
condition|)
block|{
name|ct
operator|=
literal|"create table "
operator|+
name|tableName
operator|+
literal|" (ID varchar("
operator|+
name|IDSIZE
operator|+
literal|") not null primary key, DATA blob("
operator|+
name|MINBLOB
operator|+
literal|"))"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"MySQL"
operator|.
name|equals
argument_list|(
name|dbtype
argument_list|)
condition|)
block|{
name|ct
operator|=
literal|"create table "
operator|+
name|tableName
operator|+
literal|" (ID varchar("
operator|+
name|IDSIZE
operator|+
literal|") not null primary key, DATA mediumblob)"
expr_stmt|;
block|}
else|else
block|{
name|ct
operator|=
literal|"create table "
operator|+
name|tableName
operator|+
literal|" (ID varchar("
operator|+
name|IDSIZE
operator|+
literal|") not null primary key, DATA blob)"
expr_stmt|;
block|}
name|stmt
operator|.
name|execute
argument_list|(
name|ct
argument_list|)
expr_stmt|;
block|}
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|commit
argument_list|()
expr_stmt|;
if|if
condition|(
name|options
operator|.
name|isDropTablesOnClose
argument_list|()
condition|)
block|{
name|tablesToBeDropped
operator|.
name|add
argument_list|(
name|tableName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|this
operator|.
name|ch
operator|.
name|closeConnection
argument_list|(
name|con
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|callStack
operator|=
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|?
operator|new
name|Exception
argument_list|(
literal|"call stack of RDBBlobStore creation"
argument_list|)
else|:
literal|null
expr_stmt|;
block|}
specifier|private
name|long
name|minLastModified
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|storeBlock
parameter_list|(
name|byte
index|[]
name|digest
parameter_list|,
name|int
name|level
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|storeBlockInDatabase
argument_list|(
name|digest
argument_list|,
name|level
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|storeBlockInDatabase
parameter_list|(
name|byte
index|[]
name|digest
parameter_list|,
name|int
name|level
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|SQLException
block|{
name|String
name|id
init|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|digest
argument_list|)
decl_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|Connection
name|con
init|=
name|this
operator|.
name|ch
operator|.
name|getRWConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|PreparedStatement
name|prep
init|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"update "
operator|+
name|metaTable
operator|+
literal|" set lastMod = ? where id = ?"
argument_list|)
decl_stmt|;
name|int
name|count
decl_stmt|;
try|try
block|{
name|prep
operator|.
name|setLong
argument_list|(
literal|1
argument_list|,
name|now
argument_list|)
expr_stmt|;
name|prep
operator|.
name|setString
argument_list|(
literal|2
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|count
operator|=
name|prep
operator|.
name|executeUpdate
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"trying to update metadata"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"trying to update metadata"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
name|prep
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
try|try
block|{
name|prep
operator|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"insert into "
operator|+
name|dataTable
operator|+
literal|"(id, data) values(?, ?)"
argument_list|)
expr_stmt|;
try|try
block|{
name|prep
operator|.
name|setString
argument_list|(
literal|1
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|prep
operator|.
name|setBytes
argument_list|(
literal|2
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|prep
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|prep
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
comment|// TODO: this code used to ignore exceptions here, assuming that it might be a case where the blob is already in the database (maybe this requires inspecting the exception code)
name|String
name|message
init|=
literal|"insert document failed for id "
operator|+
name|id
operator|+
literal|" with length "
operator|+
name|data
operator|.
name|length
operator|+
literal|" (check max size of datastore_data.data)"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|message
argument_list|,
name|ex
argument_list|)
throw|;
block|}
try|try
block|{
name|prep
operator|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"insert into "
operator|+
name|metaTable
operator|+
literal|"(id, lvl, lastMod) values(?, ?, ?)"
argument_list|)
expr_stmt|;
try|try
block|{
name|prep
operator|.
name|setString
argument_list|(
literal|1
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|prep
operator|.
name|setInt
argument_list|(
literal|2
argument_list|,
name|level
argument_list|)
expr_stmt|;
name|prep
operator|.
name|setLong
argument_list|(
literal|3
argument_list|,
name|now
argument_list|)
expr_stmt|;
name|prep
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|prep
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
comment|// already exists - ok
block|}
block|}
block|}
finally|finally
block|{
name|con
operator|.
name|commit
argument_list|()
expr_stmt|;
name|this
operator|.
name|ch
operator|.
name|closeConnection
argument_list|(
name|con
argument_list|)
expr_stmt|;
block|}
block|}
comment|// needed in test
specifier|protected
name|byte
index|[]
name|readBlockFromBackend
parameter_list|(
name|byte
index|[]
name|digest
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|id
init|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|digest
argument_list|)
decl_stmt|;
name|Connection
name|con
init|=
name|this
operator|.
name|ch
operator|.
name|getROConnection
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
decl_stmt|;
try|try
block|{
name|PreparedStatement
name|prep
init|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"select data from "
operator|+
name|dataTable
operator|+
literal|" where id = ?"
argument_list|)
decl_stmt|;
try|try
block|{
name|prep
operator|.
name|setString
argument_list|(
literal|1
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|ResultSet
name|rs
init|=
name|prep
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Datastore block "
operator|+
name|id
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
name|data
operator|=
name|rs
operator|.
name|getBytes
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|prep
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|con
operator|.
name|commit
argument_list|()
expr_stmt|;
name|this
operator|.
name|ch
operator|.
name|closeConnection
argument_list|(
name|con
argument_list|)
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
annotation|@
name|Override
specifier|protected
name|byte
index|[]
name|readBlockFromBackend
parameter_list|(
name|BlockId
name|blockId
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|id
init|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|blockId
operator|.
name|getDigest
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|cache
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|==
literal|null
condition|)
block|{
name|Connection
name|con
init|=
name|this
operator|.
name|ch
operator|.
name|getROConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|PreparedStatement
name|prep
init|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"select data from "
operator|+
name|dataTable
operator|+
literal|" where id = ?"
argument_list|)
decl_stmt|;
try|try
block|{
name|prep
operator|.
name|setString
argument_list|(
literal|1
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|ResultSet
name|rs
init|=
name|prep
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Datastore block "
operator|+
name|id
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
name|data
operator|=
name|rs
operator|.
name|getBytes
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|prep
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|cache
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|con
operator|.
name|commit
argument_list|()
expr_stmt|;
name|this
operator|.
name|ch
operator|.
name|closeConnection
argument_list|(
name|con
argument_list|)
expr_stmt|;
block|}
block|}
comment|// System.out.println("    read block " + id + " blockLen: " +
comment|// data.length + " [0]: " + data[0]);
if|if
condition|(
name|blockId
operator|.
name|getPos
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|data
return|;
block|}
name|int
name|len
init|=
call|(
name|int
call|)
argument_list|(
name|data
operator|.
name|length
operator|-
name|blockId
operator|.
name|getPos
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|len
operator|<
literal|0
condition|)
block|{
return|return
operator|new
name|byte
index|[
literal|0
index|]
return|;
block|}
name|byte
index|[]
name|d2
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
operator|(
name|int
operator|)
name|blockId
operator|.
name|getPos
argument_list|()
argument_list|,
name|d2
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|d2
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startMark
parameter_list|()
throws|throws
name|IOException
block|{
name|minLastModified
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|markInUse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isMarkEnabled
parameter_list|()
block|{
return|return
name|minLastModified
operator|!=
literal|0
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|mark
parameter_list|(
name|BlockId
name|blockId
parameter_list|)
throws|throws
name|Exception
block|{
name|Connection
name|con
init|=
name|this
operator|.
name|ch
operator|.
name|getRWConnection
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|minLastModified
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|String
name|id
init|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|blockId
operator|.
name|getDigest
argument_list|()
argument_list|)
decl_stmt|;
name|PreparedStatement
name|prep
init|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"update "
operator|+
name|metaTable
operator|+
literal|" set lastMod = ? where id = ? and lastMod< ?"
argument_list|)
decl_stmt|;
name|prep
operator|.
name|setLong
argument_list|(
literal|1
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|prep
operator|.
name|setString
argument_list|(
literal|2
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|prep
operator|.
name|setLong
argument_list|(
literal|3
argument_list|,
name|minLastModified
argument_list|)
expr_stmt|;
name|prep
operator|.
name|executeUpdate
argument_list|()
expr_stmt|;
name|prep
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|con
operator|.
name|commit
argument_list|()
expr_stmt|;
name|this
operator|.
name|ch
operator|.
name|closeConnection
argument_list|(
name|con
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|sweep
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|sweepFromDatabase
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|int
name|sweepFromDatabase
parameter_list|()
throws|throws
name|SQLException
block|{
name|Connection
name|con
init|=
name|this
operator|.
name|ch
operator|.
name|getRWConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
name|PreparedStatement
name|prep
init|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"select id from "
operator|+
name|metaTable
operator|+
literal|" where lastMod< ?"
argument_list|)
decl_stmt|;
name|prep
operator|.
name|setLong
argument_list|(
literal|1
argument_list|,
name|minLastModified
argument_list|)
expr_stmt|;
name|ResultSet
name|rs
init|=
name|prep
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|ids
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|prep
operator|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"delete from "
operator|+
name|metaTable
operator|+
literal|" where id = ?"
argument_list|)
expr_stmt|;
name|PreparedStatement
name|prepData
init|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"delete from "
operator|+
name|dataTable
operator|+
literal|" where id = ?"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
name|prep
operator|.
name|setString
argument_list|(
literal|1
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|prep
operator|.
name|execute
argument_list|()
expr_stmt|;
name|prepData
operator|.
name|setString
argument_list|(
literal|1
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|prepData
operator|.
name|execute
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|prepData
operator|.
name|close
argument_list|()
expr_stmt|;
name|prep
operator|.
name|close
argument_list|()
expr_stmt|;
name|minLastModified
operator|=
literal|0
expr_stmt|;
return|return
name|count
return|;
block|}
finally|finally
block|{
name|con
operator|.
name|commit
argument_list|()
expr_stmt|;
name|this
operator|.
name|ch
operator|.
name|closeConnection
argument_list|(
name|con
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|deleteChunks
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|chunkIds
parameter_list|,
name|long
name|maxLastModifiedTime
parameter_list|)
throws|throws
name|Exception
block|{
comment|// sanity check
if|if
condition|(
name|chunkIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// sanity check, nothing to do
return|return
literal|true
return|;
block|}
name|Connection
name|con
init|=
name|this
operator|.
name|ch
operator|.
name|getRWConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|PreparedStatement
name|prepMeta
init|=
literal|null
decl_stmt|;
name|PreparedStatement
name|prepData
init|=
literal|null
decl_stmt|;
name|StringBuilder
name|inClause
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|batch
init|=
name|chunkIds
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|batch
condition|;
name|i
operator|++
control|)
block|{
name|inClause
operator|.
name|append
argument_list|(
literal|'?'
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
name|batch
operator|-
literal|1
condition|)
block|{
name|inClause
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|maxLastModifiedTime
operator|>
literal|0
condition|)
block|{
name|prepMeta
operator|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"delete from "
operator|+
name|metaTable
operator|+
literal|" where id in ("
operator|+
name|inClause
operator|.
name|toString
argument_list|()
operator|+
literal|") and lastMod<= ?"
argument_list|)
expr_stmt|;
name|prepMeta
operator|.
name|setLong
argument_list|(
name|batch
operator|+
literal|1
argument_list|,
name|maxLastModifiedTime
argument_list|)
expr_stmt|;
name|prepData
operator|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"delete from "
operator|+
name|dataTable
operator|+
literal|" where id in ("
operator|+
name|inClause
operator|.
name|toString
argument_list|()
operator|+
literal|") and not exists(select * from "
operator|+
name|metaTable
operator|+
literal|" m where id = m.id and m.lastMod<= ?)"
argument_list|)
expr_stmt|;
name|prepData
operator|.
name|setLong
argument_list|(
name|batch
operator|+
literal|1
argument_list|,
name|maxLastModifiedTime
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|prepMeta
operator|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"delete from "
operator|+
name|metaTable
operator|+
literal|" where id in ("
operator|+
name|inClause
operator|.
name|toString
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|prepData
operator|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"delete from "
operator|+
name|dataTable
operator|+
literal|" where id in ("
operator|+
name|inClause
operator|.
name|toString
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|batch
condition|;
name|idx
operator|++
control|)
block|{
name|prepMeta
operator|.
name|setString
argument_list|(
name|idx
operator|+
literal|1
argument_list|,
name|chunkIds
operator|.
name|get
argument_list|(
name|idx
argument_list|)
argument_list|)
expr_stmt|;
name|prepData
operator|.
name|setString
argument_list|(
name|idx
operator|+
literal|1
argument_list|,
name|chunkIds
operator|.
name|get
argument_list|(
name|idx
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|prepMeta
operator|.
name|execute
argument_list|()
expr_stmt|;
name|prepData
operator|.
name|execute
argument_list|()
expr_stmt|;
name|prepMeta
operator|.
name|close
argument_list|()
expr_stmt|;
name|prepData
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|con
operator|.
name|commit
argument_list|()
expr_stmt|;
name|this
operator|.
name|ch
operator|.
name|closeConnection
argument_list|(
name|con
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getAllChunkIds
parameter_list|(
name|long
name|maxLastModifiedTime
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|ChunkIdIterator
argument_list|(
name|this
operator|.
name|ch
argument_list|,
name|maxLastModifiedTime
argument_list|,
name|metaTable
argument_list|)
return|;
block|}
comment|/**      * Reads chunk IDs in batches.      */
specifier|private
specifier|static
class|class
name|ChunkIdIterator
extends|extends
name|AbstractIterator
argument_list|<
name|String
argument_list|>
block|{
specifier|private
name|long
name|maxLastModifiedTime
decl_stmt|;
specifier|private
name|RDBConnectionHandler
name|ch
decl_stmt|;
specifier|private
specifier|static
name|int
name|BATCHSIZE
init|=
literal|1024
operator|*
literal|64
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|results
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|String
name|lastId
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|metaTable
decl_stmt|;
specifier|public
name|ChunkIdIterator
parameter_list|(
name|RDBConnectionHandler
name|ch
parameter_list|,
name|long
name|maxLastModifiedTime
parameter_list|,
name|String
name|metaTable
parameter_list|)
block|{
name|this
operator|.
name|maxLastModifiedTime
operator|=
name|maxLastModifiedTime
expr_stmt|;
name|this
operator|.
name|ch
operator|=
name|ch
expr_stmt|;
name|this
operator|.
name|metaTable
operator|=
name|metaTable
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|computeNext
parameter_list|()
block|{
if|if
condition|(
operator|!
name|results
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|results
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
return|;
block|}
else|else
block|{
comment|// need to refill
if|if
condition|(
name|refill
argument_list|()
condition|)
block|{
return|return
name|computeNext
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|endOfData
argument_list|()
return|;
block|}
block|}
block|}
specifier|private
name|boolean
name|refill
parameter_list|()
block|{
name|StringBuffer
name|query
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"select id from "
operator|+
name|metaTable
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxLastModifiedTime
operator|>
literal|0
condition|)
block|{
name|query
operator|.
name|append
argument_list|(
literal|" where lastMod<= ?"
argument_list|)
expr_stmt|;
if|if
condition|(
name|lastId
operator|!=
literal|null
condition|)
block|{
name|query
operator|.
name|append
argument_list|(
literal|" and id> ?"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|lastId
operator|!=
literal|null
condition|)
block|{
name|query
operator|.
name|append
argument_list|(
literal|" where id> ?"
argument_list|)
expr_stmt|;
block|}
block|}
name|query
operator|.
name|append
argument_list|(
literal|" order by id"
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|connection
operator|=
name|this
operator|.
name|ch
operator|.
name|getROConnection
argument_list|()
expr_stmt|;
try|try
block|{
name|PreparedStatement
name|prep
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
name|query
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|idx
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|maxLastModifiedTime
operator|>
literal|0
condition|)
block|{
name|prep
operator|.
name|setLong
argument_list|(
name|idx
operator|++
argument_list|,
name|maxLastModifiedTime
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lastId
operator|!=
literal|null
condition|)
block|{
name|prep
operator|.
name|setString
argument_list|(
name|idx
operator|++
argument_list|,
name|lastId
argument_list|)
expr_stmt|;
block|}
name|prep
operator|.
name|setFetchSize
argument_list|(
name|BATCHSIZE
argument_list|)
expr_stmt|;
name|ResultSet
name|rs
init|=
name|prep
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|lastId
operator|=
name|rs
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
name|lastId
argument_list|)
expr_stmt|;
block|}
return|return
operator|!
name|results
operator|.
name|isEmpty
argument_list|()
return|;
block|}
finally|finally
block|{
name|connection
operator|.
name|commit
argument_list|()
expr_stmt|;
name|this
operator|.
name|ch
operator|.
name|closeConnection
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"error executing ID lookup"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|this
operator|.
name|ch
operator|.
name|rollbackConnection
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|this
operator|.
name|ch
operator|.
name|closeConnection
argument_list|(
name|connection
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

