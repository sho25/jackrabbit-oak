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
comment|/**  * Permission evaluation tests for multiple move operations.  */
end_comment

begin_class
specifier|public
class|class
name|MultipleMoveTest
extends|extends
name|AbstractEvaluationTest
block|{
specifier|protected
name|String
name|nodePath3
decl_stmt|;
specifier|protected
name|String
name|siblingDestPath
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
name|node3
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|childNPath
argument_list|)
operator|.
name|addNode
argument_list|(
name|nodeName3
argument_list|)
decl_stmt|;
name|node3
operator|.
name|setProperty
argument_list|(
literal|"movedProp"
argument_list|,
literal|"val"
argument_list|)
expr_stmt|;
name|nodePath3
operator|=
name|node3
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
name|siblingDestPath
operator|=
name|siblingPath
operator|+
literal|"/destination"
expr_stmt|;
block|}
specifier|private
name|void
name|move
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|dest
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|move
argument_list|(
name|source
argument_list|,
name|dest
argument_list|,
name|testSession
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|move
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|dest
parameter_list|,
name|Session
name|session
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|session
operator|.
name|move
argument_list|(
name|source
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|setupMovePermissions
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|dest
parameter_list|)
throws|throws
name|Exception
block|{
name|allow
argument_list|(
name|source
argument_list|,
name|privilegesFromNames
argument_list|(
operator|new
name|String
index|[]
block|{
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
name|allow
argument_list|(
name|dest
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMoveSubTreeBack
parameter_list|()
throws|throws
name|Exception
block|{
name|setupMovePermissions
argument_list|(
name|path
argument_list|,
name|siblingPath
argument_list|)
expr_stmt|;
try|try
block|{
comment|// first move must succeed
name|move
argument_list|(
name|childNPath
argument_list|,
name|siblingDestPath
argument_list|)
expr_stmt|;
comment|// moving child back must fail due to missing privileges
name|move
argument_list|(
name|siblingDestPath
operator|+
literal|'/'
operator|+
name|nodeName3
argument_list|,
name|path
operator|+
literal|"/subtreeBack"
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
comment|// FIXME OAK-2225 AbstractEvaluationTest doesn't restore ACEs properly causing later tests to fail
comment|//    @Test
comment|//    public void testMoveSubTreeBack2() throws Exception {
comment|//        allow(testRootNode.getPath(), privilegesFromNames(new String[]{
comment|//                Privilege.JCR_REMOVE_NODE,
comment|//                Privilege.JCR_REMOVE_CHILD_NODES,
comment|//                Privilege.JCR_ADD_CHILD_NODES,
comment|//                Privilege.JCR_NODE_TYPE_MANAGEMENT
comment|//        }));
comment|//
comment|//        first move must succeed
comment|//        move(childNPath, siblingDestPath);
comment|//        moving child back must fail due to missing privileges
comment|//        move(siblingDestPath + '/' + nodeName3, path + "/subtreeBack");
comment|//        testSession.save();
comment|//    }
annotation|@
name|Test
specifier|public
name|void
name|testMoveSubTreeBack3
parameter_list|()
throws|throws
name|Exception
block|{
name|setupMovePermissions
argument_list|(
name|path
argument_list|,
name|siblingPath
argument_list|)
expr_stmt|;
try|try
block|{
comment|// first move must succeed
name|move
argument_list|(
name|childNPath
argument_list|,
name|siblingDestPath
argument_list|)
expr_stmt|;
comment|// moving child back must fail due to missing privileges
name|move
argument_list|(
name|siblingDestPath
operator|+
literal|'/'
operator|+
name|nodeName3
argument_list|,
name|childNPath
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
comment|// FIXME OAK-2225 AbstractEvaluationTest doesn't restore ACEs properly causing later tests to fail
comment|//    @Ignore("Known Limitation of OAK-710")
comment|//    @Test
comment|//    public void testMoveSubTreeBack4() throws Exception {
comment|//        allow(testRootNode.getPath(), privilegesFromNames(new String[]{
comment|//                Privilege.JCR_REMOVE_NODE,
comment|//                Privilege.JCR_REMOVE_CHILD_NODES,
comment|//                Privilege.JCR_ADD_CHILD_NODES,
comment|//                Privilege.JCR_NODE_TYPE_MANAGEMENT
comment|//        }));
comment|//
comment|//        first move must succeed
comment|//        move(childNPath, siblingDestPath);
comment|//        moving child back must fail due to missing privileges
comment|//        move(siblingDestPath + '/' + nodeName3, childNPath);
comment|//        testSession.save();
comment|//    }
annotation|@
name|Test
specifier|public
name|void
name|testMoveDestParent
parameter_list|()
throws|throws
name|Exception
block|{
name|setupMovePermissions
argument_list|(
name|path
argument_list|,
name|siblingPath
argument_list|)
expr_stmt|;
try|try
block|{
comment|// first move must succeed
name|move
argument_list|(
name|childNPath
argument_list|,
name|siblingDestPath
argument_list|)
expr_stmt|;
comment|// moving dest parent must fail due to missing privileges
name|move
argument_list|(
name|siblingPath
argument_list|,
name|path
operator|+
literal|"/parentMove"
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
comment|// FIXME OAK-2225 AbstractEvaluationTest doesn't restore ACEs properly causing later tests to fail
comment|//    @Ignore("Known Limitation of OAK-710")
comment|//    @Test
comment|//    public void testMoveDestParent2() throws Exception {
comment|//        allow(testRootNode.getPath(), privilegesFromNames(new String[]{
comment|//                Privilege.JCR_REMOVE_NODE,
comment|//                Privilege.JCR_REMOVE_CHILD_NODES,
comment|//                Privilege.JCR_ADD_CHILD_NODES,
comment|//                Privilege.JCR_NODE_TYPE_MANAGEMENT
comment|//        }));
comment|//
comment|//        first move must succeed
comment|//        move(childNPath, siblingDestPath);
comment|//        moving dest parent to original source location
comment|//        move(siblingPath, path + "/parentMove");
comment|//        testSession.save();
comment|//    }
annotation|@
name|Test
specifier|public
name|void
name|testMoveDestParent3
parameter_list|()
throws|throws
name|Exception
block|{
name|setupMovePermissions
argument_list|(
name|path
argument_list|,
name|siblingPath
argument_list|)
expr_stmt|;
try|try
block|{
comment|// first move must succeed
name|move
argument_list|(
name|childNPath
argument_list|,
name|siblingDestPath
argument_list|)
expr_stmt|;
comment|// moving dest parent to location of originally moved node must fail due to missing privileges
name|move
argument_list|(
name|siblingPath
argument_list|,
name|childNPath
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
comment|// FIXME OAK-2225 AbstractEvaluationTest doesn't restore ACEs properly causing later tests to fail
comment|//    @Ignore("Known Limitation of OAK-710")
comment|//    @Test
comment|//    public void testMoveDestParent4() throws Exception {
comment|//        allow(testRootNode.getPath(), privilegesFromNames(new String[]{
comment|//                Privilege.JCR_REMOVE_NODE,
comment|//                Privilege.JCR_REMOVE_CHILD_NODES,
comment|//                Privilege.JCR_ADD_CHILD_NODES,
comment|//                Privilege.JCR_NODE_TYPE_MANAGEMENT
comment|//        }));
comment|//
comment|//        first move must succeed
comment|//        move(childNPath, siblingDestPath);
comment|//        moving dest parent to original source location
comment|//        move(siblingPath, childNPath);
comment|//        testSession.save();
comment|//    }
block|}
end_class

end_unit

