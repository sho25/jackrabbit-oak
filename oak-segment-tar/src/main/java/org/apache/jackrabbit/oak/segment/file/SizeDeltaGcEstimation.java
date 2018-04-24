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
name|segment
operator|.
name|file
package|;
end_package

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
name|EstimationStrategy
operator|.
name|Context
import|;
end_import

begin_class
class|class
name|SizeDeltaGcEstimation
block|{
specifier|private
specifier|final
name|EstimationStrategy
name|estimationStrategy
decl_stmt|;
specifier|private
specifier|final
name|EstimationStrategy
operator|.
name|Context
name|context
decl_stmt|;
name|SizeDeltaGcEstimation
parameter_list|(
name|long
name|delta
parameter_list|,
annotation|@
name|Nonnull
name|GCJournal
name|gcJournal
parameter_list|,
name|long
name|currentSize
parameter_list|,
name|boolean
name|full
parameter_list|)
block|{
if|if
condition|(
name|full
condition|)
block|{
name|estimationStrategy
operator|=
operator|new
name|FullSizeDeltaEstimationStrategy
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|estimationStrategy
operator|=
operator|new
name|TailSizeDeltaEstimationStrategy
argument_list|()
expr_stmt|;
block|}
name|context
operator|=
operator|new
name|Context
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|getSizeDelta
parameter_list|()
block|{
return|return
name|delta
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getCurrentSize
parameter_list|()
block|{
return|return
name|currentSize
return|;
block|}
annotation|@
name|Override
specifier|public
name|GCJournal
name|getGCJournal
parameter_list|()
block|{
return|return
name|gcJournal
return|;
block|}
block|}
expr_stmt|;
block|}
specifier|public
name|EstimationResult
name|estimate
parameter_list|()
block|{
return|return
name|estimationStrategy
operator|.
name|estimate
argument_list|(
name|context
argument_list|)
return|;
block|}
block|}
end_class

end_unit

