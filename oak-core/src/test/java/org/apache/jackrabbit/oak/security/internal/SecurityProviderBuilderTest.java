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
name|authentication
operator|.
name|AuthenticationConfiguration
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
name|authentication
operator|.
name|token
operator|.
name|TokenConfiguration
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|withSettings
import|;
end_import

begin_class
specifier|public
class|class
name|SecurityProviderBuilderTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|SecurityProviderBuilder
name|builder
init|=
name|SecurityProviderBuilder
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testDefault
parameter_list|()
block|{
name|SecurityProvider
name|sp
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|sp
operator|.
name|getConfiguration
argument_list|(
name|AuthenticationConfiguration
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|sp
operator|.
name|getConfiguration
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|sp
operator|.
name|getConfiguration
argument_list|(
name|PrincipalConfiguration
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|sp
operator|.
name|getConfiguration
argument_list|(
name|PrivilegeConfiguration
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|sp
operator|.
name|getConfiguration
argument_list|(
name|TokenConfiguration
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|sp
operator|.
name|getConfiguration
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCompositeConfigurations
parameter_list|()
block|{
name|AuthenticationConfiguration
name|ac
init|=
operator|(
name|AuthenticationConfiguration
operator|)
name|mock
argument_list|(
name|CompositeConfiguration
operator|.
name|class
argument_list|,
name|withSettings
argument_list|()
operator|.
name|extraInterfaces
argument_list|(
name|AuthenticationConfiguration
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|PrivilegeConfiguration
name|pc
init|=
operator|(
name|PrivilegeConfiguration
operator|)
name|mock
argument_list|(
name|CompositeConfiguration
operator|.
name|class
argument_list|,
name|withSettings
argument_list|()
operator|.
name|extraInterfaces
argument_list|(
name|PrivilegeConfiguration
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|UserConfiguration
name|uc
init|=
operator|(
name|UserConfiguration
operator|)
name|mock
argument_list|(
name|CompositeConfiguration
operator|.
name|class
argument_list|,
name|withSettings
argument_list|()
operator|.
name|extraInterfaces
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|AuthorizationConfiguration
name|auc
init|=
operator|(
name|AuthorizationConfiguration
operator|)
name|mock
argument_list|(
name|CompositeConfiguration
operator|.
name|class
argument_list|,
name|withSettings
argument_list|()
operator|.
name|extraInterfaces
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|PrincipalConfiguration
name|pnc
init|=
operator|(
name|PrincipalConfiguration
operator|)
name|mock
argument_list|(
name|CompositeConfiguration
operator|.
name|class
argument_list|,
name|withSettings
argument_list|()
operator|.
name|extraInterfaces
argument_list|(
name|PrincipalConfiguration
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|TokenConfiguration
name|tc
init|=
operator|(
name|TokenConfiguration
operator|)
name|mock
argument_list|(
name|CompositeConfiguration
operator|.
name|class
argument_list|,
name|withSettings
argument_list|()
operator|.
name|extraInterfaces
argument_list|(
name|TokenConfiguration
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|SecurityProvider
name|sp
init|=
name|builder
operator|.
name|with
argument_list|(
name|ac
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|,
name|pc
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|,
name|uc
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|,
name|auc
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|,
name|pnc
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|,
name|tc
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|sp
operator|.
name|getConfiguration
argument_list|(
name|AuthenticationConfiguration
operator|.
name|class
argument_list|)
operator|instanceof
name|CompositeConfiguration
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sp
operator|.
name|getConfiguration
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
operator|instanceof
name|CompositeConfiguration
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sp
operator|.
name|getConfiguration
argument_list|(
name|PrincipalConfiguration
operator|.
name|class
argument_list|)
operator|instanceof
name|CompositeConfiguration
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sp
operator|.
name|getConfiguration
argument_list|(
name|PrivilegeConfiguration
operator|.
name|class
argument_list|)
operator|instanceof
name|CompositeConfiguration
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sp
operator|.
name|getConfiguration
argument_list|(
name|TokenConfiguration
operator|.
name|class
argument_list|)
operator|instanceof
name|CompositeConfiguration
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sp
operator|.
name|getConfiguration
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
operator|instanceof
name|CompositeConfiguration
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSingularConfigurations
parameter_list|()
block|{
name|SecurityProvider
name|sp
init|=
name|builder
operator|.
name|with
argument_list|(
name|mock
argument_list|(
name|AuthenticationConfiguration
operator|.
name|class
argument_list|)
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|,
name|mock
argument_list|(
name|PrivilegeConfiguration
operator|.
name|class
argument_list|)
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|,
name|mock
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|,
name|mock
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|,
name|mock
argument_list|(
name|PrincipalConfiguration
operator|.
name|class
argument_list|)
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|,
name|mock
argument_list|(
name|TokenConfiguration
operator|.
name|class
argument_list|)
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|sp
operator|.
name|getConfiguration
argument_list|(
name|AuthenticationConfiguration
operator|.
name|class
argument_list|)
operator|instanceof
name|CompositeConfiguration
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|sp
operator|.
name|getConfiguration
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
operator|instanceof
name|CompositeConfiguration
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|sp
operator|.
name|getConfiguration
argument_list|(
name|PrincipalConfiguration
operator|.
name|class
argument_list|)
operator|instanceof
name|CompositeConfiguration
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|sp
operator|.
name|getConfiguration
argument_list|(
name|PrivilegeConfiguration
operator|.
name|class
argument_list|)
operator|instanceof
name|CompositeConfiguration
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|sp
operator|.
name|getConfiguration
argument_list|(
name|TokenConfiguration
operator|.
name|class
argument_list|)
operator|instanceof
name|CompositeConfiguration
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|sp
operator|.
name|getConfiguration
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
operator|instanceof
name|CompositeConfiguration
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
