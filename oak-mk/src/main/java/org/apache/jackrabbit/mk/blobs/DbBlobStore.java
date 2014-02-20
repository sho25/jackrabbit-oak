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
name|mk
operator|.
name|blobs
package|;
end_package

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
name|h2
operator|.
name|jdbcx
operator|.
name|JdbcConnectionPool
import|;
end_import

begin_comment
comment|/**  * A database blob store.  */
end_comment

begin_class
specifier|public
class|class
name|DbBlobStore
extends|extends
name|AbstractBlobStore
block|{
specifier|private
name|JdbcConnectionPool
name|cp
decl_stmt|;
specifier|private
name|long
name|minLastModified
decl_stmt|;
specifier|public
name|void
name|setConnectionPool
parameter_list|(
name|JdbcConnectionPool
name|cp
parameter_list|)
throws|throws
name|SQLException
block|{
name|this
operator|.
name|cp
operator|=
name|cp
expr_stmt|;
name|Connection
name|conn
init|=
name|cp
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|Statement
name|stat
init|=
name|conn
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|stat
operator|.
name|execute
argument_list|(
literal|"create table if not exists datastore_meta"
operator|+
literal|"(id varchar primary key, level int, lastMod bigint)"
argument_list|)
expr_stmt|;
name|stat
operator|.
name|execute
argument_list|(
literal|"create table if not exists datastore_data"
operator|+
literal|"(id varchar primary key, data binary)"
argument_list|)
expr_stmt|;
name|stat
operator|.
name|close
argument_list|()
expr_stmt|;
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
specifier|synchronized
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
name|storeBlockToDatabase
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
name|storeBlockToDatabase
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
name|Connection
name|conn
init|=
name|cp
operator|.
name|getConnection
argument_list|()
decl_stmt|;
try|try
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
name|conn
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
name|conn
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
name|conn
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
name|conn
operator|.
name|close
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
name|Connection
name|conn
init|=
name|cp
operator|.
name|getConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|PreparedStatement
name|prep
init|=
name|conn
operator|.
name|prepareStatement
argument_list|(
literal|"select data from datastore_data where id = ?"
argument_list|)
decl_stmt|;
try|try
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
name|digest
argument_list|)
decl_stmt|;
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
name|byte
index|[]
name|data
init|=
name|rs
operator|.
name|getBytes
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// System.out.println("    read block " + id + " blockLen: " + data.length + " [0]: " + data[0]);
if|if
condition|(
name|blockId
operator|.
name|pos
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
name|pos
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
name|pos
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
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
if|if
condition|(
name|minLastModified
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|Connection
name|conn
init|=
name|cp
operator|.
name|getConnection
argument_list|()
decl_stmt|;
try|try
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
name|digest
argument_list|)
decl_stmt|;
name|PreparedStatement
name|prep
init|=
name|conn
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
name|conn
operator|.
name|close
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
name|int
name|count
init|=
literal|0
decl_stmt|;
name|Connection
name|conn
init|=
name|cp
operator|.
name|getConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|PreparedStatement
name|prep
init|=
name|conn
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
name|conn
operator|.
name|prepareStatement
argument_list|(
literal|"delete from datastore_meta where id = ?"
argument_list|)
expr_stmt|;
name|PreparedStatement
name|prepData
init|=
name|conn
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
block|}
finally|finally
block|{
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|minLastModified
operator|=
literal|0
expr_stmt|;
return|return
name|count
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|deleteChunk
parameter_list|(
name|String
name|chunkId
parameter_list|)
throws|throws
name|Exception
block|{
name|Connection
name|conn
init|=
name|cp
operator|.
name|getConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|PreparedStatement
name|prep
init|=
name|conn
operator|.
name|prepareStatement
argument_list|(
literal|"delete from datastore_meta where id = ?"
argument_list|)
decl_stmt|;
empty_stmt|;
empty_stmt|;
empty_stmt|;
comment|// TODO  and lastMod<= ?
empty_stmt|;
empty_stmt|;
empty_stmt|;
name|PreparedStatement
name|prepData
init|=
name|conn
operator|.
name|prepareStatement
argument_list|(
literal|"delete from datastore_data where id = ?"
argument_list|)
decl_stmt|;
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
name|conn
operator|.
name|commit
argument_list|()
expr_stmt|;
name|conn
operator|.
name|close
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
specifier|final
name|Connection
name|conn
init|=
name|cp
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|PreparedStatement
name|prep
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|(
name|maxLastModifiedTime
operator|!=
literal|0
operator|)
operator|&&
operator|(
name|maxLastModifiedTime
operator|!=
operator|-
literal|1
operator|)
condition|)
block|{
name|prep
operator|=
name|conn
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
name|conn
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
annotation|@
name|Override
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
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|conn
operator|!=
literal|null
operator|)
operator|&&
operator|!
name|conn
operator|.
name|isClosed
argument_list|()
condition|)
block|{
name|conn
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
block|{
comment|// ignore
block|}
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

