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
name|JsonBuilder
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
name|model
operator|.
name|AbstractPropertyState
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
name|model
operator|.
name|Scalar
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
name|model
operator|.
name|Scalar
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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

begin_class
specifier|public
class|class
name|KernelPropertyState
extends|extends
name|AbstractPropertyState
block|{
comment|// fixme make package private
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|Scalar
name|value
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Scalar
argument_list|>
name|values
decl_stmt|;
specifier|public
name|KernelPropertyState
parameter_list|(
name|String
name|name
parameter_list|,
name|Scalar
name|value
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|values
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|KernelPropertyState
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|Scalar
argument_list|>
name|values
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|value
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|values
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
name|String
name|getEncodedValue
parameter_list|()
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|String
name|sep
init|=
literal|""
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"["
argument_list|)
decl_stmt|;
for|for
control|(
name|Scalar
name|s
range|:
name|values
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|sep
argument_list|)
expr_stmt|;
name|sep
operator|=
literal|","
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|encodeValue
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|encodeValue
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
specifier|public
name|boolean
name|isMultiValued
parameter_list|()
block|{
return|return
name|value
operator|==
literal|null
return|;
block|}
specifier|public
name|Scalar
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
name|List
argument_list|<
name|Scalar
argument_list|>
name|getValues
parameter_list|()
block|{
return|return
name|values
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
specifier|static
name|String
name|encodeValue
parameter_list|(
name|Scalar
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|STRING
condition|)
block|{
return|return
literal|'"'
operator|+
name|JsonBuilder
operator|.
name|escape
argument_list|(
name|value
operator|.
name|getString
argument_list|()
argument_list|)
operator|+
literal|'"'
return|;
block|}
else|else
block|{
return|return
name|value
operator|.
name|getString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

