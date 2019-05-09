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
name|security
operator|.
name|authorization
operator|.
name|permission
package|;
end_package

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
name|ImmutableSet
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
name|oak
operator|.
name|api
operator|.
name|CommitFailedException
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
name|api
operator|.
name|PropertyState
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
name|api
operator|.
name|Tree
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
name|api
operator|.
name|Type
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
name|commons
operator|.
name|PathUtils
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
name|namepath
operator|.
name|NamePathMapper
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
name|nodetype
operator|.
name|ReadOnlyNodeTypeManager
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
name|ReadWriteVersionManager
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
name|security
operator|.
name|authorization
operator|.
name|ProviderCtx
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
name|commit
operator|.
name|CommitHook
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
name|commit
operator|.
name|CommitInfo
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
name|state
operator|.
name|DefaultNodeStateDiff
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
name|state
operator|.
name|NodeBuilder
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
name|state
operator|.
name|NodeState
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
name|state
operator|.
name|NodeStateUtils
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
import|;
end_import

begin_comment
comment|/**  * Commit hook which is responsible for storing the path of the versionable  * node with every version history. This includes creating the path property  * for every workspace the version history is represented and updating the  * path upon moving around a versionable node.  */
end_comment

begin_class
specifier|public
class|class
name|VersionablePathHook
implements|implements
name|CommitHook
block|{
specifier|private
specifier|final
name|String
name|workspaceName
decl_stmt|;
specifier|private
specifier|final
name|ProviderCtx
name|providerCtx
decl_stmt|;
specifier|public
name|VersionablePathHook
parameter_list|(
annotation|@
name|NotNull
name|String
name|workspaceName
parameter_list|,
annotation|@
name|NotNull
name|ProviderCtx
name|providerCtx
parameter_list|)
block|{
name|this
operator|.
name|workspaceName
operator|=
name|workspaceName
expr_stmt|;
name|this
operator|.
name|providerCtx
operator|=
name|providerCtx
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|NodeState
name|processCommit
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|rootBuilder
init|=
name|after
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|vsRoot
init|=
name|rootBuilder
operator|.
name|child
argument_list|(
name|NodeTypeConstants
operator|.
name|JCR_SYSTEM
argument_list|)
operator|.
name|child
argument_list|(
name|NodeTypeConstants
operator|.
name|JCR_VERSIONSTORAGE
argument_list|)
decl_stmt|;
name|ReadWriteVersionManager
name|vMgr
init|=
operator|new
name|ReadWriteVersionManager
argument_list|(
name|vsRoot
argument_list|,
name|rootBuilder
argument_list|)
decl_stmt|;
name|ReadOnlyNodeTypeManager
name|ntMgr
init|=
name|ReadOnlyNodeTypeManager
operator|.
name|getInstance
argument_list|(
name|providerCtx
operator|.
name|getRootProvider
argument_list|()
operator|.
name|createReadOnlyRoot
argument_list|(
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
argument_list|)
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|Diff
argument_list|(
name|vMgr
argument_list|,
name|ntMgr
argument_list|,
operator|new
name|Node
argument_list|(
name|rootBuilder
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"VersionablePathHook : workspaceName = "
operator|+
name|workspaceName
return|;
block|}
specifier|private
specifier|final
class|class
name|Diff
extends|extends
name|DefaultNodeStateDiff
implements|implements
name|VersionConstants
block|{
specifier|private
specifier|final
name|ReadWriteVersionManager
name|versionManager
decl_stmt|;
specifier|private
specifier|final
name|ReadOnlyNodeTypeManager
name|ntMgr
decl_stmt|;
specifier|private
specifier|final
name|Node
name|nodeAfter
decl_stmt|;
specifier|private
name|Diff
parameter_list|(
annotation|@
name|NotNull
name|ReadWriteVersionManager
name|versionManager
parameter_list|,
annotation|@
name|NotNull
name|ReadOnlyNodeTypeManager
name|ntMgr
parameter_list|,
annotation|@
name|NotNull
name|Node
name|node
parameter_list|)
block|{
name|this
operator|.
name|versionManager
operator|=
name|versionManager
expr_stmt|;
name|this
operator|.
name|ntMgr
operator|=
name|ntMgr
expr_stmt|;
name|this
operator|.
name|nodeAfter
operator|=
name|node
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
return|return
name|setVersionablePath
argument_list|(
name|after
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
return|return
name|setVersionablePath
argument_list|(
name|after
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|EMPTY_NODE
argument_list|,
name|after
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// do not traverse into hidden trees
return|return
literal|true
return|;
block|}
name|Node
name|node
init|=
operator|new
name|Node
argument_list|(
name|nodeAfter
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|Diff
argument_list|(
name|versionManager
argument_list|,
name|ntMgr
argument_list|,
name|node
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|setVersionablePath
parameter_list|(
annotation|@
name|NotNull
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
name|JcrConstants
operator|.
name|JCR_VERSIONHISTORY
operator|.
name|equals
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
name|nodeAfter
operator|.
name|isVersionable
argument_list|(
name|ntMgr
argument_list|)
condition|)
block|{
name|NodeBuilder
name|vhBuilder
init|=
name|versionManager
operator|.
name|getOrCreateVersionHistory
argument_list|(
name|nodeAfter
operator|.
name|builder
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|vhBuilder
operator|.
name|hasProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|)
condition|)
block|{
name|vhBuilder
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|MIX_REP_VERSIONABLE_PATHS
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
expr_stmt|;
block|}
name|String
name|versionablePath
init|=
name|nodeAfter
operator|.
name|path
decl_stmt|;
name|vhBuilder
operator|.
name|setProperty
argument_list|(
name|workspaceName
argument_list|,
name|versionablePath
argument_list|,
name|Type
operator|.
name|PATH
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
specifier|private
specifier|final
class|class
name|Node
block|{
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|builder
decl_stmt|;
specifier|private
name|Node
parameter_list|(
annotation|@
name|NotNull
name|NodeBuilder
name|rootBuilder
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
literal|"/"
expr_stmt|;
name|this
operator|.
name|builder
operator|=
name|rootBuilder
expr_stmt|;
block|}
specifier|private
name|Node
parameter_list|(
annotation|@
name|NotNull
name|Node
name|parent
parameter_list|,
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
name|parent
operator|.
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|parent
operator|.
name|path
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|isVersionable
parameter_list|(
annotation|@
name|NotNull
name|ReadOnlyNodeTypeManager
name|ntMgr
parameter_list|)
block|{
comment|// this is not 100% correct, because t.getPath() will
comment|// not return the correct path for node after, but is
comment|// sufficient to check if it is versionable
name|Tree
name|tree
init|=
name|providerCtx
operator|.
name|getTreeProvider
argument_list|()
operator|.
name|createReadOnlyTree
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ntMgr
operator|.
name|isNodeType
argument_list|(
name|tree
argument_list|,
name|VersionConstants
operator|.
name|MIX_VERSIONABLE
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

