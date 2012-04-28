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
name|core
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
name|oak
operator|.
name|api
operator|.
name|CoreValue
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
name|Tree
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
name|NodeStateBuilder
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
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
name|util
operator|.
name|PagedIterator
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
name|util
operator|.
name|Iterators
operator|.
name|flatten
import|;
end_import

begin_comment
comment|/**  * Implementation of tree based on {@link NodeStateBuilder}s. Each subtree  * has an associated node state builder which is used for building the new  * trees resulting from calling mutating methods.  */
end_comment

begin_class
specifier|public
class|class
name|TreeImpl
implements|implements
name|Tree
block|{
comment|/** Underlying store */
specifier|private
specifier|final
name|NodeStore
name|store
decl_stmt|;
comment|/**      * Underlying persistent state or {@code null} if this instance represents an      * added tree      */
specifier|private
specifier|final
name|NodeState
name|baseState
decl_stmt|;
specifier|private
specifier|final
name|NodeStateBuilder
name|builder
decl_stmt|;
comment|/** Listener for changes on this tree */
specifier|private
specifier|final
name|Listener
name|listener
decl_stmt|;
comment|/** Name of this tree */
specifier|private
name|String
name|name
decl_stmt|;
comment|/** Parent of this tree */
specifier|private
name|TreeImpl
name|parent
decl_stmt|;
specifier|private
name|TreeImpl
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|NodeState
name|baseState
parameter_list|,
name|NodeStateBuilder
name|builder
parameter_list|,
name|TreeImpl
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|Listener
name|listener
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
name|this
operator|.
name|baseState
operator|=
name|baseState
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
comment|/**      * Create a new instance which represents the root of a tree.      * @param store  underlying store to the tree      * @param nodeStateBuilder  builder for the root      * @param listener  change listener for the tree. May be {@code null} if      *                  listening to changes is not needed.      */
name|TreeImpl
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|NodeStateBuilder
name|nodeStateBuilder
parameter_list|,
name|Listener
name|listener
parameter_list|)
block|{
name|this
argument_list|(
name|store
argument_list|,
name|nodeStateBuilder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|nodeStateBuilder
argument_list|,
literal|null
argument_list|,
literal|""
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
comment|/**      * Listener for changes on {@code Tree}s      */
interface|interface
name|Listener
block|{
comment|/**          * The child of the given {@code name} has been added to {@code tree}.          * @param parent  parent to which a child was added          * @param name  name of the added child          */
name|void
name|addChild
parameter_list|(
name|TreeImpl
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
function_decl|;
comment|/**          * The child of the given {@code name} has been removed from {@code tree}          * @param parent  parent from which a child was removed          * @param name  name of the removed child          */
name|void
name|removeChild
parameter_list|(
name|TreeImpl
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
function_decl|;
comment|/**          * The property of the given {@code name} and {@code value} has been set.          * @param parent  parent on which the property was set.          * @param name  name of the property          * @param value  value of the property          */
name|void
name|setProperty
parameter_list|(
name|TreeImpl
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|CoreValue
name|value
parameter_list|)
function_decl|;
comment|/**          * The property of the given {@code name} and {@code values} has been set.          * @param parent  parent on which the property was set.          * @param name  name of the property          * @param values  values of the property          */
name|void
name|setProperty
parameter_list|(
name|TreeImpl
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|CoreValue
argument_list|>
name|values
parameter_list|)
function_decl|;
comment|/**          * The property of the given {@code name} has been removed.          * @param parent  parent on which the property was removed.          * @param name  name of the property          */
name|void
name|removeProperty
parameter_list|(
name|TreeImpl
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
function_decl|;
comment|/**          * The child with the given {@code name} has been moved.          * @param sourceParent  parent from which the child was moved          * @param sourceName  name of the moved child          * @param moved  moved child          */
name|void
name|move
parameter_list|(
name|TreeImpl
name|sourceParent
parameter_list|,
name|String
name|sourceName
parameter_list|,
name|TreeImpl
name|moved
parameter_list|)
function_decl|;
comment|/**          * The child with the given {@code name} been copied.          * @param sourceParent  parent from which the child way copied          * @param sourceName  name of the copied child          * @param copied  copied child          */
name|void
name|copy
parameter_list|(
name|TreeImpl
name|sourceParent
parameter_list|,
name|String
name|sourceName
parameter_list|,
name|TreeImpl
name|copied
parameter_list|)
function_decl|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
name|name
return|;
block|}
else|else
block|{
name|String
name|path
init|=
name|parent
operator|.
name|getPath
argument_list|()
decl_stmt|;
return|return
name|path
operator|.
name|isEmpty
argument_list|()
condition|?
name|name
else|:
name|path
operator|+
literal|'/'
operator|+
name|name
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Tree
name|getParent
parameter_list|()
block|{
return|return
name|parent
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
name|getNodeState
argument_list|()
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Status
name|getPropertyStatus
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|baseState
operator|==
literal|null
condition|)
block|{
comment|// This instance is NEW...
if|if
condition|(
name|hasProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// ...so all children are new
return|return
name|Status
operator|.
name|NEW
return|;
block|}
else|else
block|{
comment|// ...unless they don't exist.
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|hasProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// We have the property...
if|if
condition|(
name|baseState
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// ...but didn't have it before. So its NEW.
return|return
name|Status
operator|.
name|NEW
return|;
block|}
else|else
block|{
comment|// ... and did have it before. So...
name|PropertyState
name|base
init|=
name|baseState
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|PropertyState
name|head
init|=
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|base
operator|.
name|equals
argument_list|(
name|head
argument_list|)
condition|)
block|{
comment|// ...it's EXISTING if it hasn't changed
return|return
name|Status
operator|.
name|EXISTING
return|;
block|}
else|else
block|{
comment|// ...and MODIFIED otherwise.
return|return
name|Status
operator|.
name|MODIFIED
return|;
block|}
block|}
block|}
else|else
block|{
comment|// We don't have the property
if|if
condition|(
name|baseState
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// ...and didn't have it before. So it doesn't exist.
return|return
literal|null
return|;
block|}
else|else
block|{
comment|// ...and didn't have it before. So it's REMOVED
return|return
name|Status
operator|.
name|REMOVED
return|;
block|}
block|}
block|}
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
name|getNodeState
argument_list|()
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getPropertyCount
parameter_list|()
block|{
return|return
name|getNodeState
argument_list|()
operator|.
name|getPropertyCount
argument_list|()
return|;
block|}
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
name|getNodeState
argument_list|()
operator|.
name|getProperties
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|TreeImpl
name|getChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|NodeStateBuilder
name|childBuilder
init|=
name|builder
operator|.
name|getChildBuilder
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|childBuilder
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
name|NodeState
name|childBaseState
init|=
name|baseState
operator|==
literal|null
condition|?
literal|null
else|:
name|baseState
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
operator|new
name|TreeImpl
argument_list|(
name|store
argument_list|,
name|childBaseState
argument_list|,
name|childBuilder
argument_list|,
name|this
argument_list|,
name|name
argument_list|,
name|listener
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Status
name|getChildStatus
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|baseState
operator|==
literal|null
condition|)
block|{
comment|// This instance is NEW...
if|if
condition|(
name|hasChild
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// ...so all children are new
return|return
name|Status
operator|.
name|NEW
return|;
block|}
else|else
block|{
comment|// ...unless they don't exist.
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|hasChild
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// We have the child...
if|if
condition|(
name|baseState
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// ...but didn't have it before. So its NEW.
return|return
name|Status
operator|.
name|NEW
return|;
block|}
else|else
block|{
comment|// ... and did have it before. So...
if|if
condition|(
name|isSame
argument_list|(
name|baseState
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|,
name|getNodeState
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|)
condition|)
block|{
comment|// ...it's EXISTING if it hasn't changed
return|return
name|Status
operator|.
name|EXISTING
return|;
block|}
else|else
block|{
comment|// ...and MODIFIED otherwise.
return|return
name|Status
operator|.
name|MODIFIED
return|;
block|}
block|}
block|}
else|else
block|{
comment|// We don't have the child
if|if
condition|(
name|baseState
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// ...and didn't have it before. So it doesn't exist.
return|return
literal|null
return|;
block|}
else|else
block|{
comment|// ...and didn't have it before. So it's REMOVED
return|return
name|Status
operator|.
name|REMOVED
return|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getNodeState
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getChildrenCount
parameter_list|()
block|{
return|return
name|getNodeState
argument_list|()
operator|.
name|getChildNodeCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|Tree
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|Tree
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Tree
argument_list|>
name|iterator
parameter_list|()
block|{
name|Iterator
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|childEntries
init|=
name|flatten
argument_list|(
operator|new
name|PagedIterator
argument_list|<
name|ChildNodeEntry
argument_list|>
argument_list|(
literal|1024
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
name|getNodeState
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
return|return
name|Iterators
operator|.
name|map
argument_list|(
name|childEntries
argument_list|,
operator|new
name|Function1
argument_list|<
name|ChildNodeEntry
argument_list|,
name|Tree
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Tree
name|apply
parameter_list|(
name|ChildNodeEntry
name|entry
parameter_list|)
block|{
name|NodeStateBuilder
name|childBuilder
init|=
name|builder
operator|.
name|getChildBuilder
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|TreeImpl
argument_list|(
name|store
argument_list|,
name|childBuilder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|childBuilder
argument_list|,
name|TreeImpl
operator|.
name|this
argument_list|,
name|entry
operator|.
name|getName
argument_list|()
argument_list|,
name|listener
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|Tree
name|addChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|builder
operator|.
name|addNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|TreeImpl
name|added
init|=
name|getChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|added
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|addChild
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|added
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|removeChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|boolean
name|result
init|=
name|builder
operator|.
name|removeNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
condition|)
block|{
name|listener
operator|.
name|removeChild
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|CoreValue
name|value
parameter_list|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|setProperty
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|CoreValue
argument_list|>
name|values
parameter_list|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
expr_stmt|;
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|setProperty
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|builder
operator|.
name|removeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|removeProperty
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Move this tree to the parent at {@code destParent} with the new name      * {@code destName}.      *      * @param destParent  new parent for this tree      * @param destName  new name for this tree      * @return  {@code true} if successful, {@code false otherwise}. I.e.      * when {@code destName} already exists at {@code destParent}      */
specifier|public
name|boolean
name|move
parameter_list|(
name|TreeImpl
name|destParent
parameter_list|,
name|String
name|destName
parameter_list|)
block|{
name|boolean
name|result
init|=
name|builder
operator|.
name|moveTo
argument_list|(
name|destParent
operator|.
name|builder
argument_list|,
name|destName
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
condition|)
block|{
name|TreeImpl
name|oldParent
init|=
name|parent
decl_stmt|;
name|String
name|oldName
init|=
name|name
decl_stmt|;
name|name
operator|=
name|destName
expr_stmt|;
name|parent
operator|=
name|destParent
expr_stmt|;
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|move
argument_list|(
name|oldParent
argument_list|,
name|oldName
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**      * Copy this tree to the parent at {@code destParent} with the name {@code destName}.      *      * @param destParent  parent for the copied tree      * @param destName  name for the copied tree      * @return  {@code true} if successful, {@code false otherwise}. I.e.      * when {@code destName} already exists at {@code destParent}      */
specifier|public
name|boolean
name|copy
parameter_list|(
name|TreeImpl
name|destParent
parameter_list|,
name|String
name|destName
parameter_list|)
block|{
name|boolean
name|result
init|=
name|builder
operator|.
name|copyTo
argument_list|(
name|destParent
operator|.
name|builder
argument_list|,
name|destName
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
condition|)
block|{
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|copy
argument_list|(
name|parent
argument_list|,
name|name
argument_list|,
name|destParent
operator|.
name|getChild
argument_list|(
name|destName
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
return|return
name|result
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|NodeState
name|getNodeState
parameter_list|()
block|{
return|return
name|builder
operator|.
name|getNodeState
argument_list|()
return|;
block|}
specifier|private
name|boolean
name|isSame
parameter_list|(
name|NodeState
name|state1
parameter_list|,
name|NodeState
name|state2
parameter_list|)
block|{
specifier|final
name|boolean
index|[]
name|isDirty
init|=
block|{
literal|false
block|}
decl_stmt|;
name|store
operator|.
name|compare
argument_list|(
name|state1
argument_list|,
name|state2
argument_list|,
operator|new
name|NodeStateDiff
argument_list|()
block|{
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
name|isDirty
index|[
literal|0
index|]
operator|=
literal|true
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
name|isDirty
index|[
literal|0
index|]
operator|=
literal|true
expr_stmt|;
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
name|isDirty
index|[
literal|0
index|]
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|isDirty
index|[
literal|0
index|]
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
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
comment|// cut transitivity here
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
name|isDirty
index|[
literal|0
index|]
operator|=
literal|true
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
operator|!
name|isDirty
index|[
literal|0
index|]
return|;
block|}
block|}
end_class

end_unit

