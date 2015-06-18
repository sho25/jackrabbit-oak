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
name|Node
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
name|Value
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|JcrConstants
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|security
operator|.
name|ExerciseUtility
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
name|AbstractJCRTest
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
name|assertArrayEquals
import|;
end_import

begin_comment
comment|/**  *<pre>  * Module: User Management  * =============================================================================  *  * Title: Authorizable Properties  * -----------------------------------------------------------------------------  *  * Goal:  * The aim of this exercise is to be aware of the API calls to set arbitrary  * (non-protected) properties on authorizables.  *  * Exercises:  *  * - Overview  *   List all methods defined on {@link org.apache.jackrabbit.api.security.user.Authorizable}  *   that allow to read and write arbitrary properties on a given user or group.  *  * - {@link #testArbitraryProperties()}  *   Become familiar with the API to read arbitrary properties with an user  *   or group. Also fill in a list of properties defined by JCR and the  *   authorizable node types that cannot be obtained using the methods define  *   on {@link org.apache.jackrabbit.api.security.user.Authorizable}.  *  * - {@link #testSpecialProperties()}  *   This tests uses the user properties API to retrieve protected JCR and  *   user management internal properties.  *   Fix the tests according to your expectations and your understanding of  *   the API contract and the implementation.  *</pre>  *  * @see org.apache.jackrabbit.api.security.user.Authorizable  */
end_comment

begin_class
specifier|public
class|class
name|L7_AuthorizablePropertiesTest
extends|extends
name|AbstractJCRTest
block|{
specifier|private
specifier|final
specifier|static
name|String
name|EMAIL_REL_PATH
init|=
literal|"properties/email"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|PETS_REL_PATH
init|=
literal|"pets"
decl_stmt|;
specifier|private
name|UserManager
name|userManager
decl_stmt|;
specifier|private
name|User
name|testUser
decl_stmt|;
specifier|private
name|Group
name|testGroup
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
name|userManager
operator|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|superuser
operator|)
operator|.
name|getUserManager
argument_list|()
expr_stmt|;
name|testUser
operator|=
name|ExerciseUtility
operator|.
name|createTestUser
argument_list|(
name|userManager
argument_list|)
expr_stmt|;
name|testUser
operator|.
name|disable
argument_list|(
literal|"no longer active"
argument_list|)
expr_stmt|;
name|testGroup
operator|=
name|ExerciseUtility
operator|.
name|createTestGroup
argument_list|(
name|userManager
argument_list|)
expr_stmt|;
name|testGroup
operator|.
name|addMember
argument_list|(
name|testUser
argument_list|)
expr_stmt|;
name|testGroup
operator|.
name|setProperty
argument_list|(
literal|"Name"
argument_list|,
name|superuser
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
literal|"Test Group"
argument_list|)
argument_list|)
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
if|if
condition|(
name|testGroup
operator|!=
literal|null
condition|)
block|{
name|testGroup
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
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
specifier|private
name|Node
name|getAuthorizableNode
parameter_list|(
name|Authorizable
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|path
init|=
name|authorizable
operator|.
name|getPath
argument_list|()
decl_stmt|;
return|return
name|superuser
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
return|;
block|}
specifier|public
name|void
name|testArbitraryProperties
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// set 2 different properties (single and multivalued)
name|testUser
operator|.
name|setProperty
argument_list|(
name|EMAIL_REL_PATH
argument_list|,
name|superuser
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
literal|"testUser@oak.apache.org"
argument_list|)
argument_list|)
expr_stmt|;
name|testUser
operator|.
name|setProperty
argument_list|(
name|PETS_REL_PATH
argument_list|,
operator|new
name|Value
index|[]
block|{
name|superuser
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
literal|"cat"
argument_list|)
block|,
name|superuser
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
literal|"rabbit"
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|Node
name|node
init|=
name|getAuthorizableNode
argument_list|(
name|testUser
argument_list|)
decl_stmt|;
comment|// EXERCISE: build the list of existing user properties rel paths
name|List
argument_list|<
name|String
argument_list|>
name|userPropertiesPath
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|relPath
range|:
name|userPropertiesPath
control|)
block|{
name|assertTrue
argument_list|(
name|testUser
operator|.
name|hasProperty
argument_list|(
name|relPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Value
index|[]
name|emailsExpected
init|=
name|testUser
operator|.
name|getProperty
argument_list|(
name|EMAIL_REL_PATH
argument_list|)
decl_stmt|;
name|String
name|expectedRelPath
init|=
literal|null
decl_stmt|;
comment|// EXERCISE
name|assertEquals
argument_list|(
name|emailsExpected
index|[
literal|0
index|]
argument_list|,
name|node
operator|.
name|getProperty
argument_list|(
name|expectedRelPath
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|Value
index|[]
name|petsExpected
init|=
name|testUser
operator|.
name|getProperty
argument_list|(
name|PETS_REL_PATH
argument_list|)
decl_stmt|;
name|expectedRelPath
operator|=
literal|null
expr_stmt|;
comment|// EXERCISE
name|assertArrayEquals
argument_list|(
name|petsExpected
argument_list|,
name|node
operator|.
name|getProperty
argument_list|(
name|expectedRelPath
argument_list|)
operator|.
name|getValues
argument_list|()
argument_list|)
expr_stmt|;
comment|// EXERCISE: build a list of protected JCR properties that cannot be accessed using the Authorizable interface
name|List
argument_list|<
name|String
argument_list|>
name|protectedJcrPropertyNames
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|relPath
range|:
name|protectedJcrPropertyNames
control|)
block|{
name|assertFalse
argument_list|(
name|testUser
operator|.
name|hasProperty
argument_list|(
name|relPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// EXERCISE: build a list of protected properties defined by the authorizable or user node type definitions that cannot be accessed using the Authorizable interface
name|List
argument_list|<
name|String
argument_list|>
name|protectedAuthorizablePropertyNames
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|relPath
range|:
name|protectedAuthorizablePropertyNames
control|)
block|{
name|assertFalse
argument_list|(
name|testUser
operator|.
name|hasProperty
argument_list|(
name|relPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// remove the properties again
name|testUser
operator|.
name|removeProperty
argument_list|(
name|EMAIL_REL_PATH
argument_list|)
expr_stmt|;
name|testUser
operator|.
name|removeProperty
argument_list|(
name|PETS_REL_PATH
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testSpecialProperties
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|expectedGroupPropNames
init|=
literal|null
decl_stmt|;
comment|// EXERCISE
name|Iterator
argument_list|<
name|String
argument_list|>
name|propNames
init|=
name|testGroup
operator|.
name|getPropertyNames
argument_list|()
decl_stmt|;
while|while
condition|(
name|propNames
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
name|expectedGroupPropNames
operator|.
name|remove
argument_list|(
name|propNames
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|expectedGroupPropNames
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|Boolean
name|hasPrimaryType
init|=
literal|null
decl_stmt|;
comment|// EXERCISE
name|assertEquals
argument_list|(
name|hasPrimaryType
operator|.
name|booleanValue
argument_list|()
argument_list|,
name|testGroup
operator|.
name|hasProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|)
expr_stmt|;
name|Value
index|[]
name|expectedMembers
init|=
literal|null
decl_stmt|;
comment|// EXERCISE
name|Value
index|[]
name|members
init|=
name|testGroup
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_MEMBERS
argument_list|)
decl_stmt|;
block|}
block|}
end_class

end_unit

