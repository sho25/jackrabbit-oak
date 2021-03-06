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
operator|.
name|spi
operator|.
name|monitor
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

begin_comment
comment|/**  * Callback interface that eases the collection of statistics about I/O  * operations.  */
end_comment

begin_interface
specifier|public
interface|interface
name|IOMonitor
block|{
comment|/**      * Called before a segment is read from the file system.      *      * @param file   File containing the segment.      * @param msb    Most significant bits of the segment ID.      * @param lsb    Least significant bits of the segment ID.      * @param length Size of the segment.      */
name|void
name|beforeSegmentRead
parameter_list|(
name|File
name|file
parameter_list|,
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|,
name|int
name|length
parameter_list|)
function_decl|;
comment|/**      * Called after a segment is read from the file system. This is called only      * in case of successful operations.      *      * @param file    File containing the segment.      * @param msb     Most significant bits of the segment ID.      * @param lsb     Least significant bits of the segment ID.      * @param length  Size of the segment.      * @param elapsed Time spent by the read operation, in nanoseconds.      */
name|void
name|afterSegmentRead
parameter_list|(
name|File
name|file
parameter_list|,
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|,
name|int
name|length
parameter_list|,
name|long
name|elapsed
parameter_list|)
function_decl|;
comment|/**      * Called before a segment is written to the file system.      *      * @param file   File containing the segment.      * @param msb    Most significant bits of the segment ID.      * @param lsb    Least significant bits of the segment ID.      * @param length Size of the segment.      */
name|void
name|beforeSegmentWrite
parameter_list|(
name|File
name|file
parameter_list|,
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|,
name|int
name|length
parameter_list|)
function_decl|;
comment|/**      * Called after a segment is written to the file system. This is called only      * in case of successful operations.      *      * @param file    File containing the segment.      * @param msb     Most significant bits of the segment ID.      * @param lsb     Least significant bits of the segment ID.      * @param length  Size of the segment.      * @param elapsed Time spent by the write operation, in nanoseconds.      */
name|void
name|afterSegmentWrite
parameter_list|(
name|File
name|file
parameter_list|,
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|,
name|int
name|length
parameter_list|,
name|long
name|elapsed
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

