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
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
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

begin_class
specifier|public
class|class
name|UnmergedBranchTest
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
annotation|@
name|Test
specifier|public
name|void
name|purgeUnmergedBranch
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentStore
name|testStore
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|DocumentMK
name|mk1
init|=
name|create
argument_list|(
name|testStore
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|int
name|cId1
init|=
name|mk1
operator|.
name|getNodeStore
argument_list|()
operator|.
name|getClusterId
argument_list|()
decl_stmt|;
name|DocumentMK
name|mk2
init|=
name|create
argument_list|(
name|testStore
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|int
name|cId2
init|=
name|mk2
operator|.
name|getNodeStore
argument_list|()
operator|.
name|getClusterId
argument_list|()
decl_stmt|;
comment|//1. Create branch commits on both cluster nodes
name|String
name|rev1
init|=
name|mk1
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/child1\":{}"
argument_list|,
literal|null
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|String
name|branchRev1
init|=
name|mk1
operator|.
name|branch
argument_list|(
name|rev1
argument_list|)
decl_stmt|;
name|String
name|brev1
init|=
name|mk1
operator|.
name|commit
argument_list|(
literal|"/child1"
argument_list|,
literal|"^\"foo\":1"
argument_list|,
name|branchRev1
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|String
name|rev2
init|=
name|mk2
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/child2\":{}"
argument_list|,
literal|null
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|String
name|branchRev2
init|=
name|mk2
operator|.
name|branch
argument_list|(
name|rev2
argument_list|)
decl_stmt|;
name|String
name|brev2
init|=
name|mk2
operator|.
name|commit
argument_list|(
literal|"/child2"
argument_list|,
literal|"^\"foo\":1"
argument_list|,
name|branchRev2
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Revision
argument_list|,
name|RevisionVector
argument_list|>
name|revs1
init|=
name|getUncommittedRevisions
argument_list|(
name|mk1
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Revision
argument_list|,
name|RevisionVector
argument_list|>
name|revs2
init|=
name|getUncommittedRevisions
argument_list|(
name|mk2
argument_list|)
decl_stmt|;
comment|//2. Assert that branch rev are uncommited
name|assertTrue
argument_list|(
name|revs1
operator|.
name|containsKey
argument_list|(
name|RevisionVector
operator|.
name|fromString
argument_list|(
name|brev1
argument_list|)
operator|.
name|asTrunkRevision
argument_list|()
operator|.
name|getRevision
argument_list|(
name|cId1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|revs2
operator|.
name|containsKey
argument_list|(
name|RevisionVector
operator|.
name|fromString
argument_list|(
name|brev2
argument_list|)
operator|.
name|asTrunkRevision
argument_list|()
operator|.
name|getRevision
argument_list|(
name|cId2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//3. Restart cluster 1 so that purge happens but only for cluster 1
name|mk1
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|mk1
operator|=
name|create
argument_list|(
name|testStore
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|revs1
operator|=
name|getUncommittedRevisions
argument_list|(
name|mk1
argument_list|)
expr_stmt|;
name|revs2
operator|=
name|getUncommittedRevisions
argument_list|(
name|mk2
argument_list|)
expr_stmt|;
comment|//4. Assert that post restart unmerged branch rev for c1 are purged
name|assertFalse
argument_list|(
name|revs1
operator|.
name|containsKey
argument_list|(
name|RevisionVector
operator|.
name|fromString
argument_list|(
name|brev1
argument_list|)
operator|.
name|asTrunkRevision
argument_list|()
operator|.
name|getRevision
argument_list|(
name|cId1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|revs2
operator|.
name|containsKey
argument_list|(
name|RevisionVector
operator|.
name|fromString
argument_list|(
name|brev2
argument_list|)
operator|.
name|asTrunkRevision
argument_list|()
operator|.
name|getRevision
argument_list|(
name|cId2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SortedMap
argument_list|<
name|Revision
argument_list|,
name|RevisionVector
argument_list|>
name|getUncommittedRevisions
parameter_list|(
name|DocumentMK
name|mk
parameter_list|)
block|{
comment|// only look at revisions in this document.
comment|// uncommitted revisions are not split off
name|NodeDocument
name|doc
init|=
name|getRootDoc
argument_list|(
name|mk
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|valueMap
init|=
name|doc
operator|.
name|getLocalMap
argument_list|(
name|NodeDocument
operator|.
name|REVISIONS
argument_list|)
decl_stmt|;
name|SortedMap
argument_list|<
name|Revision
argument_list|,
name|RevisionVector
argument_list|>
name|revisions
init|=
operator|new
name|TreeMap
argument_list|<
name|Revision
argument_list|,
name|RevisionVector
argument_list|>
argument_list|(
name|StableRevisionComparator
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|commit
range|:
name|valueMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|Utils
operator|.
name|isCommitted
argument_list|(
name|commit
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
block|{
name|Revision
name|r
init|=
name|commit
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|getClusterId
argument_list|()
operator|==
name|mk
operator|.
name|getNodeStore
argument_list|()
operator|.
name|getClusterId
argument_list|()
condition|)
block|{
name|RevisionVector
name|b
init|=
name|RevisionVector
operator|.
name|fromString
argument_list|(
name|commit
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|revisions
operator|.
name|put
argument_list|(
name|r
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|revisions
return|;
block|}
specifier|private
name|NodeDocument
name|getRootDoc
parameter_list|(
name|DocumentMK
name|mk
parameter_list|)
block|{
return|return
name|mk
operator|.
name|getNodeStore
argument_list|()
operator|.
name|getDocumentStore
argument_list|()
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
return|;
block|}
specifier|private
name|DocumentMK
name|create
parameter_list|(
name|DocumentStore
name|ds
parameter_list|,
name|int
name|clusterId
parameter_list|)
block|{
return|return
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|ds
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
operator|.
name|open
argument_list|()
return|;
block|}
block|}
end_class

end_unit

