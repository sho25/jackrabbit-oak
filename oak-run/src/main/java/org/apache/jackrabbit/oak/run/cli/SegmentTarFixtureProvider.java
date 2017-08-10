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
name|run
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
name|segment
operator|.
name|SegmentNodeStoreBuilders
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
name|segment
operator|.
name|file
operator|.
name|FileStore
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
name|segment
operator|.
name|file
operator|.
name|FileStoreBuilder
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
name|segment
operator|.
name|file
operator|.
name|InvalidFileStoreVersionException
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
name|segment
operator|.
name|file
operator|.
name|ReadOnlyFileStore
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
name|spi
operator|.
name|whiteboard
operator|.
name|Whiteboard
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
name|oak
operator|.
name|segment
operator|.
name|file
operator|.
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
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
name|whiteboard
operator|.
name|WhiteboardUtils
operator|.
name|getService
import|;
end_import

begin_class
class|class
name|SegmentTarFixtureProvider
block|{
specifier|static
name|NodeStore
name|configureSegment
parameter_list|(
name|Options
name|options
parameter_list|,
name|BlobStore
name|blobStore
parameter_list|,
name|Whiteboard
name|wb
parameter_list|,
name|Closer
name|closer
parameter_list|,
name|boolean
name|readOnly
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
name|StatisticsProvider
name|statisticsProvider
init|=
name|checkNotNull
argument_list|(
name|getService
argument_list|(
name|wb
argument_list|,
name|StatisticsProvider
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|options
operator|.
name|getOptionBean
argument_list|(
name|CommonOptions
operator|.
name|class
argument_list|)
operator|.
name|getStoreArg
argument_list|()
decl_stmt|;
name|FileStoreBuilder
name|builder
init|=
name|fileStoreBuilder
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|)
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|256
argument_list|)
decl_stmt|;
name|FileStoreBuilderCustomizer
name|customizer
init|=
name|getService
argument_list|(
name|wb
argument_list|,
name|FileStoreBuilderCustomizer
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|customizer
operator|!=
literal|null
condition|)
block|{
name|customizer
operator|.
name|customize
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|blobStore
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|withBlobStore
argument_list|(
name|blobStore
argument_list|)
expr_stmt|;
block|}
name|NodeStore
name|nodeStore
decl_stmt|;
if|if
condition|(
name|readOnly
condition|)
block|{
name|ReadOnlyFileStore
name|fileStore
init|=
name|builder
operator|.
name|withStatisticsProvider
argument_list|(
name|statisticsProvider
argument_list|)
operator|.
name|buildReadOnly
argument_list|()
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|fileStore
argument_list|)
expr_stmt|;
name|nodeStore
operator|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|fileStore
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|FileStore
name|fileStore
init|=
name|builder
operator|.
name|withStatisticsProvider
argument_list|(
name|statisticsProvider
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|fileStore
argument_list|)
expr_stmt|;
name|nodeStore
operator|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|fileStore
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
return|return
name|nodeStore
return|;
block|}
block|}
end_class

end_unit

