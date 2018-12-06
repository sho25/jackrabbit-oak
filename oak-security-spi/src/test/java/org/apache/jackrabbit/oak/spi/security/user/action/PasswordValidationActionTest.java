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
name|user
operator|.
name|action
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|ConstraintViolationException
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
name|util
operator|.
name|PasswordUtil
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
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
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
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|PasswordValidationActionTest
block|{
specifier|private
specifier|final
name|SecurityProvider
name|securityProvider
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|SecurityProvider
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Root
name|root
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Root
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|NamePathMapper
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|PasswordValidationAction
name|pwAction
init|=
operator|new
name|PasswordValidationAction
argument_list|()
decl_stmt|;
specifier|private
name|User
name|user
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|user
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|User
operator|.
name|class
argument_list|)
expr_stmt|;
name|pwAction
operator|.
name|init
argument_list|(
name|securityProvider
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|PasswordValidationAction
operator|.
name|CONSTRAINT
argument_list|,
literal|"^.*(?=.{8,})(?=.*[a-z])(?=.*[A-Z]).*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOnCreateNullPw
parameter_list|()
throws|throws
name|Exception
block|{
name|pwAction
operator|.
name|onCreate
argument_list|(
name|user
argument_list|,
literal|null
argument_list|,
name|root
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ConstraintViolationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testOnCreateInvalidPw
parameter_list|()
throws|throws
name|Exception
block|{
name|pwAction
operator|.
name|onCreate
argument_list|(
name|user
argument_list|,
literal|"pw"
argument_list|,
name|root
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ConstraintViolationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testOnCreateEmptyPw
parameter_list|()
throws|throws
name|Exception
block|{
name|pwAction
operator|.
name|onCreate
argument_list|(
name|user
argument_list|,
literal|""
argument_list|,
name|root
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOnCreateValidPw
parameter_list|()
throws|throws
name|Exception
block|{
name|pwAction
operator|.
name|onCreate
argument_list|(
name|user
argument_list|,
literal|"abCDefGH"
argument_list|,
name|root
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPasswordValidationActionInvalid
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|invalid
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|invalid
operator|.
name|add
argument_list|(
literal|"pw1"
argument_list|)
expr_stmt|;
name|invalid
operator|.
name|add
argument_list|(
literal|"only6C"
argument_list|)
expr_stmt|;
name|invalid
operator|.
name|add
argument_list|(
literal|"12345678"
argument_list|)
expr_stmt|;
name|invalid
operator|.
name|add
argument_list|(
literal|"WITHOUTLOWERCASE"
argument_list|)
expr_stmt|;
name|invalid
operator|.
name|add
argument_list|(
literal|"withoutuppercase"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|pw
range|:
name|invalid
control|)
block|{
try|try
block|{
name|pwAction
operator|.
name|onPasswordChange
argument_list|(
name|user
argument_list|,
name|pw
argument_list|,
name|root
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should throw constraint violation"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPasswordValidationActionValid
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|valid
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|valid
operator|.
name|add
argument_list|(
literal|"abCDefGH"
argument_list|)
expr_stmt|;
name|valid
operator|.
name|add
argument_list|(
literal|"Abbbbbbbbbbbb"
argument_list|)
expr_stmt|;
name|valid
operator|.
name|add
argument_list|(
literal|"cDDDDDDDDDDDDDDDDD"
argument_list|)
expr_stmt|;
name|valid
operator|.
name|add
argument_list|(
literal|"gH%%%%%%%%%%%%%%%%^^"
argument_list|)
expr_stmt|;
name|valid
operator|.
name|add
argument_list|(
literal|"&)(*&^%23qW"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|pw
range|:
name|valid
control|)
block|{
name|pwAction
operator|.
name|onPasswordChange
argument_list|(
name|user
argument_list|,
name|pw
argument_list|,
name|root
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ConstraintViolationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testPasswordValidationActionOnChange
parameter_list|()
throws|throws
name|Exception
block|{
name|pwAction
operator|.
name|init
argument_list|(
name|securityProvider
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|PasswordValidationAction
operator|.
name|CONSTRAINT
argument_list|,
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|hashed
init|=
name|PasswordUtil
operator|.
name|buildPasswordHash
argument_list|(
literal|"abc"
argument_list|)
decl_stmt|;
name|pwAction
operator|.
name|onPasswordChange
argument_list|(
name|user
argument_list|,
name|hashed
argument_list|,
name|Mockito
operator|.
name|mock
argument_list|(
name|Root
operator|.
name|class
argument_list|)
argument_list|,
name|Mockito
operator|.
name|mock
argument_list|(
name|NamePathMapper
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

