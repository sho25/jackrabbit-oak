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
name|h2
operator|.
name|mvstore
operator|.
name|MVMap
operator|.
name|Builder
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

begin_comment
comment|/**  * A cache map. This map supports re-opening the store if this is needed.  *<p>  * Note that a failure to open the underlying store will be handled gracefully,  * in that the {@code CacheMap} can be constructed, but will not actually cache  * anything. The same is true for the case where the underlying store starts to  * fail and can not be re-opened.  *   * @param<K> the key type  * @param<V> the value type  */
end_comment

begin_class
specifier|public
class|class
name|CacheMap
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
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
name|CacheMap
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|MapFactory
name|factory
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|MVMap
operator|.
name|Builder
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|builder
decl_stmt|;
specifier|private
name|int
name|openCount
decl_stmt|;
specifier|private
specifier|volatile
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|map
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|closed
decl_stmt|;
specifier|public
name|CacheMap
parameter_list|(
name|MapFactory
name|factory
parameter_list|,
name|String
name|name
parameter_list|,
name|Builder
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|builder
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
name|openMap
argument_list|()
expr_stmt|;
comment|// OAK-8051: if opening failed, immediately try to re-open,
comment|// until either the the map is closed, or open
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|map
operator|==
literal|null
operator|&&
operator|!
name|closed
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
name|reopen
argument_list|(
name|i
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|reopen
parameter_list|(
name|int
name|i
parameter_list|,
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|i
operator|>
literal|10
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Too many re-opens; disabling this cache map"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
return|return;
block|}
comment|// clear the interrupt flag, to avoid re-opening many times
name|Thread
operator|.
name|interrupted
argument_list|()
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Re-opening map "
operator|+
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Re-opening map "
operator|+
name|name
operator|+
literal|" again"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Re-opening map "
operator|+
name|name
operator|+
literal|" again"
argument_list|)
expr_stmt|;
block|}
name|openMap
argument_list|()
expr_stmt|;
block|}
specifier|public
name|V
name|put
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|closed
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
return|return
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|reopen
argument_list|(
name|i
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|V
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|closed
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
return|return
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|reopen
argument_list|(
name|i
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|closed
condition|)
block|{
return|return
literal|false
return|;
block|}
try|try
block|{
return|return
name|map
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|reopen
argument_list|(
name|i
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|V
name|remove
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|closed
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
return|return
name|map
operator|.
name|remove
argument_list|(
name|key
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|reopen
argument_list|(
name|i
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|closed
condition|)
block|{
return|return;
block|}
try|try
block|{
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|reopen
argument_list|(
name|i
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|void
name|openMap
parameter_list|()
block|{
name|openCount
operator|=
name|factory
operator|.
name|reopenStoreIfNeeded
argument_list|(
name|openCount
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|m2
init|=
name|factory
operator|.
name|openMap
argument_list|(
name|name
argument_list|,
name|builder
argument_list|)
decl_stmt|;
if|if
condition|(
name|m2
operator|!=
literal|null
condition|)
block|{
name|map
operator|=
name|m2
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

