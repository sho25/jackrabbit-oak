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
name|jcr
operator|.
name|query
package|;
end_package

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
name|assertTrue
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
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
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
name|api
operator|.
name|Result
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
name|api
operator|.
name|ResultRow
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
name|jcr
operator|.
name|query
operator|.
name|PrefetchIterator
operator|.
name|PrefetchOptions
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

begin_comment
comment|/**  * Test the PrefetchIterator class.  */
end_comment

begin_class
specifier|public
class|class
name|PrefetchIteratorTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testFastSize
parameter_list|()
block|{
name|Iterable
argument_list|<
name|Integer
argument_list|>
name|s
decl_stmt|;
name|PrefetchIterator
argument_list|<
name|Integer
argument_list|>
name|it
decl_stmt|;
name|s
operator|=
name|seq
argument_list|(
literal|0
argument_list|,
literal|21
argument_list|)
expr_stmt|;
name|it
operator|=
operator|new
name|PrefetchIterator
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|s
operator|.
name|iterator
argument_list|()
argument_list|,
operator|new
name|PrefetchOptions
argument_list|()
block|{
block|{
name|size
operator|=
operator|-
literal|1
expr_stmt|;
name|fastSize
operator|=
literal|true
expr_stmt|;
name|fastSizeCallback
operator|=
operator|new
name|Result
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getColumnNames
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getColumnSelectorNames
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|ResultRow
argument_list|>
name|getRows
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getSelectorNames
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getSize
parameter_list|()
block|{
return|return
literal|100
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getSize
parameter_list|(
name|SizePrecision
name|precision
parameter_list|,
name|long
name|max
parameter_list|)
block|{
return|return
literal|100
return|;
block|}
block|}
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|21
argument_list|,
name|it
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testKnownSize
parameter_list|()
block|{
name|Iterable
argument_list|<
name|Integer
argument_list|>
name|s
decl_stmt|;
name|PrefetchIterator
argument_list|<
name|Integer
argument_list|>
name|it
decl_stmt|;
name|s
operator|=
name|seq
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|it
operator|=
operator|new
name|PrefetchIterator
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|s
operator|.
name|iterator
argument_list|()
argument_list|,
operator|new
name|PrefetchOptions
argument_list|()
block|{
block|{
name|min
operator|=
literal|5
expr_stmt|;
name|timeout
operator|=
literal|0
expr_stmt|;
name|max
operator|=
literal|10
expr_stmt|;
name|size
operator|=
literal|200
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// reports the 'wrong' value as it was set manually
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|it
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTimeout
parameter_list|()
block|{
name|Iterable
argument_list|<
name|Integer
argument_list|>
name|s
decl_stmt|;
name|PrefetchIterator
argument_list|<
name|Integer
argument_list|>
name|it
decl_stmt|;
comment|// long delay (10 ms per row)
specifier|final
name|long
name|testTimeout
init|=
literal|10
decl_stmt|;
name|s
operator|=
name|seq
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|it
operator|=
operator|new
name|PrefetchIterator
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|s
operator|.
name|iterator
argument_list|()
argument_list|,
operator|new
name|PrefetchOptions
argument_list|()
block|{
block|{
name|min
operator|=
literal|5
expr_stmt|;
name|timeout
operator|=
name|testTimeout
expr_stmt|;
name|max
operator|=
literal|10
expr_stmt|;
name|size
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|it
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// no delay
name|s
operator|=
name|seq
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|it
operator|=
operator|new
name|PrefetchIterator
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|s
operator|.
name|iterator
argument_list|()
argument_list|,
operator|new
name|PrefetchOptions
argument_list|()
block|{
block|{
name|min
operator|=
literal|5
expr_stmt|;
name|timeout
operator|=
name|testTimeout
expr_stmt|;
name|max
operator|=
literal|1000
expr_stmt|;
name|size
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|it
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
block|{
comment|// the following is the same as:
comment|// for (int size = 0; size< 100; size++)
for|for
control|(
name|int
name|size
range|:
name|seq
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|)
control|)
block|{
for|for
control|(
name|int
name|readBefore
range|:
name|seq
argument_list|(
literal|0
argument_list|,
literal|30
argument_list|)
control|)
block|{
comment|// every 3th time, use a timeout
specifier|final
name|long
name|testTimeout
init|=
name|size
operator|%
literal|3
operator|==
literal|0
condition|?
literal|100
else|:
literal|0
decl_stmt|;
name|Iterable
argument_list|<
name|Integer
argument_list|>
name|s
init|=
name|seq
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
decl_stmt|;
name|PrefetchIterator
argument_list|<
name|Integer
argument_list|>
name|it
init|=
operator|new
name|PrefetchIterator
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|s
operator|.
name|iterator
argument_list|()
argument_list|,
operator|new
name|PrefetchOptions
argument_list|()
block|{
block|{
name|min
operator|=
literal|20
expr_stmt|;
name|timeout
operator|=
name|testTimeout
expr_stmt|;
name|max
operator|=
literal|30
expr_stmt|;
name|size
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|x
range|:
name|seq
argument_list|(
literal|0
argument_list|,
name|readBefore
argument_list|)
control|)
block|{
name|boolean
name|hasNext
init|=
name|it
operator|.
name|hasNext
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|hasNext
condition|)
block|{
name|assertEquals
argument_list|(
name|x
argument_list|,
name|size
argument_list|)
expr_stmt|;
break|break;
block|}
name|String
name|m
init|=
literal|"s:"
operator|+
name|size
operator|+
literal|" b:"
operator|+
name|readBefore
operator|+
literal|" x:"
operator|+
name|x
decl_stmt|;
name|assertTrue
argument_list|(
name|m
argument_list|,
name|hasNext
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|m
argument_list|,
name|x
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|m
init|=
literal|"s:"
operator|+
name|size
operator|+
literal|" b:"
operator|+
name|readBefore
decl_stmt|;
name|int
name|max
init|=
name|testTimeout
operator|<=
literal|0
condition|?
literal|20
else|:
literal|30
decl_stmt|;
if|if
condition|(
name|size
operator|>
name|max
operator|&&
name|readBefore
operator|<
name|size
condition|)
block|{
name|assertEquals
argument_list|(
name|m
argument_list|,
operator|-
literal|1
argument_list|,
name|it
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// calling it twice must not change the result
name|assertEquals
argument_list|(
name|m
argument_list|,
operator|-
literal|1
argument_list|,
name|it
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|m
argument_list|,
name|size
argument_list|,
name|it
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// calling it twice must not change the result
name|assertEquals
argument_list|(
name|m
argument_list|,
name|size
argument_list|,
name|it
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|x
range|:
name|seq
argument_list|(
name|readBefore
argument_list|,
name|size
argument_list|)
control|)
block|{
name|m
operator|=
literal|"s:"
operator|+
name|size
operator|+
literal|" b:"
operator|+
name|readBefore
operator|+
literal|" x:"
operator|+
name|x
expr_stmt|;
name|assertTrue
argument_list|(
name|m
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|m
argument_list|,
name|x
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchElementException
name|e
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
block|}
comment|/**      * Create an integer sequence.      *       * @param start the first value      * @param limit the last value + 1      * @return a sequence of the values [start .. limit-1]      */
specifier|private
specifier|static
name|Iterable
argument_list|<
name|Integer
argument_list|>
name|seq
parameter_list|(
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|limit
parameter_list|)
block|{
return|return
name|seq
argument_list|(
name|start
argument_list|,
name|limit
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**      * Create an integer sequence.      *       * @param start the first value      * @param limit the last value + 1      * @param sleep the time to wait for each element      * @return a sequence of the values [start .. limit-1]      */
specifier|private
specifier|static
name|Iterable
argument_list|<
name|Integer
argument_list|>
name|seq
parameter_list|(
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|limit
parameter_list|,
specifier|final
name|int
name|sleep
parameter_list|)
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
name|int
name|x
init|=
name|start
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|x
operator|<
name|limit
return|;
block|}
annotation|@
name|Override
specifier|public
name|Integer
name|next
parameter_list|()
block|{
if|if
condition|(
name|sleep
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleep
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
return|return
name|x
operator|++
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"["
operator|+
name|start
operator|+
literal|".."
operator|+
operator|(
name|limit
operator|-
literal|1
operator|)
operator|+
literal|"]"
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

