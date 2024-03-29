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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterators
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
name|cache
operator|.
name|CacheStats
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
name|NodeStore
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
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Collection
operator|.
name|NODES
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
name|NO_BINARY
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
name|assertNotNull
import|;
end_import

begin_class
specifier|public
class|class
name|PreviousDocCacheTest
extends|extends
name|AbstractMongoConnectionTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|cacheTestPrevDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentNodeStore
name|ns
init|=
name|mk
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|DocumentStore
name|docStore
init|=
name|ns
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
specifier|final
name|int
name|SPLIT_THRESHOLD
init|=
literal|10
decl_stmt|;
name|NodeBuilder
name|b
decl_stmt|;
comment|//Set property 110 times. Split at each 10. This should lead to 11 leaf prev docs and 1 intermediate prev doc.
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<=
name|SPLIT_THRESHOLD
condition|;
name|j
operator|++
control|)
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
name|SPLIT_THRESHOLD
condition|;
name|i
operator|++
control|)
block|{
name|b
operator|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"node-"
operator|+
name|j
operator|+
literal|"-"
operator|+
name|i
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
name|splitDocs
argument_list|(
name|ns
argument_list|,
name|SPLIT_THRESHOLD
argument_list|)
expr_stmt|;
block|}
name|CacheStats
name|nodesCache
init|=
literal|null
decl_stmt|;
name|CacheStats
name|prevDocsCache
init|=
literal|null
decl_stmt|;
for|for
control|(
name|CacheStats
name|cacheStats
range|:
name|docStore
operator|.
name|getCacheStats
argument_list|()
control|)
block|{
if|if
condition|(
literal|"Document-Documents"
operator|.
name|equals
argument_list|(
name|cacheStats
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|nodesCache
operator|=
name|cacheStats
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"Document-PrevDocuments"
operator|.
name|equals
argument_list|(
name|cacheStats
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|prevDocsCache
operator|=
name|cacheStats
expr_stmt|;
block|}
block|}
name|assertNotNull
argument_list|(
literal|"Nodes cache must not be null"
argument_list|,
name|nodesCache
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Prev docs cache must not be null"
argument_list|,
name|prevDocsCache
argument_list|)
expr_stmt|;
name|validateFullyLoadedCache
argument_list|(
name|docStore
argument_list|,
name|SPLIT_THRESHOLD
argument_list|,
name|nodesCache
argument_list|,
name|prevDocsCache
argument_list|)
expr_stmt|;
name|docStore
operator|.
name|invalidateCache
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"No entries expected in nodes cache"
argument_list|,
literal|0
argument_list|,
name|nodesCache
operator|.
name|getElementCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"No entries expected in prev docs cache"
argument_list|,
literal|0
argument_list|,
name|prevDocsCache
operator|.
name|getElementCount
argument_list|()
argument_list|)
expr_stmt|;
name|NodeDocument
name|doc
init|=
name|docStore
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
literal|"0:/"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Only main doc entry expected in nodes cache"
argument_list|,
literal|1
argument_list|,
name|nodesCache
operator|.
name|getElementCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"No entries expected in prev docs cache"
argument_list|,
literal|0
argument_list|,
name|prevDocsCache
operator|.
name|getElementCount
argument_list|()
argument_list|)
expr_stmt|;
name|Iterators
operator|.
name|size
argument_list|(
name|doc
operator|.
name|getAllPreviousDocs
argument_list|()
argument_list|)
expr_stmt|;
name|validateFullyLoadedCache
argument_list|(
name|docStore
argument_list|,
name|SPLIT_THRESHOLD
argument_list|,
name|nodesCache
argument_list|,
name|prevDocsCache
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|validateFullyLoadedCache
parameter_list|(
name|DocumentStore
name|docStore
parameter_list|,
name|int
name|splitThreshold
parameter_list|,
name|CacheStats
name|nodesCache
parameter_list|,
name|CacheStats
name|prevDocsCache
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Nodes cache must have 2 elements - '/' and intermediate split doc"
argument_list|,
literal|2
argument_list|,
name|nodesCache
operator|.
name|getElementCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected number of leaf prev docs"
argument_list|,
name|splitThreshold
operator|+
literal|1
argument_list|,
name|prevDocsCache
operator|.
name|getElementCount
argument_list|()
argument_list|)
expr_stmt|;
name|resetStats
argument_list|(
name|nodesCache
argument_list|,
name|prevDocsCache
argument_list|)
expr_stmt|;
name|NodeDocument
name|doc
init|=
name|docStore
operator|.
name|getIfCached
argument_list|(
name|NODES
argument_list|,
literal|"0:/"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Root doc must be available in nodes cache"
argument_list|,
literal|1
argument_list|,
name|nodesCache
operator|.
name|getHitCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Prev docs must not be read"
argument_list|,
literal|0
argument_list|,
name|prevDocsCache
operator|.
name|getHitCount
argument_list|()
argument_list|)
expr_stmt|;
name|Iterators
operator|.
name|size
argument_list|(
name|doc
operator|.
name|getAllPreviousDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Nodes cache should not have a miss"
argument_list|,
literal|0
argument_list|,
name|nodesCache
operator|.
name|getMissCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Prev docs cache should not have a miss"
argument_list|,
literal|0
argument_list|,
name|prevDocsCache
operator|.
name|getMissCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|resetStats
parameter_list|(
name|CacheStats
modifier|...
name|cacheStatses
parameter_list|)
block|{
for|for
control|(
name|CacheStats
name|cacheStats
range|:
name|cacheStatses
control|)
block|{
name|cacheStats
operator|.
name|resetStats
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|splitDocs
parameter_list|(
name|DocumentNodeStore
name|ns
parameter_list|,
name|int
name|splitDocLimit
parameter_list|)
block|{
name|DocumentStore
name|store
init|=
name|ns
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
name|NodeDocument
name|doc
init|=
name|Utils
operator|.
name|getRootDocument
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|ops
init|=
name|SplitOperations
operator|.
name|forDocument
argument_list|(
name|doc
argument_list|,
name|ns
argument_list|,
name|ns
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
name|NO_BINARY
argument_list|,
name|splitDocLimit
operator|/
literal|2
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|ops
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|UpdateOp
name|op
range|:
name|ops
control|)
block|{
if|if
condition|(
operator|!
name|op
operator|.
name|isNew
argument_list|()
operator|||
operator|!
name|store
operator|.
name|create
argument_list|(
name|NODES
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|op
argument_list|)
argument_list|)
condition|)
block|{
name|store
operator|.
name|createOrUpdate
argument_list|(
name|NODES
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|merge
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|store
operator|.
name|merge
argument_list|(
name|builder
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
block|}
end_class

end_unit

