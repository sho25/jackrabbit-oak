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
name|oak
operator|.
name|remote
package|;
end_package

begin_comment
comment|/**  * A collection of search results.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RemoteResults
extends|extends
name|Iterable
argument_list|<
name|RemoteResult
argument_list|>
block|{
comment|/**      * If available, it returns the number of results that the query is able to      * return. The number of results is independent of the offset and limit      * options used when executing the query.      *      * @return The total number of results, or -1 if this information is not      * available.      */
name|long
name|getTotal
parameter_list|()
function_decl|;
comment|/**      * The name of the columns contained in the search result.      *      * @return An instance of {@code Iterable}, where each element represents      * the name of a column in the search result.      */
name|Iterable
argument_list|<
name|String
argument_list|>
name|getColumns
parameter_list|()
function_decl|;
comment|/**      * The name of the selectors involved in the query.      *      * @return An instance of {@code Iterable}, where each element represents      * the name of a selector involved in the query.      */
name|Iterable
argument_list|<
name|String
argument_list|>
name|getSelectors
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

