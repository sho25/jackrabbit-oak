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
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenDataException
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

begin_interface
specifier|public
interface|interface
name|SegmentNodeStoreStatsMBean
block|{
name|String
name|TYPE
init|=
literal|"SegmentStoreStats"
decl_stmt|;
comment|/**      * @return  time series of the number of commits      */
name|CompositeData
name|getCommitsCount
parameter_list|()
function_decl|;
comment|/**      * @return  time series of the number of commits queuing      */
name|CompositeData
name|getQueuingCommitsCount
parameter_list|()
function_decl|;
comment|/**      * @return  time series of the commit times      */
name|CompositeData
name|getCommitTimes
parameter_list|()
function_decl|;
comment|/**      * @return  time series of the queuing times      */
name|CompositeData
name|getQueuingTimes
parameter_list|()
function_decl|;
comment|/**      * @return tabular data of the form&lt;commits,writerGroup&gt; collected       *<b>in the last minute</b>      * @throws OpenDataException if data is not available      */
name|TabularData
name|getCommitsCountPerWriterGroupLastMinute
parameter_list|()
throws|throws
name|OpenDataException
function_decl|;
comment|/**      * @return tabular data of the form&lt;commits,writer&gt; for writers       *         not included in groups      * @throws OpenDataException if data is not available      */
name|TabularData
name|getCommitsCountForOtherWriters
parameter_list|()
throws|throws
name|OpenDataException
function_decl|;
comment|/**      * @return tabular data of the form<em>&lt;writer, writerDetails, writerTimeStamp&gt;</em>      * for each writer currently in the queue      * @throws OpenDataException if data is not available      */
name|TabularData
name|getQueuedWriters
parameter_list|()
throws|throws
name|OpenDataException
function_decl|;
comment|/**      * Turns on/off, depending on the value of {@code flag}, the collection of       * stack traces for each writer.      * @param flag {@code boolean} indicating whether to collect or not      */
name|void
name|setCollectStackTraces
parameter_list|(
name|boolean
name|flag
parameter_list|)
function_decl|;
comment|/**      * @return collectStackTraces status flag      */
name|boolean
name|isCollectStackTraces
parameter_list|()
function_decl|;
comment|/**      * Modifies the maximum number of writers outside already defined      * groups to be recorded.      * Changing the default value will reset the overall collection process.      *       * @param otherWritersLimit the new size      */
name|void
name|setNumberOfOtherWritersToDetail
parameter_list|(
name|int
name|otherWritersLimit
parameter_list|)
function_decl|;
comment|/**      * @return maximum number of writers outside already defined      * groups to be recorded      */
name|int
name|getNumberOfOtherWritersToDetail
parameter_list|()
function_decl|;
comment|/**      * @return current groups used for grouping writers.      */
name|String
index|[]
name|getWriterGroupsForLastMinuteCounts
parameter_list|()
function_decl|;
comment|/**      * Modifies the groups used for grouping writers.      * Changing the default value will reset the overall collection process.      * @param writerGroups groups defined by regexps      */
name|void
name|setWriterGroupsForLastMinuteCounts
parameter_list|(
name|String
index|[]
name|writerGroups
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

