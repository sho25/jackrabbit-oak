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
name|spi
operator|.
name|gc
operator|.
name|GCMonitor
import|;
end_import

begin_comment
comment|// FIXME OAK-4283: Align GCMonitor API with implementation: Unify with GCMonitor
end_comment

begin_interface
specifier|public
interface|interface
name|GCListener
extends|extends
name|GCMonitor
block|{
enum|enum
name|Status
block|{
name|SUCCESS
block|,
name|FAILURE
block|}
name|void
name|compacted
parameter_list|(
annotation|@
name|Nonnull
name|Status
name|status
parameter_list|,
name|int
name|generation
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

