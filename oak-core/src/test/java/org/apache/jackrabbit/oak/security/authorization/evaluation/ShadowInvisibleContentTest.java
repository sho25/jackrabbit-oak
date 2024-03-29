begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|evaluation
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
name|oak
operator|.
name|api
operator|.
name|CommitFailedException
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|util
operator|.
name|NodeUtil
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
name|ShadowInvisibleContentTest
extends|extends
name|AbstractOakCoreTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testShadowInvisibleNode
parameter_list|()
throws|throws
name|Exception
block|{
name|setupPermission
argument_list|(
literal|"/a"
argument_list|,
name|testPrincipal
argument_list|,
literal|true
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
literal|"/a/b"
argument_list|,
name|testPrincipal
argument_list|,
literal|false
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
literal|"/a/b/c"
argument_list|,
name|testPrincipal
argument_list|,
literal|true
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
expr_stmt|;
name|Root
name|testRoot
init|=
name|getTestRoot
argument_list|()
decl_stmt|;
name|Tree
name|a
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
literal|"/a"
argument_list|)
decl_stmt|;
comment|// /b not visible to this session
name|assertFalse
argument_list|(
name|a
operator|.
name|hasChild
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
comment|// shadow /b with transient node of the same name
name|Tree
name|b
init|=
name|a
operator|.
name|addChild
argument_list|(
literal|"b"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|hasChild
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|hasChild
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isConstraintViolation
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testShadowInvisibleProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|setupPermission
argument_list|(
literal|"/a"
argument_list|,
name|testPrincipal
argument_list|,
literal|true
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
literal|"/a"
argument_list|,
name|testPrincipal
argument_list|,
literal|false
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
expr_stmt|;
name|Root
name|testRoot
init|=
name|getTestRoot
argument_list|()
decl_stmt|;
name|Tree
name|a
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
literal|"/a"
argument_list|)
decl_stmt|;
comment|// /a/aProp not visible to this session
name|assertNull
argument_list|(
name|a
operator|.
name|getProperty
argument_list|(
literal|"aProp"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|a
operator|.
name|hasProperty
argument_list|(
literal|"aProp"
argument_list|)
argument_list|)
expr_stmt|;
comment|// shadow /a/aProp with transient property of the same name
name|a
operator|.
name|setProperty
argument_list|(
literal|"aProp"
argument_list|,
literal|"aValue1"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|a
operator|.
name|getProperty
argument_list|(
literal|"aProp"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|hasProperty
argument_list|(
literal|"aProp"
argument_list|)
argument_list|)
expr_stmt|;
comment|// after commit() normal access control again takes over!
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// does not fail since only read access is denied
name|assertNull
argument_list|(
name|a
operator|.
name|getProperty
argument_list|(
literal|"aProp"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|a
operator|.
name|hasProperty
argument_list|(
literal|"aProp"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testShadowInvisibleProperty2
parameter_list|()
throws|throws
name|Exception
block|{
name|setupPermission
argument_list|(
literal|"/a"
argument_list|,
name|testPrincipal
argument_list|,
literal|true
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
literal|"/a"
argument_list|,
name|testPrincipal
argument_list|,
literal|false
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
literal|"/a"
argument_list|,
name|testPrincipal
argument_list|,
literal|false
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_ALTER_PROPERTIES
argument_list|)
expr_stmt|;
name|Root
name|testRoot
init|=
name|getTestRoot
argument_list|()
decl_stmt|;
name|Tree
name|a
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
literal|"/a"
argument_list|)
decl_stmt|;
comment|// /a/aProp not visible to this session
name|assertNull
argument_list|(
name|a
operator|.
name|getProperty
argument_list|(
literal|"aProp"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|a
operator|.
name|hasProperty
argument_list|(
literal|"aProp"
argument_list|)
argument_list|)
expr_stmt|;
comment|// shadow /a/aProp with transient property of the same name *and value*
name|a
operator|.
name|setProperty
argument_list|(
literal|"aProp"
argument_list|,
literal|"aValue"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|a
operator|.
name|getProperty
argument_list|(
literal|"aProp"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|hasProperty
argument_list|(
literal|"aProp"
argument_list|)
argument_list|)
expr_stmt|;
comment|// after commit() normal access control again takes over
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// does not fail since no changes are detected, even when write access is denied
name|assertNull
argument_list|(
name|a
operator|.
name|getProperty
argument_list|(
literal|"aProp"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|a
operator|.
name|hasProperty
argument_list|(
literal|"aProp"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAddNodeCollidingWithInvisibleNode
parameter_list|()
throws|throws
name|Exception
block|{
name|setupPermission
argument_list|(
literal|"/a"
argument_list|,
name|testPrincipal
argument_list|,
literal|true
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
literal|"/a/b"
argument_list|,
name|testPrincipal
argument_list|,
literal|false
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
literal|"/a/b/c"
argument_list|,
name|testPrincipal
argument_list|,
literal|true
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
expr_stmt|;
name|Root
name|testRoot
init|=
name|getTestRoot
argument_list|()
decl_stmt|;
name|Tree
name|a
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
literal|"/a"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|a
operator|.
name|getChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|getChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"c"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
operator|new
name|NodeUtil
argument_list|(
name|a
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"b"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|getChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|a
operator|.
name|getChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"c"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// now shadowed
comment|// since we have write access, the old content gets replaced
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// note that also the deny-read ACL gets replaced
name|assertTrue
argument_list|(
name|a
operator|.
name|getChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|a
operator|.
name|getChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"c"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

