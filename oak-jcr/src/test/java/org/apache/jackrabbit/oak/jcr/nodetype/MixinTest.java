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
name|nodetype
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
name|NoSuchNodeTypeException
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|Authorizable
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
name|NT_UNSTRUCTURED
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|MixinTest
extends|extends
name|AbstractJCRTest
block|{
specifier|public
name|void
name|testRemoveMixinWithoutMixinProperty
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
literal|"testRemoveMixinWithoutMixinProperty"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
try|try
block|{
name|node
operator|.
name|removeMixin
argument_list|(
name|JcrConstants
operator|.
name|MIX_REFERENCEABLE
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchNodeTypeException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
name|node
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
specifier|public
name|void
name|testRemoveInheritedMixin
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
literal|"testRemoveInheritedMixin"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|node
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
try|try
block|{
name|node
operator|.
name|removeMixin
argument_list|(
name|JcrConstants
operator|.
name|MIX_REFERENCEABLE
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchNodeTypeException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
name|node
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
specifier|public
name|void
name|testRemoveInheritedMixin2
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Authorizable
name|user
init|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|superuser
operator|)
operator|.
name|getUserManager
argument_list|()
operator|.
name|getAuthorizable
argument_list|(
literal|"admin"
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotExecutableException
argument_list|()
throw|;
block|}
name|Node
name|node
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|node
operator|.
name|isNodeType
argument_list|(
name|JcrConstants
operator|.
name|MIX_REFERENCEABLE
argument_list|)
argument_list|)
expr_stmt|;
name|node
operator|.
name|removeMixin
argument_list|(
name|JcrConstants
operator|.
name|MIX_REFERENCEABLE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchNodeTypeException
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
name|testRemoveMixVersionable
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
name|removeMixin
argument_list|(
name|mixVersionable
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
name|testRemoveMixVersionable1
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
argument_list|)
decl_stmt|;
name|node
operator|.
name|addMixin
argument_list|(
name|mixReferenceable
argument_list|)
expr_stmt|;
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
name|removeMixin
argument_list|(
name|mixVersionable
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
name|testRemoveAddMixVersionable
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
name|String
name|vhId
init|=
name|node
operator|.
name|getVersionHistory
argument_list|()
operator|.
name|getUUID
argument_list|()
decl_stmt|;
name|node
operator|.
name|removeMixin
argument_list|(
name|mixVersionable
argument_list|)
expr_stmt|;
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
name|assertFalse
argument_list|(
name|vhId
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getVersionHistory
argument_list|()
operator|.
name|getUUID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRemoveAddMixVersionable1
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
argument_list|)
decl_stmt|;
name|node
operator|.
name|addMixin
argument_list|(
name|mixReferenceable
argument_list|)
expr_stmt|;
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
name|String
name|vhId
init|=
name|node
operator|.
name|getVersionHistory
argument_list|()
operator|.
name|getUUID
argument_list|()
decl_stmt|;
name|node
operator|.
name|removeMixin
argument_list|(
name|mixVersionable
argument_list|)
expr_stmt|;
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
name|assertEquals
argument_list|(
name|vhId
argument_list|,
name|node
operator|.
name|getVersionHistory
argument_list|()
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

