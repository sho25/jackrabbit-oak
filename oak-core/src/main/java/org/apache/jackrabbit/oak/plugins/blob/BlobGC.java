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
name|java
operator|.
name|lang
operator|.
name|System
operator|.
name|nanoTime
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
name|management
operator|.
name|ManagementOperation
operator|.
name|Status
operator|.
name|formatTime
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
name|management
operator|.
name|ManagementOperation
operator|.
name|done
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
name|management
operator|.
name|ManagementOperation
operator|.
name|newManagementOperation
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
name|Callable
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
name|Executor
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
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
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
name|management
operator|.
name|ManagementOperation
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

begin_comment
comment|/**  * Default implementation of {@link BlobGCMBean} based on a {@link BlobGarbageCollector}.  */
end_comment

begin_class
specifier|public
class|class
name|BlobGC
implements|implements
name|BlobGCMBean
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BlobGC
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OP_NAME
init|=
literal|"Blob garbage collection"
decl_stmt|;
specifier|private
specifier|final
name|BlobGarbageCollector
name|blobGarbageCollector
decl_stmt|;
specifier|private
specifier|final
name|Executor
name|executor
decl_stmt|;
specifier|private
name|ManagementOperation
argument_list|<
name|String
argument_list|>
name|gcOp
init|=
name|done
argument_list|(
name|OP_NAME
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/**      * @param blobGarbageCollector  Blob garbage collector      * @param executor              executor for running the garbage collection task      */
specifier|public
name|BlobGC
parameter_list|(
annotation|@
name|Nonnull
name|BlobGarbageCollector
name|blobGarbageCollector
parameter_list|,
annotation|@
name|Nonnull
name|Executor
name|executor
parameter_list|)
block|{
name|this
operator|.
name|blobGarbageCollector
operator|=
name|checkNotNull
argument_list|(
name|blobGarbageCollector
argument_list|)
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|checkNotNull
argument_list|(
name|executor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|CompositeData
name|startBlobGC
parameter_list|(
specifier|final
name|boolean
name|markOnly
parameter_list|)
block|{
if|if
condition|(
name|gcOp
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|gcOp
operator|=
name|newManagementOperation
argument_list|(
name|OP_NAME
argument_list|,
operator|new
name|Callable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|t0
init|=
name|nanoTime
argument_list|()
decl_stmt|;
name|blobGarbageCollector
operator|.
name|collectGarbage
argument_list|(
name|markOnly
argument_list|)
expr_stmt|;
return|return
literal|"Blob gc completed in "
operator|+
name|formatTime
argument_list|(
name|nanoTime
argument_list|()
operator|-
name|t0
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|executor
operator|.
name|execute
argument_list|(
name|gcOp
argument_list|)
expr_stmt|;
block|}
return|return
name|getBlobGCStatus
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|CompositeData
name|getBlobGCStatus
parameter_list|()
block|{
return|return
name|gcOp
operator|.
name|getStatus
argument_list|()
operator|.
name|toCompositeData
argument_list|()
return|;
block|}
block|}
end_class

end_unit

