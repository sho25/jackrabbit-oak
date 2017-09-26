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
name|document
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Receives callbacks from the {@link NodeDocumentSweeper} on what updates  * are required for the sweep ({@link #sweepUpdate(Map)} and required  * invalidation of documents.  */
end_comment

begin_interface
interface|interface
name|NodeDocumentSweepListener
block|{
comment|/**      * Called for a batch of sweep updates that should be performed.      *      * @param updates the update operations. The keys in the map are the paths      *                of the documents to update.      * @throws DocumentStoreException if the operation fails.      */
name|void
name|sweepUpdate
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|UpdateOp
argument_list|>
name|updates
parameter_list|)
throws|throws
name|DocumentStoreException
function_decl|;
block|}
end_interface

end_unit

