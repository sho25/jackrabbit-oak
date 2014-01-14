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
name|evaluation
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeTemplate
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
name|oak
operator|.
name|Oak
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
name|plugins
operator|.
name|identifier
operator|.
name|IdentifierManager
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
name|nodetype
operator|.
name|TypeEditorProvider
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
name|nodetype
operator|.
name|write
operator|.
name|ReadWriteNodeTypeManager
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
name|JcrConstants
operator|.
name|JCR_UUID
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
name|plugins
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
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
name|JcrUUIDTest
extends|extends
name|AbstractOakCoreTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|NT_NAME
init|=
literal|"referenceableTestNodeType"
decl_stmt|;
specifier|private
name|ReadWriteNodeTypeManager
name|ntMgr
decl_stmt|;
specifier|private
name|String
name|referenceablePath
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
name|ntMgr
operator|=
operator|new
name|ReadWriteNodeTypeManager
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Root
name|getWriteRoot
parameter_list|()
block|{
return|return
name|root
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Tree
name|getTypes
parameter_list|()
block|{
return|return
name|root
operator|.
name|getTree
argument_list|(
name|NODE_TYPES_PATH
argument_list|)
return|;
block|}
block|}
expr_stmt|;
if|if
condition|(
operator|!
name|ntMgr
operator|.
name|hasNodeType
argument_list|(
name|NT_NAME
argument_list|)
condition|)
block|{
name|NodeTypeTemplate
name|tmpl
init|=
name|ntMgr
operator|.
name|createNodeTypeTemplate
argument_list|()
decl_stmt|;
name|tmpl
operator|.
name|setName
argument_list|(
name|NT_NAME
argument_list|)
expr_stmt|;
name|tmpl
operator|.
name|setDeclaredSuperTypeNames
argument_list|(
operator|new
name|String
index|[]
block|{
name|JcrConstants
operator|.
name|MIX_REFERENCEABLE
block|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
block|}
argument_list|)
expr_stmt|;
name|ntMgr
operator|.
name|registerNodeType
argument_list|(
name|tmpl
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|NodeUtil
name|a
init|=
operator|new
name|NodeUtil
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
decl_stmt|;
name|NodeUtil
name|test
init|=
name|a
operator|.
name|addChild
argument_list|(
literal|"referenceable"
argument_list|,
name|NT_NAME
argument_list|)
decl_stmt|;
name|test
operator|.
name|setString
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|,
name|IdentifierManager
operator|.
name|generateUUID
argument_list|()
argument_list|)
expr_stmt|;
name|referenceablePath
operator|=
name|test
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Oak
name|withEditors
parameter_list|(
name|Oak
name|oak
parameter_list|)
block|{
return|return
name|oak
operator|.
name|with
argument_list|(
operator|new
name|TypeEditorProvider
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Creating a tree which is referenceable doesn't require any property      * related privilege to be granted as the jcr:uuid property is defined to      * be autocreated and protected.      */
annotation|@
name|Test
specifier|public
name|void
name|testCreateJcrUuid
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
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ADD_CHILD_NODES
argument_list|)
expr_stmt|;
name|Root
name|testRoot
init|=
name|getTestRoot
argument_list|()
decl_stmt|;
name|testRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|NodeUtil
name|a
init|=
operator|new
name|NodeUtil
argument_list|(
name|testRoot
operator|.
name|getTree
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
decl_stmt|;
name|NodeUtil
name|test
init|=
name|a
operator|.
name|addChild
argument_list|(
literal|"referenceable2"
argument_list|,
name|NT_NAME
argument_list|)
decl_stmt|;
name|test
operator|.
name|setString
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|,
name|IdentifierManager
operator|.
name|generateUUID
argument_list|()
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|/**      * Creating a referenceable tree with an invalid jcr:uuid must fail.      */
annotation|@
name|Test
specifier|public
name|void
name|testCreateInvalidJcrUuid
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
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ADD_CHILD_NODES
argument_list|)
expr_stmt|;
try|try
block|{
name|Root
name|testRoot
init|=
name|getTestRoot
argument_list|()
decl_stmt|;
name|testRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|NodeUtil
name|a
init|=
operator|new
name|NodeUtil
argument_list|(
name|testRoot
operator|.
name|getTree
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
decl_stmt|;
name|NodeUtil
name|test
init|=
name|a
operator|.
name|addChild
argument_list|(
literal|"referenceable2"
argument_list|,
name|NT_NAME
argument_list|)
decl_stmt|;
name|test
operator|.
name|setString
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|,
literal|"not a uuid"
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Creating a referenceable node with an invalid uuid must fail."
argument_list|)
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
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Creating a referenceable tree with an invalid jcr:uuid must fail.      */
annotation|@
name|Test
specifier|public
name|void
name|testCreateBooleanJcrUuid
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
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ADD_CHILD_NODES
argument_list|)
expr_stmt|;
try|try
block|{
name|Root
name|testRoot
init|=
name|getTestRoot
argument_list|()
decl_stmt|;
name|testRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|NodeUtil
name|a
init|=
operator|new
name|NodeUtil
argument_list|(
name|testRoot
operator|.
name|getTree
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
decl_stmt|;
name|NodeUtil
name|test
init|=
name|a
operator|.
name|addChild
argument_list|(
literal|"referenceable2"
argument_list|,
name|NT_NAME
argument_list|)
decl_stmt|;
name|test
operator|.
name|setBoolean
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Creating a referenceable node with an boolean uuid must fail."
argument_list|)
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
comment|/**      * Creating a non-referenceable tree with an jcr:uuid must fail      * with AccessDeniedException unless the REP_ADD_PROPERTY privilege      * is granted      */
annotation|@
name|Test
specifier|public
name|void
name|testCreateNonReferenceableJcrUuid
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
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ADD_CHILD_NODES
argument_list|)
expr_stmt|;
try|try
block|{
name|Root
name|testRoot
init|=
name|getTestRoot
argument_list|()
decl_stmt|;
name|NodeUtil
name|a
init|=
operator|new
name|NodeUtil
argument_list|(
name|testRoot
operator|.
name|getTree
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
decl_stmt|;
name|a
operator|.
name|setString
argument_list|(
name|JCR_UUID
argument_list|,
name|IdentifierManager
operator|.
name|generateUUID
argument_list|()
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Creating a jcr:uuid property for an unstructured node without ADD_PROPERTY permission must fail."
argument_list|)
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
name|isAccessViolation
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Modifying the jcr:uuid property must fail due to constraint violations.      */
annotation|@
name|Test
specifier|public
name|void
name|testModifyJcrUuid
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
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_WRITE
argument_list|)
expr_stmt|;
try|try
block|{
name|Root
name|testRoot
init|=
name|getTestRoot
argument_list|()
decl_stmt|;
name|Tree
name|test
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
name|referenceablePath
argument_list|)
decl_stmt|;
name|test
operator|.
name|setProperty
argument_list|(
name|JCR_UUID
argument_list|,
literal|"anothervalue"
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"An attempt to change the jcr:uuid property must fail"
argument_list|)
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
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Creating a non-referenceable tree with a jcr:uuid must fail      * with AccessDeniedException unless the REP_ADD_PROPERTY privilege      * is granted      */
annotation|@
name|Test
specifier|public
name|void
name|testModifyNonReferenceableJcrUuid
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeUtil
name|a
init|=
operator|new
name|NodeUtil
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
decl_stmt|;
name|a
operator|.
name|setString
argument_list|(
name|JCR_UUID
argument_list|,
literal|"some-value"
argument_list|)
expr_stmt|;
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
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ADD_CHILD_NODES
argument_list|)
expr_stmt|;
try|try
block|{
name|Root
name|testRoot
init|=
name|getTestRoot
argument_list|()
decl_stmt|;
name|a
operator|=
operator|new
name|NodeUtil
argument_list|(
name|testRoot
operator|.
name|getTree
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|a
operator|.
name|getString
argument_list|(
name|JCR_UUID
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|a
operator|.
name|setString
argument_list|(
name|JCR_UUID
argument_list|,
name|IdentifierManager
operator|.
name|generateUUID
argument_list|()
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Modifying a jcr:uuid property for an unstructured node without MODIFY_PROPERTY permission must fail."
argument_list|)
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
name|isAccessViolation
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Removing the jcr:uuid property must fail due to constraint violations.      */
annotation|@
name|Test
specifier|public
name|void
name|testRemoveJcrUuid
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
name|JCR_READ
argument_list|)
expr_stmt|;
try|try
block|{
name|Root
name|testRoot
init|=
name|getTestRoot
argument_list|()
decl_stmt|;
name|Tree
name|test
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
name|referenceablePath
argument_list|)
decl_stmt|;
name|test
operator|.
name|removeProperty
argument_list|(
name|JCR_UUID
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Removing the jcr:uuid property of a referenceable node must fail."
argument_list|)
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
name|assertEquals
argument_list|(
literal|22
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

