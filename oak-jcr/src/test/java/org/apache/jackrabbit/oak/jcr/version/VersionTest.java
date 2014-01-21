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
name|NodeIterator
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
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|QueryManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|RowIterator
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
comment|/**  *<code>VersionTest</code> performs tests on JCR Version nodes.  */
end_comment

begin_class
specifier|public
class|class
name|VersionTest
extends|extends
name|AbstractJCRTest
block|{
specifier|public
name|void
name|testGetNodeByIdentifier
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Node
name|n
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
name|n
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
name|String
name|id
init|=
name|vMgr
operator|.
name|getBaseVersion
argument_list|(
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|getIdentifier
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Session.getNodeByIdentifier() did not return Version object for a nt:version node."
argument_list|,
name|superuser
operator|.
name|getNodeByIdentifier
argument_list|(
name|id
argument_list|)
operator|instanceof
name|Version
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
name|void
name|testGetNodeByUUID
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Node
name|n
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
name|n
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
name|String
name|uuid
init|=
name|vMgr
operator|.
name|getBaseVersion
argument_list|(
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|getUUID
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Session.getNodeByUUID() did not return Version object for a nt:version node."
argument_list|,
name|superuser
operator|.
name|getNodeByUUID
argument_list|(
name|uuid
argument_list|)
operator|instanceof
name|Version
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testVersionFromQuery
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|Node
name|n
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
name|n
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
name|checkpoint
argument_list|(
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|QueryManager
name|qm
init|=
name|superuser
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getQueryManager
argument_list|()
decl_stmt|;
name|Version
name|v
init|=
name|vMgr
operator|.
name|getBaseVersion
argument_list|(
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
name|qm
operator|.
name|createQuery
argument_list|(
literal|"//element(*, nt:version)[@jcr:uuid = '"
operator|+
name|v
operator|.
name|getIdentifier
argument_list|()
operator|+
literal|"']"
argument_list|,
name|Query
operator|.
name|XPATH
argument_list|)
decl_stmt|;
name|NodeIterator
name|nodes
init|=
name|q
operator|.
name|execute
argument_list|()
operator|.
name|getNodes
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|nodes
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodes
operator|.
name|nextNode
argument_list|()
operator|instanceof
name|Version
argument_list|)
expr_stmt|;
name|RowIterator
name|rows
init|=
name|q
operator|.
name|execute
argument_list|()
operator|.
name|getRows
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|rows
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rows
operator|.
name|nextRow
argument_list|()
operator|.
name|getNode
argument_list|()
operator|instanceof
name|Version
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testFrozenNode
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Node
name|n
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
name|n
operator|.
name|addMixin
argument_list|(
name|mixVersionable
argument_list|)
expr_stmt|;
name|Node
name|child
init|=
name|n
operator|.
name|addNode
argument_list|(
name|nodeName2
argument_list|,
name|ntUnstructured
argument_list|)
decl_stmt|;
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
name|checkpoint
argument_list|(
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Version
name|v
init|=
name|vMgr
operator|.
name|getBaseVersion
argument_list|(
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|Node
name|frozenChild
init|=
name|v
operator|.
name|getFrozenNode
argument_list|()
operator|.
name|getNode
argument_list|(
name|child
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ntFrozenNode
argument_list|,
name|frozenChild
operator|.
name|getPrimaryNodeType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// OAK-1009& OAK-1346
specifier|public
name|void
name|testFrozenUUID
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Node
name|n
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
name|n
operator|.
name|addMixin
argument_list|(
name|mixVersionable
argument_list|)
expr_stmt|;
name|Node
name|child
init|=
name|n
operator|.
name|addNode
argument_list|(
name|nodeName2
argument_list|,
literal|"nt:folder"
argument_list|)
decl_stmt|;
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
name|Version
name|v
init|=
name|vMgr
operator|.
name|checkpoint
argument_list|(
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|vMgr
operator|.
name|checkpoint
argument_list|(
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Version
name|baseVersion
init|=
name|vMgr
operator|.
name|getBaseVersion
argument_list|(
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|Node
name|frozenChild
init|=
name|baseVersion
operator|.
name|getFrozenNode
argument_list|()
operator|.
name|getNode
argument_list|(
name|child
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|child
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|frozenChild
operator|.
name|getProperty
argument_list|(
name|Property
operator|.
name|JCR_FROZEN_UUID
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|vMgr
operator|.
name|restore
argument_list|(
name|v
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

