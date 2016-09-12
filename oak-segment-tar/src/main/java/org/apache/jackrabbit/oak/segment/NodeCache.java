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
name|segment
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

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
comment|/**  * Partial mapping of {@code String} keys to values of type {@link RecordId}. The mappings  * can further be associated with a cost, which is a metric for the cost occurring when the  * given mapping is lost. Higher values represent higher costs.  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeCache
block|{
comment|/**      * Add a mapping from {@code key} to {@code value} with a given {@code cost}.      */
name|void
name|put
parameter_list|(
annotation|@
name|Nonnull
name|String
name|stableId
parameter_list|,
annotation|@
name|Nonnull
name|RecordId
name|recordId
parameter_list|,
name|byte
name|cost
parameter_list|)
function_decl|;
comment|/**      * @return  The mapping for {@code key}, or {@code null} if none.      */
annotation|@
name|CheckForNull
name|RecordId
name|get
parameter_list|(
annotation|@
name|Nonnull
name|String
name|stableId
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

