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
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
comment|/**  * Permission evaluation tests for move operations.  */
end_comment

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"OAK-710 : permission validator doesn't detect move"
argument_list|)
specifier|public
class|class
name|MoveTest
extends|extends
name|AbstractEvaluationTest
block|{
specifier|private
name|String
name|nodePath3
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSessionMove
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|destPath
init|=
name|path
operator|+
literal|'/'
operator|+
name|nodeName1
decl_stmt|;
comment|// give 'add_child_nodes' and 'nt-management' privilege
comment|// -> not sufficient privileges for a move
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
try|try
block|{
name|testSession
operator|.
name|move
argument_list|(
name|childNPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Move requires addChildNodes and removeChildNodes privilege."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success.
block|}
comment|// add 'remove_child_nodes' at 'path
comment|// -> not sufficient for a move since 'remove_node' privilege is missing
comment|//    on the move-target
name|allow
argument_list|(
name|path
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_REMOVE_CHILD_NODES
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|testSession
operator|.
name|move
argument_list|(
name|childNPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Move requires addChildNodes and removeChildNodes privilege."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success.
block|}
comment|// allow 'remove_node' at childNPath
comment|// -> now move must succeed
name|allow
argument_list|(
name|childNPath
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_REMOVE_NODE
argument_list|)
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|move
argument_list|(
name|childNPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// withdraw  'add_child_nodes' privilege on former src-parent
comment|// -> moving child-node back must fail
name|deny
argument_list|(
name|path
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
name|move
argument_list|(
name|destPath
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
argument_list|(
literal|"Move requires addChildNodes and removeChildNodes privilege."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success.
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWorkspaceMove
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|destPath
init|=
name|path
operator|+
literal|'/'
operator|+
name|nodeName1
decl_stmt|;
comment|// give 'add_child_nodes', 'nt-mgmt' privilege
comment|// -> not sufficient privileges for a move.
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
try|try
block|{
name|testSession
operator|.
name|getWorkspace
argument_list|()
operator|.
name|move
argument_list|(
name|childNPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Move requires addChildNodes and removeChildNodes privilege."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success.
block|}
comment|// add 'remove_child_nodes' at 'path
comment|// -> no sufficient for a move since 'remove_node' privilege is missing
comment|//    on the move-target
name|allow
argument_list|(
name|path
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_REMOVE_CHILD_NODES
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
name|move
argument_list|(
name|childNPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Move requires addChildNodes and removeChildNodes privilege."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success.
block|}
comment|// allow 'remove_node' at childNPath
comment|// -> now move must succeed
name|allow
argument_list|(
name|childNPath
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_REMOVE_NODE
argument_list|)
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|getWorkspace
argument_list|()
operator|.
name|move
argument_list|(
name|childNPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
comment|// withdraw  'add_child_nodes' privilege on former src-parent
comment|// -> moving child-node back must fail
name|deny
argument_list|(
name|path
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
name|move
argument_list|(
name|destPath
argument_list|,
name|childNPath
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Move requires addChildNodes and removeChildNodes privilege."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success.
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMoveAccessControlledNode
parameter_list|()
throws|throws
name|Exception
block|{
comment|// permissions defined @ childNode
comment|// -> revoke read permission
name|deny
argument_list|(
name|childNPath
argument_list|,
name|readPrivileges
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
name|assertHasPrivileges
argument_list|(
name|childNPath
argument_list|,
name|readPrivileges
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|nodePath3
argument_list|)
argument_list|)
expr_stmt|;
name|assertHasPrivileges
argument_list|(
name|nodePath3
argument_list|,
name|readPrivileges
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// move the ancestor node
name|String
name|movedChildNPath
init|=
name|path
operator|+
literal|"/movedNode"
decl_stmt|;
name|String
name|movedNode3Path
init|=
name|movedChildNPath
operator|+
literal|'/'
operator|+
name|nodeName3
decl_stmt|;
name|superuser
operator|.
name|move
argument_list|(
name|childNPath
argument_list|,
name|movedChildNPath
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// expected behavior:
comment|// the AC-content present on childNode is still enforced both on
comment|// the node itself and on the subtree.
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|movedChildNPath
argument_list|)
argument_list|)
expr_stmt|;
name|assertHasPrivileges
argument_list|(
name|movedChildNPath
argument_list|,
name|readPrivileges
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|movedNode3Path
argument_list|)
argument_list|)
expr_stmt|;
name|assertHasPrivileges
argument_list|(
name|movedNode3Path
argument_list|,
name|readPrivileges
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMoveAccessControlledNodeInSubtree
parameter_list|()
throws|throws
name|Exception
block|{
comment|// permissions defined @ node3Path
comment|// -> revoke read permission
name|deny
argument_list|(
name|nodePath3
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|nodePath3
argument_list|)
argument_list|)
expr_stmt|;
name|assertHasPrivileges
argument_list|(
name|nodePath3
argument_list|,
name|readPrivileges
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// move the ancestor node
name|String
name|movedChildNPath
init|=
name|path
operator|+
literal|"/movedNode"
decl_stmt|;
name|String
name|movedNode3Path
init|=
name|movedChildNPath
operator|+
literal|'/'
operator|+
name|nodeName3
decl_stmt|;
name|superuser
operator|.
name|move
argument_list|(
name|childNPath
argument_list|,
name|movedChildNPath
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// expected behavior:
comment|// the AC-content present on node3 is still enforced
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|movedNode3Path
argument_list|)
argument_list|)
expr_stmt|;
name|assertHasPrivileges
argument_list|(
name|movedNode3Path
argument_list|,
name|readPrivileges
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMoveWithDifferentEffectiveAc
parameter_list|()
throws|throws
name|Exception
block|{
comment|// @path read is denied, @childNode its allowed again
name|deny
argument_list|(
name|path
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|allow
argument_list|(
name|childNPath
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|nodePath3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|nodePath3
argument_list|,
name|readPrivileges
argument_list|)
argument_list|)
expr_stmt|;
comment|// move the ancestor node
name|String
name|movedPath
init|=
name|path
operator|+
literal|"/movedNode"
decl_stmt|;
name|superuser
operator|.
name|move
argument_list|(
name|nodePath3
argument_list|,
name|movedPath
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// expected behavior:
comment|// due to move node3 should not e visible any more
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|movedPath
argument_list|)
argument_list|)
expr_stmt|;
name|assertHasPrivileges
argument_list|(
name|movedPath
argument_list|,
name|readPrivileges
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMoveNodeWithGlobRestriction
parameter_list|()
throws|throws
name|Exception
block|{
comment|// permissions defined @ path
comment|// restriction: remove read priv to nodeName3 node
name|deny
argument_list|(
name|childNPath
argument_list|,
name|readPrivileges
argument_list|,
name|createGlobRestriction
argument_list|(
literal|'/'
operator|+
name|nodeName3
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|nodePath3
argument_list|)
argument_list|)
expr_stmt|;
name|assertHasPrivileges
argument_list|(
name|nodePath3
argument_list|,
name|readPrivileges
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
name|movedChildNPath
init|=
name|path
operator|+
literal|"/movedNode"
decl_stmt|;
name|String
name|movedNode3Path
init|=
name|movedChildNPath
operator|+
literal|'/'
operator|+
name|nodeName3
decl_stmt|;
name|superuser
operator|.
name|move
argument_list|(
name|childNPath
argument_list|,
name|movedChildNPath
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|movedNode3Path
argument_list|)
argument_list|)
expr_stmt|;
name|assertHasPrivileges
argument_list|(
name|movedNode3Path
argument_list|,
name|readPrivileges
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMoveNodeWithGlobRestriction2
parameter_list|()
throws|throws
name|Exception
block|{
comment|// permissions defined @ path
comment|// restriction: remove read priv to nodeName3 node
name|deny
argument_list|(
name|childNPath
argument_list|,
name|readPrivileges
argument_list|,
name|createGlobRestriction
argument_list|(
literal|'/'
operator|+
name|nodeName3
argument_list|)
argument_list|)
expr_stmt|;
name|assertHasPrivileges
argument_list|(
name|nodePath3
argument_list|,
name|readPrivileges
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
name|movedChildNPath
init|=
name|path
operator|+
literal|"/movedNode"
decl_stmt|;
name|String
name|movedNode3Path
init|=
name|movedChildNPath
operator|+
literal|'/'
operator|+
name|nodeName3
decl_stmt|;
name|superuser
operator|.
name|move
argument_list|(
name|childNPath
argument_list|,
name|movedChildNPath
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|movedNode3Path
argument_list|)
argument_list|)
expr_stmt|;
name|assertHasPrivileges
argument_list|(
name|movedNode3Path
argument_list|,
name|readPrivileges
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

