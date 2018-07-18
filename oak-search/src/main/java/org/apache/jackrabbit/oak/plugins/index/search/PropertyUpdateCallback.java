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
operator|.
name|search
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
name|PropertyState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Callback to be invoked for each indexable property change  */
end_comment

begin_interface
specifier|public
interface|interface
name|PropertyUpdateCallback
block|{
comment|/**      * Invoked upon any change in property either added, updated or removed.      * Implementation can determine if property is added, updated or removed based      * on whether before or after is null      *      * @param nodePath path of node for which is to be indexed for this property change      * @param propertyRelativePath relative path of the property wrt the indexed node      * @param pd property definition associated with the property to be indexed      * @param before before state. Is null when property is added. For other cases its not null      * @param after after state of the property. Is null when property is removed. For other cases its not null      */
name|void
name|propertyUpdated
parameter_list|(
name|String
name|nodePath
parameter_list|,
name|String
name|propertyRelativePath
parameter_list|,
name|PropertyDefinition
name|pd
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|before
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|after
parameter_list|)
function_decl|;
comment|/**      * Invoked after editor has traversed all the changes      *      * @throws CommitFailedException in case some validation fails      */
name|void
name|done
parameter_list|()
throws|throws
name|CommitFailedException
function_decl|;
block|}
end_interface

end_unit

