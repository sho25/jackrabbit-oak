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
name|spi
operator|.
name|query
package|;
end_package

begin_comment
comment|/**  * A row returned by the index.  */
end_comment

begin_interface
specifier|public
interface|interface
name|IndexRow
block|{
comment|/**      * The path of the node, if available.      *      * @return the path      */
name|String
name|getPath
parameter_list|()
function_decl|;
comment|/**      * The value of the given property, if available. This might be a property      * of the given node, or a pseudo-property (a property that is only      * available in the index but not in the node itself, such as "jcr:score").      *      * @param columnName the column name      * @return the value, or null if not available      */
name|PropertyValue
name|getValue
parameter_list|(
name|String
name|columnName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

