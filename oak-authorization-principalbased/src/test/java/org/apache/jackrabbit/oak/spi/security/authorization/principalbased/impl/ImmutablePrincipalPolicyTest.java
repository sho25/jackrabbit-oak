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
name|authorization
operator|.
name|principalbased
operator|.
name|impl
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
name|commons
operator|.
name|PathUtils
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
name|authorization
operator|.
name|accesscontrol
operator|.
name|ImmutableACL
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
name|principal
operator|.
name|EveryonePrincipal
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
name|privilege
operator|.
name|PrivilegeConstants
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
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|accesscontrol
operator|.
name|AccessControlConstants
operator|.
name|REP_GLOB
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
name|assertNotEquals
import|;
end_import

begin_class
specifier|public
class|class
name|ImmutablePrincipalPolicyTest
extends|extends
name|AbstractPrincipalBasedTest
block|{
specifier|private
name|PrincipalPolicyImpl
name|policy
decl_stmt|;
specifier|private
name|ImmutablePrincipalPolicy
name|immutable
decl_stmt|;
annotation|@
name|Before
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
name|policy
operator|=
name|setupPrincipalBasedAccessControl
argument_list|(
name|getTestSystemUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|testContentJcrPath
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|immutable
operator|=
operator|new
name|ImmutablePrincipalPolicy
argument_list|(
name|policy
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|getTestSystemUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|immutable
operator|.
name|getPrincipal
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AccessControlException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testAddEntry
parameter_list|()
throws|throws
name|Exception
block|{
name|immutable
operator|.
name|addEntry
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|,
name|privilegesFromNames
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AccessControlException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testAddEntryWithRestrictions
parameter_list|()
throws|throws
name|Exception
block|{
name|immutable
operator|.
name|addEntry
argument_list|(
literal|null
argument_list|,
name|privilegesFromNames
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|getNamePathMapper
argument_list|()
operator|.
name|getJcrName
argument_list|(
name|REP_GLOB
argument_list|)
argument_list|,
name|getValueFactory
argument_list|(
name|root
argument_list|)
operator|.
name|createValue
argument_list|(
literal|"*"
argument_list|)
argument_list|)
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHashcode
parameter_list|()
block|{
name|int
name|expectedHashCode
init|=
name|immutable
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|ImmutablePrincipalPolicy
name|ipp
init|=
operator|new
name|ImmutablePrincipalPolicy
argument_list|(
name|policy
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|policy
operator|.
name|getOakPath
argument_list|()
argument_list|,
name|policy
operator|.
name|getEntries
argument_list|()
argument_list|,
name|policy
operator|.
name|getRestrictionProvider
argument_list|()
argument_list|,
name|policy
operator|.
name|getNamePathMapper
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedHashCode
argument_list|,
name|ipp
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedHashCode
argument_list|,
operator|new
name|ImmutablePrincipalPolicy
argument_list|(
name|policy
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|ImmutablePrincipalPolicy
name|ipp
init|=
operator|new
name|ImmutablePrincipalPolicy
argument_list|(
name|policy
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|policy
operator|.
name|getOakPath
argument_list|()
argument_list|,
name|policy
operator|.
name|getEntries
argument_list|()
argument_list|,
name|policy
operator|.
name|getRestrictionProvider
argument_list|()
argument_list|,
name|policy
operator|.
name|getNamePathMapper
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|immutable
argument_list|,
name|ipp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|immutable
argument_list|,
operator|new
name|ImmutablePrincipalPolicy
argument_list|(
name|policy
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|immutable
argument_list|,
name|immutable
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotEquals
parameter_list|()
block|{
name|ImmutablePrincipalPolicy
name|differentPath
init|=
operator|new
name|ImmutablePrincipalPolicy
argument_list|(
name|policy
operator|.
name|getPrincipal
argument_list|()
argument_list|,
literal|"/different/path"
argument_list|,
name|policy
operator|.
name|getEntries
argument_list|()
argument_list|,
name|policy
operator|.
name|getRestrictionProvider
argument_list|()
argument_list|,
name|policy
operator|.
name|getNamePathMapper
argument_list|()
argument_list|)
decl_stmt|;
name|ImmutablePrincipalPolicy
name|differentPrincipal
init|=
operator|new
name|ImmutablePrincipalPolicy
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|policy
operator|.
name|getOakPath
argument_list|()
argument_list|,
name|policy
operator|.
name|getEntries
argument_list|()
argument_list|,
name|policy
operator|.
name|getRestrictionProvider
argument_list|()
argument_list|,
name|policy
operator|.
name|getNamePathMapper
argument_list|()
argument_list|)
decl_stmt|;
name|ImmutablePrincipalPolicy
name|differentEntries
init|=
operator|new
name|ImmutablePrincipalPolicy
argument_list|(
name|policy
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|policy
operator|.
name|getOakPath
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|policy
operator|.
name|getRestrictionProvider
argument_list|()
argument_list|,
name|policy
operator|.
name|getNamePathMapper
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotEquals
argument_list|(
name|immutable
argument_list|,
name|policy
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|immutable
argument_list|,
operator|new
name|ImmutableACL
argument_list|(
name|policy
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|immutable
argument_list|,
name|differentPath
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|immutable
argument_list|,
name|differentPrincipal
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|immutable
argument_list|,
name|differentEntries
argument_list|)
expr_stmt|;
name|int
name|hc
init|=
name|immutable
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|assertNotEquals
argument_list|(
name|hc
argument_list|,
name|policy
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|hc
argument_list|,
operator|new
name|ImmutableACL
argument_list|(
name|policy
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|hc
argument_list|,
name|differentPath
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|hc
argument_list|,
name|differentPrincipal
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|hc
argument_list|,
name|differentEntries
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

