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
name|document
operator|.
name|Path
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
name|document
operator|.
name|DocumentNodeStore
operator|.
name|META_PROP_NAMES
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
name|BundlingHandler
block|{
specifier|private
specifier|final
name|BundledTypesRegistry
name|registry
decl_stmt|;
specifier|private
specifier|final
name|Path
name|path
decl_stmt|;
specifier|private
specifier|final
name|BundlingContext
name|ctx
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|nodeState
decl_stmt|;
specifier|public
name|BundlingHandler
parameter_list|(
name|BundledTypesRegistry
name|registry
parameter_list|)
block|{
name|this
argument_list|(
name|checkNotNull
argument_list|(
name|registry
argument_list|)
argument_list|,
name|BundlingContext
operator|.
name|NULL
argument_list|,
name|Path
operator|.
name|ROOT
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
specifier|private
name|BundlingHandler
parameter_list|(
name|BundledTypesRegistry
name|registry
parameter_list|,
name|BundlingContext
name|ctx
parameter_list|,
name|Path
name|path
parameter_list|,
name|NodeState
name|nodeState
parameter_list|)
block|{
name|this
operator|.
name|registry
operator|=
name|registry
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
name|this
operator|.
name|nodeState
operator|=
name|nodeState
expr_stmt|;
block|}
comment|/**      * Returns property path. For non bundling case this is the actual property name      * while for bundling case this is the relative path from bundling root      */
specifier|public
name|String
name|getPropertyPath
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
return|return
name|ctx
operator|.
name|isBundling
argument_list|()
condition|?
name|ctx
operator|.
name|getPropertyPath
argument_list|(
name|propertyName
argument_list|)
else|:
name|propertyName
return|;
block|}
comment|/**      * Returns true if and only if current node is bundled in another node      */
specifier|public
name|boolean
name|isBundledNode
parameter_list|()
block|{
return|return
name|ctx
operator|.
name|matcher
operator|.
name|depth
argument_list|()
operator|>
literal|0
return|;
block|}
comment|/**      * Returns absolute path of the current node      */
specifier|public
name|Path
name|getNodeFullPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
specifier|public
name|NodeState
name|getNodeState
parameter_list|()
block|{
return|return
name|nodeState
return|;
block|}
specifier|public
name|Set
argument_list|<
name|PropertyState
argument_list|>
name|getMetaProps
parameter_list|()
block|{
return|return
name|ctx
operator|.
name|metaProps
return|;
block|}
comment|/**      * Returns name of properties which needs to be removed or marked as deleted      */
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getRemovedProps
parameter_list|()
block|{
return|return
name|ctx
operator|.
name|removedProps
return|;
block|}
specifier|public
name|Path
name|getRootBundlePath
parameter_list|()
block|{
return|return
name|ctx
operator|.
name|isBundling
argument_list|()
condition|?
name|ctx
operator|.
name|bundlingPath
else|:
name|path
return|;
block|}
specifier|public
name|BundlingHandler
name|childAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|state
parameter_list|)
block|{
name|Path
name|childPath
init|=
name|childPath
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|BundlingContext
name|childContext
decl_stmt|;
name|Matcher
name|childMatcher
init|=
name|ctx
operator|.
name|matcher
operator|.
name|next
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|childMatcher
operator|.
name|isMatch
argument_list|()
condition|)
block|{
name|childContext
operator|=
name|createChildContext
argument_list|(
name|childMatcher
argument_list|)
expr_stmt|;
name|childContext
operator|.
name|addMetaProp
argument_list|(
name|createProperty
argument_list|(
name|DocumentBundlor
operator|.
name|META_PROP_BUNDLING_PATH
argument_list|,
name|childMatcher
operator|.
name|getMatchedPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|DocumentBundlor
name|bundlor
init|=
name|registry
operator|.
name|getBundlor
argument_list|(
name|state
argument_list|)
decl_stmt|;
if|if
condition|(
name|bundlor
operator|!=
literal|null
condition|)
block|{
name|PropertyState
name|bundlorConfig
init|=
name|bundlor
operator|.
name|asPropertyState
argument_list|()
decl_stmt|;
name|childContext
operator|=
operator|new
name|BundlingContext
argument_list|(
name|childPath
argument_list|,
name|bundlor
operator|.
name|createMatcher
argument_list|()
argument_list|)
expr_stmt|;
name|childContext
operator|.
name|addMetaProp
argument_list|(
name|bundlorConfig
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|childContext
operator|=
name|BundlingContext
operator|.
name|NULL
expr_stmt|;
block|}
block|}
return|return
operator|new
name|BundlingHandler
argument_list|(
name|registry
argument_list|,
name|childContext
argument_list|,
name|childPath
argument_list|,
name|state
argument_list|)
return|;
block|}
specifier|public
name|BundlingHandler
name|childDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|state
parameter_list|)
block|{
name|Path
name|childPath
init|=
name|childPath
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|BundlingContext
name|childContext
decl_stmt|;
name|Matcher
name|childMatcher
init|=
name|ctx
operator|.
name|matcher
operator|.
name|next
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|childMatcher
operator|.
name|isMatch
argument_list|()
condition|)
block|{
name|childContext
operator|=
name|createChildContext
argument_list|(
name|childMatcher
argument_list|)
expr_stmt|;
name|removeDeletedChildProperties
argument_list|(
name|state
argument_list|,
name|childContext
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|childContext
operator|=
name|getBundlorContext
argument_list|(
name|childPath
argument_list|,
name|state
argument_list|)
expr_stmt|;
if|if
condition|(
name|childContext
operator|.
name|isBundling
argument_list|()
condition|)
block|{
name|removeBundlingMetaProps
argument_list|(
name|state
argument_list|,
name|childContext
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|BundlingHandler
argument_list|(
name|registry
argument_list|,
name|childContext
argument_list|,
name|childPath
argument_list|,
name|state
argument_list|)
return|;
block|}
specifier|public
name|BundlingHandler
name|childChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|Path
name|childPath
init|=
name|childPath
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|BundlingContext
name|childContext
decl_stmt|;
name|Matcher
name|childMatcher
init|=
name|ctx
operator|.
name|matcher
operator|.
name|next
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|childMatcher
operator|.
name|isMatch
argument_list|()
condition|)
block|{
name|childContext
operator|=
name|createChildContext
argument_list|(
name|childMatcher
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//Use the before state for looking up bundlor config
comment|//as after state may have been recreated all together
comment|//and bundlor config might have got lost
name|childContext
operator|=
name|getBundlorContext
argument_list|(
name|childPath
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BundlingHandler
argument_list|(
name|registry
argument_list|,
name|childContext
argument_list|,
name|childPath
argument_list|,
name|after
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isBundlingRoot
parameter_list|()
block|{
if|if
condition|(
name|ctx
operator|.
name|isBundling
argument_list|()
condition|)
block|{
return|return
name|ctx
operator|.
name|bundlingPath
operator|.
name|equals
argument_list|(
name|path
argument_list|)
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|result
init|=
name|path
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|isBundledNode
argument_list|()
condition|)
block|{
name|result
operator|=
name|path
operator|+
literal|"( Bundling root - "
operator|+
name|getRootBundlePath
argument_list|()
operator|+
literal|")"
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|Path
name|childPath
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
return|;
block|}
specifier|private
name|BundlingContext
name|createChildContext
parameter_list|(
name|Matcher
name|childMatcher
parameter_list|)
block|{
return|return
name|ctx
operator|.
name|child
argument_list|(
name|childMatcher
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|BundlingContext
name|getBundlorContext
parameter_list|(
name|Path
name|path
parameter_list|,
name|NodeState
name|state
parameter_list|)
block|{
name|BundlingContext
name|result
init|=
name|BundlingContext
operator|.
name|NULL
decl_stmt|;
name|PropertyState
name|bundlorConfig
init|=
name|state
operator|.
name|getProperty
argument_list|(
name|DocumentBundlor
operator|.
name|META_PROP_PATTERN
argument_list|)
decl_stmt|;
if|if
condition|(
name|bundlorConfig
operator|!=
literal|null
condition|)
block|{
name|DocumentBundlor
name|bundlor
init|=
name|DocumentBundlor
operator|.
name|from
argument_list|(
name|bundlorConfig
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|BundlingContext
argument_list|(
name|path
argument_list|,
name|bundlor
operator|.
name|createMatcher
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
specifier|static
name|void
name|removeDeletedChildProperties
parameter_list|(
name|NodeState
name|state
parameter_list|,
name|BundlingContext
name|childContext
parameter_list|)
block|{
name|removeBundlingMetaProps
argument_list|(
name|state
argument_list|,
name|childContext
argument_list|)
expr_stmt|;
for|for
control|(
name|PropertyState
name|ps
range|:
name|state
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|childContext
operator|.
name|removeProperty
argument_list|(
name|ps
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|removeBundlingMetaProps
parameter_list|(
name|NodeState
name|state
parameter_list|,
name|BundlingContext
name|childContext
parameter_list|)
block|{
comment|//Explicitly remove meta prop related to bundling as it would not
comment|//be part of normal listing of properties and hence would not be deleted
comment|//as part of diff
for|for
control|(
name|String
name|name
range|:
name|META_PROP_NAMES
control|)
block|{
if|if
condition|(
name|state
operator|.
name|hasProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|childContext
operator|.
name|removeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
class|class
name|BundlingContext
block|{
specifier|static
specifier|final
name|BundlingContext
name|NULL
init|=
operator|new
name|BundlingContext
argument_list|(
name|Path
operator|.
name|ROOT
argument_list|,
name|Matcher
operator|.
name|NON_MATCHING
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|bundlingPath
decl_stmt|;
specifier|final
name|Matcher
name|matcher
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|PropertyState
argument_list|>
name|metaProps
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|removedProps
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|public
name|BundlingContext
parameter_list|(
name|Path
name|bundlingPath
parameter_list|,
name|Matcher
name|matcher
parameter_list|)
block|{
name|this
operator|.
name|bundlingPath
operator|=
name|bundlingPath
expr_stmt|;
name|this
operator|.
name|matcher
operator|=
name|matcher
expr_stmt|;
block|}
specifier|public
name|BundlingContext
name|child
parameter_list|(
name|Matcher
name|matcher
parameter_list|)
block|{
return|return
operator|new
name|BundlingContext
argument_list|(
name|bundlingPath
argument_list|,
name|matcher
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isBundling
parameter_list|()
block|{
return|return
name|matcher
operator|.
name|isMatch
argument_list|()
return|;
block|}
specifier|public
name|String
name|getPropertyPath
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|matcher
operator|.
name|getMatchedPath
argument_list|()
argument_list|,
name|propertyName
argument_list|)
return|;
block|}
specifier|public
name|void
name|addMetaProp
parameter_list|(
name|PropertyState
name|state
parameter_list|)
block|{
name|metaProps
operator|.
name|add
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|removedProps
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

