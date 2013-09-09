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
name|commit
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
name|java
operator|.
name|util
operator|.
name|List
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

begin_comment
comment|/**  * Validator that detects changes to a specified subtree and delegates the  * validation of such changes to another given validator.  *  * @see SubtreeExcludingValidator  * @since Oak 0.3  */
end_comment

begin_class
specifier|public
class|class
name|SubtreeValidator
extends|extends
name|DefaultValidator
block|{
specifier|private
specifier|final
name|Validator
name|validator
decl_stmt|;
specifier|private
specifier|final
name|String
name|head
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|tail
decl_stmt|;
specifier|public
name|SubtreeValidator
parameter_list|(
name|Validator
name|validator
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
block|{
name|this
argument_list|(
name|validator
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|SubtreeValidator
parameter_list|(
name|Validator
name|validator
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|path
parameter_list|)
block|{
name|this
operator|.
name|validator
operator|=
name|checkNotNull
argument_list|(
name|validator
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
operator|!
name|path
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|head
operator|=
name|path
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|tail
operator|=
name|path
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
name|path
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Validator
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
name|descend
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Validator
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
return|return
name|descend
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Validator
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
return|return
name|descend
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|private
name|Validator
name|descend
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
operator|!
name|head
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|tail
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|validator
return|;
block|}
else|else
block|{
return|return
operator|new
name|SubtreeValidator
argument_list|(
name|validator
argument_list|,
name|tail
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

