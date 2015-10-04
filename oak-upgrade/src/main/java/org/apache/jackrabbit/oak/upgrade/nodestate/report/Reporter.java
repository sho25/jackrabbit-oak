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
name|upgrade
operator|.
name|nodestate
operator|.
name|report
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * A {@code Reporter} receives callbacks for every NodeState  * and PropertyState that was accessed via a {ReportingNodeState}  * instance.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Reporter
block|{
comment|/**      * Callback reporting that the given {@code nodeState} was accessed.      *      * @param nodeState The accessed {@code ReportingNodeState} instance.      */
name|void
name|reportNode
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|ReportingNodeState
name|nodeState
parameter_list|)
function_decl|;
comment|/**      * Callback reporting that the property named {@code propertyName}      * was accessed on the {@code parent} node.      *      * @param parent The parent node state of the reported property.      * @param propertyName The name of the reported property.      */
name|void
name|reportProperty
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|ReportingNodeState
name|parent
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|String
name|propertyName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

