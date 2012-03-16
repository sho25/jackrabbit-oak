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
name|mk
operator|.
name|model
package|;
end_package

begin_comment
comment|/**  * Builder interface for constructing new {@link NodeState node states}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeBuilder
block|{
comment|/**      * Sets or removes the named property.      *      * @param name property name      * @param encodedValue encoded value of the property,      *                     or<code>null</code> to remove the named property      */
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|encodedValue
parameter_list|)
function_decl|;
comment|/**      * Sets or removes the named child node.      *      * @param name child node name      * @param childNode new child node state,      *                  or<code>null</code> to remove the named child node      */
name|void
name|setChildNode
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|childNode
parameter_list|)
function_decl|;
comment|/**      * Returns an immutable node state that matches the current state of      * the builder.      *      * @return immutable node state      */
name|NodeState
name|getNodeState
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

