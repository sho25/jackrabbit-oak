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
name|security
operator|.
name|internal
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
name|ConfigurationBase
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
name|SecurityConfiguration
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_class
specifier|public
specifier|final
class|class
name|SecurityProviderHelper
block|{
specifier|private
name|SecurityProviderHelper
parameter_list|()
block|{     }
specifier|public
specifier|static
name|SecurityProvider
name|updateConfig
parameter_list|(
annotation|@
name|NotNull
name|SecurityProvider
name|securityProvider
parameter_list|,
annotation|@
name|NotNull
name|SecurityConfiguration
name|sc
parameter_list|,
annotation|@
name|NotNull
name|Class
argument_list|<
name|?
extends|extends
name|SecurityConfiguration
argument_list|>
name|cls
parameter_list|)
block|{
name|Object
name|cc
init|=
name|securityProvider
operator|.
name|getConfiguration
argument_list|(
name|cls
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|cc
operator|instanceof
name|CompositeConfiguration
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
else|else
block|{
name|CompositeConfiguration
name|composite
init|=
operator|(
name|CompositeConfiguration
operator|)
name|cc
decl_stmt|;
name|SecurityConfiguration
name|defConfig
init|=
name|composite
operator|.
name|getDefaultConfig
argument_list|()
decl_stmt|;
if|if
condition|(
name|sc
operator|instanceof
name|ConfigurationBase
condition|)
block|{
name|ConfigurationBase
name|cb
init|=
operator|(
name|ConfigurationBase
operator|)
name|sc
decl_stmt|;
name|cb
operator|.
name|setSecurityProvider
argument_list|(
name|securityProvider
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setRootProvider
argument_list|(
operator|(
operator|(
name|ConfigurationBase
operator|)
name|defConfig
operator|)
operator|.
name|getRootProvider
argument_list|()
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setTreeProvider
argument_list|(
operator|(
operator|(
name|ConfigurationBase
operator|)
name|defConfig
operator|)
operator|.
name|getTreeProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|composite
operator|.
name|addConfiguration
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|composite
operator|.
name|addConfiguration
argument_list|(
name|defConfig
argument_list|)
expr_stmt|;
block|}
return|return
name|securityProvider
return|;
block|}
block|}
end_class

end_unit

