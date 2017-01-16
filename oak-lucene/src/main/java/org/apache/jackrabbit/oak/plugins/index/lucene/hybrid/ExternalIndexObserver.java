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
name|hybrid
package|;
end_package

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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|lucene
operator|.
name|IndexDefinition
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
name|lucene
operator|.
name|IndexTracker
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
name|lucene
operator|.
name|LuceneDocumentMaker
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
name|observation
operator|.
name|Filter
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
name|CommitContext
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
name|Observer
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
name|stats
operator|.
name|MeterStats
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
name|StatisticsProvider
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
name|StatsOptions
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
name|TimerStats
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
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

begin_class
class|class
name|ExternalIndexObserver
implements|implements
name|Observer
implements|,
name|Filter
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
name|IndexingQueue
name|indexingQueue
decl_stmt|;
specifier|private
specifier|final
name|IndexTracker
name|indexTracker
decl_stmt|;
specifier|private
specifier|final
name|MeterStats
name|added
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|timer
decl_stmt|;
specifier|public
name|ExternalIndexObserver
parameter_list|(
name|IndexingQueue
name|indexingQueue
parameter_list|,
name|IndexTracker
name|indexTracker
parameter_list|,
name|StatisticsProvider
name|statisticsProvider
parameter_list|)
block|{
name|this
operator|.
name|indexingQueue
operator|=
name|checkNotNull
argument_list|(
name|indexingQueue
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexTracker
operator|=
name|checkNotNull
argument_list|(
name|indexTracker
argument_list|)
expr_stmt|;
name|this
operator|.
name|added
operator|=
name|statisticsProvider
operator|.
name|getMeter
argument_list|(
literal|"HYBRID_EXTERNAL_ADDED"
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
name|this
operator|.
name|timer
operator|=
name|statisticsProvider
operator|.
name|getTimer
argument_list|(
literal|"HYBRID_EXTERNAL_TIME"
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|excludes
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|root
parameter_list|,
annotation|@
name|Nonnull
name|CommitInfo
name|info
parameter_list|)
block|{
comment|//Only interested in external changes
if|if
condition|(
operator|!
name|info
operator|.
name|isExternal
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|CommitContext
name|commitContext
init|=
operator|(
name|CommitContext
operator|)
name|info
operator|.
name|getInfo
argument_list|()
operator|.
name|get
argument_list|(
name|CommitContext
operator|.
name|NAME
argument_list|)
decl_stmt|;
comment|//Commit done internally i.e. one not using Root/Tree API
if|if
condition|(
name|commitContext
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
name|IndexedPaths
name|indexedPaths
init|=
operator|(
name|IndexedPaths
operator|)
name|commitContext
operator|.
name|get
argument_list|(
name|LuceneDocumentHolder
operator|.
name|NAME
argument_list|)
decl_stmt|;
comment|//Nothing to be indexed
if|if
condition|(
name|indexedPaths
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"IndexPaths not found. Journal support missing"
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|indexedPaths
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|contentChanged
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|after
parameter_list|,
annotation|@
name|Nonnull
name|CommitInfo
name|info
parameter_list|)
block|{
comment|//Only interested in external changes
if|if
condition|(
name|excludes
argument_list|(
name|after
argument_list|,
name|info
argument_list|)
condition|)
block|{
return|return;
block|}
name|CommitContext
name|commitContext
init|=
operator|(
name|CommitContext
operator|)
name|info
operator|.
name|getInfo
argument_list|()
operator|.
name|get
argument_list|(
name|CommitContext
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|IndexedPaths
name|indexedPaths
init|=
operator|(
name|IndexedPaths
operator|)
name|commitContext
operator|.
name|get
argument_list|(
name|LuceneDocumentHolder
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|commitContext
operator|.
name|remove
argument_list|(
name|LuceneDocumentHolder
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|log
operator|.
name|trace
argument_list|(
literal|"Received indexed paths {}"
argument_list|,
name|indexedPaths
argument_list|)
expr_stmt|;
name|int
name|droppedCount
init|=
literal|0
decl_stmt|;
name|int
name|indexedCount
init|=
literal|0
decl_stmt|;
name|TimerStats
operator|.
name|Context
name|ctx
init|=
name|timer
operator|.
name|time
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|indexPaths
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexedPathInfo
name|indexData
range|:
name|indexedPaths
control|)
block|{
name|String
name|path
init|=
name|indexData
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|NodeState
name|indexedNode
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|indexPath
range|:
name|indexData
operator|.
name|getIndexPaths
argument_list|()
control|)
block|{
name|IndexDefinition
name|defn
init|=
name|indexTracker
operator|.
name|getIndexDefinition
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
comment|//Only update those indexes which are in use in "this" cluster node
comment|//i.e. for which IndexDefinition is being tracked by IndexTracker
comment|//This would avoid wasted effort for those cases where index is updated
comment|//but not used locally
if|if
condition|(
name|defn
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
comment|//Lazily initialize indexedNode
if|if
condition|(
name|indexedNode
operator|==
literal|null
condition|)
block|{
name|indexedNode
operator|=
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|after
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|indexedNode
operator|.
name|exists
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|IndexDefinition
operator|.
name|IndexingRule
name|indexingRule
init|=
name|defn
operator|.
name|getApplicableIndexingRule
argument_list|(
name|indexedNode
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexingRule
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"No indexingRule found for path {} for index {}"
argument_list|,
name|path
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|indexPaths
operator|.
name|add
argument_list|(
name|indexPath
argument_list|)
expr_stmt|;
try|try
block|{
name|Document
name|doc
init|=
operator|new
name|LuceneDocumentMaker
argument_list|(
name|defn
argument_list|,
name|indexingRule
argument_list|,
name|path
argument_list|)
operator|.
name|makeDocument
argument_list|(
name|indexedNode
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|indexingQueue
operator|.
name|add
argument_list|(
name|LuceneDoc
operator|.
name|forUpdate
argument_list|(
name|indexPath
argument_list|,
name|path
argument_list|,
name|doc
argument_list|)
argument_list|)
condition|)
block|{
name|indexedCount
operator|++
expr_stmt|;
block|}
else|else
block|{
name|droppedCount
operator|++
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Ignoring making LuceneDocument for path {} for index {} due to exception"
argument_list|,
name|path
argument_list|,
name|indexPath
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|droppedCount
operator|>
literal|0
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Dropped [{}] docs from indexing as queue is full"
argument_list|,
name|droppedCount
argument_list|)
expr_stmt|;
block|}
name|added
operator|.
name|mark
argument_list|(
name|indexedCount
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|stop
argument_list|()
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Added {} documents for {} indexes from external changes"
argument_list|,
name|indexedCount
argument_list|,
name|indexPaths
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
