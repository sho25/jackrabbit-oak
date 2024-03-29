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

begin_comment
comment|/**  * Implementations of this interface can be notified of progress of  * commit that would update the index. e.g. {@link AsyncIndexUpdate}  * notifies {@link IndexUpdate} about how commit progresses, which,  * in turn notifies registered callbacks (via  * {@link IndexingContext#registerIndexCommitCallback}).  */
end_comment

begin_interface
specifier|public
interface|interface
name|IndexCommitCallback
block|{
name|void
name|commitProgress
parameter_list|(
name|IndexProgress
name|indexProgress
parameter_list|)
function_decl|;
enum|enum
name|IndexProgress
block|{
name|COMMIT_SUCCEDED
block|,
name|COMMIT_FAILED
block|,
name|ABORT_REQUESTED
block|}
block|}
end_interface

end_unit

