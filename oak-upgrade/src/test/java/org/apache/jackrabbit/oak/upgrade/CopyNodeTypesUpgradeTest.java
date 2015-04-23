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
name|upgrade
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
name|commons
operator|.
name|cnd
operator|.
name|CndImporter
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
name|commons
operator|.
name|cnd
operator|.
name|CompactNodeTypeDefReader
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
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
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
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeDefinition
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|PropertyDefinition
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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

begin_class
specifier|public
class|class
name|CopyNodeTypesUpgradeTest
extends|extends
name|AbstractRepositoryUpgradeTest
block|{
annotation|@
name|Override
specifier|protected
name|void
name|createSourceContent
parameter_list|(
name|Repository
name|repository
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Session
name|session
init|=
name|repository
operator|.
name|login
argument_list|(
name|CREDENTIALS
argument_list|)
decl_stmt|;
specifier|final
name|Reader
name|cnd
init|=
operator|new
name|InputStreamReader
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-nodetypes.cnd"
argument_list|)
argument_list|)
decl_stmt|;
name|CndImporter
operator|.
name|registerNodeTypes
argument_list|(
name|cnd
argument_list|,
name|session
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|customNodeTypesAreRegistered
parameter_list|()
throws|throws
name|RepositoryException
block|{
specifier|final
name|JackrabbitSession
name|adminSession
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
specifier|final
name|NodeTypeManager
name|nodeTypeManager
init|=
name|adminSession
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getNodeTypeManager
argument_list|()
decl_stmt|;
specifier|final
name|NodeType
name|testFolderNodeType
init|=
name|nodeTypeManager
operator|.
name|getNodeType
argument_list|(
literal|"test:Folder"
argument_list|)
decl_stmt|;
specifier|final
name|NodeDefinition
index|[]
name|cnd
init|=
name|testFolderNodeType
operator|.
name|getChildNodeDefinitions
argument_list|()
decl_stmt|;
specifier|final
name|PropertyDefinition
index|[]
name|pd
init|=
name|testFolderNodeType
operator|.
name|getPropertyDefinitions
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"More than one child node definition"
argument_list|,
literal|1
argument_list|,
name|cnd
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect default primary type"
argument_list|,
literal|"test:Folder"
argument_list|,
name|cnd
index|[
literal|0
index|]
operator|.
name|getDefaultPrimaryTypeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"More than two property definitions"
argument_list|,
literal|4
argument_list|,
name|pd
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

