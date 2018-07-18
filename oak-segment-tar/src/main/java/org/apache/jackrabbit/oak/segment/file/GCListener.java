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
name|tar
operator|.
name|GCGeneration
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
name|gc
operator|.
name|GCMonitor
import|;
end_import

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
comment|/**  * Listener receiving notifications about the garbage collection process  */
end_comment

begin_interface
interface|interface
name|GCListener
extends|extends
name|GCMonitor
block|{
comment|/**      * Notification of a successfully completed compaction resulting in      * a new generation of segments      * @param newGeneration  the new generation number      */
name|void
name|compactionSucceeded
parameter_list|(
annotation|@
name|NotNull
name|GCGeneration
name|newGeneration
parameter_list|)
function_decl|;
comment|/**      * Notification of a failed compaction. A new generation of      * segments could not be created.      * @param failedGeneration  the generation number that could not be created      */
name|void
name|compactionFailed
parameter_list|(
annotation|@
name|NotNull
name|GCGeneration
name|failedGeneration
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

