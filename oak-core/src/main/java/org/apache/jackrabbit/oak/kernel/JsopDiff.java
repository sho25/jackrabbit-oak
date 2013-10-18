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

begin_comment
comment|/**  * TODO document  */
end_comment

begin_class
specifier|public
class|class
name|JsopDiff
implements|implements
name|NodeStateDiff
block|{
specifier|private
specifier|final
name|JsopBuilder
name|jsop
decl_stmt|;
specifier|private
specifier|final
name|BlobSerializer
name|blobs
decl_stmt|;
specifier|protected
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
name|JsopDiff
parameter_list|(
name|JsopBuilder
name|jsop
parameter_list|,
name|String
name|path
parameter_list|,
name|BlobSerializer
name|blobs
parameter_list|)
block|{
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
name|this
operator|.
name|blobs
operator|=
name|blobs
expr_stmt|;
block|}
name|JsopDiff
parameter_list|(
name|BlobSerializer
name|blobs
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|JsopBuilder
argument_list|()
argument_list|,
literal|"/"
argument_list|,
name|blobs
argument_list|)
expr_stmt|;
block|}
name|JsopDiff
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|BlobSerializer
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create the JSOP diff between {@code before} and {@code after} for      * debugging purposes.      *<p>      * This method does not store binaries but returns them inlined      * in the format<code>Blob{...}</code>, where the<code>...</code>      * is implementation-dependent - typically the SHA256 hash of the binary.      *      * @param before  before node state      * @param after  after node state      * @return  jsop diff between {@code before} and {@code after}      */
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
argument_list|()
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
comment|//-----------------------------------------------------< NodeStateDiff>--
annotation|@
name|Override
specifier|public
name|boolean
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
operator|new
name|JsonSerializer
argument_list|(
name|jsop
argument_list|,
name|blobs
argument_list|)
operator|.
name|serialize
argument_list|(
name|after
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
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
operator|new
name|JsonSerializer
argument_list|(
name|jsop
argument_list|,
name|blobs
argument_list|)
operator|.
name|serialize
argument_list|(
name|after
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
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
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
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
operator|new
name|JsonSerializer
argument_list|(
name|jsop
argument_list|,
name|blobs
argument_list|)
operator|.
name|serialize
argument_list|(
name|after
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
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
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
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
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|JsopDiff
argument_list|(
name|jsop
argument_list|,
name|buildPath
argument_list|(
name|name
argument_list|)
argument_list|,
name|blobs
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
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
block|}
end_class

end_unit

