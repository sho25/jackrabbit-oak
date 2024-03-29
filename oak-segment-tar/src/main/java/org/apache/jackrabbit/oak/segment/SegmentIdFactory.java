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
comment|/**  * A factory for {@link SegmentId} given their representation in MSB/LSB longs.  *<p>  * An instance of this class is used by the {@link SegmentTracker} to delegate  * the creation of {@link SegmentId}s to its caller, that usually is a {@link  * SegmentStore}. This way, the {@link SegmentStore} may attach additional,  * implementation-dependent information to the returned {@link SegmentId} in a  * way that is transparent to the implementation of the {@link SegmentTracker}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SegmentIdFactory
block|{
comment|/**      * Creates a {@link SegmentId} represented by the given MSB/LSB pair.      *      * @param msb The most significant bits of the {@link SegmentId}.      * @param lsb The least significant bits of the {@link SegmentId}.      * @return An instance of {@link SegmentId}. The returned instance is never      * {@code null}.      */
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
block|}
end_interface

end_unit

