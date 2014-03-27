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
name|spi
operator|.
name|security
package|;
end_package

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
name|AbstractSecurityTest
import|;
end_import

begin_comment
comment|/**  * AbstractCompositeConfigurationTest... TODO  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractCompositeConfigurationTest
parameter_list|<
name|T
extends|extends
name|SecurityConfiguration
parameter_list|>
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|CompositeConfiguration
argument_list|<
name|T
argument_list|>
name|compositeConfiguration
decl_stmt|;
specifier|public
name|void
name|setCompositeConfiguration
parameter_list|(
name|CompositeConfiguration
argument_list|<
name|T
argument_list|>
name|compositeConfiguration
parameter_list|)
block|{
name|this
operator|.
name|compositeConfiguration
operator|=
name|compositeConfiguration
expr_stmt|;
block|}
specifier|public
name|CompositeConfiguration
name|getCompositeConfiguration
parameter_list|()
block|{
return|return
name|compositeConfiguration
return|;
block|}
specifier|public
name|List
argument_list|<
name|T
argument_list|>
name|getConfigurations
parameter_list|()
block|{
return|return
name|compositeConfiguration
operator|.
name|getConfigurations
argument_list|()
return|;
block|}
specifier|public
name|void
name|addConfiguration
parameter_list|(
name|T
name|configuration
parameter_list|)
block|{
name|compositeConfiguration
operator|.
name|addConfiguration
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeConfiguration
parameter_list|(
name|T
name|configuration
parameter_list|)
block|{
name|compositeConfiguration
operator|.
name|removeConfiguration
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setDefault
parameter_list|(
name|T
name|configuration
parameter_list|)
block|{
name|compositeConfiguration
operator|.
name|setDefaultConfig
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

