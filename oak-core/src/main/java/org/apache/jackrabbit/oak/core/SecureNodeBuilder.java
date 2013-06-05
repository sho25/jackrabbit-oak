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
comment|/**      * Underlying node builder.      */
specifier|private
specifier|final
name|NodeBuilder
name|builder
decl_stmt|;
comment|/**      * Security context of this subtree.      */
specifier|private
specifier|final
name|SecurityContext
name|context
decl_stmt|;
name|SecureNodeBuilder
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|,
annotation|@
name|Nonnull
name|SecurityContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
name|checkNotNull
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|checkNotNull
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|NodeState
name|getBaseState
parameter_list|()
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
name|base
operator|!=
literal|null
condition|)
block|{
comment|// TODO: should use a missing state instead of null
name|base
operator|=
operator|new
name|SecureNodeState
argument_list|(
name|base
argument_list|,
name|context
argument_list|)
expr_stmt|;
comment|// TODO: baseContext?
block|}
return|return
name|base
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
name|context
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
name|context
operator|.
name|canReadThisNode
argument_list|()
return|;
comment|// TODO: isNew()?
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
return|;
comment|// TODO: might disclose hidden content
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
name|void
name|reset
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|state
parameter_list|)
throws|throws
name|IllegalStateException
block|{
name|builder
operator|.
name|reset
argument_list|(
name|state
argument_list|)
expr_stmt|;
comment|// NOTE: can be dangerous with SecureNodeState
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
name|context
operator|.
name|canReadProperty
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
name|context
operator|.
name|canReadAll
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
name|context
operator|.
name|canReadAll
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
elseif|else
if|if
condition|(
name|context
operator|.
name|canReadThisNode
argument_list|()
condition|)
block|{
comment|// TODO: check DENY_PROPERTIES?
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
name|NodeBuilder
name|child
init|=
name|builder
operator|.
name|setChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
operator|new
name|SecureNodeBuilder
argument_list|(
name|child
argument_list|,
name|context
operator|.
name|getChildContext
argument_list|(
name|name
argument_list|,
name|child
operator|.
name|getBaseState
argument_list|()
argument_list|)
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
name|NodeBuilder
name|child
init|=
name|builder
operator|.
name|setChildNode
argument_list|(
name|name
argument_list|,
name|nodeState
argument_list|)
decl_stmt|;
return|return
operator|new
name|SecureNodeBuilder
argument_list|(
name|child
argument_list|,
name|context
operator|.
name|getChildContext
argument_list|(
name|name
argument_list|,
name|child
operator|.
name|getBaseState
argument_list|()
argument_list|)
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
name|NodeBuilder
name|child
init|=
name|builder
operator|.
name|getChildNode
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|context
operator|.
name|canReadAll
argument_list|()
condition|)
block|{
return|return
operator|new
name|SecureNodeBuilder
argument_list|(
name|child
argument_list|,
name|context
operator|.
name|getChildContext
argument_list|(
name|name
argument_list|,
name|child
operator|.
name|getBaseState
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|child
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|long
name|getChildNodeCount
parameter_list|()
block|{
if|if
condition|(
name|context
operator|.
name|canReadAll
argument_list|()
condition|)
block|{
return|return
name|builder
operator|.
name|getChildNodeCount
argument_list|()
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
name|context
operator|.
name|canReadProperty
argument_list|(
name|property
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

