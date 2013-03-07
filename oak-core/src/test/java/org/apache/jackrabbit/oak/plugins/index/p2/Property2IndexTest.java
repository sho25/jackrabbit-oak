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
name|plugins
operator|.
name|index
operator|.
name|p2
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|CommitFailedException
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
name|Type
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
name|index
operator|.
name|IndexHook
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
name|memory
operator|.
name|MemoryNodeState
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
name|query
operator|.
name|index
operator|.
name|FilterImpl
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
name|query
operator|.
name|Filter
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
name|query
operator|.
name|PropertyValues
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
name|NodeBuilder
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|Sets
import|;
end_import

begin_comment
comment|/**  * Test the Property2 index mechanism.  */
end_comment

begin_class
specifier|public
class|class
name|Property2IndexTest
block|{
specifier|private
specifier|static
specifier|final
name|int
name|MANY
init|=
literal|100
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testPropertyLookup
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|MemoryNodeState
operator|.
name|EMPTY_NODE
decl_stmt|;
comment|// Add index definition
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"oak:queryIndexDefinition"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"p2"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"propertyNames"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
comment|// Add some content and process it through the property index hook
name|builder
operator|=
name|before
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"abc"
argument_list|,
literal|"def"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
comment|// plus lots of dummy content to highlight the benefit of indexing
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|MANY
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"n"
operator|+
name|i
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"xyz"
argument_list|)
expr_stmt|;
block|}
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
comment|// Add an index
name|IndexHook
name|p
init|=
operator|new
name|Property2IndexDiff
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|p
operator|.
name|apply
argument_list|()
expr_stmt|;
name|p
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Query the index
name|Property2IndexLookup
name|lookup
init|=
operator|new
name|Property2IndexLookup
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"foo"
argument_list|,
literal|"def"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"foo"
argument_list|,
literal|"ghi"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MANY
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"foo"
argument_list|,
literal|"xyz"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MANY
operator|+
literal|2
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"foo"
argument_list|,
literal|null
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|double
name|cost
decl_stmt|;
name|cost
operator|=
name|lookup
operator|.
name|getCost
argument_list|(
literal|null
argument_list|,
literal|"foo"
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"xyz"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"cost: "
operator|+
name|cost
argument_list|,
name|cost
operator|>=
name|MANY
argument_list|)
expr_stmt|;
name|cost
operator|=
name|lookup
operator|.
name|getCost
argument_list|(
literal|null
argument_list|,
literal|"foo"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"cost: "
operator|+
name|cost
argument_list|,
name|cost
operator|>=
name|MANY
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|find
parameter_list|(
name|Property2IndexLookup
name|lookup
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|,
name|Filter
name|filter
parameter_list|)
block|{
return|return
name|Sets
operator|.
name|newHashSet
argument_list|(
name|lookup
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|name
argument_list|,
name|value
operator|==
literal|null
condition|?
literal|null
else|:
name|PropertyValues
operator|.
name|newString
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|find
parameter_list|(
name|Property2IndexLookup
name|lookup
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
name|find
argument_list|(
name|lookup
argument_list|,
name|name
argument_list|,
name|value
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCustomConfigPropertyLookup
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|MemoryNodeState
operator|.
name|EMPTY_NODE
decl_stmt|;
comment|// Add index definition
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"fooIndex"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"oak:queryIndexDefinition"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"p2"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"propertyNames"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"foo"
argument_list|,
literal|"extrafoo"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
comment|// Add some content and process it through the property index hook
name|builder
operator|=
name|before
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"extrafoo"
argument_list|,
literal|"pqr"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"abc"
argument_list|,
literal|"def"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
comment|// plus lots of dummy content to highlight the benefit of indexing
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|MANY
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"n"
operator|+
name|i
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"xyz"
argument_list|)
expr_stmt|;
block|}
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
comment|// Add an index
name|IndexHook
name|p
init|=
operator|new
name|Property2IndexDiff
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|p
operator|.
name|apply
argument_list|()
expr_stmt|;
name|p
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Query the index
name|Property2IndexLookup
name|lookup
init|=
operator|new
name|Property2IndexLookup
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"foo"
argument_list|,
literal|"def"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"foo"
argument_list|,
literal|"ghi"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MANY
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"foo"
argument_list|,
literal|"xyz"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"extrafoo"
argument_list|,
literal|"pqr"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"pqr"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected: no index for "pqr"
block|}
block|}
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-666">OAK-666:      *      Property2Index: node type is used when indexing, but ignored when      *      querying</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testCustomConfigNodeType
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|MemoryNodeState
operator|.
name|EMPTY_NODE
decl_stmt|;
comment|// Add index definitions
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|index
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
decl_stmt|;
name|index
operator|.
name|child
argument_list|(
literal|"fooIndex"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"oak:queryIndexDefinition"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"p2"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"propertyNames"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"foo"
argument_list|,
literal|"extrafoo"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"declaringNodeTypes"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"nt:unstructured"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|index
operator|.
name|child
argument_list|(
literal|"fooIndexFile"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"oak:queryIndexDefinition"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"p2"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"propertyNames"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"declaringNodeTypes"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"nt:file"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
comment|// Add some content and process it through the property index hook
name|builder
operator|=
name|before
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:unstructured"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:unstructured"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"abc"
argument_list|,
literal|"def"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
comment|// Add an index
name|IndexHook
name|p
init|=
operator|new
name|Property2IndexDiff
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|p
operator|.
name|apply
argument_list|()
expr_stmt|;
name|p
operator|.
name|close
argument_list|()
expr_stmt|;
name|NodeState
name|indexedState
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|FilterImpl
name|f
init|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|f
operator|.
name|setNodeType
argument_list|(
literal|"nt:unstructured"
argument_list|)
expr_stmt|;
comment|// Query the index
name|Property2IndexLookup
name|lookup
init|=
operator|new
name|Property2IndexLookup
argument_list|(
name|indexedState
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"foo"
argument_list|,
literal|"abc"
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"foo"
argument_list|,
literal|"def"
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"foo"
argument_list|,
literal|"ghi"
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"pqr"
argument_list|,
literal|"foo"
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected: no index for "pqr"
block|}
block|}
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-666">OAK-666:      *      Property2Index: node type is used when indexing, but ignored when      *      querying</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testCustomConfigNodeTypeFallback
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|MemoryNodeState
operator|.
name|EMPTY_NODE
decl_stmt|;
comment|// Add index definitions
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|index
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
decl_stmt|;
name|index
operator|.
name|child
argument_list|(
literal|"fooIndex"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"oak:queryIndexDefinition"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"p2"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"propertyNames"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"foo"
argument_list|,
literal|"extrafoo"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|index
operator|.
name|child
argument_list|(
literal|"fooIndexFile"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"oak:queryIndexDefinition"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"p2"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"propertyNames"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"declaringNodeTypes"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"nt:file"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
comment|// Add some content and process it through the property index hook
name|builder
operator|=
name|before
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:unstructured"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:unstructured"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"abc"
argument_list|,
literal|"def"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
comment|// Add an index
name|IndexHook
name|p
init|=
operator|new
name|Property2IndexDiff
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|p
operator|.
name|apply
argument_list|()
expr_stmt|;
name|p
operator|.
name|close
argument_list|()
expr_stmt|;
name|NodeState
name|indexedState
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|FilterImpl
name|f
init|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|f
operator|.
name|setNodeType
argument_list|(
literal|"nt:unstructured"
argument_list|)
expr_stmt|;
comment|// Query the index
name|Property2IndexLookup
name|lookup
init|=
operator|new
name|Property2IndexLookup
argument_list|(
name|indexedState
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"foo"
argument_list|,
literal|"abc"
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"foo"
argument_list|,
literal|"def"
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"foo"
argument_list|,
literal|"ghi"
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|find
argument_list|(
name|lookup
argument_list|,
literal|"pqr"
argument_list|,
literal|"foo"
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected: no index for "pqr"
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnique
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|MemoryNodeState
operator|.
name|EMPTY_NODE
decl_stmt|;
comment|// Add index definition
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"fooIndex"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"oak:queryIndexDefinition"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"p2"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"unique"
argument_list|,
literal|"true"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"propertyNames"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|builder
operator|=
name|before
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"abc"
argument_list|,
literal|"def"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|IndexHook
name|p
init|=
operator|new
name|Property2IndexDiff
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|p
argument_list|)
expr_stmt|;
try|try
block|{
name|p
operator|.
name|apply
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Unique constraint should be respected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
finally|finally
block|{
name|p
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUniqueByTypeOK
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|MemoryNodeState
operator|.
name|EMPTY_NODE
decl_stmt|;
comment|// Add index definition
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"fooIndex"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"oak:queryIndexDefinition"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"p2"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"unique"
argument_list|,
literal|"true"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"propertyNames"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
operator|.
name|setProperty
argument_list|(
name|Property2IndexDiff
operator|.
name|declaringNodeTypes
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"typeFoo"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|builder
operator|=
name|before
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"typeFoo"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"typeBar"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|IndexHook
name|p
init|=
operator|new
name|Property2IndexDiff
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|p
operator|.
name|apply
argument_list|()
expr_stmt|;
name|p
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUniqueByTypeKO
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|MemoryNodeState
operator|.
name|EMPTY_NODE
decl_stmt|;
comment|// Add index definition
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"fooIndex"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"oak:queryIndexDefinition"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"p2"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"unique"
argument_list|,
literal|"true"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"propertyNames"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
operator|.
name|setProperty
argument_list|(
name|Property2IndexDiff
operator|.
name|declaringNodeTypes
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"typeFoo"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|builder
operator|=
name|before
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"typeFoo"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"typeFoo"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|IndexHook
name|p
init|=
operator|new
name|Property2IndexDiff
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|p
argument_list|)
expr_stmt|;
try|try
block|{
name|p
operator|.
name|apply
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Unique constraint should be respected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
finally|finally
block|{
name|p
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUniqueByTypeDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|MemoryNodeState
operator|.
name|EMPTY_NODE
decl_stmt|;
comment|// Add index definition
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"fooIndex"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"oak:queryIndexDefinition"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"p2"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"unique"
argument_list|,
literal|"true"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"propertyNames"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
operator|.
name|setProperty
argument_list|(
name|Property2IndexDiff
operator|.
name|declaringNodeTypes
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"typeFoo"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"typeFoo"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"typeBar"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|builder
operator|=
name|before
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|removeNode
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|IndexHook
name|p
init|=
operator|new
name|Property2IndexDiff
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|p
operator|.
name|apply
argument_list|()
expr_stmt|;
name|p
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

