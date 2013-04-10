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
name|BINARIES
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
name|BOOLEANS
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
name|LONGS
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
name|STRINGS
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * TODO document  */
end_comment

begin_class
class|class
name|JsopDiff
implements|implements
name|NodeStateDiff
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JsopDiff
operator|.
name|class
argument_list|)
decl_stmt|;
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
specifier|public
specifier|static
name|String
name|diffToJsop
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|JsopDiff
name|diff
init|=
operator|new
name|JsopDiff
argument_list|(
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|String
name|writeBlob
parameter_list|(
name|Blob
name|blob
parameter_list|)
block|{
return|return
literal|"Blob{"
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|blob
operator|.
name|sha256
argument_list|()
argument_list|)
operator|+
literal|'}'
return|;
block|}
block|}
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|diff
argument_list|)
expr_stmt|;
return|return
name|diff
operator|.
name|toString
argument_list|()
return|;
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
name|toJsonValue
argument_list|(
name|propertyState
argument_list|,
name|jsop
argument_list|)
expr_stmt|;
name|jsop
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|toJsonValue
argument_list|(
name|propertyState
argument_list|,
name|jsop
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|toJsonValue
parameter_list|(
name|PropertyState
name|property
parameter_list|,
name|JsopBuilder
name|jsop
parameter_list|)
block|{
name|int
name|type
init|=
name|property
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
for|for
control|(
name|boolean
name|value
range|:
name|property
operator|.
name|getValue
argument_list|(
name|BOOLEANS
argument_list|)
control|)
block|{
name|jsop
operator|.
name|value
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|PropertyType
operator|.
name|LONG
case|:
for|for
control|(
name|long
name|value
range|:
name|property
operator|.
name|getValue
argument_list|(
name|LONGS
argument_list|)
control|)
block|{
name|jsop
operator|.
name|value
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|PropertyType
operator|.
name|BINARY
case|:
for|for
control|(
name|Blob
name|value
range|:
name|property
operator|.
name|getValue
argument_list|(
name|BINARIES
argument_list|)
control|)
block|{
name|String
name|binId
init|=
name|writeBlob
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|jsop
operator|.
name|value
argument_list|(
name|TypeCodes
operator|.
name|encode
argument_list|(
name|type
argument_list|,
name|binId
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
for|for
control|(
name|String
name|value
range|:
name|property
operator|.
name|getValue
argument_list|(
name|STRINGS
argument_list|)
control|)
block|{
if|if
condition|(
name|PropertyType
operator|.
name|STRING
operator|!=
name|type
operator|||
name|TypeCodes
operator|.
name|split
argument_list|(
name|value
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|value
operator|=
name|TypeCodes
operator|.
name|encode
argument_list|(
name|type
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|jsop
operator|.
name|value
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
comment|/**      * Make sure {@code blob} is persisted and return the id of the persisted blob.      * @param blob  blob to persist      * @return  id of the persisted blob      */
specifier|protected
name|String
name|writeBlob
parameter_list|(
name|Blob
name|blob
parameter_list|)
block|{
name|String
name|blobId
decl_stmt|;
if|if
condition|(
name|blob
operator|instanceof
name|KernelBlob
condition|)
block|{
name|blobId
operator|=
operator|(
operator|(
name|KernelBlob
operator|)
name|blob
operator|)
operator|.
name|getBinaryID
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|InputStream
name|is
init|=
name|blob
operator|.
name|getNewStream
argument_list|()
decl_stmt|;
name|blobId
operator|=
name|kernel
operator|.
name|write
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|close
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
return|return
name|blobId
return|;
block|}
specifier|private
specifier|static
name|void
name|close
parameter_list|(
name|InputStream
name|stream
parameter_list|)
block|{
try|try
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error closing stream"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

