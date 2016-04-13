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
name|jcr
operator|.
name|nodetype
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|Repository
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
name|SimpleCredentials
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
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|JackrabbitRepository
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
name|oak
operator|.
name|jcr
operator|.
name|Jcr
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
name|segment
operator|.
name|SegmentStore
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
name|segment
operator|.
name|file
operator|.
name|FileStore
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
name|net
operator|.
name|lingala
operator|.
name|zip4j
operator|.
name|core
operator|.
name|ZipFile
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
name|segment
operator|.
name|SegmentNodeStore
operator|.
name|builder
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
name|UpgradeTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|upgradeFrom10
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|testFolder
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|,
name|UpgradeTest
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|repoHome
init|=
operator|new
name|File
argument_list|(
name|testFolder
argument_list|,
literal|"test-repo-1.0"
argument_list|)
decl_stmt|;
name|repoHome
operator|.
name|delete
argument_list|()
expr_stmt|;
name|File
name|tmpZip
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"test-repo"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|NodeTypeTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"test-repo-1.0.zip"
argument_list|)
argument_list|,
operator|new
name|FileOutputStream
argument_list|(
name|tmpZip
argument_list|)
argument_list|)
expr_stmt|;
name|ZipFile
name|repoZip
init|=
operator|new
name|ZipFile
argument_list|(
name|tmpZip
argument_list|)
decl_stmt|;
name|repoZip
operator|.
name|extractAll
argument_list|(
name|testFolder
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|tmpZip
operator|.
name|delete
argument_list|()
expr_stmt|;
name|SegmentStore
name|store
init|=
name|FileStore
operator|.
name|builder
argument_list|(
name|repoHome
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Repository
name|repo
init|=
operator|new
name|Jcr
argument_list|(
name|builder
argument_list|(
name|store
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|createRepository
argument_list|()
decl_stmt|;
name|Session
name|s
init|=
name|repo
operator|.
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Node
name|myType
init|=
name|s
operator|.
name|getNode
argument_list|(
literal|"/jcr:system/jcr:nodeTypes/test:MyType"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|Iterators
operator|.
name|size
argument_list|(
name|myType
operator|.
name|getNodes
argument_list|(
literal|"jcr:propertyDefinition"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|NodeTypeManager
name|ntMgr
init|=
name|s
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getNodeTypeManager
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|ntMgr
operator|.
name|hasNodeType
argument_list|(
literal|"test:MyType"
argument_list|)
argument_list|)
expr_stmt|;
name|NodeType
name|nt
init|=
name|ntMgr
operator|.
name|getNodeType
argument_list|(
literal|"test:MyType"
argument_list|)
decl_stmt|;
name|PropertyDefinition
index|[]
name|pDefs
init|=
name|nt
operator|.
name|getDeclaredPropertyDefinitions
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|pDefs
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|PropertyDefinition
name|pd
range|:
name|pDefs
control|)
block|{
name|String
name|name
init|=
name|pd
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"test:mandatory"
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|pd
operator|.
name|isMandatory
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"test:optional"
argument_list|)
condition|)
block|{
name|assertFalse
argument_list|(
name|pd
operator|.
name|isMandatory
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Unexpected property definition: "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
block|}
comment|// flip mandatory flag for test:mandatory
name|String
name|cnd
init|=
literal|"<'test'='http://www.apache.org/jackrabbit/test'>\n"
operator|+
literal|"[test:MyType]> nt:unstructured\n"
operator|+
literal|" - test:mandatory (string)\n"
operator|+
literal|" - test:optional (string)"
decl_stmt|;
name|CndImporter
operator|.
name|registerNodeTypes
argument_list|(
operator|new
name|StringReader
argument_list|(
name|cnd
argument_list|)
argument_list|,
name|s
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|myType
operator|=
name|s
operator|.
name|getNode
argument_list|(
literal|"/jcr:system/jcr:nodeTypes/test:MyType"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|Iterators
operator|.
name|size
argument_list|(
name|myType
operator|.
name|getNodes
argument_list|(
literal|"jcr:propertyDefinition"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|nt
operator|=
name|ntMgr
operator|.
name|getNodeType
argument_list|(
literal|"test:MyType"
argument_list|)
expr_stmt|;
name|pDefs
operator|=
name|nt
operator|.
name|getDeclaredPropertyDefinitions
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|pDefs
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|PropertyDefinition
name|pd
range|:
name|pDefs
control|)
block|{
name|String
name|name
init|=
name|pd
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"test:mandatory"
argument_list|)
condition|)
block|{
name|assertFalse
argument_list|(
name|pd
operator|.
name|isMandatory
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"test:optional"
argument_list|)
condition|)
block|{
name|assertFalse
argument_list|(
name|pd
operator|.
name|isMandatory
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Unexpected property definition: "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
block|}
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
if|if
condition|(
name|repo
operator|instanceof
name|JackrabbitRepository
condition|)
block|{
operator|(
operator|(
name|JackrabbitRepository
operator|)
name|repo
operator|)
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

