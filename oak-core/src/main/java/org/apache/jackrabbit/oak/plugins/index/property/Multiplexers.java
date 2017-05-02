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
name|property
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
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
name|INDEX_CONTENT_NODE_NAME
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
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|property
operator|.
name|strategy
operator|.
name|ContentMirrorStoreStrategy
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
name|property
operator|.
name|strategy
operator|.
name|FilteringIndexStoreStrategy
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
name|property
operator|.
name|strategy
operator|.
name|IndexStoreStrategy
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
name|property
operator|.
name|strategy
operator|.
name|UniqueEntryStoreStrategy
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
name|mount
operator|.
name|Mount
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
name|mount
operator|.
name|MountInfoProvider
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
import|;
end_import

begin_class
specifier|public
class|class
name|Multiplexers
block|{
specifier|static
name|boolean
name|RO_PRIVATE_UNIQUE_INDEX
decl_stmt|;
specifier|private
name|Multiplexers
parameter_list|()
block|{     }
static|static
block|{
comment|// TODO OAK-4645 set default to true once the code is stable
name|String
name|ro
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"oak.multiplexing.readOnlyPrivateUniqueIndex"
argument_list|,
literal|"false"
argument_list|)
decl_stmt|;
name|RO_PRIVATE_UNIQUE_INDEX
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|ro
argument_list|)
expr_stmt|;
block|}
comment|/** Index storage strategy */
specifier|private
specifier|static
specifier|final
name|IndexStoreStrategy
name|UNIQUE
init|=
operator|new
name|UniqueEntryStoreStrategy
argument_list|(
name|INDEX_CONTENT_NODE_NAME
argument_list|)
decl_stmt|;
comment|/** Index storage strategy */
specifier|private
specifier|static
specifier|final
name|IndexStoreStrategy
name|MIRROR
init|=
operator|new
name|ContentMirrorStoreStrategy
argument_list|(
name|INDEX_CONTENT_NODE_NAME
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|Set
argument_list|<
name|IndexStoreStrategy
argument_list|>
name|getStrategies
parameter_list|(
name|boolean
name|unique
parameter_list|,
name|MountInfoProvider
name|mountInfoProvider
parameter_list|,
name|NodeBuilder
name|definition
parameter_list|,
name|String
name|defaultName
parameter_list|)
block|{
name|Iterable
argument_list|<
name|String
argument_list|>
name|children
init|=
name|definition
operator|.
name|getChildNodeNames
argument_list|()
decl_stmt|;
return|return
name|getStrategies
argument_list|(
name|unique
argument_list|,
name|mountInfoProvider
argument_list|,
name|children
argument_list|,
name|defaultName
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Set
argument_list|<
name|IndexStoreStrategy
argument_list|>
name|getStrategies
parameter_list|(
name|boolean
name|unique
parameter_list|,
name|MountInfoProvider
name|mountInfoProvider
parameter_list|,
name|NodeState
name|definition
parameter_list|,
name|String
name|defaultName
parameter_list|)
block|{
name|Iterable
argument_list|<
name|String
argument_list|>
name|children
init|=
name|definition
operator|.
name|getChildNodeNames
argument_list|()
decl_stmt|;
return|return
name|getStrategies
argument_list|(
name|unique
argument_list|,
name|mountInfoProvider
argument_list|,
name|children
argument_list|,
name|defaultName
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|IndexStoreStrategy
argument_list|>
name|getStrategies
parameter_list|(
name|boolean
name|unique
parameter_list|,
name|MountInfoProvider
name|mountInfoProvider
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|children
parameter_list|,
name|String
name|defaultName
parameter_list|)
block|{
if|if
condition|(
name|mountInfoProvider
operator|.
name|hasNonDefaultMounts
argument_list|()
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// TODO should this be collected from the index def?
for|for
control|(
name|String
name|name
range|:
name|children
control|)
block|{
if|if
condition|(
name|isIndexStorageNode
argument_list|(
name|name
argument_list|,
name|defaultName
argument_list|)
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
name|names
operator|.
name|remove
argument_list|(
name|defaultName
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|IndexStoreStrategy
argument_list|>
name|strategies
init|=
operator|new
name|HashSet
argument_list|<
name|IndexStoreStrategy
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Mount
name|m
range|:
name|mountInfoProvider
operator|.
name|getNonDefaultMounts
argument_list|()
control|)
block|{
name|String
name|n
init|=
name|getNodeForMount
argument_list|(
name|m
argument_list|,
name|defaultName
argument_list|)
decl_stmt|;
name|names
operator|.
name|remove
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|strategies
operator|.
name|add
argument_list|(
name|newStrategy
argument_list|(
name|unique
argument_list|,
literal|false
argument_list|,
name|n
argument_list|,
name|m
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Mount
name|defMount
init|=
name|mountInfoProvider
operator|.
name|getDefaultMount
argument_list|()
decl_stmt|;
comment|// TODO what to do with non-default names that are not covered by
comment|// the mount?
for|for
control|(
name|String
name|n
range|:
name|names
control|)
block|{
name|strategies
operator|.
name|add
argument_list|(
name|newStrategy
argument_list|(
name|unique
argument_list|,
literal|true
argument_list|,
name|n
argument_list|,
name|defMount
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// default mount
name|strategies
operator|.
name|add
argument_list|(
name|newStrategy
argument_list|(
name|unique
argument_list|,
literal|true
argument_list|,
name|defaultName
argument_list|,
name|defMount
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|strategies
return|;
block|}
else|else
block|{
return|return
name|unique
condition|?
name|newHashSet
argument_list|(
name|newUniqueStrategy
argument_list|(
name|defaultName
argument_list|)
argument_list|)
else|:
name|newHashSet
argument_list|(
name|newMirrorStrategy
argument_list|(
name|defaultName
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|IndexStoreStrategy
name|newUniqueStrategy
parameter_list|(
name|String
name|defaultName
parameter_list|)
block|{
if|if
condition|(
name|INDEX_CONTENT_NODE_NAME
operator|.
name|equals
argument_list|(
name|defaultName
argument_list|)
condition|)
block|{
return|return
name|UNIQUE
return|;
block|}
else|else
block|{
return|return
operator|new
name|UniqueEntryStoreStrategy
argument_list|(
name|defaultName
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|IndexStoreStrategy
name|newMirrorStrategy
parameter_list|(
name|String
name|defaultName
parameter_list|)
block|{
if|if
condition|(
name|INDEX_CONTENT_NODE_NAME
operator|.
name|equals
argument_list|(
name|defaultName
argument_list|)
condition|)
block|{
return|return
name|MIRROR
return|;
block|}
else|else
block|{
return|return
operator|new
name|ContentMirrorStoreStrategy
argument_list|(
name|defaultName
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|IndexStoreStrategy
name|newStrategy
parameter_list|(
name|boolean
name|unique
parameter_list|,
name|boolean
name|defaultMount
parameter_list|,
name|String
name|name
parameter_list|,
name|Mount
name|m
parameter_list|)
block|{
name|Predicate
argument_list|<
name|String
argument_list|>
name|filter
init|=
name|newFilter
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|boolean
name|readOnly
init|=
name|unique
operator|&&
operator|!
name|m
operator|.
name|isDefault
argument_list|()
operator|&&
name|RO_PRIVATE_UNIQUE_INDEX
decl_stmt|;
return|return
name|unique
condition|?
operator|new
name|FilteringIndexStoreStrategy
argument_list|(
operator|new
name|UniqueEntryStoreStrategy
argument_list|(
name|name
argument_list|)
argument_list|,
name|filter
argument_list|,
name|readOnly
argument_list|)
else|:
operator|new
name|FilteringIndexStoreStrategy
argument_list|(
operator|new
name|ContentMirrorStoreStrategy
argument_list|(
name|name
argument_list|)
argument_list|,
name|filter
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Predicate
argument_list|<
name|String
argument_list|>
name|newFilter
parameter_list|(
specifier|final
name|Mount
name|m
parameter_list|)
block|{
return|return
operator|new
name|Predicate
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|String
name|p
parameter_list|)
block|{
return|return
name|m
operator|.
name|isMounted
argument_list|(
name|p
argument_list|)
return|;
block|}
block|}
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isIndexStorageNode
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|defaultName
parameter_list|)
block|{
return|return
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
operator|&&
operator|(
name|name
operator|.
name|equals
argument_list|(
name|defaultName
argument_list|)
operator|||
name|name
operator|.
name|endsWith
argument_list|(
name|asSuffix
argument_list|(
name|defaultName
argument_list|)
argument_list|)
operator|)
return|;
block|}
specifier|public
specifier|static
name|String
name|getIndexNodeName
parameter_list|(
name|MountInfoProvider
name|mountInfoProvider
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|defaultName
parameter_list|)
block|{
name|Mount
name|mount
init|=
name|mountInfoProvider
operator|.
name|getMountByPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|getNodeForMount
argument_list|(
name|mount
argument_list|,
name|defaultName
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|getNodeForMount
parameter_list|(
name|Mount
name|mount
parameter_list|,
name|String
name|defaultName
parameter_list|)
block|{
if|if
condition|(
name|mount
operator|.
name|isDefault
argument_list|()
condition|)
block|{
return|return
name|defaultName
return|;
block|}
return|return
literal|":"
operator|+
name|mount
operator|.
name|getPathFragmentName
argument_list|()
operator|+
name|asSuffix
argument_list|(
name|defaultName
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|asSuffix
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
literal|"-"
operator|+
name|stripStartingColon
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|stripStartingColon
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|":"
argument_list|)
condition|)
block|{
return|return
name|name
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
return|;
block|}
return|return
name|name
return|;
block|}
block|}
end_class

end_unit

