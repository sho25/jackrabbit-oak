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
name|plugins
operator|.
name|blob
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
name|api
operator|.
name|jmx
operator|.
name|Description
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
name|api
operator|.
name|jmx
operator|.
name|Name
import|;
end_import

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

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
import|;
end_import

begin_comment
comment|/**  * MBean for starting and monitoring the progress of  * blob garbage collection.  *  * @see org.apache.jackrabbit.oak.api.jmx.RepositoryManagementMBean  */
end_comment

begin_interface
specifier|public
interface|interface
name|BlobGCMBean
block|{
name|String
name|TYPE
init|=
literal|"BlobGarbageCollection"
decl_stmt|;
comment|/**      * Initiate a data store garbage collection operation.      *      * @param markOnly whether to only mark references and not sweep in the mark and sweep operation.      * @return  the status of the operation right after it was initiated      */
name|CompositeData
name|startBlobGC
parameter_list|(
annotation|@
name|Name
argument_list|(
literal|"markOnly"
argument_list|)
annotation|@
name|Description
argument_list|(
literal|"Set to true to only mark references and not sweep in the mark and sweep operation. "
operator|+
literal|"This mode is to be used when the underlying BlobStore is shared between multiple "
operator|+
literal|"different repositories. For all other cases set it to false to perform full garbage collection"
argument_list|)
name|boolean
name|markOnly
parameter_list|)
function_decl|;
comment|/**      * Initiate a data store garbage collection operation.      *      * @param markOnly whether to only mark references and not sweep in the mark and sweep operation.      * @param forceBlobIdRetrieve whether to force retrieve blob ids from datastore      * @return  the status of the operation right after it was initiated      */
name|CompositeData
name|startBlobGC
parameter_list|(
annotation|@
name|Name
argument_list|(
literal|"markOnly"
argument_list|)
annotation|@
name|Description
argument_list|(
literal|"Set to true to only mark references and not sweep in the mark and sweep operation. "
operator|+
literal|"This mode is to be used when the underlying BlobStore is shared between multiple "
operator|+
literal|"different repositories. For all other cases set it to false to perform full garbage collection"
argument_list|)
name|boolean
name|markOnly
parameter_list|,
annotation|@
name|Name
argument_list|(
literal|"forceBlobIdRetrieve"
argument_list|)
annotation|@
name|Description
argument_list|(
literal|"Set to true to force retrieve all ids from the datastore bypassing any local tracking"
argument_list|)
name|boolean
name|forceBlobIdRetrieve
parameter_list|)
function_decl|;
comment|/**      * Data store garbage collection status      *      * @return  the status of the ongoing operation or if none the terminal      * status of the last operation or<em>Status not available</em> if none.      */
annotation|@
name|Nonnull
name|CompositeData
name|getBlobGCStatus
parameter_list|()
function_decl|;
comment|/**      * Show details of the data Store garbage collection process.      *       * @return List of available repositories and their status      */
name|TabularData
name|getGlobalMarkStats
parameter_list|()
function_decl|;
comment|/**      * Data Store consistency check      *       * @return the missing blobs      */
name|CompositeData
name|checkConsistency
parameter_list|()
function_decl|;
comment|/**      * Consistency check status      *       * @return the status of the ongoing operation or if none the terminal      * status of the last operation or<em>Status not available</em> if none.      */
annotation|@
name|Nonnull
name|CompositeData
name|getConsistencyCheckStatus
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

