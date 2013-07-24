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
name|jcr
operator|.
name|operation
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_comment
comment|/**  * A {@code SessionOperation} provides an execution context for executing session scoped operations.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SessionOperation
parameter_list|<
name|T
parameter_list|>
block|{
specifier|private
specifier|final
name|boolean
name|update
decl_stmt|;
specifier|protected
name|SessionOperation
parameter_list|(
name|boolean
name|update
parameter_list|)
block|{
name|this
operator|.
name|update
operator|=
name|update
expr_stmt|;
block|}
specifier|protected
name|SessionOperation
parameter_list|()
block|{
name|this
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns {@code true} if this operation updates the the transient      * @return      */
specifier|public
name|boolean
name|isUpdate
parameter_list|()
block|{
return|return
name|update
return|;
block|}
comment|/**      * Return {@code true} if this operation refreshed the transient space      * @return      */
specifier|public
name|boolean
name|isRefresh
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|checkPreconditions
parameter_list|()
throws|throws
name|RepositoryException
block|{     }
specifier|public
specifier|abstract
name|T
name|perform
parameter_list|()
throws|throws
name|RepositoryException
function_decl|;
block|}
end_class

end_unit

