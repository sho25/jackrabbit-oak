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
name|nodetype
operator|.
name|NodeDefinitionTemplate
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
name|NodeTypeTemplate
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
name|OnParentVersionAction
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
name|oak
operator|.
name|spi
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
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
name|version
operator|.
name|VersionConstants
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

begin_comment
comment|/**  * Test OPV ABORT  */
end_comment

begin_class
specifier|public
class|class
name|OpvAbortTest
extends|extends
name|AbstractJCRTest
implements|implements
name|VersionConstants
block|{
specifier|private
name|VersionManager
name|vMgr
decl_stmt|;
annotation|@
name|Override
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
name|vMgr
operator|=
name|superuser
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getVersionManager
argument_list|()
expr_stmt|;
name|NodeTypeManager
name|ntMgr
init|=
name|superuser
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getNodeTypeManager
argument_list|()
decl_stmt|;
name|NodeDefinitionTemplate
name|def
init|=
name|ntMgr
operator|.
name|createNodeDefinitionTemplate
argument_list|()
decl_stmt|;
name|def
operator|.
name|setOnParentVersion
argument_list|(
name|OnParentVersionAction
operator|.
name|ABORT
argument_list|)
expr_stmt|;
name|def
operator|.
name|setName
argument_list|(
literal|"child"
argument_list|)
expr_stmt|;
name|def
operator|.
name|setRequiredPrimaryTypeNames
argument_list|(
operator|new
name|String
index|[]
block|{
name|NT_BASE
block|}
argument_list|)
expr_stmt|;
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
literal|"OpvAbortTest"
argument_list|)
expr_stmt|;
name|tmpl
operator|.
name|setMixin
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|tmpl
operator|.
name|getNodeDefinitionTemplates
argument_list|()
operator|.
name|add
argument_list|(
name|def
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
name|testRootNode
operator|.
name|addMixin
argument_list|(
name|MIX_VERSIONABLE
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testDirectChild
parameter_list|()
throws|throws
name|Exception
block|{
name|testRootNode
operator|.
name|addMixin
argument_list|(
literal|"OpvAbortTest"
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"child"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|OnParentVersionAction
operator|.
name|ABORT
argument_list|,
name|n
operator|.
name|getDefinition
argument_list|()
operator|.
name|getOnParentVersion
argument_list|()
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
try|try
block|{
name|vMgr
operator|.
name|checkpoint
argument_list|(
name|testRootNode
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|VersionException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
name|superuser
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testChildInSubTree
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|n
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|nodeName1
argument_list|)
operator|.
name|addNode
argument_list|(
name|nodeName2
argument_list|)
decl_stmt|;
name|n
operator|.
name|addMixin
argument_list|(
literal|"OpvAbortTest"
argument_list|)
expr_stmt|;
name|Node
name|child
init|=
name|n
operator|.
name|addNode
argument_list|(
literal|"child"
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|OnParentVersionAction
operator|.
name|ABORT
argument_list|,
name|child
operator|.
name|getDefinition
argument_list|()
operator|.
name|getOnParentVersion
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|vMgr
operator|.
name|checkpoint
argument_list|(
name|testRootNode
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|VersionException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
name|superuser
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

