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
name|index
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
name|Nullable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|annotation
operator|.
name|versioning
operator|.
name|ProviderType
import|;
end_import

begin_comment
comment|/**  * Captures information related to index  */
end_comment

begin_interface
annotation|@
name|ProviderType
specifier|public
interface|interface
name|IndexInfo
block|{
comment|/**      * Returns paths of index definition in the repository      */
name|String
name|getIndexPath
parameter_list|()
function_decl|;
comment|/**      * Returns type of index definition like 'property' or 'lucene'      */
name|String
name|getType
parameter_list|()
function_decl|;
comment|/**      * Returns name of the async index lane to which this index is bound to      * or null if its not an async index      */
annotation|@
name|Nullable
name|String
name|getAsyncLaneName
parameter_list|()
function_decl|;
comment|/**      * Time in millis at which index was last updated      *      * @return time in millis or -1 if unknown, -2 if synchronous      */
name|long
name|getLastUpdatedTime
parameter_list|()
function_decl|;
comment|/**      * Returns time in millis of the repository state up to which index is up-to-date.      * This may or may not be same as {@code #getLastUpdatedTime}. For e.g.      * consider an index at /oak:index/fooIndex bound to async lane "async".      * The index might have got updated 2 cycle ago when async indexer traversed content      * node which were indexed by this index and it was not updated in last index cycle.      * Then {@code indexedUptoTime} is the time of last complete cycle while      * {@code lastUpdatedTime} is the time of 2nd last cycle      *      * @return time in millis or -1 if unknown      */
name|long
name|getIndexedUpToTime
parameter_list|()
function_decl|;
comment|/**      * An estimate of entry count in the index      */
name|long
name|getEstimatedEntryCount
parameter_list|()
function_decl|;
comment|/**      * Index data storage size       * @return storage size or -1 if unknown      */
name|long
name|getSizeInBytes
parameter_list|()
function_decl|;
comment|/**      * Determines if index definition has changed but no reindexing      * was done for that change.      */
name|boolean
name|hasIndexDefinitionChangedWithoutReindexing
parameter_list|()
function_decl|;
comment|/**      * If the index definition has changed without doing any reindexing      * then this method can be used to determine the diff in the index      * definition      * @return diff if the definition change otherwise null      */
annotation|@
name|Nullable
name|String
name|getIndexDefinitionDiff
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

