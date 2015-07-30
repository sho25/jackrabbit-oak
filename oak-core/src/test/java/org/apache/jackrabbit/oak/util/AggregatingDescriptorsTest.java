begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
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
name|assertNotNull
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
name|Collection
import|;
end_import

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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ValueFactory
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
name|commons
operator|.
name|SimpleValueFactory
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
name|Descriptors
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
name|spi
operator|.
name|whiteboard
operator|.
name|Tracker
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
name|AggregatingDescriptorsTest
block|{
specifier|static
class|class
name|DescriptorEntry
block|{
specifier|private
specifier|final
name|String
name|key
decl_stmt|;
specifier|private
specifier|final
name|Value
name|value
decl_stmt|;
specifier|private
specifier|final
name|Value
index|[]
name|values
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|singleValued
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|standard
decl_stmt|;
name|DescriptorEntry
parameter_list|(
name|String
name|key
parameter_list|,
name|Value
name|value
parameter_list|,
name|Value
index|[]
name|values
parameter_list|,
name|boolean
name|singleValued
parameter_list|,
name|boolean
name|standard
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|this
operator|.
name|singleValued
operator|=
name|singleValued
expr_stmt|;
name|this
operator|.
name|standard
operator|=
name|standard
expr_stmt|;
block|}
specifier|static
name|DescriptorEntry
name|fromKey
parameter_list|(
name|String
name|key
parameter_list|,
name|Descriptors
name|descriptors
parameter_list|)
block|{
if|if
condition|(
name|descriptors
operator|.
name|isSingleValueDescriptor
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
name|newSingleValuedEntry
argument_list|(
name|key
argument_list|,
name|descriptors
operator|.
name|getValue
argument_list|(
name|key
argument_list|)
argument_list|,
name|descriptors
operator|.
name|isStandardDescriptor
argument_list|(
name|key
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|newMultiValuedEntry
argument_list|(
name|key
argument_list|,
name|descriptors
operator|.
name|getValues
argument_list|(
name|key
argument_list|)
argument_list|,
name|descriptors
operator|.
name|isStandardDescriptor
argument_list|(
name|key
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|DescriptorEntry
name|newMultiValuedEntry
parameter_list|(
name|String
name|key
parameter_list|,
name|Value
index|[]
name|values
parameter_list|,
name|boolean
name|standardDescriptor
parameter_list|)
block|{
return|return
operator|new
name|DescriptorEntry
argument_list|(
name|key
argument_list|,
literal|null
argument_list|,
name|values
argument_list|,
literal|false
argument_list|,
name|standardDescriptor
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|DescriptorEntry
name|newSingleValuedEntry
parameter_list|(
name|String
name|key
parameter_list|,
name|Value
name|value
parameter_list|,
name|boolean
name|standardDescriptor
parameter_list|)
block|{
return|return
operator|new
name|DescriptorEntry
argument_list|(
name|key
argument_list|,
name|value
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|standardDescriptor
argument_list|)
return|;
block|}
block|}
class|class
name|MyTracker
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Tracker
argument_list|<
name|T
argument_list|>
block|{
name|List
argument_list|<
name|T
argument_list|>
name|services
decl_stmt|;
specifier|public
name|void
name|setServices
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|services
parameter_list|)
block|{
name|this
operator|.
name|services
operator|=
name|services
expr_stmt|;
block|}
specifier|public
name|void
name|addService
parameter_list|(
name|T
name|service
parameter_list|)
block|{
if|if
condition|(
name|services
operator|==
literal|null
condition|)
block|{
name|services
operator|=
operator|new
name|LinkedList
argument_list|<
name|T
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|services
operator|.
name|add
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|T
argument_list|>
name|getServices
parameter_list|()
block|{
return|return
name|services
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
block|{
comment|// no-op
block|}
block|}
specifier|private
name|MyTracker
argument_list|<
name|Descriptors
argument_list|>
name|createTracker
parameter_list|()
block|{
return|return
operator|new
name|MyTracker
argument_list|<
name|Descriptors
argument_list|>
argument_list|()
return|;
block|}
specifier|private
name|void
name|assertEmpty
parameter_list|(
name|AggregatingDescriptors
name|aggregator
parameter_list|)
block|{
name|assertFalse
argument_list|(
name|aggregator
operator|.
name|isSingleValueDescriptor
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|aggregator
operator|.
name|isStandardDescriptor
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|aggregator
operator|.
name|getValue
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|aggregator
operator|.
name|getValues
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|keys
init|=
name|aggregator
operator|.
name|getKeys
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|keys
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|keys
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNullServices
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
operator|new
name|AggregatingDescriptors
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should complain"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// ok
block|}
specifier|final
name|MyTracker
argument_list|<
name|Descriptors
argument_list|>
name|tracker
init|=
name|createTracker
argument_list|()
decl_stmt|;
name|AggregatingDescriptors
name|aggregator
init|=
operator|new
name|AggregatingDescriptors
argument_list|(
name|tracker
argument_list|)
decl_stmt|;
name|assertEmpty
argument_list|(
name|aggregator
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmptyServices
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|MyTracker
argument_list|<
name|Descriptors
argument_list|>
name|tracker
init|=
name|createTracker
argument_list|()
decl_stmt|;
name|AggregatingDescriptors
name|aggregator
init|=
operator|new
name|AggregatingDescriptors
argument_list|(
name|tracker
argument_list|)
decl_stmt|;
name|tracker
operator|.
name|setServices
argument_list|(
operator|new
name|LinkedList
argument_list|<
name|Descriptors
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
name|aggregator
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertMatches
parameter_list|(
name|AggregatingDescriptors
name|aggregator
parameter_list|,
name|int
name|expectedEntryCount
parameter_list|,
name|GenericDescriptors
modifier|...
name|descriptors
parameter_list|)
block|{
comment|// prepare the expectedEntries map
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DescriptorEntry
argument_list|>
name|expectedEntries
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|AggregatingDescriptorsTest
operator|.
name|DescriptorEntry
argument_list|>
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
name|descriptors
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
index|[]
name|keys
init|=
name|descriptors
index|[
name|i
index|]
operator|.
name|getKeys
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|keys
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|DescriptorEntry
name|entry
init|=
name|DescriptorEntry
operator|.
name|fromKey
argument_list|(
name|keys
index|[
name|j
index|]
argument_list|,
name|descriptors
index|[
name|i
index|]
argument_list|)
decl_stmt|;
comment|// implements overwriting: eg descriptors[1] values overwrite descriptors[0] values
comment|// (in terms of the AggregatingDescriptors it is the opposite: the service
comment|// that is enlisted first always wins - with the idea that later added
comment|// services should not overwrite earlier ones - lowest startorder wins)
name|expectedEntries
operator|.
name|put
argument_list|(
name|keys
index|[
name|j
index|]
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|expectedEntryCount
argument_list|,
name|expectedEntries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// now go through the resulting expectedEntries and match them
comment|// with the aggregator one
specifier|final
name|Collection
argument_list|<
name|DescriptorEntry
argument_list|>
name|entries
init|=
name|expectedEntries
operator|.
name|values
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|DescriptorEntry
argument_list|>
name|it
init|=
name|entries
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|DescriptorEntry
name|entry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|entry
operator|.
name|standard
argument_list|,
name|aggregator
operator|.
name|isStandardDescriptor
argument_list|(
name|entry
operator|.
name|key
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|entry
operator|.
name|singleValued
condition|)
block|{
name|assertTrue
argument_list|(
name|aggregator
operator|.
name|isSingleValueDescriptor
argument_list|(
name|entry
operator|.
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|Value
name|expectedValue
init|=
name|entry
operator|.
name|value
decl_stmt|;
name|Value
name|actualValue
init|=
name|aggregator
operator|.
name|getValue
argument_list|(
name|entry
operator|.
name|key
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expectedValue
operator|.
name|equals
argument_list|(
name|actualValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|aggregator
operator|.
name|isSingleValueDescriptor
argument_list|(
name|entry
operator|.
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|Value
index|[]
name|expectedValues
init|=
name|entry
operator|.
name|values
decl_stmt|;
name|Value
index|[]
name|actualValues
init|=
name|aggregator
operator|.
name|getValues
argument_list|(
name|entry
operator|.
name|key
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedValues
operator|.
name|length
argument_list|,
name|actualValues
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expectedValues
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|expectedValues
index|[
name|i
index|]
argument_list|,
name|actualValues
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|assertEquals
argument_list|(
name|expectedEntryCount
argument_list|,
name|aggregator
operator|.
name|getKeys
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInitialDescriptors
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ValueFactory
name|valueFactory
init|=
operator|new
name|SimpleValueFactory
argument_list|()
decl_stmt|;
specifier|final
name|MyTracker
argument_list|<
name|Descriptors
argument_list|>
name|tracker
init|=
name|createTracker
argument_list|()
decl_stmt|;
specifier|final
name|GenericDescriptors
name|input
init|=
operator|new
name|GenericDescriptors
argument_list|()
decl_stmt|;
name|input
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"b"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|input
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"c"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|addService
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|AggregatingDescriptors
name|aggregator
init|=
operator|new
name|AggregatingDescriptors
argument_list|(
name|tracker
argument_list|)
decl_stmt|;
name|assertMatches
argument_list|(
name|aggregator
argument_list|,
literal|2
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLaterAddedDescriptors
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ValueFactory
name|valueFactory
init|=
operator|new
name|SimpleValueFactory
argument_list|()
decl_stmt|;
specifier|final
name|MyTracker
argument_list|<
name|Descriptors
argument_list|>
name|tracker
init|=
name|createTracker
argument_list|()
decl_stmt|;
name|AggregatingDescriptors
name|aggregator
init|=
operator|new
name|AggregatingDescriptors
argument_list|(
name|tracker
argument_list|)
decl_stmt|;
name|assertMatches
argument_list|(
name|aggregator
argument_list|,
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|GenericDescriptors
name|input1
init|=
operator|new
name|GenericDescriptors
argument_list|()
decl_stmt|;
name|input1
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"b"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|input1
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"c"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|addService
argument_list|(
name|input1
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|aggregator
argument_list|,
literal|2
argument_list|,
name|input1
argument_list|)
expr_stmt|;
specifier|final
name|GenericDescriptors
name|input2
init|=
operator|new
name|GenericDescriptors
argument_list|()
decl_stmt|;
name|input2
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"c2"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|input2
operator|.
name|put
argument_list|(
literal|"c"
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"d"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|addService
argument_list|(
name|input2
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|aggregator
argument_list|,
literal|3
argument_list|,
name|input2
argument_list|,
name|input1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

