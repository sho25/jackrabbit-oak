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
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_interface
specifier|public
interface|interface
name|FileStoreStatsMBean
block|{
name|String
name|TYPE
init|=
literal|"FileStoreStats"
decl_stmt|;
name|long
name|getApproximateSize
parameter_list|()
function_decl|;
comment|/**      * @return the number of tar files in the segment store      */
name|int
name|getTarFileCount
parameter_list|()
function_decl|;
comment|/**      * @return the number of segments in the segment store      */
name|int
name|getSegmentCount
parameter_list|()
function_decl|;
comment|/**      * @return  time series of the writes to repository      */
name|CompositeData
name|getWriteStats
parameter_list|()
function_decl|;
comment|/**      * @return  time series of the writes to repository      */
name|CompositeData
name|getRepositorySize
parameter_list|()
function_decl|;
name|String
name|fileStoreInfoAsString
parameter_list|()
function_decl|;
comment|/**      * @return  count of the writes to journal      */
name|long
name|getJournalWriteStatsAsCount
parameter_list|()
function_decl|;
comment|/**      * @return  time series of the writes to journal      */
name|CompositeData
name|getJournalWriteStatsAsCompositeData
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

