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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|Authorizable
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
name|Group
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
name|assertSame
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

begin_class
specifier|public
class|class
name|AuthorizableTypeTest
block|{
specifier|private
name|User
name|user
decl_stmt|;
specifier|private
name|Group
name|gr
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
name|Mockito
operator|.
name|when
argument_list|(
name|user
operator|.
name|isGroup
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|gr
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Group
operator|.
name|class
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|gr
operator|.
name|isGroup
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetType
parameter_list|()
throws|throws
name|Exception
block|{
name|assertSame
argument_list|(
name|AuthorizableType
operator|.
name|USER
argument_list|,
name|AuthorizableType
operator|.
name|getType
argument_list|(
name|UserManager
operator|.
name|SEARCH_TYPE_USER
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|AuthorizableType
operator|.
name|GROUP
argument_list|,
name|AuthorizableType
operator|.
name|getType
argument_list|(
name|UserManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|AuthorizableType
operator|.
name|AUTHORIZABLE
argument_list|,
name|AuthorizableType
operator|.
name|getType
argument_list|(
name|UserManager
operator|.
name|SEARCH_TYPE_AUTHORIZABLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetTypeIllegalSearchType
parameter_list|()
throws|throws
name|Exception
block|{
name|AuthorizableType
operator|.
name|getType
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsTypeUser
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|AuthorizableType
operator|.
name|USER
operator|.
name|isType
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|AuthorizableType
operator|.
name|USER
operator|.
name|isType
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|AuthorizableType
operator|.
name|USER
operator|.
name|isType
argument_list|(
name|gr
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsTypeGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|AuthorizableType
operator|.
name|GROUP
operator|.
name|isType
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|AuthorizableType
operator|.
name|GROUP
operator|.
name|isType
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|AuthorizableType
operator|.
name|GROUP
operator|.
name|isType
argument_list|(
name|gr
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsTypeAuthorizable
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|AuthorizableType
operator|.
name|AUTHORIZABLE
operator|.
name|isType
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|AuthorizableType
operator|.
name|AUTHORIZABLE
operator|.
name|isType
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|AuthorizableType
operator|.
name|AUTHORIZABLE
operator|.
name|isType
argument_list|(
name|gr
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAuthorizableClass
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|User
operator|.
name|class
argument_list|,
name|AuthorizableType
operator|.
name|USER
operator|.
name|getAuthorizableClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Group
operator|.
name|class
argument_list|,
name|AuthorizableType
operator|.
name|GROUP
operator|.
name|getAuthorizableClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Authorizable
operator|.
name|class
argument_list|,
name|AuthorizableType
operator|.
name|AUTHORIZABLE
operator|.
name|getAuthorizableClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
