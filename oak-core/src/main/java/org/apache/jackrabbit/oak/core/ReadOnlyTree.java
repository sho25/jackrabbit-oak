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
name|JcrConstants
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
name|TreeLocation
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

begin_class
specifier|public
class|class
name|ReadOnlyTree
implements|implements
name|Tree
block|{
comment|/**      * Parent of this tree, {@code null} for the root      */
specifier|private
specifier|final
name|ReadOnlyTree
name|parent
decl_stmt|;
comment|/**      * Name of this tree      */
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/**      * Underlying node state      */
specifier|final
name|NodeState
name|state
decl_stmt|;
specifier|public
name|ReadOnlyTree
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|rootState
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
literal|""
argument_list|,
name|rootState
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ReadOnlyTree
parameter_list|(
annotation|@
name|Nullable
name|ReadOnlyTree
name|parent
parameter_list|,
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|state
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
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|checkNotNull
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
operator|!
name|name
operator|.
name|isEmpty
argument_list|()
operator|||
name|parent
operator|==
literal|null
argument_list|)
expr_stmt|;
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
name|boolean
name|isRoot
parameter_list|()
block|{
return|return
name|parent
operator|==
literal|null
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
name|isRoot
argument_list|()
condition|)
block|{
comment|// shortcut
return|return
literal|"/"
return|;
block|}
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
specifier|private
name|void
name|buildPath
parameter_list|(
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
name|sb
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
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Tree
name|getParent
parameter_list|()
block|{
name|checkState
argument_list|(
name|parent
operator|!=
literal|null
argument_list|,
literal|"root tree does not have a parent"
argument_list|)
expr_stmt|;
return|return
name|parent
return|;
block|}
annotation|@
name|Override
annotation|@
name|Deprecated
specifier|public
name|Tree
name|getParentOrNull
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
name|state
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
name|hasProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|Status
operator|.
name|EXISTING
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
specifier|public
name|boolean
name|hasProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|state
operator|.
name|hasProperty
argument_list|(
name|name
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
return|return
name|state
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
name|state
operator|.
name|getProperties
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|ReadOnlyTree
name|getChild
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|ReadOnlyTree
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|state
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
name|Deprecated
specifier|public
name|ReadOnlyTree
name|getChildOrNull
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
name|ReadOnlyTree
name|child
init|=
name|getChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|child
operator|.
name|exists
argument_list|()
condition|?
name|child
else|:
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Status
name|getStatus
parameter_list|()
block|{
return|return
name|Status
operator|.
name|EXISTING
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
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|TreeLocation
name|getLocation
parameter_list|()
block|{
return|return
operator|new
name|NodeLocation
argument_list|(
name|this
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
name|Nonnull
name|String
name|name
parameter_list|)
block|{
return|return
name|state
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
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
name|state
operator|.
name|getChildNodeCount
argument_list|()
return|;
block|}
comment|/**      * This implementation does not respect ordered child nodes, but always      * returns them in some implementation specific order.      *<p/>      * TODO: respect orderable children (needed?)      *      * @return the children.      */
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
specifier|final
name|Iterator
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|iterator
init|=
name|state
operator|.
name|getChildNodeEntries
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|Tree
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Tree
name|next
parameter_list|()
block|{
name|ChildNodeEntry
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
operator|new
name|ReadOnlyTree
argument_list|(
name|ReadOnlyTree
operator|.
name|this
argument_list|,
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
block|}
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|orderBefore
parameter_list|(
name|String
name|name
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
name|String
name|name
parameter_list|,
name|T
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
name|String
name|name
parameter_list|,
name|T
name|value
parameter_list|,
name|Type
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getPath
argument_list|()
operator|+
literal|": "
operator|+
name|state
return|;
block|}
comment|//-----------------------------------------------------------< internal>---
annotation|@
name|Nonnull
name|String
name|getIdentifier
parameter_list|()
block|{
name|PropertyState
name|property
init|=
name|state
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
return|return
name|property
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
return|;
block|}
elseif|else
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
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|parent
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
comment|//-------------------------------------------------------< TreeLocation>---
specifier|private
specifier|final
class|class
name|NodeLocation
extends|extends
name|AbstractNodeLocation
argument_list|<
name|ReadOnlyTree
argument_list|>
block|{
specifier|private
name|NodeLocation
parameter_list|(
name|ReadOnlyTree
name|tree
parameter_list|)
block|{
name|super
argument_list|(
name|tree
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|TreeLocation
name|createNodeLocation
parameter_list|(
name|ReadOnlyTree
name|tree
parameter_list|)
block|{
return|return
operator|new
name|NodeLocation
argument_list|(
name|tree
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|TreeLocation
name|createPropertyLocation
parameter_list|(
name|AbstractNodeLocation
argument_list|<
name|ReadOnlyTree
argument_list|>
name|parentLocation
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|PropertyLocation
argument_list|(
name|parentLocation
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|ReadOnlyTree
name|getParentTree
parameter_list|()
block|{
return|return
name|tree
operator|.
name|parent
return|;
block|}
annotation|@
name|Override
specifier|protected
name|ReadOnlyTree
name|getChildTree
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|tree
operator|.
name|getChildOrNull
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|PropertyState
name|getPropertyState
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|tree
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
name|Tree
name|getTree
parameter_list|()
block|{
return|return
name|tree
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|remove
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|private
specifier|final
class|class
name|PropertyLocation
extends|extends
name|AbstractPropertyLocation
argument_list|<
name|ReadOnlyTree
argument_list|>
block|{
specifier|private
name|PropertyLocation
parameter_list|(
name|AbstractNodeLocation
argument_list|<
name|ReadOnlyTree
argument_list|>
name|parentLocation
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|parentLocation
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyState
name|getProperty
parameter_list|()
block|{
return|return
name|parentLocation
operator|.
name|tree
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
name|boolean
name|remove
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|set
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

