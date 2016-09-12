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
operator|.
name|file
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Integer
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
name|assertFalse
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
name|assertNull
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
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
name|Predicate
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

begin_class
specifier|public
class|class
name|PriorityCacheTest
block|{
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|illegalSize
parameter_list|()
block|{
operator|new
name|PriorityCache
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
literal|42
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|zeroSize
parameter_list|()
block|{
operator|new
name|PriorityCache
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|legalHash
parameter_list|()
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|8
condition|;
name|k
operator|++
control|)
block|{
operator|new
name|PriorityCache
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
literal|0x1000000
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|illegalHash
parameter_list|()
block|{
operator|new
name|PriorityCache
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
literal|0x1000000
argument_list|,
literal|9
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|singletonCache
parameter_list|()
block|{
name|PriorityCache
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|cache
init|=
operator|new
name|PriorityCache
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cache
operator|.
name|put
argument_list|(
literal|"one"
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Cache is full -> cannot put another key of the same cost
name|assertFalse
argument_list|(
name|cache
operator|.
name|put
argument_list|(
literal|"two"
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Retrieving "one" leads to a cache hit increasing this key's cost to 1
name|assertEquals
argument_list|(
name|valueOf
argument_list|(
literal|1
argument_list|)
argument_list|,
name|cache
operator|.
name|get
argument_list|(
literal|"one"
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cache
operator|.
name|get
argument_list|(
literal|"one"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cache
operator|.
name|get
argument_list|(
literal|"two"
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Inserting "two" only succeeds for cost 2, which is bigger than "one"'s cost of 1
name|assertFalse
argument_list|(
name|cache
operator|.
name|put
argument_list|(
literal|"two"
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
operator|(
name|byte
operator|)
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cache
operator|.
name|put
argument_list|(
literal|"two"
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
operator|(
name|byte
operator|)
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|valueOf
argument_list|(
literal|2
argument_list|)
argument_list|,
name|cache
operator|.
name|get
argument_list|(
literal|"two"
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cache
operator|.
name|get
argument_list|(
literal|"two"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cache
operator|.
name|get
argument_list|(
literal|"one"
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|readWrite
parameter_list|()
block|{
name|PriorityCache
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|cache
init|=
operator|new
name|PriorityCache
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|(
literal|128
argument_list|,
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|128
condition|;
name|k
operator|++
control|)
block|{
if|if
condition|(
name|cache
operator|.
name|put
argument_list|(
literal|"key-"
operator|+
name|k
argument_list|,
name|k
argument_list|,
literal|0
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|k
argument_list|)
argument_list|,
name|cache
operator|.
name|get
argument_list|(
literal|"key-"
operator|+
name|k
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cache
operator|.
name|get
argument_list|(
literal|"key-"
operator|+
name|k
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNull
argument_list|(
name|cache
operator|.
name|get
argument_list|(
literal|"key-"
operator|+
name|k
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|128
condition|;
name|k
operator|++
control|)
block|{
name|Integer
name|value
init|=
name|cache
operator|.
name|get
argument_list|(
literal|"key-"
operator|+
name|k
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|k
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|updateKey
parameter_list|()
block|{
name|PriorityCache
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|cache
init|=
operator|new
name|PriorityCache
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cache
operator|.
name|put
argument_list|(
literal|"one"
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Cache is full -> cannot put another key of the same cost
name|assertFalse
argument_list|(
name|cache
operator|.
name|put
argument_list|(
literal|"two"
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// But updating an existing key works and boosts its cost to 1
name|assertTrue
argument_list|(
name|cache
operator|.
name|put
argument_list|(
literal|"one"
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// such that adding another key only works at cost 2 and greater
name|assertFalse
argument_list|(
name|cache
operator|.
name|put
argument_list|(
literal|"two"
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
operator|(
name|byte
operator|)
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cache
operator|.
name|put
argument_list|(
literal|"two"
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
operator|(
name|byte
operator|)
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|updateWithNewGeneration
parameter_list|()
block|{
name|PriorityCache
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|cache
init|=
operator|new
name|PriorityCache
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cache
operator|.
name|put
argument_list|(
literal|"one"
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Cache is full but we can still put a key of a higher generation
name|assertTrue
argument_list|(
name|cache
operator|.
name|put
argument_list|(
literal|"two"
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cache
operator|.
name|get
argument_list|(
literal|"one"
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Cannot put a key of a lower generation
name|assertFalse
argument_list|(
name|cache
operator|.
name|put
argument_list|(
literal|"two"
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// But one of the same generation
name|assertTrue
argument_list|(
name|cache
operator|.
name|put
argument_list|(
literal|"two"
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|generationPurge
parameter_list|()
block|{
name|PriorityCache
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|cache
init|=
operator|new
name|PriorityCache
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|(
literal|65536
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|gen
init|=
literal|4
init|;
name|gen
operator|>=
literal|0
condition|;
name|gen
operator|--
control|)
block|{
comment|// Backward iteration avoids earlier generations are replaced with later ones
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|100
condition|;
name|k
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|cache
operator|.
name|put
argument_list|(
literal|"key-"
operator|+
name|gen
operator|+
literal|"-"
operator|+
name|k
argument_list|,
literal|0
argument_list|,
name|gen
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
condition|)
block|{
name|assumeTrue
argument_list|(
literal|"All test keys are in the cache"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|assertEquals
argument_list|(
literal|500
argument_list|,
name|cache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|cache
operator|.
name|purgeGenerations
argument_list|(
operator|new
name|Predicate
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|Integer
name|generation
parameter_list|)
block|{
return|return
name|generation
operator|<=
literal|2
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|cache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

