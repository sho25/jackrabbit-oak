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
name|authentication
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
name|security
operator|.
name|principal
operator|.
name|TmpPrincipalProvider
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
name|LoginContextProvider
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
name|PrincipalProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|security
operator|.
name|auth
operator|.
name|Subject
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
name|LoginContext
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
name|java
operator|.
name|security
operator|.
name|AccessController
import|;
end_import

begin_comment
comment|/**  * LoginContextProviderImpl...  TODO  */
end_comment

begin_class
specifier|public
class|class
name|LoginContextProviderImpl
implements|implements
name|LoginContextProvider
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LoginContextProviderImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|APP_NAME
init|=
literal|"jackrabbit.oak"
decl_stmt|;
specifier|private
specifier|final
name|Configuration
name|authConfig
decl_stmt|;
specifier|private
specifier|final
name|PrincipalProvider
name|principalProvider
decl_stmt|;
specifier|public
name|LoginContextProviderImpl
parameter_list|(
name|ContentRepository
name|repository
parameter_list|)
block|{
comment|// TODO: use configurable authentication config and principal provider
name|authConfig
operator|=
operator|new
name|ConfigurationImpl
argument_list|()
expr_stmt|;
name|principalProvider
operator|=
operator|new
name|TmpPrincipalProvider
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|LoginContext
name|getLoginContext
parameter_list|(
name|Credentials
name|credentials
parameter_list|,
name|String
name|workspaceName
parameter_list|)
throws|throws
name|LoginException
block|{
comment|// TODO: add proper implementation
comment|// TODO  - authentication against configurable spi-authentication
comment|// TODO  - validation of workspace name (including access rights for the given 'user')
name|Subject
name|subject
init|=
name|getSubject
argument_list|()
decl_stmt|;
return|return
operator|new
name|LoginContext
argument_list|(
name|APP_NAME
argument_list|,
name|subject
argument_list|,
operator|new
name|CallbackHandlerImpl
argument_list|(
name|credentials
argument_list|,
name|principalProvider
argument_list|)
argument_list|,
name|authConfig
argument_list|)
return|;
block|}
comment|//-------------------------------------------------===--------< private>---
specifier|private
name|Subject
name|getSubject
parameter_list|()
block|{
name|Subject
name|subject
init|=
literal|null
decl_stmt|;
try|try
block|{
name|subject
operator|=
name|Subject
operator|.
name|getSubject
argument_list|(
name|AccessController
operator|.
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Can't check for pre-authentication. Reason:"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|subject
operator|==
literal|null
condition|)
block|{
name|subject
operator|=
operator|new
name|Subject
argument_list|()
expr_stmt|;
block|}
return|return
name|subject
return|;
block|}
block|}
end_class

end_unit

