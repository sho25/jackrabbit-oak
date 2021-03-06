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
name|segment
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|valueOf
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|atomic
operator|.
name|AtomicInteger
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
name|base
operator|.
name|Function
import|;
end_import

begin_class
specifier|public
class|class
name|ReaderCacheTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|empty
parameter_list|()
block|{
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|Function
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|loader
init|=
operator|new
name|Function
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|String
name|apply
parameter_list|(
annotation|@
name|Nullable
name|Integer
name|input
parameter_list|)
block|{
name|counter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
name|valueOf
argument_list|(
name|input
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|StringCache
name|c
init|=
operator|new
name|StringCache
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|repeat
init|=
literal|0
init|;
name|repeat
operator|<
literal|10
condition|;
name|repeat
operator|++
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
name|c
operator|.
name|get
argument_list|(
name|i
argument_list|,
name|i
argument_list|,
name|i
argument_list|,
name|loader
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// the LIRS cache should be almost empty (low hit rate there)
name|assertTrue
argument_list|(
name|valueOf
argument_list|(
name|counter
argument_list|)
argument_list|,
name|counter
operator|.
name|get
argument_list|()
operator|>
literal|1000
argument_list|)
expr_stmt|;
comment|// but the fast cache should improve the total hit rate
name|assertTrue
argument_list|(
name|valueOf
argument_list|(
name|counter
argument_list|)
argument_list|,
name|counter
operator|.
name|get
argument_list|()
operator|<
literal|5000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|largeEntries
parameter_list|()
block|{
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|String
name|large
init|=
operator|new
name|String
argument_list|(
operator|new
name|char
index|[
literal|1024
index|]
argument_list|)
decl_stmt|;
name|Function
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|loader
init|=
operator|new
name|Function
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|String
name|apply
parameter_list|(
annotation|@
name|Nullable
name|Integer
name|input
parameter_list|)
block|{
name|counter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
name|large
operator|+
name|input
return|;
block|}
block|}
decl_stmt|;
name|StringCache
name|c
init|=
operator|new
name|StringCache
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|repeat
init|=
literal|0
init|;
name|repeat
operator|<
literal|10
condition|;
name|repeat
operator|++
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|large
operator|+
name|i
argument_list|,
name|c
operator|.
name|get
argument_list|(
name|i
argument_list|,
name|i
argument_list|,
name|i
argument_list|,
name|loader
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|large
operator|+
literal|0
argument_list|,
name|c
operator|.
name|get
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|loader
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// the LIRS cache should be almost empty (low hit rate there)
comment|// and large strings are not kept in the fast cache, so hit rate should be bad
name|assertTrue
argument_list|(
name|valueOf
argument_list|(
name|counter
argument_list|)
argument_list|,
name|counter
operator|.
name|get
argument_list|()
operator|>
literal|9000
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|valueOf
argument_list|(
name|counter
argument_list|)
argument_list|,
name|counter
operator|.
name|get
argument_list|()
operator|<
literal|10000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|clear
parameter_list|()
block|{
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|Function
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|uniqueLoader
init|=
operator|new
name|Function
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|String
name|apply
parameter_list|(
annotation|@
name|Nullable
name|Integer
name|input
parameter_list|)
block|{
return|return
name|valueOf
argument_list|(
name|counter
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|StringCache
name|c
init|=
operator|new
name|StringCache
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// load a new entry
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|c
operator|.
name|get
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|uniqueLoader
argument_list|)
argument_list|)
expr_stmt|;
comment|// but only once
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|c
operator|.
name|get
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|uniqueLoader
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// after clearing the cache, load a new entry
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|c
operator|.
name|get
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|uniqueLoader
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|c
operator|.
name|get
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|uniqueLoader
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|randomized
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|Function
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|>
name|loaderList
init|=
operator|new
name|ArrayList
argument_list|<
name|Function
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|segmentCount
init|=
literal|10
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
name|segmentCount
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|x
init|=
name|i
decl_stmt|;
name|Function
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|loader
init|=
operator|new
name|Function
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|String
name|apply
parameter_list|(
annotation|@
name|Nullable
name|Integer
name|input
parameter_list|)
block|{
return|return
literal|"loader #"
operator|+
name|x
operator|+
literal|" offset "
operator|+
name|input
return|;
block|}
block|}
decl_stmt|;
name|loaderList
operator|.
name|add
argument_list|(
name|loader
argument_list|)
expr_stmt|;
block|}
name|StringCache
name|c
init|=
operator|new
name|StringCache
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|1
argument_list|)
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|int
name|segment
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|segmentCount
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|Function
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|loader
init|=
name|loaderList
operator|.
name|get
argument_list|(
name|segment
argument_list|)
decl_stmt|;
name|String
name|x
init|=
name|c
operator|.
name|get
argument_list|(
name|segment
argument_list|,
name|segment
argument_list|,
name|offset
argument_list|,
name|loader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|loader
operator|.
name|apply
argument_list|(
name|offset
argument_list|)
argument_list|,
name|x
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

