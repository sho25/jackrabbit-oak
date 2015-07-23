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
name|reference
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
name|ImmutableSet
operator|.
name|of
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
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
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
name|emptySet
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|REFERENCE
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|WEAKREFERENCE
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
name|JCR_UUID
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
name|INTEGRITY
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
name|STRING
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
name|commons
operator|.
name|PathUtils
operator|.
name|isAbsolute
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
name|reference
operator|.
name|NodeReferenceConstants
operator|.
name|REF_NAME
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
name|reference
operator|.
name|NodeReferenceConstants
operator|.
name|WEAK_REF_NAME
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
name|MISSING_NODE
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
name|version
operator|.
name|VersionConstants
operator|.
name|VERSION_STORE_PATH
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
name|Map
operator|.
name|Entry
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
name|spi
operator|.
name|commit
operator|.
name|DefaultEditor
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

begin_comment
comment|/**  * Index editor for keeping a references to a node up to date.  *   */
end_comment

begin_class
class|class
name|ReferenceEditor
extends|extends
name|DefaultEditor
implements|implements
name|IndexEditor
block|{
specifier|private
specifier|static
specifier|final
name|ContentMirrorStoreStrategy
name|STORE
init|=
operator|new
name|ContentMirrorStoreStrategy
argument_list|()
decl_stmt|;
comment|/** Parent editor, or {@code null} if this is the root editor. */
specifier|private
specifier|final
name|ReferenceEditor
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
specifier|private
specifier|final
name|NodeState
name|root
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|definition
decl_stmt|;
comment|/**      *<UUID, Set<paths-pointing-to-the-uuid>>      */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|newRefs
decl_stmt|;
comment|/**      *<UUID, Set<paths-pointing-to-the-uuid>>      */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|rmRefs
decl_stmt|;
comment|/**      *<UUID, Set<paths-pointing-to-the-uuid>>      */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|newWeakRefs
decl_stmt|;
comment|/**      *<UUID, Set<paths-pointing-to-the-uuid>>      */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|rmWeakRefs
decl_stmt|;
comment|/**      * set of removed Ids of nodes that have a :reference property. These UUIDs      * need to be verified in the #after call      */
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|rmIds
decl_stmt|;
comment|/**      * set of ids that were added during this commit. we need it to reconcile      * moves      */
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|newIds
decl_stmt|;
comment|/**      * flag marking a reindex, case in which we don't need to keep track of the      * newIds set      */
specifier|private
name|boolean
name|isReindex
decl_stmt|;
specifier|public
name|ReferenceEditor
parameter_list|(
name|NodeBuilder
name|definition
parameter_list|,
name|NodeState
name|root
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
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|newRefs
operator|=
name|newHashMap
argument_list|()
expr_stmt|;
name|this
operator|.
name|rmRefs
operator|=
name|newHashMap
argument_list|()
expr_stmt|;
name|this
operator|.
name|newWeakRefs
operator|=
name|newHashMap
argument_list|()
expr_stmt|;
name|this
operator|.
name|rmWeakRefs
operator|=
name|newHashMap
argument_list|()
expr_stmt|;
name|this
operator|.
name|rmIds
operator|=
name|newHashSet
argument_list|()
expr_stmt|;
name|this
operator|.
name|newIds
operator|=
name|newHashSet
argument_list|()
expr_stmt|;
block|}
specifier|private
name|ReferenceEditor
parameter_list|(
name|ReferenceEditor
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
name|root
operator|=
name|parent
operator|.
name|root
expr_stmt|;
name|this
operator|.
name|newRefs
operator|=
name|parent
operator|.
name|newRefs
expr_stmt|;
name|this
operator|.
name|rmRefs
operator|=
name|parent
operator|.
name|rmRefs
expr_stmt|;
name|this
operator|.
name|newWeakRefs
operator|=
name|parent
operator|.
name|newWeakRefs
expr_stmt|;
name|this
operator|.
name|rmWeakRefs
operator|=
name|parent
operator|.
name|rmWeakRefs
expr_stmt|;
name|this
operator|.
name|rmIds
operator|=
name|parent
operator|.
name|rmIds
expr_stmt|;
name|this
operator|.
name|newIds
operator|=
name|parent
operator|.
name|newIds
expr_stmt|;
name|this
operator|.
name|isReindex
operator|=
name|parent
operator|.
name|isReindex
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
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|MISSING_NODE
operator|==
name|before
operator|&&
name|parent
operator|==
literal|null
condition|)
block|{
name|isReindex
operator|=
literal|true
expr_stmt|;
block|}
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
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
comment|// update references
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|ref
range|:
name|rmRefs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|uuid
init|=
name|ref
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|rm
init|=
name|ref
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|add
init|=
name|emptySet
argument_list|()
decl_stmt|;
if|if
condition|(
name|newRefs
operator|.
name|containsKey
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
name|add
operator|=
name|newRefs
operator|.
name|remove
argument_list|(
name|uuid
argument_list|)
expr_stmt|;
block|}
name|update
argument_list|(
name|definition
argument_list|,
name|REF_NAME
argument_list|,
name|uuid
argument_list|,
name|add
argument_list|,
name|rm
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|ref
range|:
name|newRefs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|uuid
init|=
name|ref
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|rmIds
operator|.
name|contains
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|add
init|=
name|ref
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|rm
init|=
name|emptySet
argument_list|()
decl_stmt|;
name|update
argument_list|(
name|definition
argument_list|,
name|REF_NAME
argument_list|,
name|uuid
argument_list|,
name|add
argument_list|,
name|rm
argument_list|)
expr_stmt|;
block|}
name|checkReferentialIntegrity
argument_list|(
name|root
argument_list|,
name|definition
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|Sets
operator|.
name|difference
argument_list|(
name|rmIds
argument_list|,
name|newIds
argument_list|)
argument_list|)
expr_stmt|;
comment|// update weak references
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|ref
range|:
name|rmWeakRefs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|uuid
init|=
name|ref
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|rm
init|=
name|ref
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|add
init|=
name|emptySet
argument_list|()
decl_stmt|;
if|if
condition|(
name|newWeakRefs
operator|.
name|containsKey
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
name|add
operator|=
name|newWeakRefs
operator|.
name|remove
argument_list|(
name|uuid
argument_list|)
expr_stmt|;
block|}
name|update
argument_list|(
name|definition
argument_list|,
name|WEAK_REF_NAME
argument_list|,
name|uuid
argument_list|,
name|add
argument_list|,
name|rm
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|ref
range|:
name|newWeakRefs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|uuid
init|=
name|ref
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|add
init|=
name|ref
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|rm
init|=
name|emptySet
argument_list|()
decl_stmt|;
name|update
argument_list|(
name|definition
argument_list|,
name|WEAK_REF_NAME
argument_list|,
name|uuid
argument_list|,
name|add
argument_list|,
name|rm
argument_list|)
expr_stmt|;
block|}
block|}
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
name|propertyChanged
argument_list|(
literal|null
argument_list|,
name|after
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|before
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|before
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
operator|==
name|REFERENCE
condition|)
block|{
if|if
condition|(
operator|!
name|isVersionStorePath
argument_list|(
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|put
argument_list|(
name|rmRefs
argument_list|,
name|before
operator|.
name|getValue
argument_list|(
name|STRINGS
argument_list|)
argument_list|,
name|concat
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|before
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|before
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
operator|==
name|WEAKREFERENCE
condition|)
block|{
name|put
argument_list|(
name|rmWeakRefs
argument_list|,
name|before
operator|.
name|getValue
argument_list|(
name|STRINGS
argument_list|)
argument_list|,
name|concat
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|before
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|JCR_UUID
operator|.
name|equals
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
comment|// node remove + add -> changed uuid
name|rmIds
operator|.
name|add
argument_list|(
name|before
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|after
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|after
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
operator|==
name|REFERENCE
condition|)
block|{
if|if
condition|(
operator|!
name|isVersionStorePath
argument_list|(
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|put
argument_list|(
name|newRefs
argument_list|,
name|after
operator|.
name|getValue
argument_list|(
name|STRINGS
argument_list|)
argument_list|,
name|concat
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|after
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|after
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
operator|==
name|WEAKREFERENCE
condition|)
block|{
name|put
argument_list|(
name|newWeakRefs
argument_list|,
name|after
operator|.
name|getValue
argument_list|(
name|STRINGS
argument_list|)
argument_list|,
name|concat
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|after
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|JCR_UUID
operator|.
name|equals
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
comment|// node remove + add -> changed uuid
name|newIds
operator|.
name|add
argument_list|(
name|after
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|propertyChanged
argument_list|(
name|before
argument_list|,
literal|null
argument_list|)
expr_stmt|;
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
name|String
name|uuid
init|=
name|after
operator|.
name|getString
argument_list|(
name|JCR_UUID
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|isReindex
operator|&&
name|uuid
operator|!=
literal|null
condition|)
block|{
name|newIds
operator|.
name|add
argument_list|(
name|uuid
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ReferenceEditor
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
name|ReferenceEditor
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
throws|throws
name|CommitFailedException
block|{
name|String
name|uuid
init|=
name|before
operator|.
name|getString
argument_list|(
name|JCR_UUID
argument_list|)
decl_stmt|;
if|if
condition|(
name|uuid
operator|!=
literal|null
condition|)
block|{
name|rmIds
operator|.
name|add
argument_list|(
name|uuid
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ReferenceEditor
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
return|;
block|}
comment|// ---------- Utils -----------------------------------------
specifier|private
specifier|static
name|boolean
name|isVersionStorePath
parameter_list|(
name|String
name|oakPath
parameter_list|)
block|{
return|return
name|oakPath
operator|!=
literal|null
operator|&&
name|oakPath
operator|.
name|startsWith
argument_list|(
name|VERSION_STORE_PATH
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|put
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|map
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|keys
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|String
name|asRelative
init|=
name|isAbsolute
argument_list|(
name|value
argument_list|)
condition|?
name|value
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
else|:
name|value
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|keys
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|values
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|values
operator|=
name|newHashSet
argument_list|()
expr_stmt|;
block|}
name|values
operator|.
name|add
argument_list|(
name|asRelative
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|update
parameter_list|(
name|NodeBuilder
name|child
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|key
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|add
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|rm
parameter_list|)
block|{
name|NodeBuilder
name|index
init|=
name|child
operator|.
name|child
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|empty
init|=
name|of
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|rm
control|)
block|{
name|STORE
operator|.
name|update
argument_list|(
name|index
argument_list|,
name|p
argument_list|,
name|name
argument_list|,
name|child
argument_list|,
name|of
argument_list|(
name|key
argument_list|)
argument_list|,
name|empty
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|p
range|:
name|add
control|)
block|{
comment|// TODO do we still need to encode the values?
name|STORE
operator|.
name|update
argument_list|(
name|index
argument_list|,
name|p
argument_list|,
name|name
argument_list|,
name|child
argument_list|,
name|empty
argument_list|,
name|of
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|hasReferences
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|NodeState
name|definition
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|key
parameter_list|)
block|{
return|return
name|definition
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
operator|&&
name|STORE
operator|.
name|count
argument_list|(
name|root
argument_list|,
name|definition
argument_list|,
name|name
argument_list|,
name|of
argument_list|(
name|key
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|>
literal|0
return|;
block|}
specifier|private
specifier|static
name|void
name|checkReferentialIntegrity
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|NodeState
name|definition
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|idsOfRemovedNodes
parameter_list|)
throws|throws
name|CommitFailedException
block|{
for|for
control|(
name|String
name|id
range|:
name|idsOfRemovedNodes
control|)
block|{
if|if
condition|(
name|hasReferences
argument_list|(
name|root
argument_list|,
name|definition
argument_list|,
name|REF_NAME
argument_list|,
name|id
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|INTEGRITY
argument_list|,
literal|1
argument_list|,
literal|"Unable to delete referenced node"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

