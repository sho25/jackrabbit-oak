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

begin_class
class|class
name|DefaultGarbageCollectionStrategy
extends|extends
name|AbstractGarbageCollectionStrategy
block|{
annotation|@
name|Override
name|EstimationStrategy
name|getFullEstimationStrategy
parameter_list|()
block|{
return|return
operator|new
name|FullSizeDeltaEstimationStrategy
argument_list|()
return|;
block|}
annotation|@
name|Override
name|EstimationStrategy
name|getTailEstimationStrategy
parameter_list|()
block|{
return|return
operator|new
name|TailSizeDeltaEstimationStrategy
argument_list|()
return|;
block|}
annotation|@
name|Override
name|CompactionStrategy
name|getFullCompactionStrategy
parameter_list|()
block|{
return|return
operator|new
name|FullCompactionStrategy
argument_list|()
return|;
block|}
annotation|@
name|Override
name|CompactionStrategy
name|getTailCompactionStrategy
parameter_list|()
block|{
return|return
operator|new
name|FallbackCompactionStrategy
argument_list|(
operator|new
name|TailCompactionStrategy
argument_list|()
argument_list|,
operator|new
name|FullCompactionStrategy
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
name|CleanupStrategy
name|getCleanupStrategy
parameter_list|()
block|{
return|return
operator|new
name|DefaultCleanupStrategy
argument_list|()
return|;
block|}
block|}
end_class

end_unit

