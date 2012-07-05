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
name|spi
operator|.
name|state
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
comment|/**  * A {@code ChildNodeEntry} instance represents the child node states of a  * {@link NodeState}.  *<h2>Equality and hash codes</h2>  *<p>  * Two child node entries are considered equal if and only if their names  * and referenced node states match. The {@link Object#equals(Object)}  * method needs to be implemented so that it complies with this definition.  * And while child node entries are not meant for use as hash keys, the  * {@link Object#hashCode()} method should still be implemented according  * to this equality contract.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ChildNodeEntry
block|{
comment|/**      * The name of the child node state wrt. to its parent state.      * @return  name of the child node      */
annotation|@
name|Nonnull
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * The child node state      * @return child node state      */
annotation|@
name|Nonnull
name|NodeState
name|getNodeState
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

