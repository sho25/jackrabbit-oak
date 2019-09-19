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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
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
name|UpdateOp
operator|.
name|Key
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
name|UpdateOp
operator|.
name|Operation
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
name|bundlor
operator|.
name|BundlingConfigInitializer
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
name|bundlor
operator|.
name|BundlingHandler
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
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_CONTENT
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
name|JcrConstants
operator|.
name|JCR_DATA
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
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
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
name|document
operator|.
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
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
name|document
operator|.
name|TestUtils
operator|.
name|merge
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
name|document
operator|.
name|UpdateOp
operator|.
name|Operation
operator|.
name|Type
operator|.
name|SET_MAP_ENTRY
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
name|document
operator|.
name|bundlor
operator|.
name|DocumentBundlor
operator|.
name|META_PROP_BUNDLED_CHILD
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
name|document
operator|.
name|bundlor
operator|.
name|DocumentBundlor
operator|.
name|META_PROP_BUNDLING_PATH
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
name|document
operator|.
name|bundlor
operator|.
name|DocumentBundlor
operator|.
name|META_PROP_PATTERN
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|hasEntry
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
name|hasKey
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
name|hasSize
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
name|assertThat
import|;
end_import

begin_class
specifier|public
class|class
name|CommitDiffTest
block|{
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
specifier|private
name|BundlingHandler
name|bundlingHandler
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|ns
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|builder
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
operator|new
name|InitialContent
argument_list|()
operator|.
name|initialize
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|BundlingConfigInitializer
operator|.
name|INSTANCE
operator|.
name|initialize
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|ns
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|bundlingHandler
operator|=
name|ns
operator|.
name|getBundlingConfigHandler
argument_list|()
operator|.
name|newBundlingHandler
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addBundlingRoot
parameter_list|()
block|{
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|(
name|ns
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|CommitDiff
name|diff
init|=
name|newCommitDiff
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|NodeBuilder
name|ntFile
init|=
name|newNtFile
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|ntFile
operator|.
name|child
argument_list|(
name|JCR_CONTENT
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|diff
operator|.
name|childNodeAdded
argument_list|(
literal|"file"
argument_list|,
name|ntFile
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|diff
operator|.
name|getNumChanges
argument_list|()
argument_list|)
expr_stmt|;
name|Commit
name|c
init|=
name|builder
operator|.
name|build
argument_list|(
name|Revision
operator|.
name|newRevision
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|Revision
name|r
init|=
name|c
operator|.
name|getRevision
argument_list|()
decl_stmt|;
name|UpdateOp
name|op
init|=
name|c
operator|.
name|getUpdateOperationForNode
argument_list|(
name|Path
operator|.
name|fromString
argument_list|(
literal|"/file"
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Key
argument_list|,
name|Operation
argument_list|>
name|changes
init|=
name|op
operator|.
name|getChanges
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|changes
operator|.
name|keySet
argument_list|()
argument_list|,
name|hasSize
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
literal|"_deleted"
argument_list|,
name|r
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
name|MODIFIED_IN_SECS
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|r
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
name|META_PROP_PATTERN
argument_list|,
name|r
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addBundledNodeWithRoot
parameter_list|()
block|{
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|(
name|ns
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|CommitDiff
name|diff
init|=
name|newCommitDiff
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|diff
operator|.
name|childNodeAdded
argument_list|(
literal|"file"
argument_list|,
name|newNtFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|diff
operator|.
name|getNumChanges
argument_list|()
argument_list|)
expr_stmt|;
name|Commit
name|c
init|=
name|builder
operator|.
name|build
argument_list|(
name|Revision
operator|.
name|newRevision
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|Revision
name|r
init|=
name|c
operator|.
name|getRevision
argument_list|()
decl_stmt|;
name|UpdateOp
name|op
init|=
name|c
operator|.
name|getUpdateOperationForNode
argument_list|(
name|Path
operator|.
name|fromString
argument_list|(
literal|"/file"
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Key
argument_list|,
name|Operation
argument_list|>
name|changes
init|=
name|op
operator|.
name|getChanges
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|changes
operator|.
name|keySet
argument_list|()
argument_list|,
name|hasSize
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
literal|"_deleted"
argument_list|,
name|r
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
name|MODIFIED_IN_SECS
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|r
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
name|META_PROP_PATTERN
argument_list|,
name|r
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasEntry
argument_list|(
operator|new
name|Key
argument_list|(
name|META_PROP_BUNDLED_CHILD
argument_list|,
name|r
argument_list|)
argument_list|,
operator|new
name|Operation
argument_list|(
name|SET_MAP_ENTRY
argument_list|,
literal|"true"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// changes for jcr:content child node
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
name|JCR_CONTENT
operator|+
literal|"/"
operator|+
name|JCR_PRIMARYTYPE
argument_list|,
name|r
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
name|JCR_CONTENT
operator|+
literal|"/"
operator|+
name|JCR_DATA
argument_list|,
name|r
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
name|JCR_CONTENT
operator|+
literal|"/"
operator|+
name|META_PROP_BUNDLING_PATH
argument_list|,
name|r
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"OAK-8629"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|removeBundledNodeWithRoot
parameter_list|()
throws|throws
name|Exception
block|{
name|addTestFile
argument_list|()
expr_stmt|;
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|(
name|ns
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|CommitDiff
name|diff
init|=
name|newCommitDiff
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|diff
operator|.
name|childNodeDeleted
argument_list|(
literal|"file"
argument_list|,
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"file"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|diff
operator|.
name|getNumChanges
argument_list|()
argument_list|)
expr_stmt|;
name|Commit
name|c
init|=
name|builder
operator|.
name|build
argument_list|(
name|Revision
operator|.
name|newRevision
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|Revision
name|r
init|=
name|c
operator|.
name|getRevision
argument_list|()
decl_stmt|;
name|UpdateOp
name|op
init|=
name|c
operator|.
name|getUpdateOperationForNode
argument_list|(
name|Path
operator|.
name|fromString
argument_list|(
literal|"/file"
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Key
argument_list|,
name|Operation
argument_list|>
name|changes
init|=
name|op
operator|.
name|getChanges
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|changes
operator|.
name|keySet
argument_list|()
argument_list|,
name|hasSize
argument_list|(
literal|9
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
literal|"_deleted"
argument_list|,
name|r
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
literal|"_deletedOnce"
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
name|MODIFIED_IN_SECS
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|r
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasEntry
argument_list|(
operator|new
name|Key
argument_list|(
name|META_PROP_PATTERN
argument_list|,
name|r
argument_list|)
argument_list|,
operator|new
name|Operation
argument_list|(
name|SET_MAP_ENTRY
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasEntry
argument_list|(
operator|new
name|Key
argument_list|(
name|META_PROP_BUNDLED_CHILD
argument_list|,
name|r
argument_list|)
argument_list|,
operator|new
name|Operation
argument_list|(
name|SET_MAP_ENTRY
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// changes for jcr:content child node
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
name|JCR_CONTENT
operator|+
literal|"/"
operator|+
name|JCR_PRIMARYTYPE
argument_list|,
name|r
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
name|JCR_CONTENT
operator|+
literal|"/"
operator|+
name|JCR_DATA
argument_list|,
name|r
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
name|JCR_CONTENT
operator|+
literal|"/"
operator|+
name|META_PROP_BUNDLING_PATH
argument_list|,
name|r
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"OAK-8629"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|removeBundledNode
parameter_list|()
throws|throws
name|Exception
block|{
name|addTestFile
argument_list|()
expr_stmt|;
name|NodeBuilder
name|after
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|after
operator|.
name|child
argument_list|(
literal|"file"
argument_list|)
operator|.
name|child
argument_list|(
name|JCR_CONTENT
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|(
name|ns
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|CommitDiff
name|diff
init|=
name|newCommitDiff
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|after
operator|.
name|getNodeState
argument_list|()
operator|.
name|compareAgainstBaseState
argument_list|(
name|ns
operator|.
name|getRoot
argument_list|()
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|diff
operator|.
name|getNumChanges
argument_list|()
argument_list|)
expr_stmt|;
name|Commit
name|c
init|=
name|builder
operator|.
name|build
argument_list|(
name|Revision
operator|.
name|newRevision
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|Revision
name|r
init|=
name|c
operator|.
name|getRevision
argument_list|()
decl_stmt|;
name|UpdateOp
name|op
init|=
name|c
operator|.
name|getUpdateOperationForNode
argument_list|(
name|Path
operator|.
name|fromString
argument_list|(
literal|"/file"
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Key
argument_list|,
name|Operation
argument_list|>
name|changes
init|=
name|op
operator|.
name|getChanges
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|changes
operator|.
name|keySet
argument_list|()
argument_list|,
name|hasSize
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
name|MODIFIED_IN_SECS
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// changes for jcr:content child node
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
name|JCR_CONTENT
operator|+
literal|"/"
operator|+
name|JCR_PRIMARYTYPE
argument_list|,
name|r
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
name|JCR_CONTENT
operator|+
literal|"/"
operator|+
name|JCR_DATA
argument_list|,
name|r
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
name|JCR_CONTENT
operator|+
literal|"/"
operator|+
name|META_PROP_BUNDLING_PATH
argument_list|,
name|r
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|changeBundledNode
parameter_list|()
throws|throws
name|Exception
block|{
name|addTestFile
argument_list|()
expr_stmt|;
name|NodeBuilder
name|after
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|after
operator|.
name|child
argument_list|(
literal|"file"
argument_list|)
operator|.
name|child
argument_list|(
name|JCR_CONTENT
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_DATA
argument_list|,
literal|"modified"
argument_list|)
expr_stmt|;
name|CommitBuilder
name|builder
init|=
operator|new
name|CommitBuilder
argument_list|(
name|ns
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|CommitDiff
name|diff
init|=
name|newCommitDiff
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|after
operator|.
name|getNodeState
argument_list|()
operator|.
name|compareAgainstBaseState
argument_list|(
name|ns
operator|.
name|getRoot
argument_list|()
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|diff
operator|.
name|getNumChanges
argument_list|()
argument_list|)
expr_stmt|;
name|Commit
name|c
init|=
name|builder
operator|.
name|build
argument_list|(
name|Revision
operator|.
name|newRevision
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|Revision
name|r
init|=
name|c
operator|.
name|getRevision
argument_list|()
decl_stmt|;
name|UpdateOp
name|op
init|=
name|c
operator|.
name|getUpdateOperationForNode
argument_list|(
name|Path
operator|.
name|fromString
argument_list|(
literal|"/file"
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Key
argument_list|,
name|Operation
argument_list|>
name|changes
init|=
name|op
operator|.
name|getChanges
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|changes
operator|.
name|keySet
argument_list|()
argument_list|,
name|hasSize
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
name|MODIFIED_IN_SECS
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// changes for jcr:content child node
name|assertThat
argument_list|(
name|changes
argument_list|,
name|hasKey
argument_list|(
operator|new
name|Key
argument_list|(
name|JCR_CONTENT
operator|+
literal|"/"
operator|+
name|JCR_DATA
argument_list|,
name|r
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addTestFile
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|builder
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"file"
argument_list|,
name|newNtFile
argument_list|()
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
specifier|private
name|CommitDiff
name|newCommitDiff
parameter_list|(
name|CommitBuilder
name|builder
parameter_list|)
block|{
return|return
operator|new
name|CommitDiff
argument_list|(
name|bundlingHandler
argument_list|,
name|builder
argument_list|,
name|ns
operator|.
name|getBlobSerializer
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|NodeState
name|newNtFile
parameter_list|()
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"nt:file"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|NodeBuilder
name|content
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"jcr:content"
argument_list|)
decl_stmt|;
name|content
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"nt:resource"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|content
operator|.
name|setProperty
argument_list|(
name|JCR_DATA
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|getNodeState
argument_list|()
return|;
block|}
block|}
end_class

end_unit
