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
name|plugins
operator|.
name|index
operator|.
name|property
operator|.
name|jmx
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
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_comment
comment|/**  * MBean for starting and monitoring the progress of asynchronous reindexing of  * the property index  *   * @see org.apache.jackrabbit.oak.api.jmx.RepositoryManagementMBean  */
end_comment

begin_interface
specifier|public
interface|interface
name|PropertyIndexAsyncReindexMBean
block|{
name|String
name|TYPE
init|=
literal|"PropertyIndexAsyncReindex"
decl_stmt|;
comment|/**      * Initiate a reindex operation for the property indexes marked for      * reindexing      *       * @return the status of the operation right after it was initiated      */
annotation|@
name|Nonnull
name|CompositeData
name|startPropertyIndexAsyncReindex
parameter_list|()
function_decl|;
comment|/**      * Asynchronous Property Index reindexing status      *       * @return the status of the ongoing operation or if none the terminal      *         status of the last operation or<em>Status not available</em> if      *         none.      */
annotation|@
name|Nonnull
name|CompositeData
name|getPropertyIndexAsyncReindexStatus
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

