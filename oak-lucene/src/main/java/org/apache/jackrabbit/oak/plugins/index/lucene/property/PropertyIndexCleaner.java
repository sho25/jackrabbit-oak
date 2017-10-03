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
name|index
operator|.
name|lucene
operator|.
name|property
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|HashMap
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
name|Set
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
name|TimeUnit
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
name|base
operator|.
name|Stopwatch
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
name|index
operator|.
name|AsyncIndexInfo
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
name|AsyncIndexInfoService
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
name|IndexPathService
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
name|IndexUtils
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

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|index
operator|.
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
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
name|index
operator|.
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|TYPE_LUCENE
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
name|index
operator|.
name|lucene
operator|.
name|property
operator|.
name|HybridPropertyIndexUtil
operator|.
name|PROPERTY_INDEX
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
name|index
operator|.
name|lucene
operator|.
name|property
operator|.
name|HybridPropertyIndexUtil
operator|.
name|PROP_STORAGE_TYPE
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
name|index
operator|.
name|lucene
operator|.
name|property
operator|.
name|HybridPropertyIndexUtil
operator|.
name|STORAGE_TYPE_CONTENT_MIRROR
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
name|index
operator|.
name|lucene
operator|.
name|property
operator|.
name|HybridPropertyIndexUtil
operator|.
name|STORAGE_TYPE_UNIQUE
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
name|spi
operator|.
name|state
operator|.
name|NodeStateUtils
operator|.
name|getNode
import|;
end_import

