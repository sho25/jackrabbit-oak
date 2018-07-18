begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|spi
operator|.
name|commit
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
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginException
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
name|ImmutableMap
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
name|Oak
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
name|OakBaseTest
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
name|fixture
operator|.
name|NodeStoreFixture
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
name|NodeState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|CommitFailedException
operator|.
name|MERGE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|StringContains
operator|.
name|containsString
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

begin_class
specifier|public
class|class
name|CommitContextTest
extends|extends
name|OakBaseTest
block|{
specifier|private
name|CommitInfoCapturingObserver
name|observer
init|=
operator|new
name|CommitInfoCapturingObserver
argument_list|()
decl_stmt|;
specifier|private
name|ContentSession
name|session
decl_stmt|;
specifier|private
name|ContentRepository
name|repository
decl_stmt|;
specifier|public
name|CommitContextTest
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|)
block|{
name|super
argument_list|(
name|fixture
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|closeRepository
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|repository
operator|instanceof
name|Closeable
condition|)
block|{
operator|(
operator|(
name|Closeable
operator|)
name|repository
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|basicSetup
parameter_list|()
throws|throws
name|Exception
block|{
name|repository
operator|=
operator|new
name|Oak
argument_list|(
name|store
argument_list|)
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
name|observer
argument_list|)
operator|.
name|createContentRepository
argument_list|()
expr_stmt|;
name|session
operator|=
name|newSession
argument_list|()
expr_stmt|;
name|Root
name|root
init|=
name|session
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|of
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|observer
operator|.
name|info
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|observer
operator|.
name|info
operator|.
name|getInfo
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|observer
operator|.
name|info
operator|.
name|getInfo
argument_list|()
operator|.
name|containsKey
argument_list|(
name|CommitContext
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|observer
operator|.
name|info
operator|.
name|getInfo
argument_list|()
operator|.
name|put
argument_list|(
literal|"x"
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Info map should be immutable"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{          }
block|}
annotation|@
name|Test
specifier|public
name|void
name|attributeAddedByCommitHook
parameter_list|()
throws|throws
name|Exception
block|{
name|repository
operator|=
operator|new
name|Oak
argument_list|(
name|store
argument_list|)
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
name|observer
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|CommitHook
argument_list|()
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|NodeState
name|processCommit
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|CommitContext
name|attrs
init|=
operator|(
name|CommitContext
operator|)
name|info
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
decl_stmt|;
name|assertNotNull
argument_list|(
name|attrs
argument_list|)
expr_stmt|;
name|attrs
operator|.
name|set
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
return|return
name|after
return|;
block|}
block|}
argument_list|)
operator|.
name|createContentRepository
argument_list|()
expr_stmt|;
name|session
operator|=
name|newSession
argument_list|()
expr_stmt|;
name|Root
name|root
init|=
name|session
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|observer
operator|.
name|info
argument_list|)
expr_stmt|;
name|CommitContext
name|attrs
init|=
operator|(
name|CommitContext
operator|)
name|observer
operator|.
name|info
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
decl_stmt|;
name|assertNotNull
argument_list|(
name|attrs
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|attributesBeingReset
parameter_list|()
throws|throws
name|Exception
block|{
comment|//This test can only work with DocumentNodeStore as only that
comment|//reattempt a failed merge. SegmentNodeStore would not do another
comment|//attempt
name|assumeDocumentStore
argument_list|()
expr_stmt|;
specifier|final
name|AtomicInteger
name|invokeCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|repository
operator|=
operator|new
name|Oak
argument_list|(
name|store
argument_list|)
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
name|observer
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|CommitHook
argument_list|()
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|NodeState
name|processCommit
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|CommitContext
name|attrs
init|=
operator|(
name|CommitContext
operator|)
name|info
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
decl_stmt|;
name|int
name|count
init|=
name|invokeCount
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
name|attrs
operator|.
name|set
argument_list|(
literal|"a"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|attrs
operator|.
name|set
argument_list|(
literal|"b"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|attrs
operator|.
name|set
argument_list|(
literal|"a"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
block|}
return|return
name|after
return|;
block|}
block|}
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|CommitHook
argument_list|()
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|NodeState
name|processCommit
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|invokeCount
operator|.
name|get
argument_list|()
operator|==
literal|1
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|MERGE
argument_list|,
literal|0
argument_list|,
literal|"attribute reset test"
argument_list|)
throw|;
block|}
return|return
name|after
return|;
block|}
block|}
argument_list|)
operator|.
name|createContentRepository
argument_list|()
expr_stmt|;
name|session
operator|=
name|newSession
argument_list|()
expr_stmt|;
name|Root
name|root
init|=
name|session
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|observer
operator|.
name|info
argument_list|)
expr_stmt|;
name|CommitContext
name|attrs
init|=
operator|(
name|CommitContext
operator|)
name|observer
operator|.
name|info
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
decl_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|attrs
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|attrs
operator|.
name|get
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assumeDocumentStore
parameter_list|()
block|{
name|Assume
operator|.
name|assumeThat
argument_list|(
name|fixture
operator|.
name|toString
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"DocumentNodeStore"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ContentSession
name|newSession
parameter_list|()
throws|throws
name|LoginException
throws|,
name|NoSuchWorkspaceException
block|{
return|return
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|CommitInfoCapturingObserver
implements|implements
name|Observer
block|{
name|CommitInfo
name|info
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|contentChanged
parameter_list|(
annotation|@
name|NotNull
name|NodeState
name|root
parameter_list|,
annotation|@
name|NotNull
name|CommitInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

