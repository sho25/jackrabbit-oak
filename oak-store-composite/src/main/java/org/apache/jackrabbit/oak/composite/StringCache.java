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
name|composite
package|;
end_package

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_comment
comment|/**  * This class caches the path strings used in the CompositeNodeState to avoid  * keeping too many strings in the memory.  */
end_comment

begin_class
specifier|public
class|class
name|StringCache
block|{
specifier|private
specifier|static
specifier|final
name|int
name|CACHE_SIZE
init|=
literal|1000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|StringCache
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|cache
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|(
name|CACHE_SIZE
argument_list|)
decl_stmt|;
specifier|private
name|CompositeNodeStoreMonitor
name|monitor
decl_stmt|;
specifier|public
name|String
name|get
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|cache
operator|.
name|size
argument_list|()
operator|>=
name|CACHE_SIZE
operator|&&
operator|!
name|cache
operator|.
name|containsKey
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Cache size too big. Revise your mount setup."
argument_list|)
expr_stmt|;
return|return
name|path
return|;
block|}
return|return
name|cache
operator|.
name|computeIfAbsent
argument_list|(
name|path
argument_list|,
parameter_list|(
name|k
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|monitor
operator|!=
literal|null
condition|)
block|{
name|monitor
operator|.
name|onAddStringCacheEntry
argument_list|()
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
argument_list|)
return|;
block|}
specifier|public
name|StringCache
name|withMonitor
parameter_list|(
name|CompositeNodeStoreMonitor
name|monitor
parameter_list|)
block|{
name|this
operator|.
name|monitor
operator|=
name|monitor
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

