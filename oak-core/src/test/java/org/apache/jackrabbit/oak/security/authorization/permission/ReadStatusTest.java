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
name|authorization
operator|.
name|permission
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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|permission
operator|.
name|Permissions
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
name|restriction
operator|.
name|RestrictionPattern
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
name|PrivilegeBitsProvider
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|security
operator|.
name|authorization
operator|.
name|permission
operator|.
name|ReadStatus
operator|.
name|ALLOW_ALL
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
name|authorization
operator|.
name|permission
operator|.
name|ReadStatus
operator|.
name|DENY_ALL
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
name|authorization
operator|.
name|permission
operator|.
name|ReadStatus
operator|.
name|DENY_THIS
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
name|privilege
operator|.
name|PrivilegeConstants
operator|.
name|JCR_ALL
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
name|privilege
operator|.
name|PrivilegeConstants
operator|.
name|JCR_READ
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
name|privilege
operator|.
name|PrivilegeConstants
operator|.
name|REP_READ_NODES
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
name|privilege
operator|.
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
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
name|ReadStatusTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|PrivilegeBitsProvider
name|bitsProvider
decl_stmt|;
specifier|private
name|PermissionEntry
name|grantAll
decl_stmt|;
specifier|private
name|PermissionEntry
name|denyAll
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
name|bitsProvider
operator|=
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|grantAll
operator|=
name|createPermissionEntry
argument_list|(
literal|true
argument_list|,
name|JCR_ALL
argument_list|)
expr_stmt|;
name|denyAll
operator|=
name|createPermissionEntry
argument_list|(
literal|false
argument_list|,
name|JCR_ALL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|NotNull
specifier|private
name|PermissionEntry
name|createPermissionEntry
parameter_list|(
name|boolean
name|isAllow
parameter_list|,
annotation|@
name|NotNull
name|String
modifier|...
name|privNames
parameter_list|)
block|{
return|return
name|createPermissionEntry
argument_list|(
name|isAllow
argument_list|,
name|RestrictionPattern
operator|.
name|EMPTY
argument_list|,
name|privNames
argument_list|)
return|;
block|}
annotation|@
name|NotNull
specifier|private
name|PermissionEntry
name|createPermissionEntry
parameter_list|(
name|boolean
name|isAllow
parameter_list|,
annotation|@
name|NotNull
name|RestrictionPattern
name|pattern
parameter_list|,
annotation|@
name|NotNull
name|String
modifier|...
name|privNames
parameter_list|)
block|{
return|return
operator|new
name|PermissionEntry
argument_list|(
literal|"/path"
argument_list|,
name|isAllow
argument_list|,
literal|0
argument_list|,
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|privNames
argument_list|)
argument_list|,
name|pattern
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|assertDenied
parameter_list|(
annotation|@
name|NotNull
name|ReadStatus
name|rs
parameter_list|)
block|{
name|assertFalse
argument_list|(
name|rs
operator|.
name|allowsThis
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rs
operator|.
name|allowsProperties
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rs
operator|.
name|allowsAll
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|assertAllowed
parameter_list|(
annotation|@
name|NotNull
name|ReadStatus
name|rs
parameter_list|,
name|boolean
name|canReadProperties
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|rs
operator|.
name|allowsThis
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|canReadProperties
argument_list|,
name|rs
operator|.
name|allowsProperties
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rs
operator|.
name|allowsAll
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSkippedAllowed
parameter_list|()
block|{
name|ReadStatus
name|rs
init|=
name|ReadStatus
operator|.
name|create
argument_list|(
name|grantAll
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertAllowed
argument_list|(
name|rs
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|rs
argument_list|,
name|ReadStatus
operator|.
name|create
argument_list|(
name|grantAll
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSkippedDenied
parameter_list|()
block|{
name|ReadStatus
name|rs
init|=
name|ReadStatus
operator|.
name|create
argument_list|(
name|denyAll
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertDenied
argument_list|(
name|rs
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|rs
argument_list|,
name|ReadStatus
operator|.
name|create
argument_list|(
name|denyAll
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadAcTargetPermissionAllow
parameter_list|()
block|{
name|ReadStatus
name|rs
init|=
name|ReadStatus
operator|.
name|create
argument_list|(
name|grantAll
argument_list|,
name|Permissions
operator|.
name|READ_ACCESS_CONTROL
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertAllowed
argument_list|(
name|rs
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|rs
argument_list|,
name|ReadStatus
operator|.
name|create
argument_list|(
name|grantAll
argument_list|,
name|Permissions
operator|.
name|READ_ACCESS_CONTROL
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadAcTargetPermissionDeny
parameter_list|()
block|{
name|ReadStatus
name|rs
init|=
name|ReadStatus
operator|.
name|create
argument_list|(
name|denyAll
argument_list|,
name|Permissions
operator|.
name|READ_ACCESS_CONTROL
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertDenied
argument_list|(
name|rs
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|rs
argument_list|,
name|ReadStatus
operator|.
name|create
argument_list|(
name|denyAll
argument_list|,
name|Permissions
operator|.
name|READ_ACCESS_CONTROL
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNonEmptyPatternAllow
parameter_list|()
block|{
name|PermissionEntry
name|entry
init|=
name|createPermissionEntry
argument_list|(
literal|true
argument_list|,
name|mock
argument_list|(
name|RestrictionPattern
operator|.
name|class
argument_list|)
argument_list|,
name|JCR_ALL
argument_list|)
decl_stmt|;
name|ReadStatus
name|rs
init|=
name|ReadStatus
operator|.
name|create
argument_list|(
name|entry
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertAllowed
argument_list|(
name|rs
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNonEmptyPatternDeny
parameter_list|()
block|{
name|PermissionEntry
name|entry
init|=
name|createPermissionEntry
argument_list|(
literal|false
argument_list|,
name|mock
argument_list|(
name|RestrictionPattern
operator|.
name|class
argument_list|)
argument_list|,
name|JCR_ALL
argument_list|)
decl_stmt|;
name|ReadStatus
name|rs
init|=
name|ReadStatus
operator|.
name|create
argument_list|(
name|entry
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertDenied
argument_list|(
name|rs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOnlyReadNodesGranted
parameter_list|()
block|{
name|PermissionEntry
name|entry
init|=
name|createPermissionEntry
argument_list|(
literal|true
argument_list|,
name|REP_READ_NODES
argument_list|)
decl_stmt|;
name|ReadStatus
name|rs
init|=
name|ReadStatus
operator|.
name|create
argument_list|(
name|entry
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertAllowed
argument_list|(
name|rs
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOnlyReadNodesDenied
parameter_list|()
block|{
name|PermissionEntry
name|entry
init|=
name|createPermissionEntry
argument_list|(
literal|false
argument_list|,
name|REP_READ_NODES
argument_list|)
decl_stmt|;
name|ReadStatus
name|rs
init|=
name|ReadStatus
operator|.
name|create
argument_list|(
name|entry
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertDenied
argument_list|(
name|rs
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|DENY_THIS
argument_list|,
name|rs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOnlyReadPropertiesGranted
parameter_list|()
block|{
name|PermissionEntry
name|entry
init|=
name|createPermissionEntry
argument_list|(
literal|true
argument_list|,
name|REP_READ_PROPERTIES
argument_list|)
decl_stmt|;
name|ReadStatus
name|rs
init|=
name|ReadStatus
operator|.
name|create
argument_list|(
name|entry
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertAllowed
argument_list|(
name|rs
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOnlyReadPropertiesDenied
parameter_list|()
block|{
name|PermissionEntry
name|entry
init|=
name|createPermissionEntry
argument_list|(
literal|false
argument_list|,
name|REP_READ_PROPERTIES
argument_list|)
decl_stmt|;
name|ReadStatus
name|rs
init|=
name|ReadStatus
operator|.
name|create
argument_list|(
name|entry
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertDenied
argument_list|(
name|rs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadGranted
parameter_list|()
block|{
name|PermissionEntry
name|entry
init|=
name|createPermissionEntry
argument_list|(
literal|true
argument_list|,
name|JCR_READ
argument_list|)
decl_stmt|;
name|ReadStatus
name|rs
init|=
name|ReadStatus
operator|.
name|create
argument_list|(
name|entry
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertAllowed
argument_list|(
name|rs
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|ALLOW_ALL
argument_list|,
name|rs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadDenied
parameter_list|()
block|{
name|PermissionEntry
name|entry
init|=
name|createPermissionEntry
argument_list|(
literal|false
argument_list|,
name|JCR_READ
argument_list|)
decl_stmt|;
name|ReadStatus
name|rs
init|=
name|ReadStatus
operator|.
name|create
argument_list|(
name|entry
argument_list|,
name|Permissions
operator|.
name|ALL
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertDenied
argument_list|(
name|rs
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|DENY_ALL
argument_list|,
name|rs
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
