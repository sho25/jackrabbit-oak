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
name|plugins
operator|.
name|index
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|spi
operator|.
name|state
operator|.
name|NodeBuilder
import|;
end_import

begin_comment
comment|/**  * Extension point for plugging in different kinds of IndexHook providers.  *   * @see IndexHook  */
end_comment

begin_interface
specifier|public
interface|interface
name|IndexHookProvider
block|{
comment|/**      *       * Each provider knows how to produce a certain type of index. If the      *<code>type</code> param is of an unknown value, the provider is expected      * to return an empty list.      *       *<p>      * The<code>builder</code> must point to the repository content node, not      * the index content node. Each<code>IndexHook</code> implementation will      * have to drill down to its specific index content, and possibly deal with      * multiple indexes of the same type.      *</p>      *       * @param type      *            the index type      * @param builder      *            the node state builder of the content node that will be used      *            for updates      * @return a list of index hooks of the given type      */
annotation|@
name|Nonnull
name|List
argument_list|<
name|?
extends|extends
name|IndexHook
argument_list|>
name|getIndexHooks
parameter_list|(
name|String
name|type
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

