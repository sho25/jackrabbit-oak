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

begin_comment
comment|/**  * A node builder that throws an {@link UnsupportedOperationException} on  * all attempts to modify the given base state.  */
end_comment

begin_class
specifier|public
class|class
name|ReadOnlyBuilder
implements|implements
name|NodeBuilder
block|{
specifier|private
specifier|final
name|NodeState
name|state
decl_stmt|;
specifier|public
name|ReadOnlyBuilder
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
specifier|protected
name|RuntimeException
name|unsupported
parameter_list|()
block|{
return|return
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This builder is read-only."
argument_list|)
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
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isConnected
parameter_list|()
block|{
return|return
literal|true
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
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getNodeState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getBaseState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
throw|throw
name|unsupported
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getChildNodeCount
parameter_list|()
block|{
return|return
name|state
operator|.
name|getChildNodeCount
argument_list|()
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
name|Iterable
argument_list|<
name|String
argument_list|>
name|getChildNodeNames
parameter_list|()
block|{
return|return
name|state
operator|.
name|getChildNodeNames
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeBuilder
name|setNode
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|nodeState
parameter_list|)
block|{
throw|throw
name|unsupported
argument_list|()
throw|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeBuilder
name|removeNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
throw|throw
name|unsupported
argument_list|()
throw|;
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
throw|throw
name|unsupported
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|NodeBuilder
name|setProperty
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
throw|throw
name|unsupported
argument_list|()
throw|;
block|}
annotation|@
name|Override
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
name|T
name|value
parameter_list|)
block|{
throw|throw
name|unsupported
argument_list|()
throw|;
block|}
annotation|@
name|Override
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
name|unsupported
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|ReadOnlyBuilder
name|child
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|NodeState
name|child
init|=
name|state
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
operator|new
name|ReadOnlyBuilder
argument_list|(
name|child
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
name|unsupported
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

