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
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|RemovalCause
import|;
end_import

begin_comment
comment|/**  * A listener that gets notified of entries that were removed from the cache.  *   * @param<K> the key type  * @param<V> the value type  */
end_comment

begin_interface
specifier|public
interface|interface
name|EvictionListener
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
block|{
name|void
name|evicted
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|,
name|RemovalCause
name|removalCause
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

