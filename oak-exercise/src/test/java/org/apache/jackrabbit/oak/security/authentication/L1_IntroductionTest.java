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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|api
operator|.
name|JackrabbitSession
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
name|User
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

begin_import
import|import static
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
name|ExerciseUtility
operator|.
name|createTestUser
import|;
end_import

begin_import
import|import static
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
name|ExerciseUtility
operator|.
name|getTestCredentials
import|;
end_import

begin_comment
comment|/**  *<pre>  * Module: Authentication  * =============================================================================  *  * Title: Introduction - Login Step by Step  * -----------------------------------------------------------------------------  *  * Goal:  * Make yourself familiar with the authentication as present in JCR and in Oak.  *  * Exercise:  *  * Walk though repository login starting from the JCR repository login and  * make yourself familiar with the authentication.  *  * - {@link #testUserLogin()}  * - {@link #testAdminLogin()}  *  * Questions:  *  * - What is the Oak API correspondent of {@link Repository#login(javax.jcr.Credentials)}?  *  * - Identify those parts/classes/configurations in the repository authentication  *   that can be customized  *  *  * Additional Exercises:  * -----------------------------------------------------------------------------  *  * - Modify the test to use the other variants of {@link javax.jcr.Repository#login}  * - Modify the test to use {@link org.apache.jackrabbit.api.JackrabbitRepository#login(javax.jcr.Credentials, String, java.util.Map)}  *  * Questions:  *  * - Explain the difference between the different login flavors  *  * - Explain the difference of the {@code JackrabbitRepository} login extension  *   wrt regular JCR login and explain what it is (could be) used for.  *   Hint: Look at Sling (Granite|CQ), search in the Apache JIRA  *  *</pre>  *  * @see javax.jcr.Repository#login  * @see org.apache.jackrabbit.api.JackrabbitRepository#login  * @see org.apache.jackrabbit.oak.api.ContentRepository#login(javax.jcr.Credentials, String)  * @see javax.jcr.Credentials  * @see javax.jcr.SimpleCredentials  */
end_comment

begin_class
specifier|public
class|class
name|L1_IntroductionTest
extends|extends
name|AbstractJCRTest
block|{
specifier|private
name|Repository
name|repository
decl_stmt|;
specifier|private
name|User
name|user
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
name|user
operator|=
name|createTestUser
argument_list|(
operator|(
operator|(
name|JackrabbitSession
operator|)
name|superuser
operator|)
operator|.
name|getUserManager
argument_list|()
argument_list|)
expr_stmt|;
empty_stmt|;
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
name|user
operator|.
name|remove
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
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
name|testUserLogin
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|testSession
operator|=
name|repository
operator|.
name|login
argument_list|(
name|getTestCredentials
argument_list|(
name|user
operator|.
name|getID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAdminLogin
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Credentials
name|adminCredentials
init|=
name|getHelper
argument_list|()
operator|.
name|getSuperuserCredentials
argument_list|()
decl_stmt|;
name|testSession
operator|=
name|repository
operator|.
name|login
argument_list|(
name|adminCredentials
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

