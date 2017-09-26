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
name|util
package|;
end_package

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
name|Comparator
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
name|List
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
name|plugins
operator|.
name|document
operator|.
name|Revision
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
name|plugins
operator|.
name|document
operator|.
name|StableRevisionComparator
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
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Tests for {@link MergeSortedIterators}.  */
end_comment

begin_class
specifier|public
class|class
name|MergeSortedIteratorsTest
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|list
argument_list|()
argument_list|,
name|sort
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|list
argument_list|()
argument_list|,
name|sort
argument_list|(
name|list
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|list
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|,
name|sort
argument_list|(
name|list
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
name|list
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|list
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|,
name|sort
argument_list|(
name|list
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|,
name|list
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|list
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|,
name|sort
argument_list|(
name|list
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|,
name|list
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|list
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|,
name|sort
argument_list|(
name|list
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|,
name|list
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|list
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|)
argument_list|,
name|sort
argument_list|(
name|list
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
argument_list|,
name|list
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
argument_list|,
name|list
argument_list|(
literal|3
argument_list|,
literal|6
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * See OAK-1233 and OAK-1479      */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testData
parameter_list|()
block|{
name|List
argument_list|<
name|Iterator
argument_list|<
name|Revision
argument_list|>
argument_list|>
name|iterators
init|=
name|prepareData
argument_list|()
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|Iterator
argument_list|<
name|Revision
argument_list|>
argument_list|>
name|it
init|=
name|iterators
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|final
name|Comparator
argument_list|<
name|Revision
argument_list|>
name|comp
init|=
name|StableRevisionComparator
operator|.
name|REVERSE
decl_stmt|;
name|MergeSortedIterators
argument_list|<
name|Revision
argument_list|>
name|sort
init|=
operator|new
name|MergeSortedIterators
argument_list|<
name|Revision
argument_list|>
argument_list|(
name|comp
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Revision
argument_list|>
name|nextIterator
parameter_list|()
block|{
return|return
name|it
operator|.
name|hasNext
argument_list|()
condition|?
name|it
operator|.
name|next
argument_list|()
else|:
literal|null
return|;
block|}
block|}
decl_stmt|;
while|while
condition|(
name|sort
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|sort
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|List
argument_list|<
name|Integer
argument_list|>
name|sort
parameter_list|(
name|List
argument_list|<
name|Integer
argument_list|>
modifier|...
name|lists
parameter_list|)
block|{
name|List
argument_list|<
name|Iterator
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|iterators
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|Integer
argument_list|>
name|list
range|:
name|lists
control|)
block|{
name|iterators
operator|.
name|add
argument_list|(
name|list
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Iterator
argument_list|<
name|Iterator
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|it
init|=
name|iterators
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|MergeSortedIterators
argument_list|<
name|Integer
argument_list|>
name|sort
init|=
operator|new
name|MergeSortedIterators
argument_list|<
name|Integer
argument_list|>
argument_list|(
operator|new
name|Comparator
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Integer
name|o1
parameter_list|,
name|Integer
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|compareTo
argument_list|(
name|o2
argument_list|)
return|;
block|}
block|}
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|nextIterator
parameter_list|()
block|{
return|return
name|it
operator|.
name|hasNext
argument_list|()
condition|?
name|it
operator|.
name|next
argument_list|()
else|:
literal|null
return|;
block|}
block|}
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|sorted
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|sort
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|sorted
operator|.
name|add
argument_list|(
name|sort
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|sorted
return|;
block|}
specifier|final
specifier|static
name|String
index|[]
index|[]
name|TEST_DATA
init|=
block|{
block|{
literal|"r14298e4442a-0-2"
block|,
literal|"r14298e443e5-0-2"
block|,             }
block|,
block|{
literal|"r14298e4548d-0-1"
block|}
block|,     }
decl_stmt|;
specifier|private
specifier|static
name|List
argument_list|<
name|Iterator
argument_list|<
name|Revision
argument_list|>
argument_list|>
name|prepareData
parameter_list|()
block|{
name|List
argument_list|<
name|Iterator
argument_list|<
name|Revision
argument_list|>
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Iterator
argument_list|<
name|Revision
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
index|[]
name|revsString
range|:
name|TEST_DATA
control|)
block|{
name|List
argument_list|<
name|Revision
argument_list|>
name|revs
init|=
operator|new
name|ArrayList
argument_list|<
name|Revision
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|r
range|:
name|revsString
control|)
block|{
name|revs
operator|.
name|add
argument_list|(
name|Revision
operator|.
name|fromString
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|add
argument_list|(
name|revs
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|Integer
argument_list|>
name|list
parameter_list|(
name|int
modifier|...
name|values
parameter_list|)
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|v
range|:
name|values
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
block|}
end_class

end_unit
