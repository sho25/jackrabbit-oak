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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
operator|.
name|filter
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
name|Iterables
operator|.
name|size
import|;
end_import

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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|api
operator|.
name|Blob
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
name|tree
operator|.
name|TreeFactory
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
name|security
operator|.
name|Context
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
name|security
operator|.
name|authorization
operator|.
name|permission
operator|.
name|PermissionProvider
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
name|security
operator|.
name|authorization
operator|.
name|permission
operator|.
name|TreePermission
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

begin_class
class|class
name|SecureNodeBuilder
implements|implements
name|NodeBuilder
block|{
comment|/**      * Root builder, or {@code this} for the root builder itself.      */
specifier|private
specifier|final
name|SecureNodeBuilder
name|rootBuilder
decl_stmt|;
comment|/**      * Parent builder, or {@code null} for a root builder.      */
specifier|private
specifier|final
name|SecureNodeBuilder
name|parent
decl_stmt|;
comment|/**      * Name of this child node within the parent builder,      * or {@code null} for a root builder.      */
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/**      * Permissions provider for evaluating access rights to the underlying raw builder      */
specifier|private
specifier|final
name|LazyValue
argument_list|<
name|PermissionProvider
argument_list|>
name|permissionProvider
decl_stmt|;
comment|/**      * Access control context for evaluating access rights to the underlying raw builder      */
specifier|private
specifier|final
name|Context
name|acContext
decl_stmt|;
comment|/**      * Underlying node builder.      */
specifier|private
specifier|final
name|NodeBuilder
name|builder
decl_stmt|;
comment|/**      * Security context of this subtree, updated whenever the base revision      * changes.      */
specifier|private
name|TreePermission
name|treePermission
init|=
literal|null
decl_stmt|;
comment|// initialized lazily
comment|/**      * Possibly outdated reference to the tree permission of the root      * builder. Used to detect when the base state (and thus the security      * context) of the root builder has changed, and trigger an update of      * the tree permission of this builder.      *      * @see #treePermission      */
specifier|private
name|TreePermission
name|rootPermission
init|=
literal|null
decl_stmt|;
comment|// initialized lazily
name|SecureNodeBuilder
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|,
annotation|@
name|Nonnull
name|LazyValue
argument_list|<
name|PermissionProvider
argument_list|>
name|permissionProvider
parameter_list|,
annotation|@
name|Nonnull
name|Context
name|acContext
parameter_list|)
block|{
name|this
operator|.
name|rootBuilder
operator|=
name|this
expr_stmt|;
name|this
operator|.
name|parent
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|name
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|permissionProvider
operator|=
name|checkNotNull
argument_list|(
name|permissionProvider
argument_list|)
expr_stmt|;
name|this
operator|.
name|acContext
operator|=
name|checkNotNull
argument_list|(
name|acContext
argument_list|)
expr_stmt|;
name|this
operator|.
name|builder
operator|=
name|checkNotNull
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
specifier|private
name|SecureNodeBuilder
parameter_list|(
name|SecureNodeBuilder
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|rootBuilder
operator|=
name|parent
operator|.
name|rootBuilder
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
name|name
expr_stmt|;
name|this
operator|.
name|permissionProvider
operator|=
name|parent
operator|.
name|permissionProvider
expr_stmt|;
name|this
operator|.
name|acContext
operator|=
name|parent
operator|.
name|acContext
expr_stmt|;
name|this
operator|.
name|builder
operator|=
name|parent
operator|.
name|builder
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|getBaseState
parameter_list|()
block|{
return|return
operator|new
name|SecureNodeState
argument_list|(
name|builder
operator|.
name|getBaseState
argument_list|()
argument_list|,
name|getTreePermission
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|getNodeState
parameter_list|()
block|{
return|return
operator|new
name|SecureNodeState
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|getTreePermission
argument_list|()
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
name|builder
operator|.
name|exists
argument_list|()
operator|&&
operator|(
name|builder
operator|.
name|isReplaced
argument_list|()
operator|||
name|getTreePermission
argument_list|()
operator|.
name|canRead
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isNew
parameter_list|()
block|{
return|return
name|builder
operator|.
name|isNew
argument_list|()
operator|||
operator|(
name|builder
operator|.
name|isReplaced
argument_list|()
operator|&&
operator|!
name|getTreePermission
argument_list|()
operator|.
name|canRead
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isNew
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|builder
operator|.
name|isNew
argument_list|(
name|name
argument_list|)
operator|||
operator|(
name|builder
operator|.
name|isReplaced
argument_list|(
name|name
argument_list|)
operator|&&
operator|!
name|getTreePermission
argument_list|()
operator|.
name|canRead
argument_list|(
name|builder
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
argument_list|)
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isModified
parameter_list|()
block|{
return|return
name|builder
operator|.
name|isModified
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isReplaced
parameter_list|()
block|{
return|return
name|builder
operator|.
name|isReplaced
argument_list|()
operator|&&
operator|!
name|isNew
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isReplaced
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|builder
operator|.
name|isReplaced
argument_list|(
name|name
argument_list|)
operator|&&
operator|!
name|isNew
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|void
name|baseChanged
parameter_list|()
block|{
name|checkState
argument_list|(
name|parent
operator|==
literal|null
argument_list|)
expr_stmt|;
name|treePermission
operator|=
literal|null
expr_stmt|;
comment|// trigger re-evaluation of the context
name|rootPermission
operator|=
literal|null
expr_stmt|;
name|getTreePermission
argument_list|()
expr_stmt|;
comment|// sets both tree permissions and root node permissions
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|remove
parameter_list|()
block|{
return|return
name|exists
argument_list|()
operator|&&
name|builder
operator|.
name|remove
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|moveTo
parameter_list|(
name|NodeBuilder
name|newParent
parameter_list|,
name|String
name|newName
parameter_list|)
block|{
return|return
name|exists
argument_list|()
operator|&&
name|builder
operator|.
name|moveTo
argument_list|(
name|newParent
argument_list|,
name|newName
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
name|String
name|name
parameter_list|)
block|{
name|PropertyState
name|property
init|=
name|builder
operator|.
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
operator|new
name|ReadablePropertyPredicate
argument_list|()
operator|.
name|apply
argument_list|(
name|property
argument_list|)
condition|)
block|{
return|return
name|property
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
specifier|synchronized
name|long
name|getPropertyCount
parameter_list|()
block|{
if|if
condition|(
name|getTreePermission
argument_list|()
operator|.
name|canReadProperties
argument_list|()
operator|||
name|isNew
argument_list|()
condition|)
block|{
return|return
name|builder
operator|.
name|getPropertyCount
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|size
argument_list|(
name|filter
argument_list|(
name|builder
operator|.
name|getProperties
argument_list|()
argument_list|,
operator|new
name|ReadablePropertyPredicate
argument_list|()
argument_list|)
argument_list|)
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
name|?
extends|extends
name|PropertyState
argument_list|>
name|getProperties
parameter_list|()
block|{
if|if
condition|(
name|getTreePermission
argument_list|()
operator|.
name|canReadProperties
argument_list|()
operator|||
name|isNew
argument_list|()
condition|)
block|{
return|return
name|builder
operator|.
name|getProperties
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|filter
argument_list|(
name|builder
operator|.
name|getProperties
argument_list|()
argument_list|,
operator|new
name|ReadablePropertyPredicate
argument_list|()
argument_list|)
return|;
block|}
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
name|getString
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
name|STRING
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
annotation|@
name|Nonnull
specifier|public
name|NodeBuilder
name|setProperty
parameter_list|(
annotation|@
name|Nonnull
name|PropertyState
name|property
parameter_list|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|property
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
parameter_list|<
name|T
parameter_list|>
name|NodeBuilder
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|T
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
return|return
name|this
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
parameter_list|<
name|T
parameter_list|>
name|NodeBuilder
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
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
name|builder
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
return|return
name|this
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeBuilder
name|removeProperty
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
comment|// only remove properties that we can see
name|builder
operator|.
name|removeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
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
name|getChildNodeNames
parameter_list|()
block|{
return|return
name|filter
argument_list|(
name|builder
operator|.
name|getChildNodeNames
argument_list|()
argument_list|,
operator|new
name|Predicate
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
name|String
name|input
parameter_list|)
block|{
return|return
name|input
operator|!=
literal|null
operator|&&
name|getChildNode
argument_list|(
name|input
argument_list|)
operator|.
name|exists
argument_list|()
return|;
block|}
block|}
argument_list|)
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
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|builder
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
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
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeBuilder
name|child
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|getChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|setChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeBuilder
name|setChildNode
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
name|builder
operator|.
name|setChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
operator|new
name|SecureNodeBuilder
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeBuilder
name|setChildNode
parameter_list|(
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|nodeState
parameter_list|)
block|{
name|builder
operator|.
name|setChildNode
argument_list|(
name|name
argument_list|,
name|nodeState
argument_list|)
expr_stmt|;
return|return
operator|new
name|SecureNodeBuilder
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeBuilder
name|getChildNode
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|SecureNodeBuilder
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|long
name|getChildNodeCount
parameter_list|(
name|long
name|max
parameter_list|)
block|{
if|if
condition|(
name|getTreePermission
argument_list|()
operator|.
name|canReadAll
argument_list|()
condition|)
block|{
return|return
name|builder
operator|.
name|getChildNodeCount
argument_list|(
name|max
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|size
argument_list|(
name|getChildNodeNames
argument_list|()
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Blob
name|createBlob
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|builder
operator|.
name|createBlob
argument_list|(
name|stream
argument_list|)
return|;
block|}
comment|/**      * Permissions of this tree.      *      * @return The permissions for this tree.      */
specifier|private
name|TreePermission
name|getTreePermission
parameter_list|()
block|{
if|if
condition|(
name|treePermission
operator|==
literal|null
operator|||
name|rootPermission
operator|!=
name|rootBuilder
operator|.
name|treePermission
condition|)
block|{
name|NodeState
name|base
init|=
name|builder
operator|.
name|getBaseState
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
name|Tree
name|baseTree
init|=
name|TreeFactory
operator|.
name|createReadOnlyTree
argument_list|(
name|base
argument_list|)
decl_stmt|;
name|treePermission
operator|=
name|permissionProvider
operator|.
name|get
argument_list|()
operator|.
name|getTreePermission
argument_list|(
name|baseTree
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|rootPermission
operator|=
name|treePermission
expr_stmt|;
block|}
else|else
block|{
name|treePermission
operator|=
name|parent
operator|.
name|getTreePermission
argument_list|()
operator|.
name|getChildPermission
argument_list|(
name|name
argument_list|,
name|base
argument_list|)
expr_stmt|;
name|rootPermission
operator|=
name|parent
operator|.
name|rootPermission
expr_stmt|;
block|}
block|}
return|return
name|treePermission
return|;
block|}
comment|//------------------------------------------------------< inner classes>---
comment|/**      * Predicate for testing whether a given property is readable.      */
specifier|private
class|class
name|ReadablePropertyPredicate
implements|implements
name|Predicate
argument_list|<
name|PropertyState
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nonnull
name|PropertyState
name|property
parameter_list|)
block|{
return|return
name|getTreePermission
argument_list|()
operator|.
name|canRead
argument_list|(
name|property
argument_list|)
operator|||
name|isNew
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

