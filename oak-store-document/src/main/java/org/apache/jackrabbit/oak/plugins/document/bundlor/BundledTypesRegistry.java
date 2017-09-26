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
name|document
operator|.
name|bundlor
package|;
end_package

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
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|ImmutableMap
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
name|Maps
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
name|JcrConstants
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
name|spi
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
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
import|import static
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
operator|.
name|STRINGS
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|memory
operator|.
name|PropertyStates
operator|.
name|createProperty
import|;
end_import

begin_class
specifier|public
class|class
name|BundledTypesRegistry
block|{
specifier|public
specifier|static
name|BundledTypesRegistry
name|NOOP
init|=
name|BundledTypesRegistry
operator|.
name|from
argument_list|(
name|EMPTY_NODE
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DocumentBundlor
argument_list|>
name|bundlors
decl_stmt|;
specifier|public
name|BundledTypesRegistry
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|DocumentBundlor
argument_list|>
name|bundlors
parameter_list|)
block|{
name|this
operator|.
name|bundlors
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|bundlors
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|BundledTypesRegistry
name|from
parameter_list|(
name|NodeState
name|configParentState
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|DocumentBundlor
argument_list|>
name|bundlors
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|e
range|:
name|configParentState
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|NodeState
name|config
init|=
name|e
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
if|if
condition|(
name|config
operator|.
name|getBoolean
argument_list|(
name|DocumentBundlor
operator|.
name|PROP_DISABLED
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|bundlors
operator|.
name|put
argument_list|(
name|e
operator|.
name|getName
argument_list|()
argument_list|,
name|DocumentBundlor
operator|.
name|from
argument_list|(
name|config
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BundledTypesRegistry
argument_list|(
name|bundlors
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|DocumentBundlor
name|getBundlor
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
if|if
condition|(
name|isVersionedNode
argument_list|(
name|state
argument_list|)
condition|)
block|{
return|return
name|getBundlorForVersionedNode
argument_list|(
name|state
argument_list|)
return|;
block|}
comment|//Prefer mixin (as they are more specific) over primaryType
for|for
control|(
name|String
name|mixin
range|:
name|getMixinNames
argument_list|(
name|state
argument_list|,
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|)
control|)
block|{
name|DocumentBundlor
name|bundlor
init|=
name|bundlors
operator|.
name|get
argument_list|(
name|mixin
argument_list|)
decl_stmt|;
if|if
condition|(
name|bundlor
operator|!=
literal|null
condition|)
block|{
return|return
name|bundlor
return|;
block|}
block|}
return|return
name|bundlors
operator|.
name|get
argument_list|(
name|getPrimaryTypeName
argument_list|(
name|state
argument_list|,
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|DocumentBundlor
name|getBundlorForVersionedNode
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
comment|//Prefer mixin (as they are more specific) over primaryType
for|for
control|(
name|String
name|mixin
range|:
name|getMixinNames
argument_list|(
name|state
argument_list|,
name|JcrConstants
operator|.
name|JCR_FROZENMIXINTYPES
argument_list|)
control|)
block|{
name|DocumentBundlor
name|bundlor
init|=
name|bundlors
operator|.
name|get
argument_list|(
name|mixin
argument_list|)
decl_stmt|;
if|if
condition|(
name|bundlor
operator|!=
literal|null
condition|)
block|{
return|return
name|bundlor
return|;
block|}
block|}
return|return
name|bundlors
operator|.
name|get
argument_list|(
name|getPrimaryTypeName
argument_list|(
name|state
argument_list|,
name|JcrConstants
operator|.
name|JCR_FROZENPRIMARYTYPE
argument_list|)
argument_list|)
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|DocumentBundlor
argument_list|>
name|getBundlors
parameter_list|()
block|{
return|return
name|bundlors
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isVersionedNode
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
return|return
name|JcrConstants
operator|.
name|NT_FROZENNODE
operator|.
name|equals
argument_list|(
name|getPrimaryTypeName
argument_list|(
name|state
argument_list|,
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|getPrimaryTypeName
parameter_list|(
name|NodeState
name|nodeState
parameter_list|,
name|String
name|typePropName
parameter_list|)
block|{
name|PropertyState
name|ps
init|=
name|nodeState
operator|.
name|getProperty
argument_list|(
name|typePropName
argument_list|)
decl_stmt|;
return|return
operator|(
name|ps
operator|==
literal|null
operator|)
condition|?
name|JcrConstants
operator|.
name|NT_BASE
else|:
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAME
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Iterable
argument_list|<
name|String
argument_list|>
name|getMixinNames
parameter_list|(
name|NodeState
name|nodeState
parameter_list|,
name|String
name|typePropName
parameter_list|)
block|{
name|PropertyState
name|ps
init|=
name|nodeState
operator|.
name|getProperty
argument_list|(
name|typePropName
argument_list|)
decl_stmt|;
return|return
operator|(
name|ps
operator|==
literal|null
operator|)
condition|?
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
else|:
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|)
return|;
block|}
comment|//~--------------------------------------------< Builder>
specifier|public
specifier|static
name|BundledTypesRegistryBuilder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|BundledTypesRegistryBuilder
argument_list|(
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
class|class
name|BundledTypesRegistryBuilder
block|{
specifier|private
specifier|final
name|NodeBuilder
name|builder
decl_stmt|;
specifier|public
name|BundledTypesRegistryBuilder
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
block|}
specifier|public
name|TypeBuilder
name|forType
parameter_list|(
name|String
name|typeName
parameter_list|)
block|{
name|NodeBuilder
name|child
init|=
name|builder
operator|.
name|child
argument_list|(
name|typeName
argument_list|)
decl_stmt|;
name|child
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
return|return
operator|new
name|TypeBuilder
argument_list|(
name|this
argument_list|,
name|child
argument_list|)
return|;
block|}
specifier|public
name|TypeBuilder
name|forType
parameter_list|(
name|String
name|typeName
parameter_list|,
name|String
modifier|...
name|includes
parameter_list|)
block|{
name|TypeBuilder
name|typeBuilder
init|=
name|forType
argument_list|(
name|typeName
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|include
range|:
name|includes
control|)
block|{
name|typeBuilder
operator|.
name|include
argument_list|(
name|include
argument_list|)
expr_stmt|;
block|}
return|return
name|typeBuilder
return|;
block|}
specifier|public
name|BundledTypesRegistry
name|buildRegistry
parameter_list|()
block|{
return|return
name|BundledTypesRegistry
operator|.
name|from
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|NodeState
name|build
parameter_list|()
block|{
return|return
name|builder
operator|.
name|getNodeState
argument_list|()
return|;
block|}
specifier|public
specifier|static
class|class
name|TypeBuilder
block|{
specifier|private
specifier|final
name|BundledTypesRegistryBuilder
name|parent
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|typeBuilder
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|patterns
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|private
name|TypeBuilder
parameter_list|(
name|BundledTypesRegistryBuilder
name|parent
parameter_list|,
name|NodeBuilder
name|typeBuilder
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|typeBuilder
operator|=
name|typeBuilder
expr_stmt|;
block|}
specifier|public
name|TypeBuilder
name|include
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
name|patterns
operator|.
name|add
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|BundledTypesRegistry
name|buildRegistry
parameter_list|()
block|{
name|setupPatternProp
argument_list|()
expr_stmt|;
return|return
name|parent
operator|.
name|buildRegistry
argument_list|()
return|;
block|}
specifier|public
name|BundledTypesRegistryBuilder
name|registry
parameter_list|()
block|{
name|setupPatternProp
argument_list|()
expr_stmt|;
return|return
name|parent
return|;
block|}
specifier|public
name|NodeState
name|build
parameter_list|()
block|{
name|setupPatternProp
argument_list|()
expr_stmt|;
return|return
name|parent
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
name|void
name|setupPatternProp
parameter_list|()
block|{
name|typeBuilder
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|DocumentBundlor
operator|.
name|PROP_PATTERN
argument_list|,
name|patterns
argument_list|,
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

