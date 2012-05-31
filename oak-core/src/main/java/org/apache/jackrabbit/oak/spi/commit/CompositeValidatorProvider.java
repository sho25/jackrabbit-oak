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
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_comment
comment|/**  * This {@code ValidatorProvider} aggregates a list of validator providers into  * a single validator provider.  */
end_comment

begin_class
specifier|public
class|class
name|CompositeValidatorProvider
implements|implements
name|ValidatorProvider
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|ValidatorProvider
argument_list|>
name|providers
decl_stmt|;
specifier|public
name|CompositeValidatorProvider
parameter_list|(
name|List
argument_list|<
name|ValidatorProvider
argument_list|>
name|providers
parameter_list|)
block|{
name|this
operator|.
name|providers
operator|=
name|providers
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Validator
name|getRootValidator
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|List
argument_list|<
name|Validator
argument_list|>
name|rootValidators
init|=
operator|new
name|ArrayList
argument_list|<
name|Validator
argument_list|>
argument_list|(
name|providers
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ValidatorProvider
name|provider
range|:
name|providers
control|)
block|{
name|rootValidators
operator|.
name|add
argument_list|(
name|provider
operator|.
name|getRootValidator
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|CompositeValidator
argument_list|(
name|rootValidators
argument_list|)
return|;
block|}
block|}
end_class

end_unit

