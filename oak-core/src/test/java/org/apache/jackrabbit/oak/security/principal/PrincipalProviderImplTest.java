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
name|principal
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
name|Set
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
name|user
operator|.
name|UserManager
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
name|AbstractSecurityTest
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
name|AdminPrincipal
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
name|EveryonePrincipal
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
name|UserConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * PrincipalProviderImplTest...  */
end_comment

begin_class
specifier|public
class|class
name|PrincipalProviderImplTest
extends|extends
name|AbstractSecurityTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipals
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|root
init|=
name|admin
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|PrincipalProviderImpl
name|principalProvider
init|=
operator|new
name|PrincipalProviderImpl
argument_list|(
name|root
argument_list|,
name|getSecurityProvider
argument_list|()
operator|.
name|getUserConfiguration
argument_list|()
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|String
name|adminId
init|=
name|admin
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getUserID
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|principals
init|=
name|principalProvider
operator|.
name|getPrincipals
argument_list|(
name|adminId
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|principals
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principals
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|principals
operator|.
name|contains
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|containsAdminPrincipal
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Principal
name|principal
range|:
name|principals
control|)
block|{
name|assertNotNull
argument_list|(
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|principal
operator|instanceof
name|AdminPrincipal
condition|)
block|{
name|containsAdminPrincipal
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|containsAdminPrincipal
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEveryone
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|root
init|=
name|admin
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|UserConfiguration
name|config
init|=
name|getSecurityProvider
argument_list|()
operator|.
name|getUserConfiguration
argument_list|()
decl_stmt|;
name|PrincipalProviderImpl
name|principalProvider
init|=
operator|new
name|PrincipalProviderImpl
argument_list|(
name|root
argument_list|,
name|config
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|Principal
name|everyone
init|=
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|everyone
operator|instanceof
name|EveryonePrincipal
argument_list|)
expr_stmt|;
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
name|user
operator|.
name|Group
name|everyoneGroup
init|=
literal|null
decl_stmt|;
try|try
block|{
name|UserManager
name|userMgr
init|=
name|config
operator|.
name|getUserManager
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|everyoneGroup
operator|=
name|userMgr
operator|.
name|createGroup
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Principal
name|ep
init|=
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|ep
operator|instanceof
name|EveryonePrincipal
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|everyoneGroup
operator|!=
literal|null
condition|)
block|{
name|everyoneGroup
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

