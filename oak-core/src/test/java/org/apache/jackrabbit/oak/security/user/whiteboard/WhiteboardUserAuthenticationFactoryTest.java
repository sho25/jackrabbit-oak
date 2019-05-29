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
name|user
operator|.
name|whiteboard
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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|Authentication
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
name|UserAuthenticationFactory
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
name|assertNull
import|;
end_import

begin_class
specifier|public
class|class
name|WhiteboardUserAuthenticationFactoryTest
block|{
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
name|UserConfiguration
name|userConfiguration
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|WhiteboardUserAuthenticationFactory
name|createFactory
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|UserAuthenticationFactory
name|defaultFactory
parameter_list|,
annotation|@
name|NotNull
specifier|final
name|String
modifier|...
name|userIds
parameter_list|)
block|{
return|return
operator|new
name|WhiteboardUserAuthenticationFactory
argument_list|(
name|defaultFactory
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|List
argument_list|<
name|UserAuthenticationFactory
argument_list|>
name|getServices
parameter_list|()
block|{
name|List
argument_list|<
name|UserAuthenticationFactory
argument_list|>
name|factories
init|=
operator|new
name|ArrayList
argument_list|<
name|UserAuthenticationFactory
argument_list|>
argument_list|(
name|userIds
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|uid
range|:
name|userIds
control|)
block|{
name|factories
operator|.
name|add
argument_list|(
operator|new
name|TestUserAuthenticationFactory
argument_list|(
name|uid
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|factories
return|;
block|}
block|}
return|;
block|}
annotation|@
name|NotNull
specifier|private
name|UserConfiguration
name|getUserConfiguration
parameter_list|()
block|{
return|return
name|userConfiguration
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNoServiceNoDefault
parameter_list|()
block|{
name|WhiteboardUserAuthenticationFactory
name|factory
init|=
operator|new
name|WhiteboardUserAuthenticationFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|factory
operator|.
name|getAuthentication
argument_list|(
name|getUserConfiguration
argument_list|()
argument_list|,
name|root
argument_list|,
literal|"userId"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSingleService
parameter_list|()
throws|throws
name|Exception
block|{
name|WhiteboardUserAuthenticationFactory
name|factory
init|=
name|createFactory
argument_list|(
literal|null
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|factory
operator|.
name|getAuthentication
argument_list|(
name|getUserConfiguration
argument_list|()
argument_list|,
name|root
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|factory
operator|.
name|getAuthentication
argument_list|(
name|getUserConfiguration
argument_list|()
argument_list|,
name|root
argument_list|,
literal|"another"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultipleService
parameter_list|()
throws|throws
name|Exception
block|{
name|WhiteboardUserAuthenticationFactory
name|factory
init|=
name|createFactory
argument_list|(
literal|null
argument_list|,
literal|"test"
argument_list|,
literal|"test2"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|factory
operator|.
name|getAuthentication
argument_list|(
name|getUserConfiguration
argument_list|()
argument_list|,
name|root
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|factory
operator|.
name|getAuthentication
argument_list|(
name|getUserConfiguration
argument_list|()
argument_list|,
name|root
argument_list|,
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|factory
operator|.
name|getAuthentication
argument_list|(
name|getUserConfiguration
argument_list|()
argument_list|,
name|root
argument_list|,
literal|"another"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefault
parameter_list|()
throws|throws
name|Exception
block|{
name|WhiteboardUserAuthenticationFactory
name|factory
init|=
name|createFactory
argument_list|(
operator|new
name|TestUserAuthenticationFactory
argument_list|(
literal|"abc"
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|factory
operator|.
name|getAuthentication
argument_list|(
name|getUserConfiguration
argument_list|()
argument_list|,
name|root
argument_list|,
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|factory
operator|.
name|getAuthentication
argument_list|(
name|getUserConfiguration
argument_list|()
argument_list|,
name|root
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|factory
operator|.
name|getAuthentication
argument_list|(
name|getUserConfiguration
argument_list|()
argument_list|,
name|root
argument_list|,
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|factory
operator|.
name|getAuthentication
argument_list|(
name|getUserConfiguration
argument_list|()
argument_list|,
name|root
argument_list|,
literal|"another"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultipleServiceAndDefault
parameter_list|()
throws|throws
name|Exception
block|{
name|WhiteboardUserAuthenticationFactory
name|factory
init|=
name|createFactory
argument_list|(
operator|new
name|TestUserAuthenticationFactory
argument_list|(
literal|"abc"
argument_list|)
argument_list|,
literal|"test"
argument_list|,
literal|"test2"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|factory
operator|.
name|getAuthentication
argument_list|(
name|getUserConfiguration
argument_list|()
argument_list|,
name|root
argument_list|,
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|factory
operator|.
name|getAuthentication
argument_list|(
name|getUserConfiguration
argument_list|()
argument_list|,
name|root
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|factory
operator|.
name|getAuthentication
argument_list|(
name|getUserConfiguration
argument_list|()
argument_list|,
name|root
argument_list|,
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|factory
operator|.
name|getAuthentication
argument_list|(
name|getUserConfiguration
argument_list|()
argument_list|,
name|root
argument_list|,
literal|"another"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|TestUserAuthenticationFactory
implements|implements
name|UserAuthenticationFactory
block|{
specifier|private
specifier|final
name|String
name|userId
decl_stmt|;
specifier|private
name|TestUserAuthenticationFactory
parameter_list|(
annotation|@
name|NotNull
name|String
name|userId
parameter_list|)
block|{
name|this
operator|.
name|userId
operator|=
name|userId
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Authentication
name|getAuthentication
parameter_list|(
annotation|@
name|NotNull
name|UserConfiguration
name|configuration
parameter_list|,
annotation|@
name|NotNull
name|Root
name|root
parameter_list|,
annotation|@
name|Nullable
name|String
name|userId
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|userId
operator|.
name|equals
argument_list|(
name|userId
argument_list|)
condition|)
block|{
return|return
name|Mockito
operator|.
name|mock
argument_list|(
name|Authentication
operator|.
name|class
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

