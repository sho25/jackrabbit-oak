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
name|exercise
operator|.
name|security
operator|.
name|authentication
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|GuestCredentials
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|LoginException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
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
name|Session
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|test
operator|.
name|AbstractJCRTest
import|;
end_import

begin_comment
comment|/**  *<pre>  * Module: Authentication  * =============================================================================  *  * Title: Null Login with PrePopulated Subject (Pre-Authentication without LoginModule)  * -----------------------------------------------------------------------------  *  * Goal:  * Understand the meaning and usage of the pre-authenticated login with  * {@link javax.jcr.Repository#login()} (i.e. {@code null} credentials).  *  * Exercises:  *  * - {@link #testNullLogin()}  *   Step through a regular JCR login without credentials and explain why this  *   is expected to fail.  *  * - {@link #testSuccessfulNullLogin()}  *   This test-case illustrates a usage of the 'null' login. Complete the test  *   by creating/populating a valid {@link Subject} and verify your expectations  *   after a successful login.  *  *  * Additional Exercises:  * -----------------------------------------------------------------------------  *  * In Jackrabbit 2.x the null-credentials login was treated as login of the  * anonymous guest user.  *  * - Use {@link #testJr2CompatibleLoginConfiguration} to configure the  *   {@link javax.security.auth.login.LoginContext} such that the repository behaves  *   like Jackrabbit 2.x and treats {@link javax.jcr.Repository#login()}  *   (null-login) as anonymous login.  *  *  * Related Exercises:  * -----------------------------------------------------------------------------  *  * - {@link L3_LoginModuleTest}  * - {@link L8_PreAuthTest}  *  *</pre>  *  * @see<a href="http://jackrabbit.apache.org/oak/docs/security/authentication/preauthentication.html">Pre-Authentication Documentation</a>  */
end_comment

begin_class
specifier|public
class|class
name|L9_NullLoginTest
extends|extends
name|AbstractJCRTest
block|{
specifier|private
name|Repository
name|repository
decl_stmt|;
specifier|private
name|Session
name|testSession
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|repository
operator|=
name|getHelper
argument_list|()
operator|.
name|getRepository
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
if|if
condition|(
name|testSession
operator|!=
literal|null
operator|&&
name|testSession
operator|.
name|isLive
argument_list|()
condition|)
block|{
name|testSession
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testNullLogin
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|testSession
operator|=
name|repository
operator|.
name|login
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LoginException
name|e
parameter_list|)
block|{
comment|// success
comment|// EXERCISE: explain what is going on and why it is expected to fail.
block|}
block|}
specifier|public
name|void
name|testSuccessfulNullLogin
parameter_list|()
throws|throws
name|Exception
block|{
comment|// EXERCISE: populate a subject that results in successful null-login
name|Subject
name|subject
init|=
literal|null
decl_stmt|;
name|String
name|expectedId
init|=
literal|null
decl_stmt|;
name|testSession
operator|=
name|Subject
operator|.
name|doAs
argument_list|(
name|subject
argument_list|,
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Session
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Session
name|run
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedId
argument_list|,
name|testSession
operator|.
name|getUserID
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testJr2CompatibleLoginConfiguration
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// EXERCISE: define the JAAS configuration that allows you to have null-login treated as anonymous login.
name|Configuration
name|configuration
init|=
literal|null
decl_stmt|;
name|Configuration
operator|.
name|setConfiguration
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
try|try
block|{
name|testSession
operator|=
name|repository
operator|.
name|login
argument_list|()
expr_stmt|;
name|Session
name|guest
init|=
name|repository
operator|.
name|login
argument_list|(
operator|new
name|GuestCredentials
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|expectedId
init|=
name|guest
operator|.
name|getUserID
argument_list|()
decl_stmt|;
name|guest
operator|.
name|logout
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedId
argument_list|,
name|testSession
operator|.
name|getUserID
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|Configuration
operator|.
name|setConfiguration
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

