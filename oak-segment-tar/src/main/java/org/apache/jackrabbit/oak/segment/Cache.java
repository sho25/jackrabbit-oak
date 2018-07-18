begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
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
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
comment|/**  * Partial mapping of keys of type {@code K} to values of type {@link V}. If supported by the  * underlying implementation the mappings can further be associated with a cost, which is a  * metric for the cost occurring when the given mapping is lost. Higher values represent higher  * costs.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Cache
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
block|{
comment|/**      * Add a mapping from {@code key} to {@code value}.      * @throws UnsupportedOperationException   if the underlying implementation doesn't      *         support values without an associated cost and {@link #put(Object, Object, byte)}      *         should be used instead.      */
name|void
name|put
parameter_list|(
annotation|@
name|NotNull
name|K
name|key
parameter_list|,
annotation|@
name|NotNull
name|V
name|value
parameter_list|)
function_decl|;
comment|/**      * Add a mapping from {@code key} to {@code value} with a given {@code cost}.      * @throws UnsupportedOperationException   if the underlying implementation doesn't      *         support values with an associated cost and {@link #put(Object, Object)}      *         should be used instead.      */
name|void
name|put
parameter_list|(
annotation|@
name|NotNull
name|K
name|key
parameter_list|,
annotation|@
name|NotNull
name|V
name|value
parameter_list|,
name|byte
name|cost
parameter_list|)
function_decl|;
comment|/**      * @return  The mapping for {@code key}, or {@code null} if none.      */
annotation|@
name|Nullable
name|V
name|get
parameter_list|(
annotation|@
name|NotNull
name|K
name|key
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

