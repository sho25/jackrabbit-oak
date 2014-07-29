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
name|ItemNotFoundException
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
name|PathNotFoundException
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
name|AccessControlEntry
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
name|VersionHistory
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
name|VersionIterator
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
name|JackrabbitAccessControlList
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
name|jackrabbit
operator|.
name|authorization
operator|.
name|AccessControlUtils
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
name|NotExecutableException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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

begin_class
specifier|public
class|class
name|ReadVersionContent
extends|extends
name|AbstractEvaluationTest
block|{
specifier|private
name|Version
name|v
decl_stmt|;
specifier|private
name|Version
name|v2
decl_stmt|;
specifier|private
name|VersionHistory
name|vh
decl_stmt|;
specifier|private
name|String
name|versionablePath
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
name|deny
argument_list|(
name|VersionConstants
operator|.
name|VERSION_STORE_PATH
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|createVersionableNode
argument_list|(
name|superuser
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|versionablePath
operator|=
name|n
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|v
operator|=
name|n
operator|.
name|checkin
argument_list|()
expr_stmt|;
name|vh
operator|=
name|n
operator|.
name|getVersionHistory
argument_list|()
expr_stmt|;
name|n
operator|.
name|checkout
argument_list|()
expr_stmt|;
name|v2
operator|=
name|n
operator|.
name|checkin
argument_list|()
expr_stmt|;
name|n
operator|.
name|checkout
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
name|Override
annotation|@
name|After
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|JackrabbitAccessControlList
name|acl
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|superuser
argument_list|,
name|VersionConstants
operator|.
name|VERSION_STORE_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|acl
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|AccessControlEntry
name|entry
range|:
name|acl
operator|.
name|getAccessControlEntries
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getPrincipal
argument_list|()
operator|.
name|equals
argument_list|(
name|testUser
operator|.
name|getPrincipal
argument_list|()
argument_list|)
condition|)
block|{
name|acl
operator|.
name|removeAccessControlEntry
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|VersionConstants
operator|.
name|VERSION_STORE_PATH
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|Node
name|createVersionableNode
parameter_list|(
name|Node
name|parent
parameter_list|)
throws|throws
name|Exception
block|{
name|Node
name|n
init|=
operator|(
name|parent
operator|.
name|hasNode
argument_list|(
name|nodeName1
argument_list|)
operator|)
condition|?
name|parent
operator|.
name|getNode
argument_list|(
name|nodeName1
argument_list|)
else|:
name|parent
operator|.
name|addNode
argument_list|(
name|nodeName1
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|.
name|canAddMixin
argument_list|(
name|mixVersionable
argument_list|)
condition|)
block|{
name|n
operator|.
name|addMixin
argument_list|(
name|mixVersionable
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|NotExecutableException
argument_list|()
throw|;
block|}
name|n
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
return|return
name|n
return|;
block|}
comment|/**      * @since oak      */
annotation|@
name|Test
specifier|public
name|void
name|testHasVersionContentNodes
parameter_list|()
throws|throws
name|Exception
block|{
comment|// version information must still be accessible
name|assertTrue
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|v
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|v2
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|vh
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @since oak      */
annotation|@
name|Test
specifier|public
name|void
name|testGetBaseVersion
parameter_list|()
throws|throws
name|Exception
block|{
comment|// version information must still be accessible
name|Version
name|base
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|versionablePath
argument_list|)
operator|.
name|getBaseVersion
argument_list|()
decl_stmt|;
name|Version
name|base2
init|=
name|testSession
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getVersionManager
argument_list|()
operator|.
name|getBaseVersion
argument_list|(
name|versionablePath
argument_list|)
decl_stmt|;
block|}
comment|/**      * @since oak      */
annotation|@
name|Test
specifier|public
name|void
name|testGetVersionHistory
parameter_list|()
throws|throws
name|Exception
block|{
comment|// accessing the version history must be allowed if the versionable node
comment|// is readable to the editing test session.
name|Node
name|testNode
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|versionablePath
argument_list|)
decl_stmt|;
name|VersionHistory
name|vh
init|=
name|testNode
operator|.
name|getVersionHistory
argument_list|()
decl_stmt|;
name|VersionHistory
name|vh2
init|=
name|testSession
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getVersionManager
argument_list|()
operator|.
name|getVersionHistory
argument_list|(
name|versionablePath
argument_list|)
decl_stmt|;
block|}
comment|/**      * @since oak      */
annotation|@
name|Test
specifier|public
name|void
name|testGetVersionHistoryNode
parameter_list|()
throws|throws
name|Exception
block|{
comment|// accessing the version history must be allowed if the versionable node
comment|// is readable to the editing test session.
name|Node
name|testNode
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|versionablePath
argument_list|)
decl_stmt|;
name|String
name|vhPath
init|=
name|vh
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|vhUUID
init|=
name|vh
operator|.
name|getIdentifier
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|vh
operator|.
name|isSame
argument_list|(
name|testNode
operator|.
name|getSession
argument_list|()
operator|.
name|getNode
argument_list|(
name|vhPath
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vh
operator|.
name|isSame
argument_list|(
name|testNode
operator|.
name|getSession
argument_list|()
operator|.
name|getNodeByIdentifier
argument_list|(
name|vhUUID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vh
operator|.
name|isSame
argument_list|(
name|testNode
operator|.
name|getSession
argument_list|()
operator|.
name|getNodeByUUID
argument_list|(
name|vhUUID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @since oak      */
annotation|@
name|Test
specifier|public
name|void
name|testVersionHistoryGetUUID
parameter_list|()
throws|throws
name|Exception
block|{
name|VersionHistory
name|testVh
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|versionablePath
argument_list|)
operator|.
name|getVersionHistory
argument_list|()
decl_stmt|;
name|testVh
operator|.
name|getUUID
argument_list|()
expr_stmt|;
block|}
comment|/**      * @since oak      */
annotation|@
name|Test
specifier|public
name|void
name|testVersionHistoryGetIdentifier
parameter_list|()
throws|throws
name|Exception
block|{
name|VersionHistory
name|testVh
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|versionablePath
argument_list|)
operator|.
name|getVersionHistory
argument_list|()
decl_stmt|;
name|testVh
operator|.
name|getIdentifier
argument_list|()
expr_stmt|;
block|}
comment|/**      * @since oak      */
annotation|@
name|Test
specifier|public
name|void
name|testVersionHistoryGetVersionableIdentifier
parameter_list|()
throws|throws
name|Exception
block|{
name|VersionHistory
name|testVh
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|versionablePath
argument_list|)
operator|.
name|getVersionHistory
argument_list|()
decl_stmt|;
name|testVh
operator|.
name|getVersionableIdentifier
argument_list|()
expr_stmt|;
block|}
comment|/**      * @since oak      */
annotation|@
name|Test
specifier|public
name|void
name|testVersionHistoryGetVersionableUUID
parameter_list|()
throws|throws
name|Exception
block|{
name|VersionHistory
name|testVh
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|versionablePath
argument_list|)
operator|.
name|getVersionHistory
argument_list|()
decl_stmt|;
name|testVh
operator|.
name|getVersionableUUID
argument_list|()
expr_stmt|;
block|}
comment|/**      * @since oak      */
annotation|@
name|Test
specifier|public
name|void
name|testVersionHistoryIsSame
parameter_list|()
throws|throws
name|Exception
block|{
comment|// accessing the version history must be allowed if the versionable node
comment|// is readable to the editing test session.
name|Node
name|testNode
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|versionablePath
argument_list|)
decl_stmt|;
name|VersionHistory
name|testVh
init|=
name|testNode
operator|.
name|getVersionHistory
argument_list|()
decl_stmt|;
name|Node
name|vh2
init|=
name|testNode
operator|.
name|getSession
argument_list|()
operator|.
name|getNode
argument_list|(
name|testVh
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|testVh
operator|.
name|isSame
argument_list|(
name|vh2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @since oak      */
annotation|@
name|Test
specifier|public
name|void
name|testGetAllVersions
parameter_list|()
throws|throws
name|Exception
block|{
comment|// accessing the version history must be allowed if the versionable node
comment|// is readable to the editing test session.
name|Node
name|testNode
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|versionablePath
argument_list|)
decl_stmt|;
name|VersionHistory
name|vh
init|=
name|testNode
operator|.
name|getVersionHistory
argument_list|()
decl_stmt|;
name|VersionIterator
name|versionIterator
init|=
name|vh
operator|.
name|getAllVersions
argument_list|()
decl_stmt|;
comment|// TODO
block|}
comment|/**      * @since oak      */
annotation|@
name|Test
specifier|public
name|void
name|testGetAllLinearVersions
parameter_list|()
throws|throws
name|Exception
block|{
comment|// accessing the version history must be allowed if the versionable node
comment|// is readable to the editing test session.
name|Node
name|testNode
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|versionablePath
argument_list|)
decl_stmt|;
name|VersionHistory
name|vh
init|=
name|testNode
operator|.
name|getVersionHistory
argument_list|()
decl_stmt|;
name|VersionIterator
name|versionIterator
init|=
name|vh
operator|.
name|getAllLinearVersions
argument_list|()
decl_stmt|;
comment|// TODO
block|}
comment|/**      * @since oak      */
annotation|@
name|Test
specifier|public
name|void
name|testAccessVersionHistoryVersionableNodeNotAccessible
parameter_list|()
throws|throws
name|Exception
block|{
comment|// revert read permission on the versionable node
name|modify
argument_list|(
name|versionablePath
argument_list|,
name|Privilege
operator|.
name|JCR_READ
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// versionable node is not readable any more for test session.
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|versionablePath
argument_list|)
argument_list|)
expr_stmt|;
comment|// access version history directly => should fail
try|try
block|{
name|VersionHistory
name|history
init|=
operator|(
name|VersionHistory
operator|)
name|testSession
operator|.
name|getNode
argument_list|(
name|vh
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Access to version history should be denied if versionable node is not accessible"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
comment|// success
block|}
try|try
block|{
name|VersionHistory
name|history
init|=
operator|(
name|VersionHistory
operator|)
name|testSession
operator|.
name|getNodeByIdentifier
argument_list|(
name|vh
operator|.
name|getIdentifier
argument_list|()
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Access to version history should be denied if versionable node is not accessible"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ItemNotFoundException
name|e
parameter_list|)
block|{
comment|// success
block|}
try|try
block|{
name|VersionHistory
name|history
init|=
operator|(
name|VersionHistory
operator|)
name|testSession
operator|.
name|getNodeByUUID
argument_list|(
name|vh
operator|.
name|getUUID
argument_list|()
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Access to version history should be denied if versionable node is not accessible"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ItemNotFoundException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
comment|/**      * @since oak      */
annotation|@
name|Test
specifier|public
name|void
name|testAccessVersionHistoryVersionableNodeRemoved
parameter_list|()
throws|throws
name|Exception
block|{
name|superuser
operator|.
name|getNode
argument_list|(
name|versionablePath
argument_list|)
operator|.
name|remove
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
name|assertTrue
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|versionablePath
argument_list|)
argument_list|)
expr_stmt|;
comment|// accessing the version history directly should still succeed as
comment|// read permission is still granted on the tree defined by the parent.
name|VersionHistory
name|history
init|=
operator|(
name|VersionHistory
operator|)
name|testSession
operator|.
name|getNode
argument_list|(
name|vh
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|history
operator|=
operator|(
name|VersionHistory
operator|)
name|testSession
operator|.
name|getNodeByIdentifier
argument_list|(
name|vh
operator|.
name|getIdentifier
argument_list|()
argument_list|)
expr_stmt|;
name|history
operator|=
operator|(
name|VersionHistory
operator|)
name|testSession
operator|.
name|getNodeByUUID
argument_list|(
name|vh
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
comment|// revoking read permission on the parent node -> version history
comment|// must no longer be accessible
name|modify
argument_list|(
name|path
argument_list|,
name|Privilege
operator|.
name|JCR_READ
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
name|vh
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

