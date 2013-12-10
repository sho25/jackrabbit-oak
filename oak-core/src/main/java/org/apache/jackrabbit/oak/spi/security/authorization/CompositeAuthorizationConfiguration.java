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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

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
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlPolicy
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlPolicyIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|Privilege
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
name|ImmutableList
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
name|Iterators
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
name|JackrabbitAccessControlManager
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
name|JackrabbitAccessControlPolicy
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
name|commons
operator|.
name|iterator
operator|.
name|AccessControlPolicyIteratorAdapter
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
name|accesscontrol
operator|.
name|AbstractAccessControlManager
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
name|permission
operator|.
name|PermissionProvider
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
name|CompositeRestrictionProvider
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

begin_comment
comment|/**  * {@link CompositeAuthorizationConfiguration} that combines different  * authorization models.  */
end_comment

begin_class
specifier|public
class|class
name|CompositeAuthorizationConfiguration
extends|extends
name|CompositeConfiguration
argument_list|<
name|AuthorizationConfiguration
argument_list|>
implements|implements
name|AuthorizationConfiguration
block|{
specifier|public
name|CompositeAuthorizationConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|super
argument_list|(
name|AuthorizationConfiguration
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
name|AccessControlManager
name|getAccessControlManager
parameter_list|(
specifier|final
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
specifier|final
annotation|@
name|Nonnull
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|List
argument_list|<
name|AccessControlManager
argument_list|>
name|mgrs
init|=
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
name|AuthorizationConfiguration
argument_list|,
name|AccessControlManager
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AccessControlManager
name|apply
parameter_list|(
name|AuthorizationConfiguration
name|authorizationConfiguration
parameter_list|)
block|{
return|return
name|authorizationConfiguration
operator|.
name|getAccessControlManager
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
operator|new
name|CompositeAcMgr
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|,
name|mgrs
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RestrictionProvider
name|getRestrictionProvider
parameter_list|()
block|{
return|return
name|CompositeRestrictionProvider
operator|.
name|newInstance
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
name|AuthorizationConfiguration
argument_list|,
name|RestrictionProvider
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RestrictionProvider
name|apply
parameter_list|(
name|AuthorizationConfiguration
name|authorizationConfiguration
parameter_list|)
block|{
return|return
name|authorizationConfiguration
operator|.
name|getRestrictionProvider
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PermissionProvider
name|getPermissionProvider
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
comment|// TODO
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"not yet implemented."
argument_list|)
throw|;
block|}
comment|/**      *      */
specifier|private
specifier|static
class|class
name|CompositeAcMgr
extends|extends
name|AbstractAccessControlManager
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|AccessControlManager
argument_list|>
name|acMgrs
decl_stmt|;
specifier|private
name|CompositeAcMgr
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
name|NamePathMapper
name|namePathMapper
parameter_list|,
annotation|@
name|Nonnull
name|SecurityProvider
name|securityProvider
parameter_list|,
annotation|@
name|Nonnull
name|List
argument_list|<
name|AccessControlManager
argument_list|>
name|acMgrs
parameter_list|)
block|{
name|super
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|,
name|securityProvider
argument_list|)
expr_stmt|;
name|this
operator|.
name|acMgrs
operator|=
name|acMgrs
expr_stmt|;
block|}
comment|//-------------------------------------------< AccessControlManager>---
annotation|@
name|Override
specifier|public
name|Privilege
index|[]
name|getSupportedPrivileges
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|Privilege
argument_list|>
name|privs
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|AccessControlManager
name|acMgr
range|:
name|acMgrs
control|)
block|{
name|privs
operator|.
name|add
argument_list|(
name|acMgr
operator|.
name|getSupportedPrivileges
argument_list|(
name|absPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Privilege
argument_list|>
name|l
init|=
name|privs
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|l
operator|.
name|toArray
argument_list|(
operator|new
name|Privilege
index|[
name|l
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|AccessControlPolicy
index|[]
name|getPolicies
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|AccessControlPolicy
argument_list|>
name|privs
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|AccessControlManager
name|acMgr
range|:
name|acMgrs
control|)
block|{
name|privs
operator|.
name|add
argument_list|(
name|acMgr
operator|.
name|getPolicies
argument_list|(
name|absPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|AccessControlPolicy
argument_list|>
name|l
init|=
name|privs
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|l
operator|.
name|toArray
argument_list|(
operator|new
name|AccessControlPolicy
index|[
name|l
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|AccessControlPolicy
index|[]
name|getEffectivePolicies
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|AccessControlPolicy
argument_list|>
name|privs
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|AccessControlManager
name|acMgr
range|:
name|acMgrs
control|)
block|{
name|privs
operator|.
name|add
argument_list|(
name|acMgr
operator|.
name|getEffectivePolicies
argument_list|(
name|absPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|AccessControlPolicy
argument_list|>
name|l
init|=
name|privs
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|l
operator|.
name|toArray
argument_list|(
operator|new
name|AccessControlPolicy
index|[
name|l
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|AccessControlPolicyIterator
name|getApplicablePolicies
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|List
argument_list|<
name|AccessControlPolicyIterator
argument_list|>
name|l
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|AccessControlManager
name|acMgr
range|:
name|acMgrs
control|)
block|{
name|l
operator|.
name|add
argument_list|(
name|acMgr
operator|.
name|getApplicablePolicies
argument_list|(
name|absPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|AccessControlPolicyIteratorAdapter
argument_list|(
name|Iterators
operator|.
name|concat
argument_list|(
name|l
operator|.
name|toArray
argument_list|(
operator|new
name|AccessControlPolicyIterator
index|[
name|l
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setPolicy
parameter_list|(
name|String
name|absPath
parameter_list|,
name|AccessControlPolicy
name|policy
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// TODO
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"not yet implemented."
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removePolicy
parameter_list|(
name|String
name|absPath
parameter_list|,
name|AccessControlPolicy
name|policy
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// TODO
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"not yet implemented."
argument_list|)
throw|;
block|}
comment|//---------------------------------< JackrabbitAccessControlManager>---
annotation|@
name|Override
specifier|public
name|JackrabbitAccessControlPolicy
index|[]
name|getApplicablePolicies
parameter_list|(
name|Principal
name|principal
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|JackrabbitAccessControlPolicy
argument_list|>
name|policies
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|AccessControlManager
name|acMgr
range|:
name|acMgrs
control|)
block|{
if|if
condition|(
name|acMgr
operator|instanceof
name|JackrabbitAccessControlManager
condition|)
block|{
name|policies
operator|.
name|add
argument_list|(
operator|(
operator|(
name|JackrabbitAccessControlManager
operator|)
name|acMgr
operator|)
operator|.
name|getApplicablePolicies
argument_list|(
name|principal
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|JackrabbitAccessControlPolicy
argument_list|>
name|l
init|=
name|policies
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|l
operator|.
name|toArray
argument_list|(
operator|new
name|JackrabbitAccessControlPolicy
index|[
name|l
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|JackrabbitAccessControlPolicy
index|[]
name|getPolicies
parameter_list|(
name|Principal
name|principal
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|JackrabbitAccessControlPolicy
argument_list|>
name|privs
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|AccessControlManager
name|acMgr
range|:
name|acMgrs
control|)
block|{
if|if
condition|(
name|acMgr
operator|instanceof
name|JackrabbitAccessControlManager
condition|)
block|{
name|privs
operator|.
name|add
argument_list|(
operator|(
operator|(
name|JackrabbitAccessControlManager
operator|)
name|acMgr
operator|)
operator|.
name|getPolicies
argument_list|(
name|principal
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|JackrabbitAccessControlPolicy
argument_list|>
name|l
init|=
name|privs
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|l
operator|.
name|toArray
argument_list|(
operator|new
name|JackrabbitAccessControlPolicy
index|[
name|l
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|AccessControlPolicy
index|[]
name|getEffectivePolicies
parameter_list|(
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|AccessControlPolicy
argument_list|>
name|privs
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|AccessControlManager
name|acMgr
range|:
name|acMgrs
control|)
block|{
if|if
condition|(
name|acMgr
operator|instanceof
name|JackrabbitAccessControlManager
condition|)
block|{
name|privs
operator|.
name|add
argument_list|(
operator|(
operator|(
name|JackrabbitAccessControlManager
operator|)
name|acMgr
operator|)
operator|.
name|getEffectivePolicies
argument_list|(
name|principals
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|AccessControlPolicy
argument_list|>
name|l
init|=
name|privs
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|l
operator|.
name|toArray
argument_list|(
operator|new
name|AccessControlPolicy
index|[
name|l
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

