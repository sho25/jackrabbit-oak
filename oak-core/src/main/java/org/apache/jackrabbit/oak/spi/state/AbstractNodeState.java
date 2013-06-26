begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|spi
operator|.
name|state
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
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
name|BOOLEAN
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
name|Iterator
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

begin_comment
comment|/**  * Abstract base class for {@link NodeState} implementations.  * This base class contains default implementations of the  * {@link #equals(Object)} and {@link #hashCode()} methods based on  * the implemented interface.  *<p>  * This class also implements trivial (and potentially very slow) versions of  * the {@link #getProperty(String)} and {@link #getPropertyCount()} methods  * based on {@link #getProperties()}. The {@link #getChildNodeCount()} method  * is similarly implemented based on {@link #getChildNodeEntries()}.  * Subclasses should normally override these method with a more efficient  * alternatives.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractNodeState
implements|implements
name|NodeState
block|{
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
name|boolean
name|getBoolean
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|property
operator|!=
literal|null
operator|&&
name|property
operator|.
name|getType
argument_list|()
operator|==
name|BOOLEAN
operator|&&
name|property
operator|.
name|getValue
argument_list|(
name|BOOLEAN
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|String
name|getName
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
operator|&&
name|property
operator|.
name|getType
argument_list|()
operator|==
name|NAME
condition|)
block|{
return|return
name|property
operator|.
name|getValue
argument_list|(
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
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getNames
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
operator|&&
name|property
operator|.
name|getType
argument_list|()
operator|==
name|NAMES
condition|)
block|{
return|return
name|property
operator|.
name|getValue
argument_list|(
name|NAMES
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|emptyList
argument_list|()
return|;
block|}
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
for|for
control|(
name|PropertyState
name|property
range|:
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|property
return|;
block|}
block|}
return|return
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
name|count
argument_list|(
name|getProperties
argument_list|()
argument_list|)
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
specifier|public
name|long
name|getChildNodeCount
parameter_list|()
block|{
return|return
name|count
argument_list|(
name|getChildNodeEntries
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getChildNodeNames
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|getChildNodeEntries
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|ChildNodeEntry
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|ChildNodeEntry
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * Generic default comparison algorithm that simply walks through the      * property and child node lists of the given base state and compares      * the entries one by one with corresponding ones (if any) in this state.      */
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
operator|!
name|comparePropertiesAgainstBaseState
argument_list|(
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
name|base
operator|.
name|getChildNodeEntries
argument_list|()
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
argument_list|()
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
return|return
literal|true
return|;
block|}
comment|/**      * Returns a string representation of this node state.      *      * @return string representation      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
operator|!
name|exists
argument_list|()
condition|)
block|{
return|return
literal|"{N/A}"
return|;
block|}
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"{"
argument_list|)
decl_stmt|;
name|String
name|separator
init|=
literal|" "
decl_stmt|;
for|for
control|(
name|PropertyState
name|property
range|:
name|getProperties
argument_list|()
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|separator
argument_list|)
expr_stmt|;
name|separator
operator|=
literal|", "
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|property
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|separator
argument_list|)
expr_stmt|;
name|separator
operator|=
literal|", "
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|entry
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
comment|/**      * Checks whether the given object is equal to this one. Two node states      * are considered equal if all their properties and child nodes match,      * regardless of ordering. Subclasses may override this method with a      * more efficient equality check if one is available.      *      * @param that target of the comparison      * @return {@code true} if the objects are equal,      *         {@code false} otherwise      */
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
operator|==
literal|null
operator|||
operator|!
operator|(
name|that
operator|instanceof
name|NodeState
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|NodeState
name|other
init|=
operator|(
name|NodeState
operator|)
name|that
decl_stmt|;
if|if
condition|(
name|exists
argument_list|()
operator|!=
name|other
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getPropertyCount
argument_list|()
operator|!=
name|other
operator|.
name|getPropertyCount
argument_list|()
operator|||
name|getChildNodeCount
argument_list|()
operator|!=
name|other
operator|.
name|getChildNodeCount
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|PropertyState
name|property
range|:
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|property
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getProperty
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// TODO inefficient unless there are very few child nodes
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|getChildNodeEntries
argument_list|()
control|)
block|{
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
name|other
operator|.
name|getChildNode
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
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
comment|/**      * Returns a hash code that's compatible with how the      * {@link #equals(Object)} method is implemented. The current      * implementation simply returns zero for everything since      * {@link NodeState} instances are not intended for use as hash keys.      *      * @return hash code      */
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/**      * Compares the properties of {@code base} state with {@code this}      * state.      *      * @param base the base node state.      * @param diff the node state diff.      * @return {@code true} to continue the comparison, {@code false} to stop      */
specifier|protected
name|boolean
name|comparePropertiesAgainstBaseState
parameter_list|(
name|NodeState
name|base
parameter_list|,
name|NodeStateDiff
name|diff
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|baseProperties
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
name|PropertyState
name|beforeProperty
range|:
name|base
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|beforeProperty
operator|.
name|getName
argument_list|()
decl_stmt|;
name|PropertyState
name|afterProperty
init|=
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|afterProperty
operator|==
literal|null
condition|)
block|{
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
else|else
block|{
name|baseProperties
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|beforeProperty
operator|.
name|equals
argument_list|(
name|afterProperty
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|diff
operator|.
name|propertyChanged
argument_list|(
name|beforeProperty
argument_list|,
name|afterProperty
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
name|PropertyState
name|afterProperty
range|:
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|baseProperties
operator|.
name|contains
argument_list|(
name|afterProperty
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
name|propertyAdded
argument_list|(
name|afterProperty
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
comment|//-----------------------------------------------------------< private>--
specifier|protected
specifier|static
name|long
name|count
parameter_list|(
name|Iterable
argument_list|<
name|?
argument_list|>
name|iterable
parameter_list|)
block|{
name|long
name|n
init|=
literal|0
decl_stmt|;
name|Iterator
argument_list|<
name|?
argument_list|>
name|iterator
init|=
name|iterable
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|n
operator|++
expr_stmt|;
block|}
return|return
name|n
return|;
block|}
block|}
end_class

end_unit