begin_class
specifier|public
class|class
name|PropertyIndexCleaner
block|{
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NodeStore
name|nodeStore
decl_stmt|;
specifier|private
specifier|final
name|IndexPathService
name|indexPathService
decl_stmt|;
specifier|private
specifier|final
name|AsyncIndexInfoService
name|asyncIndexInfoService
decl_stmt|;
specifier|private
name|UniqueIndexCleaner
name|uniqueIndexCleaner
init|=
operator|new
name|UniqueIndexCleaner
argument_list|(
name|TimeUnit
operator|.
name|HOURS
argument_list|,
literal|1
argument_list|)
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|lastAsyncInfo
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
specifier|public
name|PropertyIndexCleaner
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|IndexPathService
name|indexPathService
parameter_list|,
name|AsyncIndexInfoService
name|asyncIndexInfoService
parameter_list|)
block|{
name|this
operator|.
name|nodeStore
operator|=
name|nodeStore
expr_stmt|;
name|this
operator|.
name|indexPathService
operator|=
name|indexPathService
expr_stmt|;
name|this
operator|.
name|asyncIndexInfoService
operator|=
name|asyncIndexInfoService
expr_stmt|;
block|}
comment|/**      * Performs the cleanup run      *      * @return true if the cleanup was attempted      */
specifier|public
name|boolean
name|run
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|Stopwatch
name|w
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|asyncInfo
init|=
name|getAsyncInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastAsyncInfo
operator|.
name|equals
argument_list|(
name|asyncInfo
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"No change found in async state from last run {}. Skipping the run"
argument_list|,
name|asyncInfo
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|CleanupStats
name|stats
init|=
operator|new
name|CleanupStats
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|syncIndexes
init|=
name|getSyncIndexPaths
argument_list|()
decl_stmt|;
name|IndexInfo
name|indexInfo
init|=
name|switchBucketsAndCollectIndexData
argument_list|(
name|syncIndexes
argument_list|,
name|asyncInfo
argument_list|,
name|stats
argument_list|)
decl_stmt|;
name|purgeOldBuckets
argument_list|(
name|indexInfo
operator|.
name|oldBucketPaths
argument_list|,
name|stats
argument_list|)
expr_stmt|;
name|purgeOldUniqueIndexEntries
argument_list|(
name|indexInfo
operator|.
name|uniqueIndexPaths
argument_list|,
name|stats
argument_list|)
expr_stmt|;
name|lastAsyncInfo
operator|=
name|asyncInfo
expr_stmt|;
if|if
condition|(
name|w
operator|.
name|elapsed
argument_list|(
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
operator|>
literal|5
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Property index cleanup done in {}. {}"
argument_list|,
name|w
argument_list|,
name|stats
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Property index cleanup done in {}. {}"
argument_list|,
name|w
argument_list|,
name|stats
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Specifies the threshold for created time such that only those entries      * in unique indexes are purged which have      *      *     async indexer time - creation time> threshold      *      * @param unit time unit      * @param time time value in given unit      */
specifier|public
name|void
name|setCreatedTimeThreshold
parameter_list|(
name|TimeUnit
name|unit
parameter_list|,
name|long
name|time
parameter_list|)
block|{
name|uniqueIndexCleaner
operator|=
operator|new
name|UniqueIndexCleaner
argument_list|(
name|unit
argument_list|,
name|time
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|getSyncIndexPaths
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|indexPaths
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|NodeState
name|root
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|indexPath
range|:
name|indexPathService
operator|.
name|getIndexPaths
argument_list|()
control|)
block|{
name|NodeState
name|idx
init|=
name|getNode
argument_list|(
name|root
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|TYPE_LUCENE
operator|.
name|equals
argument_list|(
name|idx
operator|.
name|getString
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|)
argument_list|)
operator|&&
name|idx
operator|.
name|hasChildNode
argument_list|(
name|PROPERTY_INDEX
argument_list|)
condition|)
block|{
name|indexPaths
operator|.
name|add
argument_list|(
name|indexPath
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|indexPaths
return|;
block|}
specifier|private
name|IndexInfo
name|switchBucketsAndCollectIndexData
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|indexPaths
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|asyncInfo
parameter_list|,
name|CleanupStats
name|stats
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|IndexInfo
name|indexInfo
init|=
operator|new
name|IndexInfo
argument_list|()
decl_stmt|;
name|NodeState
name|root
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|boolean
name|modified
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|indexPath
range|:
name|indexPaths
control|)
block|{
name|NodeState
name|idx
init|=
name|getNode
argument_list|(
name|root
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
name|NodeBuilder
name|idxb
init|=
name|child
argument_list|(
name|builder
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
name|String
name|laneName
init|=
name|IndexUtils
operator|.
name|getAsyncLaneName
argument_list|(
name|idx
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
name|Long
name|lastIndexedTo
init|=
name|asyncInfo
operator|.
name|get
argument_list|(
name|laneName
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastIndexedTo
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Not able to determine async index info for lane {}. "
operator|+
literal|"Known lanes {}"
argument_list|,
name|laneName
argument_list|,
name|asyncInfo
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|NodeState
name|propertyIndexNode
init|=
name|idx
operator|.
name|getChildNode
argument_list|(
name|PROPERTY_INDEX
argument_list|)
decl_stmt|;
name|NodeBuilder
name|propIndexNodeBuilder
init|=
name|idxb
operator|.
name|getChildNode
argument_list|(
name|PROPERTY_INDEX
argument_list|)
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|cne
range|:
name|propertyIndexNode
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|NodeState
name|propIdxState
init|=
name|cne
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|String
name|propName
init|=
name|cne
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|simplePropertyIndex
argument_list|(
name|propIdxState
argument_list|)
condition|)
block|{
name|NodeBuilder
name|propIdx
init|=
name|propIndexNodeBuilder
operator|.
name|getChildNode
argument_list|(
name|propName
argument_list|)
decl_stmt|;
name|BucketSwitcher
name|bs
init|=
operator|new
name|BucketSwitcher
argument_list|(
name|propIdx
argument_list|)
decl_stmt|;
name|modified
operator||=
name|bs
operator|.
name|switchBucket
argument_list|(
name|lastIndexedTo
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|bucketName
range|:
name|bs
operator|.
name|getOldBuckets
argument_list|()
control|)
block|{
name|String
name|bucketPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|indexPath
argument_list|,
name|PROPERTY_INDEX
argument_list|,
name|propName
argument_list|,
name|bucketName
argument_list|)
decl_stmt|;
name|indexInfo
operator|.
name|oldBucketPaths
operator|.
name|add
argument_list|(
name|bucketPath
argument_list|)
expr_stmt|;
name|stats
operator|.
name|purgedIndexPaths
operator|.
name|add
argument_list|(
name|indexPath
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|uniquePropertyIndex
argument_list|(
name|propIdxState
argument_list|)
condition|)
block|{
name|String
name|indexNodePath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|indexPath
argument_list|,
name|PROPERTY_INDEX
argument_list|,
name|propName
argument_list|)
decl_stmt|;
name|indexInfo
operator|.
name|uniqueIndexPaths
operator|.
name|put
argument_list|(
name|indexNodePath
argument_list|,
name|lastIndexedTo
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|modified
condition|)
block|{
name|merge
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
return|return
name|indexInfo
return|;
block|}
specifier|private
name|void
name|purgeOldBuckets
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|bucketPaths
parameter_list|,
name|CleanupStats
name|stats
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|bucketPaths
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|NodeState
name|root
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|bucketPaths
control|)
block|{
name|NodeBuilder
name|bucket
init|=
name|child
argument_list|(
name|builder
argument_list|,
name|path
argument_list|)
decl_stmt|;
comment|//TODO Recursive delete to avoid large transaction
name|bucket
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|stats
operator|.
name|bucketCount
operator|=
name|bucketPaths
operator|.
name|size
argument_list|()
expr_stmt|;
name|merge
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|purgeOldUniqueIndexEntries
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|asyncInfo
parameter_list|,
name|CleanupStats
name|stats
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeState
name|root
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|e
range|:
name|asyncInfo
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|indexNodePath
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|NodeBuilder
name|idxb
init|=
name|child
argument_list|(
name|builder
argument_list|,
name|indexNodePath
argument_list|)
decl_stmt|;
name|int
name|removalCount
init|=
name|uniqueIndexCleaner
operator|.
name|clean
argument_list|(
name|idxb
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|removalCount
operator|>
literal|0
condition|)
block|{
name|stats
operator|.
name|purgedIndexPaths
operator|.
name|add
argument_list|(
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|indexNodePath
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Removed [{}] entries from [{}]"
argument_list|,
name|removalCount
argument_list|,
name|indexNodePath
argument_list|)
expr_stmt|;
block|}
name|stats
operator|.
name|uniqueIndexEntryRemovalCount
operator|+=
name|removalCount
expr_stmt|;
block|}
if|if
condition|(
name|stats
operator|.
name|uniqueIndexEntryRemovalCount
operator|>
literal|0
condition|)
block|{
name|merge
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|merge
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
throws|throws
name|CommitFailedException
block|{
comment|//TODO Configure conflict hooks
comment|//TODO Configure validator
comment|//Configure CommitContext
name|nodeStore
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
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|getAsyncInfo
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|infos
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|asyncLane
range|:
name|asyncIndexInfoService
operator|.
name|getAsyncLanes
argument_list|()
control|)
block|{
name|AsyncIndexInfo
name|info
init|=
name|asyncIndexInfoService
operator|.
name|getInfo
argument_list|(
name|asyncLane
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|infos
operator|.
name|put
argument_list|(
name|asyncLane
argument_list|,
name|info
operator|.
name|getLastIndexedTo
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No AsyncIndexInfo found for lane name [{}]"
argument_list|,
name|asyncLane
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|infos
return|;
block|}
specifier|private
specifier|static
name|NodeBuilder
name|child
parameter_list|(
name|NodeBuilder
name|nb
parameter_list|,
name|String
name|path
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|checkNotNull
argument_list|(
name|path
argument_list|)
argument_list|)
control|)
block|{
comment|//Use getChildNode to avoid creating new entries by default
name|nb
operator|=
name|nb
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|nb
return|;
block|}
specifier|private
specifier|static
name|boolean
name|simplePropertyIndex
parameter_list|(
name|NodeState
name|propIdxState
parameter_list|)
block|{
return|return
name|STORAGE_TYPE_CONTENT_MIRROR
operator|.
name|equals
argument_list|(
name|propIdxState
operator|.
name|getString
argument_list|(
name|PROP_STORAGE_TYPE
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|uniquePropertyIndex
parameter_list|(
name|NodeState
name|propIdxState
parameter_list|)
block|{
return|return
name|STORAGE_TYPE_UNIQUE
operator|.
name|equals
argument_list|(
name|propIdxState
operator|.
name|getString
argument_list|(
name|PROP_STORAGE_TYPE
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|IndexInfo
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|oldBucketPaths
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/* indexPath, lastIndexedTo */
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|uniqueIndexPaths
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
block|}
specifier|private
specifier|static
class|class
name|CleanupStats
block|{
name|int
name|uniqueIndexEntryRemovalCount
decl_stmt|;
name|int
name|bucketCount
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|purgedIndexPaths
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"Removed %d index buckets, %d unique index entries "
operator|+
literal|"from indexes %s"
argument_list|,
name|bucketCount
argument_list|,
name|uniqueIndexEntryRemovalCount
argument_list|,
name|purgedIndexPaths
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

