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
name|DriverManager
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
name|Iterator
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|api
operator|.
name|MicroKernelException
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
name|commons
operator|.
name|StringUtils
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
class|class
name|RDBBlobStore
extends|extends
name|CachingBlobStore
implements|implements
name|Closeable
block|{
comment|/**      * Creates a {@linkplain RDBBlobStore} instance using an embedded H2      * database in in-memory mode.      */
specifier|public
name|RDBBlobStore
parameter_list|()
block|{
try|try
block|{
name|String
name|jdbcurl
init|=
literal|"jdbc:h2:mem:oaknodes"
decl_stmt|;
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|jdbcurl
argument_list|,
literal|"sa"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|initialize
argument_list|(
name|connection
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
name|MicroKernelException
argument_list|(
literal|"initializing RDB blob store"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**      * Creates a {@linkplain RDBBlobStore} instance using the provided JDBC      * connection information.      */
specifier|public
name|RDBBlobStore
parameter_list|(
name|String
name|jdbcurl
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
block|{
try|try
block|{
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|jdbcurl
argument_list|,
name|username
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|initialize
argument_list|(
name|connection
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
name|MicroKernelException
argument_list|(
literal|"initializing RDB blob store"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**      * Creates a {@linkplain RDBBlobStore} instance using the provided      * {@link DataSource}.      */
specifier|public
name|RDBBlobStore
parameter_list|(
name|DataSource
name|ds
parameter_list|)
block|{
try|try
block|{
name|initialize
argument_list|(
name|ds
operator|.
name|getConnection
argument_list|()
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
name|MicroKernelException
argument_list|(
literal|"initializing RDB blob store"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|this
operator|.
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|connection
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|MicroKernelException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
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
name|connection
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
specifier|private
name|Exception
name|callStack
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|private
name|void
name|initialize
parameter_list|(
name|Connection
name|con
parameter_list|)
throws|throws
name|Exception
block|{
name|con
operator|.
name|setAutoCommit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|createTables
argument_list|(
name|con
argument_list|,
literal|"binary"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
name|con
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"failed to create tables, retrying after getting DB metadata"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
comment|// try to query database meta info for binary type
name|String
name|btype
init|=
name|RDBMeta
operator|.
name|findBinaryType
argument_list|(
name|con
operator|.
name|getMetaData
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|btype
operator|==
literal|null
condition|)
block|{
name|String
name|message
init|=
literal|"Could not determine binary type for "
operator|+
name|RDBMeta
operator|.
name|getDataBaseName
argument_list|(
name|con
operator|.
name|getMetaData
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|Exception
argument_list|(
name|message
argument_list|)
throw|;
block|}
name|createTables
argument_list|(
name|con
argument_list|,
name|btype
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|connection
operator|=
name|con
expr_stmt|;
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
name|void
name|createTables
parameter_list|(
name|Connection
name|con
parameter_list|,
name|String
name|binaryType
parameter_list|)
throws|throws
name|SQLException
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
literal|"create table if not exists datastore_meta (id varchar primary key, level int, lastMod bigint)"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|execute
argument_list|(
literal|"create table if not exists datastore_data (id varchar primary key, data "
operator|+
name|binaryType
operator|+
literal|")"
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
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"update datastore_meta set lastMod = ? where id = ?"
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
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"insert into datastore_data(id, data) values(?, ?)"
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
name|e
parameter_list|)
block|{
comment|// already exists - ok
block|}
try|try
block|{
name|prep
operator|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"insert into datastore_meta(id, level, lastMod) values(?, ?, ?)"
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
name|connection
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
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
try|try
block|{
name|PreparedStatement
name|prep
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"select data from datastore_data where id = ?"
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
name|connection
operator|.
name|commit
argument_list|()
expr_stmt|;
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
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"update datastore_meta set lastMod = ? where id = ? and lastMod< ?"
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
name|connection
operator|.
name|commit
argument_list|()
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
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"select id from datastore_meta where lastMod< ?"
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
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"delete from datastore_meta where id = ?"
argument_list|)
expr_stmt|;
name|PreparedStatement
name|prepData
init|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"delete from datastore_data where id = ?"
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
name|connection
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|deleteChunk
parameter_list|(
name|String
name|chunkId
parameter_list|,
name|long
name|maxLastModifiedTime
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|PreparedStatement
name|prep
init|=
literal|null
decl_stmt|;
name|PreparedStatement
name|prepData
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|maxLastModifiedTime
operator|>
literal|0
condition|)
block|{
name|prep
operator|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"delete from datastore_meta where id = ? and lastMod<= ?"
argument_list|)
expr_stmt|;
name|prep
operator|.
name|setLong
argument_list|(
literal|2
argument_list|,
name|maxLastModifiedTime
argument_list|)
expr_stmt|;
name|prepData
operator|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"delete from datastore_data where id = ? and lastMod<= ?"
argument_list|)
expr_stmt|;
name|prepData
operator|.
name|setLong
argument_list|(
literal|2
argument_list|,
name|maxLastModifiedTime
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|prep
operator|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"delete from datastore_meta where id = ?"
argument_list|)
expr_stmt|;
name|prepData
operator|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"delete from datastore_data where id = ?"
argument_list|)
expr_stmt|;
block|}
name|prep
operator|.
name|setString
argument_list|(
literal|1
argument_list|,
name|chunkId
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
name|chunkId
argument_list|)
expr_stmt|;
name|prepData
operator|.
name|execute
argument_list|()
expr_stmt|;
name|prep
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
name|connection
operator|.
name|commit
argument_list|()
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
name|PreparedStatement
name|prep
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|maxLastModifiedTime
operator|>
literal|0
condition|)
block|{
name|prep
operator|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"select id from datastore_meta where lastMod<= ?"
argument_list|)
expr_stmt|;
name|prep
operator|.
name|setLong
argument_list|(
literal|1
argument_list|,
name|maxLastModifiedTime
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|prep
operator|=
name|connection
operator|.
name|prepareStatement
argument_list|(
literal|"select id from datastore_meta"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ResultSet
name|rs
init|=
name|prep
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
return|return
operator|new
name|AbstractIterator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|protected
name|String
name|computeNext
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
return|return
name|rs
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
return|;
block|}
else|else
block|{
name|rs
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
try|try
block|{
if|if
condition|(
operator|(
name|rs
operator|!=
literal|null
operator|)
operator|&&
operator|!
name|rs
operator|.
name|isClosed
argument_list|()
condition|)
block|{
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e2
parameter_list|)
block|{                     }
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|endOfData
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

