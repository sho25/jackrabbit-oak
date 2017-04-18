begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Calendar
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|IndexingContext
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
name|binary
operator|.
name|BinaryTextExtractor
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
name|util
operator|.
name|FacetHelper
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
name|util
operator|.
name|FacetsConfigProvider
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
name|writer
operator|.
name|LuceneIndexWriter
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
name|writer
operator|.
name|LuceneIndexWriterFactory
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
name|stats
operator|.
name|Clock
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
name|benchmark
operator|.
name|PerfLogger
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
name|util
operator|.
name|ISO8601
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
name|facet
operator|.
name|FacetsConfig
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
name|lucene
operator|.
name|IndexDefinition
operator|.
name|INDEX_DEFINITION_NODE
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
name|PROP_REFRESH_DEFN
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneIndexEditorContext
implements|implements
name|FacetsConfigProvider
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LuceneIndexEditorContext
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|PerfLogger
name|PERF_LOGGER
init|=
operator|new
name|PerfLogger
argument_list|(
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LuceneIndexEditorContext
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".perf"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|FacetsConfig
name|facetsConfig
decl_stmt|;
specifier|private
name|IndexDefinition
name|definition
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|definitionBuilder
decl_stmt|;
specifier|private
specifier|final
name|LuceneIndexWriterFactory
name|indexWriterFactory
decl_stmt|;
specifier|private
name|LuceneIndexWriter
name|writer
init|=
literal|null
decl_stmt|;
specifier|private
name|long
name|indexedNodes
decl_stmt|;
specifier|private
specifier|final
name|IndexUpdateCallback
name|updateCallback
decl_stmt|;
specifier|private
name|boolean
name|reindex
decl_stmt|;
specifier|private
specifier|final
name|ExtractedTextCache
name|extractedTextCache
decl_stmt|;
specifier|private
specifier|final
name|IndexAugmentorFactory
name|augmentorFactory
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|root
decl_stmt|;
specifier|private
specifier|final
name|IndexingContext
name|indexingContext
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|asyncIndexing
decl_stmt|;
comment|//Intentionally static, so that it can be set without passing around clock objects
comment|//Set for testing ONLY
specifier|private
specifier|static
name|Clock
name|clock
init|=
name|Clock
operator|.
name|SIMPLE
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|indexDefnRewritten
decl_stmt|;
specifier|private
name|BinaryTextExtractor
name|textExtractor
decl_stmt|;
name|LuceneIndexEditorContext
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|NodeBuilder
name|definition
parameter_list|,
annotation|@
name|Nullable
name|IndexDefinition
name|indexDefinition
parameter_list|,
name|IndexUpdateCallback
name|updateCallback
parameter_list|,
name|LuceneIndexWriterFactory
name|indexWriterFactory
parameter_list|,
name|ExtractedTextCache
name|extractedTextCache
parameter_list|,
name|IndexAugmentorFactory
name|augmentorFactory
parameter_list|,
name|IndexingContext
name|indexingContext
parameter_list|,
name|boolean
name|asyncIndexing
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|indexingContext
operator|=
name|checkNotNull
argument_list|(
name|indexingContext
argument_list|)
expr_stmt|;
name|this
operator|.
name|definitionBuilder
operator|=
name|definition
expr_stmt|;
name|this
operator|.
name|indexWriterFactory
operator|=
name|indexWriterFactory
expr_stmt|;
name|this
operator|.
name|definition
operator|=
name|indexDefinition
operator|!=
literal|null
condition|?
name|indexDefinition
else|:
name|createIndexDefinition
argument_list|(
name|root
argument_list|,
name|definition
argument_list|,
name|indexingContext
argument_list|,
name|asyncIndexing
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexedNodes
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|updateCallback
operator|=
name|updateCallback
expr_stmt|;
name|this
operator|.
name|extractedTextCache
operator|=
name|extractedTextCache
expr_stmt|;
name|this
operator|.
name|augmentorFactory
operator|=
name|augmentorFactory
expr_stmt|;
name|this
operator|.
name|asyncIndexing
operator|=
name|asyncIndexing
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|definition
operator|.
name|isOfOldFormat
argument_list|()
condition|)
block|{
name|indexDefnRewritten
operator|=
literal|true
expr_stmt|;
name|IndexDefinition
operator|.
name|updateDefinition
argument_list|(
name|definition
argument_list|,
name|indexingContext
operator|.
name|getIndexPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|indexDefnRewritten
operator|=
literal|false
expr_stmt|;
block|}
block|}
name|LuceneIndexWriter
name|getWriter
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
block|{
comment|//Lazy initialization so as to ensure that definition is based
comment|//on latest NodeBuilder state specially in case of reindexing
name|writer
operator|=
name|indexWriterFactory
operator|.
name|newInstance
argument_list|(
name|definition
argument_list|,
name|definitionBuilder
argument_list|,
name|reindex
argument_list|)
expr_stmt|;
block|}
return|return
name|writer
return|;
block|}
specifier|public
name|IndexingContext
name|getIndexingContext
parameter_list|()
block|{
return|return
name|indexingContext
return|;
block|}
comment|/**      * close writer if it's not null      */
name|void
name|closeWriter
parameter_list|()
throws|throws
name|IOException
block|{
name|Calendar
name|currentTime
init|=
name|getCalendar
argument_list|()
decl_stmt|;
specifier|final
name|long
name|start
init|=
name|PERF_LOGGER
operator|.
name|start
argument_list|()
decl_stmt|;
name|boolean
name|indexUpdated
init|=
name|getWriter
argument_list|()
operator|.
name|close
argument_list|(
name|currentTime
operator|.
name|getTimeInMillis
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexUpdated
condition|)
block|{
name|PERF_LOGGER
operator|.
name|end
argument_list|(
name|start
argument_list|,
operator|-
literal|1
argument_list|,
literal|"Closed writer for directory {}"
argument_list|,
name|definition
argument_list|)
expr_stmt|;
comment|//OAK-2029 Record the last updated status so
comment|//as to make IndexTracker detect changes when index
comment|//is stored in file system
name|NodeBuilder
name|status
init|=
name|definitionBuilder
operator|.
name|child
argument_list|(
literal|":status"
argument_list|)
decl_stmt|;
name|status
operator|.
name|setProperty
argument_list|(
literal|"lastUpdated"
argument_list|,
name|ISO8601
operator|.
name|format
argument_list|(
name|currentTime
argument_list|)
argument_list|,
name|Type
operator|.
name|DATE
argument_list|)
expr_stmt|;
name|status
operator|.
name|setProperty
argument_list|(
literal|"indexedNodes"
argument_list|,
name|indexedNodes
argument_list|)
expr_stmt|;
name|PERF_LOGGER
operator|.
name|end
argument_list|(
name|start
argument_list|,
operator|-
literal|1
argument_list|,
literal|"Overall Closed IndexWriter for directory {}"
argument_list|,
name|definition
argument_list|)
expr_stmt|;
if|if
condition|(
name|textExtractor
operator|!=
literal|null
condition|)
block|{
name|textExtractor
operator|.
name|done
argument_list|(
name|reindex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Only set for testing */
specifier|static
name|void
name|setClock
parameter_list|(
name|Clock
name|c
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|clock
operator|=
name|c
expr_stmt|;
block|}
specifier|static
specifier|private
name|Calendar
name|getCalendar
parameter_list|()
block|{
name|Calendar
name|ret
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|ret
operator|.
name|setTime
argument_list|(
name|clock
operator|.
name|getDate
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
specifier|public
name|void
name|enableReindexMode
parameter_list|()
block|{
name|reindex
operator|=
literal|true
expr_stmt|;
name|IndexFormatVersion
name|version
init|=
name|IndexDefinition
operator|.
name|determineVersionForFreshIndex
argument_list|(
name|definitionBuilder
argument_list|)
decl_stmt|;
name|definitionBuilder
operator|.
name|setProperty
argument_list|(
name|IndexDefinition
operator|.
name|INDEX_VERSION
argument_list|,
name|version
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
comment|//Avoid obtaining the latest NodeState from builder as that would force purge of current transient state
comment|//as index definition does not get modified as part of IndexUpdate run in most case we rely on base state
comment|//For case where index definition is rewritten there we get fresh state
name|NodeState
name|defnState
init|=
name|indexDefnRewritten
condition|?
name|definitionBuilder
operator|.
name|getNodeState
argument_list|()
else|:
name|definitionBuilder
operator|.
name|getBaseState
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|IndexDefinition
operator|.
name|isDisableStoredIndexDefinition
argument_list|()
condition|)
block|{
name|definitionBuilder
operator|.
name|setChildNode
argument_list|(
name|INDEX_DEFINITION_NODE
argument_list|,
name|NodeStateCloner
operator|.
name|cloneVisibleState
argument_list|(
name|defnState
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|uid
init|=
name|configureUniqueId
argument_list|(
name|definitionBuilder
argument_list|)
decl_stmt|;
comment|//Refresh the index definition based on update builder state
name|definition
operator|=
name|IndexDefinition
operator|.
name|newBuilder
argument_list|(
name|root
argument_list|,
name|defnState
argument_list|,
name|indexingContext
operator|.
name|getIndexPath
argument_list|()
argument_list|)
operator|.
name|version
argument_list|(
name|version
argument_list|)
operator|.
name|uid
argument_list|(
name|uid
argument_list|)
operator|.
name|reindex
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
specifier|public
name|long
name|incIndexedNodes
parameter_list|()
block|{
name|indexedNodes
operator|++
expr_stmt|;
return|return
name|indexedNodes
return|;
block|}
specifier|private
name|boolean
name|isAsyncIndexing
parameter_list|()
block|{
return|return
name|asyncIndexing
return|;
block|}
specifier|public
name|long
name|getIndexedNodes
parameter_list|()
block|{
return|return
name|indexedNodes
return|;
block|}
name|void
name|indexUpdate
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|updateCallback
operator|.
name|indexUpdate
argument_list|()
expr_stmt|;
block|}
specifier|public
name|IndexDefinition
name|getDefinition
parameter_list|()
block|{
return|return
name|definition
return|;
block|}
name|LuceneDocumentMaker
name|newDocumentMaker
parameter_list|(
name|IndexDefinition
operator|.
name|IndexingRule
name|rule
parameter_list|,
name|String
name|path
parameter_list|)
block|{
comment|//Faceting is only enabled for async mode
name|FacetsConfigProvider
name|facetsConfigProvider
init|=
name|isAsyncIndexing
argument_list|()
condition|?
name|this
else|:
literal|null
decl_stmt|;
return|return
operator|new
name|LuceneDocumentMaker
argument_list|(
name|getTextExtractor
argument_list|()
argument_list|,
name|facetsConfigProvider
argument_list|,
name|augmentorFactory
argument_list|,
name|definition
argument_list|,
name|rule
argument_list|,
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|FacetsConfig
name|getFacetsConfig
parameter_list|()
block|{
if|if
condition|(
name|facetsConfig
operator|==
literal|null
condition|)
block|{
name|facetsConfig
operator|=
name|FacetHelper
operator|.
name|getFacetsConfig
argument_list|(
name|definitionBuilder
argument_list|)
expr_stmt|;
block|}
return|return
name|facetsConfig
return|;
block|}
specifier|private
name|BinaryTextExtractor
name|getTextExtractor
parameter_list|()
block|{
if|if
condition|(
name|textExtractor
operator|==
literal|null
operator|&&
name|isAsyncIndexing
argument_list|()
condition|)
block|{
comment|//Create lazily to ensure that if its reindex case then update definition is picked
name|textExtractor
operator|=
operator|new
name|BinaryTextExtractor
argument_list|(
name|extractedTextCache
argument_list|,
name|definition
argument_list|,
name|reindex
argument_list|)
expr_stmt|;
block|}
return|return
name|textExtractor
return|;
block|}
specifier|public
name|boolean
name|isReindex
parameter_list|()
block|{
return|return
name|reindex
return|;
block|}
specifier|public
specifier|static
name|String
name|configureUniqueId
parameter_list|(
name|NodeBuilder
name|definition
parameter_list|)
block|{
name|NodeBuilder
name|status
init|=
name|definition
operator|.
name|child
argument_list|(
name|IndexDefinition
operator|.
name|STATUS_NODE
argument_list|)
decl_stmt|;
name|String
name|uid
init|=
name|status
operator|.
name|getString
argument_list|(
name|IndexDefinition
operator|.
name|PROP_UID
argument_list|)
decl_stmt|;
if|if
condition|(
name|uid
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|uid
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|Clock
operator|.
name|SIMPLE
operator|.
name|getTimeIncreasing
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|uid
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|Clock
operator|.
name|SIMPLE
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|status
operator|.
name|setProperty
argument_list|(
name|IndexDefinition
operator|.
name|PROP_UID
argument_list|,
name|uid
argument_list|)
expr_stmt|;
block|}
return|return
name|uid
return|;
block|}
specifier|private
specifier|static
name|IndexDefinition
name|createIndexDefinition
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|NodeBuilder
name|definition
parameter_list|,
name|IndexingContext
name|indexingContext
parameter_list|,
name|boolean
name|asyncIndexing
parameter_list|)
block|{
name|NodeState
name|defnState
init|=
name|definition
operator|.
name|getBaseState
argument_list|()
decl_stmt|;
if|if
condition|(
name|asyncIndexing
operator|&&
operator|!
name|IndexDefinition
operator|.
name|isDisableStoredIndexDefinition
argument_list|()
condition|)
block|{
if|if
condition|(
name|definition
operator|.
name|getBoolean
argument_list|(
name|PROP_REFRESH_DEFN
argument_list|)
condition|)
block|{
name|definition
operator|.
name|removeProperty
argument_list|(
name|PROP_REFRESH_DEFN
argument_list|)
expr_stmt|;
name|NodeState
name|clonedState
init|=
name|NodeStateCloner
operator|.
name|cloneVisibleState
argument_list|(
name|defnState
argument_list|)
decl_stmt|;
name|definition
operator|.
name|setChildNode
argument_list|(
name|INDEX_DEFINITION_NODE
argument_list|,
name|clonedState
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Refreshed the index definition for [{}]"
argument_list|,
name|indexingContext
operator|.
name|getIndexPath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Updated index definition is {}"
argument_list|,
name|NodeStateUtils
operator|.
name|toString
argument_list|(
name|clonedState
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|definition
operator|.
name|hasChildNode
argument_list|(
name|INDEX_DEFINITION_NODE
argument_list|)
condition|)
block|{
name|definition
operator|.
name|setChildNode
argument_list|(
name|INDEX_DEFINITION_NODE
argument_list|,
name|NodeStateCloner
operator|.
name|cloneVisibleState
argument_list|(
name|defnState
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Stored the cloned index definition for [{}]. Changes in index definition would now only be "
operator|+
literal|"effective post reindexing"
argument_list|,
name|indexingContext
operator|.
name|getIndexPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|defnState
argument_list|,
name|indexingContext
operator|.
name|getIndexPath
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

