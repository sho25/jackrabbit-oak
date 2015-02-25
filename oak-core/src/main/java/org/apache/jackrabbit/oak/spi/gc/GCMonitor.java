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
name|gc
package|;
end_package

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

begin_comment
comment|/**  * {@code GCMonitor} instance are used to monitor garbage collection.  * Instances of {@code GCMonitor} are registered to the {@link Whiteboard}  * to receive notifications regarding garbage collection.  */
end_comment

begin_interface
specifier|public
interface|interface
name|GCMonitor
block|{
comment|/**      * Log a message at the INFO level according to the specified format      * and arguments.      *<p/>      *<p>This form avoids superfluous string concatenation when the logger      * is disabled for the INFO level. However, this variant incurs the hidden      * (and relatively small) cost of creating an<code>Object[]</code> before invoking the method,      * even if this logger is disabled for INFO. The variants taking      * {@link #info(String, Object) one} and {@link #info(String, Object, Object) two}      * arguments exist solely in order to avoid this hidden cost.</p>      *      * @param format    the format string      * @param arguments a list of 3 or more arguments      */
comment|/**      * Informal notification on the progress of garbage collection.      * @param message  The message with {} place holders for the {@code arguments}      * @param arguments      */
name|void
name|info
parameter_list|(
name|String
name|message
parameter_list|,
name|Object
index|[]
name|arguments
parameter_list|)
function_decl|;
comment|/**      * Warning about a condition that might have advert effects on the overall      * garbage collection process but does not prevent the process from running.      * @param message  The message with {} place holders for the {@code arguments}      * @param arguments      */
name|void
name|warn
parameter_list|(
name|String
name|message
parameter_list|,
name|Object
index|[]
name|arguments
parameter_list|)
function_decl|;
comment|/**      * An error caused the garbage collection process to terminate prematurely.      * @param message      * @param exception      */
name|void
name|error
parameter_list|(
name|String
name|message
parameter_list|,
name|Exception
name|exception
parameter_list|)
function_decl|;
comment|/**      * A garbage collection cycle is skipped for a specific {@code reason}.      * @param reason  The reason with {} place holders for the {@code arguments}      * @param arguments      */
name|void
name|skipped
parameter_list|(
name|String
name|reason
parameter_list|,
name|Object
index|[]
name|arguments
parameter_list|)
function_decl|;
comment|/**      * The compaction phase of the garbage collection process terminated successfully.      */
name|void
name|compacted
parameter_list|()
function_decl|;
comment|/**      * The cleanup phase of the garbage collection process terminated successfully.      * @param reclaimedSize  number of bytes reclaimed      * @param currentSize    number of bytes after garbage collection      */
name|void
name|cleaned
parameter_list|(
name|long
name|reclaimedSize
parameter_list|,
name|long
name|currentSize
parameter_list|)
function_decl|;
class|class
name|Empty
implements|implements
name|GCMonitor
block|{
annotation|@
name|Override
specifier|public
name|void
name|info
parameter_list|(
name|String
name|message
parameter_list|,
name|Object
index|[]
name|arguments
parameter_list|)
block|{ }
annotation|@
name|Override
specifier|public
name|void
name|warn
parameter_list|(
name|String
name|message
parameter_list|,
name|Object
index|[]
name|arguments
parameter_list|)
block|{ }
annotation|@
name|Override
specifier|public
name|void
name|error
parameter_list|(
name|String
name|message
parameter_list|,
name|Exception
name|e
parameter_list|)
block|{ }
annotation|@
name|Override
specifier|public
name|void
name|skipped
parameter_list|(
name|String
name|reason
parameter_list|,
name|Object
index|[]
name|arguments
parameter_list|)
block|{ }
annotation|@
name|Override
specifier|public
name|void
name|compacted
parameter_list|()
block|{ }
annotation|@
name|Override
specifier|public
name|void
name|cleaned
parameter_list|(
name|long
name|reclaimedSize
parameter_list|,
name|long
name|currentSize
parameter_list|)
block|{ }
block|}
block|}
end_interface

end_unit

