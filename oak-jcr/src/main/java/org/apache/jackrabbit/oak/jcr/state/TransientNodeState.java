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
name|jcr
operator|.
name|state
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|ScalarImpl
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
name|mk
operator|.
name|model
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
name|mk
operator|.
name|model
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
name|mk
operator|.
name|model
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
name|Scalar
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
name|jcr
operator|.
name|SessionContext
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
name|jcr
operator|.
name|SessionImpl
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
name|jcr
operator|.
name|json
operator|.
name|FullJsonParser
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
name|jcr
operator|.
name|json
operator|.
name|JsonValue
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
name|jcr
operator|.
name|json
operator|.
name|JsonValue
operator|.
name|JsonAtom
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
name|jcr
operator|.
name|json
operator|.
name|JsonValue
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
name|jcr
operator|.
name|json
operator|.
name|UnescapingJsonTokenizer
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
name|jcr
operator|.
name|state
operator|.
name|ChangeTree
operator|.
name|NodeDelta
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
name|jcr
operator|.
name|util
operator|.
name|Function1
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
name|jcr
operator|.
name|util
operator|.
name|Iterators
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
name|jcr
operator|.
name|util
operator|.
name|PagedIterator
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
name|jcr
operator|.
name|util
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
name|jcr
operator|.
name|util
operator|.
name|Predicate
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
name|KernelNodeState
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
name|KernelPropertyState
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ItemExistsException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ItemNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PathNotFoundException
import|;
end_import

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
name|Iterator
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
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|jcr
operator|.
name|util
operator|.
name|Iterators
operator|.
name|toIterable
import|;
end_import

begin_comment
comment|/**  * A {@code TransientNodeState} instance uses a {@code TransientSpace} to record changes  * to a {@code PersistedNodeState}.  */
end_comment

