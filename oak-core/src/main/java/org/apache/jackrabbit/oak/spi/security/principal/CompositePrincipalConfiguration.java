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
operator|.
name|principal
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|api
operator|.
name|security
operator|.
name|principal
operator|.
name|PrincipalManager
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
name|Root
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
name|namepath
operator|.
name|NamePathMapper
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
name|security
operator|.
name|CompositeConfiguration
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
name|security
operator|.
name|SecurityProvider
import|;
end_import

begin_comment
comment|/**  * {@link PrincipalConfiguration} that combines different principal provider  * implementations that share a common principal manager implementation.  */
end_comment

begin_class
specifier|public
class|class
name|CompositePrincipalConfiguration
extends|extends
name|CompositeConfiguration
argument_list|<
name|PrincipalConfiguration
argument_list|>
implements|implements
name|PrincipalConfiguration
block|{
specifier|public
name|CompositePrincipalConfiguration
parameter_list|()
block|{
name|super
argument_list|(
name|PrincipalConfiguration
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CompositePrincipalConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|super
argument_list|(
name|PrincipalConfiguration
operator|.
name|NAME
argument_list|,
name|securityProvider
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PrincipalManager
name|getPrincipalManager
parameter_list|(
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
return|return
operator|new
name|PrincipalManagerImpl
argument_list|(
name|getPrincipalProvider
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PrincipalProvider
name|getPrincipalProvider
parameter_list|(
specifier|final
name|Root
name|root
parameter_list|,
specifier|final
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
return|return
operator|new
name|CompositePrincipalProvider
argument_list|(
name|Lists
operator|.
name|transform
argument_list|(
name|getConfigurations
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|PrincipalConfiguration
argument_list|,
name|PrincipalProvider
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PrincipalProvider
name|apply
parameter_list|(
name|PrincipalConfiguration
name|principalConfiguration
parameter_list|)
block|{
return|return
name|principalConfiguration
operator|.
name|getPrincipalProvider
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

