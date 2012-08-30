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
name|api
package|;
end_package

begin_comment
comment|/**  * A result from executing a query.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Result
block|{
comment|/**      * Get the list of column names.      *      * @return the column names      */
name|String
index|[]
name|getColumnNames
parameter_list|()
function_decl|;
comment|/**      * Get the list of selector names.      *      * @return the selector names      */
name|String
index|[]
name|getSelectorNames
parameter_list|()
function_decl|;
comment|/**      * Get the rows.      *      * @return the rows      */
name|Iterable
argument_list|<
name|?
extends|extends
name|ResultRow
argument_list|>
name|getRows
parameter_list|()
function_decl|;
comment|/**      * Get the number of rows, if known. If the size is not known, -1 is      * returned.      *      * @return the size or -1 if unknown      */
name|long
name|getSize
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

