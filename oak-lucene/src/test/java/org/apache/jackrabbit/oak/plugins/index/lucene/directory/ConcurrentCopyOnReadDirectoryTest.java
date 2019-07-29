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
name|index
operator|.
name|lucene
operator|.
name|directory
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Closer
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
name|InitialContentHelper
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
name|concurrent
operator|.
name|ExecutorCloser
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
name|index
operator|.
name|lucene
operator|.
name|IndexCopier
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
name|index
operator|.
name|lucene
operator|.
name|LuceneIndexDefinition
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
name|state
operator|.
name|NodeState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|*
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|MoreExecutors
operator|.
name|sameThreadExecutor
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
name|plugins
operator|.
name|index
operator|.
name|search
operator|.
name|FulltextIndexConstants
operator|.
name|INDEX_DATA_CHILD_NAME
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|ConcurrentCopyOnReadDirectoryTest
block|{
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|temporaryFolder
init|=
operator|new
name|TemporaryFolder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Closer
name|closer
init|=
name|Closer
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|private
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|private
name|Directory
name|remote
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|executorService
operator|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
operator|new
name|ExecutorCloser
argument_list|(
name|executorService
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// normal remote directory
name|remote
operator|=
operator|new
name|RAMDirectory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|RemoteIndexInput
argument_list|(
name|super
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|IndexOutput
name|output
init|=
name|remote
operator|.
name|createOutput
argument_list|(
literal|"file"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|output
operator|.
name|writeString
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|remoteInput
init|=
name|remote
operator|.
name|openInput
argument_list|(
literal|"file"
argument_list|,
name|IOContext
operator|.
name|READ
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|remoteInput
operator|.
name|length
argument_list|()
operator|>
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|closer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"OAK-8513"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|concurrentPrefetch
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create filtering remote that would block on opening an input
name|CountDownLatch
name|copyWaiter
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|CountDownLatch
name|copyBlocker
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Directory
name|blockingRemote
init|=
operator|new
name|BlockingInputDirectory
argument_list|(
name|copyWaiter
argument_list|,
name|copyBlocker
argument_list|,
name|remote
argument_list|)
decl_stmt|;
name|IndexCopier
name|copier
init|=
operator|new
name|IndexCopier
argument_list|(
name|sameThreadExecutor
argument_list|()
argument_list|,
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|NodeState
name|root
init|=
name|InitialContentHelper
operator|.
name|INITIAL_CONTENT
decl_stmt|;
specifier|final
name|LuceneIndexDefinition
name|defn
init|=
operator|new
name|LuceneIndexDefinition
argument_list|(
name|root
argument_list|,
name|root
argument_list|,
literal|"/foo"
argument_list|)
decl_stmt|;
comment|// create a CoR instance to start pre-fetching in a separate thread as we'd be blocking it
name|Future
argument_list|<
name|String
argument_list|>
name|concCoR
init|=
name|executorService
operator|.
name|submit
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|openCoR
argument_list|(
name|copier
argument_list|,
name|blockingRemote
argument_list|,
name|defn
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
return|return
name|getThrowableAsString
argument_list|(
name|t
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
try|try
block|{
comment|// wait for CoR to start fetching which we're blocking its completion via copyBlocker latch
name|copyWaiter
operator|.
name|await
argument_list|()
expr_stmt|;
comment|// get another directory instance with normal remote while the previous is blocked by us
name|Directory
name|dir
init|=
name|openCoR
argument_list|(
name|copier
argument_list|,
name|remote
argument_list|,
name|defn
argument_list|)
decl_stmt|;
name|IndexInput
name|input
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"file"
argument_list|,
name|IOContext
operator|.
name|READ
argument_list|)
decl_stmt|;
name|copyBlocker
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|String
name|futureException
init|=
name|concCoR
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
literal|"Concurrent CoR must not throw exception"
argument_list|,
name|futureException
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Must not be reading from remote"
argument_list|,
name|input
operator|instanceof
name|RemoteIndexInput
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|copyBlocker
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Directory
name|openCoR
parameter_list|(
name|IndexCopier
name|copier
parameter_list|,
name|Directory
name|remote
parameter_list|,
name|LuceneIndexDefinition
name|defn
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|copier
operator|.
name|wrapForRead
argument_list|(
literal|"/oak:index/foo"
argument_list|,
name|defn
argument_list|,
name|remote
argument_list|,
name|INDEX_DATA_CHILD_NAME
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|getThrowableAsString
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|t
operator|.
name|getMessage
argument_list|()
operator|+
literal|"\n"
argument_list|)
decl_stmt|;
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|sw
operator|.
name|getBuffer
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|static
class|class
name|BlockingInputDirectory
extends|extends
name|FilterDirectory
block|{
specifier|private
specifier|final
name|CountDownLatch
name|copyWaiter
decl_stmt|;
specifier|private
specifier|final
name|CountDownLatch
name|copyBlocker
decl_stmt|;
name|BlockingInputDirectory
parameter_list|(
name|CountDownLatch
name|copyWaiter
parameter_list|,
name|CountDownLatch
name|copyBlocker
parameter_list|,
name|Directory
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|copyWaiter
operator|=
name|copyWaiter
expr_stmt|;
name|this
operator|.
name|copyBlocker
operator|=
name|copyBlocker
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|input
decl_stmt|;
try|try
block|{
name|input
operator|=
name|super
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// signal that input has been opened
name|copyWaiter
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
comment|// wait while we're signalled that we can be done with opening input
name|boolean
name|wait
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|wait
condition|)
block|{
try|try
block|{
comment|// block until we are signalled to call super
name|copyBlocker
operator|.
name|await
argument_list|()
expr_stmt|;
name|wait
operator|=
literal|false
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
return|return
name|input
return|;
block|}
block|}
specifier|static
class|class
name|RemoteIndexInput
extends|extends
name|IndexInput
block|{
specifier|private
specifier|final
name|IndexInput
name|delegate
decl_stmt|;
name|RemoteIndexInput
parameter_list|(
name|IndexInput
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
literal|"Remote "
operator|+
name|delegate
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|length
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getFilePointer
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|readByte
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

