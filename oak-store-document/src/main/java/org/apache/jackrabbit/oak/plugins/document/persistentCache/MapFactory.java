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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|h2
operator|.
name|mvstore
operator|.
name|MVMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|MapFactory
block|{
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MapFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|int
name|openCount
init|=
literal|1
decl_stmt|;
comment|/**      * Ensure the store is open, re-opening it if needed. The store is first      * closed if the old open count if the old open count matches the current      * open count.      *      * @param oldOpenCount the old open count      * @return the new open count      */
specifier|synchronized
name|int
name|reopenStoreIfNeeded
parameter_list|(
name|int
name|oldOpenCount
parameter_list|)
block|{
if|if
condition|(
name|oldOpenCount
operator|==
name|openCount
condition|)
block|{
name|closeStore
argument_list|()
expr_stmt|;
name|openCount
operator|++
expr_stmt|;
name|openStore
argument_list|()
expr_stmt|;
block|}
return|return
name|openCount
return|;
block|}
specifier|public
name|int
name|getOpenCount
parameter_list|()
block|{
return|return
name|openCount
return|;
block|}
comment|/**      * Open the store.      */
specifier|abstract
name|void
name|openStore
parameter_list|()
function_decl|;
comment|/**      * Close the store.      */
specifier|abstract
name|void
name|closeStore
parameter_list|()
function_decl|;
comment|/**      * Open or get the given map.      *       * @param<K> the key type      * @param<V> the value type      * @param name the map name      * @param builder the map builder      * @return      */
specifier|abstract
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|openMap
parameter_list|(
name|String
name|name
parameter_list|,
name|MVMap
operator|.
name|Builder
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|builder
parameter_list|)
function_decl|;
comment|/**      * Get the file size in bytes.      *       * @return the file size      */
specifier|abstract
name|long
name|getFileSize
parameter_list|()
function_decl|;
block|}
end_class

end_unit

