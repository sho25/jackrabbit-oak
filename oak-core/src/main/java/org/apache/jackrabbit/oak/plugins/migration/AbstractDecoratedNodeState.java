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
name|migration
package|;
end_package

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
name|base
operator|.
name|Predicates
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyStates
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
name|ReadOnlyBuilder
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
name|Collection
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicates
operator|.
name|notNull
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
name|tree
operator|.
name|TreeConstants
operator|.
name|OAK_CHILD_ORDER
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractDecoratedNodeState
extends|extends
name|AbstractNodeState
block|{
specifier|protected
specifier|final
name|NodeState
name|delegate
decl_stmt|;
specifier|protected
name|AbstractDecoratedNodeState
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|NodeState
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
specifier|public
name|NodeState
name|getDelegate
parameter_list|()
block|{
return|return
name|delegate
return|;
block|}
specifier|protected
name|boolean
name|hideChild
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|NodeState
name|delegateChild
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Nonnull
specifier|protected
specifier|abstract
name|NodeState
name|decorateChild
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|NodeState
name|delegateChild
parameter_list|)
function_decl|;
annotation|@
name|Nonnull
specifier|private
name|NodeState
name|decorate
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|NodeState
name|child
parameter_list|)
block|{
return|return
name|hideChild
argument_list|(
name|name
argument_list|,
name|child
argument_list|)
condition|?
name|EmptyNodeState
operator|.
name|MISSING_NODE
else|:
name|decorateChild
argument_list|(
name|name
argument_list|,
name|child
argument_list|)
return|;
block|}
specifier|protected
name|boolean
name|hideProperty
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|name
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Nonnull
specifier|protected
name|Iterable
argument_list|<
name|PropertyState
argument_list|>
name|getNewPropertyStates
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
annotation|@
name|CheckForNull
specifier|protected
specifier|abstract
name|PropertyState
name|decorateProperty
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|PropertyState
name|delegatePropertyState
parameter_list|)
function_decl|;
annotation|@
name|CheckForNull
specifier|private
name|PropertyState
name|decorate
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|PropertyState
name|property
parameter_list|)
block|{
return|return
name|property
operator|==
literal|null
operator|||
name|hideProperty
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
condition|?
literal|null
else|:
name|decorateProperty
argument_list|(
name|property
argument_list|)
return|;
block|}
comment|/**      * Convenience method to help implementations that hide nodes set the      * :childOrder (OAK_CHILD_ORDER) property to its correct value.      *<br>      * Intended to be used to implement {@link #decorateProperty(PropertyState)}.      *      * @param nodeState The current node state.      * @param propertyState The property that chould be checked.      * @return The original propertyState, unless the property is called {@code :childOrder}.      */
specifier|protected
specifier|static
name|PropertyState
name|fixChildOrderPropertyState
parameter_list|(
name|NodeState
name|nodeState
parameter_list|,
name|PropertyState
name|propertyState
parameter_list|)
block|{
if|if
condition|(
name|propertyState
operator|!=
literal|null
operator|&&
name|OAK_CHILD_ORDER
operator|.
name|equals
argument_list|(
name|propertyState
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|childNodeNames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Iterables
operator|.
name|addAll
argument_list|(
name|childNodeNames
argument_list|,
name|nodeState
operator|.
name|getChildNodeNames
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
init|=
name|Iterables
operator|.
name|filter
argument_list|(
name|propertyState
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|,
name|Predicates
operator|.
name|in
argument_list|(
name|childNodeNames
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|,
name|values
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
return|;
block|}
return|return
name|propertyState
return|;
block|}
comment|/**      * The AbstractDecoratedNodeState implementation returns a ReadOnlyBuilder, which      * will fail for any mutable operation.      *      * This method can be overridden to return a different NodeBuilder implementation.      *      * @return a NodeBuilder instance corresponding to this NodeState.      */
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeBuilder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|ReadOnlyBuilder
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|exists
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasChildNode
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|name
parameter_list|)
block|{
return|return
name|getChildNode
argument_list|(
name|name
argument_list|)
operator|.
name|exists
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|getChildNode
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
return|return
name|decorate
argument_list|(
name|name
argument_list|,
name|delegate
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
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
specifier|final
name|Iterable
argument_list|<
name|ChildNodeEntry
argument_list|>
name|transformed
init|=
name|Iterables
operator|.
name|transform
argument_list|(
name|delegate
operator|.
name|getChildNodeEntries
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|ChildNodeEntry
argument_list|,
name|ChildNodeEntry
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|ChildNodeEntry
name|apply
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|ChildNodeEntry
name|childNodeEntry
parameter_list|)
block|{
if|if
condition|(
name|childNodeEntry
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|name
init|=
name|childNodeEntry
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|NodeState
name|nodeState
init|=
name|decorate
argument_list|(
name|name
argument_list|,
name|childNodeEntry
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeState
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
operator|new
name|MemoryChildNodeEntry
argument_list|(
name|name
argument_list|,
name|nodeState
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|Iterables
operator|.
name|filter
argument_list|(
name|transformed
argument_list|,
name|notNull
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|PropertyState
name|getProperty
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
name|PropertyState
name|ps
init|=
name|decorate
argument_list|(
name|delegate
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|==
literal|null
condition|)
block|{
for|for
control|(
name|PropertyState
name|p
range|:
name|getNewPropertyStates
argument_list|()
control|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|ps
operator|=
name|p
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|ps
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
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
specifier|final
name|Iterable
argument_list|<
name|PropertyState
argument_list|>
name|propertyStates
init|=
name|Iterables
operator|.
name|transform
argument_list|(
name|delegate
operator|.
name|getProperties
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|PropertyState
argument_list|,
name|PropertyState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|PropertyState
name|apply
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|PropertyState
name|propertyState
parameter_list|)
block|{
return|return
name|decorate
argument_list|(
name|propertyState
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|Iterables
operator|.
name|filter
argument_list|(
name|Iterables
operator|.
name|concat
argument_list|(
name|propertyStates
argument_list|,
name|getNewPropertyStates
argument_list|()
argument_list|)
argument_list|,
name|notNull
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Note that any implementation-specific optimizations of wrapped NodeStates      * will not work if a AbstractDecoratedNodeState is passed into their {@code #equals()}      * method. This implementation will compare the wrapped NodeState, however. So      * optimizations work when calling {@code #equals()} on a ReportingNodeState.      *      * @param other Object to compare with this NodeState.      * @return true if the given object is equal to this NodeState, false otherwise.      */
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|this
operator|.
name|getClass
argument_list|()
operator|==
name|other
operator|.
name|getClass
argument_list|()
condition|)
block|{
specifier|final
name|AbstractDecoratedNodeState
name|o
init|=
operator|(
name|AbstractDecoratedNodeState
operator|)
name|other
decl_stmt|;
return|return
name|delegate
operator|.
name|equals
argument_list|(
name|o
operator|.
name|delegate
argument_list|)
return|;
block|}
return|return
name|delegate
operator|.
name|equals
argument_list|(
name|other
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|compareAgainstBaseState
parameter_list|(
specifier|final
name|NodeState
name|base
parameter_list|,
specifier|final
name|NodeStateDiff
name|diff
parameter_list|)
block|{
return|return
name|AbstractNodeState
operator|.
name|compareAgainstBaseState
argument_list|(
name|this
argument_list|,
name|base
argument_list|,
operator|new
name|DecoratingDiff
argument_list|(
name|diff
argument_list|,
name|this
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|DecoratingDiff
implements|implements
name|NodeStateDiff
block|{
specifier|private
specifier|final
name|NodeStateDiff
name|diff
decl_stmt|;
specifier|private
name|AbstractDecoratedNodeState
name|nodeState
decl_stmt|;
specifier|private
name|DecoratingDiff
parameter_list|(
specifier|final
name|NodeStateDiff
name|diff
parameter_list|,
specifier|final
name|AbstractDecoratedNodeState
name|nodeState
parameter_list|)
block|{
name|this
operator|.
name|diff
operator|=
name|diff
expr_stmt|;
name|this
operator|.
name|nodeState
operator|=
name|nodeState
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeAdded
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|NodeState
name|after
parameter_list|)
block|{
return|return
name|diff
operator|.
name|childNodeAdded
argument_list|(
name|name
argument_list|,
name|nodeState
operator|.
name|decorate
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeChanged
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|NodeState
name|before
parameter_list|,
specifier|final
name|NodeState
name|after
parameter_list|)
block|{
return|return
name|diff
operator|.
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|nodeState
operator|.
name|decorate
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeDeleted
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|NodeState
name|before
parameter_list|)
block|{
return|return
name|diff
operator|.
name|childNodeDeleted
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyAdded
parameter_list|(
specifier|final
name|PropertyState
name|after
parameter_list|)
block|{
return|return
name|diff
operator|.
name|propertyAdded
argument_list|(
name|nodeState
operator|.
name|decorate
argument_list|(
name|after
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyChanged
parameter_list|(
specifier|final
name|PropertyState
name|before
parameter_list|,
specifier|final
name|PropertyState
name|after
parameter_list|)
block|{
return|return
name|diff
operator|.
name|propertyChanged
argument_list|(
name|nodeState
operator|.
name|decorate
argument_list|(
name|before
argument_list|)
argument_list|,
name|nodeState
operator|.
name|decorate
argument_list|(
name|after
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyDeleted
parameter_list|(
specifier|final
name|PropertyState
name|before
parameter_list|)
block|{
return|return
name|diff
operator|.
name|propertyDeleted
argument_list|(
name|nodeState
operator|.
name|decorate
argument_list|(
name|before
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

