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
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Credentials
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NoSuchWorkspaceException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|SimpleCredentials
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginException
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
name|ContentRepository
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
name|nodetype
operator|.
name|write
operator|.
name|InitialContent
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
name|security
operator|.
name|authentication
operator|.
name|ConfigurationUtil
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
name|user
operator|.
name|UserConfiguration
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
name|util
operator|.
name|UserUtility
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_comment
comment|/**  * AbstractOakTest is the base class for oak test execution.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractSecurityTest
block|{
specifier|private
name|ContentRepository
name|contentRepository
decl_stmt|;
specifier|private
name|UserManager
name|userManager
decl_stmt|;
specifier|protected
name|NamePathMapper
name|namePathMapper
init|=
name|NamePathMapper
operator|.
name|DEFAULT
decl_stmt|;
specifier|protected
name|SecurityProvider
name|securityProvider
decl_stmt|;
specifier|protected
name|ContentSession
name|adminSession
decl_stmt|;
specifier|protected
name|Root
name|root
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|contentRepository
operator|=
operator|new
name|Oak
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|InitialContent
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|getSecurityProvider
argument_list|()
argument_list|)
operator|.
name|createContentRepository
argument_list|()
expr_stmt|;
name|adminSession
operator|=
name|login
argument_list|(
name|getAdminCredentials
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|=
name|adminSession
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
name|Configuration
operator|.
name|setConfiguration
argument_list|(
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
name|adminSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|Configuration
operator|.
name|setConfiguration
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|SecurityProvider
name|getSecurityProvider
parameter_list|()
block|{
if|if
condition|(
name|securityProvider
operator|==
literal|null
condition|)
block|{
name|securityProvider
operator|=
operator|new
name|SecurityProviderImpl
argument_list|()
expr_stmt|;
block|}
return|return
name|securityProvider
return|;
block|}
specifier|protected
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|ConfigurationUtil
operator|.
name|getDefaultConfiguration
argument_list|(
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
return|;
block|}
specifier|protected
name|ContentSession
name|login
parameter_list|(
annotation|@
name|Nullable
name|Credentials
name|credentials
parameter_list|)
throws|throws
name|LoginException
throws|,
name|NoSuchWorkspaceException
block|{
return|return
name|contentRepository
operator|.
name|login
argument_list|(
name|credentials
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|protected
name|Credentials
name|getAdminCredentials
parameter_list|()
block|{
name|String
name|adminId
init|=
name|UserUtility
operator|.
name|getAdminId
argument_list|(
name|getUserConfiguration
argument_list|()
operator|.
name|getConfigurationParameters
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|SimpleCredentials
argument_list|(
name|adminId
argument_list|,
name|adminId
operator|.
name|toCharArray
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|UserConfiguration
name|getUserConfiguration
parameter_list|()
block|{
return|return
name|getSecurityProvider
argument_list|()
operator|.
name|getUserConfiguration
argument_list|()
return|;
block|}
specifier|protected
name|UserManager
name|getUserManager
parameter_list|()
block|{
if|if
condition|(
name|userManager
operator|==
literal|null
condition|)
block|{
name|userManager
operator|=
name|getUserConfiguration
argument_list|()
operator|.
name|getUserManager
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
return|return
name|userManager
return|;
block|}
block|}
end_class

end_unit

