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
name|index
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

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
name|IOException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionParser
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
name|plugins
operator|.
name|index
operator|.
name|ContextAwareCallback
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
name|index
operator|.
name|IndexEditorProvider
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
name|index
operator|.
name|IndexUpdateCallback
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
name|index
operator|.
name|IndexingContext
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
name|index
operator|.
name|property
operator|.
name|PropertyIndexEditorProvider
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
name|run
operator|.
name|cli
operator|.
name|NodeStoreFixture
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
name|run
operator|.
name|cli
operator|.
name|NodeStoreFixtureProvider
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
name|run
operator|.
name|cli
operator|.
name|Options
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
name|commit
operator|.
name|EmptyHook
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
name|mount
operator|.
name|MountInfoProvider
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
name|mount
operator|.
name|Mounts
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

begin_comment
comment|/**  * Editor implementation which stores the property index NodeState data in a different  * SegmentNodeStore used solely for property index storage purpose  */
end_comment

begin_class
specifier|public
class|class
name|SegmentPropertyIndexEditorProvider
implements|implements
name|IndexEditorProvider
implements|,
name|Closeable
block|{
specifier|private
name|MountInfoProvider
name|mountInfoProvider
init|=
name|Mounts
operator|.
name|defaultMountInfoProvider
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|File
name|indexStoreDir
decl_stmt|;
specifier|private
name|NodeBuilder
name|rootBuilder
decl_stmt|;
specifier|private
name|NodeStoreFixture
name|fixture
decl_stmt|;
specifier|public
name|SegmentPropertyIndexEditorProvider
parameter_list|(
name|File
name|storeDir
parameter_list|)
block|{
name|this
operator|.
name|indexStoreDir
operator|=
name|storeDir
expr_stmt|;
block|}
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|Editor
name|getIndexEditor
parameter_list|(
annotation|@
name|Nonnull
name|String
name|type
parameter_list|,
annotation|@
name|Nonnull
name|NodeBuilder
name|definition
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|root
parameter_list|,
annotation|@
name|Nonnull
name|IndexUpdateCallback
name|callback
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
operator|!
name|PropertyIndexEditorProvider
operator|.
name|TYPE
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|IndexingContext
name|idxCtx
init|=
operator|(
operator|(
name|ContextAwareCallback
operator|)
name|callback
operator|)
operator|.
name|getIndexingContext
argument_list|()
decl_stmt|;
name|String
name|indexPath
init|=
name|idxCtx
operator|.
name|getIndexPath
argument_list|()
decl_stmt|;
name|PropertyIndexEditorProvider
name|pie
init|=
operator|new
name|PropertyIndexEditorProvider
argument_list|()
decl_stmt|;
name|pie
operator|.
name|with
argument_list|(
name|mountInfoProvider
argument_list|)
expr_stmt|;
name|NodeBuilder
name|idxb
init|=
name|definition
decl_stmt|;
if|if
condition|(
name|idxCtx
operator|.
name|isReindexing
argument_list|()
condition|)
block|{
comment|//In case of reindex use the NodeBuilder from SegmentNodeStore instead of default one
name|idxb
operator|=
name|createNewBuilder
argument_list|(
name|indexPath
argument_list|,
name|definition
argument_list|)
expr_stmt|;
block|}
return|return
name|pie
operator|.
name|getIndexEditor
argument_list|(
name|type
argument_list|,
name|idxb
argument_list|,
name|root
argument_list|,
name|callback
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|rootBuilder
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|fixture
operator|.
name|getStore
argument_list|()
operator|.
name|merge
argument_list|(
name|rootBuilder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|fixture
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|NodeBuilder
name|createNewBuilder
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|NodeBuilder
name|definition
parameter_list|)
block|{
name|String
name|idxNodeName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
name|String
name|idxParentPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
name|NodeBuilder
name|newIdxBuilder
init|=
name|child
argument_list|(
name|getRootBuilder
argument_list|()
argument_list|,
name|idxParentPath
argument_list|)
decl_stmt|;
name|newIdxBuilder
operator|.
name|setChildNode
argument_list|(
name|idxNodeName
argument_list|,
name|definition
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|newIdxBuilder
operator|.
name|child
argument_list|(
name|idxNodeName
argument_list|)
return|;
block|}
specifier|private
name|NodeBuilder
name|getRootBuilder
parameter_list|()
block|{
if|if
condition|(
name|rootBuilder
operator|==
literal|null
condition|)
block|{
name|rootBuilder
operator|=
name|createRootBuilder
argument_list|()
expr_stmt|;
block|}
return|return
name|rootBuilder
return|;
block|}
specifier|private
name|NodeBuilder
name|createRootBuilder
parameter_list|()
block|{
try|try
block|{
name|indexStoreDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|fixture
operator|=
name|NodeStoreFixtureProvider
operator|.
name|create
argument_list|(
name|createSegmentOptions
argument_list|(
name|indexStoreDir
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to create Segment store at "
operator|+
name|indexStoreDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|fixture
operator|.
name|getStore
argument_list|()
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|Options
name|createSegmentOptions
parameter_list|(
name|File
name|storePath
parameter_list|)
throws|throws
name|IOException
block|{
name|OptionParser
name|parser
init|=
operator|new
name|OptionParser
argument_list|()
decl_stmt|;
name|Options
name|opts
init|=
operator|new
name|Options
argument_list|()
operator|.
name|withDisableSystemExit
argument_list|()
decl_stmt|;
name|opts
operator|.
name|parseAndConfigure
argument_list|(
name|parser
argument_list|,
operator|new
name|String
index|[]
block|{
name|storePath
operator|.
name|getAbsolutePath
argument_list|()
block|}
argument_list|)
expr_stmt|;
return|return
name|opts
return|;
block|}
specifier|private
specifier|static
name|NodeBuilder
name|child
parameter_list|(
name|NodeBuilder
name|nb
parameter_list|,
name|String
name|path
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|checkNotNull
argument_list|(
name|path
argument_list|)
argument_list|)
control|)
block|{
name|nb
operator|=
name|nb
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|nb
return|;
block|}
specifier|public
name|SegmentPropertyIndexEditorProvider
name|with
parameter_list|(
name|MountInfoProvider
name|mountInfoProvider
parameter_list|)
block|{
name|this
operator|.
name|mountInfoProvider
operator|=
name|mountInfoProvider
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

