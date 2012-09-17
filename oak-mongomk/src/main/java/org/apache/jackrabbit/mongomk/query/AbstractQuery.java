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
name|query
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
name|MongoConnection
import|;
end_import

begin_comment
comment|/**  * An abstract base class for queries performed with {@code MongoDB}.  *  * @param<T>  *            The result type of the query.  *  * @author<a href="mailto:pmarx@adobe.com>Philipp Marx</a>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractQuery
parameter_list|<
name|T
parameter_list|>
block|{
comment|/** The {@link MongoConnection}. */
specifier|protected
name|MongoConnection
name|mongoConnection
decl_stmt|;
comment|/**      * Constructs a new {@code AbstractQuery}.      *      * @param mongoConnection      *            The {@link MongoConnection}.      */
specifier|protected
name|AbstractQuery
parameter_list|(
name|MongoConnection
name|mongoConnection
parameter_list|)
block|{
name|this
operator|.
name|mongoConnection
operator|=
name|mongoConnection
expr_stmt|;
block|}
comment|/**      * Executes this query.      *      * @return The result of the query.      * @throws Exception      *             If an error occurred while executing the query.      */
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

