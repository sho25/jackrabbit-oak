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
name|tree
operator|.
name|impl
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
name|checkArgument
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
name|collect
operator|.
name|Lists
operator|.
name|newArrayListWithCapacity
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
name|plugins
operator|.
name|tree
operator|.
name|impl
operator|.
name|TreeConstants
operator|.
name|OAK_CHILD_ORDER
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
name|NodeBuilder
import|;
end_import

begin_comment
comment|/**  * {@code AbstractMutableTree} extends {@code AbstractTree} with implementations  * for most write methods of {@code Tree}. Furthermore it handles the ordering  * of siblings.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractMutableTree
extends|extends
name|AbstractTree
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|remove
parameter_list|()
block|{
name|String
name|name
init|=
name|getName
argument_list|()
decl_stmt|;
name|AbstractTree
name|parent
init|=
name|getParentOrNull
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
operator|&&
name|parent
operator|.
name|hasChild
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|getNodeBuilder
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
name|NodeBuilder
name|parentBuilder
init|=
name|parent
operator|.
name|getNodeBuilder
argument_list|()
decl_stmt|;
name|PropertyState
name|order
init|=
name|parentBuilder
operator|.
name|getProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|)
decl_stmt|;
if|if
condition|(
name|order
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|newArrayListWithCapacity
argument_list|(
name|order
operator|.
name|count
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|n
range|:
name|order
operator|.
name|getValue
argument_list|(
name|NAMES
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|n
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
name|parentBuilder
operator|.
name|setProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|,
name|names
argument_list|,
name|NAMES
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
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
name|Nonnull
annotation|@
name|Override
specifier|public
name|Tree
name|addChild
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|checkArgument
argument_list|(
operator|!
name|isHidden
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|hasChild
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|NodeBuilder
name|nodeBuilder
init|=
name|getNodeBuilder
argument_list|()
decl_stmt|;
name|nodeBuilder
operator|.
name|setChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|PropertyState
name|order
init|=
name|nodeBuilder
operator|.
name|getProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|)
decl_stmt|;
if|if
condition|(
name|order
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|newArrayListWithCapacity
argument_list|(
name|order
operator|.
name|count
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|n
range|:
name|order
operator|.
name|getValue
argument_list|(
name|NAMES
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|n
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
name|names
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|nodeBuilder
operator|.
name|setProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|,
name|names
argument_list|,
name|NAMES
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|createChild
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setOrderableChildren
parameter_list|(
name|boolean
name|enable
parameter_list|)
block|{
if|if
condition|(
name|enable
condition|)
block|{
name|updateChildOrder
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|getNodeBuilder
argument_list|()
operator|.
name|removeProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Updates the child order to match any added or removed child nodes that      * are not yet reflected in the {@link org.apache.jackrabbit.oak.plugins.tree.impl.TreeConstants#OAK_CHILD_ORDER}      * property. If the {@code force} flag is set, the child order is set      * in any case, otherwise only if the node already is orderable.      *      * @param force whether to add child order information if it doesn't exist      */
specifier|protected
name|void
name|updateChildOrder
parameter_list|(
name|boolean
name|force
parameter_list|)
block|{
if|if
condition|(
name|force
operator|||
name|hasOrderableChildren
argument_list|()
condition|)
block|{
name|getNodeBuilder
argument_list|()
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|,
name|getChildNames
argument_list|()
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|orderBefore
parameter_list|(
annotation|@
name|Nullable
name|String
name|name
parameter_list|)
block|{
name|String
name|thisName
init|=
name|getName
argument_list|()
decl_stmt|;
name|AbstractTree
name|parent
init|=
name|getParentOrNull
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
comment|// root does not have siblings
block|}
elseif|else
if|if
condition|(
name|thisName
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
comment|// same node
block|}
comment|// perform the reorder
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|newArrayListWithCapacity
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|parent
operator|.
name|getNodeBuilder
argument_list|()
decl_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
comment|// first try reordering based on the (potentially out-of-sync)
comment|// child order property in the parent node
for|for
control|(
name|String
name|n
range|:
name|builder
operator|.
name|getNames
argument_list|(
name|OAK_CHILD_ORDER
argument_list|)
control|)
block|{
if|if
condition|(
name|n
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|parent
operator|.
name|hasChild
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
name|thisName
argument_list|)
expr_stmt|;
name|found
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|n
operator|.
name|equals
argument_list|(
name|thisName
argument_list|)
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
comment|// if the target node name was not found in the parent's child order
comment|// property, we need to fall back to recreating the child order list
if|if
condition|(
operator|!
name|found
condition|)
block|{
name|names
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|n
range|:
name|parent
operator|.
name|getChildNames
argument_list|()
control|)
block|{
if|if
condition|(
name|n
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
name|thisName
argument_list|)
expr_stmt|;
name|found
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|n
operator|.
name|equals
argument_list|(
name|thisName
argument_list|)
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
name|thisName
argument_list|)
expr_stmt|;
name|found
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|found
condition|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|OAK_CHILD_ORDER
argument_list|,
name|names
argument_list|,
name|NAMES
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
comment|// no such sibling (not existing or not accessible)
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
annotation|@
name|Nonnull
name|PropertyState
name|property
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|!
name|isHidden
argument_list|(
name|checkNotNull
argument_list|(
name|property
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|getNodeBuilder
argument_list|()
operator|.
name|setProperty
argument_list|(
name|property
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|void
name|setProperty
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|T
name|value
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|checkArgument
argument_list|(
operator|!
name|isHidden
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|getNodeBuilder
argument_list|()
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|checkNotNull
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|void
name|setProperty
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|T
name|value
parameter_list|,
annotation|@
name|Nonnull
name|Type
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|checkArgument
argument_list|(
operator|!
name|isHidden
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|getNodeBuilder
argument_list|()
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|checkNotNull
argument_list|(
name|value
argument_list|)
argument_list|,
name|checkNotNull
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeProperty
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
name|getNodeBuilder
argument_list|()
operator|.
name|removeProperty
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

