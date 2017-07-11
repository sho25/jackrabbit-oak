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
name|importer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|HashMap
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ArrayListMultimap
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
name|ListMultimap
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
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
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
name|IndexEditorProvider
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
name|IndexUpdate
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
name|IndexUpdateCallback
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
name|plugins
operator|.
name|index
operator|.
name|importer
operator|.
name|AsyncIndexerLock
operator|.
name|LockToken
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
name|EditorDiff
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
name|VisibleEditor
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
name|NodeStateUtils
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
name|checkArgument
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
name|importer
operator|.
name|NodeStoreUtils
operator|.
name|mergeWithConcurrentCheck
import|;
end_import

begin_class
specifier|public
class|class
name|IndexImporter
block|{
comment|/**      * Symbolic name use to indicate sync indexes      */
specifier|static
specifier|final
name|String
name|ASYNC_LANE_SYNC
init|=
literal|"sync"
decl_stmt|;
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
name|File
name|indexDir
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|IndexImporterProvider
argument_list|>
name|importers
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|IndexerInfo
name|indexerInfo
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|File
argument_list|>
name|indexes
decl_stmt|;
specifier|private
specifier|final
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|IndexInfo
argument_list|>
name|asyncLaneToIndexMapping
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|indexedState
decl_stmt|;
specifier|private
specifier|final
name|IndexEditorProvider
name|indexEditorProvider
decl_stmt|;
specifier|private
specifier|final
name|AsyncIndexerLock
name|indexerLock
decl_stmt|;
specifier|public
name|IndexImporter
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|File
name|indexDir
parameter_list|,
name|IndexEditorProvider
name|indexEditorProvider
parameter_list|,
name|AsyncIndexerLock
name|indexerLock
parameter_list|)
throws|throws
name|IOException
block|{
name|checkArgument
argument_list|(
name|indexDir
operator|.
name|exists
argument_list|()
operator|&&
name|indexDir
operator|.
name|isDirectory
argument_list|()
argument_list|,
literal|"Path [%s] does not point "
operator|+
literal|"to existing directory"
argument_list|,
name|indexDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeStore
operator|=
name|nodeStore
expr_stmt|;
name|this
operator|.
name|indexDir
operator|=
name|indexDir
expr_stmt|;
name|this
operator|.
name|indexEditorProvider
operator|=
name|indexEditorProvider
expr_stmt|;
name|indexerInfo
operator|=
name|IndexerInfo
operator|.
name|fromDirectory
argument_list|(
name|indexDir
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexerLock
operator|=
name|indexerLock
expr_stmt|;
name|indexes
operator|=
name|indexerInfo
operator|.
name|getIndexes
argument_list|()
expr_stmt|;
name|asyncLaneToIndexMapping
operator|=
name|mapIndexesToLanes
argument_list|(
name|indexes
argument_list|)
expr_stmt|;
name|indexedState
operator|=
name|checkNotNull
argument_list|(
name|nodeStore
operator|.
name|retrieve
argument_list|(
name|indexerInfo
operator|.
name|checkpoint
argument_list|)
argument_list|,
literal|"Cannot retrieve "
operator|+
literal|"checkpointed state [%s]"
argument_list|,
name|indexerInfo
operator|.
name|checkpoint
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|importIndex
parameter_list|()
throws|throws
name|IOException
throws|,
name|CommitFailedException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Proceeding to import {} indexes from {}"
argument_list|,
name|indexes
operator|.
name|keySet
argument_list|()
argument_list|,
name|indexDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
comment|//TODO Need to review it for idempotent design. A failure in any step should not
comment|//leave setup in in consistent state and provide option for recovery
comment|//Step 1 - Switch the index lanes so that async indexer does not touch them
comment|//while we are importing the index data
name|switchLanes
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Done with switching of index lanes before import"
argument_list|)
expr_stmt|;
comment|//Step 2 - Import the existing index data
name|importIndexData
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Done with importing of index data"
argument_list|)
expr_stmt|;
comment|//Step 3 - Bring index upto date
name|bringIndexUpToDate
argument_list|()
expr_stmt|;
comment|//Step 4 - Release the checkpoint
name|releaseCheckpoint
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|addImporterProvider
parameter_list|(
name|IndexImporterProvider
name|importerProvider
parameter_list|)
block|{
name|importers
operator|.
name|put
argument_list|(
name|importerProvider
operator|.
name|getType
argument_list|()
argument_list|,
name|importerProvider
argument_list|)
expr_stmt|;
block|}
name|void
name|switchLanes
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|builder
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexInfo
name|indexInfo
range|:
name|asyncLaneToIndexMapping
operator|.
name|values
argument_list|()
control|)
block|{
name|NodeBuilder
name|idxBuilder
init|=
name|NodeStoreUtils
operator|.
name|childBuilder
argument_list|(
name|builder
argument_list|,
name|indexInfo
operator|.
name|indexPath
argument_list|)
decl_stmt|;
name|AsyncLaneSwitcher
operator|.
name|switchLane
argument_list|(
name|idxBuilder
argument_list|,
name|AsyncLaneSwitcher
operator|.
name|getTempLaneName
argument_list|(
name|indexInfo
operator|.
name|asyncLaneName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|mergeWithConcurrentCheck
argument_list|(
name|nodeStore
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
name|void
name|importIndexData
parameter_list|()
throws|throws
name|CommitFailedException
throws|,
name|IOException
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
name|IndexInfo
name|indexInfo
range|:
name|asyncLaneToIndexMapping
operator|.
name|values
argument_list|()
control|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Importing index data for {}"
argument_list|,
name|indexInfo
operator|.
name|indexPath
argument_list|)
expr_stmt|;
name|NodeBuilder
name|idxBuilder
init|=
name|NodeStoreUtils
operator|.
name|childBuilder
argument_list|(
name|builder
argument_list|,
name|indexInfo
operator|.
name|indexPath
argument_list|)
decl_stmt|;
comment|//TODO Drop existing hidden folders
comment|//Increment reindex count
name|getImporter
argument_list|(
name|indexInfo
operator|.
name|type
argument_list|)
operator|.
name|importIndex
argument_list|(
name|root
argument_list|,
name|idxBuilder
argument_list|,
name|indexInfo
operator|.
name|indexDir
argument_list|)
expr_stmt|;
block|}
name|mergeWithConcurrentCheck
argument_list|(
name|nodeStore
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
name|void
name|bringIndexUpToDate
parameter_list|()
throws|throws
name|CommitFailedException
block|{
for|for
control|(
name|String
name|laneName
range|:
name|asyncLaneToIndexMapping
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|ASYNC_LANE_SYNC
operator|.
name|equals
argument_list|(
name|laneName
argument_list|)
condition|)
block|{
continue|continue;
comment|//TODO Handle sync indexes
block|}
name|bringAsyncIndexUpToDate
argument_list|(
name|laneName
argument_list|,
name|asyncLaneToIndexMapping
operator|.
name|get
argument_list|(
name|laneName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|bringAsyncIndexUpToDate
parameter_list|(
name|String
name|laneName
parameter_list|,
name|List
argument_list|<
name|IndexInfo
argument_list|>
name|indexInfos
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|LockToken
name|lockToken
init|=
name|interruptCurrentIndexing
argument_list|(
name|laneName
argument_list|)
decl_stmt|;
name|String
name|checkpoint
init|=
name|getAsync
argument_list|()
operator|.
name|getString
argument_list|(
name|laneName
argument_list|)
decl_stmt|;
name|checkNotNull
argument_list|(
name|checkpoint
argument_list|,
literal|"No current checkpoint found for lane [%s]"
argument_list|,
name|laneName
argument_list|)
expr_stmt|;
comment|//TODO Support case where checkpoint got lost or complete reindexing is done
name|NodeState
name|after
init|=
name|nodeStore
operator|.
name|retrieve
argument_list|(
name|checkpoint
argument_list|)
decl_stmt|;
name|checkNotNull
argument_list|(
name|after
argument_list|,
literal|"No state found for checkpoint [%s] for lane [%s]"
argument_list|,
name|checkpoint
argument_list|,
name|laneName
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|indexedState
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|IndexUpdate
name|indexUpdate
init|=
operator|new
name|IndexUpdate
argument_list|(
name|indexEditorProvider
argument_list|,
name|AsyncLaneSwitcher
operator|.
name|getTempLaneName
argument_list|(
name|laneName
argument_list|)
argument_list|,
name|nodeStore
operator|.
name|getRoot
argument_list|()
argument_list|,
name|builder
argument_list|,
name|IndexUpdateCallback
operator|.
name|NOOP
argument_list|)
decl_stmt|;
name|CommitFailedException
name|exception
init|=
name|EditorDiff
operator|.
name|process
argument_list|(
name|VisibleEditor
operator|.
name|wrap
argument_list|(
name|indexUpdate
argument_list|)
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
throw|throw
name|exception
throw|;
block|}
name|revertLaneChange
argument_list|(
name|builder
argument_list|,
name|indexInfos
argument_list|)
expr_stmt|;
name|mergeWithConcurrentCheck
argument_list|(
name|nodeStore
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|resumeCurrentIndexing
argument_list|(
name|lockToken
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Import done for indexes {}"
argument_list|,
name|indexInfos
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|revertLaneChange
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|List
argument_list|<
name|IndexInfo
argument_list|>
name|indexInfos
parameter_list|)
block|{
for|for
control|(
name|IndexInfo
name|info
range|:
name|indexInfos
control|)
block|{
name|NodeBuilder
name|idxBuilder
init|=
name|NodeStoreUtils
operator|.
name|childBuilder
argument_list|(
name|builder
argument_list|,
name|info
operator|.
name|indexPath
argument_list|)
decl_stmt|;
name|AsyncLaneSwitcher
operator|.
name|revertSwitch
argument_list|(
name|idxBuilder
argument_list|,
name|info
operator|.
name|indexPath
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|resumeCurrentIndexing
parameter_list|(
name|LockToken
name|lockToken
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|indexerLock
operator|.
name|unlock
argument_list|(
name|lockToken
argument_list|)
expr_stmt|;
block|}
specifier|private
name|LockToken
name|interruptCurrentIndexing
parameter_list|(
name|String
name|laneName
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
name|indexerLock
operator|.
name|lock
argument_list|(
name|laneName
argument_list|)
return|;
block|}
specifier|private
name|IndexImporterProvider
name|getImporter
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|IndexImporterProvider
name|provider
init|=
name|importers
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
return|return
name|checkNotNull
argument_list|(
name|provider
argument_list|,
literal|"No IndexImporterProvider found for type [%s]"
argument_list|,
name|type
argument_list|)
return|;
block|}
specifier|private
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|IndexInfo
argument_list|>
name|mapIndexesToLanes
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|File
argument_list|>
name|indexes
parameter_list|)
block|{
name|NodeState
name|rootState
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|IndexInfo
argument_list|>
name|map
init|=
name|ArrayListMultimap
operator|.
name|create
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
name|File
argument_list|>
name|e
range|:
name|indexes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|indexPath
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|NodeState
name|indexState
init|=
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|rootState
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
name|checkArgument
argument_list|(
name|indexState
operator|.
name|exists
argument_list|()
argument_list|,
literal|"No index node found at path [%s]"
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
name|String
name|type
init|=
name|indexState
operator|.
name|getString
argument_list|(
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
argument_list|)
decl_stmt|;
name|checkNotNull
argument_list|(
name|type
argument_list|,
literal|"No 'type' property found for index at path [%s]"
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
name|String
name|asyncName
init|=
name|getAsyncLaneName
argument_list|(
name|indexPath
argument_list|,
name|indexState
argument_list|)
decl_stmt|;
if|if
condition|(
name|asyncName
operator|==
literal|null
condition|)
block|{
name|asyncName
operator|=
name|ASYNC_LANE_SYNC
expr_stmt|;
block|}
name|map
operator|.
name|put
argument_list|(
name|asyncName
argument_list|,
operator|new
name|IndexInfo
argument_list|(
name|indexPath
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|,
name|asyncName
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
comment|/**      * Determines the async lane name. This method also check if lane was previously switched      * then it uses the actual lane name prior to switch was done      *      * @param indexPath path of index. Mostly used in reporting exception      * @param indexState nodeState for index at given path      *      * @return async lane name or null which would be the case for sync indexes      */
specifier|static
name|String
name|getAsyncLaneName
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|NodeState
name|indexState
parameter_list|)
block|{
name|PropertyState
name|asyncPrevious
init|=
name|indexState
operator|.
name|getProperty
argument_list|(
name|AsyncLaneSwitcher
operator|.
name|ASYNC_PREVIOUS
argument_list|)
decl_stmt|;
if|if
condition|(
name|asyncPrevious
operator|!=
literal|null
operator|&&
operator|!
name|AsyncLaneSwitcher
operator|.
name|isNone
argument_list|(
name|asyncPrevious
argument_list|)
condition|)
block|{
return|return
name|IndexUtils
operator|.
name|getAsyncLaneName
argument_list|(
name|indexState
argument_list|,
name|indexPath
argument_list|,
name|asyncPrevious
argument_list|)
return|;
block|}
return|return
name|IndexUtils
operator|.
name|getAsyncLaneName
argument_list|(
name|indexState
argument_list|,
name|indexPath
argument_list|)
return|;
block|}
specifier|private
name|void
name|releaseCheckpoint
parameter_list|()
block|{
name|nodeStore
operator|.
name|release
argument_list|(
name|indexerInfo
operator|.
name|checkpoint
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Released the referred checkpoint [{}]"
argument_list|,
name|indexerInfo
operator|.
name|checkpoint
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NodeState
name|getAsync
parameter_list|()
block|{
return|return
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|":async"
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|IndexInfo
block|{
specifier|final
name|String
name|indexPath
decl_stmt|;
specifier|final
name|File
name|indexDir
decl_stmt|;
specifier|final
name|String
name|asyncLaneName
decl_stmt|;
specifier|final
name|String
name|type
decl_stmt|;
specifier|private
name|IndexInfo
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|File
name|indexDir
parameter_list|,
name|String
name|asyncLaneName
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|indexPath
operator|=
name|indexPath
expr_stmt|;
name|this
operator|.
name|indexDir
operator|=
name|indexDir
expr_stmt|;
name|this
operator|.
name|asyncLaneName
operator|=
name|asyncLaneName
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|indexPath
return|;
block|}
block|}
block|}
end_class

end_unit

