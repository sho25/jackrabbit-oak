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
name|query
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ValueFactory
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
name|plugins
operator|.
name|value
operator|.
name|ValueFactoryImpl
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
name|AuthorizableType
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * UserQueryManagerTest provides test cases for {@link UserQueryManager}.  * This class include the original jr2.x test-cases provided by  * {@code NodeResolverTest} and {@code IndexNodeResolverTest}.  */
end_comment

begin_class
specifier|public
class|class
name|UserQueryManagerTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|ValueFactory
name|valueFactory
decl_stmt|;
specifier|private
name|UserQueryManager
name|queryMgr
decl_stmt|;
specifier|private
name|User
name|user
decl_stmt|;
specifier|private
name|String
name|propertyName
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
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
name|UserManager
name|userMgr
init|=
name|getUserManager
argument_list|()
decl_stmt|;
name|user
operator|=
name|getTestUser
argument_list|()
expr_stmt|;
name|queryMgr
operator|=
operator|new
name|UserQueryManager
argument_list|(
name|userMgr
argument_list|,
name|namePathMapper
argument_list|,
name|getUserConfiguration
argument_list|()
operator|.
name|getConfigurationParameters
argument_list|()
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|valueFactory
operator|=
operator|new
name|ValueFactoryImpl
argument_list|(
name|root
operator|.
name|getBlobFactory
argument_list|()
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
name|propertyName
operator|=
literal|"testProperty"
expr_stmt|;
block|}
comment|/**      * @since OAK 1.0      */
annotation|@
name|Test
specifier|public
name|void
name|testFindNodesExact
parameter_list|()
throws|throws
name|Exception
block|{
name|Value
name|vs
init|=
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"value \\, containing backslash"
argument_list|)
decl_stmt|;
name|user
operator|.
name|setProperty
argument_list|(
name|propertyName
argument_list|,
name|vs
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
try|try
block|{
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|result
init|=
name|queryMgr
operator|.
name|findAuthorizables
argument_list|(
name|propertyName
argument_list|,
literal|"value \\, containing backslash"
argument_list|,
name|AuthorizableType
operator|.
name|USER
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"expected result"
argument_list|,
name|result
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|user
operator|.
name|getID
argument_list|()
argument_list|,
name|result
operator|.
name|next
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"expected no more results"
argument_list|,
name|result
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|user
operator|.
name|removeProperty
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindNodesNonExact
parameter_list|()
throws|throws
name|Exception
block|{
name|Value
name|vs
init|=
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"value \\, containing backslash"
argument_list|)
decl_stmt|;
name|user
operator|.
name|setProperty
argument_list|(
name|propertyName
argument_list|,
name|vs
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
try|try
block|{
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|result
init|=
name|queryMgr
operator|.
name|findAuthorizables
argument_list|(
name|propertyName
argument_list|,
literal|"value \\, containing backslash"
argument_list|,
name|AuthorizableType
operator|.
name|USER
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"expected result"
argument_list|,
name|result
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|user
operator|.
name|getID
argument_list|()
argument_list|,
name|result
operator|.
name|next
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"expected no more results"
argument_list|,
name|result
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|user
operator|.
name|removeProperty
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindNodesNonExactWithApostrophe
parameter_list|()
throws|throws
name|Exception
block|{
name|Value
name|vs
init|=
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"value ' with apostrophe"
argument_list|)
decl_stmt|;
try|try
block|{
name|user
operator|.
name|setProperty
argument_list|(
name|propertyName
argument_list|,
name|vs
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|result
init|=
name|queryMgr
operator|.
name|findAuthorizables
argument_list|(
name|propertyName
argument_list|,
literal|"value ' with apostrophe"
argument_list|,
name|AuthorizableType
operator|.
name|USER
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"expected result"
argument_list|,
name|result
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|user
operator|.
name|getID
argument_list|()
argument_list|,
name|result
operator|.
name|next
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"expected no more results"
argument_list|,
name|result
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|user
operator|.
name|removeProperty
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindNodesExactWithApostrophe
parameter_list|()
throws|throws
name|Exception
block|{
name|Value
name|vs
init|=
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"value ' with apostrophe"
argument_list|)
decl_stmt|;
try|try
block|{
name|user
operator|.
name|setProperty
argument_list|(
name|propertyName
argument_list|,
name|vs
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|result
init|=
name|queryMgr
operator|.
name|findAuthorizables
argument_list|(
name|propertyName
argument_list|,
literal|"value ' with apostrophe"
argument_list|,
name|AuthorizableType
operator|.
name|USER
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"expected result"
argument_list|,
name|result
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|user
operator|.
name|getID
argument_list|()
argument_list|,
name|result
operator|.
name|next
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"expected no more results"
argument_list|,
name|result
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|user
operator|.
name|removeProperty
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

