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
name|com
operator|.
name|sleepycat
operator|.
name|je
operator|.
name|Database
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|je
operator|.
name|DatabaseConfig
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|je
operator|.
name|DatabaseEntry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|je
operator|.
name|Durability
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|je
operator|.
name|Environment
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|je
operator|.
name|EnvironmentConfig
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|je
operator|.
name|EnvironmentMutableConfig
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|je
operator|.
name|LockMode
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sleepycat
operator|.
name|je
operator|.
name|OperationStatus
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|BDbPersistence
implements|implements
name|Persistence
implements|,
name|Closeable
block|{
specifier|private
specifier|final
specifier|static
name|byte
index|[]
name|HEAD_ID
init|=
operator|new
name|byte
index|[]
block|{
literal|0
block|}
decl_stmt|;
specifier|private
specifier|final
name|File
name|homeDir
decl_stmt|;
specifier|private
name|Environment
name|dbEnv
decl_stmt|;
specifier|private
name|Database
name|db
decl_stmt|;
specifier|private
name|Database
name|head
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
name|BDbPersistence
parameter_list|(
name|File
name|homeDir
parameter_list|)
block|{
name|this
operator|.
name|homeDir
operator|=
name|homeDir
expr_stmt|;
block|}
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
name|EnvironmentConfig
name|envConfig
init|=
operator|new
name|EnvironmentConfig
argument_list|()
decl_stmt|;
comment|//envConfig.setTransactional(true);
name|envConfig
operator|.
name|setAllowCreate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|dbEnv
operator|=
operator|new
name|Environment
argument_list|(
name|dbDir
argument_list|,
name|envConfig
argument_list|)
expr_stmt|;
name|EnvironmentMutableConfig
name|envMutableConfig
init|=
operator|new
name|EnvironmentMutableConfig
argument_list|()
decl_stmt|;
comment|//envMutableConfig.setDurability(Durability.COMMIT_SYNC);
comment|//envMutableConfig.setDurability(Durability.COMMIT_NO_SYNC);
name|envMutableConfig
operator|.
name|setDurability
argument_list|(
name|Durability
operator|.
name|COMMIT_WRITE_NO_SYNC
argument_list|)
expr_stmt|;
name|dbEnv
operator|.
name|setMutableConfig
argument_list|(
name|envMutableConfig
argument_list|)
expr_stmt|;
name|DatabaseConfig
name|dbConfig
init|=
operator|new
name|DatabaseConfig
argument_list|()
decl_stmt|;
name|dbConfig
operator|.
name|setAllowCreate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//dbConfig.setDeferredWrite(true);
name|db
operator|=
name|dbEnv
operator|.
name|openDatabase
argument_list|(
literal|null
argument_list|,
literal|"revs"
argument_list|,
name|dbConfig
argument_list|)
expr_stmt|;
name|head
operator|=
name|dbEnv
operator|.
name|openDatabase
argument_list|(
literal|null
argument_list|,
literal|"head"
argument_list|,
name|dbConfig
argument_list|)
expr_stmt|;
comment|// TODO FIXME workaround in case we're not closed properly
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignore
parameter_list|)
block|{}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|db
operator|.
name|getConfig
argument_list|()
operator|.
name|getDeferredWrite
argument_list|()
condition|)
block|{
name|db
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
name|head
operator|.
name|close
argument_list|()
expr_stmt|;
name|dbEnv
operator|.
name|close
argument_list|()
expr_stmt|;
name|db
operator|=
literal|null
expr_stmt|;
name|dbEnv
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|t
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|Id
name|readHead
parameter_list|()
throws|throws
name|Exception
block|{
name|DatabaseEntry
name|key
init|=
operator|new
name|DatabaseEntry
argument_list|(
name|HEAD_ID
argument_list|)
decl_stmt|;
name|DatabaseEntry
name|data
init|=
operator|new
name|DatabaseEntry
argument_list|()
decl_stmt|;
if|if
condition|(
name|head
operator|.
name|get
argument_list|(
literal|null
argument_list|,
name|key
argument_list|,
name|data
argument_list|,
name|LockMode
operator|.
name|DEFAULT
argument_list|)
operator|==
name|OperationStatus
operator|.
name|SUCCESS
condition|)
block|{
return|return
operator|new
name|Id
argument_list|(
name|data
operator|.
name|getData
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
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
name|DatabaseEntry
name|key
init|=
operator|new
name|DatabaseEntry
argument_list|(
name|HEAD_ID
argument_list|)
decl_stmt|;
name|DatabaseEntry
name|data
init|=
operator|new
name|DatabaseEntry
argument_list|(
name|id
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|head
operator|.
name|put
argument_list|(
literal|null
argument_list|,
name|key
argument_list|,
name|data
argument_list|)
expr_stmt|;
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
name|DatabaseEntry
name|key
init|=
operator|new
name|DatabaseEntry
argument_list|(
name|id
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|DatabaseEntry
name|data
init|=
operator|new
name|DatabaseEntry
argument_list|()
decl_stmt|;
if|if
condition|(
name|db
operator|.
name|get
argument_list|(
literal|null
argument_list|,
name|key
argument_list|,
name|data
argument_list|,
name|LockMode
operator|.
name|DEFAULT
argument_list|)
operator|==
name|OperationStatus
operator|.
name|SUCCESS
condition|)
block|{
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
operator|.
name|getData
argument_list|()
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
name|Id
name|id
init|=
operator|new
name|Id
argument_list|(
name|idFactory
operator|.
name|createContentId
argument_list|(
name|bytes
argument_list|)
argument_list|)
decl_stmt|;
name|persist
argument_list|(
name|id
operator|.
name|getBytes
argument_list|()
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
return|return
name|id
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
name|DatabaseEntry
name|key
init|=
operator|new
name|DatabaseEntry
argument_list|(
name|id
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|DatabaseEntry
name|data
init|=
operator|new
name|DatabaseEntry
argument_list|()
decl_stmt|;
if|if
condition|(
name|db
operator|.
name|get
argument_list|(
literal|null
argument_list|,
name|key
argument_list|,
name|data
argument_list|,
name|LockMode
operator|.
name|DEFAULT
argument_list|)
operator|==
name|OperationStatus
operator|.
name|SUCCESS
condition|)
block|{
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
operator|.
name|getData
argument_list|()
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
name|persist
argument_list|(
name|id
operator|.
name|getBytes
argument_list|()
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
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
name|DatabaseEntry
name|key
init|=
operator|new
name|DatabaseEntry
argument_list|(
name|id
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|DatabaseEntry
name|data
init|=
operator|new
name|DatabaseEntry
argument_list|()
decl_stmt|;
if|if
condition|(
name|db
operator|.
name|get
argument_list|(
literal|null
argument_list|,
name|key
argument_list|,
name|data
argument_list|,
name|LockMode
operator|.
name|DEFAULT
argument_list|)
operator|==
name|OperationStatus
operator|.
name|SUCCESS
condition|)
block|{
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
operator|.
name|getData
argument_list|()
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
name|Id
name|id
init|=
operator|new
name|Id
argument_list|(
name|idFactory
operator|.
name|createContentId
argument_list|(
name|bytes
argument_list|)
argument_list|)
decl_stmt|;
name|persist
argument_list|(
name|id
operator|.
name|getBytes
argument_list|()
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
comment|//-------------------------------------------------------< implementation>
specifier|protected
name|void
name|persist
parameter_list|(
name|byte
index|[]
name|rawId
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|)
throws|throws
name|Exception
block|{
name|DatabaseEntry
name|key
init|=
operator|new
name|DatabaseEntry
argument_list|(
name|rawId
argument_list|)
decl_stmt|;
name|DatabaseEntry
name|data
init|=
operator|new
name|DatabaseEntry
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|db
operator|.
name|put
argument_list|(
literal|null
argument_list|,
name|key
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

