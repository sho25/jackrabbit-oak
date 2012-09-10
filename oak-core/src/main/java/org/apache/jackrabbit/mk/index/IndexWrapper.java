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
name|mk
operator|.
name|index
package|;
end_package

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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|mk
operator|.
name|ExceptionFactory
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
name|json
operator|.
name|JsopReader
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
name|json
operator|.
name|JsopStream
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
name|wrapper
operator|.
name|MicroKernelWrapper
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
name|wrapper
operator|.
name|MicroKernelWrapperBase
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
name|index
operator|.
name|Indexer
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
name|PrefixIndex
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
name|PropertyIndex
import|;
end_import

begin_comment
comment|/**  * The index mechanism, as a wrapper.  *   * @deprecated - see OAK-298  */
end_comment

begin_class
specifier|public
class|class
name|IndexWrapper
extends|extends
name|MicroKernelWrapperBase
implements|implements
name|MicroKernel
block|{
specifier|private
specifier|final
name|MicroKernelWrapper
name|mk
decl_stmt|;
specifier|private
specifier|final
name|Indexer
name|indexer
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|branchRevisions
init|=
name|Collections
operator|.
name|synchronizedSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|public
name|IndexWrapper
parameter_list|(
name|MicroKernel
name|mk
parameter_list|)
block|{
name|this
operator|.
name|mk
operator|=
name|MicroKernelWrapperBase
operator|.
name|wrap
argument_list|(
name|mk
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexer
operator|=
operator|new
name|Indexer
argument_list|(
name|mk
argument_list|)
expr_stmt|;
name|indexer
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Indexer
name|getIndexer
parameter_list|()
block|{
return|return
name|indexer
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getHeadRevision
parameter_list|()
block|{
return|return
name|mk
operator|.
name|getHeadRevision
argument_list|()
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
block|{
return|return
name|mk
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
name|boolean
name|nodeExists
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|revisionId
parameter_list|)
block|{
name|String
name|indexRoot
init|=
name|indexer
operator|.
name|getIndexRootNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|startsWith
argument_list|(
name|indexRoot
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|mk
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
block|{
return|return
name|mk
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
block|{
return|return
name|mk
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
name|waitForCommit
parameter_list|(
name|String
name|oldHeadRevisionId
parameter_list|,
name|long
name|maxWaitMillis
parameter_list|)
throws|throws
name|MicroKernelException
throws|,
name|InterruptedException
block|{
return|return
name|mk
operator|.
name|waitForCommit
argument_list|(
name|oldHeadRevisionId
argument_list|,
name|maxWaitMillis
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
block|{
return|return
name|mk
operator|.
name|write
argument_list|(
name|in
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
block|{
name|String
name|branchRevision
init|=
name|mk
operator|.
name|branch
argument_list|(
name|trunkRevisionId
argument_list|)
decl_stmt|;
name|branchRevisions
operator|.
name|add
argument_list|(
name|branchRevision
argument_list|)
expr_stmt|;
return|return
name|branchRevision
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
block|{
name|String
name|headRevision
init|=
name|mk
operator|.
name|merge
argument_list|(
name|branchRevisionId
argument_list|,
name|message
argument_list|)
decl_stmt|;
name|branchRevisions
operator|.
name|remove
argument_list|(
name|branchRevisionId
argument_list|)
expr_stmt|;
name|indexer
operator|.
name|updateUntil
argument_list|(
name|headRevision
argument_list|)
expr_stmt|;
return|return
name|mk
operator|.
name|getHeadRevision
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|commitStream
parameter_list|(
name|String
name|rootPath
parameter_list|,
name|JsopReader
name|jsonDiff
parameter_list|,
name|String
name|revisionId
parameter_list|,
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
name|branchRevisions
operator|.
name|remove
argument_list|(
name|revisionId
argument_list|)
condition|)
block|{
comment|// TODO update the index in the branch as well, if required
name|String
name|rev
init|=
name|mk
operator|.
name|commitStream
argument_list|(
name|rootPath
argument_list|,
name|jsonDiff
argument_list|,
name|revisionId
argument_list|,
name|message
argument_list|)
decl_stmt|;
name|branchRevisions
operator|.
name|add
argument_list|(
name|rev
argument_list|)
expr_stmt|;
return|return
name|rev
return|;
block|}
name|String
name|rev
init|=
name|mk
operator|.
name|commitStream
argument_list|(
name|rootPath
argument_list|,
name|jsonDiff
argument_list|,
name|revisionId
argument_list|,
name|message
argument_list|)
decl_stmt|;
name|jsonDiff
operator|.
name|resetReader
argument_list|()
expr_stmt|;
name|indexer
operator|.
name|updateIndex
argument_list|(
name|rootPath
argument_list|,
name|jsonDiff
argument_list|,
name|rev
argument_list|)
expr_stmt|;
name|rev
operator|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
expr_stmt|;
name|rev
operator|=
name|indexer
operator|.
name|updateEnd
argument_list|(
name|rev
argument_list|)
expr_stmt|;
return|return
name|rev
return|;
block|}
annotation|@
name|Override
specifier|public
name|JsopReader
name|getNodesStream
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
name|count
parameter_list|,
name|String
name|filter
parameter_list|)
block|{
name|String
name|indexRoot
init|=
name|indexer
operator|.
name|getIndexRootNode
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|path
operator|.
name|startsWith
argument_list|(
name|indexRoot
argument_list|)
condition|)
block|{
return|return
name|mk
operator|.
name|getNodesStream
argument_list|(
name|path
argument_list|,
name|revisionId
argument_list|,
name|depth
argument_list|,
name|offset
argument_list|,
name|count
argument_list|,
name|filter
argument_list|)
return|;
block|}
name|String
name|index
init|=
name|PathUtils
operator|.
name|relativize
argument_list|(
name|indexRoot
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|int
name|idx
init|=
name|index
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
comment|// not a query (expected: /index/prefix:x?y) - treat as regular node lookup
return|return
name|mk
operator|.
name|getNodesStream
argument_list|(
name|path
argument_list|,
name|revisionId
argument_list|,
name|depth
argument_list|,
name|offset
argument_list|,
name|count
argument_list|,
name|filter
argument_list|)
return|;
block|}
name|String
name|data
init|=
name|index
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|1
argument_list|)
decl_stmt|;
name|index
operator|=
name|index
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
expr_stmt|;
name|JsopStream
name|s
init|=
operator|new
name|JsopStream
argument_list|()
decl_stmt|;
name|s
operator|.
name|array
argument_list|()
expr_stmt|;
if|if
condition|(
name|index
operator|.
name|startsWith
argument_list|(
name|Indexer
operator|.
name|TYPE_PREFIX
argument_list|)
condition|)
block|{
name|String
name|prefix
init|=
name|index
operator|.
name|substring
argument_list|(
name|Indexer
operator|.
name|TYPE_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|PrefixIndex
name|prefixIndex
init|=
name|indexer
operator|.
name|getPrefixIndex
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefixIndex
operator|==
literal|null
condition|)
block|{
throw|throw
name|ExceptionFactory
operator|.
name|get
argument_list|(
literal|"Unknown index: "
operator|+
name|index
argument_list|)
throw|;
block|}
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|prefixIndex
operator|.
name|getPaths
argument_list|(
name|data
argument_list|,
name|revisionId
argument_list|)
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|s
operator|.
name|value
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|index
operator|.
name|startsWith
argument_list|(
name|Indexer
operator|.
name|TYPE_PROPERTY
argument_list|)
condition|)
block|{
name|String
name|property
init|=
name|index
operator|.
name|substring
argument_list|(
name|Indexer
operator|.
name|TYPE_PROPERTY
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|unique
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|property
operator|.
name|endsWith
argument_list|(
literal|","
operator|+
name|Indexer
operator|.
name|UNIQUE
argument_list|)
condition|)
block|{
name|unique
operator|=
literal|true
expr_stmt|;
name|property
operator|=
name|property
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|property
operator|.
name|length
argument_list|()
operator|-
name|Indexer
operator|.
name|UNIQUE
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|PropertyIndex
name|propertyIndex
init|=
name|indexer
operator|.
name|getPropertyIndex
argument_list|(
name|property
argument_list|)
decl_stmt|;
if|if
condition|(
name|propertyIndex
operator|==
literal|null
condition|)
block|{
throw|throw
name|ExceptionFactory
operator|.
name|get
argument_list|(
literal|"Unknown index: "
operator|+
name|index
argument_list|)
throw|;
block|}
if|if
condition|(
name|unique
condition|)
block|{
name|String
name|value
init|=
name|propertyIndex
operator|.
name|getPath
argument_list|(
name|data
argument_list|,
name|revisionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|s
operator|.
name|value
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|propertyIndex
operator|.
name|getPaths
argument_list|(
name|data
argument_list|,
name|revisionId
argument_list|)
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|s
operator|.
name|value
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|s
operator|.
name|endArray
argument_list|()
expr_stmt|;
return|return
name|s
return|;
block|}
annotation|@
name|Override
specifier|public
name|JsopReader
name|diffStream
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
block|{
return|return
name|mk
operator|.
name|diffStream
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
name|JsopReader
name|getJournalStream
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
block|{
return|return
name|mk
operator|.
name|getJournalStream
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
name|JsopReader
name|getRevisionsStream
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
block|{
return|return
name|mk
operator|.
name|getRevisionsStream
argument_list|(
name|since
argument_list|,
name|maxEntries
argument_list|,
name|path
argument_list|)
return|;
block|}
specifier|public
name|void
name|dispose
parameter_list|()
block|{
comment|// do nothing
block|}
specifier|public
name|MicroKernel
name|getBaseKernel
parameter_list|()
block|{
return|return
name|mk
return|;
block|}
block|}
end_class

end_unit

