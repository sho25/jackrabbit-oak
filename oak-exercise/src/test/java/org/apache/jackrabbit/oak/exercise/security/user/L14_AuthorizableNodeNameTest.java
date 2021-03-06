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
name|user
package|;
end_package

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
name|security
operator|.
name|user
operator|.
name|RandomAuthorizableNodeName
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
name|user
operator|.
name|AuthorizableNodeName
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
name|UserConstants
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
name|api
operator|.
name|util
operator|.
name|Text
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
name|assertEquals
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
name|assertNotSame
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
comment|/**  *<pre>  * Module: User Management  * =============================================================================  *  * Title: Authorizable Node Name  * -----------------------------------------------------------------------------  *  * Goal:  * Become familiar with the {@link org.apache.jackrabbit.oak.spi.security.user.AuthorizableNodeName}  * interface and how it is used in the default implementation. After having  * completed this test you should also be able to write and configure a custom  * implemenation understanding the impact it will have on the default user  * management.  *  * Exercises:  *  * - {@link #testAuthorizableNodeName()}  *   Test the fallback behaviour as implemented in Oak if no  *   {@link org.apache.jackrabbit.oak.spi.security.user.AuthorizableNodeName}  *   interface is configured.  *  * - {@link #testRandomAuthorizableNodeName()}  *   Same as above but with an {@link org.apache.jackrabbit.oak.spi.security.user.AuthorizableNodeName}  *   configured.  *  *  * Advanced Exercises:  * -----------------------------------------------------------------------------  *  * - Create a custom implementation of the {@link org.apache.jackrabbit.oak.spi.security.user.AuthorizableNodeName}  *   interface.  *  * - Make your implementation a OSGi service as described in the documentation  *   and deploy it in your Sling (Granite|CQ) repository.  *   Verify that creating a new user or group actually makes use of your  *   implementation.  *  *</pre>  *  * @see org.apache.jackrabbit.oak.spi.security.user.AuthorizableNodeName  * @see org.apache.jackrabbit.oak.security.user.RandomAuthorizableNodeName  */
end_comment

begin_class
specifier|public
class|class
name|L14_AuthorizableNodeNameTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|UserManager
name|userManager
decl_stmt|;
specifier|private
name|User
name|testUser
decl_stmt|;
specifier|private
name|AuthorizableNodeName
name|nameGenerator
init|=
operator|new
name|RandomAuthorizableNodeName
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
name|userManager
operator|=
name|getUserManager
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
if|if
condition|(
name|testUser
operator|!=
literal|null
condition|)
block|{
name|testUser
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|ConfigurationParameters
name|getSecurityConfigParameters
parameter_list|()
block|{
comment|// EXERCISE: un-comment for 'testRandomAuthorizableNodeName'
comment|//        ConfigurationParameters userConfig = ConfigurationParameters.of(UserConstants.PARAM_AUTHORIZABLE_NODE_NAME, nameGenerator);
comment|//        return ConfigurationParameters.of(UserConfiguration.NAME, userConfig);
return|return
name|ConfigurationParameters
operator|.
name|EMPTY
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAuthorizableNodeName
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|testUser
operator|=
name|userManager
operator|.
name|createUser
argument_list|(
literal|"test/:User"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|nodeName
init|=
name|Text
operator|.
name|getName
argument_list|(
name|testUser
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|expectedNodeName
init|=
literal|null
decl_stmt|;
comment|// EXERCISE : fill in the expected value
name|assertEquals
argument_list|(
name|expectedNodeName
argument_list|,
name|nodeName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRandomAuthorizableNodeName
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// EXERCISE: uncomment the setup in 'getSecurityConfigParameters' before running this test.
comment|// verify that the configuration is correct:
name|AuthorizableNodeName
name|configured
init|=
name|getUserConfiguration
argument_list|()
operator|.
name|getParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_AUTHORIZABLE_NODE_NAME
argument_list|,
name|AuthorizableNodeName
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|AuthorizableNodeName
operator|.
name|DEFAULT
argument_list|,
name|configured
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|configured
operator|instanceof
name|RandomAuthorizableNodeName
argument_list|)
expr_stmt|;
name|testUser
operator|=
name|userManager
operator|.
name|createUser
argument_list|(
literal|"test/:User"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|nodeName
init|=
name|Text
operator|.
name|getName
argument_list|(
name|testUser
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
comment|// EXERCISE: write the correct assertion wrt the generated node name.
block|}
block|}
end_class

end_unit

