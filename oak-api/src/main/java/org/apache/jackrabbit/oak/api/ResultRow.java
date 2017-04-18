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
name|api
package|;
end_package

begin_comment
comment|/**  * A query result row.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ResultRow
block|{
comment|/**      * The path, assuming there is only one selector.      *       * @return the path      * @throws IllegalArgumentException if there are multiple selectors      */
name|String
name|getPath
parameter_list|()
function_decl|;
comment|/**      * The path for the given selector name.      *       * @param selectorName the selector name (null if there is only one selector)      * @return the path      * @throws IllegalArgumentException if the selector was not found,      *      or if there are multiple selectors but the passed selectorName is null      */
name|String
name|getPath
parameter_list|(
name|String
name|selectorName
parameter_list|)
function_decl|;
comment|/**      * The tree for the given selector name.      *      * @param selectorName the selector name (null if there is only one selector)      * @return the tree      * @throws IllegalArgumentException if the selector was not found,      *      or if there are multiple selectors but the passed selectorName is null      */
name|Tree
name|getTree
parameter_list|(
name|String
name|selectorName
parameter_list|)
function_decl|;
comment|/**      * The property value.      *       * @param columnName the column name      * @return the value      * @throws IllegalArgumentException if the column was not found      */
name|PropertyValue
name|getValue
parameter_list|(
name|String
name|columnName
parameter_list|)
function_decl|;
comment|/**      * Get the list of values.      *       * @return the values      */
name|PropertyValue
index|[]
name|getValues
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

