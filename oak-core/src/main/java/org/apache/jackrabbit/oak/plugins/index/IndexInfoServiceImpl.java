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
name|Map
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
name|ConcurrentHashMap
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
name|Function
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
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Reference
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|ReferenceCardinality
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|ReferencePolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|ReferencePolicyOption
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Service
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
name|checkNotNull
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
name|Predicates
operator|.
name|notNull
import|;
end_import

begin_class
annotation|@
name|Component
annotation|@
name|Service
specifier|public
class|class
name|IndexInfoServiceImpl
implements|implements
name|IndexInfoService
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
annotation|@
name|Reference
specifier|private
name|IndexPathService
name|indexPathService
decl_stmt|;
annotation|@
name|Reference
argument_list|(
name|policy
operator|=
name|ReferencePolicy
operator|.
name|DYNAMIC
argument_list|,
name|cardinality
operator|=
name|ReferenceCardinality
operator|.
name|OPTIONAL_MULTIPLE
argument_list|,
name|policyOption
operator|=
name|ReferencePolicyOption
operator|.
name|GREEDY
argument_list|,
name|referenceInterface
operator|=
name|IndexInfoProvider
operator|.
name|class
argument_list|)
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|IndexInfoProvider
argument_list|>
name|infoProviders
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Reference
specifier|private
name|NodeStore
name|nodeStore
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|IndexInfo
argument_list|>
name|getAllIndexInfo
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|filter
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|indexPathService
operator|.
name|getIndexPaths
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|IndexInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|IndexInfo
name|apply
parameter_list|(
name|String
name|indexPath
parameter_list|)
block|{
try|try
block|{
return|return
name|getInfo
argument_list|(
name|indexPath
argument_list|)
return|;
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
literal|"Error occurred while capturing IndexInfo for path {}"
argument_list|,
name|indexPath
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
argument_list|)
argument_list|,
name|notNull
argument_list|()
argument_list|)
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
name|String
name|type
init|=
name|getIndexType
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|IndexInfoProvider
name|infoProvider
init|=
name|infoProviders
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|infoProvider
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|SimpleIndexInfo
argument_list|(
name|indexPath
argument_list|,
name|type
argument_list|)
return|;
block|}
return|return
name|infoProvider
operator|.
name|getInfo
argument_list|(
name|indexPath
argument_list|)
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
name|String
name|type
init|=
name|getIndexType
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No type property defined for index definition at path {}"
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|IndexInfoProvider
name|infoProvider
init|=
name|infoProviders
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|infoProvider
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No IndexInfoProvider for for index definition at path {} of type {}"
argument_list|,
name|indexPath
argument_list|,
name|type
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
comment|//TODO Reconsider this scenario
block|}
return|return
name|infoProvider
operator|.
name|isValid
argument_list|(
name|indexPath
argument_list|)
return|;
block|}
specifier|protected
name|void
name|bindInfoProviders
parameter_list|(
name|IndexInfoProvider
name|infoProvider
parameter_list|)
block|{
name|infoProviders
operator|.
name|put
argument_list|(
name|checkNotNull
argument_list|(
name|infoProvider
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|,
name|infoProvider
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|unbindInfoProviders
parameter_list|(
name|IndexInfoProvider
name|infoProvider
parameter_list|)
block|{
name|infoProviders
operator|.
name|remove
argument_list|(
name|infoProvider
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|getIndexType
parameter_list|(
name|String
name|indexPath
parameter_list|)
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
name|String
name|type
init|=
name|idxState
operator|.
name|getString
argument_list|(
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
operator|||
literal|"disabled"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|type
return|;
block|}
specifier|private
specifier|static
class|class
name|SimpleIndexInfo
implements|implements
name|IndexInfo
block|{
specifier|private
specifier|final
name|String
name|indexPath
decl_stmt|;
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
specifier|private
name|SimpleIndexInfo
parameter_list|(
name|String
name|indexPath
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
name|type
operator|=
name|type
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
name|type
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
literal|null
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
operator|-
literal|1
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
operator|-
literal|1
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
operator|-
literal|1
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
operator|-
literal|1
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
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

