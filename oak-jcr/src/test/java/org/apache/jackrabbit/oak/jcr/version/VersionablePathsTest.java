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

begin_class
specifier|public
class|class
name|VersionablePathsTest
extends|extends
name|AbstractJCRTest
block|{
specifier|private
name|VersionManager
name|getVersionManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|superuser
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getVersionManager
argument_list|()
return|;
block|}
specifier|public
name|void
name|testVersionablePaths
parameter_list|()
throws|throws
name|Exception
block|{
name|testRootNode
operator|.
name|addMixin
argument_list|(
name|JcrConstants
operator|.
name|MIX_VERSIONABLE
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|Node
name|vh
init|=
name|getVersionManager
argument_list|()
operator|.
name|getVersionHistory
argument_list|(
name|testRootNode
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|vh
operator|.
name|isNodeType
argument_list|(
literal|"rep:VersionablePaths"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|workspaceName
init|=
name|superuser
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|vh
operator|.
name|hasProperty
argument_list|(
name|workspaceName
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testRootNode
operator|.
name|getPath
argument_list|()
argument_list|,
name|vh
operator|.
name|getProperty
argument_list|(
name|workspaceName
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testVersionablePathsAfterRename
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|node1
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|nodeName1
argument_list|)
decl_stmt|;
name|node1
operator|.
name|addMixin
argument_list|(
name|JcrConstants
operator|.
name|MIX_VERSIONABLE
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|String
name|destPath
init|=
name|testRoot
operator|+
literal|"/"
operator|+
name|nodeName2
decl_stmt|;
name|superuser
operator|.
name|move
argument_list|(
name|node1
operator|.
name|getPath
argument_list|()
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|Node
name|vh
init|=
name|getVersionManager
argument_list|()
operator|.
name|getVersionHistory
argument_list|(
name|node1
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|vh
operator|.
name|isNodeType
argument_list|(
literal|"rep:VersionablePaths"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|workspaceName
init|=
name|superuser
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|vh
operator|.
name|hasProperty
argument_list|(
name|workspaceName
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|node1
operator|.
name|getPath
argument_list|()
argument_list|,
name|vh
operator|.
name|getProperty
argument_list|(
name|workspaceName
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testVersionablePathsAfterMove
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|node1
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|nodeName1
argument_list|)
decl_stmt|;
name|Node
name|node2
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|nodeName2
argument_list|)
decl_stmt|;
name|node1
operator|.
name|addMixin
argument_list|(
name|JcrConstants
operator|.
name|MIX_VERSIONABLE
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|String
name|destPath
init|=
name|node2
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|nodeName1
decl_stmt|;
name|superuser
operator|.
name|move
argument_list|(
name|node1
operator|.
name|getPath
argument_list|()
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|destPath
argument_list|,
name|node1
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|vh
init|=
name|getVersionManager
argument_list|()
operator|.
name|getVersionHistory
argument_list|(
name|node1
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|vh
operator|.
name|isNodeType
argument_list|(
literal|"rep:VersionablePaths"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|workspaceName
init|=
name|superuser
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|vh
operator|.
name|hasProperty
argument_list|(
name|workspaceName
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|node1
operator|.
name|getPath
argument_list|()
argument_list|,
name|vh
operator|.
name|getProperty
argument_list|(
name|workspaceName
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testVersionablePathsAfterParentMove
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|node1
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|nodeName1
argument_list|)
decl_stmt|;
name|Node
name|node3
init|=
name|node1
operator|.
name|addNode
argument_list|(
name|nodeName3
argument_list|)
decl_stmt|;
name|Node
name|node2
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|nodeName2
argument_list|)
decl_stmt|;
name|node3
operator|.
name|addMixin
argument_list|(
name|JcrConstants
operator|.
name|MIX_VERSIONABLE
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|String
name|destPath
init|=
name|node2
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|nodeName1
decl_stmt|;
name|superuser
operator|.
name|move
argument_list|(
name|node1
operator|.
name|getPath
argument_list|()
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|destPath
operator|+
literal|'/'
operator|+
name|nodeName3
argument_list|,
name|node3
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|vh
init|=
name|getVersionManager
argument_list|()
operator|.
name|getVersionHistory
argument_list|(
name|node3
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|vh
operator|.
name|isNodeType
argument_list|(
literal|"rep:VersionablePaths"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|workspaceName
init|=
name|superuser
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|vh
operator|.
name|hasProperty
argument_list|(
name|workspaceName
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|node3
operator|.
name|getPath
argument_list|()
argument_list|,
name|vh
operator|.
name|getProperty
argument_list|(
name|workspaceName
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

