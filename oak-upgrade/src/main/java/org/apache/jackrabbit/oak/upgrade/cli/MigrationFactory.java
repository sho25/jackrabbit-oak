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
name|upgrade
operator|.
name|cli
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ServiceLoader
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|core
operator|.
name|RepositoryContext
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
name|blob
operator|.
name|datastore
operator|.
name|DataStoreBlobStore
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
name|blob
operator|.
name|BlobStore
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
name|state
operator|.
name|NodeStore
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
name|upgrade
operator|.
name|RepositorySidegrade
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
name|upgrade
operator|.
name|RepositoryUpgrade
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
name|upgrade
operator|.
name|cli
operator|.
name|parser
operator|.
name|MigrationOptions
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
name|upgrade
operator|.
name|cli
operator|.
name|parser
operator|.
name|StoreArguments
import|;
end_import

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
name|ImmutableList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Closer
import|;
end_import

begin_class
specifier|public
class|class
name|MigrationFactory
block|{
specifier|protected
specifier|final
name|MigrationOptions
name|options
decl_stmt|;
specifier|protected
specifier|final
name|StoreArguments
name|stores
decl_stmt|;
specifier|protected
specifier|final
name|Closer
name|closer
decl_stmt|;
specifier|public
name|MigrationFactory
parameter_list|(
name|MigrationOptions
name|options
parameter_list|,
name|StoreArguments
name|stores
parameter_list|,
name|Closer
name|closer
parameter_list|)
block|{
name|this
operator|.
name|options
operator|=
name|options
expr_stmt|;
name|this
operator|.
name|stores
operator|=
name|stores
expr_stmt|;
name|this
operator|.
name|closer
operator|=
name|closer
expr_stmt|;
block|}
specifier|public
name|RepositoryUpgrade
name|createUpgrade
parameter_list|()
throws|throws
name|IOException
throws|,
name|RepositoryException
block|{
name|RepositoryContext
name|src
init|=
name|stores
operator|.
name|getSrcStore
argument_list|()
operator|.
name|create
argument_list|(
name|closer
argument_list|)
decl_stmt|;
name|BlobStore
name|srcBlobStore
init|=
operator|new
name|DataStoreBlobStore
argument_list|(
name|src
operator|.
name|getDataStore
argument_list|()
argument_list|)
decl_stmt|;
name|NodeStore
name|dstStore
init|=
name|createTarget
argument_list|(
name|closer
argument_list|,
name|srcBlobStore
argument_list|)
decl_stmt|;
return|return
name|createUpgrade
argument_list|(
name|src
argument_list|,
name|dstStore
argument_list|)
return|;
block|}
specifier|public
name|RepositorySidegrade
name|createSidegrade
parameter_list|()
throws|throws
name|IOException
block|{
name|BlobStore
name|srcBlobStore
init|=
name|stores
operator|.
name|getSrcBlobStore
argument_list|()
operator|.
name|create
argument_list|(
name|closer
argument_list|)
decl_stmt|;
name|NodeStore
name|srcStore
init|=
name|stores
operator|.
name|getSrcStore
argument_list|()
operator|.
name|create
argument_list|(
name|srcBlobStore
argument_list|,
name|closer
argument_list|)
decl_stmt|;
name|NodeStore
name|dstStore
init|=
name|createTarget
argument_list|(
name|closer
argument_list|,
name|srcBlobStore
argument_list|)
decl_stmt|;
return|return
name|createSidegrade
argument_list|(
name|srcStore
argument_list|,
name|dstStore
argument_list|)
return|;
block|}
specifier|protected
name|NodeStore
name|createTarget
parameter_list|(
name|Closer
name|closer
parameter_list|,
name|BlobStore
name|srcBlobStore
parameter_list|)
throws|throws
name|IOException
block|{
name|BlobStore
name|dstBlobStore
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|isCopyBinariesByReference
argument_list|()
condition|)
block|{
name|dstBlobStore
operator|=
name|srcBlobStore
expr_stmt|;
block|}
else|else
block|{
name|dstBlobStore
operator|=
name|stores
operator|.
name|getDstBlobStore
argument_list|()
operator|.
name|create
argument_list|(
name|closer
argument_list|)
expr_stmt|;
block|}
name|NodeStore
name|dstStore
init|=
name|stores
operator|.
name|getDstStore
argument_list|()
operator|.
name|create
argument_list|(
name|dstBlobStore
argument_list|,
name|closer
argument_list|)
decl_stmt|;
return|return
name|dstStore
return|;
block|}
specifier|protected
name|RepositoryUpgrade
name|createUpgrade
parameter_list|(
name|RepositoryContext
name|source
parameter_list|,
name|NodeStore
name|dstStore
parameter_list|)
block|{
name|RepositoryUpgrade
name|upgrade
init|=
operator|new
name|RepositoryUpgrade
argument_list|(
name|source
argument_list|,
name|dstStore
argument_list|)
decl_stmt|;
if|if
condition|(
name|source
operator|.
name|getDataStore
argument_list|()
operator|!=
literal|null
operator|&&
name|options
operator|.
name|isCopyBinariesByReference
argument_list|()
condition|)
block|{
name|upgrade
operator|.
name|setCopyBinariesByReference
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|upgrade
operator|.
name|setCopyVersions
argument_list|(
name|options
operator|.
name|getCopyVersions
argument_list|()
argument_list|)
expr_stmt|;
name|upgrade
operator|.
name|setCopyOrphanedVersions
argument_list|(
name|options
operator|.
name|getCopyOrphanedVersions
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|options
operator|.
name|getIncludePaths
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|upgrade
operator|.
name|setIncludes
argument_list|(
name|options
operator|.
name|getIncludePaths
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|options
operator|.
name|getExcludePaths
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|upgrade
operator|.
name|setExcludes
argument_list|(
name|options
operator|.
name|getExcludePaths
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|upgrade
operator|.
name|setSkipOnError
argument_list|(
operator|!
name|options
operator|.
name|isFailOnError
argument_list|()
argument_list|)
expr_stmt|;
name|upgrade
operator|.
name|setEarlyShutdown
argument_list|(
name|options
operator|.
name|isEarlyShutdown
argument_list|()
argument_list|)
expr_stmt|;
name|ServiceLoader
argument_list|<
name|CommitHook
argument_list|>
name|loader
init|=
name|ServiceLoader
operator|.
name|load
argument_list|(
name|CommitHook
operator|.
name|class
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|CommitHook
argument_list|>
name|iterator
init|=
name|loader
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|CommitHook
argument_list|>
name|builder
init|=
name|ImmutableList
operator|.
expr|<
name|CommitHook
operator|>
name|builder
argument_list|()
operator|.
name|addAll
argument_list|(
name|iterator
argument_list|)
decl_stmt|;
name|upgrade
operator|.
name|setCustomCommitHooks
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|upgrade
return|;
block|}
specifier|private
name|RepositorySidegrade
name|createSidegrade
parameter_list|(
name|NodeStore
name|srcStore
parameter_list|,
name|NodeStore
name|dstStore
parameter_list|)
block|{
name|RepositorySidegrade
name|sidegrade
init|=
operator|new
name|RepositorySidegrade
argument_list|(
name|srcStore
argument_list|,
name|dstStore
argument_list|)
decl_stmt|;
name|sidegrade
operator|.
name|setCopyVersions
argument_list|(
name|options
operator|.
name|getCopyVersions
argument_list|()
argument_list|)
expr_stmt|;
name|sidegrade
operator|.
name|setCopyOrphanedVersions
argument_list|(
name|options
operator|.
name|getCopyOrphanedVersions
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|options
operator|.
name|getIncludePaths
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|sidegrade
operator|.
name|setIncludes
argument_list|(
name|options
operator|.
name|getIncludePaths
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|options
operator|.
name|getExcludePaths
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|sidegrade
operator|.
name|setExcludes
argument_list|(
name|options
operator|.
name|getExcludePaths
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|options
operator|.
name|getMergePaths
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|sidegrade
operator|.
name|setMerges
argument_list|(
name|options
operator|.
name|getMergePaths
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|sidegrade
return|;
block|}
block|}
end_class

end_unit

