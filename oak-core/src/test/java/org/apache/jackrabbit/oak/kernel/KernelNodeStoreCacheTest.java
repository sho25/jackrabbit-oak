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
name|kernel
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
name|io
operator|.
name|InputStream
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|mk
operator|.
name|api
operator|.
name|MicroKernelException
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
name|mk
operator|.
name|core
operator|.
name|MicroKernelImpl
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
name|ChildNodeEntry
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
name|Test
import|;
end_import

begin_comment
comment|/**  * Tests if cache is used for repeated reads on unmodified subtree.  * See also OAK-591.  */
end_comment

begin_class
specifier|public
class|class
name|KernelNodeStoreCacheTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|PROP_FILTER
init|=
literal|"{\"properties\":[\"*\"]}"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PROP_FILTER_WITH_HASH
init|=
literal|"{\"properties\":[\"*\",\":hash\"]}"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PROP_FILTER_WITH_ID
init|=
literal|"{\"properties\":[\"*\",\":id\"]}"
decl_stmt|;
specifier|private
name|KernelNodeStore
name|store
decl_stmt|;
specifier|private
name|MicroKernelWrapper
name|wrapper
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|wrapper
operator|=
operator|new
name|MicroKernelWrapper
argument_list|(
operator|new
name|MicroKernelImpl
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|=
operator|new
name|KernelNodeStore
argument_list|(
name|wrapper
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|NodeBuilder
name|b
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
decl_stmt|;
name|b
operator|.
name|child
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
name|b
operator|.
name|child
argument_list|(
literal|"d"
argument_list|)
expr_stmt|;
name|b
operator|.
name|child
argument_list|(
literal|"e"
argument_list|)
expr_stmt|;
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
comment|/**      * Provide both :hash and :id      */
annotation|@
name|Test
specifier|public
name|void
name|withDefaultFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|uncachedReads
init|=
name|readTreeWithCleanedCache
argument_list|()
decl_stmt|;
name|modifyContent
argument_list|()
expr_stmt|;
name|int
name|cachedReads
init|=
name|readTreeWithCache
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"cachedReads: "
operator|+
name|cachedReads
operator|+
literal|" uncachedReads: "
operator|+
name|uncachedReads
argument_list|,
name|cachedReads
operator|<
name|uncachedReads
argument_list|)
expr_stmt|;
block|}
comment|/**      * Don't provide :hash nor :id. This will not reduce the number of      * MK.getNodes() after a commit.      */
annotation|@
name|Test
specifier|public
name|void
name|withSimpleFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|wrapper
operator|.
name|filter
operator|=
name|PROP_FILTER
expr_stmt|;
name|int
name|uncachedReads
init|=
name|readTreeWithCleanedCache
argument_list|()
decl_stmt|;
name|modifyContent
argument_list|()
expr_stmt|;
name|int
name|cachedReads
init|=
name|readTreeWithCache
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"cachedReads: "
operator|+
name|cachedReads
operator|+
literal|" uncachedReads: "
operator|+
name|uncachedReads
argument_list|,
name|cachedReads
argument_list|,
name|uncachedReads
argument_list|)
expr_stmt|;
block|}
comment|/**      * Only provide :hash in MK.getNodes()      */
annotation|@
name|Test
specifier|public
name|void
name|withHashFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|wrapper
operator|.
name|filter
operator|=
name|PROP_FILTER_WITH_HASH
expr_stmt|;
name|int
name|uncachedReads
init|=
name|readTreeWithCleanedCache
argument_list|()
decl_stmt|;
name|modifyContent
argument_list|()
expr_stmt|;
name|int
name|cachedReads
init|=
name|readTreeWithCache
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"cachedReads: "
operator|+
name|cachedReads
operator|+
literal|" uncachedReads: "
operator|+
name|uncachedReads
argument_list|,
name|cachedReads
operator|<
name|uncachedReads
argument_list|)
expr_stmt|;
block|}
comment|/**      * Only provide :id in MK.getNodes()      */
annotation|@
name|Test
specifier|public
name|void
name|withIdFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|wrapper
operator|.
name|filter
operator|=
name|PROP_FILTER_WITH_ID
expr_stmt|;
name|int
name|uncachedReads
init|=
name|readTreeWithCleanedCache
argument_list|()
decl_stmt|;
comment|// System.out.println("Uncached reads: " + uncachedReads);
name|modifyContent
argument_list|()
expr_stmt|;
name|int
name|cachedReads
init|=
name|readTreeWithCache
argument_list|()
decl_stmt|;
comment|// System.out.println("Cached reads: " + cachedReads);
name|assertTrue
argument_list|(
literal|"cachedReads: "
operator|+
name|cachedReads
operator|+
literal|" uncachedReads: "
operator|+
name|uncachedReads
argument_list|,
name|cachedReads
operator|<
name|uncachedReads
argument_list|)
expr_stmt|;
block|}
comment|//---------------------------< internal>-----------------------------------
specifier|private
name|int
name|readTreeWithCache
parameter_list|()
block|{
name|NodeState
name|root
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|int
name|cachedReads
init|=
name|wrapper
operator|.
name|numGetNodes
decl_stmt|;
name|readTree
argument_list|(
name|root
argument_list|)
expr_stmt|;
return|return
name|wrapper
operator|.
name|numGetNodes
operator|-
name|cachedReads
return|;
block|}
specifier|private
name|int
name|readTreeWithCleanedCache
parameter_list|()
block|{
comment|// start with virgin store / empty cache
name|store
operator|=
operator|new
name|KernelNodeStore
argument_list|(
name|wrapper
argument_list|)
expr_stmt|;
name|KernelNodeState
name|root
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|int
name|uncachedReads
init|=
name|wrapper
operator|.
name|numGetNodes
decl_stmt|;
name|readTree
argument_list|(
name|root
argument_list|)
expr_stmt|;
return|return
name|wrapper
operator|.
name|numGetNodes
operator|-
name|uncachedReads
return|;
block|}
specifier|private
name|void
name|modifyContent
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
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
specifier|private
name|void
name|readTree
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
for|for
control|(
name|ChildNodeEntry
name|cne
range|:
name|root
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|readTree
argument_list|(
name|cne
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|MicroKernelWrapper
implements|implements
name|MicroKernel
block|{
specifier|private
specifier|final
name|MicroKernel
name|kernel
decl_stmt|;
name|String
name|filter
init|=
literal|null
decl_stmt|;
name|int
name|numGetNodes
init|=
literal|0
decl_stmt|;
name|MicroKernelWrapper
parameter_list|(
name|MicroKernel
name|kernel
parameter_list|)
block|{
name|this
operator|.
name|kernel
operator|=
name|kernel
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getHeadRevision
parameter_list|()
throws|throws
name|MicroKernelException
block|{
return|return
name|kernel
operator|.
name|getHeadRevision
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|String
name|checkpoint
parameter_list|(
name|long
name|lifetime
parameter_list|)
throws|throws
name|MicroKernelException
block|{
return|return
name|kernel
operator|.
name|checkpoint
argument_list|(
name|lifetime
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRevisionHistory
parameter_list|(
name|long
name|since
parameter_list|,
name|int
name|maxEntries
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|MicroKernelException
block|{
return|return
name|kernel
operator|.
name|getRevisionHistory
argument_list|(
name|since
argument_list|,
name|maxEntries
argument_list|,
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|waitForCommit
parameter_list|(
name|String
name|oldHeadRevisionId
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|MicroKernelException
throws|,
name|InterruptedException
block|{
return|return
name|kernel
operator|.
name|waitForCommit
argument_list|(
name|oldHeadRevisionId
argument_list|,
name|timeout
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getJournal
parameter_list|(
name|String
name|fromRevisionId
parameter_list|,
name|String
name|toRevisionId
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|MicroKernelException
block|{
return|return
name|kernel
operator|.
name|getJournal
argument_list|(
name|fromRevisionId
argument_list|,
name|toRevisionId
argument_list|,
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|diff
parameter_list|(
name|String
name|fromRevisionId
parameter_list|,
name|String
name|toRevisionId
parameter_list|,
name|String
name|path
parameter_list|,
name|int
name|depth
parameter_list|)
throws|throws
name|MicroKernelException
block|{
return|return
name|kernel
operator|.
name|diff
argument_list|(
name|fromRevisionId
argument_list|,
name|toRevisionId
argument_list|,
name|path
argument_list|,
name|depth
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|nodeExists
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|revisionId
parameter_list|)
throws|throws
name|MicroKernelException
block|{
return|return
name|kernel
operator|.
name|nodeExists
argument_list|(
name|path
argument_list|,
name|revisionId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getChildNodeCount
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|revisionId
parameter_list|)
throws|throws
name|MicroKernelException
block|{
return|return
name|kernel
operator|.
name|getChildNodeCount
argument_list|(
name|path
argument_list|,
name|revisionId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNodes
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|revisionId
parameter_list|,
name|int
name|depth
parameter_list|,
name|long
name|offset
parameter_list|,
name|int
name|maxChildNodes
parameter_list|,
name|String
name|filter
parameter_list|)
throws|throws
name|MicroKernelException
block|{
name|numGetNodes
operator|++
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|filter
operator|!=
literal|null
condition|)
block|{
name|filter
operator|=
name|this
operator|.
name|filter
expr_stmt|;
block|}
return|return
name|kernel
operator|.
name|getNodes
argument_list|(
name|path
argument_list|,
name|revisionId
argument_list|,
name|depth
argument_list|,
name|offset
argument_list|,
name|maxChildNodes
argument_list|,
name|filter
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|commit
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|jsonDiff
parameter_list|,
name|String
name|revisionId
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|MicroKernelException
block|{
return|return
name|kernel
operator|.
name|commit
argument_list|(
name|path
argument_list|,
name|jsonDiff
argument_list|,
name|revisionId
argument_list|,
name|message
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|branch
parameter_list|(
name|String
name|trunkRevisionId
parameter_list|)
throws|throws
name|MicroKernelException
block|{
return|return
name|kernel
operator|.
name|branch
argument_list|(
name|trunkRevisionId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|merge
parameter_list|(
name|String
name|branchRevisionId
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|MicroKernelException
block|{
return|return
name|kernel
operator|.
name|merge
argument_list|(
name|branchRevisionId
argument_list|,
name|message
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|rebase
parameter_list|(
annotation|@
name|Nonnull
name|String
name|branchRevisionId
parameter_list|,
name|String
name|newBaseRevisionId
parameter_list|)
throws|throws
name|MicroKernelException
block|{
return|return
name|kernel
operator|.
name|rebase
argument_list|(
name|branchRevisionId
argument_list|,
name|newBaseRevisionId
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|reset
parameter_list|(
annotation|@
name|Nonnull
name|String
name|branchRevisionId
parameter_list|,
annotation|@
name|Nonnull
name|String
name|ancestorRevisionId
parameter_list|)
throws|throws
name|MicroKernelException
block|{
return|return
name|kernel
operator|.
name|reset
argument_list|(
name|branchRevisionId
argument_list|,
name|ancestorRevisionId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLength
parameter_list|(
name|String
name|blobId
parameter_list|)
throws|throws
name|MicroKernelException
block|{
return|return
name|kernel
operator|.
name|getLength
argument_list|(
name|blobId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|String
name|blobId
parameter_list|,
name|long
name|pos
parameter_list|,
name|byte
index|[]
name|buff
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|MicroKernelException
block|{
return|return
name|kernel
operator|.
name|read
argument_list|(
name|blobId
argument_list|,
name|pos
argument_list|,
name|buff
argument_list|,
name|off
argument_list|,
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|write
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|MicroKernelException
block|{
return|return
name|kernel
operator|.
name|write
argument_list|(
name|in
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

