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
name|kernel
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|api
operator|.
name|MicroKernel
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
name|json
operator|.
name|JsopBuilder
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

begin_class
class|class
name|JsopDiff
implements|implements
name|NodeStateDiff
block|{
specifier|private
specifier|final
name|MicroKernel
name|kernel
decl_stmt|;
specifier|protected
specifier|final
name|JsopBuilder
name|jsop
decl_stmt|;
specifier|protected
specifier|final
name|String
name|path
decl_stmt|;
specifier|public
name|JsopDiff
parameter_list|(
name|MicroKernel
name|kernel
parameter_list|,
name|JsopBuilder
name|jsop
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|kernel
operator|=
name|kernel
expr_stmt|;
name|this
operator|.
name|jsop
operator|=
name|jsop
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
specifier|public
name|JsopDiff
parameter_list|(
name|MicroKernel
name|kernel
parameter_list|)
block|{
name|this
argument_list|(
name|kernel
argument_list|,
operator|new
name|JsopBuilder
argument_list|()
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|diffToJsop
parameter_list|(
name|MicroKernel
name|kernel
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|String
name|path
parameter_list|,
name|JsopBuilder
name|jsop
parameter_list|)
block|{
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|JsopDiff
argument_list|(
name|kernel
argument_list|,
name|jsop
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|JsopDiff
name|createChildDiff
parameter_list|(
name|JsopBuilder
name|jsop
parameter_list|,
name|String
name|path
parameter_list|)
block|{
return|return
operator|new
name|JsopDiff
argument_list|(
name|kernel
argument_list|,
name|jsop
argument_list|,
name|path
argument_list|)
return|;
block|}
comment|//-----------------------------------------------------< NodeStateDiff>--
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
name|jsop
operator|.
name|tag
argument_list|(
literal|'^'
argument_list|)
operator|.
name|key
argument_list|(
name|buildPath
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|toJson
argument_list|(
name|after
argument_list|,
name|jsop
argument_list|)
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
name|jsop
operator|.
name|tag
argument_list|(
literal|'^'
argument_list|)
operator|.
name|key
argument_list|(
name|buildPath
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|toJson
argument_list|(
name|after
argument_list|,
name|jsop
argument_list|)
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
name|jsop
operator|.
name|tag
argument_list|(
literal|'^'
argument_list|)
operator|.
name|key
argument_list|(
name|buildPath
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
operator|.
name|value
argument_list|(
literal|null
argument_list|)
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
name|jsop
operator|.
name|tag
argument_list|(
literal|'+'
argument_list|)
operator|.
name|key
argument_list|(
name|buildPath
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|toJson
argument_list|(
name|after
argument_list|,
name|jsop
argument_list|)
expr_stmt|;
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
name|jsop
operator|.
name|tag
argument_list|(
literal|'-'
argument_list|)
operator|.
name|value
argument_list|(
name|buildPath
argument_list|(
name|name
argument_list|)
argument_list|)
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
name|String
name|path
init|=
name|buildPath
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|createChildDiff
argument_list|(
name|jsop
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------------< Object>--
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|jsop
operator|.
name|toString
argument_list|()
return|;
block|}
comment|//-----------------------------------------------------------< private>--
specifier|protected
name|String
name|buildPath
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
return|;
block|}
specifier|private
name|void
name|toJson
parameter_list|(
name|NodeState
name|nodeState
parameter_list|,
name|JsopBuilder
name|jsop
parameter_list|)
block|{
name|jsop
operator|.
name|object
argument_list|()
expr_stmt|;
for|for
control|(
name|PropertyState
name|property
range|:
name|nodeState
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|jsop
operator|.
name|key
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|toJson
argument_list|(
name|property
argument_list|,
name|jsop
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ChildNodeEntry
name|child
range|:
name|nodeState
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|jsop
operator|.
name|key
argument_list|(
name|child
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|toJson
argument_list|(
name|child
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|jsop
argument_list|)
expr_stmt|;
block|}
name|jsop
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|toJson
parameter_list|(
name|PropertyState
name|propertyState
parameter_list|,
name|JsopBuilder
name|jsop
parameter_list|)
block|{
if|if
condition|(
name|propertyState
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|jsop
operator|.
name|array
argument_list|()
expr_stmt|;
for|for
control|(
name|CoreValue
name|value
range|:
name|propertyState
operator|.
name|getValues
argument_list|()
control|)
block|{
name|toJson
argument_list|(
name|value
argument_list|,
name|jsop
argument_list|)
expr_stmt|;
block|}
name|jsop
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|toJson
argument_list|(
name|propertyState
operator|.
name|getValue
argument_list|()
argument_list|,
name|jsop
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|toJson
parameter_list|(
name|CoreValue
name|value
parameter_list|,
name|JsopBuilder
name|jsop
parameter_list|)
block|{
name|int
name|type
init|=
name|value
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|PropertyType
operator|.
name|BOOLEAN
condition|)
block|{
name|jsop
operator|.
name|value
argument_list|(
name|value
operator|.
name|getBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|PropertyType
operator|.
name|LONG
condition|)
block|{
name|jsop
operator|.
name|value
argument_list|(
name|value
operator|.
name|getLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|string
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|PropertyType
operator|.
name|BINARY
operator|&&
operator|!
operator|(
name|value
operator|instanceof
name|BinaryValue
operator|)
condition|)
block|{
name|string
operator|=
name|kernel
operator|.
name|write
argument_list|(
name|value
operator|.
name|getNewStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|string
operator|=
name|value
operator|.
name|getString
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|!=
name|PropertyType
operator|.
name|STRING
operator|||
name|CoreValueMapper
operator|.
name|startsWithHint
argument_list|(
name|string
argument_list|)
condition|)
block|{
name|string
operator|=
name|CoreValueMapper
operator|.
name|getHintForType
argument_list|(
name|type
argument_list|)
operator|+
literal|':'
operator|+
name|string
expr_stmt|;
block|}
name|jsop
operator|.
name|value
argument_list|(
name|string
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

