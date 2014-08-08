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
name|plugins
operator|.
name|document
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
name|util
operator|.
name|List
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
name|commons
operator|.
name|PathUtils
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
name|commit
operator|.
name|AnnotatingConflictHandler
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
name|commit
operator|.
name|ConflictHook
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
name|commit
operator|.
name|ConflictValidatorProvider
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
name|memory
operator|.
name|MemoryDocumentStore
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
name|util
operator|.
name|Utils
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
name|spi
operator|.
name|state
operator|.
name|NodeStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|Test
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
name|assertFalse
import|;
end_import

begin_class
specifier|public
class|class
name|NodeStoreDiffTest
block|{
specifier|private
name|NodeStore
name|ns
decl_stmt|;
specifier|private
specifier|final
name|TestDocumentStore
name|tds
init|=
operator|new
name|TestDocumentStore
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|ns
operator|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|tds
argument_list|)
operator|.
name|setUseSimpleRevision
argument_list|(
literal|true
argument_list|)
comment|//To simplify debugging
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|memoryCacheSize
argument_list|(
literal|0
argument_list|)
comment|//Keep the cache size zero such that nodeCache is not used
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"OAK-2020"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|diffWithConflict
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Last rev on /var would be 1-0-1
name|createNodes
argument_list|(
literal|"/var/a"
argument_list|,
literal|"/var/b/b1"
argument_list|)
expr_stmt|;
comment|//1. Dummy commits to bump the version no
name|createNodes
argument_list|(
literal|"/fake/b"
argument_list|)
expr_stmt|;
name|createNodes
argument_list|(
literal|"/fake/c"
argument_list|)
expr_stmt|;
comment|//Root rev = 3-0-1
comment|//Root rev = 3-0-1
comment|//2. Create a node under /var/a but hold on commit
name|NodeBuilder
name|b1
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|createNodes
argument_list|(
name|b1
argument_list|,
literal|"/var/a/a1"
argument_list|)
expr_stmt|;
comment|//3. Remove a node under /var/b and commit it
name|NodeBuilder
name|b2
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b2
operator|.
name|child
argument_list|(
literal|"var"
argument_list|)
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|child
argument_list|(
literal|"b1"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|merge
argument_list|(
name|b2
argument_list|)
expr_stmt|;
comment|//4. Now merge and commit the changes in b1 and include conflict hooks
comment|//For now exception would be thrown
name|ns
operator|.
name|merge
argument_list|(
name|b1
argument_list|,
operator|new
name|CompositeHook
argument_list|(
operator|new
name|ConflictHook
argument_list|(
operator|new
name|AnnotatingConflictHandler
argument_list|()
argument_list|)
argument_list|,
operator|new
name|EditorHook
argument_list|(
operator|new
name|ConflictValidatorProvider
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
comment|/**      * This testcase demonstrates that diff logic in merge part traverses node path      * which are not affected by the commit      * @throws Exception      */
annotation|@
name|Ignore
argument_list|(
literal|"OAK-2020"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testDiff
parameter_list|()
throws|throws
name|Exception
block|{
name|createNodes
argument_list|(
literal|"/oak:index/prop-a"
argument_list|,
literal|"/oak:index/prop-b"
argument_list|,
literal|"/etc/workflow"
argument_list|)
expr_stmt|;
comment|//1. Make some other changes so as to bump root rev=3
name|createNodes
argument_list|(
literal|"/fake/a"
argument_list|)
expr_stmt|;
name|createNodes
argument_list|(
literal|"/fake/b"
argument_list|)
expr_stmt|;
comment|//2 - Start change
name|NodeBuilder
name|b2
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|createNodes
argument_list|(
name|b2
argument_list|,
literal|"/etc/workflow/instance1"
argument_list|)
expr_stmt|;
name|tds
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|//3. Merge which does a rebase
name|ns
operator|.
name|merge
argument_list|(
name|b2
argument_list|,
operator|new
name|CommitHook
argument_list|()
block|{
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
name|NodeBuilder
name|rb
init|=
name|after
operator|.
name|builder
argument_list|()
decl_stmt|;
name|createNodes
argument_list|(
name|rb
argument_list|,
literal|"/oak:index/prop-a/a1"
argument_list|)
expr_stmt|;
comment|//2.1 Commit some change under prop-
comment|//This cause diff in lastRev of base node state in ModifiedNodeState for
comment|//oak:index due to which when the base state is compared in ModifiedNodeState
comment|//then it fetches the new DocumentNodeState whose lastRev is greater than this.base.lastRev
comment|//but less then lastRev of the of readRevision. Where readRevision is the rev of root node when
comment|//rebase was performed
comment|//This is not to be done in actual cases as CommitHooks are invoked in critical sections
comment|//and creating nodes from within CommitHooks would cause deadlock. This is done here to ensure
comment|//that changes are done when rebase has been performed and merge is about to happen
name|createNodes
argument_list|(
literal|"/oak:index/prop-b/b1"
argument_list|)
expr_stmt|;
comment|//For now we the cache is disabled (size 0) so this is not required
comment|//ns.nodeCache.invalidateAll();
return|return
name|rb
operator|.
name|getNodeState
argument_list|()
return|;
block|}
block|}
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
comment|//Assert that diff logic does not traverse to /oak:index/prop-b/b1 as
comment|//its not part of commit
name|assertFalse
argument_list|(
name|tds
operator|.
name|paths
operator|.
name|contains
argument_list|(
literal|"/oak:index/prop-b/b1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NodeState
name|merge
parameter_list|(
name|NodeBuilder
name|nb
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeState
name|result
init|=
name|ns
operator|.
name|merge
argument_list|(
name|nb
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|prRev
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|ops
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
name|void
name|ops
parameter_list|()
block|{
if|if
condition|(
name|ns
operator|instanceof
name|DocumentNodeStore
condition|)
block|{
name|DocumentNodeStore
name|dns
init|=
operator|(
operator|(
name|DocumentNodeStore
operator|)
name|ns
operator|)
decl_stmt|;
name|dns
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|//Background ops are disabled for simple revisions
name|dns
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|NodeState
name|createNodes
parameter_list|(
name|String
modifier|...
name|paths
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|nb
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|createNodes
argument_list|(
name|nb
argument_list|,
name|paths
argument_list|)
expr_stmt|;
name|NodeState
name|result
init|=
name|merge
argument_list|(
name|nb
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
specifier|private
specifier|static
name|void
name|createNodes
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
modifier|...
name|paths
parameter_list|)
block|{
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|NodeBuilder
name|cb
init|=
name|builder
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|cb
operator|=
name|cb
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|prRev
parameter_list|(
name|NodeState
name|ns
parameter_list|)
block|{
if|if
condition|(
name|ns
operator|instanceof
name|DocumentNodeState
condition|)
block|{
name|DocumentNodeState
name|dns
init|=
operator|(
operator|(
name|DocumentNodeState
operator|)
name|ns
operator|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Root at %s (%s) %n"
argument_list|,
name|dns
operator|.
name|getRevision
argument_list|()
argument_list|,
name|dns
operator|.
name|getLastRevision
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|TestDocumentStore
extends|extends
name|MemoryDocumentStore
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|find
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|NODES
condition|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|Utils
operator|.
name|getPathFromId
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|find
argument_list|(
name|collection
argument_list|,
name|key
argument_list|)
return|;
block|}
name|void
name|reset
parameter_list|()
block|{
name|paths
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

