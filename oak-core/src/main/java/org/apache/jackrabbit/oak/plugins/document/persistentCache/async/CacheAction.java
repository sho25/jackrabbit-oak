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
name|document
operator|.
name|persistentCache
operator|.
name|async
package|;
end_package

begin_comment
comment|/**  * Object represents an action on the cache (eg. put or invalidate).  *  * @param<K> key type  * @param<V> value type  */
end_comment

begin_interface
interface|interface
name|CacheAction
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
block|{
comment|/**      * Execute the action      */
name|void
name|execute
parameter_list|()
function_decl|;
comment|/**      * Cancel the action without executing it      */
name|void
name|cancel
parameter_list|()
function_decl|;
comment|/**      * Return the keys affected by this action      *      * @return keys affected by this action      */
name|Iterable
argument_list|<
name|K
argument_list|>
name|getAffectedKeys
parameter_list|()
function_decl|;
comment|/**      * Return the owner of this action      *      * @return {@link CacheWriteQueue} executing this action      */
name|CacheWriteQueue
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|getOwner
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

