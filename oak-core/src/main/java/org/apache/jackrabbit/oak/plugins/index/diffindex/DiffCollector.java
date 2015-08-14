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
name|index
operator|.
name|diffindex
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|oak
operator|.
name|spi
operator|.
name|query
operator|.
name|Filter
import|;
end_import

begin_comment
comment|/**  * In charge of collecting the paths of nodes that match a given filter from the  * diff of the 2 states.  *   */
end_comment

begin_interface
specifier|public
interface|interface
name|DiffCollector
block|{
comment|/**      * Get the cost for the given filter, and prepare the result if the index      * can be used.      *       * @param filter the filter      * @return the cost      */
name|double
name|getCost
parameter_list|(
name|Filter
name|filter
parameter_list|)
function_decl|;
comment|/**      * Get the result for this filter.      *       * @param filter the filter      * @return the result      */
name|Set
argument_list|<
name|String
argument_list|>
name|getResults
parameter_list|(
name|Filter
name|filter
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

