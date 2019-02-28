begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|plugins
operator|.
name|version
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
name|spi
operator|.
name|commit
operator|.
name|DefaultEditor
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
name|Editor
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|JCR_PRIMARYTYPE
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
name|JCR_VERSIONLABELS
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
name|commons
operator|.
name|PathUtils
operator|.
name|concat
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
name|commons
operator|.
name|PathUtils
operator|.
name|getDepth
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
name|commons
operator|.
name|PathUtils
operator|.
name|relativize
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
name|spi
operator|.
name|state
operator|.
name|AbstractNodeState
operator|.
name|comparePropertiesAgainstBaseState
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
name|spi
operator|.
name|version
operator|.
name|VersionConstants
operator|.
name|VERSION_NODE_TYPE_NAMES
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
name|spi
operator|.
name|version
operator|.
name|VersionConstants
operator|.
name|VERSION_STORE_INIT
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
name|spi
operator|.
name|version
operator|.
name|VersionConstants
operator|.
name|VERSION_STORE_NT_NAMES
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
name|spi
operator|.
name|version
operator|.
name|VersionConstants
operator|.
name|VERSION_STORE_PATH
import|;
end_import

begin_comment
comment|/**  * Validates changes on the version store.  */
end_comment

begin_class
class|class
name|VersionStorageEditor
extends|extends
name|DefaultEditor
block|{
specifier|private
specifier|static
specifier|final
name|int
name|VERSION_HISTORY_DEPTH
init|=
literal|6
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|versionStorageNode
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|workspaceRoot
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|builder
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
name|boolean
name|initPhase
decl_stmt|;
specifier|private
name|ReadWriteVersionManager
name|vMgr
decl_stmt|;
name|VersionStorageEditor
parameter_list|(
annotation|@
name|NotNull
name|NodeBuilder
name|versionStorageNode
parameter_list|,
annotation|@
name|NotNull
name|NodeBuilder
name|workspaceRoot
parameter_list|)
block|{
name|this
argument_list|(
name|versionStorageNode
argument_list|,
name|workspaceRoot
argument_list|,
name|versionStorageNode
argument_list|,
name|VERSION_STORE_PATH
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|VersionStorageEditor
parameter_list|(
annotation|@
name|NotNull
name|NodeBuilder
name|versionStorageNode
parameter_list|,
annotation|@
name|NotNull
name|NodeBuilder
name|workspaceRoot
parameter_list|,
annotation|@
name|NotNull
name|NodeBuilder
name|builder
parameter_list|,
annotation|@
name|NotNull
name|String
name|path
parameter_list|,
name|boolean
name|initPhase
parameter_list|)
block|{
name|this
operator|.
name|versionStorageNode
operator|=
name|checkNotNull
argument_list|(
name|versionStorageNode
argument_list|)
expr_stmt|;
name|this
operator|.
name|workspaceRoot
operator|=
name|checkNotNull
argument_list|(
name|workspaceRoot
argument_list|)
expr_stmt|;
name|this
operator|.
name|builder
operator|=
name|checkNotNull
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|this
operator|.
name|initPhase
operator|=
name|initPhase
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|enter
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
name|VERSION_STORE_PATH
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|initPhase
operator|=
name|isInitializationPhase
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Editor
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
throws|throws
name|CommitFailedException
block|{
name|int
name|d
init|=
name|getDepth
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
name|p
init|=
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|==
name|VERSION_HISTORY_DEPTH
operator|&&
name|name
operator|.
name|equals
argument_list|(
name|JCR_VERSIONLABELS
argument_list|)
condition|)
block|{
return|return
operator|new
name|VersionLabelsEditor
argument_list|(
name|p
argument_list|,
name|getVersionManager
argument_list|()
argument_list|)
return|;
block|}
if|if
condition|(
name|d
operator|<
name|VERSION_HISTORY_DEPTH
operator|&&
operator|!
name|isVersionStorageNode
argument_list|(
name|after
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|VersionStorageEditor
argument_list|(
name|versionStorageNode
argument_list|,
name|workspaceRoot
argument_list|,
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
argument_list|,
name|p
argument_list|,
name|initPhase
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|int
name|d
init|=
name|getDepth
argument_list|(
name|path
argument_list|)
decl_stmt|;
comment|// allow child nodes under version storage node, unless an attempt
comment|// is made to create rep:versionStorage nodes manually.
if|if
condition|(
name|d
operator|==
name|getDepth
argument_list|(
name|VERSION_STORE_PATH
argument_list|)
operator|&&
operator|!
name|isVersionStorageNode
argument_list|(
name|after
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// allow node addition during initialization phase
if|if
condition|(
name|initPhase
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|throwProtected
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|int
name|d
init|=
name|getDepth
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|==
name|VERSION_HISTORY_DEPTH
condition|)
block|{
comment|// restore version on builder
name|builder
operator|.
name|setChildNode
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
expr_stmt|;
name|String
name|relPath
init|=
name|relativize
argument_list|(
name|VERSION_STORE_PATH
argument_list|,
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
argument_list|)
decl_stmt|;
comment|// let version manager remove it properly
name|getVersionManager
argument_list|()
operator|.
name|removeVersion
argument_list|(
name|relPath
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|isVersionStorageNode
argument_list|(
name|before
argument_list|)
operator|||
name|d
operator|>
name|VERSION_HISTORY_DEPTH
condition|)
block|{
name|throwProtected
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|getDepth
argument_list|(
name|path
argument_list|)
operator|<
name|VERSION_HISTORY_DEPTH
condition|)
block|{
return|return;
block|}
name|throwProtected
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|getDepth
argument_list|(
name|path
argument_list|)
operator|<
name|VERSION_HISTORY_DEPTH
condition|)
block|{
return|return;
block|}
name|throwProtected
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|getDepth
argument_list|(
name|path
argument_list|)
operator|<
name|VERSION_HISTORY_DEPTH
condition|)
block|{
return|return;
block|}
name|throwProtected
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//-------------------------< internal>-------------------------------------
specifier|private
specifier|static
name|boolean
name|isVersionStorageNode
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
name|String
name|ntName
init|=
name|state
operator|.
name|getName
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
decl_stmt|;
return|return
name|VERSION_STORE_NT_NAMES
operator|.
name|contains
argument_list|(
name|ntName
argument_list|)
operator|||
name|VERSION_NODE_TYPE_NAMES
operator|.
name|contains
argument_list|(
name|ntName
argument_list|)
return|;
block|}
specifier|private
name|ReadWriteVersionManager
name|getVersionManager
parameter_list|()
block|{
if|if
condition|(
name|vMgr
operator|==
literal|null
condition|)
block|{
name|vMgr
operator|=
operator|new
name|ReadWriteVersionManager
argument_list|(
name|versionStorageNode
argument_list|,
name|workspaceRoot
argument_list|)
expr_stmt|;
block|}
return|return
name|vMgr
return|;
block|}
specifier|private
name|Editor
name|throwProtected
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
name|Utils
operator|.
name|throwProtected
argument_list|(
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isInitializationPhase
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
operator|!
name|comparePropertiesAgainstBaseState
argument_list|(
name|after
argument_list|,
name|before
argument_list|,
operator|new
name|DefaultNodeStateDiff
argument_list|()
block|{
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
operator|!
name|after
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|VERSION_STORE_INIT
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

