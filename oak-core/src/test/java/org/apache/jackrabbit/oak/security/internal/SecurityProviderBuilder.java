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
name|security
operator|.
name|SecurityProviderImpl
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

begin_class
specifier|public
class|class
name|SecurityProviderBuilder
block|{
specifier|private
name|ConfigurationParameters
name|configuration
init|=
literal|null
decl_stmt|;
specifier|private
name|SecurityConfiguration
name|sc
decl_stmt|;
specifier|private
name|Class
argument_list|<
name|?
extends|extends
name|SecurityConfiguration
argument_list|>
name|cls
decl_stmt|;
specifier|public
name|SecurityProviderBuilder
name|with
parameter_list|(
annotation|@
name|Nonnull
name|ConfigurationParameters
name|configuration
parameter_list|)
block|{
name|this
operator|.
name|configuration
operator|=
name|checkNotNull
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|SecurityProviderBuilder
name|with
parameter_list|(
annotation|@
name|Nonnull
name|SecurityConfiguration
name|sc
parameter_list|,
annotation|@
name|Nonnull
name|Class
argument_list|<
name|?
extends|extends
name|SecurityConfiguration
argument_list|>
name|cls
parameter_list|)
block|{
name|this
operator|.
name|sc
operator|=
name|sc
expr_stmt|;
name|this
operator|.
name|cls
operator|=
name|cls
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|SecurityProvider
name|build
parameter_list|()
block|{
name|SecurityProvider
name|sp
decl_stmt|;
if|if
condition|(
name|configuration
operator|!=
literal|null
condition|)
block|{
name|sp
operator|=
operator|new
name|SecurityProviderImpl
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sp
operator|=
operator|new
name|SecurityProviderImpl
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|sc
operator|!=
literal|null
operator|&&
name|cls
operator|!=
literal|null
condition|)
block|{
name|Object
name|cc
init|=
name|sp
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
if|if
condition|(
name|sc
operator|instanceof
name|ConfigurationBase
condition|)
block|{
operator|(
operator|(
name|ConfigurationBase
operator|)
name|sc
operator|)
operator|.
name|setSecurityProvider
argument_list|(
name|sp
argument_list|)
expr_stmt|;
block|}
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
block|}
return|return
name|sp
return|;
block|}
block|}
end_class

end_unit

