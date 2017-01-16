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
name|Map
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
name|Executors
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
name|RejectedExecutionException
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
name|ScheduledExecutorService
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
name|AtomicReference
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NoSuchWorkspaceException
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
name|Lists
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
name|api
operator|.
name|CommitFailedException
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
name|api
operator|.
name|ContentRepository
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
name|api
operator|.
name|ContentSession
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
name|api
operator|.
name|Root
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
name|api
operator|.
name|Tree
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
name|api
operator|.
name|Type
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
name|AsyncIndexUpdate
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
name|IndexEditorProvider
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
name|property
operator|.
name|PropertyIndexEditorProvider
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
name|reference
operator|.
name|ReferenceEditorProvider
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
name|MemoryNodeStore
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
name|nodetype
operator|.
name|write
operator|.
name|InitialContent
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
name|commit
operator|.
name|CommitContext
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
name|commit
operator|.
name|CommitHook
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
name|commit
operator|.
name|CommitInfo
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
name|lifecycle
operator|.
name|OakInitializer
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
name|security
operator|.
name|OpenSecurityProvider
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
name|NodeBuilder
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
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|whiteboard
operator|.
name|DefaultWhiteboard
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
name|whiteboard
operator|.
name|Registration
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
name|whiteboard
operator|.
name|Whiteboard
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
name|whiteboard
operator|.
name|WhiteboardIndexEditorProvider
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
name|whiteboard
operator|.
name|WhiteboardUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|empty
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|not
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
name|assertEquals
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
name|assertNotNull
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
name|assertNull
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
name|assertThat
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
name|assertTrue
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
name|fail
import|;
end_import

begin_comment
comment|/**  * OakTest... TODO  */
end_comment

