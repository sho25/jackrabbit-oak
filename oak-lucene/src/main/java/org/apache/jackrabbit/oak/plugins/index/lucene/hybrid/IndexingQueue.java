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
operator|.
name|lucene
operator|.
name|hybrid
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_interface
specifier|public
interface|interface
name|IndexingQueue
block|{
comment|/**      * Adds the given doc to a queue without any wait      *      * @param doc to be added      * @return true if the doc was added to the queue      */
name|boolean
name|addIfNotFullWithoutWait
parameter_list|(
name|LuceneDoc
name|doc
parameter_list|)
function_decl|;
comment|/**      * Adds the given doc to a queue with possible wait if queue is full.      * The wait would be having an upper limit      *      * @param doc LuceneDoc to be added      * @return true if the doc was added to the queue      */
name|boolean
name|add
parameter_list|(
name|LuceneDoc
name|doc
parameter_list|)
function_decl|;
comment|/**      * The docs are added directly to the index without any queuing      *      * @param docsPerIndex map of LuceneDoc per index path      */
name|void
name|addAllSynchronously
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|LuceneDoc
argument_list|>
argument_list|>
name|docsPerIndex
parameter_list|)
function_decl|;
comment|/**      * Schedules the async processing of queued entries      */
name|void
name|scheduleQueuedDocsProcessing
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

