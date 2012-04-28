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
name|kernel
package|;
end_package

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
name|PropertyState
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
name|core
operator|.
name|AbstractOakTest
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
name|state
operator|.
name|ChildNodeEntry
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
name|state
operator|.
name|NodeState
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertFalse
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
name|assertNotNull
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
name|assertNull
import|;
end_import

begin_class
specifier|public
class|class
name|KernelNodeStateTest
extends|extends
name|AbstractOakTest
block|{
annotation|@
name|Override
specifier|protected
name|NodeState
name|createInitialState
parameter_list|()
block|{
name|String
name|jsop
init|=
literal|"+\"test\":{\"a\":1,\"b\":2,\"c\":3,"
operator|+
literal|"\"x\":{},\"y\":{},\"z\":{}}"
decl_stmt|;
name|String
name|revision
init|=
name|microKernel
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
name|jsop
argument_list|,
name|microKernel
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|"test data"
argument_list|)
decl_stmt|;
return|return
operator|new
name|KernelNodeState
argument_list|(
name|microKernel
argument_list|,
name|valueFactory
argument_list|,
literal|"/test"
argument_list|,
name|revision
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPropertyCount
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|state
operator|.
name|getPropertyCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetProperty
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|state
operator|.
name|getProperty
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|state
operator|.
name|getProperty
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getValue
argument_list|()
operator|.
name|getLong
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
name|state
operator|.
name|getProperty
argument_list|(
literal|"b"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|state
operator|.
name|getProperty
argument_list|(
literal|"b"
argument_list|)
operator|.
name|getValue
argument_list|()
operator|.
name|getLong
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"c"
argument_list|,
name|state
operator|.
name|getProperty
argument_list|(
literal|"c"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|state
operator|.
name|getProperty
argument_list|(
literal|"c"
argument_list|)
operator|.
name|getValue
argument_list|()
operator|.
name|getLong
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|state
operator|.
name|getProperty
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetProperties
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Long
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyState
name|property
range|:
name|state
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|property
operator|.
name|getValue
argument_list|()
operator|.
name|getLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|names
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|)
argument_list|,
name|names
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|2
argument_list|)
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|3
argument_list|)
argument_list|)
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetChildNodeCount
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|state
operator|.
name|getChildNodeCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetChildNode
parameter_list|()
block|{
name|assertNotNull
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
literal|"z"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetChildNodeEntries
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|state
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|names
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"x"
argument_list|,
literal|"y"
argument_list|,
literal|"z"
argument_list|)
argument_list|,
name|names
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetChildNodeEntriesWithOffset
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|state
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|state
operator|.
name|getChildNodeEntries
argument_list|(
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|names
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"x"
argument_list|,
literal|"y"
argument_list|,
literal|"z"
argument_list|)
argument_list|,
name|names
argument_list|)
expr_stmt|;
comment|// Offset beyond the range
name|assertFalse
argument_list|(
name|state
operator|.
name|getChildNodeEntries
argument_list|(
literal|3
argument_list|,
operator|-
literal|1
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetChildNodeEntriesWithCount
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|state
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|state
operator|.
name|getChildNodeEntries
argument_list|(
literal|2
argument_list|,
operator|-
literal|1
argument_list|)
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|names
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"x"
argument_list|,
literal|"y"
argument_list|,
literal|"z"
argument_list|)
argument_list|,
name|names
argument_list|)
expr_stmt|;
comment|// Zero count
name|assertFalse
argument_list|(
name|state
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

