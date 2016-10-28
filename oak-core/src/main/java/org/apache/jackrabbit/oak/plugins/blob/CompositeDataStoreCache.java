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
name|plugins
operator|.
name|blob
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
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ScheduledExecutorService
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|cache
operator|.
name|AbstractCache
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
name|cache
operator|.
name|CacheLoader
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
name|util
operator|.
name|concurrent
operator|.
name|ListeningExecutorService
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
name|stats
operator|.
name|StatisticsProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|checkArgument
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
specifier|public
class|class
name|CompositeDataStoreCache
extends|extends
name|AbstractCache
argument_list|<
name|String
argument_list|,
name|File
argument_list|>
implements|implements
name|Closeable
block|{
comment|/**      * Logger instance.      */
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CompositeDataStoreCache
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Cache for downloaded blobs      */
specifier|private
specifier|final
name|FileCache
name|downloadCache
decl_stmt|;
comment|/**      * Cache for staging async uploads      */
specifier|private
specifier|final
name|UploadStagingCache
name|stagingCache
decl_stmt|;
comment|/**      * The directory where the files are created.      */
specifier|private
specifier|final
name|File
name|directory
decl_stmt|;
specifier|public
name|CompositeDataStoreCache
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|size
parameter_list|,
name|int
name|uploadSplitPercentage
parameter_list|,
name|int
name|uploadThreads
parameter_list|,
name|CacheLoader
argument_list|<
name|String
argument_list|,
name|InputStream
argument_list|>
name|loader
parameter_list|,
specifier|final
name|StagingUploader
name|uploader
parameter_list|,
name|StatisticsProvider
name|statsProvider
parameter_list|,
name|ListeningExecutorService
name|executor
parameter_list|,
name|ScheduledExecutorService
name|scheduledExecutor
comment|/* purge scheduled executor */
parameter_list|,
name|int
name|purgeInterval
comment|/* async purge interval secs */
parameter_list|,
name|int
name|stagingRetryInterval
comment|/* async retry interval secs */
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|uploadSplitPercentage
operator|>=
literal|0
operator|&&
name|uploadSplitPercentage
operator|<
literal|100
argument_list|,
literal|"Upload percentage should be between 0 and 100"
argument_list|)
expr_stmt|;
name|this
operator|.
name|directory
operator|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|long
name|uploadSize
init|=
operator|(
name|size
operator|*
name|uploadSplitPercentage
operator|)
operator|/
literal|100
decl_stmt|;
name|this
operator|.
name|downloadCache
operator|=
name|FileCache
operator|.
name|build
argument_list|(
operator|(
name|size
operator|-
name|uploadSize
operator|)
argument_list|,
name|directory
argument_list|,
name|loader
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|stagingCache
operator|=
name|UploadStagingCache
operator|.
name|build
argument_list|(
name|directory
argument_list|,
name|uploadThreads
argument_list|,
name|uploadSize
argument_list|,
name|uploader
argument_list|,
name|downloadCache
argument_list|,
name|statsProvider
argument_list|,
name|executor
argument_list|,
name|scheduledExecutor
argument_list|,
name|purgeInterval
argument_list|,
name|stagingRetryInterval
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nullable
specifier|public
name|File
name|getIfPresent
parameter_list|(
name|String
name|key
parameter_list|)
block|{
comment|// Check if the file scheduled for async upload
name|File
name|staged
init|=
name|stagingCache
operator|.
name|getIfPresent
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|staged
operator|!=
literal|null
operator|&&
name|staged
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|staged
return|;
block|}
return|return
name|downloadCache
operator|.
name|getIfPresent
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|File
name|getIfPresent
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|getIfPresent
argument_list|(
operator|(
name|String
operator|)
name|key
argument_list|)
return|;
block|}
specifier|public
name|File
name|get
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
comment|// Check if the file scheduled for async upload
name|File
name|staged
init|=
name|stagingCache
operator|.
name|getIfPresent
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|staged
operator|!=
literal|null
operator|&&
name|staged
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|staged
return|;
block|}
comment|// get from cache and download if not available
return|return
name|downloadCache
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error loading [{}] from cache"
argument_list|,
name|key
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|invalidate
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|stagingCache
operator|.
name|invalidate
argument_list|(
operator|(
name|String
operator|)
name|key
argument_list|)
expr_stmt|;
name|downloadCache
operator|.
name|invalidate
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|stage
parameter_list|(
name|String
name|key
parameter_list|,
name|File
name|file
parameter_list|)
block|{
return|return
name|stagingCache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|file
argument_list|)
operator|.
name|isPresent
argument_list|()
return|;
block|}
specifier|public
name|DataStoreCacheStatsMBean
name|getStagingCacheStats
parameter_list|()
block|{
return|return
name|stagingCache
operator|.
name|getStats
argument_list|()
return|;
block|}
specifier|public
name|DataStoreCacheStatsMBean
name|getCacheStats
parameter_list|()
block|{
return|return
name|downloadCache
operator|.
name|getStats
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|downloadCache
operator|.
name|close
argument_list|()
expr_stmt|;
name|stagingCache
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|UploadStagingCache
name|getStagingCache
parameter_list|()
block|{
return|return
name|stagingCache
return|;
block|}
name|FileCache
name|getDownloadCache
parameter_list|()
block|{
return|return
name|downloadCache
return|;
block|}
block|}
end_class

end_unit

