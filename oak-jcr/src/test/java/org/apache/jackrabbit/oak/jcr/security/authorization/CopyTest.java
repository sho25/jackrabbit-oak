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
name|authorization
operator|.
name|accesscontrol
operator|.
name|AccessControlConstants
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
name|util
operator|.
name|Text
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
comment|/**  * Testing Workspace#copy with limited permissions both on source and target  * location.  */
end_comment

begin_class
specifier|public
class|class
name|CopyTest
extends|extends
name|AbstractEvaluationTest
block|{
specifier|private
name|String
name|targetPath
decl_stmt|;
specifier|private
name|String
name|destPath
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
name|target
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"target"
argument_list|)
decl_stmt|;
name|targetPath
operator|=
name|target
operator|.
name|getPath
argument_list|()
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
name|destPath
operator|=
name|targetPath
operator|+
literal|"/copy"
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCopyNoWritePermissionAtTarget
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|testSession
operator|.
name|getWorkspace
argument_list|()
operator|.
name|copy
argument_list|(
name|path
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"no write permission at copy target"
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
name|testCopyWithInsufficientPermissions
parameter_list|()
throws|throws
name|Exception
block|{
name|allow
argument_list|(
name|targetPath
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_ADD_CHILD_NODES
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|testSession
operator|.
name|getWorkspace
argument_list|()
operator|.
name|copy
argument_list|(
name|path
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"insufficient write permission at copy target"
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
name|testCopyWithFullPermissions
parameter_list|()
throws|throws
name|Exception
block|{
name|allow
argument_list|(
name|targetPath
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_ALL
argument_list|)
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|getWorkspace
argument_list|()
operator|.
name|copy
argument_list|(
name|path
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|destPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCopyInvisibleSubTree
parameter_list|()
throws|throws
name|Exception
block|{
name|deny
argument_list|(
name|childNPath
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
name|allow
argument_list|(
name|targetPath
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_ALL
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|childNPath
argument_list|)
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|getWorkspace
argument_list|()
operator|.
name|copy
argument_list|(
name|path
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|Node
name|copiedNode
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
name|String
name|childName
init|=
name|Text
operator|.
name|getName
argument_list|(
name|childNPath
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|copiedNode
operator|.
name|hasNode
argument_list|(
name|childName
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|copiedNode
operator|.
name|hasNode
argument_list|(
name|Text
operator|.
name|getName
argument_list|(
name|childNPath2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|superuser
operator|.
name|nodeExists
argument_list|(
name|destPath
operator|+
literal|'/'
operator|+
name|childName
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCopyInvisibleProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|deny
argument_list|(
name|childNPath
argument_list|,
name|privilegesFromName
argument_list|(
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
argument_list|)
expr_stmt|;
name|allow
argument_list|(
name|targetPath
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_ALL
argument_list|)
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|getWorkspace
argument_list|()
operator|.
name|copy
argument_list|(
name|path
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|Node
name|copiedNode
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
name|String
name|childName
init|=
name|Text
operator|.
name|getName
argument_list|(
name|childNPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|copiedNode
operator|.
name|hasNode
argument_list|(
name|childName
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|copiedNode
operator|.
name|hasProperty
argument_list|(
name|childName
operator|+
literal|'/'
operator|+
name|propertyName1
argument_list|)
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|superuser
operator|.
name|nodeExists
argument_list|(
name|destPath
operator|+
literal|'/'
operator|+
name|childName
operator|+
literal|'/'
operator|+
name|propertyName1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCopyInvisibleAcContent
parameter_list|()
throws|throws
name|Exception
block|{
name|deny
argument_list|(
name|childNPath
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_READ_ACCESS_CONTROL
argument_list|)
argument_list|)
expr_stmt|;
name|allow
argument_list|(
name|targetPath
argument_list|,
name|privilegesFromName
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|getWorkspace
argument_list|()
operator|.
name|copy
argument_list|(
name|path
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|Node
name|copiedNode
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
name|String
name|childName
init|=
name|Text
operator|.
name|getName
argument_list|(
name|childNPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|copiedNode
operator|.
name|hasNode
argument_list|(
name|childName
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|child
init|=
name|copiedNode
operator|.
name|getNode
argument_list|(
name|childName
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|child
operator|.
name|hasNode
argument_list|(
name|AccessControlConstants
operator|.
name|REP_POLICY
argument_list|)
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|superuser
operator|.
name|nodeExists
argument_list|(
name|targetPath
operator|+
literal|'/'
operator|+
name|childName
operator|+
literal|"/rep:policy"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

