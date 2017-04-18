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
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|Iterators
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
name|principal
operator|.
name|PrincipalIterator
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
name|PropertyState
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
name|Tree
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
name|Type
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
name|ImpersonationImplTest
extends|extends
name|ImpersonationImplEmptyTest
block|{
specifier|private
name|User
name|impersonator
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
name|impersonator
operator|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createUser
argument_list|(
literal|"impersonator"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|impersonation
operator|.
name|grantImpersonation
argument_list|(
name|impersonator
operator|.
name|getPrincipal
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
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
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|impersonator
operator|.
name|remove
argument_list|()
expr_stmt|;
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
name|Test
specifier|public
name|void
name|testGetImpersonators
parameter_list|()
throws|throws
name|Exception
block|{
name|PrincipalIterator
name|it
init|=
name|impersonation
operator|.
name|getImpersonators
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Iterators
operator|.
name|contains
argument_list|(
name|it
argument_list|,
name|impersonator
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetImpersonatorsImpersonatorRemoved
parameter_list|()
throws|throws
name|Exception
block|{
name|Principal
name|p
init|=
name|impersonator
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
name|impersonator
operator|.
name|remove
argument_list|()
expr_stmt|;
name|PrincipalIterator
name|it
init|=
name|impersonation
operator|.
name|getImpersonators
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Iterators
operator|.
name|contains
argument_list|(
name|it
argument_list|,
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testContentRepresentation
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|PropertyState
name|property
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_IMPERSONATORS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|property
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|impersonator
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAllows
parameter_list|()
throws|throws
name|Exception
block|{
name|Subject
name|s
init|=
operator|new
name|Subject
argument_list|(
literal|true
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|impersonator
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|impersonation
operator|.
name|allows
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAllowsIncludingNonExistingGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|Subject
name|s
init|=
operator|new
name|Subject
argument_list|(
literal|true
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|impersonator
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|groupPrincipal
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|impersonation
operator|.
name|allows
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAllowsImpersonatorRemoved
parameter_list|()
throws|throws
name|Exception
block|{
name|Subject
name|s
init|=
operator|new
name|Subject
argument_list|(
literal|true
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|impersonator
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
name|impersonator
operator|.
name|remove
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|impersonation
operator|.
name|allows
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRevoke
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|impersonation
operator|.
name|revokeImpersonation
argument_list|(
name|impersonator
operator|.
name|getPrincipal
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testContentRepresentationAfterModification
parameter_list|()
throws|throws
name|Exception
block|{
name|Principal
name|principal2
init|=
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
name|impersonation
operator|.
name|grantImpersonation
argument_list|(
name|principal2
argument_list|)
expr_stmt|;
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|PropertyState
name|property
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_IMPERSONATORS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|property
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|impersonator
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|principal2
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|impersonation
operator|.
name|revokeImpersonation
argument_list|(
name|impersonator
operator|.
name|getPrincipal
argument_list|()
argument_list|)
expr_stmt|;
name|property
operator|=
name|tree
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_IMPERSONATORS
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|property
argument_list|)
expr_stmt|;
name|expected
operator|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|principal2
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|impersonation
operator|.
name|revokeImpersonation
argument_list|(
name|principal2
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|tree
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_IMPERSONATORS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