begin_class
specifier|public
class|class
name|OakTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testWithDefaultWorkspaceName
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentRepository
name|repo
init|=
operator|new
name|Oak
argument_list|()
operator|.
name|with
argument_list|(
literal|"test"
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|createContentRepository
argument_list|()
decl_stmt|;
name|String
index|[]
name|valid
init|=
operator|new
name|String
index|[]
block|{
literal|null
block|,
literal|"test"
block|}
decl_stmt|;
for|for
control|(
name|String
name|wspName
range|:
name|valid
control|)
block|{
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cs
operator|=
name|repo
operator|.
name|login
argument_list|(
literal|null
argument_list|,
name|wspName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|cs
operator|.
name|getWorkspaceName
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|String
index|[]
name|invalid
init|=
operator|new
name|String
index|[]
block|{
literal|""
block|,
literal|"another"
block|,
name|Oak
operator|.
name|DEFAULT_WORKSPACE_NAME
block|}
decl_stmt|;
for|for
control|(
name|String
name|wspName
range|:
name|invalid
control|)
block|{
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cs
operator|=
name|repo
operator|.
name|login
argument_list|(
literal|null
argument_list|,
name|wspName
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"invalid workspace nam"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchWorkspaceException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testContentRepositoryReuse
parameter_list|()
throws|throws
name|Exception
block|{
name|Oak
name|oak
init|=
operator|new
name|Oak
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
decl_stmt|;
name|ContentRepository
name|r0
init|=
literal|null
decl_stmt|;
name|ContentRepository
name|r1
init|=
literal|null
decl_stmt|;
try|try
block|{
name|r0
operator|=
name|oak
operator|.
name|createContentRepository
argument_list|()
expr_stmt|;
name|r1
operator|=
name|oak
operator|.
name|createContentRepository
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|r0
argument_list|,
name|r1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|r0
operator|!=
literal|null
condition|)
block|{
operator|(
operator|(
name|Closeable
operator|)
name|r0
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|checkExecutorShutdown
parameter_list|()
throws|throws
name|Exception
block|{
name|Runnable
name|runnable
init|=
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{              }
block|}
decl_stmt|;
name|Oak
name|oak
init|=
operator|new
name|Oak
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
decl_stmt|;
name|ContentRepository
name|repo
init|=
name|oak
operator|.
name|createContentRepository
argument_list|()
decl_stmt|;
name|WhiteboardUtils
operator|.
name|scheduleWithFixedDelay
argument_list|(
name|oak
operator|.
name|getWhiteboard
argument_list|()
argument_list|,
name|runnable
argument_list|,
literal|1
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Closeable
operator|)
name|repo
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|WhiteboardUtils
operator|.
name|scheduleWithFixedDelay
argument_list|(
name|oak
operator|.
name|getWhiteboard
argument_list|()
argument_list|,
name|runnable
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Executor should have rejected the task"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RejectedExecutionException
name|ignore
parameter_list|)
block|{          }
comment|//Externally passed executor should not be shutdown upon repository close
name|ScheduledExecutorService
name|externalExecutor
init|=
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|()
decl_stmt|;
name|Oak
name|oak2
init|=
operator|new
name|Oak
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|externalExecutor
argument_list|)
decl_stmt|;
name|ContentRepository
name|repo2
init|=
name|oak2
operator|.
name|createContentRepository
argument_list|()
decl_stmt|;
name|WhiteboardUtils
operator|.
name|scheduleWithFixedDelay
argument_list|(
name|oak2
operator|.
name|getWhiteboard
argument_list|()
argument_list|,
name|runnable
argument_list|,
literal|1
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Closeable
operator|)
name|repo2
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|WhiteboardUtils
operator|.
name|scheduleWithFixedDelay
argument_list|(
name|oak2
operator|.
name|getWhiteboard
argument_list|()
argument_list|,
name|runnable
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|externalExecutor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|closeAsyncIndexers
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicReference
argument_list|<
name|AsyncIndexUpdate
argument_list|>
name|async
init|=
operator|new
name|AtomicReference
argument_list|<
name|AsyncIndexUpdate
argument_list|>
argument_list|()
decl_stmt|;
name|Whiteboard
name|wb
init|=
operator|new
name|DefaultWhiteboard
argument_list|()
block|{
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Registration
name|register
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|T
name|service
parameter_list|,
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|properties
parameter_list|)
block|{
if|if
condition|(
name|service
operator|instanceof
name|AsyncIndexUpdate
condition|)
block|{
name|async
operator|.
name|set
argument_list|(
operator|(
name|AsyncIndexUpdate
operator|)
name|service
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|register
argument_list|(
name|type
argument_list|,
name|service
argument_list|,
name|properties
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|Oak
name|oak
init|=
operator|new
name|Oak
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|wb
argument_list|)
operator|.
name|withAsyncIndexing
argument_list|(
literal|"foo-async"
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|ContentRepository
name|repo
init|=
name|oak
operator|.
name|createContentRepository
argument_list|()
decl_stmt|;
operator|(
operator|(
name|Closeable
operator|)
name|repo
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|async
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|async
operator|.
name|get
argument_list|()
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|WhiteboardUtils
operator|.
name|getService
argument_list|(
name|wb
argument_list|,
name|AsyncIndexUpdate
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|checkMissingStrategySetting
parameter_list|()
throws|throws
name|Exception
block|{
name|Whiteboard
name|wb
init|=
operator|new
name|DefaultWhiteboard
argument_list|()
decl_stmt|;
name|WhiteboardIndexEditorProvider
name|wbProvider
init|=
operator|new
name|WhiteboardIndexEditorProvider
argument_list|()
decl_stmt|;
name|wbProvider
operator|.
name|start
argument_list|(
name|wb
argument_list|)
expr_stmt|;
name|Registration
name|r1
init|=
name|wb
operator|.
name|register
argument_list|(
name|IndexEditorProvider
operator|.
name|class
argument_list|,
operator|new
name|PropertyIndexEditorProvider
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Registration
name|r2
init|=
name|wb
operator|.
name|register
argument_list|(
name|IndexEditorProvider
operator|.
name|class
argument_list|,
operator|new
name|ReferenceEditorProvider
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Oak
name|oak
init|=
operator|new
name|Oak
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|InitialContent
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|wb
argument_list|)
operator|.
name|with
argument_list|(
name|wbProvider
argument_list|)
operator|.
name|withFailOnMissingIndexProvider
argument_list|()
decl_stmt|;
name|ContentRepository
name|repo
init|=
name|oak
operator|.
name|createContentRepository
argument_list|()
decl_stmt|;
name|ContentSession
name|cs
init|=
name|repo
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Root
name|root
init|=
name|cs
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"u1"
argument_list|,
name|Type
operator|.
name|REFERENCE
argument_list|)
expr_stmt|;
name|r1
operator|.
name|unregister
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
operator|(
operator|(
name|Closeable
operator|)
name|repo
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|commitContextInCommitInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|CommitInfoCapturingStore
name|store
init|=
operator|new
name|CommitInfoCapturingStore
argument_list|()
decl_stmt|;
name|Oak
name|oak
init|=
operator|new
name|Oak
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|ContentRepository
name|repo
init|=
name|oak
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|createContentRepository
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|store
operator|.
name|infos
argument_list|,
name|is
argument_list|(
name|not
argument_list|(
name|empty
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|CommitInfo
name|ci
range|:
name|store
operator|.
name|infos
control|)
block|{
name|assertNotNull
argument_list|(
name|ci
operator|.
name|getInfo
argument_list|()
operator|.
name|get
argument_list|(
name|CommitContext
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
operator|(
operator|(
name|Closeable
operator|)
name|repo
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|CommitInfoCapturingStore
extends|extends
name|MemoryNodeStore
block|{
name|List
argument_list|<
name|CommitInfo
argument_list|>
name|infos
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
specifier|synchronized
name|NodeState
name|merge
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|,
annotation|@
name|Nonnull
name|CommitHook
name|commitHook
parameter_list|,
annotation|@
name|Nonnull
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|info
operator|.
name|getSessionId
argument_list|()
operator|.
name|equals
argument_list|(
name|OakInitializer
operator|.
name|SESSION_ID
argument_list|)
condition|)
block|{
name|this
operator|.
name|infos
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|commitHook
argument_list|,
name|info
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

