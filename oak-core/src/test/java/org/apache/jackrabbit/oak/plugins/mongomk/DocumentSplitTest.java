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
name|mongomk
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|mongomk
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
name|junit
operator|.
name|Test
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
name|Sets
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

begin_comment
comment|/**  * Check correct splitting of documents (OAK-926).  */
end_comment

begin_class
specifier|public
class|class
name|DocumentSplitTest
extends|extends
name|BaseMongoMKTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|splitRevisions
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentStore
name|store
init|=
name|mk
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|revisions
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|NodeDocument
name|doc
init|=
name|store
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|revisions
operator|.
name|addAll
argument_list|(
name|doc
operator|.
name|getLocalRevisions
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|revisions
operator|.
name|add
argument_list|(
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"foo\":{}+\"bar\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// create nodes
while|while
condition|(
name|revisions
operator|.
name|size
argument_list|()
operator|<=
name|NodeDocument
operator|.
name|REVISIONS_SPLIT_OFF_SIZE
condition|)
block|{
name|revisions
operator|.
name|add
argument_list|(
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"foo/node-"
operator|+
name|revisions
operator|.
name|size
argument_list|()
operator|+
literal|"\":{}"
operator|+
literal|"+\"bar/node-"
operator|+
name|revisions
operator|.
name|size
argument_list|()
operator|+
literal|"\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|mk
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|String
name|head
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|doc
operator|=
name|store
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|revs
init|=
name|doc
operator|.
name|getLocalRevisions
argument_list|()
decl_stmt|;
comment|// one remaining in the local revisions map
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|revs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|r
range|:
name|revisions
control|)
block|{
name|Revision
name|rev
init|=
name|Revision
operator|.
name|fromString
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|containsRevision
argument_list|(
name|rev
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|isCommitted
argument_list|(
name|rev
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check if document is still there
name|assertNotNull
argument_list|(
name|doc
operator|.
name|getNodeAtRevision
argument_list|(
name|mk
argument_list|,
name|Revision
operator|.
name|fromString
argument_list|(
name|head
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"baz\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|mk
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|splitDeleted
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentStore
name|store
init|=
name|mk
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|revisions
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"foo\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|NodeDocument
name|doc
init|=
name|store
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/foo"
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|revisions
operator|.
name|addAll
argument_list|(
name|doc
operator|.
name|getLocalRevisions
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|create
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|revisions
operator|.
name|size
argument_list|()
operator|<=
name|NodeDocument
operator|.
name|REVISIONS_SPLIT_OFF_SIZE
condition|)
block|{
if|if
condition|(
name|create
condition|)
block|{
name|revisions
operator|.
name|add
argument_list|(
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"foo\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|revisions
operator|.
name|add
argument_list|(
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"-\"foo\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|create
operator|=
operator|!
name|create
expr_stmt|;
block|}
name|mk
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|String
name|head
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|doc
operator|=
name|store
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|deleted
init|=
name|doc
operator|.
name|getLocalDeleted
argument_list|()
decl_stmt|;
comment|// one remaining in the local deleted map
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|deleted
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|r
range|:
name|revisions
control|)
block|{
name|Revision
name|rev
init|=
name|Revision
operator|.
name|fromString
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|containsRevision
argument_list|(
name|rev
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|isCommitted
argument_list|(
name|rev
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Node
name|node
init|=
name|doc
operator|.
name|getNodeAtRevision
argument_list|(
name|mk
argument_list|,
name|Revision
operator|.
name|fromString
argument_list|(
name|head
argument_list|)
argument_list|)
decl_stmt|;
comment|// check status of node
if|if
condition|(
name|create
condition|)
block|{
name|assertNull
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNotNull
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|splitCommitRoot
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentStore
name|store
init|=
name|mk
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"foo\":{}+\"bar\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|NodeDocument
name|doc
init|=
name|store
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/foo"
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|commitRoots
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|commitRoots
operator|.
name|addAll
argument_list|(
name|doc
operator|.
name|getLocalCommitRoot
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
comment|// create nodes
while|while
condition|(
name|commitRoots
operator|.
name|size
argument_list|()
operator|<=
name|NodeDocument
operator|.
name|REVISIONS_SPLIT_OFF_SIZE
condition|)
block|{
name|commitRoots
operator|.
name|add
argument_list|(
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"foo/prop\":"
operator|+
name|commitRoots
operator|.
name|size
argument_list|()
operator|+
literal|"^\"bar/prop\":"
operator|+
name|commitRoots
operator|.
name|size
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|mk
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|doc
operator|=
name|store
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|commits
init|=
name|doc
operator|.
name|getLocalCommitRoot
argument_list|()
decl_stmt|;
comment|// one remaining in the local commit root map
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|commits
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|r
range|:
name|commitRoots
control|)
block|{
name|Revision
name|rev
init|=
name|Revision
operator|.
name|fromString
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|isCommitted
argument_list|(
name|rev
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

