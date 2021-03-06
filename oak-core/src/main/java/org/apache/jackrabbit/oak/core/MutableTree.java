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
name|elements
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
name|core
operator|.
name|MutableRoot
operator|.
name|Move
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
name|tree
operator|.
name|impl
operator|.
name|AbstractMutableTree
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_class
specifier|final
class|class
name|MutableTree
extends|extends
name|AbstractMutableTree
block|{
comment|/**      * Underlying {@code Root} of this {@code Tree} instance      */
specifier|private
specifier|final
name|MutableRoot
name|root
decl_stmt|;
comment|/**      * Parent of this tree. Null for the root.      */
specifier|private
name|MutableTree
name|parent
decl_stmt|;
comment|/**      * Name of the tree      */
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|NodeBuilder
name|nodeBuilder
decl_stmt|;
comment|/** Pointer into the list of pending moves */
specifier|private
name|Move
name|pendingMoves
decl_stmt|;
name|MutableTree
parameter_list|(
annotation|@
name|NotNull
name|MutableRoot
name|root
parameter_list|,
annotation|@
name|NotNull
name|NodeBuilder
name|nodeBuilder
parameter_list|,
annotation|@
name|NotNull
name|Move
name|pendingMoves
parameter_list|)
block|{
name|this
argument_list|(
name|root
argument_list|,
name|pendingMoves
argument_list|,
literal|null
argument_list|,
name|nodeBuilder
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
specifier|private
name|MutableTree
parameter_list|(
annotation|@
name|NotNull
name|MutableRoot
name|root
parameter_list|,
annotation|@
name|NotNull
name|Move
name|pendingMoves
parameter_list|,
annotation|@
name|Nullable
name|MutableTree
name|parent
parameter_list|,
annotation|@
name|NotNull
name|NodeBuilder
name|nodeBuilder
parameter_list|,
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|checkNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
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
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeBuilder
operator|=
name|nodeBuilder
expr_stmt|;
name|this
operator|.
name|pendingMoves
operator|=
name|checkNotNull
argument_list|(
name|pendingMoves
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------------< AbstractMutableTree>---
annotation|@
name|Override
annotation|@
name|Nullable
specifier|protected
name|AbstractMutableTree
name|getParentOrNull
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|protected
name|NodeBuilder
name|getNodeBuilder
parameter_list|()
block|{
return|return
name|nodeBuilder
return|;
block|}
comment|//-----------------------------------------------------< AbstractTree>---
annotation|@
name|Override
annotation|@
name|NotNull
specifier|protected
name|MutableTree
name|createChild
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
return|return
operator|new
name|MutableTree
argument_list|(
name|root
argument_list|,
name|pendingMoves
argument_list|,
name|this
argument_list|,
name|nodeBuilder
operator|.
name|getChildNode
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|,
name|name
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< Tree>---
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|String
name|getName
parameter_list|()
block|{
name|beforeRead
argument_list|()
expr_stmt|;
return|return
name|name
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|String
name|getPath
parameter_list|()
block|{
name|beforeRead
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|getPath
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|Status
name|getStatus
parameter_list|()
block|{
name|beforeRead
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|getStatus
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
name|beforeRead
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|exists
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyState
name|getProperty
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
name|beforeRead
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|getProperty
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasProperty
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
name|beforeRead
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|hasProperty
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getPropertyCount
parameter_list|()
block|{
name|beforeRead
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|getPropertyCount
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|Status
name|getPropertyStatus
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
name|beforeRead
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|getPropertyStatus
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
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
name|beforeRead
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|getProperties
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|Tree
name|getChild
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
name|beforeRead
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|getChild
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasChild
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
name|beforeRead
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|hasChild
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getChildrenCount
parameter_list|(
name|long
name|max
parameter_list|)
block|{
name|beforeRead
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|getChildrenCount
argument_list|(
name|max
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|Iterable
argument_list|<
name|Tree
argument_list|>
name|getChildren
parameter_list|()
block|{
name|beforeRead
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|getChildren
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|remove
parameter_list|()
block|{
name|beforeWrite
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
name|super
operator|.
name|remove
argument_list|()
decl_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|root
operator|.
name|updated
argument_list|()
expr_stmt|;
block|}
return|return
name|success
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|Tree
name|addChild
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
name|beforeWrite
argument_list|()
expr_stmt|;
name|Tree
name|child
decl_stmt|;
if|if
condition|(
name|hasChild
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|child
operator|=
name|createChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|child
operator|=
name|super
operator|.
name|addChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|root
operator|.
name|updated
argument_list|()
expr_stmt|;
block|}
return|return
name|child
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
name|beforeWrite
argument_list|()
expr_stmt|;
name|super
operator|.
name|setOrderableChildren
argument_list|(
name|enable
argument_list|)
expr_stmt|;
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
name|beforeWrite
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
name|super
operator|.
name|orderBefore
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|root
operator|.
name|updated
argument_list|()
expr_stmt|;
block|}
return|return
name|success
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
annotation|@
name|NotNull
name|PropertyState
name|property
parameter_list|)
block|{
name|beforeWrite
argument_list|()
expr_stmt|;
name|super
operator|.
name|setProperty
argument_list|(
name|property
argument_list|)
expr_stmt|;
name|root
operator|.
name|updated
argument_list|()
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
name|NotNull
name|String
name|name
parameter_list|,
annotation|@
name|NotNull
name|T
name|value
parameter_list|)
block|{
name|beforeWrite
argument_list|()
expr_stmt|;
name|super
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|root
operator|.
name|updated
argument_list|()
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
name|NotNull
name|String
name|name
parameter_list|,
annotation|@
name|NotNull
name|T
name|value
parameter_list|,
annotation|@
name|NotNull
name|Type
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
name|beforeWrite
argument_list|()
expr_stmt|;
name|super
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|root
operator|.
name|updated
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeProperty
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
name|beforeWrite
argument_list|()
expr_stmt|;
name|super
operator|.
name|removeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|root
operator|.
name|updated
argument_list|()
expr_stmt|;
block|}
comment|//---------------------------------------------------------< internal>---
comment|/**      * Set the parent and name of this tree.      * @param parent  parent of this tree      * @param name  name of this tree      */
name|void
name|setParentAndName
parameter_list|(
annotation|@
name|NotNull
name|MutableTree
name|parent
parameter_list|,
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|checkNotNull
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
comment|/**      * Move this tree to the parent at {@code destParent} with the new name      * {@code newName}.      * @param newParent new parent for this tree      * @param newName   new name for this tree      */
name|boolean
name|moveTo
parameter_list|(
annotation|@
name|NotNull
name|MutableTree
name|newParent
parameter_list|,
annotation|@
name|NotNull
name|String
name|newName
parameter_list|)
block|{
name|name
operator|=
name|checkNotNull
argument_list|(
name|newName
argument_list|)
expr_stmt|;
name|parent
operator|=
name|checkNotNull
argument_list|(
name|newParent
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
name|nodeBuilder
operator|.
name|moveTo
argument_list|(
name|newParent
operator|.
name|nodeBuilder
argument_list|,
name|newName
argument_list|)
decl_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|parent
operator|.
name|updateChildOrder
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|newParent
operator|.
name|updateChildOrder
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|success
return|;
block|}
comment|/**      * Get a possibly non existing tree.      * @param path the path to the tree      * @return a {@link Tree} instance for the child at {@code path}.      */
annotation|@
name|NotNull
name|MutableTree
name|getTree
parameter_list|(
annotation|@
name|NotNull
name|String
name|path
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|isAbsolute
argument_list|(
name|checkNotNull
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|beforeRead
argument_list|()
expr_stmt|;
name|MutableTree
name|child
init|=
name|this
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|child
operator|=
operator|new
name|MutableTree
argument_list|(
name|root
argument_list|,
name|pendingMoves
argument_list|,
name|child
argument_list|,
name|child
operator|.
name|nodeBuilder
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|child
return|;
block|}
annotation|@
name|NotNull
name|String
name|getPathInternal
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
literal|"/"
return|;
block|}
else|else
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buildPath
argument_list|(
name|sb
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|buildPath
parameter_list|(
annotation|@
name|NotNull
name|StringBuilder
name|sb
parameter_list|)
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|buildPath
argument_list|(
name|checkNotNull
argument_list|(
name|sb
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|void
name|reconnect
parameter_list|()
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|reconnect
argument_list|()
expr_stmt|;
name|nodeBuilder
operator|=
name|parent
operator|.
name|nodeBuilder
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Verifies that this session is still alive and applies any pending      * moves that might affect this node. This method needs to be called      * at the beginning of all public read-only {@link Tree} methods to      * guarantee a consistent view of the tree. See {@link #beforeWrite()}      * for the equivalent method for write operations.      *      * @throws IllegalStateException if this session is closed      */
specifier|private
name|void
name|beforeRead
parameter_list|()
throws|throws
name|IllegalStateException
block|{
name|root
operator|.
name|checkLive
argument_list|()
expr_stmt|;
if|if
condition|(
name|applyPendingMoves
argument_list|()
condition|)
block|{
name|reconnect
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Like {@link #beforeRead()} but also checks that (after any pending      * moves have been applied) the current node exists and is visible.      * This method needs to be called at the beginning of all public      * {@link Tree} methods that modify this node to guarantee a consistent      * view of the tree and to throw an exception whenever there's an      * attempt to modify a missing node.      *      * @throws IllegalStateException if this node does not exist or      *                               if this session is closed      */
specifier|private
name|void
name|beforeWrite
parameter_list|()
throws|throws
name|IllegalStateException
block|{
name|beforeRead
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|super
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"This tree does not exist"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|boolean
name|applyPendingMoves
parameter_list|()
block|{
name|boolean
name|movesApplied
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|movesApplied
operator|=
name|parent
operator|.
name|applyPendingMoves
argument_list|()
expr_stmt|;
block|}
name|Move
name|old
init|=
name|pendingMoves
decl_stmt|;
name|pendingMoves
operator|=
name|pendingMoves
operator|.
name|apply
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|pendingMoves
operator|!=
name|old
condition|)
block|{
name|movesApplied
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|movesApplied
return|;
block|}
block|}
end_class

end_unit

