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
name|principalbased
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
name|plugins
operator|.
name|tree
operator|.
name|RootProvider
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
name|plugins
operator|.
name|tree
operator|.
name|TreeProvider
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
name|Context
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
name|principal
operator|.
name|PrincipalConfiguration
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
name|checkState
import|;
end_import

begin_class
specifier|final
class|class
name|MgrProviderImpl
implements|implements
name|MgrProvider
block|{
specifier|private
specifier|final
name|PrincipalBasedAuthorizationConfiguration
name|config
decl_stmt|;
specifier|private
name|NamePathMapper
name|namePathMapper
decl_stmt|;
specifier|private
name|Root
name|root
decl_stmt|;
specifier|private
name|Context
name|ctx
decl_stmt|;
specifier|private
name|RestrictionProvider
name|restrictionProvider
decl_stmt|;
specifier|private
name|PrincipalManager
name|principalManager
decl_stmt|;
specifier|private
name|PrivilegeManager
name|privilegeManager
decl_stmt|;
specifier|private
name|PrivilegeBitsProvider
name|privilegeBitsProvider
decl_stmt|;
name|MgrProviderImpl
parameter_list|(
annotation|@
name|NotNull
name|PrincipalBasedAuthorizationConfiguration
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|NamePathMapper
operator|.
name|DEFAULT
expr_stmt|;
block|}
name|MgrProviderImpl
parameter_list|(
annotation|@
name|NotNull
name|PrincipalBasedAuthorizationConfiguration
name|config
parameter_list|,
annotation|@
name|NotNull
name|Root
name|root
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|reset
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|SecurityProvider
name|getSecurityProvider
parameter_list|()
block|{
return|return
name|config
operator|.
name|getSecurityProvider
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|(
annotation|@
name|NotNull
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
name|this
operator|.
name|ctx
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|restrictionProvider
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|principalManager
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|privilegeManager
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|privilegeBitsProvider
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Root
name|getRoot
parameter_list|()
block|{
name|checkRootInitialized
argument_list|()
expr_stmt|;
return|return
name|root
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|NamePathMapper
name|getNamePathMapper
parameter_list|()
block|{
return|return
name|namePathMapper
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Context
name|getContext
parameter_list|()
block|{
if|if
condition|(
name|ctx
operator|==
literal|null
condition|)
block|{
comment|// make sure the context allows to reveal any kind of protected access control/permission content not just
comment|// those defined by this module.
name|ctx
operator|=
name|getSecurityProvider
argument_list|()
operator|.
name|getConfiguration
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getContext
argument_list|()
expr_stmt|;
block|}
return|return
name|ctx
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|PrivilegeManager
name|getPrivilegeManager
parameter_list|()
block|{
name|checkRootInitialized
argument_list|()
expr_stmt|;
if|if
condition|(
name|privilegeManager
operator|==
literal|null
condition|)
block|{
name|privilegeManager
operator|=
name|getSecurityProvider
argument_list|()
operator|.
name|getConfiguration
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
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
return|return
name|privilegeManager
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|PrivilegeBitsProvider
name|getPrivilegeBitsProvider
parameter_list|()
block|{
name|checkRootInitialized
argument_list|()
expr_stmt|;
if|if
condition|(
name|privilegeBitsProvider
operator|==
literal|null
condition|)
block|{
name|privilegeBitsProvider
operator|=
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
return|return
name|privilegeBitsProvider
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|PrincipalManager
name|getPrincipalManager
parameter_list|()
block|{
name|checkRootInitialized
argument_list|()
expr_stmt|;
if|if
condition|(
name|principalManager
operator|==
literal|null
condition|)
block|{
name|principalManager
operator|=
name|getSecurityProvider
argument_list|()
operator|.
name|getConfiguration
argument_list|(
name|PrincipalConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getPrincipalManager
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
return|return
name|principalManager
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RestrictionProvider
name|getRestrictionProvider
parameter_list|()
block|{
if|if
condition|(
name|restrictionProvider
operator|==
literal|null
condition|)
block|{
name|restrictionProvider
operator|=
name|getSecurityProvider
argument_list|()
operator|.
name|getConfiguration
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getRestrictionProvider
argument_list|()
expr_stmt|;
block|}
return|return
name|restrictionProvider
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|TreeProvider
name|getTreeProvider
parameter_list|()
block|{
return|return
name|config
operator|.
name|getTreeProvider
argument_list|()
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RootProvider
name|getRootProvider
parameter_list|()
block|{
return|return
name|config
operator|.
name|getRootProvider
argument_list|()
return|;
block|}
specifier|private
name|void
name|checkRootInitialized
parameter_list|()
block|{
name|checkState
argument_list|(
name|root
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

