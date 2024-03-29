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
name|cache
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|ExecutionException
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
name|cache
operator|.
name|CacheLIRS
operator|.
name|EvictionCallback
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

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
name|CacheLoader
import|;
end_import

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
name|Weigher
import|;
end_import

begin_comment
comment|/**  * Test the maximum cache size (for the FileCache).  */
end_comment

begin_class
specifier|public
class|class
name|CacheSizeTest
block|{
specifier|private
specifier|static
specifier|final
name|Weigher
argument_list|<
name|String
argument_list|,
name|FileObj
argument_list|>
name|weigher
init|=
operator|new
name|Weigher
argument_list|<
name|String
argument_list|,
name|FileObj
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|weigh
parameter_list|(
name|String
name|key
parameter_list|,
name|FileObj
name|value
parameter_list|)
block|{
return|return
name|Math
operator|.
name|round
argument_list|(
name|value
operator|.
name|length
argument_list|()
operator|/
operator|(
literal|4
operator|*
literal|1024
operator|)
argument_list|)
return|;
comment|// convert to 4 KB blocks
block|}
block|}
decl_stmt|;
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|FileObj
argument_list|>
name|files
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|ExecutionException
block|{
name|long
name|maxSize
init|=
literal|20_000_000_000L
decl_stmt|;
name|long
name|size
init|=
name|Math
operator|.
name|round
argument_list|(
name|maxSize
operator|/
operator|(
literal|1024L
operator|*
literal|4
operator|)
argument_list|)
decl_stmt|;
name|CacheLoader
argument_list|<
name|String
argument_list|,
name|FileObj
argument_list|>
name|cacheLoader
init|=
operator|new
name|CacheLoader
argument_list|<
name|String
argument_list|,
name|FileObj
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FileObj
name|load
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Fetch from local cache directory and if not found load from
comment|// backend
name|FileObj
name|cachedFile
init|=
name|getFile
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|cachedFile
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|cachedFile
return|;
block|}
else|else
block|{
return|return
name|loadFile
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
block|}
decl_stmt|;
name|CacheLIRS
argument_list|<
name|String
argument_list|,
name|FileObj
argument_list|>
name|cache
init|=
operator|new
name|CacheLIRS
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|FileObj
argument_list|>
argument_list|()
operator|.
name|maximumWeight
argument_list|(
name|size
argument_list|)
operator|.
name|recordStats
argument_list|()
operator|.
name|weigher
argument_list|(
name|weigher
argument_list|)
operator|.
name|segmentCount
argument_list|(
literal|1
argument_list|)
operator|.
name|evictionCallback
argument_list|(
operator|new
name|EvictionCallback
argument_list|<
name|String
argument_list|,
name|FileObj
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|evicted
parameter_list|(
name|String
name|key
parameter_list|,
name|FileObj
name|cachedFile
parameter_list|,
name|RemovalCause
name|cause
parameter_list|)
block|{
if|if
condition|(
name|cachedFile
operator|!=
literal|null
operator|&&
name|cachedFile
operator|.
name|exists
argument_list|()
operator|&&
name|cause
operator|!=
name|RemovalCause
operator|.
name|REPLACED
condition|)
block|{
name|delete
argument_list|(
name|cachedFile
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|15
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
literal|"n"
operator|+
name|i
decl_stmt|;
name|long
name|length
init|=
literal|1_000_000_000
decl_stmt|;
name|files
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|FileObj
argument_list|(
name|name
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
literal|"n"
operator|+
name|i
decl_stmt|;
name|cache
operator|.
name|get
argument_list|(
name|name
argument_list|,
parameter_list|()
lambda|->
name|cacheLoader
operator|.
name|load
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|FileObj
name|getFile
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|FileObj
name|obj
init|=
name|files
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
comment|// doesn't exist
return|return
operator|new
name|FileObj
argument_list|(
name|key
argument_list|,
literal|0
argument_list|)
return|;
block|}
return|return
name|obj
return|;
block|}
specifier|public
name|FileObj
name|loadFile
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|FileObj
name|f
init|=
operator|new
name|FileObj
argument_list|(
name|key
argument_list|,
literal|10_000_000
argument_list|)
decl_stmt|;
name|files
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|f
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
specifier|private
name|void
name|delete
parameter_list|(
name|FileObj
name|cachedFile
parameter_list|)
block|{
name|FileObj
name|old
init|=
name|files
operator|.
name|remove
argument_list|(
name|cachedFile
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"trying to remove a file that doesn't exist: "
operator|+
name|cachedFile
argument_list|)
throw|;
block|}
name|long
name|totalLength
init|=
name|getDirectoryLength
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"removing a file: unexpected in this test; total length "
operator|+
name|totalLength
operator|+
literal|" "
operator|+
name|old
argument_list|)
throw|;
block|}
specifier|private
name|long
name|getDirectoryLength
parameter_list|()
block|{
name|long
name|length
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FileObj
name|obj
range|:
name|files
operator|.
name|values
argument_list|()
control|)
block|{
name|length
operator|+=
name|obj
operator|.
name|length
expr_stmt|;
block|}
return|return
name|length
return|;
block|}
class|class
name|FileObj
block|{
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
specifier|public
name|FileObj
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|length
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
name|files
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
operator|+
literal|"/"
operator|+
name|length
return|;
block|}
block|}
block|}
end_class

end_unit

