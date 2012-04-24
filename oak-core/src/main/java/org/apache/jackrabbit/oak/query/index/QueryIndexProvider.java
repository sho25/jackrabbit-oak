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
name|query
operator|.
name|index
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * A mechanism to index data. Indexes might be added or removed at runtime,  * possibly by changing content in the repository. The provider knows about  * indexes, and informs the query engine about indexes that where added or  * removed at runtime.  */
end_comment

begin_interface
specifier|public
interface|interface
name|QueryIndexProvider
block|{
comment|/**      * Initialize the instance.      */
name|void
name|init
parameter_list|()
function_decl|;
comment|/**      * Get the currently configured indexes.      *      * @return the list of indexes      */
name|List
argument_list|<
name|QueryIndex
argument_list|>
name|getQueryIndexes
parameter_list|()
function_decl|;
comment|/**      * Add a listener that is notified about added and removed indexes.      *      * @param listener the listener      */
name|void
name|addListener
parameter_list|(
name|QueryIndexListener
name|listener
parameter_list|)
function_decl|;
comment|/**      * Remove a listener.      *      * @param listener the listener      */
name|void
name|removeListener
parameter_list|(
name|QueryIndexListener
name|listener
parameter_list|)
function_decl|;
comment|/**      * A query index listener      */
interface|interface
name|QueryIndexListener
block|{
comment|/**          * The given index was added.          *          * @param index the index          */
name|void
name|added
parameter_list|(
name|QueryIndex
name|index
parameter_list|)
function_decl|;
comment|/**          * The given index was removed.          *          * @param index the index          */
name|void
name|removed
parameter_list|(
name|QueryIndex
name|index
parameter_list|)
function_decl|;
block|}
block|}
end_interface

end_unit

