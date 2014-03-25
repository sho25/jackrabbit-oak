begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|persistence
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
name|mk
operator|.
name|model
operator|.
name|Commit
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
name|mk
operator|.
name|model
operator|.
name|Id
import|;
end_import

begin_comment
comment|/**  * Advanced persistence implementation offering GC support.  *<p>  * The persistence implementation must ensure that objects written between {@link #start()}  * and {@link #sweep()} are not swept, in other words, they must be marked implicitely.  */
end_comment

begin_interface
specifier|public
interface|interface
name|GCPersistence
extends|extends
name|Persistence
block|{
comment|/**      * Start a GC cycle. All objects written to the persistence in subsequent calls are      * marked implicitely, i.e. they must be retained on {@link #sweep()}.      */
name|void
name|start
parameter_list|()
function_decl|;
comment|/**      * Mark a commit.      *       * @param id      *            commit id      * @return {@code true} if the commit was not marked before;      *         {@code false} otherwise      *       * @throws Exception if an error occurs      */
name|boolean
name|markCommit
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Replace a commit. Introduced to replace dangling parent commits where      * a parent commit might be collected.      *       * @param id commit id      * @param commit the commit      *      * @throws Exception if an error occurs      */
name|void
name|replaceCommit
parameter_list|(
name|Id
name|id
parameter_list|,
name|Commit
name|commit
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Mark a node.      *       * @param id      *            node id      * @return {@code true} if the node was not marked before;      *         {@code false} otherwise      *       * @throws Exception if an error occurs      */
name|boolean
name|markNode
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Mark a child node entry map.      *       * @param id      *            child node entry map id      * @return {@code true} if the child node entry map was not marked before;      *         {@code false} otherwise      *       * @throws Exception if an error occurs      */
name|boolean
name|markCNEMap
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Sweep all objects that are not marked and were written before the GC started.      *       * @return number of swept items or<code>-1</code> if number is unknown      * @throws Exception if an error occurs      */
name|int
name|sweep
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

