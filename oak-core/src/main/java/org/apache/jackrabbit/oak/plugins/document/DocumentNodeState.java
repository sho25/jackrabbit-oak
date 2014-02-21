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
name|document
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

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
name|Iterator
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
name|NoSuchElementException
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
name|Nonnull
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
name|cache
operator|.
name|CacheValue
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
name|json
operator|.
name|JsopBuilder
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
name|json
operator|.
name|JsopReader
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
name|json
operator|.
name|JsopTokenizer
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
name|commons
operator|.
name|json
operator|.
name|JsopWriter
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
name|kernel
operator|.
name|JsonSerializer
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
name|util
operator|.
name|Utils
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
name|memory
operator|.
name|EmptyNodeState
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
name|memory
operator|.
name|MemoryNodeBuilder
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
name|memory
operator|.
name|ModifiedNodeState
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
name|AbstractChildNodeEntry
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
name|AbstractNodeState
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
name|NodeStateDiff
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterators
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
import|;
end_import

begin_comment
comment|/**  * A {@link NodeState} implementation for the {@link DocumentNodeStore}.  */
end_comment

begin_class
class|class
name|DocumentNodeState
extends|extends
name|AbstractNodeState
implements|implements
name|CacheValue
block|{
specifier|public
specifier|static
specifier|final
name|Children
name|NO_CHILDREN
init|=
operator|new
name|Children
argument_list|()
decl_stmt|;
comment|/**      * The number of child nodes to fetch initially.      */
specifier|static
specifier|final
name|int
name|INITIAL_FETCH_SIZE
init|=
literal|100
decl_stmt|;
comment|/**      * The maximum number of child nodes to fetch in one call. (1600).      */
specifier|static
specifier|final
name|int
name|MAX_FETCH_SIZE
init|=
name|INITIAL_FETCH_SIZE
operator|<<
literal|4
decl_stmt|;
comment|/**      * Number of child nodes beyond which {@link DocumentNodeStore#}      * is used for diffing.      */
specifier|public
specifier|static
specifier|final
name|int
name|LOCAL_DIFF_THRESHOLD
init|=
literal|10
decl_stmt|;
specifier|private
specifier|final
name|DocumentNodeStore
name|store
decl_stmt|;
specifier|final
name|String
name|path
decl_stmt|;
specifier|final
name|Revision
name|rev
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
name|properties
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|Revision
name|lastRevision
decl_stmt|;
specifier|final
name|boolean
name|hasChildren
decl_stmt|;
comment|/**      * TODO: OAK-1056      */
specifier|private
name|boolean
name|isBranch
decl_stmt|;
name|DocumentNodeState
parameter_list|(
annotation|@
name|Nonnull
name|DocumentNodeStore
name|store
parameter_list|,
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|rev
parameter_list|)
block|{
name|this
argument_list|(
name|store
argument_list|,
name|path
argument_list|,
name|rev
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|DocumentNodeState
parameter_list|(
annotation|@
name|Nonnull
name|DocumentNodeStore
name|store
parameter_list|,
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|rev
parameter_list|,
name|boolean
name|hasChildren
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|checkNotNull
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|this
operator|.
name|rev
operator|=
name|checkNotNull
argument_list|(
name|rev
argument_list|)
expr_stmt|;
name|this
operator|.
name|hasChildren
operator|=
name|hasChildren
expr_stmt|;
block|}
name|Revision
name|getRevision
parameter_list|()
block|{
return|return
name|rev
return|;
block|}
name|DocumentNodeState
name|setBranch
parameter_list|()
block|{
name|isBranch
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
name|boolean
name|isBranch
parameter_list|()
block|{
return|return
name|isBranch
return|;
block|}
comment|//--------------------------< NodeState>-----------------------------------
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|that
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|that
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|that
operator|instanceof
name|DocumentNodeState
condition|)
block|{
name|DocumentNodeState
name|other
init|=
operator|(
name|DocumentNodeState
operator|)
name|that
decl_stmt|;
if|if
condition|(
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|lastRevision
operator|.
name|equals
argument_list|(
name|other
operator|.
name|lastRevision
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|that
operator|instanceof
name|ModifiedNodeState
condition|)
block|{
name|ModifiedNodeState
name|modified
init|=
operator|(
name|ModifiedNodeState
operator|)
name|that
decl_stmt|;
if|if
condition|(
name|modified
operator|.
name|getBaseState
argument_list|()
operator|==
name|this
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
name|that
operator|instanceof
name|NodeState
condition|)
block|{
return|return
name|AbstractNodeState
operator|.
name|equals
argument_list|(
name|this
argument_list|,
operator|(
name|NodeState
operator|)
name|that
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyState
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|properties
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|properties
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|PropertyState
argument_list|>
name|getProperties
parameter_list|()
block|{
return|return
name|properties
operator|.
name|values
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasChildNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
operator|!
name|hasChildren
operator|||
operator|!
name|isValidName
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|String
name|p
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|store
operator|.
name|getNode
argument_list|(
name|p
argument_list|,
name|lastRevision
argument_list|)
operator|!=
literal|null
return|;
block|}
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeState
name|getChildNode
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
operator|!
name|hasChildren
condition|)
block|{
name|checkValidName
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|EmptyNodeState
operator|.
name|MISSING_NODE
return|;
block|}
name|String
name|p
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|DocumentNodeState
name|child
init|=
name|store
operator|.
name|getNode
argument_list|(
name|p
argument_list|,
name|lastRevision
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|==
literal|null
condition|)
block|{
name|checkValidName
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|EmptyNodeState
operator|.
name|MISSING_NODE
return|;
block|}
else|else
block|{
return|return
name|child
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|getChildNodeCount
parameter_list|(
name|long
name|max
parameter_list|)
block|{
if|if
condition|(
operator|!
name|hasChildren
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|max
operator|>
name|DocumentNodeStore
operator|.
name|NUM_CHILDREN_CACHE_LIMIT
condition|)
block|{
comment|// count all
return|return
name|Iterators
operator|.
name|size
argument_list|(
operator|new
name|ChildNodeEntryIterator
argument_list|()
argument_list|)
return|;
block|}
name|Children
name|c
init|=
name|store
operator|.
name|getChildren
argument_list|(
name|this
argument_list|,
literal|null
argument_list|,
operator|(
name|int
operator|)
name|max
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|.
name|hasMore
condition|)
block|{
return|return
name|Long
operator|.
name|MAX_VALUE
return|;
block|}
else|else
block|{
comment|// we know the exact value
return|return
name|c
operator|.
name|children
operator|.
name|size
argument_list|()
return|;
block|}
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|getChildNodeEntries
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasChildren
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
return|return
operator|new
name|Iterable
argument_list|<
name|ChildNodeEntry
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ChildNodeEntry
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|ChildNodeEntryIterator
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeBuilder
name|builder
parameter_list|()
block|{
if|if
condition|(
name|isBranch
condition|)
block|{
return|return
operator|new
name|MemoryNodeBuilder
argument_list|(
name|this
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"/"
operator|.
name|equals
argument_list|(
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|DocumentRootBuilder
argument_list|(
name|this
argument_list|,
name|store
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|MemoryNodeBuilder
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|compareAgainstBaseState
parameter_list|(
name|NodeState
name|base
parameter_list|,
name|NodeStateDiff
name|diff
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|base
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|base
operator|==
name|EMPTY_NODE
operator|||
operator|!
name|base
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// special case
return|return
name|EmptyNodeState
operator|.
name|compareAgainstEmptyState
argument_list|(
name|this
argument_list|,
name|diff
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|base
operator|instanceof
name|DocumentNodeState
condition|)
block|{
name|DocumentNodeState
name|mBase
init|=
operator|(
name|DocumentNodeState
operator|)
name|base
decl_stmt|;
if|if
condition|(
name|store
operator|==
name|mBase
operator|.
name|store
condition|)
block|{
if|if
condition|(
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
name|mBase
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|lastRevision
operator|.
name|equals
argument_list|(
name|mBase
operator|.
name|lastRevision
argument_list|)
condition|)
block|{
comment|// no differences
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|getChildNodeCount
argument_list|(
name|LOCAL_DIFF_THRESHOLD
argument_list|)
operator|>
name|LOCAL_DIFF_THRESHOLD
condition|)
block|{
comment|// use DocumentNodeStore compare when there are many children
return|return
name|dispatch
argument_list|(
name|store
operator|.
name|diffChildren
argument_list|(
name|this
argument_list|,
name|mBase
argument_list|)
argument_list|,
name|mBase
argument_list|,
name|diff
argument_list|)
return|;
block|}
block|}
block|}
block|}
comment|// fall back to the generic node state diff algorithm
return|return
name|super
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
argument_list|,
name|diff
argument_list|)
return|;
block|}
name|void
name|setProperty
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|properties
operator|.
name|remove
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|properties
operator|.
name|put
argument_list|(
name|propertyName
argument_list|,
operator|new
name|DocumentPropertyState
argument_list|(
name|store
argument_list|,
name|propertyName
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|setProperty
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
name|properties
operator|.
name|put
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|,
name|property
argument_list|)
expr_stmt|;
block|}
name|String
name|getPropertyAsString
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
name|PropertyState
name|prop
init|=
name|properties
operator|.
name|get
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|prop
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|JsopBuilder
name|builder
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
operator|new
name|JsonSerializer
argument_list|(
name|builder
argument_list|,
name|store
operator|.
name|getBlobSerializer
argument_list|()
argument_list|)
operator|.
name|serialize
argument_list|(
name|prop
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|getPropertyNames
parameter_list|()
block|{
return|return
name|properties
operator|.
name|keySet
argument_list|()
return|;
block|}
name|void
name|copyTo
parameter_list|(
name|DocumentNodeState
name|newNode
parameter_list|)
block|{
name|newNode
operator|.
name|properties
operator|.
name|putAll
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
name|boolean
name|hasNoChildren
parameter_list|()
block|{
return|return
operator|!
name|hasChildren
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|"path: "
argument_list|)
operator|.
name|append
argument_list|(
name|path
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|"rev: "
argument_list|)
operator|.
name|append
argument_list|(
name|rev
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|properties
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Create an add node operation for this node.      */
name|UpdateOp
name|asOperation
parameter_list|(
name|boolean
name|isNew
parameter_list|)
block|{
name|String
name|id
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|UpdateOp
name|op
init|=
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
name|isNew
argument_list|)
decl_stmt|;
name|op
operator|.
name|set
argument_list|(
name|Document
operator|.
name|ID
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|NodeDocument
operator|.
name|setModified
argument_list|(
name|op
argument_list|,
name|rev
argument_list|)
expr_stmt|;
name|NodeDocument
operator|.
name|setDeleted
argument_list|(
name|op
argument_list|,
name|rev
argument_list|,
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|p
range|:
name|properties
operator|.
name|keySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|Utils
operator|.
name|escapePropertyName
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|op
operator|.
name|setMapEntry
argument_list|(
name|key
argument_list|,
name|rev
argument_list|,
name|getPropertyAsString
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|op
return|;
block|}
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
name|String
name|getId
parameter_list|()
block|{
return|return
name|path
operator|+
literal|"@"
operator|+
name|lastRevision
return|;
block|}
name|void
name|append
parameter_list|(
name|JsopWriter
name|json
parameter_list|,
name|boolean
name|includeId
parameter_list|)
block|{
if|if
condition|(
name|includeId
condition|)
block|{
name|json
operator|.
name|key
argument_list|(
literal|":id"
argument_list|)
operator|.
name|value
argument_list|(
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|p
range|:
name|properties
operator|.
name|keySet
argument_list|()
control|)
block|{
name|json
operator|.
name|key
argument_list|(
name|p
argument_list|)
operator|.
name|encodedValue
argument_list|(
name|getPropertyAsString
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|setLastRevision
parameter_list|(
name|Revision
name|lastRevision
parameter_list|)
block|{
name|this
operator|.
name|lastRevision
operator|=
name|lastRevision
expr_stmt|;
block|}
name|Revision
name|getLastRevision
parameter_list|()
block|{
return|return
name|lastRevision
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMemory
parameter_list|()
block|{
name|int
name|size
init|=
literal|180
operator|+
name|path
operator|.
name|length
argument_list|()
operator|*
literal|2
decl_stmt|;
comment|// rough approximation for properties
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
name|entry
range|:
name|properties
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|// name
name|size
operator|+=
literal|48
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|length
argument_list|()
operator|*
literal|2
expr_stmt|;
name|PropertyState
name|propState
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|propState
operator|.
name|getType
argument_list|()
operator|!=
name|Type
operator|.
name|BINARY
operator|&&
name|propState
operator|.
name|getType
argument_list|()
operator|!=
name|Type
operator|.
name|BINARIES
condition|)
block|{
comment|// assume binaries go into blob store
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|propState
operator|.
name|count
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|// size() returns length of string
comment|// overhead:
comment|// - 8 bytes per reference in values list
comment|// - 48 bytes per string
name|size
operator|+=
literal|56
operator|+
name|propState
operator|.
name|size
argument_list|(
name|i
argument_list|)
operator|*
literal|2
expr_stmt|;
block|}
block|}
block|}
return|return
name|size
return|;
block|}
comment|//------------------------------< internal>--------------------------------
specifier|private
name|boolean
name|dispatch
parameter_list|(
annotation|@
name|Nonnull
name|String
name|jsonDiff
parameter_list|,
annotation|@
name|Nonnull
name|DocumentNodeState
name|base
parameter_list|,
annotation|@
name|Nonnull
name|NodeStateDiff
name|diff
parameter_list|)
block|{
if|if
condition|(
operator|!
name|AbstractNodeState
operator|.
name|comparePropertiesAgainstBaseState
argument_list|(
name|this
argument_list|,
name|base
argument_list|,
name|diff
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|jsonDiff
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|JsopTokenizer
name|t
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|jsonDiff
argument_list|)
decl_stmt|;
name|boolean
name|continueComparison
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|continueComparison
condition|)
block|{
name|int
name|r
init|=
name|t
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|==
name|JsopReader
operator|.
name|END
condition|)
block|{
break|break;
block|}
switch|switch
condition|(
name|r
condition|)
block|{
case|case
literal|'+'
case|:
block|{
name|String
name|path
init|=
name|t
operator|.
name|readString
argument_list|()
decl_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
while|while
condition|(
name|t
operator|.
name|read
argument_list|()
operator|!=
literal|'}'
condition|)
block|{
comment|// skip properties
block|}
name|String
name|name
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|continueComparison
operator|=
name|diff
operator|.
name|childNodeAdded
argument_list|(
name|name
argument_list|,
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
literal|'-'
case|:
block|{
name|String
name|path
init|=
name|t
operator|.
name|readString
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|continueComparison
operator|=
name|diff
operator|.
name|childNodeDeleted
argument_list|(
name|name
argument_list|,
name|base
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
literal|'^'
case|:
block|{
name|String
name|path
init|=
name|t
operator|.
name|readString
argument_list|()
decl_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
name|t
operator|.
name|matches
argument_list|(
literal|'{'
argument_list|)
condition|)
block|{
name|t
operator|.
name|read
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|continueComparison
operator|=
name|diff
operator|.
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|base
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|,
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|t
operator|.
name|matches
argument_list|(
literal|'['
argument_list|)
condition|)
block|{
comment|// ignore multi valued property
while|while
condition|(
name|t
operator|.
name|read
argument_list|()
operator|!=
literal|']'
condition|)
block|{
comment|// skip values
block|}
block|}
else|else
block|{
comment|// ignore single valued property
name|t
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
break|break;
block|}
case|case
literal|'>'
case|:
block|{
name|String
name|from
init|=
name|t
operator|.
name|readString
argument_list|()
decl_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|String
name|to
init|=
name|t
operator|.
name|readString
argument_list|()
decl_stmt|;
name|String
name|fromName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|from
argument_list|)
decl_stmt|;
name|continueComparison
operator|=
name|diff
operator|.
name|childNodeDeleted
argument_list|(
name|fromName
argument_list|,
name|base
operator|.
name|getChildNode
argument_list|(
name|fromName
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|continueComparison
condition|)
block|{
break|break;
block|}
name|String
name|toName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|to
argument_list|)
decl_stmt|;
name|continueComparison
operator|=
name|diff
operator|.
name|childNodeAdded
argument_list|(
name|toName
argument_list|,
name|getChildNode
argument_list|(
name|toName
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"jsonDiff: illegal token '"
operator|+
name|t
operator|.
name|getToken
argument_list|()
operator|+
literal|"' at pos: "
operator|+
name|t
operator|.
name|getLastPos
argument_list|()
operator|+
literal|' '
operator|+
name|jsonDiff
argument_list|)
throw|;
block|}
block|}
return|return
name|continueComparison
return|;
block|}
comment|/**      * Returns up to {@code limit} child node entries, starting after the given      * {@code name}.      *      * @param name the name of the lower bound child node entry (exclusive) or      *             {@code null}, if the method should start with the first known      *             child node.      * @param limit the maximum number of child node entries to return.      * @return the child node entries.      */
annotation|@
name|Nonnull
specifier|private
name|Iterable
argument_list|<
name|ChildNodeEntry
argument_list|>
name|getChildNodeEntries
parameter_list|(
annotation|@
name|Nullable
name|String
name|name
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
name|Iterable
argument_list|<
name|DocumentNodeState
argument_list|>
name|children
init|=
name|store
operator|.
name|getChildNodes
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|limit
argument_list|)
decl_stmt|;
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|children
argument_list|,
operator|new
name|Function
argument_list|<
name|DocumentNodeState
argument_list|,
name|ChildNodeEntry
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ChildNodeEntry
name|apply
parameter_list|(
specifier|final
name|DocumentNodeState
name|input
parameter_list|)
block|{
return|return
operator|new
name|AbstractChildNodeEntry
argument_list|()
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|PathUtils
operator|.
name|getName
argument_list|(
name|input
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeState
name|getNodeState
parameter_list|()
block|{
return|return
name|input
return|;
block|}
block|}
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * A list of children for a node.      */
specifier|public
specifier|static
class|class
name|Children
implements|implements
name|CacheValue
block|{
specifier|final
name|ArrayList
argument_list|<
name|String
argument_list|>
name|children
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
name|hasMore
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|getMemory
parameter_list|()
block|{
name|int
name|size
init|=
literal|114
decl_stmt|;
for|for
control|(
name|String
name|c
range|:
name|children
control|)
block|{
name|size
operator|+=
name|c
operator|.
name|length
argument_list|()
operator|*
literal|2
operator|+
literal|56
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|children
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
specifier|private
class|class
name|ChildNodeEntryIterator
implements|implements
name|Iterator
argument_list|<
name|ChildNodeEntry
argument_list|>
block|{
specifier|private
name|String
name|previousName
decl_stmt|;
specifier|private
name|Iterator
argument_list|<
name|ChildNodeEntry
argument_list|>
name|current
decl_stmt|;
specifier|private
name|int
name|fetchSize
init|=
name|INITIAL_FETCH_SIZE
decl_stmt|;
specifier|private
name|int
name|currentRemaining
init|=
name|fetchSize
decl_stmt|;
name|ChildNodeEntryIterator
parameter_list|()
block|{
name|fetchMore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|current
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|currentRemaining
operator|>
literal|0
condition|)
block|{
comment|// current returned less than fetchSize
return|return
literal|false
return|;
block|}
name|fetchMore
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|ChildNodeEntry
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|ChildNodeEntry
name|entry
init|=
name|current
operator|.
name|next
argument_list|()
decl_stmt|;
name|previousName
operator|=
name|entry
operator|.
name|getName
argument_list|()
expr_stmt|;
name|currentRemaining
operator|--
expr_stmt|;
return|return
name|entry
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|private
name|void
name|fetchMore
parameter_list|()
block|{
name|Iterator
argument_list|<
name|ChildNodeEntry
argument_list|>
name|entries
init|=
name|getChildNodeEntries
argument_list|(
name|previousName
argument_list|,
name|fetchSize
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|currentRemaining
operator|=
name|fetchSize
expr_stmt|;
name|fetchSize
operator|=
name|Math
operator|.
name|min
argument_list|(
name|fetchSize
operator|*
literal|2
argument_list|,
name|MAX_FETCH_SIZE
argument_list|)
expr_stmt|;
if|if
condition|(
name|entries
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|current
operator|=
name|entries
expr_stmt|;
block|}
else|else
block|{
name|current
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

