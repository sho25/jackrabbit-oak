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
name|Set
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
name|ImmutableSet
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
name|Iterables
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
name|json
operator|.
name|JsopDiff
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
name|IndexInfo
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
name|IndexInfoProvider
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
name|lucene
operator|.
name|directory
operator|.
name|DirectoryUtils
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
name|directory
operator|.
name|IndexConsistencyChecker
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
name|MultiplexersLucene
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
name|EqualsDiff
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
name|ReadOnlyBuilder
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
name|index
operator|.
name|DirectoryReader
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
name|store
operator|.
name|Directory
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
name|lucene
operator|.
name|IndexDefinition
operator|.
name|INDEX_DEFINITION_NODE
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneIndexInfoProvider
implements|implements
name|IndexInfoProvider
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
name|AsyncIndexInfoService
name|asyncInfoService
decl_stmt|;
specifier|private
specifier|final
name|File
name|workDir
decl_stmt|;
specifier|public
name|LuceneIndexInfoProvider
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|AsyncIndexInfoService
name|asyncInfoService
parameter_list|,
name|File
name|workDir
parameter_list|)
block|{
name|this
operator|.
name|nodeStore
operator|=
name|checkNotNull
argument_list|(
name|nodeStore
argument_list|)
expr_stmt|;
name|this
operator|.
name|asyncInfoService
operator|=
name|checkNotNull
argument_list|(
name|asyncInfoService
argument_list|)
expr_stmt|;
name|this
operator|.
name|workDir
operator|=
name|checkNotNull
argument_list|(
name|workDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|LuceneIndexConstants
operator|.
name|TYPE_LUCENE
return|;
block|}
annotation|@
name|Override
specifier|public
name|IndexInfo
name|getInfo
parameter_list|(
name|String
name|indexPath
parameter_list|)
throws|throws
name|IOException
block|{
name|NodeState
name|idxState
init|=
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|nodeStore
operator|.
name|getRoot
argument_list|()
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
name|checkArgument
argument_list|(
name|LuceneIndexConstants
operator|.
name|TYPE_LUCENE
operator|.
name|equals
argument_list|(
name|idxState
operator|.
name|getString
argument_list|(
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
argument_list|)
argument_list|)
argument_list|,
literal|"Index definition at [%s] is not of type 'lucene'"
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
name|LuceneIndexInfo
name|info
init|=
operator|new
name|LuceneIndexInfo
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
name|computeSize
argument_list|(
name|idxState
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|computeIndexDefinitionChange
argument_list|(
name|idxState
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|computeLastUpdatedTime
argument_list|(
name|idxState
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|computeAsyncIndexInfo
argument_list|(
name|idxState
argument_list|,
name|indexPath
argument_list|,
name|info
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isValid
parameter_list|(
name|String
name|indexPath
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexConsistencyChecker
name|checker
init|=
operator|new
name|IndexConsistencyChecker
argument_list|(
name|nodeStore
operator|.
name|getRoot
argument_list|()
argument_list|,
name|indexPath
argument_list|,
name|workDir
argument_list|)
decl_stmt|;
return|return
name|checker
operator|.
name|check
argument_list|(
name|IndexConsistencyChecker
operator|.
name|Level
operator|.
name|BLOBS_ONLY
argument_list|)
operator|.
name|clean
return|;
block|}
specifier|private
name|void
name|computeAsyncIndexInfo
parameter_list|(
name|NodeState
name|idxState
parameter_list|,
name|String
name|indexPath
parameter_list|,
name|LuceneIndexInfo
name|info
parameter_list|)
block|{
name|String
name|asyncName
init|=
name|IndexUtils
operator|.
name|getAsyncLaneName
argument_list|(
name|idxState
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|asyncName
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No 'async' value for index definition at [{}]. Definition {}"
argument_list|,
name|indexPath
argument_list|,
name|idxState
argument_list|)
expr_stmt|;
return|return;
block|}
name|AsyncIndexInfo
name|asyncInfo
init|=
name|asyncInfoService
operator|.
name|getInfo
argument_list|(
name|asyncName
argument_list|)
decl_stmt|;
name|checkNotNull
argument_list|(
name|asyncInfo
argument_list|,
literal|"No async info found for name [%s] "
operator|+
literal|"for index at [%s]"
argument_list|,
name|asyncName
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
name|info
operator|.
name|indexedUptoTime
operator|=
name|asyncInfo
operator|.
name|getLastIndexedTo
argument_list|()
expr_stmt|;
name|info
operator|.
name|asyncName
operator|=
name|asyncName
expr_stmt|;
block|}
specifier|private
name|void
name|computeSize
parameter_list|(
name|NodeState
name|idxState
parameter_list|,
name|LuceneIndexInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexDefinition
name|defn
init|=
name|IndexDefinition
operator|.
name|newBuilder
argument_list|(
name|nodeStore
operator|.
name|getRoot
argument_list|()
argument_list|,
name|idxState
argument_list|,
name|info
operator|.
name|indexPath
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|dirName
range|:
name|idxState
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
if|if
condition|(
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|dirName
argument_list|)
operator|&&
name|MultiplexersLucene
operator|.
name|isIndexDirName
argument_list|(
name|dirName
argument_list|)
condition|)
block|{
name|Directory
name|dir
init|=
operator|new
name|OakDirectory
argument_list|(
operator|new
name|ReadOnlyBuilder
argument_list|(
name|idxState
argument_list|)
argument_list|,
name|dirName
argument_list|,
name|defn
argument_list|,
literal|true
argument_list|)
decl_stmt|;
try|try
init|(
name|DirectoryReader
name|dirReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
init|)
block|{
name|info
operator|.
name|numEntries
operator|+=
name|dirReader
operator|.
name|numDocs
argument_list|()
expr_stmt|;
name|info
operator|.
name|size
operator|=
name|DirectoryUtils
operator|.
name|dirSize
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|computeLastUpdatedTime
parameter_list|(
name|NodeState
name|idxState
parameter_list|,
name|LuceneIndexInfo
name|info
parameter_list|)
block|{
name|NodeState
name|status
init|=
name|idxState
operator|.
name|getChildNode
argument_list|(
name|IndexDefinition
operator|.
name|STATUS_NODE
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
operator|.
name|exists
argument_list|()
condition|)
block|{
name|PropertyState
name|updatedTime
init|=
name|status
operator|.
name|getProperty
argument_list|(
name|IndexDefinition
operator|.
name|STATUS_LAST_UPDATED
argument_list|)
decl_stmt|;
if|if
condition|(
name|updatedTime
operator|!=
literal|null
condition|)
block|{
name|info
operator|.
name|lastUpdatedTime
operator|=
name|ISO8601
operator|.
name|parse
argument_list|(
name|updatedTime
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|DATE
argument_list|)
argument_list|)
operator|.
name|getTimeInMillis
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|computeIndexDefinitionChange
parameter_list|(
name|NodeState
name|idxState
parameter_list|,
name|LuceneIndexInfo
name|info
parameter_list|)
block|{
name|NodeState
name|storedDefn
init|=
name|idxState
operator|.
name|getChildNode
argument_list|(
name|INDEX_DEFINITION_NODE
argument_list|)
decl_stmt|;
if|if
condition|(
name|storedDefn
operator|.
name|exists
argument_list|()
condition|)
block|{
name|NodeState
name|currentDefn
init|=
name|NodeStateCloner
operator|.
name|cloneVisibleState
argument_list|(
name|idxState
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|FilteringEqualsDiff
operator|.
name|equals
argument_list|(
name|storedDefn
argument_list|,
name|currentDefn
argument_list|)
condition|)
block|{
name|info
operator|.
name|indexDefinitionChanged
operator|=
literal|true
expr_stmt|;
name|info
operator|.
name|indexDiff
operator|=
name|JsopDiff
operator|.
name|diffToJsop
argument_list|(
name|storedDefn
argument_list|,
name|currentDefn
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
class|class
name|LuceneIndexInfo
implements|implements
name|IndexInfo
block|{
name|String
name|indexPath
decl_stmt|;
name|String
name|asyncName
decl_stmt|;
name|long
name|numEntries
decl_stmt|;
name|long
name|size
decl_stmt|;
name|long
name|indexedUptoTime
decl_stmt|;
name|long
name|lastUpdatedTime
decl_stmt|;
name|boolean
name|indexDefinitionChanged
decl_stmt|;
name|String
name|indexDiff
decl_stmt|;
specifier|public
name|LuceneIndexInfo
parameter_list|(
name|String
name|indexPath
parameter_list|)
block|{
name|this
operator|.
name|indexPath
operator|=
name|indexPath
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getIndexPath
parameter_list|()
block|{
return|return
name|indexPath
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|LuceneIndexConstants
operator|.
name|TYPE_LUCENE
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getAsyncLaneName
parameter_list|()
block|{
return|return
name|asyncName
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLastUpdatedTime
parameter_list|()
block|{
return|return
name|lastUpdatedTime
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getIndexedUpToTime
parameter_list|()
block|{
return|return
name|indexedUptoTime
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getEstimatedEntryCount
parameter_list|()
block|{
return|return
name|numEntries
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getSizeInBytes
parameter_list|()
block|{
return|return
name|size
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasIndexDefinitionChangedWithoutReindexing
parameter_list|()
block|{
return|return
name|indexDefinitionChanged
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getIndexDefinitionDiff
parameter_list|()
block|{
return|return
name|indexDiff
return|;
block|}
block|}
specifier|static
class|class
name|FilteringEqualsDiff
extends|extends
name|EqualsDiff
block|{
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|IGNORED_PROP_NAMES
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|IndexConstants
operator|.
name|REINDEX_COUNT
argument_list|,
name|IndexConstants
operator|.
name|REINDEX_PROPERTY_NAME
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|boolean
name|equals
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
name|before
operator|.
name|exists
argument_list|()
operator|==
name|after
operator|.
name|exists
argument_list|()
operator|&&
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|FilteringEqualsDiff
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
name|ignoredProp
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
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
name|boolean
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
name|ignoredProp
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|super
operator|.
name|propertyAdded
argument_list|(
name|after
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
if|if
condition|(
name|ignoredProp
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|super
operator|.
name|propertyDeleted
argument_list|(
name|before
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|ignoredProp
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|IGNORED_PROP_NAMES
operator|.
name|contains
argument_list|(
name|name
argument_list|)
operator|||
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

