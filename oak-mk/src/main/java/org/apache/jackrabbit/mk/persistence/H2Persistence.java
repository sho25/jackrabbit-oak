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
name|persistence
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

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
name|File
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
name|Statement
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
name|model
operator|.
name|ChildNodeEntriesMap
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
name|model
operator|.
name|Commit
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
name|model
operator|.
name|Id
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
name|model
operator|.
name|Node
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
name|model
operator|.
name|StoredCommit
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
name|model
operator|.
name|StoredNode
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
name|store
operator|.
name|BinaryBinding
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
name|store
operator|.
name|IdFactory
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
name|store
operator|.
name|NotFoundException
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
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|H2Persistence
implements|implements
name|Persistence
implements|,
name|Closeable
block|{
specifier|private
specifier|static
specifier|final
name|boolean
name|FAST
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"mk.fastDb"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|File
name|homeDir
decl_stmt|;
specifier|private
name|JdbcConnectionPool
name|cp
decl_stmt|;
comment|// TODO: make this configurable
specifier|private
name|IdFactory
name|idFactory
init|=
name|IdFactory
operator|.
name|getDigestFactory
argument_list|()
decl_stmt|;
specifier|public
name|H2Persistence
parameter_list|(
name|File
name|homeDir
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|homeDir
operator|=
name|homeDir
expr_stmt|;
block|}
comment|//---------------------------------------------------< Persistence>
specifier|public
name|void
name|initialize
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|dbDir
init|=
operator|new
name|File
argument_list|(
name|homeDir
argument_list|,
literal|"db"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dbDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|dbDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
name|Class
operator|.
name|forName
argument_list|(
literal|"org.h2.Driver"
argument_list|)
expr_stmt|;
name|String
name|url
init|=
literal|"jdbc:h2:"
operator|+
name|dbDir
operator|.
name|getCanonicalPath
argument_list|()
operator|+
literal|"/revs"
decl_stmt|;
if|if
condition|(
name|FAST
condition|)
block|{
name|url
operator|+=
literal|";log=0;undo_log=0"
expr_stmt|;
block|}
name|cp
operator|=
name|JdbcConnectionPool
operator|.
name|create
argument_list|(
name|url
argument_list|,
literal|"sa"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|cp
operator|.
name|setMaxConnections
argument_list|(
literal|40
argument_list|)
expr_stmt|;
name|Connection
name|con
init|=
name|cp
operator|.
name|getConnection
argument_list|()
decl_stmt|;
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
literal|"create table if not exists REVS(ID binary primary key, DATA binary)"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|execute
argument_list|(
literal|"create table if not exists HEAD(ID binary) as select null"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|execute
argument_list|(
literal|"create sequence if not exists DATASTORE_ID"
argument_list|)
expr_stmt|;
comment|/*             DbBlobStore store = new DbBlobStore();             store.setConnectionPool(cp);             blobStore = store; */
block|}
finally|finally
block|{
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|cp
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Id
name|readHead
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|con
init|=
name|cp
operator|.
name|getConnection
argument_list|()
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
literal|"select * from HEAD"
argument_list|)
decl_stmt|;
name|ResultSet
name|rs
init|=
name|stmt
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
name|byte
index|[]
name|rawId
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|rawId
operator|=
name|rs
operator|.
name|getBytes
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|rawId
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|Id
argument_list|(
name|rawId
argument_list|)
return|;
block|}
finally|finally
block|{
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|writeHead
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|Exception
block|{
name|Connection
name|con
init|=
name|cp
operator|.
name|getConnection
argument_list|()
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
literal|"update HEAD set ID=?"
argument_list|)
decl_stmt|;
name|stmt
operator|.
name|setBytes
argument_list|(
literal|1
argument_list|,
name|id
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|execute
argument_list|()
expr_stmt|;
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|readNode
parameter_list|(
name|StoredNode
name|node
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
name|Id
name|id
init|=
name|node
operator|.
name|getId
argument_list|()
decl_stmt|;
name|Connection
name|con
init|=
name|cp
operator|.
name|getConnection
argument_list|()
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
literal|"select DATA from REVS where ID = ?"
argument_list|)
decl_stmt|;
try|try
block|{
name|stmt
operator|.
name|setBytes
argument_list|(
literal|1
argument_list|,
name|id
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|ResultSet
name|rs
init|=
name|stmt
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|rs
operator|.
name|getBytes
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|node
operator|.
name|deserialize
argument_list|(
operator|new
name|BinaryBinding
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|stmt
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
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|Id
name|writeNode
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|node
operator|.
name|serialize
argument_list|(
operator|new
name|BinaryBinding
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|out
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|byte
index|[]
name|rawId
init|=
name|idFactory
operator|.
name|createContentId
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
comment|//String id = StringUtils.convertBytesToHex(rawId);
name|Connection
name|con
init|=
name|cp
operator|.
name|getConnection
argument_list|()
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
literal|"insert into REVS (ID, DATA) select ?, ? where not exists (select 1 from REVS where ID = ?)"
argument_list|)
decl_stmt|;
try|try
block|{
name|stmt
operator|.
name|setBytes
argument_list|(
literal|1
argument_list|,
name|rawId
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setBytes
argument_list|(
literal|2
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setBytes
argument_list|(
literal|3
argument_list|,
name|rawId
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|stmt
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
name|close
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|Id
argument_list|(
name|rawId
argument_list|)
return|;
block|}
specifier|public
name|StoredCommit
name|readCommit
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
name|Connection
name|con
init|=
name|cp
operator|.
name|getConnection
argument_list|()
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
literal|"select DATA from REVS where ID = ?"
argument_list|)
decl_stmt|;
try|try
block|{
name|stmt
operator|.
name|setBytes
argument_list|(
literal|1
argument_list|,
name|id
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|ResultSet
name|rs
init|=
name|stmt
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|rs
operator|.
name|getBytes
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|StoredCommit
operator|.
name|deserialize
argument_list|(
name|id
argument_list|,
operator|new
name|BinaryBinding
argument_list|(
name|in
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|stmt
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
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|writeCommit
parameter_list|(
name|Id
name|id
parameter_list|,
name|Commit
name|commit
parameter_list|)
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|commit
operator|.
name|serialize
argument_list|(
operator|new
name|BinaryBinding
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|out
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|Connection
name|con
init|=
name|cp
operator|.
name|getConnection
argument_list|()
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
literal|"insert into REVS (ID, DATA) select ?, ? where not exists (select 1 from REVS where ID = ?)"
argument_list|)
decl_stmt|;
try|try
block|{
name|stmt
operator|.
name|setBytes
argument_list|(
literal|1
argument_list|,
name|id
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setBytes
argument_list|(
literal|2
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setBytes
argument_list|(
literal|3
argument_list|,
name|id
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|stmt
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
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|ChildNodeEntriesMap
name|readCNEMap
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
name|Connection
name|con
init|=
name|cp
operator|.
name|getConnection
argument_list|()
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
literal|"select DATA from REVS where ID = ?"
argument_list|)
decl_stmt|;
try|try
block|{
name|stmt
operator|.
name|setBytes
argument_list|(
literal|1
argument_list|,
name|id
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|ResultSet
name|rs
init|=
name|stmt
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|rs
operator|.
name|getBytes
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|ChildNodeEntriesMap
operator|.
name|deserialize
argument_list|(
operator|new
name|BinaryBinding
argument_list|(
name|in
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|stmt
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
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|Id
name|writeCNEMap
parameter_list|(
name|ChildNodeEntriesMap
name|map
parameter_list|)
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|map
operator|.
name|serialize
argument_list|(
operator|new
name|BinaryBinding
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|out
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|byte
index|[]
name|rawId
init|=
name|idFactory
operator|.
name|createContentId
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|Connection
name|con
init|=
name|cp
operator|.
name|getConnection
argument_list|()
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
literal|"insert into REVS (ID, DATA) select ?, ? where not exists (select 1 from REVS where ID = ?)"
argument_list|)
decl_stmt|;
try|try
block|{
name|stmt
operator|.
name|setBytes
argument_list|(
literal|1
argument_list|,
name|rawId
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setBytes
argument_list|(
literal|2
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setBytes
argument_list|(
literal|3
argument_list|,
name|rawId
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|stmt
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
name|close
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|Id
argument_list|(
name|rawId
argument_list|)
return|;
block|}
block|}
end_class

end_unit

