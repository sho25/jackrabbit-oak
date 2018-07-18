begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
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
package|;
end_package

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

begin_comment
comment|/**  * Instances of this class provides {@link SegmentId} instances of a given  * {@link SegmentStore} and creates new {@code SegmentId} instances on the fly  * if required.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SegmentIdProvider
block|{
comment|/**      * @return The number of distinct segment ids this provider is tracking.      */
name|int
name|getSegmentIdCount
parameter_list|()
function_decl|;
comment|/**      * Provide a {@code SegmentId} represented by the given MSB/LSB pair.      *      * @param msb The most significant bits of the {@code SegmentId}.      * @param lsb The least significant bits of the {@code SegmentId}.      * @return A non-{@code null} instance of {@code SegmentId}.      */
annotation|@
name|NotNull
name|SegmentId
name|newSegmentId
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|)
function_decl|;
comment|/**      * Provide a {@code SegmentId} for a segment of type "bulk".      *      * @return A non-{@code null} instance of {@code SegmentId}.      */
annotation|@
name|NotNull
name|SegmentId
name|newDataSegmentId
parameter_list|()
function_decl|;
comment|/**      * Provide a {@code SegmentId} for a segment of type "data".      *      * @return A non-{@code null} instance of {@code SegmentId}.      */
annotation|@
name|NotNull
name|SegmentId
name|newBulkSegmentId
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

