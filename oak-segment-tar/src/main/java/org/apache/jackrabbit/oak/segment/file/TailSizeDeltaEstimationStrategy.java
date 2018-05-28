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
import|import static
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|format
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
name|PrintableBytes
operator|.
name|newPrintableBytes
import|;
end_import

begin_class
class|class
name|TailSizeDeltaEstimationStrategy
implements|implements
name|EstimationStrategy
block|{
annotation|@
name|Override
specifier|public
name|EstimationResult
name|estimate
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
if|if
condition|(
name|context
operator|.
name|getSizeDelta
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|EstimationResult
argument_list|(
literal|true
argument_list|,
literal|"Estimation skipped because the size delta value equals 0"
argument_list|)
return|;
block|}
name|long
name|previousSize
init|=
name|readPreviousSize
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|previousSize
operator|<
literal|0
condition|)
block|{
return|return
operator|new
name|EstimationResult
argument_list|(
literal|true
argument_list|,
literal|"Estimation skipped because of missing gc journal data (expected on first run)"
argument_list|)
return|;
block|}
name|long
name|gain
init|=
name|context
operator|.
name|getCurrentSize
argument_list|()
operator|-
name|previousSize
decl_stmt|;
name|boolean
name|gcNeeded
init|=
name|gain
operator|>
name|context
operator|.
name|getSizeDelta
argument_list|()
decl_stmt|;
name|String
name|gcInfo
init|=
name|format
argument_list|(
literal|"Segmentstore size has increased since the last tail garbage collection from %s to %s, an increase of %s or %s%%. "
argument_list|,
name|newPrintableBytes
argument_list|(
name|previousSize
argument_list|)
argument_list|,
name|newPrintableBytes
argument_list|(
name|context
operator|.
name|getCurrentSize
argument_list|()
argument_list|)
argument_list|,
name|newPrintableBytes
argument_list|(
name|gain
argument_list|)
argument_list|,
literal|100
operator|*
name|gain
operator|/
name|previousSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|gcNeeded
condition|)
block|{
name|gcInfo
operator|=
name|gcInfo
operator|+
name|format
argument_list|(
literal|"This is greater than sizeDeltaEstimation=%s, so running garbage collection"
argument_list|,
name|newPrintableBytes
argument_list|(
name|context
operator|.
name|getSizeDelta
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|gcInfo
operator|=
name|gcInfo
operator|+
name|format
argument_list|(
literal|"This is less than sizeDeltaEstimation=%s, so skipping garbage collection"
argument_list|,
name|newPrintableBytes
argument_list|(
name|context
operator|.
name|getSizeDelta
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|EstimationResult
argument_list|(
name|gcNeeded
argument_list|,
name|gcInfo
argument_list|)
return|;
block|}
specifier|private
name|long
name|readPreviousSize
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
return|return
name|context
operator|.
name|getGCJournal
argument_list|()
operator|.
name|read
argument_list|()
operator|.
name|getRepoSize
argument_list|()
return|;
block|}
block|}
end_class

end_unit
