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
name|benchmark
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_comment
comment|/**  * Test case that writes blobs concurrently and concurrently reads the blobs back when available.  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrentFileWriteTest
extends|extends
name|AbstractTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|NT_FOLDER
init|=
literal|"nt:folder"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|NT_FILE
init|=
literal|"nt:file"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|NT_RESOURCE
init|=
literal|"nt:resource"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JCR_DATA
init|=
literal|"jcr:data"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JCR_CONTENT
init|=
literal|"jcr:content"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JCR_MIME_TYPE
init|=
literal|"jcr:mimeType"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JCR_LAST_MOD
init|=
literal|"jcr:lastModified"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|FILE_SIZE
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"fileSize"
argument_list|,
literal|1900
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|FILE_COUNT
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"fileCount"
argument_list|,
literal|10
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|WRITERS
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"fileWriters"
argument_list|,
literal|50
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|READERS
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"fileReaders"
argument_list|,
literal|50
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|ROOT_NODE_NAME
init|=
literal|"concurrentFileWriteTest"
operator|+
name|TEST_ID
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|beforeTest
parameter_list|()
block|{
name|session
operator|=
name|loginWriter
argument_list|()
expr_stmt|;
try|try
block|{
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|ROOT_NODE_NAME
argument_list|,
name|NT_FOLDER
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|runTest
parameter_list|()
throws|throws
name|Exception
block|{
comment|// randomize the root folder for this run of the test
name|String
name|runId
init|=
name|String
operator|.
name|valueOf
argument_list|(
operator|(
operator|new
name|Random
argument_list|()
operator|)
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
name|ROOT_NODE_NAME
argument_list|)
operator|.
name|addNode
argument_list|(
name|runId
argument_list|,
name|NT_FOLDER
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|CountDownLatch
name|writersStopLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|WRITERS
argument_list|)
decl_stmt|;
name|AtomicBoolean
name|stopReadersFlag
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
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
name|WRITERS
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Writer
argument_list|(
name|writersStopLatch
argument_list|,
name|i
argument_list|,
name|runId
argument_list|)
argument_list|,
literal|"ConcurrentFileWriteTest-Writer-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|READERS
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Reader
argument_list|(
name|i
argument_list|,
name|stopReadersFlag
argument_list|,
name|runId
argument_list|)
argument_list|,
literal|"ConcurrentFileWriteTest-Reader-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|writersStopLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|stopReadersFlag
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterTest
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|session
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|hasNode
argument_list|(
name|ROOT_NODE_NAME
argument_list|)
condition|)
block|{
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
name|ROOT_NODE_NAME
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
class|class
name|Reader
implements|implements
name|Runnable
block|{
specifier|private
name|int
name|id
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|AtomicBoolean
name|stopFlag
decl_stmt|;
specifier|private
name|String
name|runId
decl_stmt|;
specifier|public
name|Reader
parameter_list|(
name|int
name|id
parameter_list|,
name|AtomicBoolean
name|stopFlag
parameter_list|,
name|String
name|runId
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|session
operator|=
name|loginWriter
argument_list|()
expr_stmt|;
name|this
operator|.
name|stopFlag
operator|=
name|stopFlag
expr_stmt|;
name|this
operator|.
name|runId
operator|=
name|runId
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|readFile
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|readFile
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|FILE_COUNT
operator|&&
operator|!
name|stopFlag
operator|.
name|get
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|Node
name|fileRoot
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
name|ROOT_NODE_NAME
argument_list|)
decl_stmt|;
name|String
name|fileid
init|=
literal|"file"
operator|+
name|id
operator|+
literal|"-"
operator|+
name|i
decl_stmt|;
while|while
condition|(
operator|!
name|stopFlag
operator|.
name|get
argument_list|()
operator|&&
operator|!
name|fileRoot
operator|.
name|hasNode
argument_list|(
name|fileid
argument_list|)
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|stopFlag
operator|.
name|get
argument_list|()
condition|)
block|{
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
name|ROOT_NODE_NAME
argument_list|)
operator|.
name|getNode
argument_list|(
name|runId
argument_list|)
operator|.
name|getNode
argument_list|(
name|fileid
argument_list|)
operator|.
name|getNode
argument_list|(
name|JCR_CONTENT
argument_list|)
operator|.
name|getProperty
argument_list|(
name|JCR_DATA
argument_list|)
operator|.
name|getBinary
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
class|class
name|Writer
implements|implements
name|Runnable
block|{
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|CountDownLatch
name|latch
decl_stmt|;
specifier|private
name|int
name|id
decl_stmt|;
specifier|private
name|String
name|runId
decl_stmt|;
specifier|public
name|Writer
parameter_list|(
name|CountDownLatch
name|stopLatch
parameter_list|,
name|int
name|id
parameter_list|,
name|String
name|runId
parameter_list|)
throws|throws
name|Exception
block|{
name|latch
operator|=
name|stopLatch
expr_stmt|;
name|session
operator|=
name|loginWriter
argument_list|()
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|runId
operator|=
name|runId
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|createFile
argument_list|()
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|private
name|void
name|createFile
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|FILE_COUNT
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|session
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Node
name|file
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
name|ROOT_NODE_NAME
argument_list|)
operator|.
name|getNode
argument_list|(
name|runId
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"file"
operator|+
name|id
operator|+
literal|"-"
operator|+
name|i
argument_list|,
name|NT_FILE
argument_list|)
decl_stmt|;
name|Node
name|content
init|=
name|file
operator|.
name|addNode
argument_list|(
name|JCR_CONTENT
argument_list|,
name|NT_RESOURCE
argument_list|)
decl_stmt|;
name|content
operator|.
name|setProperty
argument_list|(
name|JCR_MIME_TYPE
argument_list|,
literal|"application/octet-stream"
argument_list|)
expr_stmt|;
name|content
operator|.
name|setProperty
argument_list|(
name|JCR_LAST_MOD
argument_list|,
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
name|content
operator|.
name|setProperty
argument_list|(
name|JCR_DATA
argument_list|,
operator|new
name|TestInputStream
argument_list|(
name|FILE_SIZE
operator|*
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

