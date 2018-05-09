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
name|authorization
operator|.
name|accesscontrol
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
name|api
operator|.
name|security
operator|.
name|authorization
operator|.
name|PrivilegeManager
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
name|security
operator|.
name|authorization
operator|.
name|ProviderCtx
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
name|CommitInfo
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
name|Validator
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
name|authorization
operator|.
name|AuthorizationConfiguration
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
name|restriction
operator|.
name|RestrictionProvider
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
name|privilege
operator|.
name|PrivilegeBitsProvider
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
name|privilege
operator|.
name|PrivilegeConfiguration
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
comment|/**  * {@code AccessControlValidatorProvider} aimed to provide a root validator  * that makes sure access control related content modifications (adding, modifying  * and removing access control policies) are valid according to the  * constraints defined by this access control implementation.  */
end_comment

begin_class
specifier|public
class|class
name|AccessControlValidatorProvider
extends|extends
name|ValidatorProvider
block|{
specifier|private
specifier|final
name|ProviderCtx
name|providerCtx
decl_stmt|;
specifier|public
name|AccessControlValidatorProvider
parameter_list|(
annotation|@
name|Nonnull
name|ProviderCtx
name|providerCtx
parameter_list|)
block|{
name|this
operator|.
name|providerCtx
operator|=
name|providerCtx
expr_stmt|;
block|}
comment|//--------------------------------------------------< ValidatorProvider>---
annotation|@
name|Nonnull
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
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
block|{
name|RestrictionProvider
name|restrictionProvider
init|=
name|getConfig
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getRestrictionProvider
argument_list|()
decl_stmt|;
name|Root
name|root
init|=
name|providerCtx
operator|.
name|getRootProvider
argument_list|()
operator|.
name|createReadOnlyRoot
argument_list|(
name|before
argument_list|)
decl_stmt|;
name|PrivilegeManager
name|privilegeManager
init|=
name|getConfig
argument_list|(
name|PrivilegeConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getPrivilegeManager
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|PrivilegeBitsProvider
name|privilegeBitsProvider
init|=
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|root
argument_list|)
decl_stmt|;
return|return
operator|new
name|AccessControlValidator
argument_list|(
name|after
argument_list|,
name|privilegeManager
argument_list|,
name|privilegeBitsProvider
argument_list|,
name|restrictionProvider
argument_list|,
name|providerCtx
argument_list|)
return|;
block|}
specifier|private
parameter_list|<
name|T
parameter_list|>
name|T
name|getConfig
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|configClass
parameter_list|)
block|{
return|return
name|providerCtx
operator|.
name|getSecurityProvider
argument_list|()
operator|.
name|getConfiguration
argument_list|(
name|configClass
argument_list|)
return|;
block|}
block|}
end_class

end_unit

