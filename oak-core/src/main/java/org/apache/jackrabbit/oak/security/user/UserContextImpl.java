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
name|user
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
name|ContentSession
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
name|CoreValueFactory
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
name|spi
operator|.
name|commit
operator|.
name|ValidatorProvider
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
name|user
operator|.
name|MembershipProvider
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
name|user
operator|.
name|UserConfig
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
name|user
operator|.
name|UserContext
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
name|user
operator|.
name|UserProvider
import|;
end_import

begin_comment
comment|/**  * UserContextImpl... TODO  */
end_comment

begin_class
specifier|public
class|class
name|UserContextImpl
implements|implements
name|UserContext
block|{
specifier|private
specifier|final
name|UserConfig
name|config
decl_stmt|;
comment|// TODO add proper configuration
specifier|public
name|UserContextImpl
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|UserConfig
argument_list|(
literal|"admin"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|UserContextImpl
parameter_list|(
name|UserConfig
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|UserConfig
name|getConfig
parameter_list|()
block|{
return|return
name|config
return|;
block|}
annotation|@
name|Override
specifier|public
name|UserProvider
name|getUserProvider
parameter_list|(
name|ContentSession
name|contentSession
parameter_list|,
name|Root
name|root
parameter_list|)
block|{
return|return
operator|new
name|UserProviderImpl
argument_list|(
name|contentSession
argument_list|,
name|root
argument_list|,
name|config
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|MembershipProvider
name|getMembershipProvider
parameter_list|(
name|ContentSession
name|contentSession
parameter_list|,
name|Root
name|root
parameter_list|)
block|{
return|return
operator|new
name|MembershipProviderImpl
argument_list|(
name|contentSession
argument_list|,
name|root
argument_list|,
name|config
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ValidatorProvider
name|getUserValidatorProvider
parameter_list|(
name|CoreValueFactory
name|valueFactory
parameter_list|)
block|{
return|return
operator|new
name|UserValidatorProvider
argument_list|(
name|config
argument_list|)
return|;
block|}
block|}
end_class

end_unit

