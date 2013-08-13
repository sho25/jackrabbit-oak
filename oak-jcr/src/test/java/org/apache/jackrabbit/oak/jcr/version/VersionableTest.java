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
name|version
package|;
end_package

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
name|Property
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|version
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|version
operator|.
name|VersionException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|version
operator|.
name|VersionManager
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
name|test
operator|.
name|AbstractJCRTest
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

begin_comment
comment|/**  *<code>VersionableTest</code> contains tests for method relevant to  * versionable nodes.  */
end_comment

begin_class
specifier|public
class|class
name|VersionableTest
extends|extends
name|AbstractJCRTest
block|{
specifier|public
name|void
name|testGetTypeOfPredecessors
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Node
name|node
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|nodeName1
argument_list|,
name|testNodeType
argument_list|)
decl_stmt|;
name|node
operator|.
name|addMixin
argument_list|(
name|mixVersionable
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|VersionManager
name|vMgr
init|=
name|superuser
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getVersionManager
argument_list|()
decl_stmt|;
name|vMgr
operator|.
name|checkin
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyType
operator|.
name|nameFromValue
argument_list|(
name|PropertyType
operator|.
name|REFERENCE
argument_list|)
argument_list|,
name|PropertyType
operator|.
name|nameFromValue
argument_list|(
name|node
operator|.
name|getProperty
argument_list|(
name|jcrPredecessors
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testReadOnlyAfterCheckin
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Node
name|node
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|nodeName1
argument_list|,
name|testNodeType
argument_list|)
decl_stmt|;
name|node
operator|.
name|addMixin
argument_list|(
name|mixVersionable
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|VersionManager
name|vMgr
init|=
name|superuser
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getVersionManager
argument_list|()
decl_stmt|;
name|vMgr
operator|.
name|checkin
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|node
operator|.
name|setProperty
argument_list|(
name|propertyName1
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"setProperty() must fail on a checked-in node"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|VersionException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
specifier|public
name|void
name|testReferenceableChild
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Node
name|node
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|nodeName1
argument_list|,
name|ntUnstructured
argument_list|)
decl_stmt|;
name|node
operator|.
name|addMixin
argument_list|(
name|mixVersionable
argument_list|)
expr_stmt|;
name|Node
name|child
init|=
name|node
operator|.
name|addNode
argument_list|(
name|nodeName2
argument_list|,
name|ntUnstructured
argument_list|)
decl_stmt|;
name|child
operator|.
name|addMixin
argument_list|(
name|mixReferenceable
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|VersionManager
name|vMgr
init|=
name|superuser
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getVersionManager
argument_list|()
decl_stmt|;
name|vMgr
operator|.
name|checkin
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test from Jackrabbit: JCR-3635 (OAK-940)      *      * Tests the case when a node already has a manual set      * JcrConstants.JCR_FROZENUUID property and is versioned. The manual set      * frozenUuid will overwrite the one that is automatically assigned by the      * VersionManager, which should not happen      */
specifier|public
name|void
name|testCopyFrozenUuidProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|firstNode
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|nodeName1
argument_list|)
decl_stmt|;
name|firstNode
operator|.
name|setPrimaryType
argument_list|(
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|firstNode
operator|.
name|addMixin
argument_list|(
name|JcrConstants
operator|.
name|MIX_VERSIONABLE
argument_list|)
expr_stmt|;
name|firstNode
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// create version for the node
name|Version
name|firstNodeVersion
init|=
name|firstNode
operator|.
name|checkin
argument_list|()
decl_stmt|;
name|firstNode
operator|.
name|checkout
argument_list|()
expr_stmt|;
name|Node
name|secondNode
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|nodeName2
argument_list|)
decl_stmt|;
name|secondNode
operator|.
name|setPrimaryType
argument_list|(
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|secondNode
operator|.
name|addMixin
argument_list|(
name|JcrConstants
operator|.
name|MIX_VERSIONABLE
argument_list|)
expr_stmt|;
name|Property
name|firstNodeVersionFrozenUuid
init|=
name|firstNodeVersion
operator|.
name|getFrozenNode
argument_list|()
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_FROZENUUID
argument_list|)
decl_stmt|;
name|secondNode
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_FROZENUUID
argument_list|,
name|firstNodeVersionFrozenUuid
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|secondNode
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// create version of the second node
name|Version
name|secondNodeVersion
init|=
name|secondNode
operator|.
name|checkin
argument_list|()
decl_stmt|;
name|secondNode
operator|.
name|checkout
argument_list|()
expr_stmt|;
comment|// frozenUuid from the second node version node should not be the same as the one from the first node version
name|Property
name|secondBodeVersionFrozenUuid
init|=
name|secondNodeVersion
operator|.
name|getFrozenNode
argument_list|()
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_FROZENUUID
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|JcrConstants
operator|.
name|JCR_FROZENUUID
operator|+
literal|" should not be the same for two different versions of different nodes! "
argument_list|,
name|secondBodeVersionFrozenUuid
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|firstNodeVersionFrozenUuid
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCheckoutWithPendingChanges
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|node
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|nodeName1
argument_list|,
name|testNodeType
argument_list|)
decl_stmt|;
name|node
operator|.
name|addMixin
argument_list|(
name|mixVersionable
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|node
operator|.
name|checkin
argument_list|()
expr_stmt|;
name|Node
name|newNode
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|nodeName2
argument_list|,
name|testNodeType
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|newNode
operator|.
name|isNew
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|checkout
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|node
operator|.
name|isCheckedOut
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|newNode
operator|.
name|isNew
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

