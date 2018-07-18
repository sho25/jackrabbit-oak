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
name|authorization
operator|.
name|cug
operator|.
name|impl
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
name|security
operator|.
name|authorization
operator|.
name|composite
operator|.
name|CompositeAuthorizationConfiguration
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
name|security
operator|.
name|internal
operator|.
name|SecurityProviderBuilder
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
name|security
operator|.
name|internal
operator|.
name|SecurityProviderHelper
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
name|ConfigurationParameters
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
name|authorization
operator|.
name|AuthorizationConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_class
specifier|final
class|class
name|CugSecurityProvider
block|{
specifier|private
name|CugSecurityProvider
parameter_list|()
block|{}
specifier|public
specifier|static
name|SecurityProvider
name|newTestSecurityProvider
parameter_list|(
annotation|@
name|NotNull
name|ConfigurationParameters
name|configuration
parameter_list|)
block|{
name|CugConfiguration
name|cugConfiguration
init|=
operator|new
name|CugConfiguration
argument_list|()
decl_stmt|;
name|ConfigurationParameters
name|params
init|=
name|configuration
operator|.
name|getConfigValue
argument_list|(
name|AuthorizationConfiguration
operator|.
name|NAME
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|cugConfiguration
operator|.
name|setParameters
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|SecurityProvider
name|sp
init|=
name|SecurityProviderBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|with
argument_list|(
name|configuration
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SecurityProviderHelper
operator|.
name|updateConfig
argument_list|(
name|sp
argument_list|,
name|cugConfiguration
argument_list|,
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|sp
return|;
block|}
specifier|public
specifier|static
name|CugConfiguration
name|getCugConfiguration
parameter_list|(
annotation|@
name|NotNull
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|AuthorizationConfiguration
name|ac
init|=
name|securityProvider
operator|.
name|getConfiguration
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|ac
operator|instanceof
name|CompositeAuthorizationConfiguration
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
for|for
control|(
name|AuthorizationConfiguration
name|config
range|:
operator|(
operator|(
name|CompositeAuthorizationConfiguration
operator|)
name|ac
operator|)
operator|.
name|getConfigurations
argument_list|()
control|)
block|{
if|if
condition|(
name|config
operator|instanceof
name|CugConfiguration
condition|)
block|{
return|return
operator|(
name|CugConfiguration
operator|)
name|config
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

