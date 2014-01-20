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
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singleton
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
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
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
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
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
name|CommitFailedException
operator|.
name|CONSTRAINT
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
name|NAME
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
name|NAMES
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
name|commons
operator|.
name|PathUtils
operator|.
name|concat
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
name|DECLARING_NODE_TYPES
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
name|PROPERTY_NAMES
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
name|property
operator|.
name|PropertyIndex
operator|.
name|encode
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
name|jcr
operator|.
name|PropertyType
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
name|IndexEditor
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
name|plugins
operator|.
name|nodetype
operator|.
name|TypePredicate
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
name|Editor
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
name|query
operator|.
name|PropertyValues
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

begin_comment
comment|/**  * Index editor for keeping a property index up to date.  *   * @see PropertyIndex  * @see PropertyIndexLookup  */
end_comment

begin_class
class|class
name|PropertyIndexEditor
implements|implements
name|IndexEditor
block|{
comment|/** Index storage strategy */
specifier|private
specifier|static
specifier|final
name|IndexStoreStrategy
name|MIRROR
init|=
operator|new
name|ContentMirrorStoreStrategy
argument_list|()
decl_stmt|;
comment|/** Index storage strategy */
specifier|private
specifier|static
specifier|final
name|IndexStoreStrategy
name|UNIQUE
init|=
operator|new
name|UniqueEntryStoreStrategy
argument_list|()
decl_stmt|;
comment|/** Parent editor, or {@code null} if this is the root editor. */
specifier|private
specifier|final
name|PropertyIndexEditor
name|parent
decl_stmt|;
comment|/** Name of this node, or {@code null} for the root node. */
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/** Path of this editor, built lazily in {@link #getPath()}. */
specifier|private
name|String
name|path
decl_stmt|;
comment|/** Index definition node builder */
specifier|private
specifier|final
name|NodeBuilder
name|definition
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|propertyNames
decl_stmt|;
comment|/** Type predicate, or {@code null} if there are no type restrictions */
specifier|private
specifier|final
name|Predicate
argument_list|<
name|NodeState
argument_list|>
name|typePredicate
decl_stmt|;
comment|/**      * Keys to check for uniqueness, or {@code null} for no uniqueness checks.      */
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|keysToCheckForUniqueness
decl_stmt|;
comment|/**      * Flag to indicate whether the type of this node may have changed.      */
specifier|private
name|boolean
name|typeChanged
decl_stmt|;
comment|/**      * Matching property value keys from the before state. Lazily initialized.      */
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|beforeKeys
decl_stmt|;
comment|/**      * Matching property value keys from the after state. Lazily initialized.      */
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|afterKeys
decl_stmt|;
specifier|private
specifier|final
name|IndexUpdateCallback
name|updateCallback
decl_stmt|;
specifier|public
name|PropertyIndexEditor
parameter_list|(
name|NodeBuilder
name|definition
parameter_list|,
name|NodeState
name|root
parameter_list|,
name|IndexUpdateCallback
name|updateCallback
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|name
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|path
operator|=
literal|"/"
expr_stmt|;
name|this
operator|.
name|definition
operator|=
name|definition
expr_stmt|;
comment|// get property names
name|PropertyState
name|names
init|=
name|definition
operator|.
name|getProperty
argument_list|(
name|PROPERTY_NAMES
argument_list|)
decl_stmt|;
if|if
condition|(
name|names
operator|.
name|count
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// OAK-1273: optimize for the common case
name|this
operator|.
name|propertyNames
operator|=
name|singleton
argument_list|(
name|names
operator|.
name|getValue
argument_list|(
name|NAME
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|propertyNames
operator|=
name|newHashSet
argument_list|(
name|names
operator|.
name|getValue
argument_list|(
name|NAMES
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// get declaring types, and all their subtypes
comment|// TODO: should we reindex when type definitions change?
if|if
condition|(
name|definition
operator|.
name|hasProperty
argument_list|(
name|DECLARING_NODE_TYPES
argument_list|)
condition|)
block|{
name|this
operator|.
name|typePredicate
operator|=
operator|new
name|TypePredicate
argument_list|(
name|root
argument_list|,
name|definition
operator|.
name|getNames
argument_list|(
name|DECLARING_NODE_TYPES
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|typePredicate
operator|=
literal|null
expr_stmt|;
block|}
comment|// keep track of modified keys for uniqueness checks
if|if
condition|(
name|definition
operator|.
name|getBoolean
argument_list|(
name|IndexConstants
operator|.
name|UNIQUE_PROPERTY_NAME
argument_list|)
condition|)
block|{
name|this
operator|.
name|keysToCheckForUniqueness
operator|=
name|newHashSet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|keysToCheckForUniqueness
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|updateCallback
operator|=
name|updateCallback
expr_stmt|;
block|}
specifier|private
name|PropertyIndexEditor
parameter_list|(
name|PropertyIndexEditor
name|parent
parameter_list|,
name|String
name|name
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
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|path
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|definition
operator|=
name|parent
operator|.
name|definition
expr_stmt|;
name|this
operator|.
name|propertyNames
operator|=
name|parent
operator|.
name|propertyNames
expr_stmt|;
name|this
operator|.
name|typePredicate
operator|=
name|parent
operator|.
name|typePredicate
expr_stmt|;
name|this
operator|.
name|keysToCheckForUniqueness
operator|=
name|parent
operator|.
name|keysToCheckForUniqueness
expr_stmt|;
name|this
operator|.
name|updateCallback
operator|=
name|parent
operator|.
name|updateCallback
expr_stmt|;
block|}
comment|/**      * Returns the path of this node, building it lazily when first requested.      */
specifier|private
name|String
name|getPath
parameter_list|()
block|{
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|path
operator|=
name|concat
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
comment|/**      * Adds the encoded values of the given property to the given set.      * If the given set is uninitialized, i.e. {@code null}, then a new      * set is created for any values to be added. The set, possibly newly      * initialized, is returned.      *      * @param keys set of encoded values, or {@code null}      * @param property property whose values are to be added to the set      * @return set of encoded values, possibly initialized      */
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|addValueKeys
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|keys
parameter_list|,
name|PropertyState
name|property
parameter_list|)
block|{
if|if
condition|(
name|property
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
operator|!=
name|PropertyType
operator|.
name|BINARY
operator|&&
name|property
operator|.
name|count
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|keys
operator|==
literal|null
condition|)
block|{
name|keys
operator|=
name|newHashSet
argument_list|()
expr_stmt|;
block|}
name|keys
operator|.
name|addAll
argument_list|(
name|encode
argument_list|(
name|PropertyValues
operator|.
name|create
argument_list|(
name|property
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|keys
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getMatchingKeys
parameter_list|(
name|NodeState
name|state
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|propertyNames
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|keys
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|propertyName
range|:
name|propertyNames
control|)
block|{
name|PropertyState
name|property
init|=
name|state
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
name|keys
operator|=
name|addValueKeys
argument_list|(
name|keys
argument_list|,
name|property
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|keys
return|;
block|}
specifier|private
specifier|static
name|IndexStoreStrategy
name|getStrategy
parameter_list|(
name|boolean
name|unique
parameter_list|)
block|{
return|return
name|unique
condition|?
name|UNIQUE
else|:
name|MIRROR
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|enter
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|typeChanged
operator|=
operator|(
name|typePredicate
operator|==
literal|null
operator|)
expr_stmt|;
comment|// disables property name checks
name|beforeKeys
operator|=
literal|null
expr_stmt|;
name|afterKeys
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|leave
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
comment|// apply the type restrictions
if|if
condition|(
name|typePredicate
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|typeChanged
condition|)
block|{
comment|// possible type change, so ignore diff results and
comment|// just load all matching values from both states
name|beforeKeys
operator|=
name|getMatchingKeys
argument_list|(
name|before
argument_list|,
name|propertyNames
argument_list|)
expr_stmt|;
name|afterKeys
operator|=
name|getMatchingKeys
argument_list|(
name|after
argument_list|,
name|propertyNames
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|beforeKeys
operator|!=
literal|null
operator|&&
operator|!
name|typePredicate
operator|.
name|apply
argument_list|(
name|before
argument_list|)
condition|)
block|{
comment|// the before state doesn't match the type, so clear its values
name|beforeKeys
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|afterKeys
operator|!=
literal|null
operator|&&
operator|!
name|typePredicate
operator|.
name|apply
argument_list|(
name|after
argument_list|)
condition|)
block|{
comment|// the after state doesn't match the type, so clear its values
name|afterKeys
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|// if any changes were detected, update the index accordingly
if|if
condition|(
name|beforeKeys
operator|!=
literal|null
operator|||
name|afterKeys
operator|!=
literal|null
condition|)
block|{
comment|// first make sure that both the before and after sets are non-null
if|if
condition|(
name|beforeKeys
operator|==
literal|null
operator|||
operator|(
name|typePredicate
operator|!=
literal|null
operator|&&
operator|!
name|typePredicate
operator|.
name|apply
argument_list|(
name|before
argument_list|)
operator|)
condition|)
block|{
name|beforeKeys
operator|=
name|newHashSet
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|afterKeys
operator|==
literal|null
condition|)
block|{
name|afterKeys
operator|=
name|newHashSet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// both before and after matches found, remove duplicates
name|Set
argument_list|<
name|String
argument_list|>
name|sharedKeys
init|=
name|newHashSet
argument_list|(
name|beforeKeys
argument_list|)
decl_stmt|;
name|sharedKeys
operator|.
name|retainAll
argument_list|(
name|afterKeys
argument_list|)
expr_stmt|;
name|beforeKeys
operator|.
name|removeAll
argument_list|(
name|sharedKeys
argument_list|)
expr_stmt|;
name|afterKeys
operator|.
name|removeAll
argument_list|(
name|sharedKeys
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|beforeKeys
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|afterKeys
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|updateCallback
operator|.
name|indexUpdate
argument_list|()
expr_stmt|;
name|NodeBuilder
name|index
init|=
name|definition
operator|.
name|child
argument_list|(
name|INDEX_CONTENT_NODE_NAME
argument_list|)
decl_stmt|;
name|getStrategy
argument_list|(
name|keysToCheckForUniqueness
operator|!=
literal|null
argument_list|)
operator|.
name|update
argument_list|(
name|index
argument_list|,
name|getPath
argument_list|()
argument_list|,
name|beforeKeys
argument_list|,
name|afterKeys
argument_list|)
expr_stmt|;
if|if
condition|(
name|keysToCheckForUniqueness
operator|!=
literal|null
condition|)
block|{
name|keysToCheckForUniqueness
operator|.
name|addAll
argument_list|(
name|afterKeys
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
comment|// make sure that the index node exist, even with no content
name|definition
operator|.
name|child
argument_list|(
name|INDEX_CONTENT_NODE_NAME
argument_list|)
expr_stmt|;
comment|// check uniqueness constraints when leaving the root
if|if
condition|(
name|keysToCheckForUniqueness
operator|!=
literal|null
operator|&&
operator|!
name|keysToCheckForUniqueness
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|NodeState
name|indexMeta
init|=
name|definition
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|IndexStoreStrategy
name|s
init|=
name|getStrategy
argument_list|(
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|keysToCheckForUniqueness
control|)
block|{
if|if
condition|(
name|s
operator|.
name|count
argument_list|(
name|indexMeta
argument_list|,
name|singleton
argument_list|(
name|key
argument_list|)
argument_list|,
literal|2
argument_list|)
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CONSTRAINT
argument_list|,
literal|30
argument_list|,
literal|"Uniqueness constraint violated for key "
operator|+
name|key
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
specifier|private
name|boolean
name|isTypeProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|JCR_PRIMARYTYPE
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
name|JCR_MIXINTYPES
operator|.
name|equals
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
name|String
name|name
init|=
name|after
operator|.
name|getName
argument_list|()
decl_stmt|;
name|typeChanged
operator|=
name|typeChanged
operator|||
name|isTypeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|propertyNames
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|afterKeys
operator|=
name|addValueKeys
argument_list|(
name|afterKeys
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
name|String
name|name
init|=
name|after
operator|.
name|getName
argument_list|()
decl_stmt|;
name|typeChanged
operator|=
name|typeChanged
operator|||
name|isTypeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|propertyNames
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|beforeKeys
operator|=
name|addValueKeys
argument_list|(
name|beforeKeys
argument_list|,
name|before
argument_list|)
expr_stmt|;
name|afterKeys
operator|=
name|addValueKeys
argument_list|(
name|afterKeys
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
name|String
name|name
init|=
name|before
operator|.
name|getName
argument_list|()
decl_stmt|;
name|typeChanged
operator|=
name|typeChanged
operator|||
name|isTypeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|propertyNames
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|beforeKeys
operator|=
name|addValueKeys
argument_list|(
name|beforeKeys
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
operator|new
name|PropertyIndexEditor
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|childNodeChanged
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
return|return
operator|new
name|PropertyIndexEditor
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
return|return
operator|new
name|PropertyIndexEditor
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

