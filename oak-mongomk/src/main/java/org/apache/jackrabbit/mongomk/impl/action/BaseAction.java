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
name|mongomk
operator|.
name|impl
operator|.
name|action
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
name|mongomk
operator|.
name|impl
operator|.
name|MongoNodeStore
import|;
end_import

begin_comment
comment|/**  * An abstract base class for actions performed against {@code MongoDB}.  *  * @param<T> The result type of the query.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|BaseAction
parameter_list|<
name|T
parameter_list|>
block|{
specifier|protected
name|MongoNodeStore
name|nodeStore
decl_stmt|;
comment|/**      * Constructs a new {@code AbstractAction}.      *      * @param nodeStore Node store.      */
specifier|public
name|BaseAction
parameter_list|(
name|MongoNodeStore
name|nodeStore
parameter_list|)
block|{
name|this
operator|.
name|nodeStore
operator|=
name|nodeStore
expr_stmt|;
block|}
comment|/**      * Executes this action.      *      * @return The result of the action.      * @throws Exception If an error occurred while executing the action.      */
specifier|public
specifier|abstract
name|T
name|execute
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
end_class

end_unit

