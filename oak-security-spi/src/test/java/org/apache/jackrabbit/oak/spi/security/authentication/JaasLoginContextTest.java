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
name|authentication
package|;
end_package

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
name|callback
operator|.
name|CallbackHandler
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
name|assertNull
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

begin_class
specifier|public
class|class
name|JaasLoginContextTest
block|{
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|Configuration
name|c
init|=
name|ConfigurationUtil
operator|.
name|getDefaultConfiguration
argument_list|(
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|Configuration
operator|.
name|setConfiguration
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
block|{
name|Configuration
operator|.
name|setConfiguration
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|LoginException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testMissingConfiguration
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
operator|.
name|setConfiguration
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|JaasLoginContext
name|ctx
init|=
operator|new
name|JaasLoginContext
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|LoginException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testNullNameConstructor
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
operator|.
name|setConfiguration
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|JaasLoginContext
name|ctx
init|=
operator|new
name|JaasLoginContext
argument_list|(
literal|null
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNameConstructor
parameter_list|()
throws|throws
name|Exception
block|{
name|JaasLoginContext
name|ctx
init|=
operator|new
name|JaasLoginContext
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|ctx
operator|.
name|getSubject
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNameSubjectConstructor
parameter_list|()
throws|throws
name|Exception
block|{
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|()
decl_stmt|;
name|JaasLoginContext
name|ctx
init|=
operator|new
name|JaasLoginContext
argument_list|(
literal|"name"
argument_list|,
name|subject
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|subject
argument_list|,
name|ctx
operator|.
name|getSubject
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|LoginException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testNameNullCallbackConstructor
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|JaasLoginContext
argument_list|(
literal|"name"
argument_list|,
operator|(
name|CallbackHandler
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNameCallbackHandler
parameter_list|()
throws|throws
name|Exception
block|{
name|CallbackHandler
name|cbh
init|=
name|mock
argument_list|(
name|CallbackHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|JaasLoginContext
name|ctx
init|=
operator|new
name|JaasLoginContext
argument_list|(
literal|"name"
argument_list|,
name|cbh
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|LoginException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testNameSubjectNullCallbackConstructor
parameter_list|()
throws|throws
name|Exception
block|{
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|()
decl_stmt|;
name|JaasLoginContext
name|ctx
init|=
operator|new
name|JaasLoginContext
argument_list|(
literal|"name"
argument_list|,
name|subject
argument_list|,
literal|null
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNameSubjectCallbackConstructor
parameter_list|()
throws|throws
name|Exception
block|{
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|()
decl_stmt|;
name|JaasLoginContext
name|ctx
init|=
operator|new
name|JaasLoginContext
argument_list|(
literal|"name"
argument_list|,
name|subject
argument_list|,
name|callbacks
lambda|->
block|{}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|subject
argument_list|,
name|ctx
operator|.
name|getSubject
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNameSubjectCallbackConfigurationConstructor
parameter_list|()
throws|throws
name|Exception
block|{
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|()
decl_stmt|;
name|JaasLoginContext
name|ctx
init|=
operator|new
name|JaasLoginContext
argument_list|(
literal|"name"
argument_list|,
name|subject
argument_list|,
name|callbacks
lambda|->
block|{         }
argument_list|,
name|ConfigurationUtil
operator|.
name|getDefaultConfiguration
argument_list|(
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|subject
argument_list|,
name|ctx
operator|.
name|getSubject
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

