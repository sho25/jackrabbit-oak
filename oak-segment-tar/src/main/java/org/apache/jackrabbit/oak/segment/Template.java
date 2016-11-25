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
name|segment
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
name|base
operator|.
name|Preconditions
operator|.
name|checkElementIndex
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
name|Preconditions
operator|.
name|checkState
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
name|segment
operator|.
name|Segment
operator|.
name|RECORD_ID_BYTES
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
name|segment
operator|.
name|CacheWeights
operator|.
name|OBJECT_HEADER_SIZE
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|List
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Objects
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
name|Lists
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
name|commons
operator|.
name|StringUtils
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
name|MemoryChildNodeEntry
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
name|NodeState
import|;
end_import

begin_comment
comment|/**  * The in-memory representation of a "hidden class" of a node; inspired by the  * Chrome V8 Javascript engine).  *<p>  * Templates are always read fully in-memory.  */
end_comment

begin_class
specifier|public
class|class
name|Template
block|{
specifier|static
specifier|final
name|short
name|ZERO_CHILD_NODES_TYPE
init|=
literal|0
decl_stmt|;
specifier|static
specifier|final
name|short
name|SINGLE_CHILD_NODE_TYPE
init|=
literal|1
decl_stmt|;
specifier|static
specifier|final
name|short
name|MANY_CHILD_NODES_TYPE
init|=
literal|2
decl_stmt|;
specifier|static
specifier|final
name|String
name|ZERO_CHILD_NODES
init|=
literal|null
decl_stmt|;
specifier|static
specifier|final
name|String
name|MANY_CHILD_NODES
init|=
literal|""
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|SegmentReader
name|reader
decl_stmt|;
comment|/**      * The {@code jcr:primaryType} property, if present as a single-valued      * {@code NAME} property. Otherwise {@code null}.      */
annotation|@
name|CheckForNull
specifier|private
specifier|final
name|PropertyState
name|primaryType
decl_stmt|;
comment|/**      * The {@code jcr:mixinTypes} property, if present as a multi-valued      * {@code NAME} property. Otherwise {@code null}.      */
annotation|@
name|CheckForNull
specifier|private
specifier|final
name|PropertyState
name|mixinTypes
decl_stmt|;
comment|/**      * Templates of all the properties of a node, excluding the      * above-mentioned {@code NAME}-valued type properties, if any.      */
annotation|@
name|Nonnull
specifier|private
specifier|final
name|PropertyTemplate
index|[]
name|properties
decl_stmt|;
comment|/**      * Name of the single child node, if the node contains just one child.      * Otherwise {@link #ZERO_CHILD_NODES} (i.e. {@code null}) if there are      * no children, or {@link #MANY_CHILD_NODES} if there are more than one.      */
annotation|@
name|CheckForNull
specifier|private
specifier|final
name|String
name|childName
decl_stmt|;
name|Template
parameter_list|(
annotation|@
name|Nonnull
name|SegmentReader
name|reader
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|primaryType
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|mixinTypes
parameter_list|,
annotation|@
name|Nullable
name|PropertyTemplate
index|[]
name|properties
parameter_list|,
annotation|@
name|Nullable
name|String
name|childName
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|checkNotNull
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|this
operator|.
name|primaryType
operator|=
name|primaryType
expr_stmt|;
name|this
operator|.
name|mixinTypes
operator|=
name|mixinTypes
expr_stmt|;
if|if
condition|(
name|properties
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|this
operator|.
name|properties
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|properties
operator|=
operator|new
name|PropertyTemplate
index|[
literal|0
index|]
expr_stmt|;
block|}
name|this
operator|.
name|childName
operator|=
name|childName
expr_stmt|;
block|}
name|Template
parameter_list|(
annotation|@
name|Nonnull
name|SegmentReader
name|reader
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|state
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|checkNotNull
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|PropertyState
name|primary
init|=
literal|null
decl_stmt|;
name|PropertyState
name|mixins
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|PropertyTemplate
argument_list|>
name|templates
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyState
name|property
range|:
name|state
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|property
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Type
argument_list|<
name|?
argument_list|>
name|type
init|=
name|property
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"jcr:primaryType"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|type
operator|==
name|Type
operator|.
name|NAME
condition|)
block|{
name|primary
operator|=
name|property
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"jcr:mixinTypes"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|type
operator|==
name|Type
operator|.
name|NAMES
condition|)
block|{
name|mixins
operator|=
name|property
expr_stmt|;
block|}
else|else
block|{
name|templates
operator|.
name|add
argument_list|(
operator|new
name|PropertyTemplate
argument_list|(
name|property
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|primaryType
operator|=
name|primary
expr_stmt|;
name|this
operator|.
name|mixinTypes
operator|=
name|mixins
expr_stmt|;
name|this
operator|.
name|properties
operator|=
name|templates
operator|.
name|toArray
argument_list|(
operator|new
name|PropertyTemplate
index|[
name|templates
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|properties
argument_list|)
expr_stmt|;
name|long
name|count
init|=
name|state
operator|.
name|getChildNodeCount
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
name|childName
operator|=
name|ZERO_CHILD_NODES
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|count
operator|==
literal|1
condition|)
block|{
name|childName
operator|=
name|state
operator|.
name|getChildNodeNames
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|checkState
argument_list|(
name|childName
operator|!=
literal|null
operator|&&
operator|!
name|childName
operator|.
name|equals
argument_list|(
name|MANY_CHILD_NODES
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|childName
operator|=
name|MANY_CHILD_NODES
expr_stmt|;
block|}
block|}
annotation|@
name|CheckForNull
name|PropertyState
name|getPrimaryType
parameter_list|()
block|{
return|return
name|primaryType
return|;
block|}
annotation|@
name|CheckForNull
name|PropertyState
name|getMixinTypes
parameter_list|()
block|{
return|return
name|mixinTypes
return|;
block|}
name|PropertyTemplate
index|[]
name|getPropertyTemplates
parameter_list|()
block|{
return|return
name|properties
return|;
block|}
comment|/**      * Returns the template of the named property, or {@code null} if no such      * property exists. Use the {@link #getPrimaryType()} and      * {@link #getMixinTypes()} for accessing the JCR type properties, as      * they don't have templates.      *      * @param name property name      * @return property template, or {@code} null if not found      */
name|PropertyTemplate
name|getPropertyTemplate
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|int
name|hash
init|=
name|name
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|index
operator|<
name|properties
operator|.
name|length
operator|&&
name|properties
index|[
name|index
index|]
operator|.
name|getName
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|<
name|hash
condition|)
block|{
name|index
operator|++
expr_stmt|;
block|}
while|while
condition|(
name|index
operator|<
name|properties
operator|.
name|length
operator|&&
name|properties
index|[
name|index
index|]
operator|.
name|getName
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|==
name|hash
condition|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|properties
index|[
name|index
index|]
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|properties
index|[
name|index
index|]
return|;
block|}
name|index
operator|++
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|CheckForNull
name|String
name|getChildName
parameter_list|()
block|{
return|return
name|childName
return|;
block|}
name|SegmentPropertyState
name|getProperty
parameter_list|(
name|RecordId
name|recordId
parameter_list|,
name|int
name|index
parameter_list|)
block|{
name|checkElementIndex
argument_list|(
name|index
argument_list|,
name|properties
operator|.
name|length
argument_list|)
expr_stmt|;
name|Segment
name|segment
init|=
name|checkNotNull
argument_list|(
name|recordId
argument_list|)
operator|.
name|getSegment
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
literal|2
operator|*
name|RECORD_ID_BYTES
decl_stmt|;
if|if
condition|(
name|childName
operator|!=
name|ZERO_CHILD_NODES
condition|)
block|{
name|offset
operator|+=
name|RECORD_ID_BYTES
expr_stmt|;
block|}
name|RecordId
name|lid
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|recordId
operator|.
name|getRecordNumber
argument_list|()
argument_list|,
name|offset
argument_list|)
decl_stmt|;
name|ListRecord
name|props
init|=
operator|new
name|ListRecord
argument_list|(
name|lid
argument_list|,
name|properties
operator|.
name|length
argument_list|)
decl_stmt|;
name|RecordId
name|rid
init|=
name|props
operator|.
name|getEntry
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
name|reader
operator|.
name|readProperty
argument_list|(
name|rid
argument_list|,
name|properties
index|[
name|index
index|]
argument_list|)
return|;
block|}
name|MapRecord
name|getChildNodeMap
parameter_list|(
name|RecordId
name|recordId
parameter_list|)
block|{
name|checkState
argument_list|(
name|childName
operator|!=
name|ZERO_CHILD_NODES
argument_list|)
expr_stmt|;
name|Segment
name|segment
init|=
name|recordId
operator|.
name|getSegment
argument_list|()
decl_stmt|;
name|RecordId
name|childNodesId
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|recordId
operator|.
name|getRecordNumber
argument_list|()
argument_list|,
literal|2
operator|*
name|RECORD_ID_BYTES
argument_list|)
decl_stmt|;
return|return
name|reader
operator|.
name|readMap
argument_list|(
name|childNodesId
argument_list|)
return|;
block|}
specifier|public
name|NodeState
name|getChildNode
parameter_list|(
name|String
name|name
parameter_list|,
name|RecordId
name|recordId
parameter_list|)
block|{
if|if
condition|(
name|childName
operator|==
name|ZERO_CHILD_NODES
condition|)
block|{
return|return
name|MISSING_NODE
return|;
block|}
elseif|else
if|if
condition|(
name|childName
operator|==
name|MANY_CHILD_NODES
condition|)
block|{
name|MapRecord
name|map
init|=
name|getChildNodeMap
argument_list|(
name|recordId
argument_list|)
decl_stmt|;
name|MapEntry
name|child
init|=
name|map
operator|.
name|getEntry
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
return|return
name|child
operator|.
name|getNodeState
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|MISSING_NODE
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|childName
argument_list|)
condition|)
block|{
name|Segment
name|segment
init|=
name|recordId
operator|.
name|getSegment
argument_list|()
decl_stmt|;
name|RecordId
name|childNodeId
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|recordId
operator|.
name|getRecordNumber
argument_list|()
argument_list|,
literal|2
operator|*
name|RECORD_ID_BYTES
argument_list|)
decl_stmt|;
return|return
name|reader
operator|.
name|readNode
argument_list|(
name|childNodeId
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|MISSING_NODE
return|;
block|}
block|}
name|Iterable
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|getChildNodeEntries
parameter_list|(
name|RecordId
name|recordId
parameter_list|)
block|{
if|if
condition|(
name|childName
operator|==
name|ZERO_CHILD_NODES
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|childName
operator|==
name|MANY_CHILD_NODES
condition|)
block|{
name|MapRecord
name|map
init|=
name|getChildNodeMap
argument_list|(
name|recordId
argument_list|)
decl_stmt|;
return|return
name|map
operator|.
name|getEntries
argument_list|()
return|;
block|}
else|else
block|{
name|Segment
name|segment
init|=
name|recordId
operator|.
name|getSegment
argument_list|()
decl_stmt|;
name|RecordId
name|childNodeId
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|recordId
operator|.
name|getRecordNumber
argument_list|()
argument_list|,
literal|2
operator|*
name|RECORD_ID_BYTES
argument_list|)
decl_stmt|;
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|MemoryChildNodeEntry
argument_list|(
name|childName
argument_list|,
name|reader
operator|.
name|readNode
argument_list|(
name|childNodeId
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|public
name|boolean
name|compare
parameter_list|(
name|RecordId
name|thisId
parameter_list|,
name|RecordId
name|thatId
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|thisId
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|thatId
argument_list|)
expr_stmt|;
comment|// Compare properties
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|properties
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|PropertyState
name|thisProperty
init|=
name|getProperty
argument_list|(
name|thisId
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|PropertyState
name|thatProperty
init|=
name|getProperty
argument_list|(
name|thatId
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|thisProperty
operator|.
name|equals
argument_list|(
name|thatProperty
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// Compare child nodes
if|if
condition|(
name|childName
operator|==
name|ZERO_CHILD_NODES
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|childName
operator|!=
name|MANY_CHILD_NODES
condition|)
block|{
name|NodeState
name|thisChild
init|=
name|getChildNode
argument_list|(
name|childName
argument_list|,
name|thisId
argument_list|)
decl_stmt|;
name|NodeState
name|thatChild
init|=
name|getChildNode
argument_list|(
name|childName
argument_list|,
name|thatId
argument_list|)
decl_stmt|;
return|return
name|thisChild
operator|.
name|equals
argument_list|(
name|thatChild
argument_list|)
return|;
block|}
else|else
block|{
comment|// TODO: Leverage the HAMT data structure for the comparison
name|MapRecord
name|thisMap
init|=
name|getChildNodeMap
argument_list|(
name|thisId
argument_list|)
decl_stmt|;
name|MapRecord
name|thatMap
init|=
name|getChildNodeMap
argument_list|(
name|thatId
argument_list|)
decl_stmt|;
if|if
condition|(
name|Record
operator|.
name|fastEquals
argument_list|(
name|thisMap
argument_list|,
name|thatMap
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
comment|// shortcut
block|}
elseif|else
if|if
condition|(
name|thisMap
operator|.
name|size
argument_list|()
operator|!=
name|thatMap
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
comment|// shortcut
block|}
else|else
block|{
comment|// TODO: can this be optimized?
for|for
control|(
name|MapEntry
name|entry
range|:
name|thisMap
operator|.
name|getEntries
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getName
argument_list|()
decl_stmt|;
name|MapEntry
name|thatEntry
init|=
name|thatMap
operator|.
name|getEntry
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|thatEntry
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
operator|!
name|entry
operator|.
name|getNodeState
argument_list|()
operator|.
name|equals
argument_list|(
name|thatEntry
operator|.
name|getNodeState
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
block|}
comment|//------------------------------------------------------------< Object>--
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|object
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|object
operator|instanceof
name|Template
condition|)
block|{
name|Template
name|that
init|=
operator|(
name|Template
operator|)
name|object
decl_stmt|;
return|return
name|Objects
operator|.
name|equal
argument_list|(
name|primaryType
argument_list|,
name|that
operator|.
name|primaryType
argument_list|)
operator|&&
name|Objects
operator|.
name|equal
argument_list|(
name|mixinTypes
argument_list|,
name|that
operator|.
name|mixinTypes
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|properties
argument_list|,
name|that
operator|.
name|properties
argument_list|)
operator|&&
name|Objects
operator|.
name|equal
argument_list|(
name|childName
argument_list|,
name|that
operator|.
name|childName
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
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hashCode
argument_list|(
name|primaryType
argument_list|,
name|mixinTypes
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|properties
argument_list|)
argument_list|,
name|getTemplateType
argument_list|()
argument_list|,
name|childName
argument_list|)
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
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"{ "
argument_list|)
expr_stmt|;
if|if
condition|(
name|primaryType
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|primaryType
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mixinTypes
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|mixinTypes
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|properties
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|properties
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|" = ?, "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|childName
operator|==
name|ZERO_CHILD_NODES
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"<no children>"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|childName
operator|==
name|MANY_CHILD_NODES
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"<many children>"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
name|childName
operator|+
literal|" =<node>"
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|" }"
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
name|short
name|getTemplateType
parameter_list|()
block|{
if|if
condition|(
name|childName
operator|==
name|ZERO_CHILD_NODES
condition|)
block|{
return|return
name|ZERO_CHILD_NODES_TYPE
return|;
block|}
elseif|else
if|if
condition|(
name|childName
operator|==
name|MANY_CHILD_NODES
condition|)
block|{
return|return
name|MANY_CHILD_NODES_TYPE
return|;
block|}
else|else
block|{
return|return
name|SINGLE_CHILD_NODE_TYPE
return|;
block|}
block|}
specifier|public
name|int
name|estimateMemoryUsage
parameter_list|()
block|{
name|int
name|size
init|=
name|OBJECT_HEADER_SIZE
decl_stmt|;
name|size
operator|+=
literal|48
expr_stmt|;
name|size
operator|+=
name|StringUtils
operator|.
name|estimateMemoryUsage
argument_list|(
name|childName
argument_list|)
expr_stmt|;
name|size
operator|+=
name|estimateMemoryUsage
argument_list|(
name|mixinTypes
argument_list|)
expr_stmt|;
name|size
operator|+=
name|estimateMemoryUsage
argument_list|(
name|primaryType
argument_list|)
expr_stmt|;
for|for
control|(
name|PropertyTemplate
name|property
range|:
name|properties
control|)
block|{
name|size
operator|+=
name|property
operator|.
name|estimateMemoryUsage
argument_list|()
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
specifier|private
specifier|static
name|int
name|estimateMemoryUsage
parameter_list|(
name|PropertyState
name|propertyState
parameter_list|)
block|{
if|if
condition|(
name|propertyState
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|size
init|=
name|OBJECT_HEADER_SIZE
decl_stmt|;
name|size
operator|+=
name|StringUtils
operator|.
name|estimateMemoryUsage
argument_list|(
name|propertyState
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|propertyState
operator|.
name|count
argument_list|()
condition|;
name|k
operator|++
control|)
block|{
name|String
name|s
init|=
name|propertyState
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|,
name|k
argument_list|)
decl_stmt|;
name|size
operator|+=
name|StringUtils
operator|.
name|estimateMemoryUsage
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
block|}
end_class

end_unit

