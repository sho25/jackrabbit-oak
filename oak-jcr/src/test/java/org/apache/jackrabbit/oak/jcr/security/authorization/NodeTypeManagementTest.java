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
name|jcr
operator|.
name|security
operator|.
name|authorization
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|AccessDeniedException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ImportUUIDBehavior
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
name|Workspace
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
name|Privilege
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
name|NotExecutableException
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

begin_comment
comment|/**  * Permission evaluation tests related to {@link Privilege#JCR_NODE_TYPE_MANAGEMENT} privilege.  */
end_comment

begin_class
specifier|public
class|class
name|NodeTypeManagementTest
extends|extends
name|AbstractEvaluationTest
block|{
specifier|private
name|Node
name|childNode
decl_stmt|;
specifier|private
name|String
name|mixinName
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
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
name|Node
name|child
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|childNPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|isNodeType
argument_list|(
name|mixReferenceable
argument_list|)
operator|||
operator|!
name|child
operator|.
name|canAddMixin
argument_list|(
name|mixReferenceable
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NotExecutableException
argument_list|()
throw|;
block|}
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|testSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|mixinName
operator|=
name|testSession
operator|.
name|getNamespacePrefix
argument_list|(
name|NS_MIX_URI
argument_list|)
operator|+
literal|":referenceable"
expr_stmt|;
name|childNode
operator|=
name|testSession
operator|.
name|getNode
argument_list|(
name|child
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertReadOnly
argument_list|(
name|childNode
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCanAddMixin
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|childNode
operator|.
name|canAddMixin
argument_list|(
name|mixinName
argument_list|)
argument_list|)
expr_stmt|;
name|modify
argument_list|(
name|childNode
operator|.
name|getPath
argument_list|()
argument_list|,
name|Privilege
operator|.
name|JCR_NODE_TYPE_MANAGEMENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|childNode
operator|.
name|canAddMixin
argument_list|(
name|mixinName
argument_list|)
argument_list|)
expr_stmt|;
name|modify
argument_list|(
name|childNode
operator|.
name|getPath
argument_list|()
argument_list|,
name|Privilege
operator|.
name|JCR_NODE_TYPE_MANAGEMENT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|childNode
operator|.
name|canAddMixin
argument_list|(
name|mixinName
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddMixinWithoutPermission
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|childNode
operator|.
name|addMixin
argument_list|(
name|mixinName
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"TestSession does not have sufficient privileges to add a mixin type."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddMixin
parameter_list|()
throws|throws
name|Exception
block|{
name|modify
argument_list|(
name|childNode
operator|.
name|getPath
argument_list|()
argument_list|,
name|Privilege
operator|.
name|JCR_NODE_TYPE_MANAGEMENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|childNode
operator|.
name|addMixin
argument_list|(
name|mixinName
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveMixinWithoutPermission
parameter_list|()
throws|throws
name|Exception
block|{
operator|(
operator|(
name|Node
operator|)
name|superuser
operator|.
name|getItem
argument_list|(
name|childNode
operator|.
name|getPath
argument_list|()
argument_list|)
operator|)
operator|.
name|addMixin
argument_list|(
name|mixinName
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|testSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|childNode
operator|.
name|removeMixin
argument_list|(
name|mixinName
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"TestSession does not have sufficient privileges to remove a mixin type."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveMixin
parameter_list|()
throws|throws
name|Exception
block|{
operator|(
operator|(
name|Node
operator|)
name|superuser
operator|.
name|getItem
argument_list|(
name|childNode
operator|.
name|getPath
argument_list|()
argument_list|)
operator|)
operator|.
name|addMixin
argument_list|(
name|mixinName
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|testSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|modify
argument_list|(
name|childNode
operator|.
name|getPath
argument_list|()
argument_list|,
name|Privilege
operator|.
name|JCR_NODE_TYPE_MANAGEMENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|childNode
operator|.
name|removeMixin
argument_list|(
name|mixinName
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetPrimaryTypeWithoutPrivilege
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|child
init|=
operator|(
name|Node
operator|)
name|superuser
operator|.
name|getItem
argument_list|(
name|childNode
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|ntName
init|=
name|child
operator|.
name|getPrimaryNodeType
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
try|try
block|{
name|childNode
operator|.
name|setPrimaryType
argument_list|(
literal|"oak:Unstructured"
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"TestSession does not have sufficient privileges to change the primary type."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
name|testSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|ntName
operator|.
name|equals
argument_list|(
name|child
operator|.
name|getPrimaryNodeType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|child
operator|.
name|setPrimaryType
argument_list|(
name|ntName
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetPrimaryType
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|child
init|=
operator|(
name|Node
operator|)
name|superuser
operator|.
name|getItem
argument_list|(
name|childNode
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|ntName
init|=
name|child
operator|.
name|getPrimaryNodeType
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|changedNtName
init|=
literal|"oak:Unstructured"
decl_stmt|;
name|child
operator|.
name|setPrimaryType
argument_list|(
name|changedNtName
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|modify
argument_list|(
name|childNode
operator|.
name|getPath
argument_list|()
argument_list|,
name|Privilege
operator|.
name|JCR_NODE_TYPE_MANAGEMENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|childNode
operator|.
name|setPrimaryType
argument_list|(
name|ntName
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
comment|/**      * Test difference between common jcr:write privilege an rep:write privilege      * that includes the ability to set the primary node type upon child node      * creation.      */
annotation|@
name|Test
specifier|public
name|void
name|testAddNode
parameter_list|()
throws|throws
name|Exception
block|{
comment|// with simple write privilege a child node can be added BUT no
comment|// node type must be specified.
name|modify
argument_list|(
name|childNode
operator|.
name|getPath
argument_list|()
argument_list|,
name|Privilege
operator|.
name|JCR_WRITE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|addChildNode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|addChildNode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Missing privilege jcr:nodeTypeManagement."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success
block|}
comment|// adding jcr:nodeTypeManagement privilege will allow to use any
comment|// variant of Node.addNode.
name|modify
argument_list|(
name|childNode
operator|.
name|getPath
argument_list|()
argument_list|,
name|Privilege
operator|.
name|JCR_NODE_TYPE_MANAGEMENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|addChildNode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|addChildNode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addChildNode
parameter_list|(
name|boolean
name|specifyNodeType
parameter_list|)
throws|throws
name|Exception
block|{
name|Node
name|n
init|=
literal|null
decl_stmt|;
try|try
block|{
name|n
operator|=
operator|(
name|specifyNodeType
operator|)
condition|?
name|childNode
operator|.
name|addNode
argument_list|(
name|nodeName3
argument_list|,
name|testNodeType
argument_list|)
else|:
name|childNode
operator|.
name|addNode
argument_list|(
name|nodeName3
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|n
operator|!=
literal|null
condition|)
block|{
name|n
operator|.
name|remove
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCopy
parameter_list|()
throws|throws
name|Exception
block|{
name|Workspace
name|wsp
init|=
name|testSession
operator|.
name|getWorkspace
argument_list|()
decl_stmt|;
name|String
name|parentPath
init|=
name|childNode
operator|.
name|getParent
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|srcPath
init|=
name|childNode
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|destPath
init|=
name|parentPath
operator|+
literal|"/destination"
decl_stmt|;
try|try
block|{
name|wsp
operator|.
name|copy
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Missing write privilege."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success
block|}
comment|// with simple write privilege copying a node is not allowed.
name|modify
argument_list|(
name|parentPath
argument_list|,
name|Privilege
operator|.
name|JCR_WRITE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|wsp
operator|.
name|copy
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Missing privilege jcr:nodeTypeManagement."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success
block|}
comment|// adding jcr:nodeTypeManagement privilege will grant permission to copy.
name|modify
argument_list|(
name|parentPath
argument_list|,
name|REP_WRITE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|wsp
operator|.
name|copy
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWorkspaceMove
parameter_list|()
throws|throws
name|Exception
block|{
name|Workspace
name|wsp
init|=
name|testSession
operator|.
name|getWorkspace
argument_list|()
decl_stmt|;
name|String
name|parentPath
init|=
name|childNode
operator|.
name|getParent
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|srcPath
init|=
name|childNode
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|destPath
init|=
name|parentPath
operator|+
literal|"/destination"
decl_stmt|;
try|try
block|{
name|wsp
operator|.
name|move
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Missing write privilege."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success
block|}
comment|// with simple write privilege moving a node is not allowed.
name|modify
argument_list|(
name|parentPath
argument_list|,
name|Privilege
operator|.
name|JCR_WRITE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|wsp
operator|.
name|move
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Missing privilege jcr:nodeTypeManagement."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success
block|}
comment|// adding jcr:nodeTypeManagement privilege will grant permission to move.
name|modify
argument_list|(
name|parentPath
argument_list|,
name|REP_WRITE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|wsp
operator|.
name|move
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSessionMove
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|parentPath
init|=
name|childNode
operator|.
name|getParent
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|srcPath
init|=
name|childNode
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|destPath
init|=
name|parentPath
operator|+
literal|"/destination"
decl_stmt|;
try|try
block|{
name|testSession
operator|.
name|move
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Missing write privilege."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success
block|}
comment|// with simple write privilege moving a node is not allowed.
name|modify
argument_list|(
name|parentPath
argument_list|,
name|Privilege
operator|.
name|JCR_WRITE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|testSession
operator|.
name|move
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Missing privilege jcr:nodeTypeManagement."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success
block|}
comment|// adding jcr:nodeTypeManagement privilege will grant permission to move.
name|modify
argument_list|(
name|parentPath
argument_list|,
name|REP_WRITE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|move
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSessionImportXML
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|parentPath
init|=
name|childNode
operator|.
name|getPath
argument_list|()
decl_stmt|;
try|try
block|{
name|testSession
operator|.
name|importXML
argument_list|(
name|parentPath
argument_list|,
name|getXmlForImport
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_CREATE_NEW
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Missing write privilege."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
name|testSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// with simple write privilege moving a node is not allowed.
name|modify
argument_list|(
name|parentPath
argument_list|,
name|Privilege
operator|.
name|JCR_WRITE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|testSession
operator|.
name|importXML
argument_list|(
name|parentPath
argument_list|,
name|getXmlForImport
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_CREATE_NEW
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Missing privilege jcr:nodeTypeManagement."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
name|testSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// adding jcr:nodeTypeManagement privilege will grant permission to move.
name|modify
argument_list|(
name|parentPath
argument_list|,
name|REP_WRITE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|importXML
argument_list|(
name|parentPath
argument_list|,
name|getXmlForImport
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_CREATE_NEW
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWorkspaceImportXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Workspace
name|wsp
init|=
name|testSession
operator|.
name|getWorkspace
argument_list|()
decl_stmt|;
name|String
name|parentPath
init|=
name|childNode
operator|.
name|getPath
argument_list|()
decl_stmt|;
try|try
block|{
name|wsp
operator|.
name|importXML
argument_list|(
name|parentPath
argument_list|,
name|getXmlForImport
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_CREATE_NEW
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Missing write privilege."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success
block|}
comment|// with simple write privilege moving a node is not allowed.
name|modify
argument_list|(
name|parentPath
argument_list|,
name|Privilege
operator|.
name|JCR_WRITE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|wsp
operator|.
name|importXML
argument_list|(
name|parentPath
argument_list|,
name|getXmlForImport
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_CREATE_NEW
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Missing privilege jcr:nodeTypeManagement."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success
block|}
comment|// adding jcr:nodeTypeManagement privilege will grant permission to move.
name|modify
argument_list|(
name|parentPath
argument_list|,
name|REP_WRITE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|wsp
operator|.
name|importXML
argument_list|(
name|parentPath
argument_list|,
name|getXmlForImport
argument_list|()
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_CREATE_NEW
argument_list|)
expr_stmt|;
block|}
comment|/**      * Simple XML for testing permissions upon import.      */
specifier|private
name|InputStream
name|getXmlForImport
parameter_list|()
block|{
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\""
operator|+
literal|"         xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\""
operator|+
literal|"         xmlns:jcr=\"http://www.jcp.org/jcr/1.0\""
operator|+
literal|"         sv:name=\""
operator|+
name|nodeName3
operator|+
literal|"\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\">"
operator|+
literal|"<sv:value>"
operator|+
name|testNodeType
operator|+
literal|"</sv:value>"
operator|+
literal|"</sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|xml
operator|.
name|getBytes
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

