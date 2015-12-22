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
name|stats
package|;
end_package

begin_import
import|import
name|aQute
operator|.
name|bnd
operator|.
name|annotation
operator|.
name|ProviderType
import|;
end_import

begin_interface
annotation|@
name|ProviderType
specifier|public
interface|interface
name|CounterStats
extends|extends
name|Stats
block|{
comment|/**      * Returns the counter's current value.      *      * @return the counter's current value      */
name|long
name|getCount
parameter_list|()
function_decl|;
comment|/**      * Increment the counter by one.      */
name|void
name|inc
parameter_list|()
function_decl|;
comment|/**      * Decrement the counter by one.      */
name|void
name|dec
parameter_list|()
function_decl|;
comment|/**      * Increment the counter by {@code n}.      *      * @param n the amount by which the counter will be increased      */
name|void
name|inc
parameter_list|(
name|long
name|n
parameter_list|)
function_decl|;
comment|/**      * Decrement the counter by {@code n}.      *      * @param n the amount by which the counter will be decreased      */
name|void
name|dec
parameter_list|(
name|long
name|n
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

