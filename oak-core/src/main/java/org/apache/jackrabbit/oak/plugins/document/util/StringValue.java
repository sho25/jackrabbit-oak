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
name|document
operator|.
name|util
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
name|cache
operator|.
name|CacheValue
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

begin_comment
comment|/**  * A cache value wrapping a simple string.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|StringValue
implements|implements
name|CacheValue
block|{
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
specifier|public
name|StringValue
parameter_list|(
annotation|@
name|Nonnull
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|checkNotNull
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMemory
parameter_list|()
block|{
return|return
literal|16
comment|// shallow size
operator|+
literal|40
operator|+
name|value
operator|.
name|length
argument_list|()
operator|*
literal|2
return|;
comment|// value
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|value
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|obj
operator|instanceof
name|StringValue
condition|)
block|{
name|StringValue
name|other
init|=
operator|(
name|StringValue
operator|)
name|obj
decl_stmt|;
return|return
name|value
operator|.
name|equals
argument_list|(
name|other
operator|.
name|value
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
name|String
name|asString
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
specifier|static
name|StringValue
name|fromString
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|StringValue
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
end_class

end_unit

