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
name|state
package|;
end_package

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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_comment
comment|/**  * MBean for starting and monitoring the progress of  * revision garbage collection.  *  * @see org.apache.jackrabbit.oak.api.jmx.RepositoryManagementMBean  */
end_comment

begin_interface
specifier|public
interface|interface
name|RevisionGCMBean
block|{
name|String
name|TYPE
init|=
literal|"RevisionGarbageCollection"
decl_stmt|;
comment|/**      * Initiate a revision garbage collection operation      *      * @return  the status of the operation right after it was initiated      */
annotation|@
name|NotNull
name|CompositeData
name|startRevisionGC
parameter_list|()
function_decl|;
comment|/**      * Cancel a running revision garbage collection operation. Does nothing      * if revision garbage collection is not running.      *      * @return  the status of the operation right after it was initiated      */
annotation|@
name|NotNull
name|CompositeData
name|cancelRevisionGC
parameter_list|()
function_decl|;
comment|/**      * Revision garbage collection status      *      * @return  the status of the ongoing operation or if none the terminal      * status of the last operation or<em>Status not available</em> if none.      */
annotation|@
name|NotNull
name|CompositeData
name|getRevisionGCStatus
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

