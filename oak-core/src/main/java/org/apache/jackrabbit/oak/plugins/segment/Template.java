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
name|segment
operator|.
name|Segment
operator|.
name|RECORD_ID_BYTES
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
name|HashSet
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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

begin_class
specifier|public
class|class
name|Template
block|{
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
name|PropertyState
name|primaryType
parameter_list|,
name|PropertyState
name|mixinTypes
parameter_list|,
name|PropertyTemplate
index|[]
name|properties
parameter_list|,
name|String
name|childName
parameter_list|)
block|{
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
name|this
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
name|this
operator|.
name|childName
operator|=
name|childName
expr_stmt|;
block|}
name|Template
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
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
argument_list|()
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
specifier|public
name|boolean
name|hasPrimaryType
parameter_list|()
block|{
return|return
name|primaryType
operator|!=
literal|null
return|;
block|}
specifier|public
name|String
name|getPrimaryType
parameter_list|()
block|{
if|if
condition|(
name|primaryType
operator|!=
literal|null
condition|)
block|{
return|return
name|primaryType
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAME
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|boolean
name|hasMixinTypes
parameter_list|()
block|{
return|return
name|mixinTypes
operator|!=
literal|null
return|;
block|}
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getMixinTypes
parameter_list|()
block|{
if|if
condition|(
name|mixinTypes
operator|!=
literal|null
condition|)
block|{
return|return
name|mixinTypes
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|PropertyTemplate
index|[]
name|getPropertyTemplates
parameter_list|()
block|{
return|return
name|properties
return|;
block|}
specifier|public
name|boolean
name|hasNoChildNodes
parameter_list|()
block|{
return|return
name|childName
operator|==
name|ZERO_CHILD_NODES
return|;
block|}
specifier|public
name|boolean
name|hasOneChildNode
parameter_list|()
block|{
return|return
operator|!
name|hasNoChildNodes
argument_list|()
operator|&&
operator|!
name|hasManyChildNodes
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|hasManyChildNodes
parameter_list|()
block|{
return|return
name|childName
operator|==
name|MANY_CHILD_NODES
return|;
block|}
specifier|public
name|String
name|getChildName
parameter_list|()
block|{
if|if
condition|(
name|hasOneChildNode
argument_list|()
condition|)
block|{
return|return
name|childName
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|int
name|getPropertyCount
parameter_list|()
block|{
if|if
condition|(
name|primaryType
operator|!=
literal|null
operator|&&
name|mixinTypes
operator|!=
literal|null
condition|)
block|{
return|return
name|properties
operator|.
name|length
operator|+
literal|2
return|;
block|}
elseif|else
if|if
condition|(
name|primaryType
operator|!=
literal|null
operator|||
name|mixinTypes
operator|!=
literal|null
condition|)
block|{
return|return
name|properties
operator|.
name|length
operator|+
literal|1
return|;
block|}
else|else
block|{
return|return
name|properties
operator|.
name|length
return|;
block|}
block|}
specifier|public
name|boolean
name|hasProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|JCR_PRIMARYTYPE
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|primaryType
operator|!=
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|JCR_MIXINTYPES
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|mixinTypes
operator|!=
literal|null
return|;
block|}
else|else
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
literal|true
return|;
block|}
name|index
operator|++
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
block|}
specifier|public
name|PropertyState
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|SegmentStore
name|store
parameter_list|,
name|RecordId
name|recordId
parameter_list|)
block|{
if|if
condition|(
name|JCR_PRIMARYTYPE
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|primaryType
operator|!=
literal|null
condition|)
block|{
return|return
name|primaryType
return|;
block|}
elseif|else
if|if
condition|(
name|JCR_MIXINTYPES
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|mixinTypes
operator|!=
literal|null
condition|)
block|{
return|return
name|mixinTypes
return|;
block|}
else|else
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
name|getProperty
argument_list|(
name|store
argument_list|,
name|recordId
argument_list|,
name|index
argument_list|)
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
block|}
specifier|private
name|PropertyState
name|getProperty
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|RecordId
name|recordId
parameter_list|,
name|int
name|index
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|recordId
argument_list|)
expr_stmt|;
name|checkElementIndex
argument_list|(
name|index
argument_list|,
name|properties
operator|.
name|length
argument_list|)
expr_stmt|;
name|int
name|offset
init|=
name|recordId
operator|.
name|getOffset
argument_list|()
operator|+
name|RECORD_ID_BYTES
decl_stmt|;
if|if
condition|(
operator|!
name|hasNoChildNodes
argument_list|()
condition|)
block|{
name|offset
operator|+=
name|RECORD_ID_BYTES
expr_stmt|;
block|}
name|offset
operator|+=
name|index
operator|*
name|RECORD_ID_BYTES
expr_stmt|;
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|SegmentPropertyState
argument_list|(
name|properties
index|[
name|index
index|]
argument_list|,
name|store
argument_list|,
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Iterable
argument_list|<
name|PropertyState
argument_list|>
name|getProperties
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|RecordId
name|recordId
parameter_list|)
block|{
name|List
argument_list|<
name|PropertyState
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|properties
operator|.
name|length
operator|+
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|primaryType
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|primaryType
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
name|list
operator|.
name|add
argument_list|(
name|mixinTypes
argument_list|)
expr_stmt|;
block|}
name|int
name|offset
init|=
name|recordId
operator|.
name|getOffset
argument_list|()
operator|+
name|RECORD_ID_BYTES
decl_stmt|;
if|if
condition|(
operator|!
name|hasNoChildNodes
argument_list|()
condition|)
block|{
name|offset
operator|+=
name|RECORD_ID_BYTES
expr_stmt|;
block|}
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
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
name|RecordId
name|propertyId
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
operator|new
name|SegmentPropertyState
argument_list|(
name|properties
index|[
name|i
index|]
argument_list|,
name|store
argument_list|,
name|propertyId
argument_list|)
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|RECORD_ID_BYTES
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
specifier|public
name|long
name|getChildNodeCount
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|RecordId
name|recordId
parameter_list|)
block|{
if|if
condition|(
name|hasNoChildNodes
argument_list|()
condition|)
block|{
return|return
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|hasManyChildNodes
argument_list|()
condition|)
block|{
name|MapRecord
name|map
init|=
name|getChildNodeMap
argument_list|(
name|store
argument_list|,
name|recordId
argument_list|)
decl_stmt|;
return|return
name|map
operator|.
name|size
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|1
return|;
block|}
block|}
name|MapRecord
name|getChildNodeMap
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|RecordId
name|recordId
parameter_list|)
block|{
name|checkState
argument_list|(
name|hasManyChildNodes
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|offset
init|=
name|recordId
operator|.
name|getOffset
argument_list|()
operator|+
name|RECORD_ID_BYTES
decl_stmt|;
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
name|RecordId
name|childNodesId
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
argument_list|)
decl_stmt|;
return|return
name|MapRecord
operator|.
name|readMap
argument_list|(
name|store
argument_list|,
name|childNodesId
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|hasChildNode
parameter_list|(
name|String
name|name
parameter_list|,
name|SegmentStore
name|store
parameter_list|,
name|RecordId
name|recordId
parameter_list|)
block|{
if|if
condition|(
name|hasNoChildNodes
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|hasManyChildNodes
argument_list|()
condition|)
block|{
name|MapRecord
name|map
init|=
name|getChildNodeMap
argument_list|(
name|store
argument_list|,
name|recordId
argument_list|)
decl_stmt|;
return|return
name|map
operator|.
name|getEntry
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
return|;
block|}
else|else
block|{
return|return
name|name
operator|.
name|equals
argument_list|(
name|childName
argument_list|)
return|;
block|}
block|}
specifier|public
name|NodeState
name|getChildNode
parameter_list|(
name|String
name|name
parameter_list|,
name|SegmentStore
name|store
parameter_list|,
name|RecordId
name|recordId
parameter_list|)
block|{
if|if
condition|(
name|hasNoChildNodes
argument_list|()
condition|)
block|{
return|return
name|MISSING_NODE
return|;
block|}
elseif|else
if|if
condition|(
name|hasManyChildNodes
argument_list|()
condition|)
block|{
name|MapRecord
name|map
init|=
name|getChildNodeMap
argument_list|(
name|store
argument_list|,
name|recordId
argument_list|)
decl_stmt|;
name|RecordId
name|childNodeId
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
name|childNodeId
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|SegmentNodeState
argument_list|(
name|store
argument_list|,
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
name|int
name|offset
init|=
name|recordId
operator|.
name|getOffset
argument_list|()
operator|+
name|RECORD_ID_BYTES
decl_stmt|;
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
name|RecordId
name|childNodeId
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
argument_list|)
decl_stmt|;
return|return
operator|new
name|SegmentNodeState
argument_list|(
name|store
argument_list|,
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
name|String
argument_list|>
name|getChildNodeNames
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|RecordId
name|recordId
parameter_list|)
block|{
if|if
condition|(
name|hasNoChildNodes
argument_list|()
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
name|hasManyChildNodes
argument_list|()
condition|)
block|{
name|MapRecord
name|map
init|=
name|getChildNodeMap
argument_list|(
name|store
argument_list|,
name|recordId
argument_list|)
decl_stmt|;
return|return
name|map
operator|.
name|getKeys
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|childName
argument_list|)
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
name|SegmentStore
name|store
parameter_list|,
name|RecordId
name|recordId
parameter_list|)
block|{
if|if
condition|(
name|hasNoChildNodes
argument_list|()
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
name|hasManyChildNodes
argument_list|()
condition|)
block|{
name|MapRecord
name|map
init|=
name|getChildNodeMap
argument_list|(
name|store
argument_list|,
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
name|int
name|offset
init|=
name|recordId
operator|.
name|getOffset
argument_list|()
operator|+
name|RECORD_ID_BYTES
decl_stmt|;
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
name|RecordId
name|childNodeId
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
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
operator|new
name|SegmentNodeState
argument_list|(
name|store
argument_list|,
name|childNodeId
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|public
name|boolean
name|compareAgainstBaseState
parameter_list|(
name|SegmentStore
name|store
parameter_list|,
name|RecordId
name|afterId
parameter_list|,
name|Template
name|beforeTemplate
parameter_list|,
name|RecordId
name|beforeId
parameter_list|,
name|NodeStateDiff
name|diff
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|afterId
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|beforeTemplate
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|beforeId
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|diff
argument_list|)
expr_stmt|;
comment|// Compare type properties
if|if
condition|(
operator|!
name|compareProperties
argument_list|(
name|beforeTemplate
operator|.
name|primaryType
argument_list|,
name|primaryType
argument_list|,
name|diff
argument_list|)
operator|||
operator|!
name|compareProperties
argument_list|(
name|beforeTemplate
operator|.
name|mixinTypes
argument_list|,
name|mixinTypes
argument_list|,
name|diff
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Compare other properties, leveraging the ordering
name|int
name|beforeIndex
init|=
literal|0
decl_stmt|;
name|int
name|afterIndex
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|beforeIndex
operator|<
name|beforeTemplate
operator|.
name|properties
operator|.
name|length
operator|&&
name|afterIndex
operator|<
name|properties
operator|.
name|length
condition|)
block|{
name|int
name|d
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|properties
index|[
name|afterIndex
index|]
operator|.
name|hashCode
argument_list|()
argument_list|)
operator|.
name|compareTo
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|beforeTemplate
operator|.
name|properties
index|[
name|beforeIndex
index|]
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|==
literal|0
condition|)
block|{
name|d
operator|=
name|properties
index|[
name|afterIndex
index|]
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|beforeTemplate
operator|.
name|properties
index|[
name|beforeIndex
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|PropertyState
name|beforeProperty
init|=
literal|null
decl_stmt|;
name|PropertyState
name|afterProperty
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|d
operator|<
literal|0
condition|)
block|{
name|afterProperty
operator|=
name|getProperty
argument_list|(
name|store
argument_list|,
name|afterId
argument_list|,
name|afterIndex
operator|++
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|d
operator|>
literal|0
condition|)
block|{
name|beforeProperty
operator|=
name|beforeTemplate
operator|.
name|getProperty
argument_list|(
name|store
argument_list|,
name|beforeId
argument_list|,
name|beforeIndex
operator|++
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|afterProperty
operator|=
name|getProperty
argument_list|(
name|store
argument_list|,
name|afterId
argument_list|,
name|afterIndex
operator|++
argument_list|)
expr_stmt|;
name|beforeProperty
operator|=
name|beforeTemplate
operator|.
name|getProperty
argument_list|(
name|store
argument_list|,
name|beforeId
argument_list|,
name|beforeIndex
operator|++
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|compareProperties
argument_list|(
name|beforeProperty
argument_list|,
name|afterProperty
argument_list|,
name|diff
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
while|while
condition|(
name|afterIndex
operator|<
name|properties
operator|.
name|length
condition|)
block|{
if|if
condition|(
operator|!
name|diff
operator|.
name|propertyAdded
argument_list|(
name|getProperty
argument_list|(
name|store
argument_list|,
name|afterId
argument_list|,
name|afterIndex
operator|++
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
while|while
condition|(
name|beforeIndex
operator|<
name|beforeTemplate
operator|.
name|properties
operator|.
name|length
condition|)
block|{
name|PropertyState
name|beforeProperty
init|=
name|beforeTemplate
operator|.
name|getProperty
argument_list|(
name|store
argument_list|,
name|beforeId
argument_list|,
name|beforeIndex
operator|++
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|diff
operator|.
name|propertyDeleted
argument_list|(
name|beforeProperty
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
name|hasNoChildNodes
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|beforeTemplate
operator|.
name|hasNoChildNodes
argument_list|()
condition|)
block|{
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|beforeTemplate
operator|.
name|getChildNodeEntries
argument_list|(
name|store
argument_list|,
name|beforeId
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|diff
operator|.
name|childNodeDeleted
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|,
name|entry
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
block|}
block|}
elseif|else
if|if
condition|(
name|hasOneChildNode
argument_list|()
condition|)
block|{
name|NodeState
name|afterNode
init|=
name|getChildNode
argument_list|(
name|childName
argument_list|,
name|store
argument_list|,
name|afterId
argument_list|)
decl_stmt|;
name|NodeState
name|beforeNode
init|=
name|beforeTemplate
operator|.
name|getChildNode
argument_list|(
name|childName
argument_list|,
name|store
argument_list|,
name|beforeId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|beforeNode
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|diff
operator|.
name|childNodeAdded
argument_list|(
name|childName
argument_list|,
name|afterNode
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|beforeNode
operator|.
name|equals
argument_list|(
name|afterNode
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|diff
operator|.
name|childNodeChanged
argument_list|(
name|childName
argument_list|,
name|beforeNode
argument_list|,
name|afterNode
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
operator|(
name|beforeTemplate
operator|.
name|hasOneChildNode
argument_list|()
operator|&&
operator|!
name|beforeNode
operator|.
name|exists
argument_list|()
operator|)
operator|||
name|beforeTemplate
operator|.
name|hasManyChildNodes
argument_list|()
condition|)
block|{
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|beforeTemplate
operator|.
name|getChildNodeEntries
argument_list|(
name|store
argument_list|,
name|beforeId
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|childName
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|diff
operator|.
name|childNodeDeleted
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|,
name|entry
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
block|}
block|}
block|}
else|else
block|{
comment|// TODO: Leverage the HAMT data structure for the comparison
name|Set
argument_list|<
name|String
argument_list|>
name|baseChildNodes
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|beforeCNE
range|:
name|beforeTemplate
operator|.
name|getChildNodeEntries
argument_list|(
name|store
argument_list|,
name|beforeId
argument_list|)
control|)
block|{
name|String
name|name
init|=
name|beforeCNE
operator|.
name|getName
argument_list|()
decl_stmt|;
name|NodeState
name|beforeChild
init|=
name|beforeCNE
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|afterChild
init|=
name|getChildNode
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|afterId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|afterChild
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|diff
operator|.
name|childNodeDeleted
argument_list|(
name|name
argument_list|,
name|beforeChild
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
name|baseChildNodes
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|beforeChild
operator|.
name|equals
argument_list|(
name|afterChild
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|diff
operator|.
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|beforeChild
argument_list|,
name|afterChild
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
block|}
for|for
control|(
name|ChildNodeEntry
name|afterChild
range|:
name|getChildNodeEntries
argument_list|(
name|store
argument_list|,
name|afterId
argument_list|)
control|)
block|{
name|String
name|name
init|=
name|afterChild
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|baseChildNodes
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|diff
operator|.
name|childNodeAdded
argument_list|(
name|name
argument_list|,
name|afterChild
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
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|boolean
name|compareProperties
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|,
name|NodeStateDiff
name|diff
parameter_list|)
block|{
if|if
condition|(
name|before
operator|==
literal|null
condition|)
block|{
return|return
name|after
operator|==
literal|null
operator|||
name|diff
operator|.
name|propertyAdded
argument_list|(
name|after
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|after
operator|==
literal|null
condition|)
block|{
return|return
name|diff
operator|.
name|propertyDeleted
argument_list|(
name|before
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|before
operator|.
name|equals
argument_list|(
name|after
argument_list|)
operator|||
name|diff
operator|.
name|propertyChanged
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
return|;
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
name|hasNoChildNodes
argument_list|()
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
name|hasManyChildNodes
argument_list|()
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
block|}
end_class

end_unit

