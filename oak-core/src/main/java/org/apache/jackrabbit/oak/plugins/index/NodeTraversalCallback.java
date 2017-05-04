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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|CommitFailedException
import|;
end_import

begin_comment
comment|/**  * Callback which invoked for any changed node read by IndexUpdate  * as part of diff traversal  */
end_comment

begin_interface
interface|interface
name|NodeTraversalCallback
block|{
name|NodeTraversalCallback
name|NOOP
init|=
operator|new
name|NodeTraversalCallback
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|traversedNode
parameter_list|()
throws|throws
name|CommitFailedException
block|{          }
block|}
decl_stmt|;
name|void
name|traversedNode
parameter_list|()
throws|throws
name|CommitFailedException
function_decl|;
block|}
end_interface

end_unit
