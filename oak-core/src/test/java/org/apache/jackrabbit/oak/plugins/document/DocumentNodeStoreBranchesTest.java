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
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|PropertyState
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
name|document
operator|.
name|DocumentMK
operator|.
name|Builder
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
name|commit
operator|.
name|CompositeHook
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
name|DefaultEditor
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
name|Editor
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
name|EditorHook
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
name|EditorProvider
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
name|EmptyHook
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
name|stats
operator|.
name|Clock
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
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
annotation|@
name|Ignore
argument_list|(
literal|"OAK-5557"
argument_list|)
specifier|public
class|class
name|DocumentNodeStoreBranchesTest
block|{
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DocumentNodeStoreBranchesTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|DocumentMKBuilderProvider
name|builderProvider
init|=
operator|new
name|DocumentMKBuilderProvider
argument_list|()
decl_stmt|;
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|Revision
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|disableJournalDiff
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|DocumentNodeStore
operator|.
name|SYS_PROP_DISABLE_JOURNAL
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|reset
parameter_list|()
block|{
name|Revision
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
name|ClusterNodeInfo
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
name|DocumentNodeStore
operator|.
name|SYS_PROP_DISABLE_JOURNAL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|commitHookChangesOnBranchWithInterference
parameter_list|()
throws|throws
name|Exception
block|{
name|Clock
name|c
init|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
decl_stmt|;
name|c
operator|.
name|waitUntil
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|Revision
operator|.
name|setClock
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|ClusterNodeInfo
operator|.
name|setClock
argument_list|(
name|c
argument_list|)
expr_stmt|;
comment|// enough nodes that diffManyChildren() is called
specifier|final
name|int
name|NUM_NODES
init|=
name|DocumentMK
operator|.
name|MANY_CHILDREN_THRESHOLD
operator|*
literal|2
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"create new dns"
argument_list|)
expr_stmt|;
name|Builder
name|nsBuilder
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|nsBuilder
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|clock
argument_list|(
name|c
argument_list|)
expr_stmt|;
specifier|final
name|DocumentNodeStore
name|ns
init|=
name|nsBuilder
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
comment|// 0) initialization
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"initialization"
argument_list|)
expr_stmt|;
name|NodeBuilder
name|initBuilder
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
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
name|NUM_NODES
condition|;
name|i
operator|++
control|)
block|{
name|initBuilder
operator|.
name|child
argument_list|(
literal|"child"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|ns
operator|.
name|merge
argument_list|(
name|initBuilder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
comment|// 1) do more than UPDATE_LIMIT changes
name|LOG
operator|.
name|info
argument_list|(
literal|"starting doing many changes to force a branch commit"
argument_list|)
expr_stmt|;
name|NodeBuilder
name|rootBuilder
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|int
name|totalUpdates
init|=
literal|5
operator|*
name|DocumentRootBuilder
operator|.
name|UPDATE_LIMIT
decl_stmt|;
name|int
name|updateShare
init|=
name|totalUpdates
operator|/
name|NUM_NODES
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
name|NUM_NODES
condition|;
name|i
operator|++
control|)
block|{
name|NodeBuilder
name|childBuilder
init|=
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"child"
operator|+
name|i
argument_list|)
decl_stmt|;
name|childBuilder
operator|.
name|child
argument_list|(
literal|"grandChild"
operator|+
name|i
argument_list|)
expr_stmt|;
name|childBuilder
operator|.
name|setProperty
argument_list|(
literal|"p1"
argument_list|,
literal|"originalValue"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|updateShare
condition|;
name|j
operator|++
control|)
block|{
name|childBuilder
operator|.
name|setProperty
argument_list|(
literal|"someProperty"
operator|+
name|j
argument_list|,
literal|"sameValue"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// 2) wait 6 sec
name|LOG
operator|.
name|info
argument_list|(
literal|"after purge was triggered above, 'waiting' 6sec"
argument_list|)
expr_stmt|;
name|c
operator|.
name|waitUntil
argument_list|(
name|c
operator|.
name|getTime
argument_list|()
operator|+
literal|6000
argument_list|)
expr_stmt|;
comment|// 3) now in another 'session', do another merge - to change the head
name|LOG
operator|.
name|info
argument_list|(
literal|"in another session, do some unrelated changes to change the head"
argument_list|)
expr_stmt|;
name|NodeBuilder
name|parallelBuilder
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|parallelBuilder
operator|.
name|child
argument_list|(
literal|"unrelated"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"anyProp"
argument_list|,
literal|"anywhere"
argument_list|)
expr_stmt|;
name|ns
operator|.
name|merge
argument_list|(
name|parallelBuilder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
comment|// 4) now merge the first session - this should now fail
name|LOG
operator|.
name|info
argument_list|(
literal|"now merge the original builder - this should cause not all children to be visited"
argument_list|)
expr_stmt|;
name|ns
operator|.
name|merge
argument_list|(
name|rootBuilder
argument_list|,
name|CompositeHook
operator|.
name|compose
argument_list|(
name|Arrays
operator|.
expr|<
name|CommitHook
operator|>
name|asList
argument_list|(
operator|new
name|TestHook
argument_list|(
literal|"p"
argument_list|)
argument_list|,
operator|new
name|TestHook
argument_list|(
literal|"q"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|DocumentNodeState
name|root
init|=
name|ns
operator|.
name|getRoot
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
name|NUM_NODES
condition|;
name|i
operator|++
control|)
block|{
name|NodeState
name|child
init|=
name|root
operator|.
name|getChildNode
argument_list|(
literal|"child"
operator|+
name|i
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|child
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|child
operator|.
name|getProperty
argument_list|(
literal|"p1"
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|TestEditor
extends|extends
name|DefaultEditor
block|{
specifier|private
specifier|final
name|NodeBuilder
name|builder
decl_stmt|;
specifier|private
specifier|final
name|String
name|prefix
decl_stmt|;
name|TestEditor
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
operator|new
name|TestEditor
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
argument_list|,
name|prefix
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
operator|new
name|TestEditor
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
argument_list|,
name|prefix
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|after
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
class|class
name|TestHook
extends|extends
name|EditorHook
block|{
name|TestHook
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|EditorProvider
argument_list|()
block|{
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|Editor
name|getRootEditor
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
operator|new
name|TestEditor
argument_list|(
name|builder
argument_list|,
name|prefix
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

