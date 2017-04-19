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
name|blob
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
name|oak
operator|.
name|api
operator|.
name|Blob
import|;
end_import

begin_comment
comment|/**  * Exposes the blob along with the Node id from which referenced  */
end_comment

begin_class
specifier|public
class|class
name|ReferencedBlob
block|{
specifier|private
name|Blob
name|blob
decl_stmt|;
specifier|private
name|String
name|id
decl_stmt|;
specifier|public
name|ReferencedBlob
parameter_list|(
name|Blob
name|blob
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|setBlob
argument_list|(
name|blob
argument_list|)
expr_stmt|;
name|this
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Blob
name|getBlob
parameter_list|()
block|{
return|return
name|blob
return|;
block|}
specifier|public
name|void
name|setBlob
parameter_list|(
name|Blob
name|blob
parameter_list|)
block|{
name|this
operator|.
name|blob
operator|=
name|blob
expr_stmt|;
block|}
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
specifier|public
name|void
name|setId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ReferencedBlob{"
operator|+
literal|"blob="
operator|+
name|blob
operator|+
literal|", id='"
operator|+
name|id
operator|+
literal|'\''
operator|+
literal|'}'
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ReferencedBlob
name|that
init|=
operator|(
name|ReferencedBlob
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|getBlob
argument_list|()
operator|.
name|equals
argument_list|(
name|that
operator|.
name|getBlob
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
operator|!
operator|(
name|getId
argument_list|()
operator|!=
literal|null
condition|?
operator|!
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|that
operator|.
name|getId
argument_list|()
argument_list|)
else|:
name|that
operator|.
name|getId
argument_list|()
operator|!=
literal|null
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|getBlob
argument_list|()
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|getId
argument_list|()
operator|!=
literal|null
condition|?
name|getId
argument_list|()
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit
