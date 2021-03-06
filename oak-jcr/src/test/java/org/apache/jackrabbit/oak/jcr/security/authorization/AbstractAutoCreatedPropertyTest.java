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
name|Node
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Testing removing and re-adding node which defines autocreated protected properties.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractAutoCreatedPropertyTest
extends|extends
name|AbstractEvaluationTest
block|{
name|Node
name|targetNode
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
name|Node
name|leaf
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|childNPath
argument_list|)
decl_stmt|;
name|targetNode
operator|=
name|leaf
operator|.
name|addNode
argument_list|(
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|targetNode
operator|.
name|addMixin
argument_list|(
name|getMixinName
argument_list|()
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
specifier|abstract
name|String
name|getNodeName
parameter_list|()
function_decl|;
specifier|abstract
name|String
name|getMixinName
parameter_list|()
function_decl|;
annotation|@
name|Test
specifier|public
name|void
name|testReplaceNode
parameter_list|()
throws|throws
name|Exception
block|{
name|allow
argument_list|(
name|path
argument_list|,
name|privilegesFromNames
argument_list|(
operator|new
name|String
index|[]
block|{
name|Privilege
operator|.
name|JCR_MODIFY_PROPERTIES
block|,
name|Privilege
operator|.
name|JCR_NODE_TYPE_MANAGEMENT
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|removeItem
argument_list|(
name|targetNode
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|newNode
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|childNPath
argument_list|)
operator|.
name|addNode
argument_list|(
name|targetNode
operator|.
name|getName
argument_list|()
argument_list|,
name|targetNode
operator|.
name|getPrimaryNodeType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|newNode
operator|.
name|addMixin
argument_list|(
name|getMixinName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
name|testSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReplaceNode2
parameter_list|()
throws|throws
name|Exception
block|{
name|allow
argument_list|(
name|path
argument_list|,
name|privilegesFromNames
argument_list|(
operator|new
name|String
index|[]
block|{
name|Privilege
operator|.
name|JCR_ADD_CHILD_NODES
block|,
name|Privilege
operator|.
name|JCR_NODE_TYPE_MANAGEMENT
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|removeItem
argument_list|(
name|targetNode
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|newNode
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|childNPath
argument_list|)
operator|.
name|addNode
argument_list|(
name|targetNode
operator|.
name|getName
argument_list|()
argument_list|,
name|targetNode
operator|.
name|getPrimaryNodeType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|newNode
operator|.
name|addMixin
argument_list|(
name|getMixinName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
name|testSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReplaceNode3
parameter_list|()
throws|throws
name|Exception
block|{
name|allow
argument_list|(
name|path
argument_list|,
name|privilegesFromNames
argument_list|(
operator|new
name|String
index|[]
block|{
name|Privilege
operator|.
name|JCR_REMOVE_CHILD_NODES
block|,
name|Privilege
operator|.
name|JCR_NODE_TYPE_MANAGEMENT
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|removeItem
argument_list|(
name|targetNode
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|newNode
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|childNPath
argument_list|)
operator|.
name|addNode
argument_list|(
name|targetNode
operator|.
name|getName
argument_list|()
argument_list|,
name|targetNode
operator|.
name|getPrimaryNodeType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|newNode
operator|.
name|addMixin
argument_list|(
name|getMixinName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
name|testSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReplaceNode4
parameter_list|()
throws|throws
name|Exception
block|{
name|allow
argument_list|(
name|path
argument_list|,
name|privilegesFromNames
argument_list|(
operator|new
name|String
index|[]
block|{
name|Privilege
operator|.
name|JCR_ADD_CHILD_NODES
block|,
name|Privilege
operator|.
name|JCR_REMOVE_NODE
block|,
name|Privilege
operator|.
name|JCR_REMOVE_CHILD_NODES
block|,
name|Privilege
operator|.
name|JCR_NODE_TYPE_MANAGEMENT
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|removeItem
argument_list|(
name|targetNode
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|newNode
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|childNPath
argument_list|)
operator|.
name|addNode
argument_list|(
name|targetNode
operator|.
name|getName
argument_list|()
argument_list|,
name|targetNode
operator|.
name|getPrimaryNodeType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|newNode
operator|.
name|addMixin
argument_list|(
name|getMixinName
argument_list|()
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
name|testRemoveReAddMixin
parameter_list|()
throws|throws
name|Exception
block|{
name|allow
argument_list|(
name|path
argument_list|,
name|privilegesFromNames
argument_list|(
operator|new
name|String
index|[]
block|{
name|Privilege
operator|.
name|JCR_ADD_CHILD_NODES
block|,
name|Privilege
operator|.
name|JCR_REMOVE_NODE
block|,
name|Privilege
operator|.
name|JCR_REMOVE_CHILD_NODES
block|}
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|Node
name|refNode
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|targetNode
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|refNode
operator|.
name|removeMixin
argument_list|(
name|getMixinName
argument_list|()
argument_list|)
expr_stmt|;
name|refNode
operator|.
name|addMixin
argument_list|(
name|getMixinName
argument_list|()
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|()
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
block|}
end_class

end_unit

