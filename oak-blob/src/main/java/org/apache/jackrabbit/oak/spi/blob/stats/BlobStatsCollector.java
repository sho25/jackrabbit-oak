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
name|spi
operator|.
name|blob
operator|.
name|stats
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|aQute
operator|.
name|bnd
operator|.
name|annotation
operator|.
name|ConsumerType
import|;
end_import

begin_comment
comment|/**  * BlobStoreStatsCollector receives callback when blobs are written and read  * from BlobStore  */
end_comment

begin_interface
annotation|@
name|ConsumerType
specifier|public
interface|interface
name|BlobStatsCollector
block|{
name|BlobStatsCollector
name|NOOP
init|=
operator|new
name|BlobStatsCollector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|uploaded
parameter_list|(
name|long
name|timeTaken
parameter_list|,
name|TimeUnit
name|unit
parameter_list|,
name|long
name|size
parameter_list|)
block|{          }
annotation|@
name|Override
specifier|public
name|void
name|downloaded
parameter_list|(
name|String
name|blobId
parameter_list|,
name|long
name|timeTaken
parameter_list|,
name|TimeUnit
name|unit
parameter_list|,
name|long
name|size
parameter_list|)
block|{          }
block|}
decl_stmt|;
comment|/**      * Called when a binary content is written to BlobStore      *      * @param timeTaken time taken to perform the operation      * @param unit unit of time taken      * @param size size of binary content being written      */
name|void
name|uploaded
parameter_list|(
name|long
name|timeTaken
parameter_list|,
name|TimeUnit
name|unit
parameter_list|,
name|long
name|size
parameter_list|)
function_decl|;
comment|/**      * Called when a binary content is read from BlobStore      *      * @param blobId id of blob whose content are being read      * @param timeTaken time taken to perform the operation      * @param unit unit of time taken      * @param size size of binary content being read      */
name|void
name|downloaded
parameter_list|(
name|String
name|blobId
parameter_list|,
name|long
name|timeTaken
parameter_list|,
name|TimeUnit
name|unit
parameter_list|,
name|long
name|size
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