begin_class
specifier|public
class|class
name|TransientNodeState
block|{
specifier|private
specifier|static
specifier|final
name|int
name|BATCH_SIZE
init|=
literal|256
decl_stmt|;
specifier|private
specifier|final
name|SessionContext
argument_list|<
name|SessionImpl
argument_list|>
name|sessionContext
decl_stmt|;
specifier|private
name|String
name|revision
decl_stmt|;
specifier|private
name|NodeState
name|persistentNodeState
decl_stmt|;
specifier|private
name|NodeDelta
name|nodeDelta
decl_stmt|;
name|TransientNodeState
parameter_list|(
name|SessionContext
argument_list|<
name|SessionImpl
argument_list|>
name|sessionContext
parameter_list|,
name|NodeDelta
name|nodeDelta
parameter_list|)
block|{
name|this
operator|.
name|sessionContext
operator|=
name|sessionContext
expr_stmt|;
name|this
operator|.
name|nodeDelta
operator|=
name|nodeDelta
expr_stmt|;
block|}
comment|/**      * @return {@code true} iff this is the root node      */
specifier|public
name|boolean
name|isRoot
parameter_list|()
block|{
return|return
name|getPath
argument_list|()
operator|.
name|isRoot
argument_list|()
return|;
block|}
comment|/**      * @return the path of this node      */
specifier|public
name|Path
name|getPath
parameter_list|()
block|{
return|return
name|getNodeDelta
argument_list|()
operator|.
name|getPath
argument_list|()
return|;
block|}
comment|/**      * @return the name of this node      */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
comment|/**      * @return {@code true} iff this node has been transiently added.      */
specifier|public
name|boolean
name|isNew
parameter_list|()
block|{
name|NodeDelta
name|delta
init|=
name|getNodeDelta
argument_list|()
decl_stmt|;
return|return
name|delta
operator|.
name|isTransient
argument_list|()
operator|&&
operator|!
name|delta
operator|.
name|isRemoved
argument_list|()
return|;
block|}
comment|/**      * @return {@code true} iff this node has been transiently modified.      */
specifier|public
name|boolean
name|isModified
parameter_list|()
block|{
return|return
name|getNodeDelta
argument_list|()
operator|.
name|isTransient
argument_list|()
return|;
block|}
comment|/**      * Transiently add a node with the given {@code name}.      *      * @param name The name of the new node.      * @return the added node      * @throws javax.jcr.ItemExistsException if a node with that name exists already.      */
specifier|public
name|TransientNodeState
name|addNode
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|ItemExistsException
block|{
name|NodeDelta
name|child
init|=
name|getNodeDelta
argument_list|()
operator|.
name|addNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|getNodeState
argument_list|(
name|child
argument_list|)
return|;
block|}
comment|/**      * Transiently remove this node.      * @throws javax.jcr.ItemNotFoundException if this node has been removed already      */
specifier|public
name|void
name|remove
parameter_list|()
throws|throws
name|ItemNotFoundException
block|{
name|getNodeStateProvider
argument_list|()
operator|.
name|release
argument_list|(
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|getNodeDelta
argument_list|()
operator|.
name|getParent
argument_list|()
operator|.
name|removeNode
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Transiently move this node.      *      * @param name  name of this node at its {@code destination}      * @param destination The destination of the move.      * @throws javax.jcr.ItemExistsException  {@code name} exists at {@code destination}      * @throws javax.jcr.PathNotFoundException  {@code destination} does not exist      * @throws javax.jcr.ItemNotFoundException  {@code name} does not exist      */
specifier|public
name|void
name|move
parameter_list|(
name|String
name|name
parameter_list|,
name|Path
name|destination
parameter_list|)
throws|throws
name|ItemExistsException
throws|,
name|PathNotFoundException
throws|,
name|ItemNotFoundException
block|{
name|getNodeDelta
argument_list|()
operator|.
name|moveNode
argument_list|(
name|name
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|getNodeStateProvider
argument_list|()
operator|.
name|release
argument_list|(
name|getPath
argument_list|()
operator|.
name|concat
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Transiently set a property.      * @param name  Name of the property.      * @param value  Value of the property. Use {@code null} or {@code JsonAtom.NULL}      *               to remove the property.      */
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|JsonValue
name|value
parameter_list|)
block|{
name|getNodeDelta
argument_list|()
operator|.
name|setValue
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return {@code true} iff this instance has child nodes.      */
specifier|public
name|boolean
name|hasNodes
parameter_list|()
block|{
return|return
name|getNodes
argument_list|()
operator|.
name|hasNext
argument_list|()
return|;
block|}
comment|/**      * @return Iterator of all child node states of this instance.      */
specifier|public
name|Iterator
argument_list|<
name|TransientNodeState
argument_list|>
name|getNodes
parameter_list|()
block|{
name|Iterator
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|persistedEntries
init|=
name|Iterators
operator|.
name|flatten
argument_list|(
operator|new
name|PagedIterator
argument_list|<
name|ChildNodeEntry
argument_list|>
argument_list|(
name|BATCH_SIZE
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Iterator
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|getPage
parameter_list|(
name|long
name|pos
parameter_list|,
name|int
name|size
parameter_list|)
block|{
return|return
name|getPersistentNodeState
argument_list|()
operator|.
name|getChildNodeEntries
argument_list|(
name|pos
argument_list|,
name|size
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
specifier|final
name|NodeDelta
name|delta
init|=
name|getNodeDelta
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|ChildNodeEntry
argument_list|>
name|unmodifiedEntries
init|=
name|Iterators
operator|.
name|filter
argument_list|(
name|persistedEntries
argument_list|,
operator|new
name|Predicate
argument_list|<
name|ChildNodeEntry
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|(
name|ChildNodeEntry
name|entry
parameter_list|)
block|{
return|return
operator|!
name|delta
operator|.
name|isNodeModified
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|TransientNodeState
argument_list|>
name|unmodifiedStates
init|=
name|Iterators
operator|.
name|map
argument_list|(
name|unmodifiedEntries
argument_list|,
operator|new
name|Function1
argument_list|<
name|ChildNodeEntry
argument_list|,
name|TransientNodeState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TransientNodeState
name|apply
parameter_list|(
name|ChildNodeEntry
name|entry
parameter_list|)
block|{
return|return
name|getNodeState
argument_list|(
name|delta
operator|.
name|getNode
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|TransientNodeState
argument_list|>
name|modifiedStates
init|=
name|Iterators
operator|.
name|map
argument_list|(
name|toIterable
argument_list|(
name|delta
operator|.
name|getNodes
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|,
operator|new
name|Function1
argument_list|<
name|NodeDelta
argument_list|,
name|TransientNodeState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TransientNodeState
name|apply
parameter_list|(
name|NodeDelta
name|delta
parameter_list|)
block|{
return|return
name|getNodeState
argument_list|(
name|delta
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|Iterators
operator|.
name|chain
argument_list|(
name|unmodifiedStates
argument_list|,
name|modifiedStates
argument_list|)
return|;
block|}
comment|/**      * @return {@code true} iff this instance has properties      */
specifier|public
name|boolean
name|hasProperties
parameter_list|()
block|{
return|return
name|getProperties
argument_list|()
operator|.
name|hasNext
argument_list|()
return|;
block|}
comment|/**      * @return Iterator of all property states of this instance.      */
specifier|public
name|Iterator
argument_list|<
name|PropertyState
argument_list|>
name|getProperties
parameter_list|()
block|{
name|Iterable
argument_list|<
name|?
extends|extends
name|PropertyState
argument_list|>
name|propertyStates
init|=
name|getPersistentNodeState
argument_list|()
operator|.
name|getProperties
argument_list|()
decl_stmt|;
specifier|final
name|NodeDelta
name|delta
init|=
name|getNodeDelta
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|PropertyState
argument_list|>
name|propertyEntries
init|=
name|Iterators
operator|.
name|filter
argument_list|(
name|propertyStates
operator|.
name|iterator
argument_list|()
argument_list|,
operator|new
name|Predicate
argument_list|<
name|PropertyState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|(
name|PropertyState
name|state
parameter_list|)
block|{
return|return
operator|!
name|state
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|":"
argument_list|)
operator|&&
operator|!
name|delta
operator|.
name|hasProperty
argument_list|(
name|state
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|PropertyState
argument_list|>
name|modifiedProperties
init|=
name|delta
operator|.
name|getPropertyStates
argument_list|()
decl_stmt|;
return|return
name|Iterators
operator|.
name|chain
argument_list|(
name|propertyEntries
argument_list|,
name|Iterators
operator|.
name|toIterable
argument_list|(
name|modifiedProperties
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @param name  name of the property      * @return  value of the property named {@code name}.      * @throws javax.jcr.ItemNotFoundException  if no such property exists.      */
specifier|public
name|JsonValue
name|getPropertyValue
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|ItemNotFoundException
block|{
name|JsonValue
name|value
init|=
name|getPropertyValueOrNull
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ItemNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
block|}
return|return
name|value
return|;
block|}
specifier|public
name|PropertyState
name|getPropertyState
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|ItemNotFoundException
block|{
name|JsonValue
name|value
init|=
name|getPropertyValueOrNull
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ItemNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
block|}
return|return
name|createPropertyState
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyState
name|createPropertyState
parameter_list|(
name|String
name|name
parameter_list|,
name|JsonValue
name|value
parameter_list|)
block|{
switch|switch
condition|(
name|value
operator|.
name|type
argument_list|()
condition|)
block|{
case|case
name|STRING
case|:
case|case
name|NUMBER
case|:
case|case
name|BOOLEAN
case|:
return|return
operator|new
name|KernelPropertyState
argument_list|(
name|name
argument_list|,
name|toScalar
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
case|case
name|ARRAY
case|:
name|List
argument_list|<
name|Scalar
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|Scalar
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|JsonValue
name|v
range|:
name|value
operator|.
name|asArray
argument_list|()
operator|.
name|value
argument_list|()
control|)
block|{
name|values
operator|.
name|add
argument_list|(
name|toScalar
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|KernelPropertyState
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Invalid value"
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|Scalar
name|toScalar
parameter_list|(
name|JsonValue
name|value
parameter_list|)
block|{
switch|switch
condition|(
name|value
operator|.
name|type
argument_list|()
condition|)
block|{
case|case
name|STRING
case|:
return|return
name|ScalarImpl
operator|.
name|createString
argument_list|(
name|value
operator|.
name|asAtom
argument_list|()
operator|.
name|value
argument_list|()
argument_list|)
return|;
case|case
name|NUMBER
case|:
return|return
name|ScalarImpl
operator|.
name|createNumber
argument_list|(
name|value
operator|.
name|asAtom
argument_list|()
operator|.
name|value
argument_list|()
argument_list|)
return|;
case|case
name|BOOLEAN
case|:
return|return
name|ScalarImpl
operator|.
name|createBoolean
argument_list|(
name|value
operator|.
name|asAtom
argument_list|()
operator|.
name|isTrue
argument_list|()
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Invalid value"
argument_list|)
throw|;
block|}
block|}
comment|/**      * @param name name of the property      * @return {@code true} iff this instance has a property name {@code name}.      */
specifier|public
name|boolean
name|hasProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getPropertyValueOrNull
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/**      * @param name name of the property      * @return {@code true} iff the property named {@code name} has been transiently added.      */
specifier|public
name|boolean
name|isPropertyNew
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|JsonValue
name|value
init|=
name|getNodeDelta
argument_list|()
operator|.
name|getPropertyValue
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|value
operator|!=
literal|null
operator|&&
operator|!
name|value
operator|.
name|isNull
argument_list|()
operator|&&
name|getPersistedPropertyValue
argument_list|(
name|name
argument_list|)
operator|==
literal|null
return|;
block|}
comment|/**      * @param name name of the property      * @return {@code true} iff the property named {@code name} has been transiently modified.      */
specifier|public
name|boolean
name|isPropertyModified
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getNodeDelta
argument_list|()
operator|.
name|hasProperty
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Transiently remove a property.      * @param name  name of the property to remove.      */
specifier|public
name|void
name|removeProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|getNodeDelta
argument_list|()
operator|.
name|setValue
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"TransientNodeState("
operator|+
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|')'
return|;
block|}
comment|//------------------------------------------< private>---
specifier|private
name|NodeStateProvider
name|getNodeStateProvider
parameter_list|()
block|{
return|return
name|sessionContext
operator|.
name|getNodeStateProvider
argument_list|()
return|;
block|}
specifier|private
name|TransientNodeState
name|getNodeState
parameter_list|(
name|NodeDelta
name|nodeDelta
parameter_list|)
block|{
return|return
name|getNodeStateProvider
argument_list|()
operator|.
name|getNodeState
argument_list|(
name|nodeDelta
argument_list|)
return|;
block|}
specifier|private
name|JsonValue
name|getPropertyValueOrNull
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|JsonValue
name|value
init|=
name|getNodeDelta
argument_list|()
operator|.
name|getPropertyValue
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|getPersistedPropertyValue
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|value
operator|==
name|JsonAtom
operator|.
name|NULL
condition|?
literal|null
else|:
name|value
return|;
block|}
block|}
specifier|private
name|JsonValue
name|getPersistedPropertyValue
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|PropertyState
name|state
init|=
name|getPersistentNodeState
argument_list|()
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
comment|// fixme: use Scalar class
name|String
name|v
init|=
name|state
operator|.
name|getEncodedValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|startsWith
argument_list|(
literal|"["
argument_list|)
condition|)
block|{
return|return
name|FullJsonParser
operator|.
name|parseArray
argument_list|(
operator|new
name|UnescapingJsonTokenizer
argument_list|(
name|v
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|v
operator|.
name|startsWith
argument_list|(
literal|"\""
argument_list|)
condition|)
block|{
return|return
operator|new
name|JsonAtom
argument_list|(
name|v
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|v
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"true"
operator|.
name|equalsIgnoreCase
argument_list|(
name|v
argument_list|)
condition|)
block|{
return|return
name|JsonAtom
operator|.
name|TRUE
return|;
block|}
elseif|else
if|if
condition|(
literal|"false"
operator|.
name|equalsIgnoreCase
argument_list|(
name|v
argument_list|)
condition|)
block|{
return|return
name|JsonAtom
operator|.
name|FALSE
return|;
block|}
else|else
block|{
return|return
operator|new
name|JsonAtom
argument_list|(
name|v
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
return|;
block|}
block|}
block|}
specifier|private
specifier|synchronized
name|NodeState
name|getPersistentNodeState
parameter_list|()
block|{
name|Path
name|path
init|=
name|getNodeDelta
argument_list|()
operator|.
name|getPersistentPath
argument_list|()
decl_stmt|;
name|String
name|baseRevision
init|=
name|sessionContext
operator|.
name|getRevision
argument_list|()
decl_stmt|;
if|if
condition|(
name|persistentNodeState
operator|==
literal|null
operator|||
operator|!
name|revision
operator|.
name|equals
argument_list|(
name|baseRevision
argument_list|)
condition|)
block|{
name|revision
operator|=
name|baseRevision
expr_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|persistentNodeState
operator|=
name|EmptyNodeState
operator|.
name|INSTANCE
expr_stmt|;
block|}
else|else
block|{
name|persistentNodeState
operator|=
operator|new
name|KernelNodeState
argument_list|(
name|sessionContext
operator|.
name|getMicrokernel
argument_list|()
argument_list|,
name|path
operator|.
name|toMkPath
argument_list|()
argument_list|,
name|revision
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|persistentNodeState
return|;
block|}
specifier|private
name|NodeDelta
name|getNodeDelta
parameter_list|()
block|{
return|return
name|nodeDelta
operator|=
name|getNodeStateProvider
argument_list|()
operator|.
name|getNodeDelta
argument_list|(
name|nodeDelta
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

